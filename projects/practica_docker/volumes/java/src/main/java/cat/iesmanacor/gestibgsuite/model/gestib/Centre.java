package cat.iesmanacor.gestibgsuite.model.gestib;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "im_centre")
//@EqualsAndHashCode(exclude="programacio")
public @Data class Centre {
    @Id
    @Column(name = "idcentre")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idcentre;

    @Column(name = "identificador", unique = true, nullable = false)
    private String identificador;

    @Column(name = "any_academic", nullable = false, length = 32)
    private String anyAcademic;

    @Column(name = "nom", nullable = false, length = 2048)
    private String nom;

    @Column(name = "sincronitzar", nullable = false)
    private Boolean sincronitzar;

    @Column(name = "data_sincronitzacio", nullable = true)
    private Date dataSincronitzacio;


    /* TODO: falta posar un altre flag per saber quan s'esta sincronitzant i no deixar entrar a la gent */

    //@OneToMany(mappedBy="centre")
    //@JsonManagedReference
    //private Set<Modul> moduls = new HashSet<>();

}
