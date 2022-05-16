package cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "im_app_grups_cooperatius_membre")
@EqualsAndHashCode(exclude={"agrupament","valorsItemMembre"})
public @Data class Membre {
    @Id
    @Column(name = "idmembre")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idmembre;

    @Column(name = "nom", unique = true, nullable = false, length = 255)
    private String nom;

    @ManyToOne(optional = true)
    @JsonBackReference
    private Agrupament agrupament;

    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<ValorItemMembre> valorsItemMembre = new HashSet<>();

}
