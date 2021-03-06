
package es.nimio.nimiogcs.servicios.externos.sw.deployer;

import java.util.List;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "ServicePAIPortType", targetNamespace = "http://ws.nimio.es")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ServicePAIPortType {


    /**
     * 
     * @param param0
     * @param param1
     * @param param2
     * @return
     *     returns java.util.List<es.nimio.nimiogcs.servicios.externos.sw.deployer.ElementoBloqueado>
     */
    @WebMethod(action = "urn:getElementosBloqueados")
    @WebResult(targetNamespace = "http://ws.nimio.es")
    @RequestWrapper(localName = "getElementosBloqueados", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.GetElementosBloqueados")
    @ResponseWrapper(localName = "getElementosBloqueadosResponse", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.GetElementosBloqueadosResponse")
    public List<ElementoBloqueado> getElementosBloqueados(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        String param0,
        @WebParam(name = "param1", targetNamespace = "http://ws.nimio.es")
        List<String> param1,
        @WebParam(name = "param2", targetNamespace = "http://ws.nimio.es")
        List<String> param2);

    /**
     * 
     * @param param0
     * @param param1
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "urn:validarPROI")
    @WebResult(targetNamespace = "http://ws.nimio.es")
    @RequestWrapper(localName = "validarPROI", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.ValidarPROI")
    @ResponseWrapper(localName = "validarPROIResponse", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.ValidarPROIResponse")
    public String validarPROI(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        String param0,
        @WebParam(name = "param1", targetNamespace = "http://ws.nimio.es")
        String param1);

    /**
     * 
     * @param param0
     * @param param1
     */
    @WebMethod(action = "urn:certificarCalidadPaquetes")
    @Oneway
    @RequestWrapper(localName = "certificarCalidadPaquetes", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.CertificarCalidadPaquetes")
    public void certificarCalidadPaquetes(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        String param0,
        @WebParam(name = "param1", targetNamespace = "http://ws.nimio.es")
        List<String> param1);

    /**
     * 
     * @param param0
     * @param param1
     * @param param2
     * @param param3
     * @param param4
     * @param param5
     */
    @WebMethod(action = "urn:finalizaPase")
    @Oneway
    @RequestWrapper(localName = "finalizaPase", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.FinalizaPase")
    public void finalizaPase(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        String param0,
        @WebParam(name = "param1", targetNamespace = "http://ws.nimio.es")
        Integer param1,
        @WebParam(name = "param2", targetNamespace = "http://ws.nimio.es")
        String param2,
        @WebParam(name = "param3", targetNamespace = "http://ws.nimio.es")
        String param3,
        @WebParam(name = "param4", targetNamespace = "http://ws.nimio.es")
        String param4,
        @WebParam(name = "param5", targetNamespace = "http://ws.nimio.es")
        String param5);

    /**
     * 
     * @param param0
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "urn:getEtiquetaPase")
    @WebResult(targetNamespace = "http://ws.nimio.es")
    @RequestWrapper(localName = "getEtiquetaPase", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.GetEtiquetaPase")
    @ResponseWrapper(localName = "getEtiquetaPaseResponse", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.GetEtiquetaPaseResponse")
    public String getEtiquetaPase(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        String param0);

    /**
     * 
     * @return
     *     returns es.nimio.nimiogcs.servicios.externos.sw.deployer.GetAgrupacionesResponse
     */
    @WebMethod(action = "urn:getAgrupaciones")
    @WebResult(name = "getAgrupacionesResponse", targetNamespace = "http://ws.nimio.es", partName = "parameters")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public GetAgrupacionesResponse getAgrupaciones();

    /**
     * 
     * @param param0
     * @param param1
     * @param param2
     * @return
     *     returns java.lang.Integer
     */
    @WebMethod(action = "urn:subirElemento")
    @WebResult(targetNamespace = "http://ws.nimio.es")
    @RequestWrapper(localName = "subirElemento", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.SubirElemento")
    @ResponseWrapper(localName = "subirElementoResponse", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.SubirElementoResponse")
    public Integer subirElemento(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        Elemento param0,
        @WebParam(name = "param1", targetNamespace = "http://ws.nimio.es")
        String param1,
        @WebParam(name = "param2", targetNamespace = "http://ws.nimio.es")
        Boolean param2);

    /**
     * 
     * @return
     *     returns es.nimio.nimiogcs.servicios.externos.sw.deployer.RefrescarTipoFicheroResponse
     */
    @WebMethod(action = "urn:refrescarTipoFichero")
    @WebResult(name = "refrescarTipoFicheroResponse", targetNamespace = "http://ws.nimio.es", partName = "parameters")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public RefrescarTipoFicheroResponse refrescarTipoFichero();

    /**
     * 
     * @return
     *     returns es.nimio.nimiogcs.servicios.externos.sw.deployer.GetAplicacionesResponse
     */
    @WebMethod(action = "urn:getAplicaciones")
    @WebResult(name = "getAplicacionesResponse", targetNamespace = "http://ws.nimio.es", partName = "parameters")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public GetAplicacionesResponse getAplicaciones();

    /**
     * 
     * @param param0
     * @return
     *     returns java.util.List<es.nimio.nimiogcs.servicios.externos.sw.deployer.EtiquetaFuncional>
     */
    @WebMethod(action = "urn:getEtiquetasFuncionales")
    @WebResult(targetNamespace = "http://ws.nimio.es")
    @RequestWrapper(localName = "getEtiquetasFuncionales", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.GetEtiquetasFuncionales")
    @ResponseWrapper(localName = "getEtiquetasFuncionalesResponse", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.GetEtiquetasFuncionalesResponse")
    public List<EtiquetaFuncional> getEtiquetasFuncionales(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        String param0);

    /**
     * 
     * @param param0
     * @param param1
     * @param param2
     * @param param3
     * @param param4
     * @param param5
     * @param param6
     */
    @WebMethod(action = "urn:finalizaPaseAsincrono")
    @Oneway
    @RequestWrapper(localName = "finalizaPaseAsincrono", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.FinalizaPaseAsincrono")
    public void finalizaPaseAsincrono(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        String param0,
        @WebParam(name = "param1", targetNamespace = "http://ws.nimio.es")
        Integer param1,
        @WebParam(name = "param2", targetNamespace = "http://ws.nimio.es")
        String param2,
        @WebParam(name = "param3", targetNamespace = "http://ws.nimio.es")
        String param3,
        @WebParam(name = "param4", targetNamespace = "http://ws.nimio.es")
        String param4,
        @WebParam(name = "param5", targetNamespace = "http://ws.nimio.es")
        String param5,
        @WebParam(name = "param6", targetNamespace = "http://ws.nimio.es")
        Boolean param6);

    /**
     * 
     * @param param0
     * @param param1
     * @param param2
     * @return
     *     returns java.lang.Integer
     */
    @WebMethod(action = "urn:actualizaEstadoOK")
    @WebResult(targetNamespace = "http://ws.nimio.es")
    @RequestWrapper(localName = "actualizaEstadoOK", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.ActualizaEstadoOK")
    @ResponseWrapper(localName = "actualizaEstadoOKResponse", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.ActualizaEstadoOKResponse")
    public Integer actualizaEstadoOK(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        String param0,
        @WebParam(name = "param1", targetNamespace = "http://ws.nimio.es")
        String param1,
        @WebParam(name = "param2", targetNamespace = "http://ws.nimio.es")
        String param2);

    /**
     * 
     * @param param0
     * @return
     *     returns java.util.List<es.nimio.nimiogcs.servicios.externos.sw.deployer.Elemento>
     */
    @WebMethod(action = "urn:getElementosInfo")
    @WebResult(targetNamespace = "http://ws.nimio.es")
    @RequestWrapper(localName = "getElementosInfo", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.GetElementosInfo")
    @ResponseWrapper(localName = "getElementosInfoResponse", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.GetElementosInfoResponse")
    public List<Elemento> getElementosInfo(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        List<Elemento> param0);

    /**
     * 
     * @param param0
     * @param param1
     * @param param2
     * @return
     *     returns java.lang.Integer
     */
    @WebMethod(action = "urn:actualizaEstadoKO")
    @WebResult(targetNamespace = "http://ws.nimio.es")
    @RequestWrapper(localName = "actualizaEstadoKO", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.ActualizaEstadoKO")
    @ResponseWrapper(localName = "actualizaEstadoKOResponse", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.ActualizaEstadoKOResponse")
    public Integer actualizaEstadoKO(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        String param0,
        @WebParam(name = "param1", targetNamespace = "http://ws.nimio.es")
        String param1,
        @WebParam(name = "param2", targetNamespace = "http://ws.nimio.es")
        String param2);

    /**
     * 
     * @param param0
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "urn:executeServiceCode")
    @WebResult(targetNamespace = "http://ws.nimio.es")
    @RequestWrapper(localName = "executeServiceCode", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.ExecuteServiceCode")
    @ResponseWrapper(localName = "executeServiceCodeResponse", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.ExecuteServiceCodeResponse")
    public String executeServiceCode(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        String param0);

    /**
     * 
     * @param param0
     * @param param1
     * @param param2
     * @param param3
     * @param param4
     * @param param5
     * @return
     *     returns java.lang.Boolean
     */
    @WebMethod(action = "urn:enviarElementosACola")
    @WebResult(targetNamespace = "http://ws.nimio.es")
    @RequestWrapper(localName = "enviarElementosACola", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.EnviarElementosACola")
    @ResponseWrapper(localName = "enviarElementosAColaResponse", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.EnviarElementosAColaResponse")
    public Boolean enviarElementosACola(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        List<Elemento> param0,
        @WebParam(name = "param1", targetNamespace = "http://ws.nimio.es")
        String param1,
        @WebParam(name = "param2", targetNamespace = "http://ws.nimio.es")
        String param2,
        @WebParam(name = "param3", targetNamespace = "http://ws.nimio.es")
        String param3,
        @WebParam(name = "param4", targetNamespace = "http://ws.nimio.es")
        String param4,
        @WebParam(name = "param5", targetNamespace = "http://ws.nimio.es")
        String param5);

    /**
     * 
     * @param param0
     * @param param1
     * @param param10
     * @param param2
     * @param param3
     * @param param4
     * @param param5
     * @param param6
     * @param param7
     * @param param8
     * @param param9
     */
    @WebMethod(action = "urn:finalizaPaseMultiidioma")
    @Oneway
    @RequestWrapper(localName = "finalizaPaseMultiidioma", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.FinalizaPaseMultiidioma")
    public void finalizaPaseMultiidioma(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        List<String> param0,
        @WebParam(name = "param1", targetNamespace = "http://ws.nimio.es")
        List<String> param1,
        @WebParam(name = "param2", targetNamespace = "http://ws.nimio.es")
        List<String> param2,
        @WebParam(name = "param3", targetNamespace = "http://ws.nimio.es")
        String param3,
        @WebParam(name = "param4", targetNamespace = "http://ws.nimio.es")
        String param4,
        @WebParam(name = "param5", targetNamespace = "http://ws.nimio.es")
        String param5,
        @WebParam(name = "param6", targetNamespace = "http://ws.nimio.es")
        String param6,
        @WebParam(name = "param7", targetNamespace = "http://ws.nimio.es")
        String param7,
        @WebParam(name = "param8", targetNamespace = "http://ws.nimio.es")
        String param8,
        @WebParam(name = "param9", targetNamespace = "http://ws.nimio.es")
        Integer param9,
        @WebParam(name = "param10", targetNamespace = "http://ws.nimio.es")
        String param10);

    /**
     * 
     * @param param0
     * @return
     *     returns java.util.List<es.nimio.nimiogcs.servicios.externos.sw.deployer.TipoElemento>
     */
    @WebMethod(action = "urn:getTipoElementosByExt")
    @WebResult(targetNamespace = "http://ws.nimio.es")
    @RequestWrapper(localName = "getTipoElementosByExt", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.GetTipoElementosByExt")
    @ResponseWrapper(localName = "getTipoElementosByExtResponse", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.GetTipoElementosByExtResponse")
    public List<TipoElemento> getTipoElementosByExt(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        String param0);

    /**
     * 
     * @param param0
     * @return
     *     returns java.util.List<es.nimio.nimiogcs.servicios.externos.sw.deployer.Elemento>
     */
    @WebMethod(action = "urn:getTipoElementoByTipo")
    @WebResult(targetNamespace = "http://ws.nimio.es")
    @RequestWrapper(localName = "getTipoElementoByTipo", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.GetTipoElementoByTipo")
    @ResponseWrapper(localName = "getTipoElementoByTipoResponse", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.GetTipoElementoByTipoResponse")
    public List<Elemento> getTipoElementoByTipo(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        List<Elemento> param0);

    /**
     * 
     * @param param0
     * @param param1
     * @param param2
     * @param param3
     * @param param4
     * @param param5
     * @param param6
     */
    @WebMethod(action = "urn:finalizaPaseCambioTipo")
    @Oneway
    @RequestWrapper(localName = "finalizaPaseCambioTipo", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.FinalizaPaseCambioTipo")
    public void finalizaPaseCambioTipo(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        Integer param0,
        @WebParam(name = "param1", targetNamespace = "http://ws.nimio.es")
        String param1,
        @WebParam(name = "param2", targetNamespace = "http://ws.nimio.es")
        String param2,
        @WebParam(name = "param3", targetNamespace = "http://ws.nimio.es")
        String param3,
        @WebParam(name = "param4", targetNamespace = "http://ws.nimio.es")
        String param4,
        @WebParam(name = "param5", targetNamespace = "http://ws.nimio.es")
        List<String> param5,
        @WebParam(name = "param6", targetNamespace = "http://ws.nimio.es")
        List<String> param6);

    /**
     * 
     * @param param0
     * @return
     *     returns java.util.List<es.nimio.nimiogcs.servicios.externos.sw.deployer.Extension>
     */
    @WebMethod(action = "urn:getExtensiones")
    @WebResult(targetNamespace = "http://ws.nimio.es")
    @RequestWrapper(localName = "getExtensiones", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.GetExtensiones")
    @ResponseWrapper(localName = "getExtensionesResponse", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.GetExtensionesResponse")
    public List<Extension> getExtensiones(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        Integer param0);

    /**
     * 
     * @param param0
     * @return
     *     returns java.util.List<es.nimio.nimiogcs.servicios.externos.sw.deployer.ElementoVersiones>
     */
    @WebMethod(action = "urn:getVersiones")
    @WebResult(targetNamespace = "http://ws.nimio.es")
    @RequestWrapper(localName = "getVersiones", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.GetVersiones")
    @ResponseWrapper(localName = "getVersionesResponse", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.GetVersionesResponse")
    public List<ElementoVersiones> getVersiones(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        List<ElementoVersiones> param0);

    /**
     * 
     * @param param0
     * @return
     *     returns java.util.List<es.nimio.nimiogcs.servicios.externos.sw.deployer.TipoElemento>
     */
    @WebMethod(action = "urn:getTipoElementosGrup")
    @WebResult(targetNamespace = "http://ws.nimio.es")
    @RequestWrapper(localName = "getTipoElementosGrup", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.GetTipoElementosGrup")
    @ResponseWrapper(localName = "getTipoElementosGrupResponse", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.GetTipoElementosGrupResponse")
    public List<TipoElemento> getTipoElementosGrup(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        Integer param0);

    /**
     * 
     * @param param0
     * @param param1
     * @param param2
     */
    @WebMethod(action = "urn:certificarCalidadPase")
    @Oneway
    @RequestWrapper(localName = "certificarCalidadPase", targetNamespace = "http://ws.nimio.es", className = "es.nimio.nimiogcs.servicios.externos.sw.deployer.CertificarCalidadPase")
    public void certificarCalidadPase(
        @WebParam(name = "param0", targetNamespace = "http://ws.nimio.es")
        String param0,
        @WebParam(name = "param1", targetNamespace = "http://ws.nimio.es")
        String param1,
        @WebParam(name = "param2", targetNamespace = "http://ws.nimio.es")
        String param2);

}
