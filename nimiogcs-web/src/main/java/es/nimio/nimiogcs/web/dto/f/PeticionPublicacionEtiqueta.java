package es.nimio.nimiogcs.web.dto.f;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.Predicate;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.EtiquetaProyecto;
import es.nimio.nimiogcs.jpa.entidades.proyectos.Proyecto;

public final class PeticionPublicacionEtiqueta {

	private Proyecto proyecto;
	private EtiquetaProyecto etiqueta;
	private String codigoPROI;
	private List<ArtefactoElegido> artefactos;

	public Proyecto getProyecto() {
		return proyecto;
	}

	public void setProyecto(Proyecto proyecto) {
		this.proyecto = proyecto;
	}

	public EtiquetaProyecto getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(EtiquetaProyecto etiqueta) {
		this.etiqueta = etiqueta;
	}
	
	@NotNull(message="Debe indicar el código PROI asociado a la petición de publicación")
	@Size(min=2,max=6, message="El tamaño del código PROI debe ser de 6 caracteres, salvo cuando se indique el genérico 'XX'")
	public String getCodigoProi() { 
		return this.codigoPROI; 
	}
	
	public void setCodigoProi(String codigoProi) { 
		this.codigoPROI = codigoProi; 
	}

	public List<ArtefactoElegido> getArtefactos() {
		return artefactos;
	}

	public void setArtefactos(List<ArtefactoElegido> artefactos) {
		this.artefactos = artefactos;
	}

	// ----------------------------------------------

	/**
	 * Devuelve una tupla de tres elementos tal como lo espera la operación que
	 * iniciar el proceso de publicación de una etiqueta.
	 */
	public Tuples.T4<Proyecto, EtiquetaProyecto, String, List<Artefacto>> paraOperacion() {

		// lista de las entidades que hay que publicar
		List<Artefacto> entidades = Collections.list(
					Streams.of(getArtefactos())
					.filter(new Predicate<ArtefactoElegido>() {

						@Override
						public boolean test(ArtefactoElegido artefactoElegido) {
							return artefactoElegido.getElegido();
						}
						
					})
					.map(new Function<ArtefactoElegido, Artefacto>() {

						@Override
						public Artefacto apply(ArtefactoElegido artefactoElegido) {
							return artefactoElegido.getArtefacto();
						}
						
					})
					.getEnumeration()
				);

		// la tupla que nos interesa
		return Tuples.tuple(
				this.getProyecto(),
				this.getEtiqueta(),
				this.getCodigoProi(),
				entidades);
		
	}

	// ----------------------------------------------

	/**
	 * Estructura auxiliar que contiene los valores de selección.
	 */
	public final static class ArtefactoElegido {

		private Boolean elegido = false;
		private Artefacto artefacto;

		public Boolean getElegido() {
			return elegido;
		}

		public void setElegido(Boolean elegido) {
			this.elegido = elegido;
		}

		public Artefacto getArtefacto() {
			return artefacto;
		}

		public void setArtefacto(Artefacto artefacto) {
			this.artefacto = artefacto;
		}

		public String getTipoArtefacto() {
			return this.artefacto.getTipoArtefacto().getNombre();
		}
	}
}
