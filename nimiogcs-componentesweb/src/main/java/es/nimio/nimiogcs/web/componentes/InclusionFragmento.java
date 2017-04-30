package es.nimio.nimiogcs.web.componentes;

import es.nimio.nimiogcs.functional.Tuples;

@Fragmento(id="fragmento")
public class InclusionFragmento extends Tuples.T2<String, String> implements IComponente {

	private static final long serialVersionUID = -285564726702725747L;

	public InclusionFragmento(String plantilla, String fragmento) {
		super(plantilla, fragmento);
	}
	
	public String plantilla() { return _1; }
	public String fragmento() { return _2; }

	@Override
	public String clasesParaHtml() {
		return "";
	}
}
