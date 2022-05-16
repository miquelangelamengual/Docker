package cat.iesmanacor.gestibgsuite.model.apps.convalidacions;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "im_app_convalidacions_item")
public @Data class ItemConvalidacio {
    @Id
    @Column(name = "iditem")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iditem;

    @Column(name = "codi", nullable = true, length = 2048)
    private String codi;

    @Column(name = "nom", nullable = false, length = 2048)
    private String nom;

    @ManyToOne(optional = false)
    private CategoriaConvalidacio categoria;

    @OneToMany
    private Set<ItemConvalidacio> composa = new HashSet<>();
}
