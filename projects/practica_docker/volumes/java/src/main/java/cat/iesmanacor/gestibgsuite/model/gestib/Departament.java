package cat.iesmanacor.gestibgsuite.model.gestib;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "im_departament")
public @Data class Departament {
    @Id
    @Column(name = "iddepartament")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iddepartament;

    /* Gestib */
    @Column(name = "gestib_identificador", unique = true, nullable = false, length = 2048)
    private String gestibIdentificador;

    @Column(name = "gestib_nom", nullable = false, length = 2048)
    private String gestibNom;

    /* GSUITE */
    @Column(name = "gsuite_email", nullable = true, length = 1024)
    private String gsuiteEmail;

    //@OneToMany(mappedBy="centre")
    //@JsonManagedReference
    //private Set<Modul> moduls = new HashSet<>();


    @ManyToOne
    @JoinColumn(
            name = "user_iduser",
            nullable = true)
    //@JsonBackReference
    private Usuari capDepartament;


}
