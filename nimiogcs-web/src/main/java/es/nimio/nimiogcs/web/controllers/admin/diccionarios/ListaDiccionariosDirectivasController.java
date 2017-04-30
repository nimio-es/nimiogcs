package es.nimio.nimiogcs.web.controllers.admin.diccionarios;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionario;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.Paths;
import es.nimio.nimiogcs.web.componentes.EstructuraAbstractaPagina;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.Columnas;
import es.nimio.nimiogcs.web.componentes.basicos.ContinenteSinAspecto;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.basicos.TextoSimple;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;

@Controller
@RequestMapping(path=Paths.Admin.Diccionarios.BASE)
public class ListaDiccionariosDirectivasController {

	private IContextoEjecucion ce;
	
	@Autowired
	public ListaDiccionariosDirectivasController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// -----
	
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView listado() {
		
		EstructuraAbstractaPagina ep = 
				new EstructuraPagina("Listado diccionarios para directivas")
				.conComponentes(
						new Localizacion()
						.conItem(Paths.TO_HOME)
						.conItem(Paths.Admin.TO_ADMIN)
						.conItem(Paths.Admin.Diccionarios.TO_DICCIONARIOS)
				);
		
		List<TipoDirectivaDiccionario> diccionarios = ce.diccionariosDirectivas().findAll(new Sort("id"));
		IComponente principal;
		if(diccionarios.size() > 0) {
			TablaBasica tb = new TablaBasica(
					false,
					Arrays.asList(
							new TablaBasica.DefinicionColumna("ID", 2),
							new TablaBasica.DefinicionColumna("Descripción", 10)
					)
			);
			
			for(TipoDirectivaDiccionario d: diccionarios) {
				tb.conFila(
					new EnlaceSimple(
							d.getId(),
							Paths.Admin.Diccionarios.datos(d)
					),
					new TextoSimple(d.getDescripcion())
				);
			}
			
			principal = tb;
		} else {
			principal = new Parrafo("No constan la existencia de diccionarios para directivas.").deTipoPrincipal().enNegrita();
		}
		
		ep.conComponentes(
				new Columnas()
				.conColumna(
						new Columnas.Columna()
						.conAncho(9)
						.conComponentes(
								new PanelContinente()
								.conTitulo("Diccinarios registrados")
								.paraTipoPrimario()
								.conComponente(principal)
								.siendoContraible()
						)
				)
				.conColumna(
						new Columnas.Columna()
						.conAncho(3)
						.conComponentes(
								new PanelContinente()
								.conTitulo("Alta nuevo diccionario")
								.paraTipoDefecto()
								.conComponentes(
										new Parrafo("Se podrá registrar un nuevo diccionario a emplear usando el siguiente enlace:"),
										new Parrafo(" "),
										new ContinenteSinAspecto()
										.conComponentes(
											new EnlaceSimple()
											.conTexto("Crear nuevo diccionario")
											.paraUrl(Paths.Admin.Diccionarios.NUEVO)
										)
										.alineacionDerecha()
										.enColumna(12)
								)
						)
				)
		);
		
		return ModeloPagina.nuevaPagina(ep);
	}
	
}
