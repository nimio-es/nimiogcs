
package es.nimio.nimiogcs.servicios.externos.sw.deployer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FieldDesc complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FieldDesc">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="element" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="fieldName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="indexed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="javaType" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="minOccursIs0" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="minOccursZero" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="xmlName" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="xmlType" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FieldDesc", namespace = "http://description.axis.apache.org/xsd", propOrder = {
    "element",
    "fieldName",
    "indexed",
    "javaType",
    "minOccursIs0",
    "minOccursZero",
    "xmlName",
    "xmlType"
})
public class FieldDesc {

    protected Boolean element;
    @XmlElementRef(name = "fieldName", namespace = "http://description.axis.apache.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> fieldName;
    protected Boolean indexed;
    @XmlElementRef(name = "javaType", namespace = "http://description.axis.apache.org/xsd", type = JAXBElement.class)
    protected JAXBElement<Object> javaType;
    protected Boolean minOccursIs0;
    protected Boolean minOccursZero;
    @XmlElementRef(name = "xmlName", namespace = "http://description.axis.apache.org/xsd", type = JAXBElement.class)
    protected JAXBElement<Object> xmlName;
    @XmlElementRef(name = "xmlType", namespace = "http://description.axis.apache.org/xsd", type = JAXBElement.class)
    protected JAXBElement<Object> xmlType;

    /**
     * Gets the value of the element property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isElement() {
        return element;
    }

    /**
     * Sets the value of the element property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setElement(Boolean value) {
        this.element = value;
    }

    /**
     * Gets the value of the fieldName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFieldName() {
        return fieldName;
    }

    /**
     * Sets the value of the fieldName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFieldName(JAXBElement<String> value) {
        this.fieldName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the indexed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIndexed() {
        return indexed;
    }

    /**
     * Sets the value of the indexed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIndexed(Boolean value) {
        this.indexed = value;
    }

    /**
     * Gets the value of the javaType property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public JAXBElement<Object> getJavaType() {
        return javaType;
    }

    /**
     * Sets the value of the javaType property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public void setJavaType(JAXBElement<Object> value) {
        this.javaType = ((JAXBElement<Object> ) value);
    }

    /**
     * Gets the value of the minOccursIs0 property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMinOccursIs0() {
        return minOccursIs0;
    }

    /**
     * Sets the value of the minOccursIs0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMinOccursIs0(Boolean value) {
        this.minOccursIs0 = value;
    }

    /**
     * Gets the value of the minOccursZero property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMinOccursZero() {
        return minOccursZero;
    }

    /**
     * Sets the value of the minOccursZero property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMinOccursZero(Boolean value) {
        this.minOccursZero = value;
    }

    /**
     * Gets the value of the xmlName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public JAXBElement<Object> getXmlName() {
        return xmlName;
    }

    /**
     * Sets the value of the xmlName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public void setXmlName(JAXBElement<Object> value) {
        this.xmlName = ((JAXBElement<Object> ) value);
    }

    /**
     * Gets the value of the xmlType property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public JAXBElement<Object> getXmlType() {
        return xmlType;
    }

    /**
     * Sets the value of the xmlType property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public void setXmlType(JAXBElement<Object> value) {
        this.xmlType = ((JAXBElement<Object> ) value);
    }

}
