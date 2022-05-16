package cat.iesmanacor.gestibgsuite.api;

import cat.iesmanacor.gestibgsuite.manager.CalendariService;
import cat.iesmanacor.gestibgsuite.manager.GSuiteService;
import cat.iesmanacor.gestibgsuite.model.google.Calendari;
import cat.iesmanacor.gestibgsuite.model.google.CalendariTipus;
import com.google.api.services.directory.model.CalendarResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
public class CalendariController {

    @Autowired
    private GSuiteService gSuiteService;

    @Autowired
    private CalendariService calendariService;

    @GetMapping("/calendari/llistat")
    public ResponseEntity<List<Calendari>> getGrups(HttpServletRequest request) throws GeneralSecurityException, IOException {
        /*
        Sincronització GSuite -> BBDD
        Si el grup NO existeix creem el grup a la BBDD i associem els usuaris
        Si el grup SI existeix actualitzem els membres de la BBDD
         */
        List<CalendarResource> calendarisGSuite = gSuiteService.getCalendars();

        for (CalendarResource calendariGSuite : calendarisGSuite) {

            Calendari calendari = calendariService.findByEmail(calendariGSuite.getResourceEmail());

            if (calendari == null) {
                //Creem el calendari a la BBDD
                calendariService.save(calendariGSuite.getResourceEmail(), calendariGSuite.getResourceName(), calendariGSuite.getResourceDescription(), null, CalendariTipus.GENERAL);
            }
        }

        List<Calendari> calendaris = calendariService.findAll();

        return new ResponseEntity<>(calendaris, HttpStatus.OK);
    }

}