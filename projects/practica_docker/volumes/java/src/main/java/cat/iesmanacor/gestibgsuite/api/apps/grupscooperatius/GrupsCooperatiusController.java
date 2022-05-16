package cat.iesmanacor.gestibgsuite.api.apps.grupscooperatius;

import cat.iesmanacor.gestibgsuite.manager.MathService;
import cat.iesmanacor.gestibgsuite.manager.TokenManager;
import cat.iesmanacor.gestibgsuite.manager.UsuariService;
import cat.iesmanacor.gestibgsuite.manager.apps.grupscooperatius.ItemService;
import cat.iesmanacor.gestibgsuite.manager.apps.grupscooperatius.ValorItemService;
import cat.iesmanacor.gestibgsuite.model.Notificacio;
import cat.iesmanacor.gestibgsuite.model.NotificacioTipus;
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
public class GrupsCooperatiusController {

    @Autowired
    private UsuariService usuariService;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ValorItemService valorItemService;

    @Autowired
    private MathService mathService;

    @Autowired
    private Gson gson;

    /*-- GRUPS COOPERATIUS --*/
    @PostMapping("/apps/grupscooperatius/aleatori")
    public ResponseEntity<List<Agrupament>> getMesclaGrupsAleatoria(@RequestBody String json) {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        //Usuaris i valors dels ítems
        JsonArray membresJSON = jsonObject.get("members").getAsJsonArray();
        List<Membre> membres = new ArrayList<>();
        for(JsonElement membreJSON: membresJSON){

            Membre membre = new Membre();
            membre.setNom(membreJSON.getAsJsonObject().get("nom").getAsString());

            JsonArray itemsUsuari = membreJSON.getAsJsonObject().get("valorsItemMapped").getAsJsonArray();
            List<ValorItemMembre> valorsItemMembre = new ArrayList<>();
            for(JsonElement itemUsuari:itemsUsuari){
                ValorItemMembre valorItemMembre = new ValorItemMembre();
                ValorItem valorItem = valorItemService.findById(itemUsuari.getAsJsonObject().get("value").getAsLong());

                valorItemMembre.setMembre(membre);
                valorItemMembre.setValorItem(valorItem);

                valorsItemMembre.add(valorItemMembre);
            }
            membre.setValorsItemMembre(new HashSet<>(valorsItemMembre));
            membres.add(membre);
        }

        int numGrups = jsonObject.get("numGrups").getAsInt();

        Collections.shuffle(membres);


        List<Membre>[] grups = new ArrayList[numGrups];
        for (int j = 0; j < grups.length; j++) {
            grups[j] = new ArrayList<>();
        }

        int i = 0;
        for (Membre m : membres) {
            grups[i % numGrups].add(m);
            i++;
        }

        GrupCooperatiu grupCooperatiu = new GrupCooperatiu();
        grupCooperatiu.setNom("PROVA");

        //Pintem els resultats
        List<Agrupament> agrupaments = new ArrayList<>();

        for (int j = 0; j < grups.length; j++) {
            Agrupament agrupament = new Agrupament();
            agrupament.setGrupCooperatiu(grupCooperatiu);
            Set membresSet = new HashSet(grups[j]);
            agrupament.setMembres(membresSet);

            agrupaments.add(agrupament);
        }

        return new ResponseEntity<>(agrupaments, HttpStatus.OK);
    }


