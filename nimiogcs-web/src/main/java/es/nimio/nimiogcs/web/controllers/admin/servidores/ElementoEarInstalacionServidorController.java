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
import es.nimio.nimiogcs.functional.Tuples.T3;
import es.nimio.nimiogcs.functional.Tuples.T4;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.artefactos.Artefacto;
import es.nimio.nimiogcs.jpa.entidades.operaciones.Operacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.ServidorJava;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorAplicacion;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.relaciones.RelacionServidorArtefacto;
import es.nimio.nimiogcs.jpa.specs.ServidoresArtefactos;
import es.nimio.nimiogcs.operaciones.OperacionInternaModulo;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.f.admin.servidores.AgregarRelacionServidorConEarDto;
import es.nimio.nimiogcs.web.errores.ErrorEntidadNoEncontrada;

@Controller
@RequestMapping("/admin/ciclovida/sitios/servidor")
public class ElementoEarInstalacionServidorController extends ElementoInstalacionServidorControllerBase {

	// ------------------------------------------------
	// Construcción
	// ------------------------------------------------
	
	@Autowired
	public ElementoEarInstalacionServidorController(
			IContextoEjecucion ce) {
		super(ce);
	}
	
	// ------------------------------------------
	// Métodos que son necesarios sobrecargar
	// ------------------------------------------

	@Override
	protected String grupo() { return "EAR's"; }

	@Override
	protected String subarea() { return "ear"; }
	
	@Override
	protected String tituloPagina(boolean alta) { return alta ? "Registrar un nuevo EAR" : "Actualizar datos relación Servidor/Ear"; }

	@Override
	protected Class<?> tipoDto() { return AgregarRelacionServidorConEarDto.class; } 
	
	@Override
	protected Map<String, Collection<NombreDescripcion>> diccionariosNecesariosPagina(boolean alta) {
		// cargamos la lista de todos los EARs para que se puedan seleccionar
		Collection<NombreDescripcion> ears = alta ? 
				Collections.list(
					Streams.of(ce.artefactos().earsRegistrados())
						.map(new Function<Artefacto, NombreDescripcion>() {
							@Override
							public NombreDescripcion apply(Artefacto ear) {
								return new NombreDescripcion(ear.getId(), ear.getNombre());
							}
						})
						.getEnumeration()
					)
				: new ArrayList<NombreDescripcion>();
		
		// mapa que pasaremos al ayudante para fabricar el formulario
		Map<String, Collection<NombreDescripcion>> diccionarioValores = new HashMap<String, Collection<NombreDescripcion>>();
		if(alta) diccionarioValores.put("ears", ears);

		return diccionarioValores;
	}
	
	// ------------------------------------------
	// Registro EAR
	// ------------------------------------------
	
	@RequestMapping(path="/ear/nuevo/{id}", method=RequestMethod.GET)
	public ModeloPagina registrarEarEnServidor(@PathVariable String id) {

		// lo primero será buscar el sitio
		Servidor sitio = ce.servidores().findOne(id);
		if(sitio == null) throw new ErrorEntidadNoEncontrada();
		
		// pero también debemos garantizar que es de tipo correcto
		if(!(sitio instanceof ServidorJava)) throw new ErrorEntidadNoEncontrada();
		
		// los datos que se pasarán como parte del modelo
		AgregarRelacionServidorConEarDto datos = new AgregarRelacionServidorConEarDto(); 
		datos.setIdServidorJava(sitio.getId());

		ModeloPagina ep = crearEstructuraPaginaRegistroElemento(true, sitio);
		ep.addObject("datos", datos);
		return ep;
	}
	
