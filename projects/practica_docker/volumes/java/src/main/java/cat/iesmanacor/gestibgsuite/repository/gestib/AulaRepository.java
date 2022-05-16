package cat.iesmanacor.gestibgsuite.repository.gestib;

import cat.iesmanacor.gestibgsuite.model.gestib.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {

}