    @PostMapping("/apps/grupscooperatius/genetica")
    public ResponseEntity<List<Agrupament>> getMesclaGrupsGenetica(@RequestBody String json) {
        int numIteracions = 1000000;
        //int numIteracions = 10;

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        GrupCooperatiu grupCooperatiu = new GrupCooperatiu();
        grupCooperatiu.setNom("PROVA");

        JsonArray itemsGrupCooperatiuJSON = jsonObject.get("items").getAsJsonArray();
        List<ItemGrupCooperatiu> itemsGrupCooperatiu = new ArrayList<>();
        for(JsonElement itemGrupCooperatiuJSON: itemsGrupCooperatiuJSON){

            Item it = itemService.getItemById(itemGrupCooperatiuJSON.getAsJsonObject().get("item").getAsJsonObject().get("id").getAsLong());
            Integer ponderacio = itemGrupCooperatiuJSON.getAsJsonObject().get("percentatge").getAsInt();

            ItemGrupCooperatiu itemGrupCooperatiu = new ItemGrupCooperatiu();
            itemGrupCooperatiu.setItem(it);
            itemGrupCooperatiu.setPercentatge(ponderacio);
            itemGrupCooperatiu.setGrupCooperatiu(grupCooperatiu);

            itemsGrupCooperatiu.add(itemGrupCooperatiu);
        }

        //Usuaris i valors dels ítems
        JsonArray membresJSON = jsonObject.get("members").getAsJsonArray();
        List<Membre> membres = new ArrayList<>();
        for(JsonElement membreJSON: membresJSON){

            Membre membre = new Membre();
            membre.setNom(membreJSON.getAsJsonObject().get("nom").getAsString());

            JsonArray itemsUsuari = membreJSON.getAsJsonObject().get("valorsItemMapped").getAsJsonArray();
            List<ValorItemMembre> valorsItemMembre = new ArrayList<>();
            for(JsonElement itemUsuari:itemsUsuari){
                ValorItemMembre valorItemMembre = new ValorItemMembre();
                ValorItem valorItem = valorItemService.findById(itemUsuari.getAsJsonObject().get("value").getAsLong());

                valorItemMembre.setMembre(membre);
                valorItemMembre.setValorItem(valorItem);

                valorsItemMembre.add(valorItemMembre);
            }
            membre.setValorsItemMembre(new HashSet<>(valorsItemMembre));
            membres.add(membre);
        }


        int numGrups = jsonObject.get("numGrups").getAsInt();

        List<Membre>[] millorsAgrupacions = new ArrayList[numGrups];
        Double millorPuntuacio = null;

        for(int k=0; k<numIteracions; k++) {
            Collections.shuffle(membres);

            List<Membre>[] grups = new ArrayList[numGrups];
            for (int j = 0; j < grups.length; j++) {
                grups[j] = new ArrayList<>();
            }

            int i = 0;
            for (Membre m : membres) {
                grups[i % numGrups].add(m);
                i++;
            }


            double puntuacio = 0;
            for(List<Membre> grup: grups){
                for(ItemGrupCooperatiu itemGrupCooperatiu: itemsGrupCooperatiu){
                    List<Double> valorsItemsComptador = new ArrayList<>();

                    for(ValorItem valorItem: itemGrupCooperatiu.getItem().getValorItems()){
                        Double count = 0.0;
                        for(Membre membre: grup){
                            for (ValorItemMembre vim : membre.getValorsItemMembre()) {
                                if(vim.getValorItem().getIdvalorItem().equals(valorItem.getIdvalorItem())){
                                    count++;
                                }
                            }
                        }
                        //valorsItemsComptador.add(count);
                        valorsItemsComptador.add(count*valorItem.getPes());
                    }

                    double[] valorsItemsCountPrimitive = new double[valorsItemsComptador.size()];
                    int idx = 0;
                    for(Double d: valorsItemsComptador){
                        valorsItemsCountPrimitive[idx] = d;
                        idx++;
                    }

                    puntuacio += mathService.variance(valorsItemsCountPrimitive)*itemGrupCooperatiu.getPercentatge();
                }

            }

            //System.out.println("Puntuació grups iteració "+k + ": " +puntuacio);

            if(millorPuntuacio == null || puntuacio < millorPuntuacio) {
                millorsAgrupacions = grups;
                millorPuntuacio = puntuacio;
            }
        }

        //Pintem els resultats
        List<Agrupament> agrupaments = new ArrayList<>();

        for (int j = 0; j < millorsAgrupacions.length; j++) {
            Agrupament agrupament = new Agrupament();
            agrupament.setGrupCooperatiu(grupCooperatiu);
            Set membresSet = new HashSet(millorsAgrupacions[j]);
            agrupament.setMembres(membresSet);

            agrupaments.add(agrupament);
        }

        return new ResponseEntity<>(agrupaments, HttpStatus.OK);
    }



