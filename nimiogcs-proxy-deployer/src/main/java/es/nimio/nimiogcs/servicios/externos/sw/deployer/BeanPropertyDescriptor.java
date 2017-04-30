
package es.nimio.nimiogcs.servicios.externos.sw.deployer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BeanPropertyDescriptor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BeanPropertyDescriptor">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="actualType" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="array" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="indexed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="indexedOrArray" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="readable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="writeable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BeanPropertyDescriptor", namespace = "http://utils.axis.apache.org/xsd", propOrder = {
    "actualType",
    "array",
    "indexed",
    "indexedOrArray",
    "name",
    "readable",
    "type",
    "writeable"
})
public class BeanPropertyDescriptor {

    @XmlElementRef(name = "actualType", namespace = "http://utils.axis.apache.org/xsd", type = JAXBElement.class)
    protected JAXBElement<Object> actualType;
    protected Boolean array;
    protected Boolean indexed;
    protected Boolean indexedOrArray;
    @XmlElementRef(name = "name", namespace = "http://utils.axis.apache.org/xsd", type = JAXBElement.class)
    protected JAXBElement<String> name;
    protected Boolean readable;
    @XmlElementRef(name = "type", namespace = "http://utils.axis.apache.org/xsd", type = JAXBElement.class)
    protected JAXBElement<Object> type;
    protected Boolean writeable;

    /**
     * Gets the value of the actualType property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public JAXBElement<Object> getActualType() {
        return actualType;
    }

    /**
     * Sets the value of the actualType property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public void setActualType(JAXBElement<Object> value) {
        this.actualType = ((JAXBElement<Object> ) value);
    }

    /**
     * Gets the value of the array property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isArray() {
        return array;
    }

    /**
     * Sets the value of the array property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setArray(Boolean value) {
        this.array = value;
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
     * Gets the value of the indexedOrArray property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIndexedOrArray() {
        return indexedOrArray;
    }

    /**
     * Sets the value of the indexedOrArray property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIndexedOrArray(Boolean value) {
        this.indexedOrArray = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setName(JAXBElement<String> value) {
        this.name = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the readable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadable() {
        return readable;
    }

    /**
     * Sets the value of the readable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadable(Boolean value) {
        this.readable = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public JAXBElement<Object> getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public void setType(JAXBElement<Object> value) {
        this.type = ((JAXBElement<Object> ) value);
    }

    /**
     * Gets the value of the writeable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isWriteable() {
        return writeable;
    }

    /**
     * Sets the value of the writeable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWriteable(Boolean value) {
        this.writeable = value;
    }

}
