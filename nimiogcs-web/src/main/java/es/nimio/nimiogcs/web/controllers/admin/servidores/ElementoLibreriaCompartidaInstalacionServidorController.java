package es.nimio.nimiogcs.web.controllers.admin.servidores;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import es.nimio.nimiogcs.functional.Tuples.T2;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.ServidorJava;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorArtefacto;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorLibreriaCompartida;
import es.nimio.nimiogcs.jpa.specs.ServidoresArtefactos;
import es.nimio.nimiogcs.operaciones.OperacionInternaModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.admin.servidores.AgregarRelacionServidorConLibreriaCompartidaDto;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping("/admin/ciclovida/sitios/servidor")
public class ElementoLibreriaCompartidaInstalacionServidorController extends ElementoInstalacionServidorControllerBase {

	// ------------------------------------------------
	// Estado exclusivo de este controlador
	// ------------------------------------------------

	// ------------------------------------------------
	// Construcción
	// ------------------------------------------------
	
	@Autowired
	public ElementoLibreriaCompartidaInstalacionServidorController(
			IContextoEjecucion ce) {
		super(ce);
	}
	
	// ------------------------------------------
	// Métodos que son necesarios sobrecargar
	// ------------------------------------------

	@Override
	protected String grupo() { return "Librerías compartidas"; }

	@Override
	protected String subarea() { return "lc"; }
	
	@Override
	protected String tituloPagina(boolean alta) { return alta ? "Registrar una nueva librería compartida" : "Actualizar relación servidor/librería compartida"; }

	@Override
	protected Class<?> tipoDto() { return AgregarRelacionServidorConLibreriaCompartidaDto.class; }

	@Override
	protected Map<String, Collection<NombreDescripcion>> diccionariosNecesariosPagina(
			boolean alta) {

		// cargamos la lista de todas las librerías compartidas para que se puedan seleccionar
		Collection<NombreDescripcion> lcs = alta ? 
				Collections.list(
					Streams.of(ce.artefactos().pomsActivos())
						.map(new Function<Artefacto, NombreDescripcion>() {
							@Override
							public NombreDescripcion apply(Artefacto libreriaCompartida) {
								return new NombreDescripcion(libreriaCompartida.getId(), libreriaCompartida.getNombre());
							}
						})
						.getEnumeration()
					)
				: new ArrayList<NombreDescripcion>();
		
		// mapa que pasaremos al ayudante para fabricar el formulario
		Map<String, Collection<NombreDescripcion>> diccionarioValores = new HashMap<String, Collection<NombreDescripcion>>();
		if(alta) diccionarioValores.put("lcs", lcs);

		return diccionarioValores;
}
	
	// ------------------------------------------
	// Registro Librería compartida
	// ------------------------------------------
	
	@RequestMapping(path="/lc/nuevo/{id}", method=RequestMethod.GET)
	public ModeloPagina registrarLibreriaCompartidaEnServidor(@PathVariable String id) {

		// lo primero será buscar el sitio
		Servidor sitio = ce.servidores().findOne(id);
		if(sitio == null) throw new ErrorEntidadNoEncontrada();
		
		// pero también debemos garantizar que es de tipo correcto
		if(!(sitio instanceof ServidorJava)) throw new ErrorEntidadNoEncontrada();
		
		// los datos que se pasarán como parte del modelo
		AgregarRelacionServidorConLibreriaCompartidaDto datos = new AgregarRelacionServidorConLibreriaCompartidaDto(); 
		datos.setIdServidorJava(sitio.getId());

		ModeloPagina ep = crearEstructuraPaginaRegistroElemento(true, sitio);
		ep.addObject("datos", datos);
		return ep;
	}
	
