package cat.iesmanacor.gestibgsuite.api;

import cat.iesmanacor.gestibgsuite.manager.*;
import cat.iesmanacor.gestibgsuite.model.Notificacio;
import cat.iesmanacor.gestibgsuite.model.NotificacioTipus;
import cat.iesmanacor.gestibgsuite.model.gestib.*;
import cat.iesmanacor.gestibgsuite.model.google.Calendari;
import cat.iesmanacor.gestibgsuite.model.google.GrupCorreu;
import cat.iesmanacor.gestibgsuite.model.google.GrupCorreuTipus;
import com.google.api.client.util.ArrayMap;
import com.google.api.services.directory.model.Group;
import com.google.api.services.directory.model.Member;
import com.google.api.services.directory.model.User;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.sun.istack.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.security.GeneralSecurityException;
import java.text.Normalizer;
import java.util.*;

@RestController
@Slf4j
public class SincronitzacioController {

    @Autowired
    private CentreService centreService;

    @Autowired
    private UsuariService usuariService;

    @Autowired
    private CursService cursService;

    @Autowired
    private GrupService grupService;

    @Autowired
    private SessioService sessioService;

    @Autowired
    private DepartamentService departamentService;

    @Autowired
    private ActivitatService activitatService;

    @Autowired
    private SubmateriaService submateriaService;

    @Autowired
    private GrupCorreuService grupCorreuService;

    @Autowired
    private GSuiteService gSuiteService;

    @Autowired
    private GMailService gMailService;

    @Autowired
    private CalendariService calendariService;

    @Value("${centre.usuaris.passwordinicial}")
    private String passwordInicial;

