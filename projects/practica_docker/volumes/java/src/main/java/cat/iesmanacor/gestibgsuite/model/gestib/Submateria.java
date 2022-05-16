package cat.iesmanacor.gestibgsuite.model.gestib;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "im_submateria")
public @Data class Submateria {
    @Id
    @Column(name = "idsubmateria")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idsubmateria;

    /* GESTIB */
    @Column(name = "gestib_identificador", unique = true, nullable = false, length = 2048)
    private String gestibIdentificador;

    @Column(name = "gestib_nom", nullable = false, length = 2048)
    private String gestibNom;

    @Column(name = "gestib_nom_curt", nullable = false, length = 2048)
    private String gestibNomCurt;

    @Column(name = "gestib_curs", nullable = false, length = 2048)
    private String gestibCurs;

    //@OneToMany(mappedBy="centre")
    //@JsonManagedReference
    //private Set<Modul> moduls = new HashSet<>();

}
