
package es.nimio.nimiogcs.servicios.externos.sw.deployer;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="param0" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="param1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="param2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="param3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="param4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="param5" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="param6" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "param0",
    "param1",
    "param2",
    "param3",
    "param4",
    "param5",
    "param6"
})
@XmlRootElement(name = "finalizaPaseCambioTipo")
public class FinalizaPaseCambioTipo {

    protected Integer param0;
    @XmlElementRef(name = "param1", namespace = "http://ws.pai.nimio.es", type = JAXBElement.class)
    protected JAXBElement<String> param1;
    @XmlElementRef(name = "param2", namespace = "http://ws.pai.nimio.es", type = JAXBElement.class)
    protected JAXBElement<String> param2;
    @XmlElementRef(name = "param3", namespace = "http://ws.pai.nimio.es", type = JAXBElement.class)
    protected JAXBElement<String> param3;
    @XmlElementRef(name = "param4", namespace = "http://ws.pai.nimio.es", type = JAXBElement.class)
    protected JAXBElement<String> param4;
    @XmlElement(nillable = true)
    protected List<String> param5;
    @XmlElement(nillable = true)
    protected List<String> param6;

    /**
     * Gets the value of the param0 property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getParam0() {
        return param0;
    }

    /**
     * Sets the value of the param0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setParam0(Integer value) {
        this.param0 = value;
    }

    /**
     * Gets the value of the param1 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getParam1() {
        return param1;
    }

    /**
     * Sets the value of the param1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setParam1(JAXBElement<String> value) {
        this.param1 = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the param2 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getParam2() {
        return param2;
    }

    /**
     * Sets the value of the param2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setParam2(JAXBElement<String> value) {
        this.param2 = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the param3 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getParam3() {
        return param3;
    }

    /**
     * Sets the value of the param3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setParam3(JAXBElement<String> value) {
        this.param3 = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the param4 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getParam4() {
        return param4;
    }

    /**
     * Sets the value of the param4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setParam4(JAXBElement<String> value) {
        this.param4 = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the param5 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the param5 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParam5().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getParam5() {
        if (param5 == null) {
            param5 = new ArrayList<String>();
        }
        return this.param5;
    }

    /**
     * Gets the value of the param6 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the param6 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParam6().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getParam6() {
        if (param6 == null) {
            param6 = new ArrayList<String>();
        }
        return this.param6;
    }

}
