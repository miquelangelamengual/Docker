package cat.iesmanacor.gestibgsuite.model.gestib;

import cat.iesmanacor.gestibgsuite.model.Rol;
import cat.iesmanacor.gestibgsuite.model.google.Dispositiu;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "im_usuari")
public @Data class Usuari implements Cloneable {
    @Id
    @Column(name = "idusuari")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idusuari;

    @Column(name = "actiu", nullable = false)
    private Boolean actiu;

    //@UniqueConstraint -> per crear una clau única (primària) per a cada fila i no duplicar files
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Rol.class, fetch = FetchType.EAGER)
    @CollectionTable(name="im_usuari_rols",uniqueConstraints = @UniqueConstraint(columnNames={"rols","usuari_idusuari"}))
    private Set<Rol> rols;


    /* GSUITE */
    @Column(name = "gsuite_email", unique = true, nullable = true)
    private String gsuiteEmail;

    @Column(name = "gsuite_administrador", nullable = true)
    private Boolean gsuiteAdministrador;

    @Column(name = "gsuite_personalid", nullable = true)
    private String gsuitePersonalID;

    @Column(name = "gsuite_suspes", nullable = true)
    private Boolean gsuiteSuspes;

    @Column(name = "gsuite_eliminat", nullable = true)
    private Boolean gsuiteEliminat;

    @Column(name = "gsuite_unitat_organitzativa", nullable = true, length = 2048)
    private String gsuiteUnitatOrganitzativa;

    @Column(name = "gsuite_given_name", nullable = true)
    private String gsuiteGivenName;

    @Column(name = "gsuite_family_name", nullable = true)
    private String gsuiteFamilyName;

    @Column(name = "gsuite_full_name", nullable = true)
    private String gsuiteFullName;


    /* GESTIB */
    @Column(name = "gestib_codi", nullable = true, length = 255)
    private String gestibCodi;

    @Column(name = "gestib_nom", nullable = true, length = 255)
    private String gestibNom;

    @Column(name = "gestib_cognom1", nullable = true, length = 255)
    private String gestibCognom1;

    @Column(name = "gestib_cognom2", nullable = true, length = 255)
    private String gestibCognom2;

    @Column(name = "gestib_username", nullable = true, length = 255)
    private String gestibUsername;

    @Column(name = "gestib_expedient", nullable = true, length = 255)
    private String gestibExpedient;

    @Column(name = "gestib_grup", nullable = true)
    private String gestibGrup;

    @Column(name = "gestib_grup2", nullable = true)
    private String gestibGrup2;

    @Column(name = "gestib_grup3", nullable = true)
    private String gestibGrup3;

    @Column(name = "gestib_departament", nullable = true)
    private String gestibDepartament;

    @Column(name = "gestib_professor", nullable = true)
    private Boolean gestibProfessor;

    @Column(name = "gestib_alumne", nullable = true)
    private Boolean gestibAlumne;

    @Transient
    private List<Dispositiu> dispositius = new ArrayList<>();

    @Override
    public Usuari clone() throws CloneNotSupportedException {
        return (Usuari) super.clone();
    }
}
