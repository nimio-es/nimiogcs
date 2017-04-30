
package es.nimio.nimiogcs.servicios.externos.sw.deployer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EtiquetaFuncional complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EtiquetaFuncional">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="descripcion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idEtiqueta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "EtiquetaFuncional", namespace = "http://model.pai.nimio.es/xsd", propOrder = {
    "descripcion",
    "idEtiqueta",
    "typeDesc"
})
public class EtiquetaFuncional {

    @XmlElementRef(name = "descripcion", namespace = "http://model.pai.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> descripcion;
    @XmlElementRef(name = "idEtiqueta", namespace = "http://model.pai.nimio.es/xsd", type = JAXBElement.class)
    protected JAXBElement<String> idEtiqueta;
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
     * Gets the value of the idEtiqueta property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getIdEtiqueta() {
        return idEtiqueta;
    }

    /**
     * Sets the value of the idEtiqueta property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setIdEtiqueta(JAXBElement<String> value) {
        this.idEtiqueta = ((JAXBElement<String> ) value);
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
