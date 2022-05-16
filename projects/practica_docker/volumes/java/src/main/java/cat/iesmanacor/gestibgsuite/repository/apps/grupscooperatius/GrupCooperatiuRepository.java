package cat.iesmanacor.gestibgsuite.repository.apps.grupscooperatius;

import cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius.GrupCooperatiu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupCooperatiuRepository extends JpaRepository<GrupCooperatiu, Long> {
}
