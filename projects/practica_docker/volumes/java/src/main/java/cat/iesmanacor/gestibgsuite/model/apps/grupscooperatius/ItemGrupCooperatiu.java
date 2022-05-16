package cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "im_app_grups_cooperatius_item_grup_cooperatiu")
@IdClass(ItemGrupCooperatiuId.class)
public @Data class ItemGrupCooperatiu {

    @Id
    @ManyToOne
    @JoinColumn(name = "item", insertable = false, updatable = false)
    @JsonBackReference
    private Item item;

    @Id
    @ManyToOne
    @JoinColumn(name = "grup_cooperatiu", insertable = false, updatable = false)
    @JsonBackReference
    private GrupCooperatiu grupCooperatiu;


    @Column(name = "percentatge", nullable = false)
    private Integer percentatge;
}
