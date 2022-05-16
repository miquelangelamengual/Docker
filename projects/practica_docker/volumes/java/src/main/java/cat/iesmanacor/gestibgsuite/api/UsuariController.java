package cat.iesmanacor.gestibgsuite.api;

import cat.iesmanacor.gestibgsuite.manager.GSuiteService;
import cat.iesmanacor.gestibgsuite.manager.TokenManager;
import cat.iesmanacor.gestibgsuite.manager.UsuariService;
import cat.iesmanacor.gestibgsuite.model.Notificacio;
import cat.iesmanacor.gestibgsuite.model.NotificacioTipus;
import cat.iesmanacor.gestibgsuite.model.Rol;
import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import com.google.api.services.directory.model.Group;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class UsuariController {

    @Autowired
    private UsuariService usuariService;

    @Autowired
    private GSuiteService gSuiteService;

    @Autowired
    private TokenManager tokenManager;

    @GetMapping("/usuaris/llistat/actius")
    public ResponseEntity<List<Usuari>> getUsuarisActius() {
        log.info("Usuaris actius");
        List<Usuari> usuaris = usuariService.findUsuarisActius();
        return new ResponseEntity<>(usuaris, HttpStatus.OK);
    }

    @GetMapping("/usuaris/llistat/pendentssuspendre")
    public ResponseEntity<List<Usuari>> getUsuarisPendentsSuspendre() {
        List<Usuari> usuaris = usuariService.findUsuarisNoActius().stream().filter(usuari -> (usuari.getGsuiteSuspes() == null || !usuari.getGsuiteSuspes()) && (usuari.getGsuiteEliminat() == null || !usuari.getGsuiteEliminat())).collect(Collectors.toList());
        return new ResponseEntity<>(usuaris, HttpStatus.OK);
    }

    @GetMapping("/usuaris/llistat/suspesos")
    public ResponseEntity<List<Usuari>> getUsuarisSuspesos() {
        List<Usuari> usuaris = usuariService.findUsuarisNoActius().stream().filter(usuari -> usuari.getGsuiteSuspes() != null && usuari.getGsuiteSuspes()).collect(Collectors.toList());
        return new ResponseEntity<>(usuaris, HttpStatus.OK);
    }

    @GetMapping("/usuaris/llistat/eliminats")
    public ResponseEntity<List<Usuari>> getUsuarisEliminats() {
        List<Usuari> usuaris = usuariService.findUsuarisNoActius().stream().filter(usuari -> usuari.getGsuiteEliminat() != null && usuari.getGsuiteEliminat()).collect(Collectors.toList());
        return new ResponseEntity<>(usuaris, HttpStatus.OK);
    }

    @PostMapping("/usuaris/suspendre")
    public ResponseEntity<Notificacio> suspendreUsuaris(@RequestBody List<Usuari> usuaris) throws GeneralSecurityException, IOException {

        for (Usuari usuari : usuaris) {
            //Esborrem grups
            log.info("Usuari:" + usuari.getGsuiteFullName() + "Email:" + usuari.getGsuiteEmail());
            List<Group> grups = gSuiteService.getUserGroups(usuari.getGsuiteEmail());
            for (Group grup : grups) {
                log.info("Esborrant grup de correu " + grup.getEmail() + " de l'usuari " + usuari.getGsuiteEmail());
                gSuiteService.deleteMember(usuari.getGsuiteEmail(), grup.getEmail());
            }
            gSuiteService.suspendreUser(usuari.getGsuiteEmail(), true);

            //Suspenem a la BBDD
            usuariService.suspendreUsuari(usuari);
        }

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Usuaris suspesos correctament");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);

        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }

    @GetMapping({"/usuaris/profile/rol", "/auth/profile/rol"})
    public ResponseEntity<Set<Rol>> getRolsUsuari(HttpServletRequest request) {
        Claims claims = tokenManager.getClaims(request);

        String email = (String) claims.get("email");
        Usuari usuari = usuariService.findByEmail(email);

        if(usuari == null || email == null){
            return null;
        }
        System.out.println("Email rol: " + email + " Usuari rol: " + usuari.getGsuiteEmail() + " " + usuari.getGsuiteFullName());

        Set<Rol> rols = new HashSet<>();
        if (usuari.getRols() != null) {
            rols.addAll(usuari.getRols());
        }

        if (usuari.getGestibProfessor()) {
            rols.add(Rol.PROFESSOR);
        }

        if (usuari.getGestibAlumne()) {
            rols.add(Rol.ALUMNE);
        }

        return new ResponseEntity<>(rols, HttpStatus.OK);
    }

    @GetMapping("/usuaris/profile/{id}")
    public ResponseEntity<Usuari> getProfile(@PathVariable("id") String idUsuari, HttpServletRequest request) throws Exception {
        Claims claims = tokenManager.getClaims(request);
        String myEmail = (String) claims.get("email");

        Usuari myUser = usuariService.findByEmail(myEmail);
        Usuari usuari = usuariService.findById(Long.valueOf(idUsuari));

        //Si l'usuari que fa la consulta és el mateix o bé si té rol de cap d'estudis, director o administrador
        if (myUser != null && usuari != null && myUser.getGsuiteEmail() != null && usuari.getGsuiteEmail() != null &&
                (myUser.getGsuiteEmail().equals(usuari.getGsuiteEmail()) || myUser.getRols().contains(Rol.ADMINISTRADOR) || myUser.getRols().contains(Rol.DIRECTOR) || myUser.getRols().contains(Rol.CAP_ESTUDIS))
        ) {
            return new ResponseEntity<>(usuari, HttpStatus.OK);
        } else {
            throw new Exception("Sense permisos");
        }
    }

    @PostMapping("/usuari/reset")
    public ResponseEntity<Notificacio> resetPassword(@RequestBody Map<String, String> json) throws GeneralSecurityException, IOException {
        gSuiteService.resetPassword(json.get("usuari"),json.get("password"));

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Contrasenya canviada correctament. La nova contrasenya és: "+json.get("password"));
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);

        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }
}