package cat.iesmanacor.gestibgsuite.model.apps.convalidacions;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "im_app_convalidacions_convalidacio")
public @Data class ConvalidacioConvalidacio {
    @Id
    @Column(name = "idconvalidacio")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idconvalidacio;

    @ManyToMany
    private Set<ItemConvalidacio> origens = new HashSet<>();

    @ManyToMany
    private Set<ItemConvalidacio> convalida = new HashSet<>();
}
