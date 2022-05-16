package cat.iesmanacor.gestibgsuite.model.apps.convalidacions;

import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "im_app_convalidacions_solicitud")
public @Data class SolicitudConvalidacio {
    @Id
    @Column(name = "idsolicitud")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idsolicitud;

    @Column(name = "estat", nullable = true)
    @Enumerated(EnumType.STRING)
    private SolcititudEstatConvalidacio estat;

    @Column(name = "observacions", nullable = true)
    @Type(type = "text")
    private String observacions;

    @ManyToMany
    private Set<ItemConvalidacio> estudisOrigen = new HashSet<>();

    @Column(name = "estudisorigenmanual", nullable = true)
    @Type(type = "text")
    private String estudisOrigenManual;

    @Column(name = "estudisorigenobservacions", nullable = true)
    @Type(type = "text")
    private String estudisOrigenObservacions;

    @ManyToOne(optional = false)
    private ItemConvalidacio estudisEnCurs;

    @Column(name = "estudisencursnmanual", nullable = true)
    @Type(type = "text")
    private String estudisEnCursManual;

    @Column(name = "estudisencursobservacions", nullable = true)
    @Type(type = "text")
    private String estudisEnCursObservacions;

    @ManyToOne(optional = false)
    private Usuari alumne;
}
