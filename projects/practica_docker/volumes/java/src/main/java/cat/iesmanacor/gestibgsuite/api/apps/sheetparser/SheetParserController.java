package cat.iesmanacor.gestibgsuite.api.apps.sheetparser;

import cat.iesmanacor.gestibgsuite.manager.GMailService;
import cat.iesmanacor.gestibgsuite.manager.GoogleSpreadsheetService;
import cat.iesmanacor.gestibgsuite.manager.UsuariService;
import cat.iesmanacor.gestibgsuite.model.Notificacio;
import cat.iesmanacor.gestibgsuite.model.NotificacioTipus;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SheetParserController {

    @Autowired
    private UsuariService usuariService;

    @Autowired
    private GoogleSpreadsheetService googleSpreadsheetService;

    @Autowired
    private GMailService gMailService;

    @Autowired
    private Gson gson;



    @Transactional
    @PostMapping("/apps/sheetparser/draft")
    public ResponseEntity<List<List<String>>> getDraftAlumnes(@RequestBody String idsheet) throws GeneralSecurityException, IOException, MessagingException {
        List<List<String>> linies = googleSpreadsheetService.getSpreadsheetDataTable(idsheet, "jgalmes1@iesmanacor.cat");
        return new ResponseEntity<>(linies, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/apps/sheetparser/send")
    public ResponseEntity<Notificacio> sendEmailAlumnes(@RequestBody String json) throws GeneralSecurityException, IOException, MessagingException {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        String titol = jsonObject.get("titol").getAsString();
        String idsheet = jsonObject.get("idsheet").getAsString();
        Integer numHeaders = jsonObject.get("numHeaders").getAsInt();
        Integer numRowsAlumnes = jsonObject.get("numRowsAlumnes").getAsInt();
        Integer numColumnEmail = jsonObject.get("numColumnEmail").getAsInt();

        List<List<String>> linies = googleSpreadsheetService.getSpreadsheetDataTable(idsheet, "jgalmes1@iesmanacor.cat");

        int numAlumnes = (linies.size() - numHeaders) / numRowsAlumnes;
        int numColumnesTotal = linies.get(0).size();
        System.out.println("Lines" + linies.size() + " num alumnes" + numAlumnes);

        //Header
        List<List<String>> header = new ArrayList<>();

        for(int i=0; i<numHeaders-1; i++){
            header.add(linies.get(i));
        }

        System.out.println("header:"+header);

        //Alumnes
        List<List<List<String>>> alumnes = new ArrayList<>();
        for (int i = 0; i < numAlumnes; i++) {
            List<List<String>> alumne = new ArrayList<>();
            for(int j=0;j<numRowsAlumnes;j++){
                alumne.add(linies.get(numHeaders-1+(i*numRowsAlumnes)+j));
            }
            alumnes.add(alumne);
        }

        System.out.println("alumnes:"+alumnes);

        //E-mails
        String styleHeader = "border: solid 1px black; border-collapse:collapse; background: #CCCCCC; padding: 5px; text-align: center; vertical-align: center;";
        String styleAlumne = "border: solid 1px black; border-collapse:collapse; padding: 5px; text-align: right; vertical-align: center;";
        for(List<List<String>> alumne: alumnes){
            String missatge = "";
            String emailAlumne = "";

            missatge += "<table style=\"border-collapse:collapse; border-spacing: 0;\">";

            //Header
            for(List<String> filaHeader: header){
                missatge += "<tr>";
                int numColumnesHeader = 0;
                for(String columnaHeader: filaHeader){
                    missatge += "<th style=\""+styleHeader+"\">"+columnaHeader+"</th>";
                    numColumnesHeader++;
                }

                //Emplenam, si cal, les columnes restants (al full de càlcul buides)
                for(int i=numColumnesHeader; i<numColumnesTotal; i++ ){
                    missatge += "<th style=\""+styleHeader+"\"></th>";
                }
                missatge += "</tr>";
            }

            //Contingut
            for(List<String> filaAlumne: alumne){
                missatge += "<tr>";
                int numColumnesAlumne = 0;
                for(String columnaAlumne: filaAlumne){
                    missatge += "<td style=\""+styleAlumne+"\">"+columnaAlumne+"</td>";
                    numColumnesAlumne++;

                    if(numColumnesAlumne == numColumnEmail){
                        emailAlumne = columnaAlumne;
                    }
                }

                //Emplenam, si cal, les columnes restants (al full de càlcul buides)
                for(int i=numColumnesAlumne; i<numColumnesTotal; i++ ){
                    missatge += "<td style=\""+styleAlumne+"\"></td>";
                }
                missatge += "</tr>";
            }

            missatge += "</table>";

            if(!emailAlumne.isEmpty()) {
                System.out.println("Email alumne"+emailAlumne);
                gMailService.sendMessage(titol, missatge, emailAlumne);
            }
        }


        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Correus electrònics enviats correctament");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }
}