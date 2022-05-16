package cat.iesmanacor.gestibgsuite.api;

import cat.iesmanacor.gestibgsuite.manager.CentreService;
import cat.iesmanacor.gestibgsuite.manager.GMailService;
import cat.iesmanacor.gestibgsuite.model.gestib.Centre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
public class CentreController {

    @Autowired
    private CentreService centreService;

    @Autowired
    private GMailService gMailService;

    @PostMapping("/proves/gmail")
    public void provesGmail() throws MessagingException, GeneralSecurityException, IOException {
        gMailService.helloWorld();
    }

    @GetMapping("/centre/{id}")
    public ResponseEntity<Centre> getCentreByIdentificador(@PathVariable("id") String identificador) {
        Centre p = centreService.findByIdentificador(identificador);
        return new ResponseEntity<>(p, HttpStatus.OK);
    }

}