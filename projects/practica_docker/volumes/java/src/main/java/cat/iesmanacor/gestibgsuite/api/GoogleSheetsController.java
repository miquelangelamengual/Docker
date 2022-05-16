package cat.iesmanacor.gestibgsuite.api;

import cat.iesmanacor.gestibgsuite.manager.*;
import cat.iesmanacor.gestibgsuite.model.Notificacio;
import cat.iesmanacor.gestibgsuite.model.NotificacioTipus;
import cat.iesmanacor.gestibgsuite.model.gestib.Grup;
import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import cat.iesmanacor.gestibgsuite.model.google.Dispositiu;
import cat.iesmanacor.gestibgsuite.model.google.GrupCorreu;
import cat.iesmanacor.gestibgsuite.model.google.LlistatGoogleTipus;
import com.google.api.services.directory.model.ChromeOsDevice;
import com.google.api.services.directory.model.User;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class GoogleSheetsController {
    private final static String[] DOMAINS = {"iesmanacor.cat", "alumnes.iesmanacor.cat"};

    @Autowired
    private GoogleSpreadsheetService googleSpreadsheetService;

    @Autowired
    private GSuiteService gSuiteService;

    @Autowired
    private LlistatGoogleService llistatGoogleService;

    @Autowired
    private UsuariService usuariService;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private Gson gson;

    @Value("${gc.adminUser}")
    private String adminUser;


    @PostMapping("/google/sheets/alumnatpergrup")
    @Transactional
    public ResponseEntity alumnatPerGrup(@RequestBody List<Grup> grups, HttpServletRequest request) throws GeneralSecurityException, IOException {
        Claims claims = tokenManager.getClaims(request);
        String myEmail = (String) claims.get("email");

        Usuari myUser = usuariService.findByEmail(myEmail);

        Spreadsheet spreadsheet = googleSpreadsheetService.alumnesGrup(grups, myUser.getGsuiteEmail());

        llistatGoogleService.save(spreadsheet.getSpreadsheetId(), spreadsheet.getProperties().getTitle(), spreadsheet.getSpreadsheetUrl(), LlistatGoogleTipus.ALUMNES_PER_GRUP, myUser);

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Llistat desat amb èxit");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);

        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }

    @PostMapping("/google/sheets/usuarisgrupcorreu")
    @Transactional
    public ResponseEntity usuarisPerGrupCorreu(@RequestBody List<GrupCorreu> grupsCoreu, HttpServletRequest request) throws GeneralSecurityException, IOException {
        Claims claims = tokenManager.getClaims(request);
        String myEmail = (String) claims.get("email");

        Usuari myUser = usuariService.findByEmail(myEmail);

        Spreadsheet spreadsheet = googleSpreadsheetService.usuarisGrupCorreu(grupsCoreu, myUser.getGsuiteEmail());

        llistatGoogleService.save(spreadsheet.getSpreadsheetId(), spreadsheet.getProperties().getTitle(), spreadsheet.getSpreadsheetUrl(), LlistatGoogleTipus.USUARIS_PER_GRUPCORREU, myUser);

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Llistat desat amb èxit");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);

        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }

    @PostMapping("/google/sheets/usuarisdispositiu")
    @Transactional
    public ResponseEntity usuarisPerDispositiu(HttpServletRequest request) throws GeneralSecurityException, IOException {
        Claims claims = tokenManager.getClaims(request);
        String myEmail = (String) claims.get("email");

        Usuari myUser = usuariService.findByEmail(myEmail);

        List<Usuari> usuaris = usuariService.findAll();
        User my = gSuiteService.getUserById(this.adminUser);
        List<ChromeOsDevice> chromeOsDevices = gSuiteService.getChromeOSDevicesByUser(my);

        List<Usuari> usuarisChromebook = new ArrayList<>();
        List<Usuari> usuarisNoChromebook = new ArrayList<>();
        List<Dispositiu> dispositiusAssignats = new ArrayList<>();
        List<Dispositiu> dispositiusNoAssignats = new ArrayList<>();

        int i=1;
        for(ChromeOsDevice chromeOsDevice: chromeOsDevices){
            Dispositiu dispositiu = new Dispositiu();
            dispositiu.setIdDispositiu(chromeOsDevice.getDeviceId());
            dispositiu.setEstat(chromeOsDevice.getStatus());
            dispositiu.setModel(chromeOsDevice.getModel());
            dispositiu.setMacAddress(chromeOsDevice.getMacAddress());
            dispositiu.setOrgUnitPath(chromeOsDevice.getOrgUnitPath());
            dispositiu.setSerialNumber(chromeOsDevice.getSerialNumber());

            List<ChromeOsDevice.RecentUsers> recentUsers = chromeOsDevice.getRecentUsers();
            if(recentUsers == null || recentUsers.isEmpty() || recentUsers.get(0) == null){
                dispositiusNoAssignats.add(dispositiu);
            } else {
                //Usuari més recent
                String email = recentUsers.get(0).getEmail();
                Usuari usuari = usuariService.findByEmail(email);

                if (usuari != null) {
                    dispositiu.setUsuari(usuari);
                    usuari.getDispositius().add(dispositiu);
                    if(!usuarisChromebook.contains(usuari)){
                        usuarisChromebook.add(usuari);
                    }
                    dispositiusAssignats.add(dispositiu);
                } else {
                    dispositiusNoAssignats.add(dispositiu);
                }
            }
        }

        for(Usuari usuari: usuaris){
            if(!usuarisChromebook.contains(usuari)){
                usuarisNoChromebook.add(usuari);
            }
        }


        System.out.println("En total hi ha"+chromeOsDevices.size());
        System.out.println("Usuaris totals:"+usuaris.size());
        System.out.println("Usuaris chromebook"+usuarisChromebook.size());
        System.out.println("Usuaris no chromebook"+usuarisNoChromebook.size());
        System.out.println("Dispositius no assignats"+dispositiusNoAssignats.size());

        Spreadsheet spreadsheet = googleSpreadsheetService.usuarisDispositiu(dispositiusAssignats, dispositiusNoAssignats, usuarisChromebook, usuarisNoChromebook,myUser.getGsuiteEmail());

        llistatGoogleService.save(spreadsheet.getSpreadsheetId(), spreadsheet.getProperties().getTitle(), spreadsheet.getSpreadsheetUrl(), LlistatGoogleTipus.USUARIS_PER_DISPOSITIU, myUser);

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Llistat desat amb èxit");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);

        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }

    @PostMapping("/google/sheets/usuariscustom")
    @Transactional
    public ResponseEntity usuarisCustom(@RequestBody String json, HttpServletRequest request) throws GeneralSecurityException, IOException {
        Claims claims = tokenManager.getClaims(request);
        String myEmail = (String) claims.get("email");

        Usuari myUser = usuariService.findByEmail(myEmail);

        JsonArray usuarisJSON = gson.fromJson(json, JsonArray.class);
        List<Usuari> usuaris = new ArrayList<>();
        for(JsonElement usuariJSON: usuarisJSON){
            Long id = usuariJSON.getAsJsonObject().get("id").getAsLong();
            Usuari usuari = usuariService.findById(id);
            usuaris.add(usuari);
        }

        Spreadsheet spreadsheet = googleSpreadsheetService.usuarisCustom(usuaris, myUser.getGsuiteEmail());

        llistatGoogleService.save(spreadsheet.getSpreadsheetId(), spreadsheet.getProperties().getTitle(), spreadsheet.getSpreadsheetUrl(), LlistatGoogleTipus.USUARIS_CUSTOM, myUser);

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Llistat desat amb èxit");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);

        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }

}