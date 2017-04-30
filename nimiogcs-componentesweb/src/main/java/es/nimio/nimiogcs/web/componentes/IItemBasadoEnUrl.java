package es.nimio.nimiogcs.web.componentes;

public interface IItemBasadoEnUrl {

	public abstract String texto();

	public abstract String url();
	
	public abstract String parametros();

	public abstract boolean soloTexto();
	
	public abstract boolean tieneParametros();

}