
package es.nimio.nimiogcs.servicios.externos.sw.deployer;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="param0" type="{http://model.pai.nimio.es/xsd}Elemento" maxOccurs="unbounded" minOccurs="0"/>
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
    "param0"
})
@XmlRootElement(name = "getElementosInfo")
public class GetElementosInfo {

    @XmlElement(nillable = true)
    protected List<Elemento> param0;

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
     * {@link Elemento }
     * 
     * 
     */
    public List<Elemento> getParam0() {
        if (param0 == null) {
            param0 = new ArrayList<Elemento>();
        }
        return this.param0;
    }

}
