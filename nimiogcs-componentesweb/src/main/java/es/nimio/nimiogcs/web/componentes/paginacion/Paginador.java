package es.nimio.nimiogcs.web.componentes.paginacion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.nimio.nimiogcs.web.componentes.Fragmento;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.IContinente;

@Fragmento(grupo="_paginacion", id="paginador")
public class Paginador implements IContinente<IComponente> {

	private Indexador indexador;
	private PiePaginador pie;
	private List<IComponente> componentes = new ArrayList<IComponente>();

	
	// ----------------------------------------------
	// Api fluido
	// ----------------------------------------------
	
	public Paginador conIndexador(Indexador indexador) {
		this.indexador = indexador;
		return this;
	}
	
	public Paginador conContenido(IComponente componente) {
		this.componentes.add(componente);
		return this;
	}
	
	public Paginador conContenido(List<IComponente> contenido) {
		this.componentes.addAll(contenido);
		return this;
	}
	
	@Override
	public String clasesParaHtml() {
		return "";
	}
	@Override
	public Collection<IComponente> componentes() {
		List<IComponente> enriquecida = new ArrayList<IComponente>();
		if(indexador!=null) enriquecida.add(indexador);
		enriquecida.addAll(componentes);
		if(indexador!=null) enriquecida.add(indexador);
		if(pie!=null) enriquecida.add(pie);
		
		return enriquecida;
	}
	
	
	
	
}
