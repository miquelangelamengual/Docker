package cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius;

import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "im_app_grups_cooperatius_item")
@EqualsAndHashCode(exclude={"usuari","valorItems"})
public @Data class Item {
    @Id
    @Column(name = "iditem")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iditem;

    @Column(name = "nom", unique = true, nullable = false, length = 255)
    private String nom;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<ItemGrupCooperatiu> itemsGrupsCooperatius = new HashSet<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private Set<ValorItem> valorItems = new HashSet<>();


    @ManyToOne(optional = true)
    @JsonBackReference(value="item-usuari")
    private Usuari usuari;

}
