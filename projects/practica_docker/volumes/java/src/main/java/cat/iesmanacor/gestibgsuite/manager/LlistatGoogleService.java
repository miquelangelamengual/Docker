package cat.iesmanacor.gestibgsuite.manager;

import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import cat.iesmanacor.gestibgsuite.model.google.LlistatGoogle;
import cat.iesmanacor.gestibgsuite.model.google.LlistatGoogleTipus;
import cat.iesmanacor.gestibgsuite.repository.google.LlistatGoogleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LlistatGoogleService {
    @Autowired
    private LlistatGoogleRepository llistatGoogleRepository;

    public LlistatGoogle save(LlistatGoogle c) {
        return llistatGoogleRepository.save(c);
    }

    public LlistatGoogle save(String identificador, String nom, String url, LlistatGoogleTipus llistatGoogleTipus, Usuari propietari) {
        Date ara = new Date();
        LlistatGoogle l = new LlistatGoogle();
        l.setIdentificador(identificador);
        l.setDataCreacio(ara);
        l.setNom(nom);
        l.setPropietari(propietari);
        l.setUrl(url);
        l.setLlistatGoogleTipus(llistatGoogleTipus);

        return llistatGoogleRepository.save(l);
    }

    public LlistatGoogle findByIdentificadorAndPropietari(String identificador, Usuari propietari) {
        return llistatGoogleRepository.findLlistatGoogleByIdentificadorAndPropietari(identificador, propietari);
    }

    public List<LlistatGoogle> findAllByPropietari(Usuari propietari) {
        return llistatGoogleRepository.findAllByPropietari(propietari);
    }

    public List<LlistatGoogle> findAllByTipusAndPropietar(LlistatGoogleTipus llistatGoogleTipus, Usuari propietari) {
        return llistatGoogleRepository.findAllByLlistatGoogleTipusAndPropietari(llistatGoogleTipus, propietari);
    }

}

