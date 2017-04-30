package es.nimio.nimiogcs.datos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import es.nimio.nimiogcs.KArtifactory;
import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Programada;
import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.RepositorioArtefactos;
import es.nimio.nimiogcs.operaciones.OperacionInternaInline;

@Service
public class IniciadorDatosJenkins {

	private IContextoEjecucionBase ce;
	
	@Autowired
	public IniciadorDatosJenkins(IContextoEjecucionBase ce) {
		this.ce = ce;
	}

	@Scheduled(initialDelay=5*1000, fixedRate=365*24*60*60*1000)
	public void iniciador() {
		
		// -------------------------------------------------
		// Garantizamos que exista un destino de instalación
		// que se corresponda con Artifactory
		// -------------------------------------------------
		
		boolean necesitaInicio = 
				!existeDestino()
				|| !existeParametroServidor()
				|| !existeParametroUsuarioServidor()
				|| !existeParametroPasswordServidor();
		
		if(necesitaInicio) {

			new OperacionInternaInline<IContextoEjecucionBase>(ce) {
				
				@Override
				protected Operacion nuevaOperacion() {
					return new Programada();
				}

				@Override
				protected String usuarioOperaciones() {
					return "CAVERNICOLA";
				}
				
				@Override
				protected String generaNombreUnico() {
					return "AUTOREGISTRAR DATOS NECESARIOS PARA PUBLICACIÓN USANDO EL CANAL 'JENKINS'";
				}
				
				@Override
				protected Boolean hazlo(Boolean datos, Operacion op) throws ErrorInesperadoOperacion {
					
					if(!existeDestino()) {
						
						escribeMensaje("Dar de alta el destino.");
						RepositorioArtefactos rpar = new RepositorioArtefactos();
						rpar.setId(KArtifactory.ID_ARTIFACTORY);
						rpar.setNombre("Artifactory");
						ce.repos().destinosPublicacion().guardarYVolcar(rpar);
					}
					
					if(!existeParametroServidor()) {
						
						escribeMensaje("Parámetro global con el servidor Jenkins.");
						creaParametroGlobal(
								KArtifactory.PUBLICACION_JENKINS_SERVIDOR, 
								"http://localhost", 
								"Servidor que correrá las tareas Jenkins que se lanzará como parte de la publicación"
						);
					}
					
					if(!existeParametroUsuarioServidor()) {
						
						escribeMensaje("Parametro global con el usuario con acceso a Jenkins.");
						creaParametroGlobal(
								KArtifactory.PUBLICACION_JENKINS_SERVIDOR_USUARIO,
								"jenkins",
								"Usuario con credenciales para lanzar las tareas en Jenkins."
						);
					}

					if(!existeParametroPasswordServidor()) {
						
						escribeMensaje("Parametro global con la contraseña de acceso a Jenkins.");
						creaParametroGlobal(
								KArtifactory.PUBLICACION_JENKINS_SERVIDOR_SECRETO,
								"jenkins",
								"Contraseña de acceso a Jenkins."
						);
					}
					
					return true;
				}
				
			}.ejecutar();
		}
	}
	
	
	private boolean existeDestino() {
		return ce.repos().destinosPublicacion().buscar(KArtifactory.ID_ARTIFACTORY) != null;
	}
	
	private boolean existeParametroServidor() {
		return ce.repos().global().buscar(KArtifactory.PUBLICACION_JENKINS_SERVIDOR) != null;
	}
	
	private boolean existeParametroUsuarioServidor() {
		return ce.repos().global().buscar(KArtifactory.PUBLICACION_JENKINS_SERVIDOR_USUARIO) != null;
	}
	
	private boolean existeParametroPasswordServidor() {
		return ce.repos().global().buscar(KArtifactory.PUBLICACION_JENKINS_SERVIDOR_SECRETO) != null;
	}
	
	private void creaParametroGlobal(String id, String contenido, String descripcion) {
		ParametroGlobal pg = new ParametroGlobal();
		pg.setId(id);
		pg.setContenido(contenido);
		pg.setDescripcion(descripcion);
		ce.repos().global().guardarYVolcar(pg);
	}
}
