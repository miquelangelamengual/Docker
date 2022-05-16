package cat.iesmanacor.gestibgsuite.repository.google;

import cat.iesmanacor.gestibgsuite.model.google.GrupCorreu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupCorreuRepository extends JpaRepository<GrupCorreu, Long> {
    GrupCorreu findGrupCorreuByGsuiteEmail(String email);

    List<GrupCorreu> findAllByGestibGrup(String codiGrupGestib);
}
