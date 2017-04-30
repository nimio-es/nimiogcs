package es.nimio.nimiogcs.jpa.entidades.sistema.entornos;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue(value = "ESRV")
public class EntornoConServidores extends DestinoPublicacion {

}
