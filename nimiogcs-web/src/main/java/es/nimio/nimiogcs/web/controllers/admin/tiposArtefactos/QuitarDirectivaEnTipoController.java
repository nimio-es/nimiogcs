package es.nimio.nimiogcs.web.controllers.admin.tiposArtefactos;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.errores.ErrorInconsistenciaDatos;
import es.nimio.nimiogcs.jpa.entidades.artefactos.TipoArtefacto;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.operaciones.OperacionInternaInlineModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.componentes.paneles.PanelInformativo;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.PeticionConfirmacionGeneral;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path="/admin/tipos")
@SessionAttributes(names="estado")
public class QuitarDirectivaEnTipoController {

	private IContextoEjecucion ce;
	
	@Autowired
	public QuitarDirectivaEnTipoController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// ------------------------------
	
	@RequestMapping(path="/{idTipo}/directiva/quitar/{directiva}", method=RequestMethod.GET)
	public ModelAndView quitar (
			@PathVariable("idTipo") String idTipo,
			@PathVariable("directiva") String directiva) {
		
		// cargamos el tipo
		TipoArtefacto tipo = ce.tipos().findOne(idTipo);
		if(tipo == null) throw new ErrorEntidadNoEncontrada();
		
		// confirmamos que el tipo no tenga ya la directiva
		DirectivaBase de = null;
		for(DirectivaBase db: tipo.getDirectivasTipo())
			if(db.getDirectiva().getId().equalsIgnoreCase(directiva)) {
				de = db;
				break;
			}
		if(de==null) throw new ErrorEntidadNoEncontrada();
		
		// valor de control
		String control = UUID.randomUUID().toString();
		Map<String, Serializable> estado = new HashMap<String, Serializable>();
		estado.put("control", control);
		
		return 
				ModeloPagina.nuevaPagina(
						new EstructuraPagina("Eliminar directiva de un tipo")
						.conComponentes(
								new Localizacion()
								.conEnlace("Home", "/")
								.conTexto("Administracion")
								.conEnlace("Tipos de artefactos", "/admin/tipos")
								.conEnlace(tipo.getNombre(), "/admin/tipos/" + tipo.getId())
								.conTexto("Directivas")
								.conTexto(directiva)
								.conTexto("Quitar"),
								
								new PanelInformativo()
								.tipoPeligro()
								.conTexto(
										"Tenga presente quitar directivas de un tipo que tenga artefactos puede producir comportamientos inesperados."
								),
								
								new Formulario()
								.urlAceptacion("/admin/tipos/" + tipo.getId() + "/directiva/quitar/" + directiva)
								.conComponentes(
										AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(PeticionConfirmacionGeneral.class)
								)
								.botoneraEstandar("/admin/tipos/" + tipo.getId())
						)
				)
				.conModelo(
						"datos", 
						new PeticionConfirmacionGeneral(
								control, 
								"Eliminar directiva '" + directiva + "' del tipo de artefacto '" + tipo.getId() + "'"
						)
				)
				.conModelo("estado", estado);
	}
	
	@RequestMapping(path="/{idTipo}/directiva/quitar/{directiva}", method=RequestMethod.POST)
	public String quitar(
			final @PathVariable("idTipo") String idTipo,
			final @PathVariable("directiva") String directiva,
			final @ModelAttribute("datos") PeticionConfirmacionGeneral datos, 
			final @ModelAttribute("estado") Map<String, Serializable> estado,
			final SessionStatus ss) {
		
		// confirmamos que estamos en el caso exacto
		if(!estado.get("control").toString().equalsIgnoreCase(datos.getCodigoControl()))
			throw new ErrorInconsistenciaDatos("Error en el código de control");
		
		final TipoArtefacto tipo = ce.tipos().findOne(idTipo);
		if(tipo==null) throw new ErrorEntidadNoEncontrada();
		
		// confirmamos que el tipo no tenga ya la directiva
		DirectivaBase de = null;
		for(DirectivaBase db: tipo.getDirectivasTipo())
			if(db.getDirectiva().getId().equalsIgnoreCase(directiva)) {
				de = db;
				break;
			}
		if(de==null) throw new ErrorEntidadNoEncontrada();

		// procedemos a eliminar
		new OperacionInternaInlineModulo(ce) {
			
			@Override
			protected Boolean hazlo(Boolean datos, Operacion op) {
				
				// guardamos la relación con el tipo
				registraRelacionConOperacion(op, tipo);
				
				// empezamos quitando la relación entre el tipo y la directiva
				int p = -1;
				DirectivaBase dd = null;
				for (int i = 0; i < tipo.getDirectivasTipo().size(); i++)
				{
					DirectivaBase db = tipo.getDirectivasTipo().get(i);
					if(db.getDirectiva().getId().equalsIgnoreCase(directiva)) {
						p = i;
						dd = db;
						break;
					}
				}
				
				if(p >= 0) tipo.getDirectivasTipo().remove(p);
				ce.tipos().saveAndFlush(tipo);
				
				// y borramos el registro de la directiva
				ce.directivas().delete(dd);
				
				// fin
				return true;
			}
			
			@Override
			protected String generaNombreUnico() {
				return "QUITAR LA DIRECTIVA '" + directiva + "' DEL TIPO DE ARTEFACTO '" + idTipo + "'";
			}
			
		}.ejecutar();
		
		// fin
		ss.setComplete();
		return "redirect:/admin/tipos/" + idTipo;
	}
}
