package es.nimio.nimiogcs.web.controllers.admin.repositorios;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.stream.Streams;
import es.nimio.nimiogcs.jpa.entidades.sistema.RepositorioCodigo;
import es.nimio.nimiogcs.repositorios.RepositorioRepositoriosCodigo;
import es.nimio.nimiogcs.web.Paths;
import es.nimio.nimiogcs.web.componentes.BotonEnlace;
import es.nimio.nimiogcs.web.componentes.Botonera;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.Localizacion;
import es.nimio.nimiogcs.web.componentes.TablaBasica;
import es.nimio.nimiogcs.web.componentes.basicos.EnlaceSimple;
import es.nimio.nimiogcs.web.dto.ModeloPagina;
import es.nimio.nimiogcs.web.dto.p.EstructuraPagina;

@Controller
@RequestMapping(path=Paths.Admin.RepositoriosCodigo.BASE)
public class ListadoRepositoriosController {

	private RepositorioRepositoriosCodigo repositorios;
	
	@Autowired
	public ListadoRepositoriosController(RepositorioRepositoriosCodigo repositorios) {
		this.repositorios = repositorios;
	}
	
	/**
	 * Lista con los repositorios actualmente registrados 
	 */
	@RequestMapping(method=RequestMethod.GET)
	public ModeloPagina listado() {
		
		return ModeloPagina.nuevaPagina(
				new EstructuraPagina("Listado de repositorios de código")
					.conComponentes(
							new IComponente[] {
									new Localizacion(
											Paths.TO_HOME,
											Paths.Admin.TO_ADMIN,
											Paths.Admin.RepositoriosCodigo.TO_REPOSITORIOS
										),
									listaRepositorios(repositorios),
									new Botonera(
											new BotonEnlace(
													"Registrar repositorio", 
													Paths.Admin.RepositoriosCodigo.NUEVO, 
													BotonEnlace.TIPO_NORMAL, 
													BotonEnlace.TAM_PEQUEÑO
											)
										)
										.conAlineacionALaDerecha()
							}
						)
				);
	}
	
	/**
	 * Extrae la lista de elementos de un repositorio y la convierte en un componente tabla
	 */
	private static TablaBasica listaRepositorios(RepositorioRepositoriosCodigo repositorios) {
		
		TablaBasica tabla = new TablaBasica(
				false, 
				Arrays.asList(new TablaBasica.DefinicionColumna[] {
						new TablaBasica.DefinicionColumna("Identificador", 4),
						new TablaBasica.DefinicionColumna("Url base repositorio", 8)
				})
			).conFilas(
					Collections.list(
							Streams.of(repositorios.findAll())
								.map(new Function<RepositorioCodigo, List<IComponente>>() {

									@Override
									public List<IComponente> apply(RepositorioCodigo repositorio) {
										
										return Arrays.asList(
												new IComponente[] {
													new EnlaceSimple(
															repositorio.getNombre(),
															Paths.Admin.RepositoriosCodigo.datos(repositorio)
														),
													
													new EnlaceSimple(
															repositorio.getUriRaizRepositorio(),
															repositorio.getUriRaizRepositorio()
														)
												}
											);
									}
								})
								.getEnumeration()
						)
				);
		
		return tabla;
	}
}
