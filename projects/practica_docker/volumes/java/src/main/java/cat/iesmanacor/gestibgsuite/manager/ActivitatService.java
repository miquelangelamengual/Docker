package cat.iesmanacor.gestibgsuite.manager;

import cat.iesmanacor.gestibgsuite.model.gestib.Activitat;
import cat.iesmanacor.gestibgsuite.repository.gestib.ActivitatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivitatService {
    @Autowired
    private ActivitatRepository activitatRepository;

    public Activitat save(String identificador, String nom, String nomCurt) {
        Activitat a = new Activitat();
        a.setGestibIdentificador(identificador);
        a.setGestibNom(nom);
        a.setGestibNomCurt(nomCurt);

        return activitatRepository.save(a);
    }

    public Activitat save(Activitat a) {
        return activitatRepository.save(a);
    }

    public Activitat findByGestibIdentificador(String identificador) {
        return activitatRepository.findActivitatByGestibIdentificador(identificador);
    }
}

