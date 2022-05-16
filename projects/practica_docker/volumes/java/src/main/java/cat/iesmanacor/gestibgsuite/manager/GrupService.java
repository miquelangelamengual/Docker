package cat.iesmanacor.gestibgsuite.manager;

import cat.iesmanacor.gestibgsuite.model.gestib.Grup;
import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import cat.iesmanacor.gestibgsuite.repository.gestib.GrupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrupService {
    @Autowired
    private GrupRepository grupRepository;

    public Grup save (Grup g){
        return grupRepository.save(g);
    }

    public Grup save(String identificador, String nom, String codiCurs, String tutor1, String tutor2, String tutor3) {
        Grup g = new Grup();
        g.setGestibNom(nom);
        g.setGestibIdentificador(identificador);
        g.setGestibCurs(codiCurs);
        g.setGestibTutor1(tutor1);
        g.setGestibTutor2(tutor2);
        g.setGestibTutor3(tutor3);
        return grupRepository.save(g);
    }

    public Grup findByGestibIdentificador(String identificador) {
        return grupRepository.findGrupByGestibIdentificador(identificador);
    }

    public List<Grup> findByTutor(Usuari u) {
        return grupRepository.findAllByGestibTutor1OrGestibTutor2OrGestibTutor3(u.getGestibCodi(), u.getGestibCodi(), u.getGestibCodi());
    }

    public List<Grup> findAll(){
        return grupRepository.findAll();
    }

}

