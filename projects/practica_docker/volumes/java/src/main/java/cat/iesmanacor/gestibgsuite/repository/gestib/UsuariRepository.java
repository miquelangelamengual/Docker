package cat.iesmanacor.gestibgsuite.repository.gestib;

import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuariRepository extends JpaRepository<Usuari, Long> {
    Usuari findUsuariByGestibCodi(String codi);

    Usuari findUsuariByGsuiteEmail(String email);

    Usuari findUsuariByGestibCodiOrGsuiteEmail(String codi, String email);

    Usuari findUsuariByGsuitePersonalID(String codi);

    List<Usuari> findAllByGestibProfessorTrueAndGsuiteSuspesFalse();

    List<Usuari> findAllByGestibAlumneTrueAndGsuiteSuspesFalse();

    List<Usuari> findAllByGestibAlumneTrue();

    List<Usuari> findAllByGsuiteSuspesFalse();

    List<Usuari> findAllByGsuiteEmailIsNull();

    List<Usuari> findAllByGsuiteSuspesTrue();

    List<Usuari> findAllByGsuiteEliminatTrue();

    List<Usuari> findAllByActiu(Boolean actiu);

    List<Usuari> findAllByGestibGrupOrGestibGrup2OrGestibGrup3(String grupGestib, String grupGestib2, String grupGestib3);
}
