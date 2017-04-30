
package es.nimio.nimiogcs.servicios.externos.sw.deployer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="param0" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="param1" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="param2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="param3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="param4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="param5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="param6" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
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
@XmlRootElement(name = "finalizaPaseAsincrono")
public class FinalizaPaseAsincrono {

    @XmlElementRef(name = "param0", namespace = "http://ws.pai.nimio.es", type = JAXBElement.class)
    protected JAXBElement<String> param0;
    protected Integer param1;
    @XmlElementRef(name = "param2", namespace = "http://ws.pai.nimio.es", type = JAXBElement.class)
    protected JAXBElement<String> param2;
    @XmlElementRef(name = "param3", namespace = "http://ws.pai.nimio.es", type = JAXBElement.class)
    protected JAXBElement<String> param3;
    @XmlElementRef(name = "param4", namespace = "http://ws.pai.nimio.es", type = JAXBElement.class)
    protected JAXBElement<String> param4;
    @XmlElementRef(name = "param5", namespace = "http://ws.pai.nimio.es", type = JAXBElement.class)
    protected JAXBElement<String> param5;
    protected Boolean param6;

    /**
     * Gets the value of the param0 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getParam0() {
        return param0;
    }

    /**
     * Sets the value of the param0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setParam0(JAXBElement<String> value) {
        this.param0 = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the param1 property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getParam1() {
        return param1;
    }

    /**
     * Sets the value of the param1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setParam1(Integer value) {
        this.param1 = value;
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
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getParam5() {
        return param5;
    }

    /**
     * Sets the value of the param5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setParam5(JAXBElement<String> value) {
        this.param5 = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the param6 property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isParam6() {
        return param6;
    }

    /**
     * Sets the value of the param6 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setParam6(Boolean value) {
        this.param6 = value;
    }

}
