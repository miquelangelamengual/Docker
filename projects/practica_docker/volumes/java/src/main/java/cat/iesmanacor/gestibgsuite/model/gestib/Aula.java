package cat.iesmanacor.gestibgsuite.model.gestib;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "im_aula")
public @Data class Aula {
    @Id
    @Column(name = "idaula")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idaula;

    /* GESTIB */
    @Column(name = "gestib_identificador", unique = true, nullable = false, length = 2048)
    private String gestibIdentificador;

    @Column(name = "gestib_nom", nullable = false, length = 2048)
    private String gestibNom;


    //@OneToMany(mappedBy="centre")
    //@JsonManagedReference
    //private Set<Modul> moduls = new HashSet<>();

}
