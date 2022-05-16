package cat.iesmanacor.gestibgsuite.api.apps.convalidacions;

import cat.iesmanacor.gestibgsuite.manager.apps.convalidacions.CategoriaConvalidacioService;
import cat.iesmanacor.gestibgsuite.manager.apps.convalidacions.ConvalidacioConvalidacioService;
import cat.iesmanacor.gestibgsuite.manager.apps.convalidacions.ItemConvalidacioService;
import cat.iesmanacor.gestibgsuite.model.Notificacio;
import cat.iesmanacor.gestibgsuite.model.NotificacioTipus;
import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.CategoriaConvalidacio;
import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.ConvalidacioConvalidacio;
import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.ItemConvalidacio;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ConvalidacioConvalidacioController {

    @Autowired
    private ConvalidacioConvalidacioService convalidacioConvalidacioService;

    @Autowired
    private ItemConvalidacioService itemConvalidacioService;

    @Autowired
    private Gson gson;


    @GetMapping("/apps/convalidacions/convalidacio/llistat")
    public ResponseEntity<List<ConvalidacioConvalidacio>> getConvalidacions() {
        List<ConvalidacioConvalidacio> convalidacions = convalidacioConvalidacioService.findAll();

        return new ResponseEntity<>(convalidacions, HttpStatus.OK);
    }

    @PostMapping("/apps/convalidacions/convalidacio/desar")
    @Transactional
    public ResponseEntity<Notificacio> desarConvalidacio(@RequestBody String json, HttpServletRequest request) throws GeneralSecurityException, IOException {

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

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

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Convalidació desada correctament");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }



    @PostMapping("/apps/convalidacions/convalidacio/esborrar")
    @Transactional
    public ResponseEntity<Notificacio> esborrarConvalidacio(@RequestBody String json, HttpServletRequest request) throws GeneralSecurityException, IOException {

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        Long idConvalidacio = null;
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
        }

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("No s'ha pogut esborrar la convalidació");
        notificacio.setNotifyType(NotificacioTipus.ERROR);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);

    }


}