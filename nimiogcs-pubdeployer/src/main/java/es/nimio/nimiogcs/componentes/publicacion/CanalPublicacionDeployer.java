package es.nimio.nimiogcs.componentes.publicacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.componentes.publicacion.ICanalPublicacion;
import es.nimio.nimiogcs.componentes.publicacion.modelo.DescripcionCanal;
import es.nimio.nimiogcs.componentes.publicacion.modelo.DestinoPublicacionCanal;
import es.nimio.nimiogcs.componentes.publicacion.modelo.IDatosPeticionPublicacion;
import es.nimio.nimiogcs.componentes.publicacion.modelo.IErrores;
import es.nimio.nimiogcs.componentes.publicacion.modelo.IPeticionPublicacion;
import es.nimio.nimiogcs.datos.P;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.ITestaferroArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaPublicacionDeployer;
import es.nimio.nimiogcs.jpa.entidades.proyectos.ElementoBaseProyecto;
import es.nimio.nimiogcs.servicios.FactoriaServicioWebDeployer;
import es.nimio.nimiogcs.servicios.externos.sw.deployer.ServicePAI;
import es.nimio.nimiogcs.servicios.externos.sw.deployer.ServicePAIPortType;
import es.nimio.nimiogcs.subtareas.publicacion.PublicacionDeployer;

@Component
public class CanalPublicacionDeployer 
	implements ICanalPublicacion {

	private static final String IDENTIFICADOR_CANAL = "DEPLOYER";
	private IContextoEjecucionBase ce;
	
	@Autowired
	public CanalPublicacionDeployer(IContextoEjecucionBase ce) {
		this.ce = ce;
	}
	
	@Override
	public DescripcionCanal descripcionCanal() {
		return 
				new DescripcionCanal(
						0,
						true,
						true,
						IDENTIFICADOR_CANAL, 
						"Sistema Deployer de publicaciones.",
						new DestinoPublicacionCanal[] {
								new DestinoPublicacionCanal("INTEGRACION", "Integración"),
								new DestinoPublicacionCanal("PREPRODUCCION", "Preproducción"),
								new DestinoPublicacionCanal("PRODUCCION", "Producción"),
						}
				);
	}
	
	@Override
	public DescripcionCanal teoricamentePosiblePublicarArtfacto(Artefacto artefacto) {
		// Hay que tener en cuenta cuándo nos encontramos con evolutivos
		Artefacto base = artefacto instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)artefacto).getArtefactoAfectado() : artefacto;
		
		// Cualquier artefacto que quiera ser desplegado por DEPLOYER tiene que, obligatoriamente, 
		// disponer de una lente de publicación y tener en los canales posbles a DEPLOYER. 
		DirectivaPublicacionDeployer directiva =  P.of(base).publicacionDeployer();
		if(directiva == null) return null;
		
		return descripcionCanal();
	}
	
	@Override
	public DescripcionCanal posiblePublicarArtefacto(ElementoBaseProyecto elementoProyecto, Artefacto artefacto) {
		
		// Hay que tener en cuenta cuándo nos encontramos con evolutivos
		Artefacto base = artefacto instanceof ITestaferroArtefacto ? ((ITestaferroArtefacto)artefacto).getArtefactoAfectado() : artefacto;
		
		// Cualquier artefacto que quiera ser desplegado por DEPLOYER tiene que, obligatoriamente, 
		// disponer de una lente de publicación y tener en los canales posbles a DEPLOYER. 
		DirectivaPublicacionDeployer directiva =  P.of(base).publicacionDeployer();
		if(directiva == null) return null;
		
		// pero que tenga la directiva no hace que sea realmente publicable
		// en función del tipo, será necesario revisar unas cosas u otras.
		if (!(
				UtilidadCalcularPublicabilidadTipoWar.esPublicable(ce, elementoProyecto, artefacto)
				|| UtilidadCalcularPublicabilidadTipoPom.esPublicable(ce, elementoProyecto, artefacto)
				|| UtilidadCalcularPublicabilidadTipoLibreriaJava.esPublicable(ce, elementoProyecto, artefacto)
				|| UtilidadCalcularPublicabilidadTipoEstaticos.esPublicable(ce, elementoProyecto, artefacto)
				|| UtilidadCalcularPublicabilidadTipoGeneral.esPublicable(ce, elementoProyecto, artefacto)
			)) return null;
		
		// finalmente decidimos que devolver
		return descripcionCanal();
	}

	@Override
	public void datosPeticion(String idCanal, IPeticionPublicacion peticion) {
		 
		// si no es una petición para el canal DEPLOYER, pasamos
		if(!idCanal.equalsIgnoreCase(IDENTIFICADOR_CANAL)) return;
		
		// añadimos los parámetros exigidos por el canal 
		peticion.getParametrosCanal().put("Código PROI", "");
	}
	
	@Override
	public void validarPeticion(String idCanal, IPeticionPublicacion peticion, IErrores errores) throws ErrorInesperadoOperacion {

		// si no es una petición para el canal DEPLOYER, pasamos
		if(!idCanal.equalsIgnoreCase(IDENTIFICADOR_CANAL)) return;
		
		// miramos el valor que tiene el código de proyecto
		String codigoPROI = peticion.getParametrosCanal().get("Código PROI");
		if(Strings.isNotEmpty(codigoPROI)) {
			
			// preguntamos al servicio web si el código indicado es válido para el usuario
			ServicePAI servicioDeployer = ce.componente(FactoriaServicioWebDeployer.class).creaServicio();
			ServicePAIPortType portDeployer = servicioDeployer.getServicePAIHttpSoap12Endpoint();
			
			String proiMadre = portDeployer.validarPROI(ce.usuario().getNombre(), codigoPROI);
			if(proiMadre == null || proiMadre.isEmpty()) {
				errores.rechazarValor(
						"parametrosCanal", 
						"PROI-INVALID",
						"El código PROI indicado no es aceptable para el usuario que realiza la petición."
				);
			}
		}
	}
	
	
	@Override
	public void ejecutarPublicacion(IDatosPeticionPublicacion publicacion) throws ErrorInesperadoOperacion {
		
		if(!publicacion.getCanal().equalsIgnoreCase(IDENTIFICADOR_CANAL)) return;
			
		new PublicacionDeployer(ce)
			.ejecutarCon(publicacion);
	}
}
