package cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "im_app_grups_cooperatius_valor_item")
@EqualsAndHashCode(exclude="item")
public @Data class ValorItem {
    @Id
    @Column(name = "idvalor_item")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idvalorItem;

    @Column(name = "valor", nullable = false, length = 255)
    private String valor;

    @Column(name = "pes", nullable = false, length = 255)
    private Integer pes;

    @ManyToOne(optional = true)
    @JsonManagedReference
    private Item item;
}
