package cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "im_app_grups_cooperatius_valor_item_membre")
//Alerta amb posar Hashcode de Lombok perquè si posem valorItem i Member el HashSet detecta que són distints si no té id!
public @Data class ValorItemMembre {
    @Id
    @Column(name = "idvalor_item_membre")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idvalorItemMembre;

    @ManyToOne(optional = true)
    @JsonManagedReference
    private ValorItem valorItem;

    @ManyToOne(optional = true)
    @JsonBackReference
    private Membre membre;
}
