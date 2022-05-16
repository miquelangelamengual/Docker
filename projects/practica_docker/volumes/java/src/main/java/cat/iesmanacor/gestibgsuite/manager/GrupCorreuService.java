package cat.iesmanacor.gestibgsuite.manager;

import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import cat.iesmanacor.gestibgsuite.model.google.GrupCorreu;
import cat.iesmanacor.gestibgsuite.model.google.GrupCorreuTipus;
import cat.iesmanacor.gestibgsuite.repository.google.GrupCorreuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GrupCorreuService {
    @Autowired
    private GrupCorreuRepository grupCorreuRepository;

    public GrupCorreu save(GrupCorreu gc) {
        return grupCorreuRepository.save(gc);
    }

    public GrupCorreu save(String gestibGrup, String nom, String email, String descripcio, GrupCorreuTipus grupCorreuTipus) {
        GrupCorreu gc = new GrupCorreu();
        gc.setGestibGrup(gestibGrup);
        gc.setGsuiteNom(nom);
        gc.setGsuiteEmail(email);
        gc.setGsuiteDescripcio(descripcio);
        gc.setGrupCorreuTipus(grupCorreuTipus);

        return grupCorreuRepository.save(gc);
    }

    public List<GrupCorreu> findAll() {
        return grupCorreuRepository.findAll();
    }

    public GrupCorreu findById(Long id) {
        return grupCorreuRepository.findById(id).get();
        //return grupCorreuRepository.getById(id);
    }

    public GrupCorreu findByEmail(String email) {
        return grupCorreuRepository.findGrupCorreuByGsuiteEmail(email);
    }

    public List<GrupCorreu> findByCodiGrupGestib(String codiGrupGestib) {
        return grupCorreuRepository.findAllByGestibGrup(codiGrupGestib);
    }

    public List<GrupCorreu> findByUsuari(Usuari usuari) {
        return grupCorreuRepository.findAll().stream().filter(gc ->
                        gc.getUsuaris().stream().filter(u -> u.getIdusuari().equals(usuari.getIdusuari())).collect(Collectors.toList()).size() > 0)
                .collect(Collectors.toList());
    }

    public void insertUsuari(GrupCorreu grupCorreu, Usuari usuari) {
        if (!grupCorreuRepository.getById(grupCorreu.getIdgrup()).getUsuaris().contains(usuari)) {
            grupCorreuRepository.getById(grupCorreu.getIdgrup()).getUsuaris().add(usuari);
        }
    }

    public void esborrarUsuari(GrupCorreu grupCorreu, Usuari usuari) {
        if (grupCorreuRepository.getById(grupCorreu.getIdgrup()).getUsuaris().contains(usuari)) {
            grupCorreuRepository.getById(grupCorreu.getIdgrup()).getUsuaris().remove(usuari);
        }
    }

    public void insertGrup(GrupCorreu grupCorreu, GrupCorreu membreGrupCorreu) {
        if (!grupCorreuRepository.getById(grupCorreu.getIdgrup()).getGrupCorreus().contains(membreGrupCorreu)) {
            grupCorreuRepository.getById(grupCorreu.getIdgrup()).getGrupCorreus().add(membreGrupCorreu);
        }
    }

    public void esborrarUsuarisGrupCorreu(GrupCorreu grupCorreu) {
        Set<Usuari> usuaris = new HashSet<>(grupCorreuRepository.getById(grupCorreu.getIdgrup()).getUsuaris());
        for (Usuari usuari : usuaris) {
            grupCorreuRepository.getById(grupCorreu.getIdgrup()).getUsuaris().remove(usuari);
        }
    }

    public void esborrarGrupsCorreuGrupCorreu(GrupCorreu grupCorreu) {
        Set<GrupCorreu> grupsCorreu = new HashSet<>(grupCorreuRepository.getById(grupCorreu.getIdgrup()).getGrupCorreus());
        for (GrupCorreu grupsCorreusMembers : grupsCorreu) {
            grupCorreuRepository.getById(grupCorreu.getIdgrup()).getGrupCorreus().remove(grupsCorreusMembers);
        }
    }

    public void esborrarGrup(GrupCorreu grupCorreu){
        grupCorreuRepository.delete(grupCorreu);
    }
}

