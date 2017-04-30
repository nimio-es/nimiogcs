
package es.nimio.nimiogcs.servicios.externos.sw.deployer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ElementoVersiones complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ElementoVersiones">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="path" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="typeDesc" type="{http://description.axis.apache.org/xsd}TypeDesc" minOccurs="0"/>
 *         &lt;element name="versionInt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="versionPre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="versionPro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ElementoVersiones", namespace = "http://ws.nimio.es/xsd", propOrder = {
    "nombre",
    "path",
    "typeDesc",
    "versionInt",
    "versionPre",
    "versionPro"
})
public class ElementoVersiones {

    @XmlElementRef(name = "nombre", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> nombre;
    @XmlElementRef(name = "path", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> path;
    @XmlElementRef(name = "typeDesc", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<TypeDesc> typeDesc;
    @XmlElementRef(name = "versionInt", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> versionInt;
    @XmlElementRef(name = "versionPre", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> versionPre;
    @XmlElementRef(name = "versionPro", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> versionPro;

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
     * Gets the value of the versionInt property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getVersionInt() {
        return versionInt;
    }

    /**
     * Sets the value of the versionInt property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setVersionInt(JAXBElement<String> value) {
        this.versionInt = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the versionPre property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getVersionPre() {
        return versionPre;
    }

    /**
     * Sets the value of the versionPre property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setVersionPre(JAXBElement<String> value) {
        this.versionPre = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the versionPro property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getVersionPro() {
        return versionPro;
    }

    /**
     * Sets the value of the versionPro property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setVersionPro(JAXBElement<String> value) {
        this.versionPro = ((JAXBElement<String> ) value);
    }

}
