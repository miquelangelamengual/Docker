package cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius;

import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "im_app_grups_cooperatius_grup_cooperatiu")
public @Data class GrupCooperatiu {
    @Id
    @Column(name = "idgrup_cooperatiu")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idgrupCooperatiu;

    @Column(name = "nom", unique = true, nullable = false, length = 2048)
    private String nom;

    @OneToMany(mappedBy = "grupCooperatiu", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<ItemGrupCooperatiu> itemsGrupsCooperatius = new HashSet<>();

    @OneToMany(mappedBy = "grupCooperatiu", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Agrupament> agrupaments = new HashSet<>();

    @ManyToOne(optional = true)
    @JsonBackReference
    private Usuari usuari;

}
