package cat.iesmanacor.gestibgsuite.model.google;

import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "im_grup_correu")
public @Data class GrupCorreu {
    @Id
    @Column(name = "idgrup")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idgrup;

    @Column(name = "tipus", nullable = false)
    @Enumerated(EnumType.STRING)
    private GrupCorreuTipus grupCorreuTipus;

    /* GESTIB */
    @Column(name = "gestib_grup", nullable = true, length = 255)
    private String gestibGrup;

    /* GSUITE */
    @Column(name = "gsuite_nom", nullable = true, length = 255)
    private String gsuiteNom;

    @Column(name = "gsuite_email", nullable = true, length = 255)
    private String gsuiteEmail;

    @Column(name = "gsuite_descripcio", nullable = true)
    @Type(type = "text")
    private String gsuiteDescripcio;


    /*@OneToMany(mappedBy="grupCorreu")
    @JsonManagedReference
    private Set<Usuari> usuaris = new HashSet<>();*/

    @ManyToMany
    private Set<Usuari> usuaris = new HashSet<>();

    //Un grup de correu pot tenir com a membres altres grups de correu
    @ManyToMany
    private Set<GrupCorreu> grupCorreus = new HashSet<>();
}
