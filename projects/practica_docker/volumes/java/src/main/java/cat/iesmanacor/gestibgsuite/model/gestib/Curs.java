package cat.iesmanacor.gestibgsuite.model.gestib;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "im_curs")
public @Data class Curs {
    @Id
    @Column(name = "idcurs")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idcurs;

    @Column(name = "gestib_identificador", unique = true, nullable = false, length = 2048)
    private String gestibIdentificador;

    @Column(name = "gestib_nom", nullable = false, length = 2048)
    private String gestibNom;

    @Column(name = "gsuite_unitat_organitzativa", nullable = true, length = 2048)
    private String gsuiteUnitatOrganitzativa;

}
