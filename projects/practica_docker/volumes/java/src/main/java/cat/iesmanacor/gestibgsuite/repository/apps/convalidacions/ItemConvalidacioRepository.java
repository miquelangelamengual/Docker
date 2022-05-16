package cat.iesmanacor.gestibgsuite.repository.apps.convalidacions;

import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.ItemConvalidacio;
import cat.iesmanacor.gestibgsuite.model.gestib.Centre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemConvalidacioRepository extends JpaRepository<ItemConvalidacio, Long> {

}
