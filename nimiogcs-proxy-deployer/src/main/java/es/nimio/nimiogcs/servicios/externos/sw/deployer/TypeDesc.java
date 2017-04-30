
package es.nimio.nimiogcs.servicios.externos.sw.deployer;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TypeDesc complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TypeDesc">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="anyContentDescriptor" type="{http://utils.axis.apache.org/xsd}BeanPropertyDescriptor" minOccurs="0"/>
 *         &lt;element name="anyDesc" type="{http://utils.axis.apache.org/xsd}BeanPropertyDescriptor" minOccurs="0"/>
 *         &lt;element name="fields" type="{http://description.axis.apache.org/xsd}FieldDesc" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="propertyDescriptorMap" type="{http://util.java/xsd}Map" minOccurs="0"/>
 *         &lt;element name="propertyDescriptors" type="{http://utils.axis.apache.org/xsd}BeanPropertyDescriptor" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "TypeDesc", namespace = "http://description.axis.apache.org/xsd", propOrder = {
    "anyContentDescriptor",
    "anyDesc",
    "fields",
    "propertyDescriptorMap",
    "propertyDescriptors",
    "xmlType"
})
public class TypeDesc {

    @XmlElementRef(name = "anyContentDescriptor", namespace = "http://description.axis.apache.org/xsd", type = JAXBElement.class)
    protected JAXBElement<BeanPropertyDescriptor> anyContentDescriptor;
    @XmlElementRef(name = "anyDesc", namespace = "http://description.axis.apache.org/xsd", type = JAXBElement.class)
    protected JAXBElement<BeanPropertyDescriptor> anyDesc;
    @XmlElement(nillable = true)
    protected List<FieldDesc> fields;
    @XmlElementRef(name = "propertyDescriptorMap", namespace = "http://description.axis.apache.org/xsd", type = JAXBElement.class)
    protected JAXBElement<Map> propertyDescriptorMap;
    @XmlElement(nillable = true)
    protected List<BeanPropertyDescriptor> propertyDescriptors;
    @XmlElementRef(name = "xmlType", namespace = "http://description.axis.apache.org/xsd", type = JAXBElement.class)
    protected JAXBElement<Object> xmlType;

    /**
     * Gets the value of the anyContentDescriptor property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BeanPropertyDescriptor }{@code >}
     *     
     */
    public JAXBElement<BeanPropertyDescriptor> getAnyContentDescriptor() {
        return anyContentDescriptor;
    }

    /**
     * Sets the value of the anyContentDescriptor property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BeanPropertyDescriptor }{@code >}
     *     
     */
    public void setAnyContentDescriptor(JAXBElement<BeanPropertyDescriptor> value) {
        this.anyContentDescriptor = ((JAXBElement<BeanPropertyDescriptor> ) value);
    }

    /**
     * Gets the value of the anyDesc property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BeanPropertyDescriptor }{@code >}
     *     
     */
    public JAXBElement<BeanPropertyDescriptor> getAnyDesc() {
        return anyDesc;
    }

    /**
     * Sets the value of the anyDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BeanPropertyDescriptor }{@code >}
     *     
     */
    public void setAnyDesc(JAXBElement<BeanPropertyDescriptor> value) {
        this.anyDesc = ((JAXBElement<BeanPropertyDescriptor> ) value);
    }

    /**
     * Gets the value of the fields property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fields property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFields().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FieldDesc }
     * 
     * 
     */
    public List<FieldDesc> getFields() {
        if (fields == null) {
            fields = new ArrayList<FieldDesc>();
        }
        return this.fields;
    }

    /**
     * Gets the value of the propertyDescriptorMap property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Map }{@code >}
     *     
     */
    public JAXBElement<Map> getPropertyDescriptorMap() {
        return propertyDescriptorMap;
    }

    /**
     * Sets the value of the propertyDescriptorMap property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Map }{@code >}
     *     
     */
    public void setPropertyDescriptorMap(JAXBElement<Map> value) {
        this.propertyDescriptorMap = ((JAXBElement<Map> ) value);
    }

    /**
     * Gets the value of the propertyDescriptors property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the propertyDescriptors property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPropertyDescriptors().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BeanPropertyDescriptor }
     * 
     * 
     */
    public List<BeanPropertyDescriptor> getPropertyDescriptors() {
        if (propertyDescriptors == null) {
            propertyDescriptors = new ArrayList<BeanPropertyDescriptor>();
        }
        return this.propertyDescriptors;
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
