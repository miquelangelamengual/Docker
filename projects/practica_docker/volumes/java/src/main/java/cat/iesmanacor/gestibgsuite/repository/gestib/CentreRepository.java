package cat.iesmanacor.gestibgsuite.repository.gestib;

import cat.iesmanacor.gestibgsuite.model.gestib.Centre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CentreRepository extends JpaRepository<Centre, Long> {
    Centre findCentreByIdentificador(String identificador);
}
