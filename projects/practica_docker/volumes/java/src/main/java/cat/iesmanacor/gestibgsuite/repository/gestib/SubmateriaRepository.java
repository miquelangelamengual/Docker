package cat.iesmanacor.gestibgsuite.repository.gestib;

import cat.iesmanacor.gestibgsuite.model.gestib.Submateria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmateriaRepository extends JpaRepository<Submateria, Long> {
    Submateria findSubmateriaByGestibIdentificador(String identificador);
}
