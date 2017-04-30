package es.nimio.nimiogcs.aspectos;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import es.nimio.nimiogcs.servicios.IContextoEjecucion;

@Aspect
public abstract class CommonControllerAspects {

	protected IContextoEjecucion ce;
	
	public CommonControllerAspects(IContextoEjecucion ce) {
		this.ce = ce;
	}

	@Pointcut(value="execution(* *(..)) && within(@org.springframework.stereotype.Controller *)")
	public void controllerMethodExecution() {};
}
