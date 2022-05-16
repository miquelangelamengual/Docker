package cat.iesmanacor.gestibgsuite.repository.apps.grupscooperatius;

import cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius.Membre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembreRepository extends JpaRepository<Membre, Long> {

}
