package es.nimio.nimiogcs.servicios;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;
import es.nimio.nimiogcs.errores.ErrorInesperadoOperacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.ParametroGlobal;
import es.nimio.nimiogcs.servicios.externos.sw.deployer.ServicePAI;

@Service
public class FactoriaServicioWebDeployer {

	private static final Logger logger = LoggerFactory
			.getLogger(FactoriaServicioWebDeployer.class);

	
	private IContextoEjecucionBase ce;
	
	@Autowired
	public FactoriaServicioWebDeployer(IContextoEjecucionBase ce) {
		this.ce = ce;
	}
	
	// ---------------
	
	public ServicePAI creaServicio() throws ErrorInesperadoOperacion {
		
		ParametroGlobal pg = ce.repos().global().buscar("SERVICIOS.EXTERNOS.DEPLOYER.URL");
		if(pg == null) 
			throw new ErrorInconsistenciaDatos(
					"No existe la variable global que define la URL del servicio web a emplear"
			);
		
		logger.debug("URL del servicio DEPLOYER: " + pg.getComoValorSimple());
		
		try {
			return new ServicePAI(new URL(pg.getContenido()));
		} catch(Exception th) {
			throw new ErrorInesperadoOperacion(th);
		}
	}
}