    @PostMapping("/sync/uploadfile")
    @Transactional
    public ResponseEntity<Notificacio> uploadFitxerGestib(HttpServletRequest request) {
        try {
            Part filePart = request.getPart("arxiu");

            InputStream is = filePart.getInputStream();

            // Reads the file into memory
            /*
             * Path path = Paths.get(audioPath); byte[] data = Files.readAllBytes(path);
             * ByteString audioBytes = ByteString.copyFrom(data);
             */
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] readBuf = new byte[4096];
            while (is.available() > 0) {
                int bytesRead = is.read(readBuf);
                os.write(readBuf, 0, bytesRead);
            }

            // Passam l'arxiu a dins una carpeta
            String fileName = "/tmp/arxiu.xml";

            OutputStream outputStream = new FileOutputStream(fileName);
            os.writeTo(outputStream);

            File f = new File(fileName);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            Document doc;

            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(f);
            doc.getDocumentElement().normalize();

            //Comprovacions de seguretat
            String codiCentre = doc.getDocumentElement().getAttributeNode("codi").getValue();
            String anyAcademicCentre = doc.getDocumentElement().getAttributeNode("any").getValue();

            Centre centre = centreService.findByIdentificador(codiCentre);
            if (!centre.getIdentificador().equals(codiCentre) || !centre.getAnyAcademic().equals(anyAcademicCentre)) {
                Notificacio notificacio = new Notificacio();
                notificacio.setNotifyMessage("L'arxiu XML no correspon amb el centre o any acadèmic de la base de dades. Centre:" + centre.getIdentificador() + " - Any acadèmic:" + centre.getAnyAcademic());
                notificacio.setNotifyType(NotificacioTipus.ERROR);

                return new ResponseEntity<>(notificacio, HttpStatus.CONFLICT);
            }

            //Actualitzem el flag
            centre.setSincronitzar(true);
            centreService.save(centre);

            Notificacio notificacio = new Notificacio();
            notificacio.setNotifyMessage("Arxiu carregat correctament. Les dades s'actualitzaran en 24 hores.");
            notificacio.setNotifyType(NotificacioTipus.SUCCESS);

            return new ResponseEntity<>(notificacio, HttpStatus.OK);
        } catch (IOException | ServletException | ParserConfigurationException | SAXException e) {
            Notificacio notificacio = new Notificacio();
            notificacio.setNotifyMessage(e.getMessage());
            notificacio.setNotifyType(NotificacioTipus.ERROR);

            return new ResponseEntity<>(notificacio, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/sync/reassignarGrups")
    @Transactional
    public ResponseEntity<Notificacio> reassignarGrups(@RequestBody List<Usuari> usuaris) throws GeneralSecurityException, IOException, MessagingException {
        List<Usuari> professors = new ArrayList<>();
        List<Usuari> alumnes = new ArrayList<>();

        for (Usuari usuari : usuaris) {
            if (usuari.getGestibProfessor() != null && usuari.getGestibProfessor()) {
                professors.add(usuari);
            } else if (usuari.getGestibAlumne() != null && usuari.getGestibAlumne()) {
                alumnes.add(usuari);
            }
        }
        this.reassignarGrupsProfessor(professors);
        this.reassignarGrupsAlumne(alumnes);

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Reassignació de grups d'usuaris finalitzat correctament");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);

        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }

    @PostMapping("/sync/reassignarGrupsProfessors")
    @Transactional
    public ResponseEntity<Notificacio> reassignarGrupsProfessorsTots() throws GeneralSecurityException, IOException {
        List<Usuari> professors = usuariService.findProfessors();
        this.reassignarGrupsProfessor(professors);

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Reassignació de grups d'equips educatius finalitzat correctament");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);

        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }

    @GetMapping("/sync/reassignarGrupsAlumnes")
    @Transactional
    public ResponseEntity<Notificacio> reassignarGrupsAlumnesTots() throws GeneralSecurityException, IOException, MessagingException {
        List<Usuari> alumnes = usuariService.findAlumnes(false);
        this.reassignarGrupsAlumne(alumnes);

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Reassignació de grups d'alumnes finalitzat correctament");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);

        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }

    @GetMapping("/sync/reassignarGrupsCorreuGSuiteToDatabase")
    @Transactional
    public ResponseEntity<Notificacio> reassignarGrupsCorreuGSuiteToDatabase() throws GeneralSecurityException, IOException, MessagingException {
        this.updateGrupsCorreuGSuiteToDatabase();

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Reassignació de grups de correus de GSuite a la BBDD finalitzat correctament");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);

        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }

    /*
    Second Minute Hour Day-of-Month
    second, minute, hour, day(1-31), month(1-12), weekday(1-7) SUN-SAT
    0 0 2 * * * = a les 2AM de cada dia
     */
    @Scheduled(cron = "0 0 2 * * *")
    @PostMapping("/sync/sincronitza")
    @Transactional
    public void scheduleTaskUsingCronExpression() throws MessagingException, GeneralSecurityException, IOException {
        List<Centre> centres = centreService.findAll();
        Centre centre = centres.get(0);

        if (centre.getSincronitzar()) {
            List<Usuari> usuarisNoActiusBeforeSync = usuariService.findUsuarisNoActius();
            if (usuarisNoActiusBeforeSync == null) {
                usuarisNoActiusBeforeSync = new ArrayList<>();
            }
            this.desactivarUsuaris();
            log.info("Actualitzant XML a la base de dades...");
            List<Usuari> usuarisGestib = this.gestibxmltodatabase(centre, usuarisNoActiusBeforeSync);

            log.info("Actualitzant usuaris GSuite a la base de dades...");
            List<Usuari> usuarisGSuite = this.gsuiteuserstodatabase();

            List<Usuari> usuarisUpdate = new ArrayList<>();
            usuarisUpdate.addAll(usuarisGestib);
            usuarisUpdate.addAll(usuarisGSuite);

            log.info("Creant usuaris nous...");
            this.createNewUsers(usuarisUpdate);

            //log.info("Actualitzant Grups de Correu a la base de dades...");
            //this.createGrupsCorreuGSuiteToDatabase();
            //this.deleteGrupsCorreuGSuiteToDatabase();
            //this.updateGrupsCorreuGSuiteToDatabase();

            log.info("Actualitació de centre. Sincronització acabada");
            this.updateCentre(centre);
        }
    }

    @Transactional
    void desactivarUsuaris() {
        //Desactivar tots els usuaris (activem només els que hi ha dins l'xml)
        usuariService.desactivarUsuaris();
    }

    @Transactional
    void updateCentre(Centre centre) {
        Date ara = new Date();

        centre.setSincronitzar(false);
        centre.setDataSincronitzacio(ara);
        centreService.save(centre);
    }

    @Transactional
    List<Usuari> gestibxmltodatabase(@NotNull Centre centre, List<Usuari> usuarisNoActiusBeforeSync) {
        List<Usuari> usuarisUpdate = new ArrayList<>();

        // Passam l'arxiu a dins una carpeta
        String fileName = "/tmp/arxiu.xml";

        File f = new File(fileName);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(f);
            doc.getDocumentElement().normalize();

            log.info("LLegint el XML del Centre: " + centre.getNom());
            log.info("Començant a processar...");



            //Cursos i Grups
            NodeList nodesCurs = doc.getElementsByTagName("CURS");

            for (int i = 0; i < nodesCurs.getLength(); i++) {
                Node node = nodesCurs.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eCurs = (Element) node;
                    String codiCurs = eCurs.getAttribute("codi");
                    String descCurs = eCurs.getAttribute("descripcio");

                    Curs c = cursService.findByGestibIdentificador(codiCurs);
                    if (c == null) {
                        cursService.save(codiCurs, descCurs);
                    }

                    // Grup
                    NodeList nodesGrup = eCurs.getElementsByTagName("GRUP");

                    for (int j = 0; j < nodesGrup.getLength(); j++) {
                        Node node2 = nodesGrup.item(j);
                        if (node2.getNodeName().equals("GRUP")) {
                            Element eGrup = (Element) node2;
                            String codiGrup = eGrup.getAttribute("codi");
                            String nomGrup = eGrup.getAttribute("nom");
                            String tutor1 = eGrup.getAttribute("tutor");
                            String tutor2 = eGrup.getAttribute("tutor2");
                            String tutor3 = eGrup.getAttribute("tutor3");

                            Grup g = grupService.findByGestibIdentificador(codiGrup);
                            if (g == null) {
                                grupService.save(codiGrup, nomGrup, codiCurs, tutor1, tutor2, tutor3);
                            } else {
                                g.setGestibNom(nomGrup);
                                g.setGestibCurs(codiCurs);
                                g.setGestibTutor1(tutor1);
                                g.setGestibTutor2(tutor2);
                                g.setGestibTutor3(tutor3);
                                grupService.save(g);
                            }
                        }
                    }

                }
            }


            //Professors
            NodeList nodesProfessor = doc.getElementsByTagName("PROFESSOR");
            for (int i = 0; i < nodesProfessor.getLength(); i++) {
                Node node = nodesProfessor.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eProfe = (Element) node;
                    String codi = eProfe.getAttribute("codi");
                    String nom = eProfe.getAttribute("nom");
                    String ap1 = eProfe.getAttribute("ap1");
                    String ap2 = eProfe.getAttribute("ap2");
                    String usuari = eProfe.getAttribute("username");
                    String departament = eProfe.getAttribute("departament");

                    if (departament.length() == 0) {
                        System.out.println("ALERTA! El professor/a no té departament!!!");
                        System.out.println("\t" + codi + "\t" + nom + "\t" + ap1 + "\t" + ap2 + "\t" + usuari + "\t" + departament);
                        System.out.println();
                    }
                    //Podria passar que algú creàs a mà l'usuari a GSuite (correctament) però no haguesin passat l'xml, per tant..
                    //Dit d'una altra manera, si abans estava NO actiu i ara existeix, necessitam actualitzar els grups.
                    Usuari u = usuariService.findByGSuitePersonalID(codi);
                    if (u != null) {
                        //Si estava inactiu i ara passa a actiu
                        if (usuarisNoActiusBeforeSync.contains(u) && u.getGsuiteEmail() != null) {
                            System.out.println("Entra usuari" + u.getGsuiteEmail() + " 1");
                            usuarisUpdate.add(u);
                        }
                        usuariService.saveGestib(u, codi, nom, ap1, ap2, usuari, null, null, departament, true, false);
                    }

                    u = usuariService.findByGestibCodi(codi);
                    if (u == null) {
                        usuariService.saveGestib(codi, nom, ap1, ap2, usuari, null, null, departament, true, false);
                    } else {
                        //Si estava inactiu i ara passa a actiu
                        if (usuarisNoActiusBeforeSync.contains(u) && u.getGsuiteEmail() != null) {
                            System.out.println("Entra usuari" + u.getGsuiteEmail() + " 2");
                            usuarisUpdate.add(u);
                        }
                        u.setActiu(true);
                        usuariService.save(u);
                    }

                }
            }

            //Alumnes

            //Esborrem els grups, els tornarem a calcular

            //Fem una còpia dels alumnes en aquest estat per saber després si ha canviat el grup original o no.
            List<Usuari> alumnesOld = new ArrayList<>();
            List<Usuari> alumnes = usuariService.findAlumnes(false);
            for (Usuari alumne : alumnes) {
                alumnesOld.add(alumne.clone());
                alumne.setGestibGrup(null);
                alumne.setGestibGrup2(null);
                alumne.setGestibGrup3(null);
                usuariService.save(alumne);
            }

            NodeList nodesAlumne = doc.getElementsByTagName("ALUMNE");

            for (int i = 0; i < nodesAlumne.getLength(); i++) {
                Node node = nodesAlumne.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eAlumne = (Element) node;
                    String codi = eAlumne.getAttribute("codi");
                    String nom = eAlumne.getAttribute("nom");
                    String ap1 = eAlumne.getAttribute("ap1");
                    String ap2 = eAlumne.getAttribute("ap2");
                    String exp = eAlumne.getAttribute("expedient");
                    String grup = eAlumne.getAttribute("grup");

                    //Podria passar que algú creàs a mà l'usuari a GSuite (correctament) però no haguesin passat l'xml, per tant...
                    Usuari u = usuariService.findByGSuitePersonalID(codi);
                    if (u != null) {
                        //Si estava inactiu i ara passa a actiu
                        if (usuarisNoActiusBeforeSync.contains(u) && u.getGsuiteEmail() != null) {
                            System.out.println("Entra usuari" + u.getGsuiteEmail() + " 3");
                            usuarisUpdate.add(u);
                        }

                        //Si ha canviat de grup l'actualitzem
                        Long idusuari = u.getIdusuari();
                        if (!this.usuariTeGrup(alumnesOld.stream().filter(a -> a.getIdusuari().equals(idusuari)).findFirst().orElse(null), grup) && u.getGsuiteEmail() != null) {
                            System.out.println("L'alumne " + u.getGsuiteEmail() + " ha canviat de grup 1");
                            //Actualitzem el grup
                            usuarisUpdate.add(u);
                        }

                        Usuari alumne = usuariService.saveGestib(u, codi, nom, ap1, ap2, null, exp, grup, null, false, true);

                        //Creem nom d'usuari
                        String usuari = this.generateUsernameAlumne(alumne);
                        alumne.setGestibUsername(usuari);

                        usuariService.save(alumne);
                    }

                    u = usuariService.findByGestibCodi(codi);
                    if (u == null) {
                        Usuari alumne = usuariService.saveGestib(codi, nom, ap1, ap2, null, exp, grup, null, false, true);

                        //Creem nom d'usuari
                        String usuari = this.generateUsernameAlumne(alumne);
                        alumne.setGestibUsername(usuari);
                        usuariService.save(alumne);
                    } else {
                        //Si estava inactiu i ara passa a actiu
                        if (usuarisNoActiusBeforeSync.contains(u) && u.getGsuiteEmail() != null) {
                            System.out.println("Entra usuari" + u.getGsuiteEmail() + " 4");
                            usuarisUpdate.add(u);
                        }

                        //Si ha canviat de grup l'actualitzem
                        Long idusuari = u.getIdusuari();
                        if (!this.usuariTeGrup(alumnesOld.stream().filter(a -> a.getIdusuari().equals(idusuari)).findFirst().orElse(null), grup) && u.getGsuiteEmail() != null) {
                            System.out.println("L'alumne " + u.getGsuiteEmail() + " ha canviat de grup 1");
                            //Actualitzem el grup
                            usuarisUpdate.add(u);
                        }

                        u.setActiu(true);
                        usuariService.save(u);
                    }


                }
            }

