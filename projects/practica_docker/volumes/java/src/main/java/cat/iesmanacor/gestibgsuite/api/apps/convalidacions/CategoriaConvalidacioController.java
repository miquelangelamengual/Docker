package cat.iesmanacor.gestibgsuite.api.apps.convalidacions;

import cat.iesmanacor.gestibgsuite.manager.MathService;
import cat.iesmanacor.gestibgsuite.manager.TokenManager;
import cat.iesmanacor.gestibgsuite.manager.UsuariService;
import cat.iesmanacor.gestibgsuite.manager.apps.convalidacions.CategoriaConvalidacioService;
import cat.iesmanacor.gestibgsuite.manager.apps.grupscooperatius.ItemService;
import cat.iesmanacor.gestibgsuite.manager.apps.grupscooperatius.ValorItemService;
import cat.iesmanacor.gestibgsuite.model.Notificacio;
import cat.iesmanacor.gestibgsuite.model.NotificacioTipus;
import cat.iesmanacor.gestibgsuite.model.apps.convalidacions.CategoriaConvalidacio;
import cat.iesmanacor.gestibgsuite.model.apps.grupscooperatius.*;
import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@RestController
public class CategoriaConvalidacioController {

    @Autowired
    private CategoriaConvalidacioService categoriaConvalidacioService;

    @Autowired
    private Gson gson;

    @GetMapping("/apps/convalidacions/categories")
    public ResponseEntity<List<CategoriaConvalidacio>> getCategories() {

        List<CategoriaConvalidacio> categories = categoriaConvalidacioService.findAll();

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/apps/convalidacions/categoria/{id}")
    public ResponseEntity<CategoriaConvalidacio> getCategoriaById(@PathVariable("id") String idcategoria) {

        CategoriaConvalidacio categoria = categoriaConvalidacioService.getCategoriaConvalidacioById(Long.valueOf(idcategoria));

        return new ResponseEntity<>(categoria, HttpStatus.OK);
    }

    @PostMapping("/apps/convalidacions/categoria/desar")
    @Transactional
    public ResponseEntity<Notificacio> desarCategoriaConvalidacio(@RequestBody String json, HttpServletRequest request) throws GeneralSecurityException, IOException {

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        Long idCategoria = null;
        if (jsonObject.get("id") != null && !jsonObject.get("id").isJsonNull()){
            idCategoria = jsonObject.get("id").getAsLong();
        }

        String nom = jsonObject.get("nom").getAsString();

        CategoriaConvalidacio categoriaConvalidacio;

        if(idCategoria != null) {
            categoriaConvalidacio = categoriaConvalidacioService.getCategoriaConvalidacioById(idCategoria);
        } else {
            categoriaConvalidacio = new CategoriaConvalidacio();
        }

        categoriaConvalidacio.setNom(nom);

        categoriaConvalidacioService.save(categoriaConvalidacio);

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Categoria desada correctament");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }

    @PostMapping("/apps/convalidacions/categoria/esborrar")
    @Transactional
    public ResponseEntity<Notificacio> esborrarCategoriaConvalidacio(@RequestBody String json, HttpServletRequest request) throws GeneralSecurityException, IOException {

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        Long idCategoria = null;
        if (jsonObject.get("id") != null && !jsonObject.get("id").isJsonNull()){
            idCategoria = jsonObject.get("id").getAsLong();
        }


        if(idCategoria != null) {
            CategoriaConvalidacio categoriaConvalidacio = categoriaConvalidacioService.getCategoriaConvalidacioById(idCategoria);
            categoriaConvalidacioService.esborrar(categoriaConvalidacio);

            Notificacio notificacio = new Notificacio();
            notificacio.setNotifyMessage("Categoria esborrada correctament");
            notificacio.setNotifyType(NotificacioTipus.SUCCESS);
            return new ResponseEntity<>(notificacio, HttpStatus.OK);
        }

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("No s'ha pogut esborrar la categoria");
        notificacio.setNotifyType(NotificacioTipus.ERROR);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);

    }



}