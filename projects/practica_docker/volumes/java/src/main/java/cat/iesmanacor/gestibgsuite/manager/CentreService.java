package cat.iesmanacor.gestibgsuite.manager;

import cat.iesmanacor.gestibgsuite.model.gestib.Centre;
import cat.iesmanacor.gestibgsuite.repository.gestib.CentreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CentreService {
    @Autowired
    private CentreRepository centreRepository;

    public Centre save(Centre c) {
        return centreRepository.save(c);
    }

    public Centre save(String identificador, String nom) {
        Centre c = new Centre();
        c.setIdentificador(identificador);
        c.setNom(nom);
        return centreRepository.save(c);
    }

    public Centre findByIdentificador(String identificador) {
        return centreRepository.findCentreByIdentificador(identificador);
    }

    public List<Centre> findAll() {
        return centreRepository.findAll();
    }

}

