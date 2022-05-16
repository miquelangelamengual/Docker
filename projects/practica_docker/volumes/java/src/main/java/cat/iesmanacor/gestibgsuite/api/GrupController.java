package cat.iesmanacor.gestibgsuite.api;

import cat.iesmanacor.gestibgsuite.manager.GrupService;
import cat.iesmanacor.gestibgsuite.manager.TokenManager;
import cat.iesmanacor.gestibgsuite.manager.UsuariService;
import cat.iesmanacor.gestibgsuite.model.gestib.Grup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GrupController {

    @Autowired
    private GrupService grupService;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private UsuariService usuariService;

    @GetMapping("/grup/llistat")
    public ResponseEntity<List<Grup>> getGrups() {
        List<Grup> grups = grupService.findAll();
        return new ResponseEntity<>(grups, HttpStatus.OK);
    }

}