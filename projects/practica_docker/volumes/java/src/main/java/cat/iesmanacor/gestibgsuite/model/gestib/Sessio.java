package cat.iesmanacor.gestibgsuite.model.gestib;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "im_sessio")
public @Data class Sessio {
    @Id
    @Column(name = "idsessio")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idsessio;

    /* GESTIB */
    @Column(name = "gestib_professor", nullable = true, length = 255)
    private String gestibProfessor;

    @Column(name = "gestib_alumne", nullable = true, length = 255)
    private String gestibAlumne;

    @Column(name = "gestib_curs", nullable = false, length = 255)
    private String gestibCurs;

    @Column(name = "gestib_grup", nullable = false, length = 255)
    private String gestibGrup;

    @Column(name = "gestib_dia", nullable = false, length = 255)
    private String gestibDia;

    @Column(name = "gestib_hora", nullable = false, length = 255)
    private String gestibHora;

    @Column(name = "gestib_durada", nullable = false, length = 255)
    private String gestibDurada;

    @Column(name = "gestib_aula", nullable = true, length = 255)
    private String gestibAula;

    @Column(name = "gestib_submateria", nullable = true, length = 255)
    private String gestibSubmateria;

    @Column(name = "gestib_activitat", nullable = true, length = 255)
    private String gestibActivitat;

    @Column(name = "gestib_placa", nullable = true, length = 255)
    private String gestibPlaca;


    //@OneToMany(mappedBy="centre")
    //@JsonManagedReference
    //private Set<Modul> moduls = new HashSet<>();

}
