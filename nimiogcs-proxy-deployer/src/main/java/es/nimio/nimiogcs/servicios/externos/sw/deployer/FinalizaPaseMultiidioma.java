
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
 *         &lt;element name="param0" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="param1" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="param2" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="param3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="param4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="param5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="param6" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="param7" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="param8" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="param9" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="param10" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "param6",
    "param7",
    "param8",
    "param9",
    "param10"
})
@XmlRootElement(name = "finalizaPaseMultiidioma")
public class FinalizaPaseMultiidioma {

    @XmlElement(nillable = true)
    protected List<String> param0;
    @XmlElement(nillable = true)
    protected List<String> param1;
    @XmlElement(nillable = true)
    protected List<String> param2;
    @XmlElementRef(name = "param3", namespace = "http://ws.pai.nimio.es", type = JAXBElement.class)
    protected JAXBElement<String> param3;
    @XmlElementRef(name = "param4", namespace = "http://ws.pai.nimio.es", type = JAXBElement.class)
    protected JAXBElement<String> param4;
    @XmlElementRef(name = "param5", namespace = "http://ws.pai.nimio.es", type = JAXBElement.class)
    protected JAXBElement<String> param5;
    @XmlElementRef(name = "param6", namespace = "http://ws.pai.nimio.es", type = JAXBElement.class)
    protected JAXBElement<String> param6;
    @XmlElementRef(name = "param7", namespace = "http://ws.pai.nimio.es", type = JAXBElement.class)
    protected JAXBElement<String> param7;
    @XmlElementRef(name = "param8", namespace = "http://ws.pai.nimio.es", type = JAXBElement.class)
    protected JAXBElement<String> param8;
    protected Integer param9;
    @XmlElementRef(name = "param10", namespace = "http://ws.pai.nimio.es", type = JAXBElement.class)
    protected JAXBElement<String> param10;

    /**
     * Gets the value of the param0 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the param0 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParam0().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getParam0() {
        if (param0 == null) {
            param0 = new ArrayList<String>();
        }
        return this.param0;
    }

    /**
     * Gets the value of the param1 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the param1 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParam1().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getParam1() {
        if (param1 == null) {
            param1 = new ArrayList<String>();
        }
        return this.param1;
    }

    /**
     * Gets the value of the param2 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the param2 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParam2().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getParam2() {
        if (param2 == null) {
            param2 = new ArrayList<String>();
        }
        return this.param2;
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
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getParam6() {
        return param6;
    }

    /**
     * Sets the value of the param6 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setParam6(JAXBElement<String> value) {
        this.param6 = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the param7 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getParam7() {
        return param7;
    }

    /**
     * Sets the value of the param7 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setParam7(JAXBElement<String> value) {
        this.param7 = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the param8 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getParam8() {
        return param8;
    }

    /**
     * Sets the value of the param8 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setParam8(JAXBElement<String> value) {
        this.param8 = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the param9 property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getParam9() {
        return param9;
    }

    /**
     * Sets the value of the param9 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setParam9(Integer value) {
        this.param9 = value;
    }

    /**
     * Gets the value of the param10 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getParam10() {
        return param10;
    }

    /**
     * Sets the value of the param10 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setParam10(JAXBElement<String> value) {
        this.param10 = ((JAXBElement<String> ) value);
    }

}
