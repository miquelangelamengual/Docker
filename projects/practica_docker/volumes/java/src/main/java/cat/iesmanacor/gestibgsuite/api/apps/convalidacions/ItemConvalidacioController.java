package cat.iesmanacor.gestibgsuite.api.apps.convalidacions;

import cat.iesmanacor.gestibgsuite.manager.apps.convalidacions.CategoriaConvalidacioService;
import cat.iesmanacor.gestibgsuite.manager.apps.convalidacions.ItemConvalidacioService;
import cat.iesmanacor.gestibgsuite.model.Notificacio;
import cat.iesmanacor.gestibgsuite.model.NotificacioTipus;
import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.CategoriaConvalidacio;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class ItemConvalidacioController {

    @Autowired
    private ItemConvalidacioService itemConvalidacioService;

    @Autowired
    private CategoriaConvalidacioService categoriaConvalidacioService;

    @Autowired
    private Gson gson;

    @GetMapping("/apps/convalidacions/titulacions")
    public ResponseEntity<List<ItemConvalidacio>> getTitulacionsPrincipals() {
        List<ItemConvalidacio> items = itemConvalidacioService.findAllTitulacions();

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/apps/convalidacions/items")
    public ResponseEntity<List<ItemConvalidacio>> getItems() {

        List<ItemConvalidacio> items = itemConvalidacioService.findAll();

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @PostMapping("/apps/convalidacions/titulacio/desar")
    @Transactional
    public ResponseEntity<Notificacio> desarTitulacioConvalidacio(@RequestBody String json, HttpServletRequest request) throws GeneralSecurityException, IOException {

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        Long idTitulacio = null;
        if (jsonObject.get("id") != null && !jsonObject.get("id").isJsonNull()){
            idTitulacio = jsonObject.get("id").getAsLong();
        }

        String codi = jsonObject.get("codi").getAsString();
        String nom = jsonObject.get("nom").getAsString();

        String idCategoria = jsonObject.get("categoria").getAsJsonObject().get("value").getAsString();
        CategoriaConvalidacio categoriaConvalidacio = categoriaConvalidacioService.getCategoriaConvalidacioById(Long.valueOf(idCategoria));

        List<ItemConvalidacio> itemsFill = new ArrayList<>();
        JsonArray itemsComposicioJSON = jsonObject.get("composa").getAsJsonArray();
        for(JsonElement itemComposicio: itemsComposicioJSON){
            String codiItem = itemComposicio.getAsJsonObject().get("codi").getAsString();
            String nomItem = itemComposicio.getAsJsonObject().get("nom").getAsString();

            String idCategoriaItem = itemComposicio.getAsJsonObject().get("categoria").getAsJsonObject().get("value").getAsString();
            CategoriaConvalidacio categoriaConvalidacioItem = categoriaConvalidacioService.getCategoriaConvalidacioById(Long.valueOf(idCategoriaItem));

            Long idFill = null;
            if (itemComposicio.getAsJsonObject().get("id") != null && !itemComposicio.getAsJsonObject().get("id").isJsonNull()){
                idFill = itemComposicio.getAsJsonObject().get("id").getAsLong();
            }

            ItemConvalidacio itemFill;

            if(idFill != null) {
                itemFill = itemConvalidacioService.getItemConvalidacioById(idFill);
            } else {
                itemFill = new ItemConvalidacio();
            }
            itemFill.setCodi(codiItem);
            itemFill.setNom(nomItem);
            itemFill.setCategoria(categoriaConvalidacioItem);

            ItemConvalidacio i = itemConvalidacioService.save(itemFill);

            itemsFill.add(i);

        }

        ItemConvalidacio titulacio;

        if(idTitulacio != null) {
            titulacio = itemConvalidacioService.getItemConvalidacioById(idTitulacio);
        } else {
            titulacio = new ItemConvalidacio();
        }

        titulacio.setCodi(codi);
        titulacio.setNom(nom);
        titulacio.setCategoria(categoriaConvalidacio);

        titulacio.getComposa().clear();
        for(ItemConvalidacio itemConvalidacio:itemsFill){
            titulacio.getComposa().add(itemConvalidacio);
        }

        itemConvalidacioService.save(titulacio);

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Titulació desada correctament");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }


    @GetMapping("/apps/convalidacions/titulacio/{id}")
    public ResponseEntity<ItemConvalidacio> getTitulacioById(@PathVariable("id") String idtitulacio) {

        ItemConvalidacio titulacio = itemConvalidacioService.getItemConvalidacioById(Long.valueOf(idtitulacio));

        return new ResponseEntity<>(titulacio, HttpStatus.OK);
    }

    @PostMapping("/apps/convalidacions/titulacio/esborrar")
    @Transactional
    public ResponseEntity<Notificacio> esborrarItemConvalidacio(@RequestBody String json, HttpServletRequest request) throws GeneralSecurityException, IOException {

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        Long idTitulacio = null;
        if (jsonObject.get("id") != null && !jsonObject.get("id").isJsonNull()){
            idTitulacio = jsonObject.get("id").getAsLong();
        }


        if(idTitulacio != null) {
            ItemConvalidacio itemConvalidacio = itemConvalidacioService.getItemConvalidacioById(idTitulacio);
            itemConvalidacioService.esborrar(itemConvalidacio);

            Notificacio notificacio = new Notificacio();
            notificacio.setNotifyMessage("Titulació esborrada correctament");
            notificacio.setNotifyType(NotificacioTipus.SUCCESS);
            return new ResponseEntity<>(notificacio, HttpStatus.OK);
        }

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("No s'ha pogut esborrar la titulació");
        notificacio.setNotifyType(NotificacioTipus.ERROR);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);

    }


}