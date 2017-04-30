package es.nimio.nimiogcs.jpa.entidades.operaciones;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

// Es idéntica a una operación, cambiando únicamente el tipo.
@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue("ASINCRONO")
public class ProcesoAsincrono extends Operacion {

}
