package cat.iesmanacor.gestibgsuite.manager;

import cat.iesmanacor.gestibgsuite.model.gestib.Departament;
import cat.iesmanacor.gestibgsuite.repository.gestib.DepartamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepartamentService {
    @Autowired
    private DepartamentRepository departamentRepository;

    public Departament save(String identificador, String nom) {
        Departament d = new Departament();
        d.setGestibIdentificador(identificador);
        d.setGestibNom(nom);

        return departamentRepository.save(d);
    }

    public Departament save(Departament d) {
        return departamentRepository.save(d);
    }

    public Departament findByGestibIdentificador(String identificador) {
        return departamentRepository.findDepartamentByGestibIdentificador(identificador);
    }

    public Departament findByEmail(String email) {
        return departamentRepository.findDepartamentByGsuiteEmail(email);
    }


}

