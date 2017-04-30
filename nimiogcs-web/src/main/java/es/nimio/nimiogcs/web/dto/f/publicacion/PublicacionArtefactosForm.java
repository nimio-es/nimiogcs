package es.nimio.nimiogcs.web.dto.f.publicacion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.nimio.nimiogcs.componentes.publicacion.modelo.IPeticionPublicacion;

public class PublicacionArtefactosForm 
	implements IPeticionPublicacion {

	public final static class EleccionArtefacto {
		
		private String idArtefacto;
		private String nombreArtefacto;
		private String tipoArtefacto;
		private boolean elegido;
		
		public EleccionArtefacto() {}
		
		public EleccionArtefacto(String idArtefacto, String nombreArtefacto, String tipoArtefacto, boolean elegido) {
			this.idArtefacto = idArtefacto;
			this.nombreArtefacto = nombreArtefacto;
			this.tipoArtefacto = tipoArtefacto;
			this.elegido = elegido;
		}

		public String getIdArtefacto() {
			return idArtefacto;
		}

		public void setIdArtefacto(String idArtefacto) {
			this.idArtefacto = idArtefacto;
		}

		public String getNombreArtefacto() {
			return nombreArtefacto;
		}

		public void setNombreArtefacto(String nombreArtefacto) {
			this.nombreArtefacto = nombreArtefacto;
		}

		public String getTipoArtefacto() {
			return tipoArtefacto;
		}

		public void setTipoArtefacto(String tipoArtefacto) {
			this.tipoArtefacto = tipoArtefacto;
		}

		public boolean isElegido() {
			return elegido;
		}

		public void setElegido(boolean elegido) {
			this.elegido = elegido;
		}
	}

	private HashMap<String, String> parametrosCanal = 
			new HashMap<String, String>();
	private List<PublicacionArtefactosForm.EleccionArtefacto> artefactos = 
			new ArrayList<PublicacionArtefactosForm.EleccionArtefacto>();
	
	public PublicacionArtefactosForm() {}
	
	public PublicacionArtefactosForm(
			HashMap<String, String> parametrosCanal, 
			List<PublicacionArtefactosForm.EleccionArtefacto> artefactos) {
		this.parametrosCanal = parametrosCanal;
		this.artefactos = artefactos;
	}

	@Override
	public HashMap<String, String> getParametrosCanal() {
		return parametrosCanal;
	}
	
	@Override
	public void setParametrosCanal(HashMap<String,String> parametros) {
		this.parametrosCanal = parametros;
	};
	
	public List<PublicacionArtefactosForm.EleccionArtefacto> getArtefactos() {
		return artefactos;
	}

	public void setArtefactos(List<PublicacionArtefactosForm.EleccionArtefacto> artefactos) {
		this.artefactos = artefactos;
	}
}
