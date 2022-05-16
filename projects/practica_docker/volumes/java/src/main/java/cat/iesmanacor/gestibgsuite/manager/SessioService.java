package cat.iesmanacor.gestibgsuite.manager;

import cat.iesmanacor.gestibgsuite.model.gestib.Sessio;
import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import cat.iesmanacor.gestibgsuite.repository.gestib.SessioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SessioService {
    @Autowired
    private SessioRepository sessioRepository;

    /*
    <SESSIO
        professor="944BA534D00BE45FE040D70A59055935"
        curs="94"
        grup="557870"
        dia="3"
        hora="08:00"
        durada="55"
        aula="5938"
        submateria="2066797"
        activitat=""
        placa="10255"
            />
     */
    public Sessio saveSessio(String professor, String alumne, String curs, String grup, String dia, String hora, String durada, String aula, String submateria, String activitat, String placa) {
        Sessio s = new Sessio();
        s.setGestibProfessor(professor);
        s.setGestibAlumne(alumne);
        s.setGestibCurs(curs);
        s.setGestibGrup(grup);
        s.setGestibDia(dia);
        s.setGestibHora(hora);
        s.setGestibDurada(durada);
        s.setGestibAula(aula);
        s.setGestibSubmateria(submateria);
        s.setGestibActivitat(activitat);
        s.setGestibPlaca(placa);

        return sessioRepository.save(s);
    }

    public void deleteAllSessions() {
        sessioRepository.deleteAllInBatch();
        sessioRepository.deleteAll();
    }

    public List<Sessio> findSessionsProfessor(Usuari professor) {
        if (professor.getGestibProfessor() != null && professor.getGestibProfessor()) {
            return sessioRepository.findAllByGestibProfessor(professor.getGestibCodi());
        } else {
            return new ArrayList<>();
        }
    }

    public List<Sessio> findSessionsAlumne(Usuari alumne) {
        if (alumne.getGestibAlumne() != null && alumne.getGestibAlumne()) {
            return sessioRepository.findAllByGestibAlumne(alumne.getGestibCodi());
        } else {
            return new ArrayList<>();
        }
    }
}

