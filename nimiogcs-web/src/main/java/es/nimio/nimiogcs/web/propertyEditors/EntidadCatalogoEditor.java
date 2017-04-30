package es.nimio.nimiogcs.web.propertyEditors;

import java.beans.PropertyEditorSupport;

import org.springframework.data.jpa.repository.JpaRepository;

import es.nimio.nimiogcs.jpa.entidades.MetaRegistro;

public class EntidadCatalogoEditor<T extends MetaRegistro> extends PropertyEditorSupport {

	private JpaRepository<T,String> artefactos;
	
	public EntidadCatalogoEditor(JpaRepository<T, String> artefactos) {
		this.artefactos = artefactos;
	}
	
	@Override
	public void setAsText(String id) throws IllegalArgumentException {
		this.setValue(artefactos.findOne(id));
	}
	
	@Override
	public String getAsText() {
		@SuppressWarnings("unchecked")
		T artefacto = (T)this.getValue();
		return artefacto != null ? artefacto.getId() : null;
	}
	
}
