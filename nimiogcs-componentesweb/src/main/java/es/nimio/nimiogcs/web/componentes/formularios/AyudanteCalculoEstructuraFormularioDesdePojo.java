package es.nimio.nimiogcs.web.componentes.formularios;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.Tuples.NombreDescripcion;
import es.nimio.nimiogcs.functional.Tuples.T2;
import es.nimio.nimiogcs.web.componentes.IComponente;
import es.nimio.nimiogcs.web.componentes.basicos.ContinenteSinAspecto;
import es.nimio.nimiogcs.web.componentes.basicos.Parrafo;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.AreaTexto;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.BloqueDescripcion;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Checkbox;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EsExtension;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Estatico;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.EtiquetaFormulario;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoAsociado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GrupoDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.GruposDeDatos;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.NoEditable;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Placeholder;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Privado;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Secreto;
import es.nimio.nimiogcs.web.componentes.formularios.anotaciones.Seleccion;
import es.nimio.nimiogcs.web.componentes.paneles.PanelContinente;

/**
 * Construye una colección de componentes a partir de la información de
 * anotaciones de un DTO
 */
public final class AyudanteCalculoEstructuraFormularioDesdePojo {

	public enum Operacion {
		ALTA, EDICION
	}

	// ------------------------------------------------------
	// Tipos internos auxiliares para fabricar la colección
	// de componentes
	// ------------------------------------------------------

	static final class PanelGrupo
			extends Tuples.T5<Integer, String, String, String, ArrayList<Tuples.T2<Integer, IComponente>>> {

		private static final long serialVersionUID = -7461199124070217010L;

		public PanelGrupo(Integer orden, String id, String nombre, String descripcion) {
			super(orden, id, nombre, descripcion, new ArrayList<Tuples.T2<Integer, IComponente>>());
		}

		public int orden() {
			return _1;
		}

		public String id() {
			return _2;
		}

		public String nombre() {
			return _3;
		}

		public String descripcion() {
			return _4;
		}

		public ArrayList<Tuples.T2<Integer, IComponente>> componentes() {
			return _5;
		}

		/**
		 * devuelve un panel con toda la información que se ha almacenado
		 * asociada al grupo o panel.
		 */
		public IComponente creaComponente() {

			// ordenamos la colección en base al orden de cada componente
			Collections.sort(_5, new Comparator<Tuples.T2<Integer, ?>>() {
				@Override
				public int compare(T2<Integer, ?> o1, T2<Integer, ?> o2) {
					return o1._1.compareTo(o2._1);
				}
			});

			// y nos quedamos solamente con el componente
			Collection<IComponente> listaComponentes = new ArrayList<IComponente>();
			for (Tuples.T2<Integer, IComponente> e : componentes()) {
				listaComponentes.add(e._2);
			}

			// ¿caso espacial?
			if (orden() == 0) {
				return new ContinenteSinAspecto(listaComponentes);
			} else {
				Collection<IComponente> listaPanelConDescripcion = new ArrayList<IComponente>();
				if (this.descripcion() != null && !descripcion().isEmpty())
					listaPanelConDescripcion.add(new Parrafo(descripcion()));
				listaPanelConDescripcion.addAll(listaComponentes);
				return new PanelContinente()
						.paraTipoDefecto()
						.conTitulo(nombre())
						.conComponentes(listaPanelConDescripcion)
						.siendoContraible();
			}
		}
	}

	// ------------------------------------------------------
	// Construcción
	// ------------------------------------------------------

	public static Collection<IComponente> altaDesdeDto(Class<?> tipoDto) {
		return convertirDesdeDto(Operacion.ALTA, tipoDto);
	}

	public static Collection<IComponente> altaDesdeDto(Class<?> tipoDto,
			Map<String, Collection<NombreDescripcion>> datosSeleccion) {
		return convertirDesdeDto(Operacion.ALTA, tipoDto, datosSeleccion);
	}

	public static Collection<IComponente> convertirDesdeDto(Operacion operacion, Class<?> tipoDto) {
		return convertirDesdeDto(operacion, tipoDto, new HashMap<String, Collection<NombreDescripcion>>());
	}

