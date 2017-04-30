package es.nimio.nimiogcs.web.controllers.admin.diccionarios;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionario;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionarioDefinicion;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.Paths;
import es.nimio.nimiogcs.web.componentes.BotonEnlace;
import es.nimio.nimiogcs.web.componentes.Botonera;
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
import es.nimio.nimiogcs.web.dto.p.MetodosDeUtilidad;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping(path=Paths.Admin.Diccionarios.BASE)
public class DatosDiccionarioDirectivaController {

	private IContextoEjecucion ce;
	
	@Autowired
	public DatosDiccionarioDirectivaController(IContextoEjecucion ce) {
		this.ce = ce;
	}
	
	// -----
	
	@RequestMapping(path=Paths.Admin.Diccionarios.PTRN_REQ_DATOS, method=RequestMethod.GET)
	public ModelAndView datos(@PathVariable("id") String id) {
		
		// confirmamos la existencia del diccionario
		TipoDirectivaDiccionario diccionario = ce.diccionariosDirectivas().findOne(id);
		if(diccionario==null) throw new ErrorEntidadNoEncontrada();
		
		return paginaDatos(diccionario);
		
	}

	// ----
	
	private ModeloPagina paginaDatos(TipoDirectivaDiccionario diccionario) {
		
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Datos diccionario directiva")
				.conComponentes(
						new Localizacion()
						.conItem(Paths.TO_HOME)
						.conItem(Paths.Admin.TO_ADMIN)
						.conItem(Paths.Admin.Diccionarios.TO_DICCIONARIOS)
						.conItem(Paths.Admin.Diccionarios.toDatos(diccionario))
						.conTexto("Datos"),
						
						new Columnas()
						.conColumna(
								new Columnas.Columna()
								.conAncho(9)
								.conComponentes(paneles(diccionario))
						)
						.conColumna(
								new Columnas.Columna()
								.conAncho(3)
								.conComponentes(
										new PanelContinente()
										.conTitulo("Eliminar")
										.paraTipoDefecto()
										.conComponentes(
												new Parrafo()
												.conTexto("Para eliminar el diccionario puede usar el siguiente enlace:"),
												
												new Parrafo(" "),

												new ContinenteSinAspecto()
												.conComponente(
														new EnlaceSimple()
														.conTexto("Eliminar diccionario")
														.paraUrl(Paths.Admin.Diccionarios.eliminar(diccionario))
														.peligro()
												)
												.enColumna(12)
												.alineacionDerecha()
										)
								)
						)
				)
		);
	}
	
	private List<IComponente> paneles(TipoDirectivaDiccionario diccionario) {
		
		ArrayList<IComponente> paneles = new ArrayList<IComponente>();
		
		// los datos de identificación
		paneles.add(
				new PanelContinente()
				.conTitulo("Datos de identificación")
				.paraTipoDefecto()
				.siendoContraible()
				.conComponentes(
						MetodosDeUtilidad.parClaveValor("Identificador:", diccionario.getId()),
						MetodosDeUtilidad.parClaveValor("Descripción:", diccionario.getDescripcion())
				)
		);
		
		// lista de parámetros
		ArrayList<IComponente> parametros = new ArrayList<IComponente>();
		if(diccionario.getDefinicionesDiccionario() == null || diccionario.getDefinicionesDiccionario().size() == 0)
			parametros.add(new Parrafo("No hay parámetros definidos para el diccionario.").deTipoPrincipal().enNegrita());
		else {
			TablaBasica tb = new TablaBasica(
					false,
					Arrays.asList(
							new TablaBasica.DefinicionColumna("Clave", 2),
							new TablaBasica.DefinicionColumna("Etiqueta", 4),
							new TablaBasica.DefinicionColumna("Posicion", 1),
							new TablaBasica.DefinicionColumna("Patrón de control", 4),
							new TablaBasica.DefinicionColumna("Acción", 1)
					)
			);
			
			for(TipoDirectivaDiccionarioDefinicion def: diccionario.getDefinicionesDiccionario()) {
				tb.conFila(
						new TextoSimple(def.getClave()),
						new TextoSimple(def.getEtiqueta()),
						new TextoSimple(Integer.toString(def.getPosicion())),
						new TextoSimple(def.getPatronControl()),
						new ContinenteSinAspecto()
						.conComponente(
								new BotonEnlace()
								.conTexto("Eliminar")
								.conTamañoMuyPequeño()
								.deTipoAviso()
								.paraUrl(Paths.Admin.Diccionarios.Definiciones.eliminar(diccionario, def))
						)
						.alineacionDerecha()
				);
			}
			
			parametros.add(tb);
		}

		parametros.add(new Parrafo(" "));
		parametros.add(
				new Botonera()
				.conAlineacionALaDerecha()
				.conBoton(
						new BotonEnlace(
								"Añadir una definición",
								Paths.Admin.Diccionarios.Definiciones.nueva(diccionario)
						)
				)
		);

		// la definición de parámetros
		paneles.add(
				new PanelContinente()
				.conTitulo("Definición de parámetros")
				.paraTipoDefecto()
				.siendoContraible()
				.conComponentes(parametros)
		);
		
		return paneles;
		
	}
	
}
