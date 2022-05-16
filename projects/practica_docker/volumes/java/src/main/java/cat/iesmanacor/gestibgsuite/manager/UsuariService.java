package cat.iesmanacor.gestibgsuite.manager;

import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import cat.iesmanacor.gestibgsuite.repository.gestib.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuariService {
    @Autowired
    private UsuariRepository usuariRepository;

    public Usuari save(Usuari usuari) {
        return usuariRepository.save(usuari);
    }


    public Usuari saveGestib(String codi, String nom, String cognom1, String cognom2, String username, String expedient, String grup, String departament, Boolean esProfessor, Boolean esAlumne) {
        Usuari u = new Usuari();
        u.setActiu(true);

        /* Gestib */
        u.setGestibCodi(codi);
        u.setGestibNom(nom);
        u.setGestibCognom1(cognom1);
        u.setGestibCognom2(cognom2);
        u.setGestibUsername(username);
        u.setGestibExpedient(expedient);
        u.setGestibGrup(grup);
        u.setGestibProfessor(esProfessor);
        u.setGestibAlumne(esAlumne);
        u.setGestibDepartament(departament);
        return usuariRepository.save(u);
    }

    public Usuari saveGestib(Usuari u, String codi, String nom, String cognom1, String cognom2, String username, String expedient, String grup, String departament, Boolean esProfessor, Boolean esAlumne) {
        u.setIdusuari(u.getIdusuari());
        u.setActiu(true);

        /* Gestib */
        u.setGestibCodi(codi);
        u.setGestibNom(nom);
        u.setGestibCognom1(cognom1);
        u.setGestibCognom2(cognom2);
        u.setGestibUsername(username);
        u.setGestibExpedient(expedient);
        if (u.getGestibGrup() == null) {
            u.setGestibGrup(grup);
        } else if (!u.getGestibGrup().equals(grup) && u.getGestibGrup2() == null) {
            u.setGestibGrup2(grup);
        } else if (!u.getGestibGrup().equals(grup) && !u.getGestibGrup2().equals(grup) && u.getGestibGrup3() == null) {
            u.setGestibGrup3(grup);
        }
        u.setGestibProfessor(esProfessor);
        u.setGestibAlumne(esAlumne);
        u.setGestibDepartament(departament);

        return usuariRepository.save(u);
    }

    public Usuari saveGSuite(String email, Boolean esAdministrador, String personalID, Boolean suspes, String unitatOrganitzativa, String givenName, String familyName, String fullName, Boolean actiu) {
        Usuari u = new Usuari();

        u.setActiu(actiu);

        /* GSuite */
        u.setGsuiteEmail(email);
        u.setGsuiteAdministrador(esAdministrador);
        u.setGsuitePersonalID(personalID);
        u.setGsuiteSuspes(suspes);
        u.setGsuiteUnitatOrganitzativa(unitatOrganitzativa);
        u.setGsuiteGivenName(givenName);
        u.setGsuiteFamilyName(familyName);
        u.setGsuiteFullName(fullName);

        return usuariRepository.save(u);
    }

    public Usuari findByGestibCodi(String codi) {
        return usuariRepository.findUsuariByGestibCodi(codi);
    }

    public Usuari findByGSuitePersonalID(String codi) {
        return usuariRepository.findUsuariByGsuitePersonalID(codi);
    }

    public Usuari findById(Long id) {
        return usuariRepository.findById(id).orElse(null);
    }

    public Usuari findByEmail(String email) {
        return usuariRepository.findUsuariByGsuiteEmail(email);
    }

    /**
     * Cerquem primer per codi i si no existeix per email
     */
    public Usuari findByGestibCodiOrEmail(String codi, String email) {
        Usuari usuari = null;
        if (codi != null && !codi.isEmpty()) {
            usuari = usuariRepository.findUsuariByGestibCodi(codi);
        }
        if (usuari == null && email != null && !email.isEmpty()) {
            usuari = usuariRepository.findUsuariByGsuiteEmail(email);
        }
        return usuari;
    }

    public List<Usuari> findAll() {
        return usuariRepository.findAllByGsuiteSuspesFalse();
    }

    public List<Usuari> findProfessors() {
        return usuariRepository.findAllByGestibProfessorTrueAndGsuiteSuspesFalse();
    }

    public List<Usuari> findAlumnes(boolean inclouSuspesos) {
        if (inclouSuspesos) {
            return usuariRepository.findAllByGestibAlumneTrue();
        }
        return usuariRepository.findAllByGestibAlumneTrueAndGsuiteSuspesFalse();
    }

    public List<Usuari> findUsuarisSenseCorreu() {
        return usuariRepository.findAllByGsuiteEmailIsNull();
    }

    public List<Usuari> findUsuarisGSuiteEliminats() {
        return usuariRepository.findAllByGsuiteEliminatTrue();
    }

    public List<Usuari> findUsuarisGSuiteSuspesos() {
        return usuariRepository.findAllByGsuiteSuspesTrue();
    }

    public List<Usuari> findUsuarisByGestibGrup(String grupGestib) {
        return usuariRepository.findAllByGestibGrupOrGestibGrup2OrGestibGrup3(grupGestib, grupGestib, grupGestib);
    }

    public List<Usuari> findUsuarisActius() {
        return usuariRepository.findAllByActiu(true);
    }

    public List<Usuari> findUsuarisNoActius() {
        return usuariRepository.findAllByActiu(false);
    }

    public void desactivarUsuaris() {
        List<Usuari> usuaris = usuariRepository.findAll();
        for (Usuari usuari : usuaris) {
            usuari.setActiu(false);
            usuariRepository.save(usuari);
        }
    }

    public void suspendreUsuari(Usuari usuari) {
        usuari.setGsuiteSuspes(true);
        usuari.setActiu(false);
        usuariRepository.save(usuari);
    }
}

