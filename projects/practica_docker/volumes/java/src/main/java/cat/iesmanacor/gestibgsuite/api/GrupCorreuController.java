package cat.iesmanacor.gestibgsuite.api;

import cat.iesmanacor.gestibgsuite.manager.*;
import cat.iesmanacor.gestibgsuite.model.Notificacio;
import cat.iesmanacor.gestibgsuite.model.NotificacioTipus;
import cat.iesmanacor.gestibgsuite.model.Rol;
import cat.iesmanacor.gestibgsuite.model.gestib.*;
import cat.iesmanacor.gestibgsuite.model.google.GrupCorreu;
import cat.iesmanacor.gestibgsuite.model.google.GrupCorreuTipus;
import com.google.api.services.directory.model.Group;
import com.google.api.services.directory.model.Member;
import com.google.gson.Gson;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class GrupCorreuController {

    @Autowired
    private UsuariService usuariService;

    @Autowired
    private GSuiteService gSuiteService;

    @Autowired
    private GrupCorreuService grupCorreuService;

    @Autowired
    private SessioService sessioService;

    @Autowired
    private SubmateriaService submateriaService;

    @Autowired
    private ActivitatService activitatService;

    @Autowired
    private DepartamentService departamentService;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private Gson gson;

    @GetMapping("/grupcorreu/llistat")
    @Transactional
    public ResponseEntity<List<GrupCorreu>> getGrups(HttpServletRequest request) throws GeneralSecurityException, IOException {

        /*
        Sincronització GSuite -> BBDD
        Si el grup NO existeix creem el grup a la BBDD i associem els usuaris
        Si el grup SI existeix actualitzem els membres de la BBDD
         */
        List<Group> groups = gSuiteService.getGroups();

        for (Group grup : groups) {

            GrupCorreu grupCorreu = grupCorreuService.findByEmail(grup.getEmail());

            if (grupCorreu == null) {
                //Creem el grup de correu a la BBDD
                grupCorreuService.save(null, grup.getName(), grup.getEmail(), grup.getDescription(), GrupCorreuTipus.GENERAL);
            }
        }

        List<GrupCorreu> grupsCorreu = grupCorreuService.findAll();

        return new ResponseEntity<>(grupsCorreu, HttpStatus.OK);
    }


    @GetMapping("/grupcorreu/grupambusuaris/{id}")
    public ResponseEntity getGrupAmbUsuaris(@PathVariable("id") String idgrup) throws GeneralSecurityException, IOException {
        try {
        /*
        Sincronització GSuite -> BBDD
        Si el grup NO existeix creem el grup a la BBDD i associem els usuaris
        Si el grup SI existeix actualitzem els membres de la BBDD
         */
            Group grup = gSuiteService.getGroupById(idgrup);

            List<Member> members = gSuiteService.getMembers(grup.getId());

            GrupCorreu grupCorreu = grupCorreuService.findByEmail(grup.getEmail());

            if (grupCorreu == null) {
                //Creem el grup de correu a la BBDD
                grupCorreu = grupCorreuService.save(null, grup.getName(), grup.getEmail(), grup.getDescription(), GrupCorreuTipus.GENERAL);
            } else {
                //Actualitzem membres del grup

                //Esborrem tots els actuals, després els tornarem a crear
                grupCorreuService.esborrarUsuarisGrupCorreu(grupCorreu);
                grupCorreuService.esborrarGrupsCorreuGrupCorreu(grupCorreu);
            }

            //Afegim els membres del grup a la BBDD
            for (Member member : members) {
                //Un member pot ser un usuari o un altre grup

                Usuari usuari = usuariService.findByEmail(member.getEmail());
                GrupCorreu grupCorreuMember = grupCorreuService.findByEmail(member.getEmail());
                if (usuari != null) {
                    grupCorreuService.insertUsuari(grupCorreu, usuari);
                }
                if (grupCorreuMember != null) {
                    grupCorreuService.insertGrup(grupCorreu, grupCorreuMember);
                }
            }

            return new ResponseEntity<>(grupCorreu, HttpStatus.OK);
        } catch (Exception e){
            if(e.getMessage().contains("\"code\": 404")){
                System.out.println("El grup de correu no existeix");
                GrupCorreu grupCorreu = grupCorreuService.findByEmail(idgrup);

                //Esborrem els usuaris del grup de correu
                grupCorreuService.esborrarUsuarisGrupCorreu(grupCorreu);

                //Esborrem els grups de correu del grup de correu
                grupCorreuService.esborrarGrupsCorreuGrupCorreu(grupCorreu);

                grupCorreuService.esborrarGrup(grupCorreu);

                Notificacio notificacio = new Notificacio();
                notificacio.setNotifyMessage("Error de consistència. El grup no existeix a GSuite. S'ha esborrat de la Base de Dades.");
                notificacio.setNotifyType(NotificacioTipus.ERROR);
                return new ResponseEntity<>(notificacio, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Notificacio notificacio = new Notificacio();
            notificacio.setNotifyMessage("Error desconegut: " + e.getMessage());
            notificacio.setNotifyType(NotificacioTipus.ERROR);
            return new ResponseEntity<>(notificacio, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/grupcorreu/grupsusuari/{id}")
    public ResponseEntity<List<GrupCorreu>> getGrupsCorreuUsuari(@PathVariable("id") String idUsuari, HttpServletRequest request) throws Exception {
        Claims claims = tokenManager.getClaims(request);
        String myEmail = (String) claims.get("email");

        Usuari myUser = usuariService.findByEmail(myEmail);
        Usuari usuari = usuariService.findById(Long.valueOf(idUsuari));

        List<GrupCorreu> grupsCorreu = new ArrayList<>();
        //Si l'usuari que fa la consulta és el mateix o bé si té rol de cap d'estudis, director o administrador
        if (myUser != null && usuari != null && myUser.getGsuiteEmail() != null && usuari.getGsuiteEmail() != null &&
                (myUser.getGsuiteEmail().equals(usuari.getGsuiteEmail()) || myUser.getRols().contains(Rol.ADMINISTRADOR) || myUser.getRols().contains(Rol.DIRECTOR) || myUser.getRols().contains(Rol.CAP_ESTUDIS))
        ) {
            grupsCorreu.addAll(grupCorreuService.findByUsuari(usuari));
        } else {
            throw new Exception("Sense permisos");
        }

        return new ResponseEntity<>(grupsCorreu, HttpStatus.OK);
    }

    @PostMapping("/grupcorreu/addmember")
    @Transactional
    public ResponseEntity<Notificacio> addMember(@RequestBody String json){
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        String iduser = jsonObject.get("user").getAsString();
        String emailGroup = jsonObject.get("group").getAsString();

        Usuari usuari = usuariService.findById(Long.valueOf(iduser));
        GrupCorreu grupCorreu = grupCorreuService.findByEmail(emailGroup);

        grupCorreuService.insertUsuari(grupCorreu, usuari);
        gSuiteService.createMember(usuari.getGsuiteEmail(), grupCorreu.getGsuiteEmail());

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Usuari "+usuari.getGsuiteEmail()+" afegit correctament al grup");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }

    @PostMapping("/grupcorreu/removemember")
    @Transactional
    public ResponseEntity<Notificacio> removeMember(@RequestBody String json){
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        String iduser = jsonObject.get("user").getAsString();
        String emailGroup = jsonObject.get("group").getAsString();

        Usuari usuari = usuariService.findById(Long.valueOf(iduser));
        GrupCorreu grupCorreu = grupCorreuService.findByEmail(emailGroup);

        grupCorreuService.esborrarUsuari(grupCorreu, usuari);
        gSuiteService.deleteMember(usuari.getGsuiteEmail(), grupCorreu.getGsuiteEmail());

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Usuari "+usuari.getGsuiteEmail()+" esborrat correctament del grup");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }

    @PostMapping("/grupcorreu/desar")
    @Transactional
    public ResponseEntity<Notificacio> saveGrup(@RequestBody GrupCorreu grupCorreu) throws GeneralSecurityException, IOException {
        if (grupCorreu.getIdgrup() == null) {
            grupCorreu.setGrupCorreuTipus(GrupCorreuTipus.GENERAL);
            gSuiteService.createGroup(grupCorreu.getGsuiteEmail(), grupCorreu.getGsuiteNom(), grupCorreu.getGsuiteDescripcio());
        } else {
            gSuiteService.updateGroup(grupCorreu.getGsuiteEmail(), grupCorreu.getGsuiteNom(), grupCorreu.getGsuiteDescripcio());
        }

        GrupCorreu grupCorreuSaved = grupCorreuService.save(grupCorreu);

        //Esborrem els usuaris del grup de correu
        grupCorreuService.esborrarUsuarisGrupCorreu(grupCorreuSaved);

        //Esborrem els grups de correu del grup de correu
        grupCorreuService.esborrarGrupsCorreuGrupCorreu(grupCorreuSaved);

        //Tornem a inserir els usuaris
        for (Usuari usuari : grupCorreu.getUsuaris()) {
            grupCorreuService.insertUsuari(grupCorreuSaved, usuari);
        }

        //Tornem a inserir els grups
        for (GrupCorreu grupCorreuMember : grupCorreu.getGrupCorreus()) {
            grupCorreuService.insertGrup(grupCorreuSaved, grupCorreuMember);
        }

        grupCorreuService.save(grupCorreuSaved);

        //Sincronitzem amb GSuite
        List<Member> members = gSuiteService.getMembers(grupCorreuSaved.getGsuiteEmail());

        for (Member member : members) {
            gSuiteService.deleteMember(member.getEmail(), grupCorreuSaved.getGsuiteEmail());
        }

        //Tornem a inserir els usuaris
        for (Usuari usuari : grupCorreuSaved.getUsuaris()) {
            gSuiteService.createMember(usuari.getGsuiteEmail(), grupCorreuSaved.getGsuiteEmail());
        }

        //Tornem a inserir els grups
        for (GrupCorreu grupCorreuMember : grupCorreuSaved.getGrupCorreus()) {
            gSuiteService.createMember(grupCorreuMember.getGsuiteEmail(), grupCorreuSaved.getGsuiteEmail());
        }


        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Grup desat correctament");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }

    @PostMapping("/grupcorreu/autoemplenar")
    @Transactional
    public ResponseEntity<Notificacio> autoemplenaGrup(@RequestBody GrupCorreu grupCorreu) throws GeneralSecurityException, IOException {
        if (grupCorreu.getIdgrup() == null) {
            grupCorreu.setGrupCorreuTipus(GrupCorreuTipus.GENERAL);
            gSuiteService.createGroup(grupCorreu.getGsuiteEmail(), grupCorreu.getGsuiteNom(), grupCorreu.getGsuiteDescripcio());
        } else {
            gSuiteService.updateGroup(grupCorreu.getGsuiteEmail(), grupCorreu.getGsuiteNom(), grupCorreu.getGsuiteDescripcio());
        }

        GrupCorreu grupCorreuSaved = grupCorreuService.save(grupCorreu);
        List<Usuari> usuarisGrup = new ArrayList<>();
        if (grupCorreu.getGrupCorreuTipus().equals(GrupCorreuTipus.ALUMNAT)) {
            List<Usuari> alumnes = usuariService.findUsuarisByGestibGrup(grupCorreuSaved.getGestibGrup());
            usuarisGrup.addAll(alumnes.stream().filter(alumne->alumne.getActiu()).collect(Collectors.toList()));
        } else if (grupCorreu.getGrupCorreuTipus().equals(GrupCorreuTipus.PROFESSORAT)) {
            List<Usuari> professors = usuariService.findProfessors();
            for (Usuari profe : professors) {
                System.out.println("Usuari:" + profe.getGsuiteFullName() + "Email:" + profe.getGsuiteEmail());
                List<Sessio> sessions = sessioService.findSessionsProfessor(profe);
                Set<String> grupsProfe = new HashSet<>();
                for (Sessio sessio : sessions) {
                    String codiGrup = sessio.getGestibGrup();
                    if (codiGrup != null && !codiGrup.isEmpty()) {
                        grupsProfe.add(codiGrup);
                    }
                }
                for (String grupProfe : grupsProfe) {
                    if (grupProfe.equals(grupCorreuSaved.getGestibGrup()) && profe.getActiu()) {
                        usuarisGrup.add(profe);
                    }
                }
            }
        } else if (grupCorreu.getGrupCorreuTipus().equals(GrupCorreuTipus.TUTORS_FCT)) {
            List<Usuari> professors = usuariService.findProfessors();
            for (Usuari profe : professors) {
                System.out.println("Usuari:" + profe.getGsuiteFullName() + "Email:" + profe.getGsuiteEmail());
                List<Sessio> sessions = sessioService.findSessionsProfessor(profe);
                for (Sessio sessio : sessions) {
                    String codiGestibSubmateria = sessio.getGestibSubmateria();
                    if (codiGestibSubmateria != null && !codiGestibSubmateria.isEmpty()) {
                        Submateria submateria = submateriaService.findByGestibIdentificador(codiGestibSubmateria);

                        if (submateria != null && submateria.getGestibNom() != null && submateria.getGestibNomCurt() != null &&
                                (
                                        submateria.getGestibNom().contains("Formació en centres de treball") ||
                                                submateria.getGestibNom().contains("FCT") ||
                                                submateria.getGestibNomCurt().contains("Formació en centres de treball") ||
                                                submateria.getGestibNomCurt().contains("FCT")
                                )
                                && profe.getActiu()
                        ) {
                            usuarisGrup.add(profe);
                        }
                    }
                    String codiGestibActivitat = sessio.getGestibActivitat();
                    if (codiGestibActivitat != null && !codiGestibActivitat.isEmpty()) {
                        Activitat activitat = activitatService.findByGestibIdentificador(codiGestibActivitat);

                        if (activitat != null && activitat.getGestibNom() != null && activitat.getGestibNomCurt() != null &&
                                (
                                        activitat.getGestibNom().contains("Formació en centres de treball") ||
                                                activitat.getGestibNom().contains("FCT") ||
                                                activitat.getGestibNomCurt().contains("Formació en centres de treball") ||
                                                activitat.getGestibNomCurt().contains("FCT")

                                )
                                && profe.getActiu()
                        ) {
                            usuarisGrup.add(profe);
                        }
                    }
                }
            }
        } else if (grupCorreu.getGrupCorreuTipus().equals(GrupCorreuTipus.DEPARTAMENT)) {
            List<Usuari> professors = usuariService.findProfessors();
            for (Usuari profe : professors) {
                String departamentCodi = profe.getGestibDepartament();
                Departament departament = departamentService.findByGestibIdentificador(departamentCodi);

                if(departament != null && departament.getGsuiteEmail() != null && departament.getGsuiteEmail().equals(grupCorreuSaved.getGsuiteEmail()) && profe.getActiu()){
                    usuarisGrup.add(profe);
                }
            }
        }

        //Esborrem els usuaris del grup de correu
        grupCorreuService.esborrarUsuarisGrupCorreu(grupCorreuSaved);

        //Esborrem els grups de correu del grup de correu
        grupCorreuService.esborrarGrupsCorreuGrupCorreu(grupCorreuSaved);

        //Inserim usuaris del grup
        for (Usuari usuari : usuarisGrup) {
            grupCorreuService.insertUsuari(grupCorreuSaved, usuari);
        }

        grupCorreuService.save(grupCorreuSaved);

        //Sincronitzem amb GSuite
        List<Member> members = gSuiteService.getMembers(grupCorreuSaved.getGsuiteEmail());

        for (Member member : members) {
            gSuiteService.deleteMember(member.getEmail(), grupCorreuSaved.getGsuiteEmail());
        }

        //Tornem a inserir els usuaris
        for (Usuari usuari : usuarisGrup) {
            gSuiteService.createMember(usuari.getGsuiteEmail(), grupCorreuSaved.getGsuiteEmail());
        }


        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Grup autoemplenat correctament");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }

}