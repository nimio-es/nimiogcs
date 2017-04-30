package es.nimio.nimiogcs.componentes.chequeo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.nimio.nimiogcs.componentes.IContextoEjecucionBase;
import es.nimio.nimiogcs.componentes.chequeo.ISubsistemasExternos;
import es.nimio.nimiogcs.componentes.chequeo.modelo.DescripcionSubsistemasExternos;

@Component
public class SubsistemasExternosRequiereJenkins 
		implements ISubsistemasExternos {

	private IContextoEjecucionBase ce;
	
	@Autowired
	public SubsistemasExternosRequiereJenkins(IContextoEjecucionBase ce) {
		this.ce = ce;
	}
	
	// ---
	
	@Override
	public DescripcionSubsistemasExternos subsistemas() {
		return 
				new DescripcionSubsistemasExternos(
						"JENKINS",
						new DescripcionSubsistemasExternos.Subsistema(
								"Jenkins (publicación en Artifactory)", 
								DescripcionSubsistemasExternos.extractDomainName(
										ce.repos().global().buscar("PUBLICACION.JENKINS.SERVIDOR").getComoValorSimple()
								),
								ce.repos().global().buscar("PUBLICACION.JENKINS.SERVIDOR").getComoValorSimple(), 
								ce.repos().global().buscar("PUBLICACION.JENKINS.SERVIDOR.USUARIO").getComoValorSimple(), 
								ce.repos().global().buscar("PUBLICACION.JENKINS.SERVIDOR.PASSWORD").getComoValorSimple()
						)
				);
	}
}
