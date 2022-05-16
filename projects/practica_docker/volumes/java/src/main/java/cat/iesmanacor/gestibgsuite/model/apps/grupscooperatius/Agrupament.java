package cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "im_app_grups_cooperatius_agrupament")
public @Data class Agrupament {
    @Id
    @Column(name = "idagrupament")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idagrupament;

    @OneToMany(mappedBy = "agrupament", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Membre> membres = new HashSet<>();

    @ManyToOne(optional = true)
    @JsonBackReference
    private GrupCooperatiu grupCooperatiu;

}
