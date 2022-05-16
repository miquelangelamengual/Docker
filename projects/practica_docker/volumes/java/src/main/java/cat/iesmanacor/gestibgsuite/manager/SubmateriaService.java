package cat.iesmanacor.gestibgsuite.manager;

import cat.iesmanacor.gestibgsuite.model.gestib.Submateria;
import cat.iesmanacor.gestibgsuite.repository.gestib.SubmateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubmateriaService {
    @Autowired
    private SubmateriaRepository submateriaRepository;

    public Submateria save(String identificador, String nom, String nomCurt, String codiCurs) {
        Submateria s = new Submateria();
        s.setGestibIdentificador(identificador);
        s.setGestibNom(nom);
        s.setGestibNomCurt(nomCurt);
        s.setGestibCurs(codiCurs);

        return submateriaRepository.save(s);
    }

    public Submateria findByGestibIdentificador(String identificador) {
        return submateriaRepository.findSubmateriaByGestibIdentificador(identificador);
    }

}

