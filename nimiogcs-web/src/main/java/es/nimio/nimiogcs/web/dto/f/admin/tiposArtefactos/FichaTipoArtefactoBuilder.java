package es.nimio.nimiogcs.web.dto.f.admin.tiposArtefactos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;

public class FichaTipoArtefactoBuilder {

	public static Collection<IComponente> componenteFichaGeneral(TipoArtefacto tipoArtefacto) {

		
		ArrayList<IComponente> componentes = new ArrayList<IComponente>();
		
		// datos generales
		componentes.add(
				new PanelContinente()
				.conTitulo("Datos generales")
				.paraTipoDefecto()
				.siendoContraible()
				.conComponente(MetodosDeUtilidad.parClaveValor("Identificador:", tipoArtefacto.getId()))
				.conComponente(MetodosDeUtilidad.parClaveValor("Nombre:", tipoArtefacto.getNombre()))
		);
		
		componentes.addAll(componenteFichaDirectivas(tipoArtefacto.getDirectivasTipo()));
		
		return componentes;
	}
	private static Collection<IComponente> componenteFichaDirectivas(Collection<? extends DirectivaBase> directivas) {
	
		ArrayList<IComponente> componentes = new ArrayList<IComponente>();
		for(DirectivaBase directiva: directivas) {
		       componentes.add( new PanelContinente()
			          .conTitulo(directiva.getDirectiva().getNombre())
			          .paraTipoDefecto()
					  .siendoContraible()
					  .conComponentes(
							  listaValoresDirectiva(directiva)
					  )
		 );
		}
		return componentes;
	}
	
	private static Collection<IComponente> listaValoresDirectiva(DirectivaBase directiva) {
		
		ArrayList<IComponente> componentes = new ArrayList<IComponente>();

		for(Entry<String, String> kv: directiva.getMapaValores().entrySet()) {
			componentes.add(MetodosDeUtilidad.parClaveValor(kv.getKey(), kv.getValue()));
		}
		
		return componentes;
	}		

	public static long totalArtefactosTipoArtefacto (IContextoEjecucion ce,String tipo){
		
		Pageable peticion = new PageRequest(
				0, 
				1,
				new Sort("nombre"));
		
		Page<Artefacto> artefactos = ce.artefactos()
				.findAll(
						crearSpecConsulta(tipo), 
						peticion
				);
		
		return artefactos.getTotalElements();
		
	}
	
	private static Specification<Artefacto> crearSpecConsulta(
			final String filtroTipo) {
		
		
		return new Specification<Artefacto>() {

			@Override
			public Predicate toPredicate(Root<Artefacto> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {

				Predicate p = root.isNotNull();
			
				boolean filtrarTipo = (filtroTipo != null && !filtroTipo.isEmpty());
				// filtrar por tipo, cuando se pide que se incluyan únicamente los de ese tipo
				if(filtrarTipo) {
					p = cb.and(p, cb.equal(root.get("tipo").get("id"), filtroTipo));
				}
				
				return p;
			}
		};
	}	
	
	
}
