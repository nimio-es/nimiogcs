package es.nimio.nimiogcs.componentes.chequeo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.componentes.chequeo.ISubsistemasExternos;
import es.nimio.nimiogcs.componentes.chequeo.modelo.DescripcionSubsistemasExternos;

@Component
public class SubsistemasExternosRequiereDeployer 
	implements ISubsistemasExternos {

	private IContextoEjecucionBase ce;
	
	@Autowired
	public SubsistemasExternosRequiereDeployer(IContextoEjecucionBase ce) {
		this.ce = ce;
	}
	
	// ---
	
	@Override
	public DescripcionSubsistemasExternos subsistemas() {
		return 
				new DescripcionSubsistemasExternos(
						"DEPLOYER",
						new DescripcionSubsistemasExternos.Subsistema(
								"Proxy DEPLOYER PAI", 
								DescripcionSubsistemasExternos.extractDomainName(
										ce.repos().global().buscar("SERVICIOS.EXTERNOS.DEPLOYER.URL").getComoValorSimple()
								),
								ce.repos().global().buscar("SERVICIOS.EXTERNOS.DEPLOYER.URL").getComoValorSimple(), 
								null, 
								null
						),
						
						new DescripcionSubsistemasExternos.Subsistema(
								"Canal Subversion para comunicar descriptores de publicación",
								DescripcionSubsistemasExternos.extractDomainName(
										ce.repos().global().buscar("PUBLICACION.DEPLOYER.BUSSVN.URL").getComoValorSimple()
								),
								ce.repos().global().buscar("PUBLICACION.DEPLOYER.BUSSVN.URL").getComoValorSimple(), 
								ce.repos().global().buscar("PUBLICACION.DEPLOYER.BUSSVN.USUARIO").getComoValorSimple(), 
								ce.repos().global().buscar("PUBLICACION.DEPLOYER.BUSSVN.PASSWORD").getComoValorSimple()
						)
				);
	}
}
