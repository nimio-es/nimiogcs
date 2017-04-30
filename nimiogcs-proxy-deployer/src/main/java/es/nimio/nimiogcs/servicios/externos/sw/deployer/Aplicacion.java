
package es.nimio.nimiogcs.servicios.externos.sw.deployer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Aplicacion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Aplicacion">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="appCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="appDescripcion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
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
@XmlType(name = "Aplicacion", namespace = "http://ws.nimio.es/xsd", propOrder = {
    "appCode",
    "appDescripcion",
    "id",
    "typeDesc"
})
public class Aplicacion {

    @XmlElementRef(name = "appCode", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> appCode;
    @XmlElementRef(name = "appDescripcion", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> appDescripcion;
    protected Integer id;
    @XmlElementRef(name = "typeDesc", namespace = "http://ws.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<TypeDesc> typeDesc;

    /**
     * Gets the value of the appCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAppCode() {
        return appCode;
    }

    /**
     * Sets the value of the appCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAppCode(JAXBElement<String> value) {
        this.appCode = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the appDescripcion property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAppDescripcion() {
        return appDescripcion;
    }

    /**
     * Sets the value of the appDescripcion property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAppDescripcion(JAXBElement<String> value) {
        this.appDescripcion = ((JAXBElement<String> ) value);
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
