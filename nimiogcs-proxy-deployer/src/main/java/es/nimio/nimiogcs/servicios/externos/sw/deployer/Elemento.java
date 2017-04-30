
package es.nimio.nimiogcs.servicios.externos.sw.deployer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Elemento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Elemento">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoAplicacion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="database" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dirBase" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dirBaseInt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dirBasePre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="elementoDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="etiqueta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idTipoElemento" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="origen" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="path" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pathCVS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="proiMadre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="projectName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="proyecto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipoScript" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="typeDesc" type="{http://description.axis.apache.org/xsd}TypeDesc" minOccurs="0"/>
 *         &lt;element name="usuario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Elemento", namespace = "http://ws.nimio.es/xsd", propOrder = {
    "codigoAplicacion",
    "database",
    "dirBase",
    "dirBaseInt",
    "dirBasePre",
    "elementoDescription",
    "etiqueta",
    "extension",
    "idTipoElemento",
    "nombre",
    "origen",
    "path",
    "pathCVS",
    "proiMadre",
    "projectName",
    "proyecto",
    "tipoScript",
    "typeDesc",
    "usuario",
    "version"
})
public class Elemento {

    @XmlElementRef(name = "codigoAplicacion", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> codigoAplicacion;
    @XmlElementRef(name = "database", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> database;
    @XmlElementRef(name = "dirBase", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> dirBase;
    @XmlElementRef(name = "dirBaseInt", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> dirBaseInt;
    @XmlElementRef(name = "dirBasePre", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> dirBasePre;
    @XmlElementRef(name = "elementoDescription", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> elementoDescription;
    @XmlElementRef(name = "etiqueta", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> etiqueta;
    @XmlElementRef(name = "extension", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> extension;
    protected Integer idTipoElemento;
    @XmlElementRef(name = "nombre", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> nombre;
    @XmlElementRef(name = "origen", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> origen;
    @XmlElementRef(name = "path", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> path;
    @XmlElementRef(name = "pathCVS", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> pathCVS;
    @XmlElementRef(name = "proiMadre", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> proiMadre;
    @XmlElementRef(name = "projectName", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> projectName;
    @XmlElementRef(name = "proyecto", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> proyecto;
    @XmlElementRef(name = "tipoScript", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> tipoScript;
    @XmlElementRef(name = "typeDesc", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<TypeDesc> typeDesc;
    @XmlElementRef(name = "usuario", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> usuario;
    @XmlElementRef(name = "version", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> version;

    /**
     * Gets the value of the codigoAplicacion property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCodigoAplicacion() {
        return codigoAplicacion;
    }

    /**
     * Sets the value of the codigoAplicacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCodigoAplicacion(JAXBElement<String> value) {
        this.codigoAplicacion = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the database property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDatabase() {
        return database;
    }

    /**
     * Sets the value of the database property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDatabase(JAXBElement<String> value) {
        this.database = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the dirBase property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDirBase() {
        return dirBase;
    }

    /**
     * Sets the value of the dirBase property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDirBase(JAXBElement<String> value) {
        this.dirBase = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the dirBaseInt property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDirBaseInt() {
        return dirBaseInt;
    }

    /**
     * Sets the value of the dirBaseInt property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDirBaseInt(JAXBElement<String> value) {
        this.dirBaseInt = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the dirBasePre property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDirBasePre() {
        return dirBasePre;
    }

    /**
     * Sets the value of the dirBasePre property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDirBasePre(JAXBElement<String> value) {
        this.dirBasePre = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the elementoDescription property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getElementoDescription() {
        return elementoDescription;
    }

    /**
     * Sets the value of the elementoDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setElementoDescription(JAXBElement<String> value) {
        this.elementoDescription = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the etiqueta property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEtiqueta() {
        return etiqueta;
    }

    /**
     * Sets the value of the etiqueta property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEtiqueta(JAXBElement<String> value) {
        this.etiqueta = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setExtension(JAXBElement<String> value) {
        this.extension = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the idTipoElemento property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIdTipoElemento() {
        return idTipoElemento;
    }

    /**
     * Sets the value of the idTipoElemento property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIdTipoElemento(Integer value) {
        this.idTipoElemento = value;
    }

    /**
     * Gets the value of the nombre property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNombre() {
        return nombre;
    }

    /**
     * Sets the value of the nombre property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNombre(JAXBElement<String> value) {
        this.nombre = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the origen property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getOrigen() {
        return origen;
    }

    /**
     * Sets the value of the origen property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setOrigen(JAXBElement<String> value) {
        this.origen = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPath(JAXBElement<String> value) {
        this.path = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pathCVS property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPathCVS() {
        return pathCVS;
    }

    /**
     * Sets the value of the pathCVS property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPathCVS(JAXBElement<String> value) {
        this.pathCVS = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the proiMadre property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getProiMadre() {
        return proiMadre;
    }

    /**
     * Sets the value of the proiMadre property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setProiMadre(JAXBElement<String> value) {
        this.proiMadre = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the projectName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getProjectName() {
        return projectName;
    }

    /**
     * Sets the value of the projectName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setProjectName(JAXBElement<String> value) {
        this.projectName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the proyecto property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getProyecto() {
        return proyecto;
    }

    /**
     * Sets the value of the proyecto property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setProyecto(JAXBElement<String> value) {
        this.proyecto = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the tipoScript property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTipoScript() {
        return tipoScript;
    }

    /**
     * Sets the value of the tipoScript property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTipoScript(JAXBElement<String> value) {
        this.tipoScript = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the typeDesc property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TypeDesc }{@code >}
     *     
     */
    public JAXBElement<TypeDesc> getTypeDesc() {
        return typeDesc;
    }

    /**
     * Sets the value of the typeDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TypeDesc }{@code >}
     *     
     */
    public void setTypeDesc(JAXBElement<TypeDesc> value) {
        this.typeDesc = ((JAXBElement<TypeDesc> ) value);
    }

    /**
     * Gets the value of the usuario property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUsuario() {
        return usuario;
    }

    /**
     * Sets the value of the usuario property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUsuario(JAXBElement<String> value) {
        this.usuario = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setVersion(JAXBElement<String> value) {
        this.version = ((JAXBElement<String> ) value);
    }

}
