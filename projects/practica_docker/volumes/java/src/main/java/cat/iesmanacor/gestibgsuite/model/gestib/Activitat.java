package cat.iesmanacor.gestibgsuite.model.gestib;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "im_activitat")
public @Data class Activitat {
    @Id
    @Column(name = "idactivitat")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idactivitat;

    /* GESTIB */
    @Column(name = "gestib_identificador", unique = true, nullable = false, length = 2048)
    private String gestibIdentificador;

    @Column(name = "gestib_nom", nullable = false, length = 2048)
    private String gestibNom;

    @Column(name = "gestib_nom_curt", nullable = false, length = 2048)
    private String gestibNomCurt;


    //@OneToMany(mappedBy="centre")
    //@JsonManagedReference
    //private Set<Modul> moduls = new HashSet<>();

}
