package cat.iesmanacor.gestibgsuite.repository.apps.convalidacions;

import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.CategoriaConvalidacio;
import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.ConvalidacioConvalidacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConvalidacioConvalidacioRepository extends JpaRepository<ConvalidacioConvalidacio, Long> {
}