	@RequestMapping(path="/ear/nuevo", method=RequestMethod.POST)
	public ModelAndView registrarEarEnServidor(
			@Valid @ModelAttribute("datos") AgregarRelacionServidorConEarDto datos, 
			Errors errores) {

		// cargamos los datos del servidor
		String idServidor = datos.getIdServidorJava();
		Servidor sitio = ce.servidores().findOne(idServidor);
		if(sitio==null || !(sitio instanceof ServidorJava)) 
			throw new ErrorEntidadNoEncontrada();
		
		// si se ha informado el ear, pasamos a preguntar si ya existe una relación entre servidor y ear.
		String idEar = datos.getIdEar(); 
		boolean buscarRepeticion = (idServidor != null && !idServidor.trim().isEmpty()) && (idEar != null && !idEar.trim().isEmpty());
		if(buscarRepeticion) {
			List<RelacionServidorArtefacto> relacionesServidorEar = ce.servidoresArtefactos().findAll(ServidoresArtefactos.earsEnServidor(sitio));
			RelacionServidorAplicacion relacionServidorEar = null;
			for(RelacionServidorArtefacto relacion: relacionesServidorEar) 
				if(relacion.getArtefacto().getId().equalsIgnoreCase(idEar)) relacionServidorEar = (RelacionServidorAplicacion)relacion;
			
			if(relacionServidorEar!=null) 
				errores.rejectValue("idEar", "REL_DUPLICADA", "Ya existe una relación de instalación del EAR en el Servidor");
		}
		
		if(errores.hasErrors()) {
			ModeloPagina ep = crearEstructuraPaginaRegistroElemento(true, sitio);
			ep.addObject("datos", datos);
			return ep;
		}
		
		// superados todos los errores, nos queda guardar la relación
		// para lo que primero tenemos que convertir los identificadores en las entidades
		ServidorJava servidor = (ServidorJava)sitio;
		Artefacto ear = (Artefacto)ce.artefactos().findOne(idEar);
		
		// la operación
		// -------->
		new OperacionInternaModulo<Tuples.T4<ServidorJava, Artefacto, String, String>, Boolean>(ce) {

			@Override
			protected String nombreOperacion(T4<ServidorJava, Artefacto, String, String> datos, Operacion op) {
				return new StringBuilder("AÑADIR EAR A SERVIDOR ('")
						.append(datos._1.getNombre())
						.append("','")
						.append(datos._2.getNombre())
						.append("')")
						.toString();
			}
			
			@Override
			protected void relacionarOperacionConEntidades(T4<ServidorJava, Artefacto, String, String> datos, Operacion op) {
				registraRelacionConOperacion(op, datos._1);
				registraRelacionConOperacion(op, datos._2);
			}

			@Override
			protected Boolean hazlo(T4<ServidorJava, Artefacto, String, String> datos, Operacion op) {

				// guardamos la relación
				ce.servidoresArtefactos().saveAndFlush(
						new RelacionServidorAplicacion(
								datos._1, 
								datos._2, 
								datos._3, 
								datos._4)
						);
				
				return true;
			}
		}
		.ejecutar(
				Tuples.tuple(
						servidor, 
						ear, 
						datos.getIdAplicacion(), 
						datos.getVirtualHost()
				)
		);
		
		// <--------
		
		return redireccionInfoConfiguracionSitio(datos.getIdServidorJava());
	}

	@RequestMapping(path="/ear/editar/{id}", method=RequestMethod.GET)
	public ModeloPagina editarRegistroEarEnServidor(@PathVariable String id) {

		// lo que viene como parámetro será la relación entre Servidor y EAR
		RelacionServidorAplicacion relacion = (RelacionServidorAplicacion)ce.servidoresArtefactos().findOne(id);
		if(relacion == null) throw new ErrorEntidadNoEncontrada();
		
		// los datos que se pasarán como parte del modelo
		AgregarRelacionServidorConEarDto datos = AgregarRelacionServidorConEarDto.paraEditar(relacion); 

		ModeloPagina ep = crearEstructuraPaginaRegistroElemento(
				false, 
				(Servidor)relacion.getServidor());
		ep.addObject("datos", datos);
		return ep;
	}

	@RequestMapping(path="/ear/editar", method=RequestMethod.POST)
	public ModelAndView editarRegistroEarEnServidor(
			@Valid @ModelAttribute("datos") AgregarRelacionServidorConEarDto datos, 
			Errors errores) {

		// lo que viene como parámetro será la relación entre Servidor y EAR
		RelacionServidorAplicacion relacion = (RelacionServidorAplicacion)ce.servidoresArtefactos().findOne(datos.getIdRelacion());
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
		new OperacionInternaModulo<Tuples.T3<RelacionServidorAplicacion, String, String>, Boolean>(ce) {

			@Override
			protected String nombreOperacion(T3<RelacionServidorAplicacion, String, String> datos, Operacion op) {
				return new StringBuilder("CAMBIAR DATOS RELACIÓN SERVIDOR-EAR ('")
						.append(datos._1.getServidor().getNombre())
						.append("','")
						.append(datos._1.getArtefacto().getNombre())
						.append("')")
						.toString();
			}
			
			@Override
			protected void relacionarOperacionConEntidades(T3<RelacionServidorAplicacion, String, String> datos,
					Operacion op) {
				registraRelacionConOperacion(op, datos._1.getServidor());
				registraRelacionConOperacion(op, datos._1.getArtefacto());
			}

			@Override
			protected Boolean hazlo(T3<RelacionServidorAplicacion, String, String> datos, Operacion op) {
				datos._1.setIdAplicacion(datos._2);
				datos._1.setVirtualHost(datos._3);
				ce.servidoresArtefactos().saveAndFlush(datos._1);
				return null;
			}
		}
		.ejecutar(
				Tuples.tuple(
						relacion,
						datos.getIdAplicacion(),
						datos.getVirtualHost()
				)
		);
		
		// <---------
		
		return redireccionInfoConfiguracionSitio(datos.getIdServidorJava());
	}
}
