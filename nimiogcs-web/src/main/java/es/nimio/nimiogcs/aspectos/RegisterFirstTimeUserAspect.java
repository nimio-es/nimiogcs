package es.nimio.nimiogcs.aspectos;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.stereotype.Component;

import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.usuarios.Usuario;
import es.nimio.nimiogcs.modelo.IUsuario;
import es.nimio.nimiogcs.operaciones.OperacionInternaInlineModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

@Component
@Aspect
public class RegisterFirstTimeUserAspect extends CommonControllerAspects {

	private static final Logger logger = 
			LoggerFactory.getLogger(RegisterFirstTimeUserAspect.class);
	
	@Autowired
	public RegisterFirstTimeUserAspect(IContextoEjecucion ce) {
		super(ce);
	}

	@Before("controllerMethodExecution()")
	public void checkUserRecordExists() {

		final IUsuario user = ce.usuario();
		
		if(user!=null) {
		
			final String userName = user.getNombre().toUpperCase();
			
			if(!ce.usuarios().exists(userName)) {
				
				// hay que registrar al usuario
				try {
					new OperacionInternaInlineModulo(ce) {
						
						@Override
						protected Boolean hazlo(Boolean datos, Operacion op) throws ErrorInesperadoOperacion {
							
							Usuario usuario = new Usuario();
							usuario.setId(userName);
							
							if(user instanceof LdapUserDetails) {
								LdapUserDetails ldapUser = (LdapUserDetails)user;
								usuario.setNombre(ldapUser.getUsername());
								// usuario.setCorre(ldapUser.getMail());
							}
							
							// guardamos
							ce.repos().usuarios().guardarYVolcar(usuario);
							
							return true;
						}
						
						@Override
						protected String generaNombreUnico() {
							return "PRIMERA VEZ DEL USUARIO '" + user.getNombre() + "'";
						}
					}.ejecutar();
				} catch (Exception e) {
					// Salvo por dejar constancia a nivel del logger, no hacemos nada más
					logger.error("Error durante el tratamiento de la operación", e);
				}
			}
		}
	}
}
