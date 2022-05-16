package cat.iesmanacor.gestibgsuite.manager;

import cat.iesmanacor.gestibgsuite.model.google.Calendari;
import cat.iesmanacor.gestibgsuite.model.google.CalendariTipus;
import cat.iesmanacor.gestibgsuite.repository.google.CalendariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalendariService {
    @Autowired
    private CalendariRepository calendariRepository;

    public Calendari save(Calendari c) {
        return calendariRepository.save(c);
    }

    public Calendari save(String email, String nom, String descripcio, String grup, CalendariTipus calendariTipus) {
        Calendari c = new Calendari();
        c.setGsuiteEmail(email);
        c.setGsuiteNom(nom);
        c.setGsuiteDescripcio(descripcio);
        c.setGestibGrup(grup);
        c.setCalendariTipus(calendariTipus);

        return calendariRepository.save(c);
    }

    public Calendari findByEmail(String email) {
        return calendariRepository.findCalendariByGsuiteEmail(email);
    }

    public List<Calendari> findAll() {
        return calendariRepository.findAll();
    }

    public Calendari findByGestibGrup(String gestibGrup) {
        return calendariRepository.findCalendariByGestibGrup(gestibGrup);
    }

}

