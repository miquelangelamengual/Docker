package cat.iesmanacor.gestibgsuite.model.apps.convalidacions;

import cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius.Membre;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "im_app_convalidacions_categoria")
public @Data class CategoriaConvalidacio {
    @Id
    @Column(name = "idcategoria")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idcategoria;

    @Column(name = "nom", nullable = true, length = 2048)
    private String nom;
}
