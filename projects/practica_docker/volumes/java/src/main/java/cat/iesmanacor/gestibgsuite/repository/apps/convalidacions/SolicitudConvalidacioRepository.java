package cat.iesmanacor.gestibgsuite.repository.apps.convalidacions;

import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.ItemConvalidacio;
import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.SolicitudConvalidacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudConvalidacioRepository extends JpaRepository<SolicitudConvalidacio, Long> {

}