	public static Collection<IComponente> convertirDesdeDto(Operacion operacion, Class<?> tipoDto,
			Map<String, Collection<NombreDescripcion>> datosSeleccion) {

		// debemos manejar una lista de paneles donde se agrupan los datos
		ArrayList<AyudanteCalculoEstructuraFormularioDesdePojo.PanelGrupo> paneles = new ArrayList<AyudanteCalculoEstructuraFormularioDesdePojo.PanelGrupo>();

		// siempre hay un panel 0 o vacío
		paneles.add(new PanelGrupo(0, "INTERNO", "", ""));

		// cogemos la lista de grupos de datos y la convertimos en una lista de
		// panel grupo
		Class<?> tipoAnalizado = tipoDto;
		boolean continuar = true;

		// también vamos a ir recogiendo los campos
		ArrayList<Field> campos = new ArrayList<Field>();
		while (continuar) {
			GruposDeDatos grupos = tipoAnalizado.getAnnotation(GruposDeDatos.class);

			// de momento solo añadimos los campos que no sean estáticos
			for(Field f: tipoAnalizado.getDeclaredFields()) {
				if(java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
				campos.add(f);
			}
			
			if (grupos != null) {
				for (GrupoDeDatos grupo : grupos.grupos()) {
					paneles.add(new PanelGrupo(grupo.orden(), grupo.id(), grupo.nombre(), grupo.textoDescripcion()));
				}
			}
			continuar = esExtension(tipoAnalizado);
			if (continuar) {
				tipoAnalizado = tipoAnalizado.getSuperclass();
			}
		}

		// ordenamos los paneles
		Collections.sort(paneles, new Comparator<AyudanteCalculoEstructuraFormularioDesdePojo.PanelGrupo>() {

			@Override
			public int compare(AyudanteCalculoEstructuraFormularioDesdePojo.PanelGrupo o1,
					AyudanteCalculoEstructuraFormularioDesdePojo.PanelGrupo o2) {
				return new Integer(o1.orden()).compareTo(o2.orden());
			}

		});

		// ahora recorremos los campos y vamos construyendo el control de
		// formulario que corresponda
		// según lo que vayamos necesitando.
		for (Field field : campos) {

			// antes de revisar si debemos o no meterlo en alguna parte,
			// simplemente confirmamos si hay que ignorarlo
			if (ignorar(operacion, field))
				continue;

			// valores que de forma general tienen todos los valores de entrada
			// de datos
			PanelGrupo panel = paneles.get(0); // por defecto todo se agregará
												// al grupo sin estructura de
												// panel
			int posicionDentroDelGrupo = 1; // todos se añaden en la primera
											// posición
			String etiqueta = ""; // etiqueta que se usará en el formulario para
									// la entrada.
			String placeholder = ""; // placeholder para las entradas de datos
										// de tipo texto.
			String bloqueDescripcion = ""; // bloque con la descripción que
											// asociamos a la entrada.

			// si no hay indicador de otro tipo, usamos el formato convencional
			GrupoAsociado configuracionCampo = field.getAnnotation(GrupoAsociado.class);
			if (configuracionCampo != null) {

				// buscamos el panel al que hay que asociarlo
				panel = panelGrupoPorNombre(paneles, configuracionCampo.grupoContiene());

				// si no se encuentra, devolvemos el primero
				if (panel == null)
					panel = paneles.get(0);

				// también tomamos en consideración la posición
				posicionDentroDelGrupo = configuracionCampo.ordenEnGrupo();
			}

			// -- ahora pasamos a ver qué otros datos hay que tener en cuenta

			// cuando sea campo privado
			// ¿o hay que ocultarlo en la operación de edición?
			if (esCampoPrivado(field) || hayQueOcultarlo(operacion, field)) {
				panel.componentes()
						.add(Tuples.tuple(posicionDentroDelGrupo, (IComponente) new Oculto(field.getName())));
				continue;
			}

			// etiqueta
			etiqueta = etiqueta(field);

			// bloque de descripción
			bloqueDescripcion = bloqueDescripcion(field);

			// ¿se trata de una selección?
			if (esCampoSeleccion(field)) {

				String idEntradaValores = idEntradaValores(field);
				Collection<NombreDescripcion> valores = (idEntradaValores == null
						|| !datosSeleccion.containsKey(idEntradaValores)) ? new ArrayList<Tuples.NombreDescripcion>()
								: datosSeleccion.get(idEntradaValores);

				panel.componentes()
						.add(Tuples.tuple(posicionDentroDelGrupo,
								(IComponente) new GrupoFormularioSeleccion(field.getName(), etiqueta, bloqueDescripcion,
										valores)));

				continue;
			}

			if (esCampoCheck(field)) {

				panel.componentes()
						.add(Tuples.tuple(posicionDentroDelGrupo,
								(IComponente) new GrupoFormularioCheck(field.getName(), etiqueta,
										getTextoCampoCheck(field), bloqueDescripcion)));

				continue;
			}

			// ¿se trata de un campo estático? ¿O, estando en modo edición, es no editable?
			if (esCampoEstatico(field) || (operacion == Operacion.EDICION && esNoEditable(field))) {

				panel.componentes().add(Tuples.tuple(posicionDentroDelGrupo,
						(IComponente) new GrupoFormularioTextoEstatico(field.getName()).conEtiqueta(etiqueta)));

				continue;
			}

			if(esSecreto(field)) {
				
				panel.componentes().add(Tuples.tuple(posicionDentroDelGrupo,
						(IComponente) new GrupoFormularioSecreto(
								field.getName(), 
								placeholder, 
								etiqueta, 
								bloqueDescripcion)
						)
				);
				
				continue;
			}
			
			// el grupo cuando es un área de texto
			if(esAreaTexto(field)) {
				
				panel.componentes().add(
						Tuples.tuple(
								posicionDentroDelGrupo,
								(IComponente) new GrupoFormularioAreaTexto(
										field.getName(), 
										etiqueta, 
										filasAreaTexto(field), 
										bloqueDescripcion
								)
						)
				);
				
				continue;
			}
			
			// placeholder
			placeholder = placeholder(field);

			// al ser tipo texto, forzamos que el contenido sea un grupo de
			// texto
			panel.componentes().add(Tuples.tuple(posicionDentroDelGrupo,
					(IComponente) new GrupoFormularioTexto(field.getName(), placeholder, etiqueta, bloqueDescripcion)));
		}

		// procesados todos los campos ya estamos en disposición de devolver
		// la colección de componentes que se corresponden con los paneles
		Collection<IComponente> componentes = new ArrayList<IComponente>();
		for (PanelGrupo panel : paneles) {
			componentes.add(panel.creaComponente());
		}

		return componentes;
	}

	private static boolean esExtension(Class<?> tipoDto) {
		EsExtension extension = tipoDto.getAnnotation(EsExtension.class);
		return extension != null;
	}

	private static PanelGrupo panelGrupoPorNombre(Collection<PanelGrupo> grupos, String nombre) {
		for (PanelGrupo grupo : grupos) {
			if (grupo.id().equalsIgnoreCase(nombre))
				return grupo;
		}
		return null;
	}

	private static boolean ignorar(Operacion op, Field field) {
		Estatico estatico = field.getAnnotation(Estatico.class);
		if (estatico == null)
			return false;
		if (op == Operacion.ALTA)
			return !estatico.enAlta();
		if (op == Operacion.EDICION)
			return !estatico.enEdicion();
		return false;
	}

	private static boolean esCampoPrivado(Field field) {
		return field.getAnnotation(Privado.class) != null;
	}

	private static boolean hayQueOcultarlo(Operacion op, Field field) {
		if (op == Operacion.ALTA)
			return false;
		NoEditable noEditar = field.getAnnotation(NoEditable.class);
		if (noEditar == null)
			return false;
		return noEditar.ocultar();
	}

	private static boolean esCampoSeleccion(Field field) {
		return field.getAnnotation(Seleccion.class) != null;
	}

	private static boolean esCampoCheck(Field field) {
		Checkbox checkbox = field.getAnnotation(Checkbox.class);
		return (checkbox != null);
	}

	private static boolean esSecreto(Field field) {
		return field.getAnnotation(Secreto.class) != null;
	}
	
	private static String getTextoCampoCheck(Field field) {
		Checkbox checkbox = field.getAnnotation(Checkbox.class);
		return checkbox != null ? checkbox.texto() : "";
	}

	private static String etiqueta(Field field) {
		EtiquetaFormulario etiqueta = field.getAnnotation(EtiquetaFormulario.class);
		if (etiqueta == null)
			return "";
		return etiqueta.value();
	}

	private static String bloqueDescripcion(Field field) {
		BloqueDescripcion descripcion = field.getAnnotation(BloqueDescripcion.class);
		if (descripcion == null)
			return "";
		return descripcion.value();
	}

	private static String placeholder(Field field) {
		Placeholder placeholder = field.getAnnotation(Placeholder.class);
		if (placeholder == null)
			return "";
		return placeholder.value();
	}

	private static String idEntradaValores(Field field) {
		Seleccion seleccion = field.getAnnotation(Seleccion.class);
		if (seleccion == null)
			return null;
		return seleccion.idColeccion();
	}

	private static boolean esCampoEstatico(Field field) {
		return field.getAnnotation(Estatico.class) != null;
	}
	
	private static boolean esNoEditable(Field field) {
		return field.getAnnotation(NoEditable.class) != null;
	}
	
	private static boolean esAreaTexto(Field field) {
		return field.getAnnotation(AreaTexto.class) != null;
	}

	private static int filasAreaTexto(Field field) {
		AreaTexto at = field.getAnnotation(AreaTexto.class);
		return at.filas();
	}

}