	@RequestMapping(path="/lc/nuevo", method=RequestMethod.POST)
	public ModelAndView registrarLibreriaCompartidaEnServidor(
			@Valid @ModelAttribute("datos") AgregarRelacionServidorConLibreriaCompartidaDto datos,
			Errors errores) {

		// cargamos los datos del servidor
		String idServidor = datos.getIdServidorJava();
		Servidor sitio = ce.servidores().findOne(idServidor);
		if(sitio==null || !(sitio instanceof ServidorJava)) 
			throw new ErrorEntidadNoEncontrada();

		// validamos la no duplicidad de la relación con una librería compartida
		String idLibreriaCompartida = datos.getIdLibreriaCompartida();
		boolean buscarRepeticion = (idServidor != null && !idServidor.trim().isEmpty()) && (idLibreriaCompartida != null && !idLibreriaCompartida.trim().isEmpty());
		if(buscarRepeticion) {
			List<RelacionServidorArtefacto> relacionesServidorLibreriasCompartidas = ce.servidoresArtefactos().findAll(ServidoresArtefactos.pomsEnServidor(sitio));
			RelacionServidorLibreriaCompartida relacionServidorLibreriaCompartida = null;
			for(RelacionServidorArtefacto relacion: relacionesServidorLibreriasCompartidas) 
				if(relacion.getArtefacto().getId().equalsIgnoreCase(idLibreriaCompartida)) relacionServidorLibreriaCompartida = (RelacionServidorLibreriaCompartida)relacion;
			
			if(relacionServidorLibreriaCompartida!=null) 
				errores.rejectValue("idLibreriaCompartida", "REL_DUPLICADA", "Ya existe una relación de instalación de Librería compartida en el Servidor");
		}
		
		// si hubiere errores, hay que volver a la página de alta
		if(errores.hasErrors()) {
			ModeloPagina ep = crearEstructuraPaginaRegistroElemento(true, sitio);
			ep.addObject("datos", datos);
			return ep;
		}

		// superados todos los errores, nos queda guardar la relación
		// para lo que primero tenemos que convertir los identificadores en las entidades
		final ServidorJava servidor = (ServidorJava)sitio;
		final Artefacto pom = (Artefacto)ce.artefactos().findOne(idLibreriaCompartida);
		
		// ----------->
		// la operación
		new OperacionInternaModulo<Tuples.T3<ServidorJava, Artefacto, String>, Boolean>(ce) {

			@Override
			protected String nombreOperacion(Tuples.T3<ServidorJava, Artefacto, String> datos, Operacion op) {
				return new StringBuilder("AÑADIR LIB.COMPARTIDA A SERVIDOR '")
						.append(datos._1.getNombre())
						.append("'")
						.toString();
			}
			
			@Override
			protected void relacionarOperacionConEntidades(Tuples.T3<ServidorJava, Artefacto, String> datos, Operacion op) {
				registraRelacionConOperacion(op, datos._1);
				registraRelacionConOperacion(op, datos._2);
			}

			@Override
			protected Boolean hazlo(Tuples.T3<ServidorJava, Artefacto, String> datos, Operacion op) {
				ce.servidoresArtefactos().saveAndFlush(
						new RelacionServidorLibreriaCompartida(
								servidor, 
								pom, 
								datos._3)
				);
				return true;
			}
			
		}
		.ejecutar(Tuples.tuple(servidor, pom, datos.getCarpetaServidor()));
		// <---------
		
		return redireccionInfoConfiguracionSitio(datos.getIdServidorJava());
	}

	
	
	@RequestMapping(path="/lc/editar/{id}", method=RequestMethod.GET)
	public ModeloPagina editarRegistroLibreriaCompartidaEnServidor(@PathVariable String id) {

		// lo que viene como parámetro será la relación entre Servidor y EAR
		RelacionServidorLibreriaCompartida relacion = (RelacionServidorLibreriaCompartida)ce.servidoresArtefactos().findOne(id);
		if(relacion == null) throw new ErrorEntidadNoEncontrada();
		
		// los datos que se pasarán como parte del modelo
		AgregarRelacionServidorConLibreriaCompartidaDto datos = AgregarRelacionServidorConLibreriaCompartidaDto.paraEditar(relacion); 

		ModeloPagina ep = crearEstructuraPaginaRegistroElemento(
				false, 
				relacion.getServidor());
		ep.addObject("datos", datos);
		return ep;
	}

	@RequestMapping(path="/lc/editar", method=RequestMethod.POST)
	public ModelAndView editarRegistroEarEnServidor(
			@Valid @ModelAttribute("datos") AgregarRelacionServidorConLibreriaCompartidaDto datos, 
			Errors errores) {

		// lo que viene como parámetro será la relación entre Servidor y EAR
		RelacionServidorLibreriaCompartida relacion = (RelacionServidorLibreriaCompartida)ce.servidoresArtefactos().findOne(datos.getIdRelacion());
		if(relacion == null) throw new ErrorEntidadNoEncontrada();

		if(errores.hasErrors()) {
			ModeloPagina ep = crearEstructuraPaginaRegistroElemento(
					false, 
					relacion.getServidor());
			ep.addObject("datos", datos);
			return ep;
		}
		
		// pasamos los cambios a la entidad
		// --------->
		new OperacionInternaModulo<Tuples.T2<RelacionServidorLibreriaCompartida, String>, Boolean>(ce) {

			@Override
			protected String nombreOperacion(T2<RelacionServidorLibreriaCompartida, String> datos,
					Operacion op) {
				return new StringBuilder("EDITAR CARPETA LIBRERIA COMPARTIDA EN SERVIDOR ('")
						.append(datos._1.getServidor().getNombre())
						.append("')")
						.toString();
			}
			
			@Override
			protected void relacionarOperacionConEntidades(T2<RelacionServidorLibreriaCompartida, String> datos,
					Operacion op) {
				registraRelacionConOperacion(op, datos._1.getServidor());
				registraRelacionConOperacion(op, datos._1.getArtefacto());
			}

			@Override
			protected Boolean hazlo(T2<RelacionServidorLibreriaCompartida, String> datos, Operacion op) {
				datos._1.setCarpetaServidor(datos._2);
				ce.servidoresArtefactos().saveAndFlush(datos._1);
				
				return true;
			}
			
		}
		.ejecutar(Tuples.tuple(relacion, datos.getCarpetaServidor()));
		// <---------
		
		return redireccionInfoConfiguracionSitio(datos.getIdServidorJava());
	}

	
}
