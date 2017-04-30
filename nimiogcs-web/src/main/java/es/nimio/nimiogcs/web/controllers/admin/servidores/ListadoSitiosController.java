package es.nimio.nimiogcs.web.controllers.admin.servidores;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.stream.Stream;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.Servidor;
import es.nimio.nimiogcs.jpa.entidades.sistema.servidores.ServidorJava;
import es.nimio.nimiogcs.repositorios.RepositorioServidores;

@Controller
@RequestMapping("/admin/ciclovida/sitios")
public class ListadoSitiosController {

	// ------------------------------------------------
	// Estado
	// ------------------------------------------------

	private RepositorioServidores sitios;
	
	// ------------------------------------------------
	// Construcción
	// ------------------------------------------------
	
	@Autowired
	public ListadoSitiosController(
			RepositorioServidores sitios) {
		this.sitios = sitios;
	}

	// ************************************************
	
	// ------------------------------------------------
	// Puntos de servicio
	// ------------------------------------------------
	
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView list() {
		
		// cargamos la lista de sitios
		Collection<Servidor> sitiosRegistrados = sitios.findAll(); 
		
		// ahora, calculamos la lista de valores que hay que fijar en cada caso
		Stream<Tuples.T6<String, String, String, String, ArrayList<String>, ArrayList<String>>> sitiosAMostrar = Streams.of(sitiosRegistrados)
				.map(new Function<Servidor, Tuples.T6<String, String, String, String, ArrayList<String>, ArrayList<String>>>() {

					@Override
					public Tuples.T6<String, String, String, String, ArrayList<String>, ArrayList<String>> apply(Servidor sitio) {
						return Tuples.tuple(
								sitio.getId(),
								sitio.getNombre(), 
								tipoSitio(sitio), 
								"-", 
								new ArrayList<String>(), 
								new ArrayList<String>());
					}
					
				});
		
		ModelAndView mv = new ModelAndView("/admin/ciclovida/sitios/listado");
		mv.addObject("sitios", Collections.list(sitiosAMostrar.getEnumeration()));
		
		return mv;
	}
	
	private String tipoSitio(Servidor sitio) {
		if(sitio instanceof ServidorJava) {
			ServidorJava sj = (ServidorJava) sitio;
			return new StringBuilder("Servidor (")
				.append(sj.getTipoServidor().getDescripcion())
				.append(')')
				.toString();
		}
		
		return "";
	}
	
}
