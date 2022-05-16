package cat.iesmanacor.gestibgsuite.repository.gestib;

import cat.iesmanacor.gestibgsuite.model.gestib.Grup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupRepository extends JpaRepository<Grup, Long> {
    Grup findGrupByGestibIdentificador(String identificador);

    List<Grup> findAllByGestibTutor1OrGestibTutor2OrGestibTutor3(String tutor1, String tutor2, String tutor3);

}
