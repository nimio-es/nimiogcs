
package es.nimio.nimiogcs.servicios.externos.sw.deployer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TipoElemento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TipoElemento">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="descripcion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dirBase" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dirBaseInt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dirBasePre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="idAgrupacion" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="servidor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="typeDesc" type="{http://description.axis.apache.org/xsd}TypeDesc" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TipoElemento", namespace = "http://model.pai.nimio.es/xsd", propOrder = {
    "descripcion",
    "dirBase",
    "dirBaseInt",
    "dirBasePre",
    "id",
    "idAgrupacion",
    "nombre",
    "servidor",
    "typeDesc"
})
public class TipoElemento {

    @XmlElementRef(name = "descripcion", namespace = "http://model.pai.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> descripcion;
    @XmlElementRef(name = "dirBase", namespace = "http://model.pai.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> dirBase;
    @XmlElementRef(name = "dirBaseInt", namespace = "http://model.pai.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> dirBaseInt;
    @XmlElementRef(name = "dirBasePre", namespace = "http://model.pai.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> dirBasePre;
    protected Integer id;
    protected Integer idAgrupacion;
    @XmlElementRef(name = "nombre", namespace = "http://model.pai.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> nombre;
    @XmlElementRef(name = "servidor", namespace = "http://model.pai.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> servidor;
    @XmlElementRef(name = "typeDesc", namespace = "http://model.pai.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<TypeDesc> typeDesc;

    /**
     * Gets the value of the descripcion property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDescripcion() {
        return descripcion;
    }

    /**
     * Sets the value of the descripcion property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDescripcion(JAXBElement<String> value) {
        this.descripcion = ((JAXBElement<String> ) value);
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
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setId(Integer value) {
        this.id = value;
    }

    /**
     * Gets the value of the idAgrupacion property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIdAgrupacion() {
        return idAgrupacion;
    }

    /**
     * Sets the value of the idAgrupacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIdAgrupacion(Integer value) {
        this.idAgrupacion = value;
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
     * Gets the value of the servidor property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getServidor() {
        return servidor;
    }

    /**
     * Sets the value of the servidor property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setServidor(JAXBElement<String> value) {
        this.servidor = ((JAXBElement<String> ) value);
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

}
