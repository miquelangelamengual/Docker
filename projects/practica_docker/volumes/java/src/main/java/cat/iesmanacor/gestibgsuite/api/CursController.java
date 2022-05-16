package cat.iesmanacor.gestibgsuite.api;

import cat.iesmanacor.gestibgsuite.manager.CursService;
import cat.iesmanacor.gestibgsuite.model.gestib.Curs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CursController {

    @Autowired
    private CursService cursService;


    @GetMapping("/curs/getByCodiGestib/{id}")
    public ResponseEntity<Curs> getGrups(@PathVariable("id") String identificador) {
        Curs curs = cursService.findByGestibIdentificador(identificador);
        return new ResponseEntity<>(curs, HttpStatus.OK);
    }

}