package cat.iesmanacor.gestibgsuite.api.apps.convalidacions;

import cat.iesmanacor.gestibgsuite.manager.apps.convalidacions.ConvalidacioConvalidacioService;
import cat.iesmanacor.gestibgsuite.manager.apps.convalidacions.ItemConvalidacioService;
import cat.iesmanacor.gestibgsuite.manager.apps.convalidacions.SolicitudConvalidacioService;
import cat.iesmanacor.gestibgsuite.model.Notificacio;
import cat.iesmanacor.gestibgsuite.model.NotificacioTipus;
import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.ConvalidacioConvalidacio;
import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.ItemConvalidacio;
import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.SolicitudConvalidacio;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
public class SolicitudConvalidacioController {

    @Autowired
    private SolicitudConvalidacioService solicitudConvalidacioService;

    @Autowired
    private Gson gson;


    @GetMapping("/apps/convalidacions/solicitud/llistat")
    public ResponseEntity<List<SolicitudConvalidacio>> getSolicituds() {
        List<SolicitudConvalidacio> solicituds = solicitudConvalidacioService.findAll();

        return new ResponseEntity<>(solicituds, HttpStatus.OK);
    }

    @PostMapping("/apps/convalidacions/solicitud/desar")
    @Transactional
    public ResponseEntity<Notificacio> desarSolicitud(@RequestBody String json, HttpServletRequest request) throws GeneralSecurityException, IOException {

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
/*
        Long idConvalidacio = null;
        if (jsonObject.get("id") != null && !jsonObject.get("id").isJsonNull()){
            idConvalidacio = jsonObject.get("id").getAsLong();
        }

        ConvalidacioConvalidacio convalidacio;

        if(idConvalidacio != null) {
            convalidacio = convalidacioConvalidacioService.getConvalidacioConvalidacioById(idConvalidacio);
            convalidacio.getOrigens().clear();
            convalidacio.getConvalida().clear();
        } else {
            convalidacio = new ConvalidacioConvalidacio();
        }

        JsonArray origensJSON = jsonObject.get("origens").getAsJsonArray();

        for(JsonElement itemJSON: origensJSON){
            Long idItem = itemJSON.getAsJsonObject().get("id").getAsLong();
            ItemConvalidacio item = itemConvalidacioService.getItemConvalidacioById(idItem);
            convalidacio.getOrigens().add(item);
        }

        JsonArray convalidaJSON = jsonObject.get("convalida").getAsJsonArray();

        for(JsonElement itemJSON: convalidaJSON){
            Long idItem = itemJSON.getAsJsonObject().get("id").getAsLong();
            ItemConvalidacio item = itemConvalidacioService.getItemConvalidacioById(idItem);
            convalidacio.getConvalida().add(item);
        }

        convalidacioConvalidacioService.save(convalidacio);
*/
        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Sol·licitud desada correctament");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }



    @PostMapping("/apps/convalidacions/solicitud/esborrar")
    @Transactional
    public ResponseEntity<Notificacio> esborrarSolicitud(@RequestBody String json, HttpServletRequest request) throws GeneralSecurityException, IOException {

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        /*Long idConvalidacio = null;
        if (jsonObject.get("id") != null && !jsonObject.get("id").isJsonNull()){
            idConvalidacio = jsonObject.get("id").getAsLong();
        }


        if(idConvalidacio != null) {
            ConvalidacioConvalidacio convalidacio = convalidacioConvalidacioService.getConvalidacioConvalidacioById(idConvalidacio);
            convalidacioConvalidacioService.esborrar(convalidacio);

            Notificacio notificacio = new Notificacio();
            notificacio.setNotifyMessage("Convalidació esborrada correctament");
            notificacio.setNotifyType(NotificacioTipus.SUCCESS);
            return new ResponseEntity<>(notificacio, HttpStatus.OK);
        }*/

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("No s'ha pogut esborrar la convalidació");
        notificacio.setNotifyType(NotificacioTipus.ERROR);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);

    }


}