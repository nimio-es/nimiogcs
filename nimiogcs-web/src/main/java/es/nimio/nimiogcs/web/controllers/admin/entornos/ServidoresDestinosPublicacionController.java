package es.nimio.nimiogcs.web.controllers.admin.entornos;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.Tuples.NombreDescripcion;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.DestinoPublicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.EntornoConServidores;
import es.nimio.nimiogcs.jpa.entidades.sistema.entornos.relaciones.RelacionEntornoServidor;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;
import es.nimio.nimiogcs.jpa.specs.EntornosServidores;
import es.nimio.nimiogcs.operaciones.OperacionInternaModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.formularios.AyudanteCalculoEstructuraFormularioDesdePojo;
import es.nimio.nimiogcs.web.componentes.formularios.Formulario;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.admin.entornos.AsociarServidorAEntornoForm;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;
import es.nimio.nimiogcs.web.dto.p.admin.entornos.PaginaServidoresEntornos;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping("/admin/ciclovida/entornos/servidores")
public class ServidoresDestinosPublicacionController {

	final private IContextoEjecucion ce;
	
	@Autowired
	public ServidoresDestinosPublicacionController(
			IContextoEjecucion ce) {
		this.ce = ce;
	}

	// ************************************************
	// Ficha
	// ************************************************

	/**
	 * Da los datos de todos los servidores asociados a un entorno.
	 */
	@RequestMapping(path="/{id}", method=RequestMethod.GET)
	public ModeloPagina read(@PathVariable String id) {
		
		// cargamos
		DestinoPublicacion entorno = ce.destinosPublicacion().findOne(id);
		if(entorno == null) throw new ErrorEntidadNoEncontrada();
		
		return ModeloPagina.nuevaPagina(
				new PaginaServidoresEntornos(
						entorno,
						ce.operaciones().artefactoConOperacionesEnCurso(entorno.getId()) != 0,
						ce.entornosServidores().findAll(EntornosServidores.servidoresEntorno(entorno))
				)
		);
	}
	
	
	// *************************************************
	// Vincular Entorno a servidor
	// *************************************************
	
	/**
	 * Iniciar el proceso de vinculado de un servidor en un entorno
	 */
	@RequestMapping(path="/vincular/{id}", method=RequestMethod.GET)
	public ModeloPagina vincular(@PathVariable String id) {
		
		// cargamos
		DestinoPublicacion entorno = ce.destinosPublicacion().findOne(id);
		if(entorno == null || !(entorno instanceof EntornoConServidores)) throw new ErrorEntidadNoEncontrada();

		// se trata de presentar el formulario
		return creaEstructuraPaginaAsociarServidorAEntorno(entorno)
				.conModelo("datos", 
						new AsociarServidorAEntornoForm()
						.paraEntorno((EntornoConServidores)entorno)
				);
	}
	
	/**
	 * Finaliza la operación
	 * @param datos
	 * @param errores
	 * @return
	 * @throws Throwable
	 */
	@RequestMapping(path="/vincular", method=RequestMethod.POST)
	public ModelAndView vincular(@Valid @ModelAttribute("datos") AsociarServidorAEntornoForm datos, Errors errores) {		
		// cargamos
		DestinoPublicacion entorno = ce.destinosPublicacion().findOne(datos.getIdEntorno());
		if(entorno == null || !(entorno instanceof EntornoConServidores)) throw new ErrorEntidadNoEncontrada();

		// comprobamos errores
		if(errores.hasErrors()) 
			return creaEstructuraPaginaAsociarServidorAEntorno(entorno)
					.conModelo("datos", datos);

		// también cargamos el servidor
		Servidor servidor = ce.servidores().findOne(datos.getServidor());
		if(servidor == null) throw new ErrorEntidadNoEncontrada();
		

		// creamos la relación
		new OperacionInternaModulo<Tuples.T2<EntornoConServidores, Servidor>, Boolean>(ce) {

			@Override
			protected String nombreOperacion(Tuples.T2<EntornoConServidores, Servidor> tupla, Operacion op) {
				return new StringBuilder("ASOCIAR SERVIDOR '")
						.append(tupla._2.getNombre())
						.append("' A ENTORNO '")
						.append(tupla._1.getNombre())
						.append("'")
						.toString();
			}

			@Override
			protected void relacionarOperacionConEntidades(Tuples.T2<EntornoConServidores, Servidor> tupla, Operacion op) {
				registraRelacionConOperacion(op, tupla._1);
				registraRelacionConOperacion(op, tupla._2);
			}

			@Override
			protected Boolean hazlo(Tuples.T2<EntornoConServidores, Servidor> tupla, Operacion op) {
				ce.entornosServidores().saveAndFlush(
						new RelacionEntornoServidor(tupla._1, tupla._2)
				);
				
				return true;
			}
		}
		.ejecutar(Tuples.tuple((EntornoConServidores)entorno, servidor));
		
		return new ModelAndView("redirect:/admin/ciclovida/entornos/servidores/" + entorno.getId());
	}
	
	private ModeloPagina creaEstructuraPaginaAsociarServidorAEntorno(DestinoPublicacion entorno) {
		
		// generamos un diccionario con los servidores aún no vinculados
		HashMap<String, Collection<NombreDescripcion>> diccionarios = new HashMap<String, Collection<NombreDescripcion>>();
		diccionarios.put(
				"servidores",
				Collections.list(
						Streams.of(ce.servidores().servidoresAunNoRelacionadosConEntornos())
						.map(
								new Function<Servidor, NombreDescripcion>() {

									@Override
									public NombreDescripcion apply(Servidor v) {
										return new NombreDescripcion(v.getId(), v.getNombre());
									}
									
								}
						)
						.getEnumeration()
				)
		);
		
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Vincular servidor a entorno")
				.conComponentes(
						
						new Localizacion() 
						.conEnlace("Home", "/")
						.conTexto("Administracion")
						.conTexto("Ciclo de Vida")
						.conEnlace("Entornos", "admin/ciclovida/entornos")
						.conEnlace(entorno.getNombre(), "admin/ciclovida/entornos/" + entorno.getId())
						.conEnlace("Servidores", "admin/ciclovida/entornos/servidores/" + entorno.getId())
						.conTexto("Vincular nuevo servidor"),
						
						new Formulario(
								"datos",
								"/admin/ciclovida/entornos/servidores/vincular",
								"POST",
								"/admin/ciclovida/entornos/servidores/" + entorno.getId(),
								AyudanteCalculoEstructuraFormularioDesdePojo.altaDesdeDto(
										AsociarServidorAEntornoForm.class, 
										diccionarios
								)
						)
				)
		);
	}
}