            //Departaments
            NodeList nodesDepartament = doc.getElementsByTagName("DEPARTAMENT");

            for (int i = 0; i < nodesDepartament.getLength(); i++) {
                Node node = nodesDepartament.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eDepartament = (Element) node;
                    String codi = eDepartament.getAttribute("codi");
                    String nom = eDepartament.getAttribute("descripcio");

                    Departament d = departamentService.findByGestibIdentificador(codi);
                    if (d == null) {
                        departamentService.save(codi, nom);
                    }
                }
            }

            //Activitats
            NodeList nodesActivitat = doc.getElementsByTagName("ACTIVITAT");

            for (int i = 0; i < nodesActivitat.getLength(); i++) {
                Node node = nodesActivitat.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eActivitat = (Element) node;
                    String codi = eActivitat.getAttribute("codi");
                    String nom = eActivitat.getAttribute("descripcio");
                    String nomCurt = eActivitat.getAttribute("curta");

                    Activitat a = activitatService.findByGestibIdentificador(codi);
                    if (a == null) {
                        activitatService.save(codi, nom, nomCurt);
                    }
                }
            }

            //Submatèries
            /*
                <SUBMATERIA
                    codi="2066737"
                    curs="62"
                    descripcio="Biologia i geologia-A"
                    curta="BG-A"
                />
             */
            NodeList nodesSubmateria = doc.getElementsByTagName("SUBMATERIA");

            for (int i = 0; i < nodesSubmateria.getLength(); i++) {
                Node node = nodesSubmateria.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eSubmateria = (Element) node;
                    String codi = eSubmateria.getAttribute("codi");
                    String nom = eSubmateria.getAttribute("descripcio");
                    String nomCurt = eSubmateria.getAttribute("curta");
                    String curs = eSubmateria.getAttribute("curs");

                    Submateria s = submateriaService.findByGestibIdentificador(codi);
                    if (s == null) {
                        submateriaService.save(codi, nom, nomCurt, curs);
                    }
                }
            }

            //Sessió
            /*
                Sessió Professors
                <SESSIO
                    professor="XXXXXX"
                    curs="33"
                    grup="3333"
                    dia="3"
                    hora="08:00"
                    durada="55"
                    aula="333"
                    submateria="333333"
                    activitat=""
                    placa="3333"
                />

                Sessió Alumnes
                <SESSIO
                    alumne="XXXXXXX"
                    dia="3"
                    hora="08:00"
                    durada="55"
                    aula="55"
                    submateria="555555"
                />

                Sessió Grup
                <SESSIO
                    curs="563"
                    grup="557859"
                    dia="3"
                    hora="09:50"
                    durada="55"
                    aula="12707"
                    submateria="2065627"
                    activitat=""
                />
             */
            sessioService.deleteAllSessions();
            NodeList nodesSessio = doc.getElementsByTagName("SESSIO");

            for (int i = 0; i < nodesSessio.getLength(); i++) {
                Node node = nodesSessio.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eSessio = (Element) node;
                    String professor = eSessio.getAttribute("professor");
                    String alumne = eSessio.getAttribute("alumne");
                    String curs = eSessio.getAttribute("curs");
                    String grup = eSessio.getAttribute("grup");
                    String dia = eSessio.getAttribute("dia");
                    String hora = eSessio.getAttribute("hora");
                    String durada = eSessio.getAttribute("durada");
                    String aula = eSessio.getAttribute("aula");
                    String submateria = eSessio.getAttribute("submateria");
                    String activitat = eSessio.getAttribute("activitat");
                    String placa = eSessio.getAttribute("placa");

                    sessioService.saveSessio(professor, alumne, curs, grup, dia, hora, durada, aula, submateria, activitat, placa);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException | CloneNotSupportedException ex) {
            System.out.println(ex.getMessage());
        }

        System.out.println("Sincronització correcte");
        return usuarisUpdate;
    }

    @Transactional
    List<Usuari> gsuiteuserstodatabase() throws GeneralSecurityException, IOException {
        List<Usuari> usuarisUpdate = new ArrayList<>();

        //Sincronitzem la BBDD amb la informació de GSuite
        List<User> gsuiteUsers = gSuiteService.getUsers();

        for (User gsuiteUser : gsuiteUsers) {

            String email = gsuiteUser.getPrimaryEmail();
            Boolean isAdmin = gsuiteUser.getIsAdmin();
            Boolean isSuspes = gsuiteUser.getSuspended();
            String givenName = gsuiteUser.getName().getGivenName();
            String familyName = gsuiteUser.getName().getFamilyName();
            String fullName = gsuiteUser.getName().getFullName();
            String unitatOrganitzativa = gsuiteUser.getOrgUnitPath();
            String personalIdKey = "";

            try {
                List personalIDValueOrganization = (ArrayList) gsuiteUser.getExternalIds();
                ArrayMap userKey = (ArrayMap) personalIDValueOrganization.get(0);

                String valueKey = userKey.getKey(0).toString();
                String valueValue = userKey.getValue(0).toString();

                String organizationKey = userKey.getKey(1).toString();
                String organizationValue = userKey.getValue(1).toString();


                //System.out.println(valueKey + "<<->>" + valueValue + "<<->>" + organizationKey + "<<->>" + organizationValue+ "<<->>");

                if (valueKey.equals("value") && organizationKey.equals("type") && organizationValue.equals("organization")) {
                    personalIdKey = valueValue;
                }
            } catch (Exception e) {
            }


            //System.out.println("Personal ID és" + personalIdKey);
            Usuari u = usuariService.findByGestibCodiOrEmail(personalIdKey, email);
            log.info("Personal Key:" + personalIdKey + "E-mail:" + email);

            if (u != null) {
                log.info("Actualitzant usuari " + u.getGestibNom() + " " + u.getGestibCognom1() + " " + u.getGestibCognom2());

                //Si l'han activat a GSuite però no està sincronitzat amb la BBDD cal actualitzar l'usuari
                if (
                        ((u.getGsuiteSuspes() != null && u.getGsuiteSuspes()) || (u.getGsuiteEliminat() != null && u.getGsuiteEliminat()))
                                && !isSuspes && u.getGsuiteEmail() != null) {
                    System.out.println("Entra usuari" + u.getGsuiteEmail() + " 5 <" + u.getGsuiteSuspes() + "<" + u.getGsuiteEliminat() + "<" + isSuspes);
                    usuarisUpdate.add(u);
                }

                u.setGsuiteEmail(email);
                u.setGsuiteAdministrador(isAdmin);
                u.setGsuitePersonalID(personalIdKey);
                u.setGsuiteSuspes(isSuspes);
                u.setGsuiteUnitatOrganitzativa(unitatOrganitzativa);
                u.setGsuiteGivenName(givenName);
                u.setGsuiteFamilyName(familyName);
                u.setGsuiteFullName(fullName);

                //Si és algú del PAS ho podem conèixer perquè no tindrà pesonalIDKey, aleshores ha d'estar actiu però
                //no tindrà usuari Gestib.
                if (personalIdKey == null || personalIdKey.isEmpty()) {
                    u.setActiu(true);
                }

                usuariService.save(u);
            } else {
                log.info("L'usuari no existeix. Creant usuari " + email + " amb clau " + personalIdKey);
                boolean actiu = false;
                if (personalIdKey == null || personalIdKey.isEmpty()) {
                    actiu = true;
                }
                usuariService.saveGSuite(email, isAdmin, personalIdKey, isSuspes, unitatOrganitzativa, givenName, familyName, fullName, actiu);
            }

        }

        log.info("Sincronització correcte amb GSuite cap a la BBDD");

        return usuarisUpdate;
    }

    @Transactional
    void createNewUsers(List<Usuari> usuarisUpdate) throws GeneralSecurityException, IOException, MessagingException {

        List<Usuari> professorsNous = new ArrayList<>();
        List<Usuari> alumnesNous = new ArrayList<>();
        //List<Usuari> professorsUpdate = new ArrayList<>();
        Set<Usuari> professorsUpdate = new HashSet<>();
        //List<Usuari> alumnesUpdate = new ArrayList<>();
        Set<Usuari> alumnesUpdate = new HashSet<>();

        //Els usuaris sense correu són usuaris nous
        List<Usuari> usuarisSenseCorreu = usuariService.findUsuarisSenseCorreu();
        for (Usuari usuari : usuarisSenseCorreu) {
            //Comprovam si està actiu pels usuaris eliminats, no hem de crear el de tothom
            if (usuari.getGestibProfessor() && usuari.getActiu()) {
                String username = usuari.getGsuiteEmail();
                if (username == null) {
                    username = this.generateUsername(usuari);
                    username += "@iesmanacor.cat";
                }
                User usuariGSuite = gSuiteService.createUser(username, usuari.getGestibNom(), usuari.getGestibCognom1() + " " + usuari.getGestibCognom2(), usuari.getGestibCodi(), "/professors");
                log.info("Usuari" + usuariGSuite.getPrimaryEmail() + " creat correctament a GSuite");

                //Actualitzar usuari Gestib
                usuari.setActiu(true);
                usuari.setGsuiteEmail(usuariGSuite.getPrimaryEmail());
                usuari.setGsuiteAdministrador(usuariGSuite.getIsAdmin());
                usuari.setGsuitePersonalID(usuari.getGestibCodi());
                usuari.setGsuiteSuspes(usuariGSuite.getSuspended());
                usuari.setGsuiteUnitatOrganitzativa(usuariGSuite.getOrgUnitPath());
                usuari.setGsuiteGivenName(usuariGSuite.getName().getGivenName());
                usuari.setGsuiteFamilyName(usuariGSuite.getName().getFamilyName());
                usuari.setGsuiteFullName(usuariGSuite.getName().getFullName());

                usuariService.save(usuari);

                professorsNous.add(usuari);
            } else if (usuari.getGestibAlumne() && usuari.getActiu()) {
                //Username
                String username = usuari.getGsuiteEmail();
                if (username == null) {
                    username = this.generateUsername(usuari);
                    username += "@alumnes.iesmanacor.cat";
                }

                //Unitat organitzativa de l'alumne
                Grup grup = grupService.findByGestibIdentificador(usuari.getGestibGrup());
                Curs curs = null;
                if (grup != null) {
                    curs = cursService.findByGestibIdentificador(grup.getGestibCurs());
                    String rutaUnitat = "/alumnes/";
                    if (curs.getGestibNom().toLowerCase().contains("eso")) {
                        rutaUnitat += "eso/" + curs.getGsuiteUnitatOrganitzativa();
                    } else if (curs.getGestibNom().toLowerCase().contains("batx")) {
                        rutaUnitat += "batx/" + curs.getGsuiteUnitatOrganitzativa();
                    } else {
                        rutaUnitat += "fp/" + curs.getGsuiteUnitatOrganitzativa();
                    }

                    log.info(username + "---" + usuari.getGestibNom() + "---" + usuari.getGestibCognom1() + " " + usuari.getGestibCognom2() + " " + curs.getGsuiteUnitatOrganitzativa() + "---" + usuari.getGestibCodi() + "---" + rutaUnitat);
                    User usuariGSuite = gSuiteService.createUser(username, usuari.getGestibNom(), usuari.getGestibCognom1() + " " + usuari.getGestibCognom2() + " " + curs.getGsuiteUnitatOrganitzativa(), usuari.getGestibCodi(), rutaUnitat);
                    System.out.println("Usuari" + usuariGSuite.getPrimaryEmail() + " creat correctament");
                    log.info("Usuari" + usuariGSuite.getPrimaryEmail() + " creat correctament a GSuite com alumne");

                    //Actualitzar usuari Gestib
                    usuari.setActiu(true);
                    usuari.setGsuiteEmail(usuariGSuite.getPrimaryEmail());
                    usuari.setGsuiteAdministrador(usuariGSuite.getIsAdmin());
                    usuari.setGsuitePersonalID(usuari.getGestibCodi());
                    usuari.setGsuiteSuspes(usuariGSuite.getSuspended());
                    usuari.setGsuiteUnitatOrganitzativa(usuariGSuite.getOrgUnitPath());
                    usuari.setGsuiteGivenName(usuariGSuite.getName().getGivenName());
                    usuari.setGsuiteFamilyName(usuariGSuite.getName().getFamilyName());
                    usuari.setGsuiteFullName(usuariGSuite.getName().getFullName());

                    usuariService.save(usuari);

                    alumnesNous.add(usuari);
                }
            } else {
                System.out.println("Error a l'usuari " + usuari.getGestibNom() + usuari.getGestibCognom1());
            }
        }

        //També pot passar que hi hagi alumnes o professors (sobretot professors) que ja tinguin compte però
        // estàssin deshabilitats a GSuite i tornin al centre
        List<Usuari> usuarisSuspesos = usuariService.findUsuarisGSuiteSuspesos();
        List<Usuari> usuarisActius = usuariService.findUsuarisActius();

        for (Usuari usuariGestib : usuarisActius) {
            for (Usuari usuariSuspes : usuarisSuspesos) {
                if (usuariGestib.getIdusuari().equals(usuariSuspes.getIdusuari())) {
                    usuariSuspes.setGsuiteEliminat(false);
                    usuariSuspes.setGsuiteSuspes(false);
                    usuariSuspes.setActiu(true);
                    usuariService.save(usuariSuspes);

                    //Actualitzem GSuite
                    User usuariGsuite = gSuiteService.getUserById(usuariSuspes.getGsuiteEmail());
                    if (usuariGsuite != null) {
                        gSuiteService.suspendreUser(usuariSuspes.getGsuiteEmail(), false);
                    }

                    if (usuariGestib != null && usuariGestib.getGestibProfessor() != null && usuariGestib.getGestibProfessor()) {
                        professorsNous.add(usuariGestib);
                    } else if (usuariGestib != null && usuariGestib.getGestibAlumne() != null && usuariGestib.getGestibAlumne()) {
                        alumnesNous.add(usuariGestib);
                    }
                }
            }
        }

        //També pot passar que hagin habilitat l'usuari a GSuite i no estigui sincronitzat amb la BBDD. Actualitzem els usuaris
        for (Usuari usuari : usuarisUpdate) {
            usuari.setGsuiteEliminat(false);
            usuari.setGsuiteSuspes(false);
            usuari.setActiu(true);
            usuariService.save(usuari);

            if (usuari != null && usuari.getGestibProfessor() != null && usuari.getGestibProfessor()) {
                professorsUpdate.add(usuari);
            } else if (usuari != null && usuari.getGestibAlumne() != null && usuari.getGestibAlumne()) {
                alumnesUpdate.add(usuari);
            }
        }


        if (professorsNous != null && !professorsNous.isEmpty()) {
            log.info("Hi ha " + professorsNous.size() + " profes nous");
            //Reassignar equips educatius
            this.reassignarGrupsProfessor(professorsNous);

            //Notificacions
            StringBuilder body = new StringBuilder();
            for (Usuari professor : professorsNous) {
                body.append(professor.getGsuiteFullName()).append(" correu: ").append(professor.getGsuiteEmail()).append(" - Contrasenya: ").append(this.passwordInicial).append("\n\n");
            }
            /* TODO: FALTA AVISAR AL CAP DE DEPARTAMENT */
            gMailService.sendMessage("Nous professors donats d'alta a GSuite", body.toString(), "jgalmes1@iesmanacor.cat");
            //gMailService.sendMessage("Nous professors donats d'alta a GSuite", body, "directiva@iesmanacor.cat");
            //gMailService.sendMessage("Nous professors donats d'alta a GSuite", body, "mduran@iesmanacor.cat");
        }

        if (alumnesNous != null && !alumnesNous.isEmpty()) {
            log.info("Hi ha " + alumnesNous.size() + " alumnes nous");
            //Reassignar grups
            this.reassignarGrupsAlumne(alumnesNous);

            //Notificacions
            StringBuilder bodyAll = new StringBuilder();
            Multimap<Usuari, String> tutors = LinkedHashMultimap.create();

            for (Usuari alumne : alumnesNous) {
                String infoAlumne = alumne.getGsuiteFullName() + " correu: " + alumne.getGsuiteEmail() + " - Contrasenya: " + this.passwordInicial + "\n\n";

                Grup grup = grupService.findByGestibIdentificador(alumne.getGestibGrup());
                Usuari tutor1 = usuariService.findByGestibCodi(grup.getGestibTutor1());
                Usuari tutor2 = usuariService.findByGestibCodi(grup.getGestibTutor2());
                Usuari tutor3 = usuariService.findByGestibCodi(grup.getGestibTutor3());

                if (tutor1 != null) {
                    tutors.put(tutor1, infoAlumne);
                }

                if (tutor2 != null) {
                    tutors.put(tutor2, infoAlumne);
                }

                if (tutor3 != null) {
                    tutors.put(tutor3, infoAlumne);
                }
            }


            Map<Usuari, Collection<String>> tutorsMap = tutors.asMap();

            for (Usuari tutor : tutors.keySet()) {
                StringBuilder alumnesTutor = new StringBuilder();

                for (String missatge : tutors.get(tutor)) {
                    alumnesTutor.append(missatge);
                }

                bodyAll.append(alumnesTutor);
                //gMailService.sendMessage("Nous alumnes donats d'alta a GSuite", alumnesTutor, tutor.getGsuiteEmail());
            }

            gMailService.sendMessage("Nous alumnes donats d'alta a GSuite", bodyAll.toString(), "jgalmes1@iesmanacor.cat");
            //gMailService.sendMessage("Nous alumnes donats d'alta a GSuite", bodyAll, "directiva@iesmanacor.cat");
        }


        if (professorsUpdate != null && !professorsUpdate.isEmpty()) {
            log.info("Hi ha " + professorsUpdate.size() + " profes a actualitzar");
            //Reassignar equips educatius
            List<Usuari> professorsUpdateList = new ArrayList<>(professorsUpdate);
            this.reassignarGrupsProfessor(professorsUpdateList);

            //Notificacions
            StringBuilder body = new StringBuilder();
            for (Usuari professor : professorsUpdate) {
                body.append(professor.getGsuiteFullName()).append(" correu: ").append(professor.getGsuiteEmail()).append("\n\n");
            }
            gMailService.sendMessage("Nous professors actualitzats a GSuite", body.toString(), "jgalmes1@iesmanacor.cat");
        }

        if (alumnesUpdate != null && !alumnesUpdate.isEmpty()) {
            log.info("Hi ha " + alumnesUpdate.size() + " alumnes a actualitzar");
            //Reassignar grups
            List<Usuari> alumnesUpdateList = new ArrayList<>(alumnesUpdate);
            this.reassignarGrupsAlumne(alumnesUpdateList);

            //Notificacions
            StringBuilder bodyAll = new StringBuilder();

            for (Usuari alumne : alumnesUpdate) {
                bodyAll.append(alumne.getGsuiteFullName())
                        .append(" correu: ")
                        .append(alumne.getGsuiteEmail())
                        .append("\n\n");
            }

            gMailService.sendMessage("Nous alumnes actualitzats a GSuite", bodyAll.toString(), "jgalmes1@iesmanacor.cat");
        }

        log.info("El procés de nous usuaris creats i actualitzats ha finalitzat correctament");
        gMailService.sendMessage("El procés de nous usuaris creats i actualitzats ha finalitzat correctament", "El procés de nous usuaris creats i actualitzats ha finalitzat correctament", "jgalmes1@iesmanacor.cat");
    }

    @Transactional
    void createGrupsCorreuGSuiteToDatabase() throws GeneralSecurityException, IOException {
        List<Group> groups = gSuiteService.getGroups();

        for (Group grup : groups) {

            GrupCorreu grupCorreu = grupCorreuService.findByEmail(grup.getEmail());

            if (grupCorreu == null) {
                //Creem el grup de correu a la BBDD
                grupCorreuService.save(null, grup.getName(), grup.getEmail(), grup.getDescription(), GrupCorreuTipus.GENERAL);
            }
        }

        System.out.println("Acaba creació grups de correu.");
    }

    @Transactional
    void deleteGrupsCorreuGSuiteToDatabase() throws GeneralSecurityException, IOException {
        List<Group> groups = gSuiteService.getGroups();

        List<GrupCorreu> grupsCorreu = grupCorreuService.findAll();

        //Esborrem a la base de dades els grups que NO siguin a GSuite
        for (GrupCorreu grupCorreu : grupsCorreu) {
            //Inicialitzem els grups de correu i usuaris (EAGER) per evitar sortir del context (Error No Session)
            Hibernate.initialize(grupCorreu);
            Hibernate.initialize(grupCorreu.getGrupCorreus());
            Hibernate.initialize(grupCorreu.getUsuaris());

            boolean trobat = false;
            for (Group grup : groups) {
                if (grupCorreu.getGsuiteEmail().equals(grup.getEmail())) {
                    trobat = true;
                }
            }
            if (!trobat) {
                //Esborrem els usuaris del grup de correu
                grupCorreuService.esborrarUsuarisGrupCorreu(grupCorreu);

                //Esborrem els grups de correu del grup de correu
                grupCorreuService.esborrarGrupsCorreuGrupCorreu(grupCorreu);

                //Esborrem el grup
                grupCorreuService.esborrarGrup(grupCorreu);
            }
        }

        System.out.println("Acaba esborrat grups de correu.");

    }

    @Transactional
    void updateGrupsCorreuGSuiteToDatabase() throws GeneralSecurityException, IOException {

        List<GrupCorreu> grupsCorreu = grupCorreuService.findAll();

        for (GrupCorreu grupCorreu : grupsCorreu) {
            System.out.println("PROCESSANT GRUP" + grupCorreu.getGsuiteEmail());

            //Inicialitzem els grups de correu i usuaris (EAGER) per evitar sortir del context (Error No Session)
            Hibernate.initialize(grupCorreu);
            Hibernate.initialize(grupCorreu.getGrupCorreus());
            Hibernate.initialize(grupCorreu.getUsuaris());

            //Esborrem els usuaris del grup de correu
            grupCorreuService.esborrarUsuarisGrupCorreu(grupCorreu);

            //Esborrem els grups de correu del grup de correu
            grupCorreuService.esborrarGrupsCorreuGrupCorreu(grupCorreu);

            List<Member> members = gSuiteService.getMembers(grupCorreu.getGsuiteEmail());

            for (Member member : members) {
                Usuari usuari = usuariService.findByEmail(member.getEmail());
                GrupCorreu grupCorreuMember = grupCorreuService.findByEmail(member.getEmail());
                if (usuari != null) {
                    grupCorreuService.insertUsuari(grupCorreu, usuari);
                }
                if (grupCorreuMember != null) {
                    grupCorreuService.insertGrup(grupCorreu, grupCorreuMember);
                }
            }

            grupCorreuService.save(grupCorreu);
        }
        System.out.println("Acaba processat grups de correu.");
    }

    @Transactional
    void reassignarGrupsProfessor(List<Usuari> professors) throws GeneralSecurityException, IOException {
        final String PREFIX = "ee.";

        //Esborrem equips educatius
        log.info("Esborrem equips educatius");
        for (Usuari profe : professors) {
            System.out.println("Usuari:" + profe.getGsuiteFullName() + "Email:" + profe.getGsuiteEmail());
            List<Group> grupsProfe = gSuiteService.getUserGroups(profe.getGsuiteEmail());
            for (Group grupProfe : grupsProfe) {
                GrupCorreu grupCorreu = grupCorreuService.findByEmail(grupProfe.getEmail());
                System.out.println("Grup correu:" + grupCorreu.getGrupCorreuTipus().toString() + " - " + grupCorreu.getGsuiteEmail());
                //Només esborrem els grups d'alumnes i professors
                if (grupCorreu.getGrupCorreuTipus().equals(GrupCorreuTipus.PROFESSORAT) ||
                        grupCorreu.getGrupCorreuTipus().equals(GrupCorreuTipus.ALUMNAT)
                ) {
                    log.info("Esborrant grup de correu" + grupProfe.getEmail() + " de l'usuari " + profe.getGsuiteEmail());
                    gSuiteService.deleteMember(profe.getGsuiteEmail(), grupProfe.getEmail());
                }
            }
        }

        //Tornem a crear els grups de profe
        for (Usuari usuari : professors) {
            //Comprovam si està actiu pels usuaris eliminats, no hem de crear el de tothom
            if (usuari.getGestibProfessor() && usuari.getActiu()) {
                //Afegir al departament
                if (usuari.getGestibDepartament() != null && !usuari.getGestibDepartament().isEmpty()) {
                    Departament departamentUsuari = departamentService.findByGestibIdentificador(usuari.getGestibDepartament());
                    if (departamentUsuari != null && departamentUsuari.getGsuiteEmail() != null && !departamentUsuari.getGsuiteEmail().isEmpty()) {
                        gSuiteService.createMember(usuari.getGsuiteEmail(), departamentUsuari.getGsuiteEmail());
                        System.out.println("Usuari " + usuari.getGsuiteEmail() + " afegit al grup " + departamentUsuari.getGsuiteEmail());
                        log.info("Usuari " + usuari.getGsuiteEmail() + " afegit al grup " + departamentUsuari.getGsuiteEmail());
                    } else {
                        log.error("El departament amb codi " + usuari.getGestibDepartament() + " no existeix.");
                    }
                }

                //Grup de tutors
                List<Grup> grupsTutoria = grupService.findByTutor(usuari);

                //Esborrem tutors
                gSuiteService.deleteMember(usuari.getGsuiteEmail(), "tutoria1i2@iesmanacor.cat");
                gSuiteService.deleteMember(usuari.getGsuiteEmail(), "tutoria3i4@iesmanacor.cat");
                gSuiteService.deleteMember(usuari.getGsuiteEmail(), "tutoria.eso@iesmanacor.cat");
                gSuiteService.deleteMember(usuari.getGsuiteEmail(), "tutoria.batxillerat@iesmanacor.cat");
                gSuiteService.deleteMember(usuari.getGsuiteEmail(), "tutoria.fpb@iesmanacor.cat");
                gSuiteService.deleteMember(usuari.getGsuiteEmail(), "tutoria.fp@iesmanacor.cat");
                if (grupsTutoria != null && !grupsTutoria.isEmpty()) {
                    for (Grup grup : grupsTutoria) {
                        Curs curs = cursService.findByGestibIdentificador(grup.getGestibCurs());
                        if (curs.getGestibNom().toLowerCase().contains("eso")) {
                            if (curs.getGestibNom().contains("1") || curs.getGestibNom().contains("2")) {
                                gSuiteService.createMember(usuari.getGsuiteEmail(), "tutoria1i2@iesmanacor.cat");
                                log.info("Usuari " + usuari.getGsuiteEmail() + " afegit al grup tutoria1i2@iesmanacor.cat");
                            } else if (curs.getGestibNom().contains("3") || curs.getGestibNom().contains("4")) {
                                gSuiteService.createMember(usuari.getGsuiteEmail(), "tutoria3i4@iesmanacor.cat");
                                log.info("Usuari " + usuari.getGsuiteEmail() + " afegit al grup tutoria3i4@iesmanacor.cat");
                            }
                            gSuiteService.createMember(usuari.getGsuiteEmail(), "tutoria.eso@iesmanacor.cat");
                            log.info("Usuari " + usuari.getGsuiteEmail() + " afegit al grup tutoria.eso@iesmanacor.cat. Nom curs:"+curs.getGestibNom());
                        } else if (curs.getGestibNom().toLowerCase().contains("batx")) {
                            gSuiteService.createMember(usuari.getGsuiteEmail(), "tutoria.batxillerat@iesmanacor.cat");
                            log.info("Usuari " + usuari.getGsuiteEmail() + " afegit al grup tutoria.batxillerat@iesmanacor.cat");
                        } else if (curs.getGestibNom().toLowerCase().contains("11")) {
                            gSuiteService.createMember(usuari.getGsuiteEmail(), "tutoria.fpb@iesmanacor.cat");
                            log.info("Usuari " + usuari.getGsuiteEmail() + " afegit al grup tutoria.fpb@iesmanacor.cat");
                        } else {
                            gSuiteService.createMember(usuari.getGsuiteEmail(), "tutoria.fp@iesmanacor.cat");
                            log.info("Usuari " + usuari.getGsuiteEmail() + " afegit al grup tutoria.fp@iesmanacor.cat");
                        }
                    }
                } else {
                    log.info("L'usuari " + usuari.getGsuiteEmail() + " no és tutor");
                }

                // Afegir al grup de Professors
                gSuiteService.createMember(usuari.getGsuiteEmail(), "professorat@iesmanacor.cat");
                System.out.println("Usuari " + usuari.getGsuiteEmail() + " afegit al grup professorat@iesmanacor.cat");

                // Afegir al Calendari Escolar IES Manacor
                gSuiteService.insertUserCalendar(usuari.getGsuiteEmail(), "iesmanacor.cat_43616c4945534d616e61636f72@resource.calendar.google.com");

                // Afegir al Calendari Extraescolar IES Manacor
                gSuiteService.insertUserCalendar(usuari.getGsuiteEmail(), "iesmanacor.cat_43616c45787472614945534d616e61636f72@resource.calendar.google.com");
            }
        }

        //Tornem a crear els equips educatius
        log.info("Reassignem equips educatius");
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
                List<GrupCorreu> grupsCorreuProfe = grupCorreuService.findByCodiGrupGestib(grupProfe);
                for (GrupCorreu grupCorreu : grupsCorreuProfe) {
                    if (grupCorreu.getGsuiteEmail().startsWith(PREFIX)) {
                        System.out.println("Creant grup de correu " + grupCorreu.getGsuiteEmail() + " per l'usuari " + profe.getGsuiteEmail());
                        gSuiteService.createMember(profe.getGsuiteEmail(), grupCorreu.getGsuiteEmail());
                    }
                }
            }
        }

        //Actualitzem la BBDD
        for (Usuari profe : professors) {
            //Esborrem tots els antics
            List<GrupCorreu> grupsCorreuOld = grupCorreuService.findByUsuari(profe);
            for (GrupCorreu grupCorreu : grupsCorreuOld) {
                grupCorreuService.esborrarUsuari(grupCorreu, profe);
                log.info("BBDD: Esborrant usuari "+profe.getGsuiteEmail()+" del grup "+grupCorreu.getGsuiteEmail());
            }

            //Afegim els nous que provenen de GSuite
            List<Group> grupsProfe = gSuiteService.getUserGroups(profe.getGsuiteEmail());
            for (Group grupProfe : grupsProfe) {
                GrupCorreu grupCorreu = grupCorreuService.findByEmail(grupProfe.getEmail());
                grupCorreuService.insertUsuari(grupCorreu, profe);
                log.info("BBDD: Insertant usuari "+profe.getGsuiteEmail()+" del grup "+grupCorreu.getGsuiteEmail());
            }
        }

        log.info("Reassignació de profes finalitzada correctament");
    }

    @Transactional
    void reassignarGrupsAlumne(List<Usuari> alumnes) throws GeneralSecurityException, IOException, MessagingException {

        final String PREFIX = "ee.";

        //Esborrem grups anteriors
        for (Usuari alumne : alumnes) {
            //Si està mal matriculat o matriculat incomplet no tindrà usuari GSuite i podria ser null (aparèixer a XML pero no a GSuite)
            if (alumne.getGsuiteEmail() != null) {
                List<Group> grupsAlumne = gSuiteService.getUserGroups(alumne.getGsuiteEmail());
                for (Group grupAlumne : grupsAlumne) {
                    GrupCorreu grupCorreu = grupCorreuService.findByEmail(grupAlumne.getEmail());
                    //Només esborrem els grups d'alumnes i professors
                    if (grupCorreu.getGrupCorreuTipus().equals(GrupCorreuTipus.PROFESSORAT) ||
                            grupCorreu.getGrupCorreuTipus().equals(GrupCorreuTipus.ALUMNAT)
                    ) {
                        gSuiteService.deleteMember(alumne.getGsuiteEmail(), grupAlumne.getEmail());
                    }
                }
            }
        }

        //Tornem a crear els grups de classe
        for (Usuari alumne : alumnes) {
            List<String> grupsAlumne = new ArrayList<>();
            if (alumne.getGestibGrup() != null) {
                grupsAlumne.add(alumne.getGestibGrup());
            }
            if (alumne.getGestibGrup2() != null) {
                grupsAlumne.add(alumne.getGestibGrup2());
            }
            if (alumne.getGestibGrup3() != null) {
                grupsAlumne.add(alumne.getGestibGrup3());
            }

            for (String grupAlumne : grupsAlumne) {
                System.out.println("Usuari:" + alumne.getGsuiteFullName() + "Email:" + alumne.getGsuiteEmail());

                // Afegir al grup d'Alumnes
                gSuiteService.createMember(alumne.getGsuiteEmail(), "alumnes@iesmanacor.cat");
                System.out.println("Usuari " + alumne.getGsuiteEmail() + " afegit al grup alumnes@iesmanacor.cat");

                Grup grup = grupService.findByGestibIdentificador(grupAlumne);
                Curs curs = null;
                if (grup != null) {
                    curs = cursService.findByGestibIdentificador(grup.getGestibCurs());

                    //Afegir als seus calendaris de grup
                    String grupCalendari = curs.getGsuiteUnitatOrganitzativa() + grup.getGestibNom();
                    grupCalendari = grupCalendari.toLowerCase();
                    Calendari calendariGrup = calendariService.findByGestibGrup(grupCalendari);

                    if (calendariGrup != null && calendariGrup.getGsuiteEmail() != null) {
                        gSuiteService.insertUserCalendar(alumne.getGsuiteEmail(), calendariGrup.getGsuiteEmail());
                    }
                }

                List<GrupCorreu> grupsCorreuAlumne = grupCorreuService.findByCodiGrupGestib(grupAlumne);
                for (GrupCorreu grupCorreu : grupsCorreuAlumne) {
                    if (grupCorreu != null && grupCorreu.getGestibGrup() != null && !grupCorreu.getGestibGrup().isEmpty() && !grupCorreu.getGsuiteEmail().startsWith(PREFIX)) {
                        System.out.println("Creant grup de correu " + grupCorreu.getGsuiteEmail() + " per l'usuari " + alumne.getGsuiteEmail());
                        gSuiteService.createMember(alumne.getGsuiteEmail(), grupCorreu.getGsuiteEmail());

                        if (grup != null) {
                            curs = cursService.findByGestibIdentificador(grup.getGestibCurs());
                            String rutaUnitat = "/alumnes/";
                            if (curs.getGestibNom().toLowerCase().contains("eso")) {
                                rutaUnitat += "eso/" + curs.getGsuiteUnitatOrganitzativa();
                            } else if (curs.getGestibNom().toLowerCase().contains("batx")) {
                                rutaUnitat += "batx/" + curs.getGsuiteUnitatOrganitzativa();
                            } else {
                                rutaUnitat += "fp/" + curs.getGsuiteUnitatOrganitzativa();
                            }

                            User usuariGSuite = gSuiteService.updateUser(alumne.getGsuiteEmail(), alumne.getGestibNom(), alumne.getGestibCognom1() + " " + alumne.getGestibCognom2() + " " + curs.getGsuiteUnitatOrganitzativa(), alumne.getGestibCodi(), rutaUnitat);
                            if (usuariGSuite != null) {
                                System.out.println("Usuari" + usuariGSuite.getPrimaryEmail() + " modificat correctament a GSuite");
                                log.info("Usuari" + usuariGSuite.getPrimaryEmail() + " modificat correctament a GSuite");
                            } else {
                                gMailService.sendMessage("Error modificant usuari " + alumne.getGsuiteEmail(), "Error modificant usuari " + alumne.getGsuiteEmail(), "jgalmes1@iesmanacor.cat");
                            }
                        }
                    }
                }
            }
        }

        //Actualitzem la BBDD
        for (Usuari alumne : alumnes) {
            //Esborrem tots els antics
            List<GrupCorreu> grupsCorreuOld = grupCorreuService.findByUsuari(alumne);
            for (GrupCorreu grupCorreu : grupsCorreuOld) {
                grupCorreuService.esborrarUsuari(grupCorreu, alumne);
            }

            //Afegim els nous que provenen de GSuite
            List<Group> grupsProfe = gSuiteService.getUserGroups(alumne.getGsuiteEmail());
            for (Group grupProfe : grupsProfe) {
                GrupCorreu grupCorreu = grupCorreuService.findByEmail(grupProfe.getEmail());
                grupCorreuService.insertUsuari(grupCorreu, alumne);
            }
        }

        log.info("Reassignació de grups d'alumnes");
    }

    private String generateUsernameAlumne(Usuari a) {

        String nom = removeAccents(a.getGestibNom());
        String ap1 = removeAccents(a.getGestibCognom1());
        String username = nom.substring(0, 1) + ap1.replaceAll("\\s", "");
        String exp = a.getGestibExpedient();
        username = username.toLowerCase() + exp;

        return username;
    }

    private String generateUsername(Usuari p) throws GeneralSecurityException, IOException {

        String nom = removeAccents(p.getGestibNom());
        String ap1 = removeAccents(p.getGestibCognom1());
        String username = nom.substring(0, 1) + ap1.replaceAll("\\s", "");
        String domini = "@iesmanacor.cat";
        if (p.getGestibAlumne()) {
            username += p.getGestibExpedient();
            domini = "@alumnes.iesmanacor.cat";
        }
        username = username.toLowerCase();

        User u = gSuiteService.getUserById(username + domini);

        if (u == null) {
            return username;
        }

        //Si existeix cerquem el més proper
        int i = 1;
        while (u != null) {
            u = gSuiteService.getUserById(username + i + domini);
            i++;
        }

        return username + i;
    }

    private String removeAccents(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String accentRemoved = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return accentRemoved;
    }

    private boolean usuariTeGrup(Usuari u, String grup) {
        if (u == null || grup == null) {
            return false;
        }
        return (grup.equals(u.getGestibGrup()) || grup.equals(u.getGestibGrup2()) || grup.equals(u.getGestibGrup3()));
    }
}