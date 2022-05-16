package cat.iesmanacor.gestibgsuite.repository.google;

import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import cat.iesmanacor.gestibgsuite.model.google.LlistatGoogle;
import cat.iesmanacor.gestibgsuite.model.google.LlistatGoogleTipus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LlistatGoogleRepository extends JpaRepository<LlistatGoogle, Long> {
    LlistatGoogle findLlistatGoogleByIdentificadorAndPropietari(String identificador, Usuari propietari);

    List<LlistatGoogle> findAllByPropietari(Usuari propietari);

    List<LlistatGoogle> findAllByLlistatGoogleTipusAndPropietari(LlistatGoogleTipus tipus, Usuari propietari);
}