    /*-- ITEMS --*/
    @GetMapping("/apps/grupscooperatius/items")
    public ResponseEntity<List<Item>> getItems(HttpServletRequest request) {
        Claims claims = tokenManager.getClaims(request);
        String myEmail = (String) claims.get("email");

        Usuari myUser = usuariService.findByEmail(myEmail);

        List<Item> items = itemService.findAllByUsuari(myUser);

        return new ResponseEntity<>(items,HttpStatus.OK);
    }

    @GetMapping("/apps/grupscooperatius/item/{id}")
    public ResponseEntity<Item> getItemGrupCooperatiu(@PathVariable("id") String iditem, HttpServletRequest request) throws GeneralSecurityException, IOException {
        Claims claims = tokenManager.getClaims(request);
        String myEmail = (String) claims.get("email");

        Usuari myUser = usuariService.findByEmail(myEmail);

        Item item = itemService.getItemById(Long.valueOf(iditem));

        if(item != null && item.getUsuari().getIdusuari().equals(myUser.getIdusuari())){
            return new ResponseEntity<>(item, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/apps/grupscooperatius/item/valors/{id}")
    public ResponseEntity<List<ValorItem>> getValorsItemGrupCooperatiu(@PathVariable("id") String iditem, HttpServletRequest request) throws GeneralSecurityException, IOException {
        Claims claims = tokenManager.getClaims(request);
        String myEmail = (String) claims.get("email");

        Usuari myUser = usuariService.findByEmail(myEmail);

        Item item = itemService.getItemById(Long.valueOf(iditem));
        List<ValorItem> valors = valorItemService.findAllValorsByItem(item);

        //Seguretat
        boolean usuariCorrecte = valors.stream().allMatch(valorItem -> valorItem.getItem().getUsuari().getIdusuari().equals(myUser.getIdusuari()));

        if(valors != null && usuariCorrecte){
            return new ResponseEntity<>(valors, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/apps/grupscooperatius/item/desar")
    @Transactional
    public ResponseEntity<Notificacio> desarItemGrupCooperatiu(@RequestBody String json, HttpServletRequest request) throws GeneralSecurityException, IOException {
        Claims claims = tokenManager.getClaims(request);
        String myEmail = (String) claims.get("email");

        Usuari myUser = usuariService.findByEmail(myEmail);

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        Long idItem = null;
        if (!jsonObject.get("id").isJsonNull()){
            idItem = jsonObject.get("id").getAsLong();
        }

        String nomItem = jsonObject.get("nom").getAsString();

        JsonArray valors = jsonObject.get("valorsItem").getAsJsonArray();

        Item item;

        if(idItem != null) {
            item = itemService.getItemById(idItem);
        } else {
            item = new Item();
        }

        item.setUsuari(myUser);
        item.setNom(nomItem);

        Item i = itemService.save(item);

        //Valors
        List<ValorItem> valorsItem = new ArrayList<>();
        for(JsonElement valor: valors){
            ValorItem vi = new ValorItem();
            vi.setValor(valor.getAsJsonObject().get("valor").getAsString());
            vi.setPes(valor.getAsJsonObject().get("pes").getAsInt());
            vi.setItem(i);

            valorsItem.add(vi);
        }

        //Esborrem antics valors
        itemService.deleteAllValorsByItem(i);

        //Creem els nous
        for(ValorItem valorItem: valorsItem){
            valorItemService.save(valorItem);
        }


        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Ítem desat correctament");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }

}