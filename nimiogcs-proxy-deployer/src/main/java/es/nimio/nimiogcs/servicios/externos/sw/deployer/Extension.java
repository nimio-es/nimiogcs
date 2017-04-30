
package es.nimio.nimiogcs.servicios.externos.sw.deployer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Extension complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Extension">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idTipoElemento" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
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
@XmlType(name = "Extension", namespace = "http://model.pai.nimio.es/xsd", propOrder = {
    "extension",
    "idTipoElemento",
    "typeDesc"
})
public class Extension {

    @XmlElementRef(name = "extension", namespace = "http://model.pai.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> extension;
    protected Integer idTipoElemento;
    @XmlElementRef(name = "typeDesc", namespace = "http://model.pai.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<TypeDesc> typeDesc;

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
