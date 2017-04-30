package es.nimio.nimiogcs.web.dto.f.directivas;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.springframework.validation.Errors;

import es.nimio.nimiogcs.Strings;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaBase;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.DirectivaDiccionario;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionario;
import es.nimio.nimiogcs.jpa.entidades.artefactos.directivas.TipoDirectivaDiccionarioDefinicion;
import es.nimio.nimiogcs.servicios.IContextoEjecucion;

public class FormularioDirectivaDiccionario extends FormularioBaseDirectiva {

	private static final long serialVersionUID = -6266576624319788199L;

	private String diccionario;
	private HashMap<String, String> mapa = new HashMap<String, String>();
	
	public String getDiccionario() { return diccionario; }
	public void setDiccionario(String diccionario) { this.diccionario = diccionario; }
	public HashMap<String, String> getMapa() { return mapa; }
	public void setMapa(HashMap<String, String> mapa) { this.mapa = mapa; }
	

	@Override
	public void validar(IContextoEjecucion ce, Errors errores) {
		
		// para validar, tenemos que cargar el diccionario
		TipoDirectivaDiccionario tdd = ce.diccionariosDirectivas().findOne(diccionario);
		
		// creamos un mapa con los patrones de control
		HashMap<String, Pattern> patrones = new HashMap<String, Pattern>();
		for(TipoDirectivaDiccionarioDefinicion dd: tdd.getDefinicionesDiccionario())
			if(Strings.isNotEmpty(dd.getPatronControl()))
				patrones.put(dd.getClave(), Pattern.compile(dd.getPatronControl()));
		
		// ahora recorremos cada clave del mapa de entrada revisando si supera el control
		for(Entry<String, String> e: mapa.entrySet())
			if(patrones.containsKey(e.getKey()))
				if(!patrones.get(e.getKey()).matcher(e.getValue()).matches())
					errores.rejectValue("mapa[" + e.getKey() + "]", "FALLO-PATRON-CONTROL", "El valor no concuerda con el patrón de control.");
	}

	@Override
	public DirectivaDiccionario nueva(IContextoEjecucion ce) {
		DirectivaDiccionario nueva = new DirectivaDiccionario();
		actualiza(ce, nueva);
		return nueva;
	}

	@Override
	public void actualiza(IContextoEjecucion ce, DirectivaBase original) {
		DirectivaDiccionario dd = (DirectivaDiccionario)original;
		dd.setDiccionario(ce.diccionariosDirectivas().findOne(diccionario));
		dd.setValores(mapa);
	}

	@Override
	public void datosDesde(DirectivaBase directiva) {
		mapa.putAll(((DirectivaDiccionario)directiva).getValores());
	}

}
