package cat.iesmanacor.gestibgsuite.manager;

import cat.iesmanacor.gestibgsuite.model.gestib.Curs;
import cat.iesmanacor.gestibgsuite.repository.gestib.CursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursService {
    @Autowired
    private CursRepository cursRepository;

    public Curs save(Curs c) {
        return cursRepository.save(c);
    }

    public Curs save(String identificador, String nom) {
        Curs c = new Curs();
        c.setGestibIdentificador(identificador);
        c.setGestibNom(nom);
        return cursRepository.save(c);
    }

    public Curs findByGestibIdentificador(String identificador) {
        return cursRepository.findCursByGestibIdentificador(identificador);
    }

    public List<Curs> findAll(){
        return cursRepository.findAll();
    }

}

