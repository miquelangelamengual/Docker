package cat.iesmanacor.gestibgsuite.api.old;

import cat.iesmanacor.gestibgsuite.manager.*;
import cat.iesmanacor.gestibgsuite.model.Notificacio;
import cat.iesmanacor.gestibgsuite.model.NotificacioTipus;
import cat.iesmanacor.gestibgsuite.model.gestib.Curs;
import cat.iesmanacor.gestibgsuite.model.gestib.Departament;
import cat.iesmanacor.gestibgsuite.model.google.Calendari;
import cat.iesmanacor.gestibgsuite.model.google.GrupCorreu;
import cat.iesmanacor.gestibgsuite.model.google.GrupCorreuTipus;
import com.google.api.services.directory.model.Group;
import com.google.api.services.directory.model.Member;
import com.google.api.services.directory.model.User;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

@RestController
public class GSuiteController {
    @Autowired
    private GSuiteService gSuiteService;

    @Autowired
    private GrupCorreuService grupCorreuService;

    @Autowired
    private CalendariService calendariService;

    @Autowired
    private DepartamentService departamentService;

    @Autowired
    private CursService cursService;


    @PostMapping("/auth/samplenotification")
    public ResponseEntity<Notificacio> sampleNotification() throws InterruptedException {
        Thread.sleep(2000);
        Notificacio n = new Notificacio();
        n.setNotifyMessage("Prova notificació");
        n.setNotifyType(NotificacioTipus.WARNING);
        return new ResponseEntity<>(n, HttpStatus.OK);
    }

    @GetMapping("/gsuite/users")
    public ResponseEntity<List<User>> getUsers() throws GeneralSecurityException, IOException {
        List<User> users = gSuiteService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/gsuite/groups")
    public ResponseEntity<List<Group>> getGroups() throws GeneralSecurityException, IOException {
        List<Group> groups = gSuiteService.getGroups();
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @GetMapping("/gsuite/group/{id}/members")
    public ResponseEntity<List<Member>> getGroupMembers(@PathVariable("id") String idgrup) throws GeneralSecurityException, IOException {
        List<Member> members = gSuiteService.getMembers(idgrup);
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @GetMapping("/gsuite/groups/create")
    public ResponseEntity<String> getGroupsCreate() throws GeneralSecurityException, IOException {
        List<Group> groups = gSuiteService.getGroups();
        for (Group g : groups) {
            GrupCorreu gc = grupCorreuService.findByEmail(g.getEmail());
            if (gc == null) {
                grupCorreuService.save(null, g.getName(), g.getEmail(), g.getDescription(), GrupCorreuTipus.GENERAL);
            }
        }
        return new ResponseEntity<>("Grups creats correctament", HttpStatus.OK);
    }

    @GetMapping("/gsuite/groups/load")
    @Transactional
    public ResponseEntity<String> loadGrupsGestibToGrupsCorreu() {

        Multimap<String, String> emailsGrups = LinkedHashMultimap.create();

        // 1r ESO
        emailsGrups.put("557834", "eso1a@iesmanacor.cat");
        emailsGrups.put("557834", "ee.eso1a@iesmanacor.cat");
        emailsGrups.put("557848", "eso1b@iesmanacor.cat");
        emailsGrups.put("557848", "ee.eso1b@iesmanacor.cat");
        emailsGrups.put("557850", "eso1c@iesmanacor.cat");
        emailsGrups.put("557850", "ee.eso1c@iesmanacor.cat");
        emailsGrups.put("557851", "eso1d@iesmanacor.cat");
        emailsGrups.put("557851", "ee.eso1d@iesmanacor.cat");
        emailsGrups.put("557853", "eso1e@iesmanacor.cat");
        emailsGrups.put("557853", "ee.eso1e@iesmanacor.cat");
        //emailsGrups.put("491854", "eso1f@iesmanacor.cat");
        //emailsGrups.put("491855", "eso1g@iesmanacor.cat");
        //emailsGrups.put("xxxx", "eso1h@iesmanacor.cat");
        // 2n ESO
        emailsGrups.put("557855", "eso2a@iesmanacor.cat");
        emailsGrups.put("557855", "ee.eso2a@iesmanacor.cat");
        emailsGrups.put("557856", "eso2b@iesmanacor.cat");
        emailsGrups.put("557856", "ee.eso2b@iesmanacor.cat");
        emailsGrups.put("557857", "eso2c@iesmanacor.cat");
        emailsGrups.put("557857", "ee.eso2c@iesmanacor.cat");
        emailsGrups.put("557858", "eso2d@iesmanacor.cat");
        emailsGrups.put("557858", "ee.eso2d@iesmanacor.cat");
        emailsGrups.put("557859", "eso2e@iesmanacor.cat");
        emailsGrups.put("557859", "ee.eso2e@iesmanacor.cat");
        emailsGrups.put("557860", "eso2f@iesmanacor.cat");
        emailsGrups.put("557860", "ee.eso2f@iesmanacor.cat");
        //emailsGrups.put("491856", "eso2g@iesmanacor.cat");
        //emailsGrups.put("xxxx", "eso2h@iesmanacor.cat");
        // 3r ESO
        emailsGrups.put("557862", "eso3a@iesmanacor.cat");
        emailsGrups.put("557862", "ee.eso3a@iesmanacor.cat");
        emailsGrups.put("557865", "eso3b@iesmanacor.cat");
        emailsGrups.put("557865", "ee.eso3b@iesmanacor.cat");
        emailsGrups.put("557866", "eso3c@iesmanacor.cat");
        emailsGrups.put("557866", "ee.eso3c@iesmanacor.cat");
        emailsGrups.put("557867", "eso3d@iesmanacor.cat");
        emailsGrups.put("557867", "ee.eso3d@iesmanacor.cat");
        emailsGrups.put("557869", "eso3e@iesmanacor.cat");
        emailsGrups.put("557869", "ee.eso3e@iesmanacor.cat");
        emailsGrups.put("557870", "eso3f@iesmanacor.cat");
        emailsGrups.put("557870", "ee.eso3f@iesmanacor.cat");
        //emailsGrups.put("xxxx", "eso3g@iesmanacor.cat");
        //emailsGrups.put("xxxx", "eso3h@iesmanacor.cat");
        // 4t ESO
        emailsGrups.put("558197", "eso4a@iesmanacor.cat");
        emailsGrups.put("558197", "ee.eso4a@iesmanacor.cat");
        emailsGrups.put("558198", "eso4b@iesmanacor.cat");
        emailsGrups.put("558198", "ee.eso4b@iesmanacor.cat");
        emailsGrups.put("558199", "eso4c@iesmanacor.cat");
        emailsGrups.put("558199", "ee.eso4c@iesmanacor.cat");
        emailsGrups.put("558200", "eso4d@iesmanacor.cat");
        emailsGrups.put("558200", "ee.eso4d@iesmanacor.cat");
        emailsGrups.put("558201", "eso4e@iesmanacor.cat");
        emailsGrups.put("558201", "ee.eso4e@iesmanacor.cat");
        //emailsGrups.put("xxxx", "eso4f@iesmanacor.cat");
        //emailsGrups.put("xxxx", "eso4g@iesmanacor.cat");
        //emailsGrups.put("xxxx", "eso4h@iesmanacor.cat");
        // 1r Batx
        emailsGrups.put("558202", "batx1a@iesmanacor.cat");
        emailsGrups.put("558202", "ee.batx1a@iesmanacor.cat");
        emailsGrups.put("558203", "batx1b@iesmanacor.cat");
        emailsGrups.put("558203", "ee.batx1b@iesmanacor.cat");
        emailsGrups.put("558204", "batx1c@iesmanacor.cat");
        emailsGrups.put("558204", "ee.batx1c@iesmanacor.cat");
        emailsGrups.put("558205", "batx1d@iesmanacor.cat");
        emailsGrups.put("558205", "ee.batx1d@iesmanacor.cat");
        //emailsGrups.put("xxxxx", "batx1e@iesmanacor.cat");
        // 2n Batx
        emailsGrups.put("558206", "batx2a@iesmanacor.cat");
        emailsGrups.put("558206", "ee.batx2a@iesmanacor.cat");
        emailsGrups.put("558207", "batx2b@iesmanacor.cat");
        emailsGrups.put("558207", "ee.batx2b@iesmanacor.cat");
        emailsGrups.put("558208", "batx2c@iesmanacor.cat");
        emailsGrups.put("558208", "ee.batx2c@iesmanacor.cat");
        emailsGrups.put("558209", "batx2d@iesmanacor.cat");
        emailsGrups.put("558209", "ee.batx2d@iesmanacor.cat");
        //emailsGrups.put("xxxxx", "batx2e@iesmanacor.cat");
        // ADG21
        emailsGrups.put("558632", "adg21a@iesmanacor.cat");
        emailsGrups.put("558632", "ee.adg21a@iesmanacor.cat");
        emailsGrups.put("558633", "adg21b@iesmanacor.cat");
        emailsGrups.put("558633", "ee.adg21b@iesmanacor.cat");
        emailsGrups.put("561036", "adg21c@iesmanacor.cat");
        emailsGrups.put("561036", "ee.adg21c@iesmanacor.cat");
        // ADG32
        emailsGrups.put("558649", "adg32a@iesmanacor.cat");
        emailsGrups.put("558649", "ee.adg32a@iesmanacor.cat");
        emailsGrups.put("558651", "adg32b@iesmanacor.cat");
        emailsGrups.put("558651", "ee.adg32b@iesmanacor.cat");
        emailsGrups.put("561255", "adg32c@iesmanacor.cat");
        emailsGrups.put("561255", "ee.adg32c@iesmanacor.cat");
        // COM11
        emailsGrups.put("558604", "com11a@iesmanacor.cat");
        emailsGrups.put("558604", "ee.com11a@iesmanacor.cat");
        emailsGrups.put("558605", "com11b@iesmanacor.cat");
        emailsGrups.put("558605", "ee.com11b@iesmanacor.cat");
        emailsGrups.put("561103", "com11c@iesmanacor.cat");
        emailsGrups.put("561103", "ee.com11c@iesmanacor.cat");
        // COM21
        emailsGrups.put("558607", "com21a@iesmanacor.cat");
        emailsGrups.put("558607", "ee.com21a@iesmanacor.cat");
        emailsGrups.put("558611", "com21b@iesmanacor.cat");
        emailsGrups.put("558611", "ee.com21b@iesmanacor.cat");
        //emailsGrups.put("471959", "com21c@iesmanacor.cat");
        // COM31
        emailsGrups.put("558661", "com31a@iesmanacor.cat");
        emailsGrups.put("558661", "ee.com31a@iesmanacor.cat");
        emailsGrups.put("558662", "com31b@iesmanacor.cat");
        emailsGrups.put("558662", "ee.com31b@iesmanacor.cat");
        emailsGrups.put("561256", "com31c@iesmanacor.cat");
        emailsGrups.put("561256", "ee.com31c@iesmanacor.cat");
        // TMV11
        emailsGrups.put("558600", "tmv11a@iesmanacor.cat");
        emailsGrups.put("558600", "ee.tmv11a@iesmanacor.cat");
        emailsGrups.put("558602", "tmv11b@iesmanacor.cat");
        emailsGrups.put("558602", "ee.tmv11b@iesmanacor.cat");
        emailsGrups.put("561102", "tmv11c@iesmanacor.cat");
        emailsGrups.put("561102", "ee.tmv11c@iesmanacor.cat");
        // TMV21
        emailsGrups.put("558615", "tmv21a@iesmanacor.cat");
        emailsGrups.put("558615", "ee.tmv21a@iesmanacor.cat");
        emailsGrups.put("558618", "tmv21b@iesmanacor.cat");
        emailsGrups.put("558618", "ee.tmv21b@iesmanacor.cat");
        emailsGrups.put("558621", "tmv21c@iesmanacor.cat");
        emailsGrups.put("558621", "ee.tmv21c@iesmanacor.cat");
        emailsGrups.put("561098", "tmv21d@iesmanacor.cat");
        emailsGrups.put("561098", "ee.tmv21d@iesmanacor.cat");
        // TMV22
        emailsGrups.put("558626", "tmv22a@iesmanacor.cat");
        emailsGrups.put("558626", "ee.tmv22a@iesmanacor.cat");
        emailsGrups.put("558627", "tmv22b@iesmanacor.cat");
        emailsGrups.put("558627", "ee.tmv22b@iesmanacor.cat");
        emailsGrups.put("558629", "tmv22c@iesmanacor.cat");
        emailsGrups.put("558629", "ee.tmv22c@iesmanacor.cat");
        emailsGrups.put("558631", "tmv22d@iesmanacor.cat");
        emailsGrups.put("558631", "ee.tmv22d@iesmanacor.cat");
        emailsGrups.put("561099", "tmv22e@iesmanacor.cat");
        emailsGrups.put("561099", "ee.tmv22e@iesmanacor.cat");
        // TMV31
        emailsGrups.put("558654", "tmv31a@iesmanacor.cat");
        emailsGrups.put("558654", "ee.tmv31a@iesmanacor.cat");
        emailsGrups.put("558655", "tmv31b@iesmanacor.cat");
        emailsGrups.put("558655", "ee.tmv31b@iesmanacor.cat");
        //emailsGrups.put("474674", "tmv31c@iesmanacor.cat");
        // ELE21
        emailsGrups.put("558634", "ele21a@iesmanacor.cat");
        emailsGrups.put("558634", "ee.ele21a@iesmanacor.cat");
        emailsGrups.put("558635", "ele21b@iesmanacor.cat");
        emailsGrups.put("558635", "ee.ele21b@iesmanacor.cat");
        emailsGrups.put("561100", "ele21c@iesmanacor.cat");
        emailsGrups.put("561100", "ee.ele21c@iesmanacor.cat");
        // ELE31
        emailsGrups.put("558664", "ele31a@iesmanacor.cat");
        emailsGrups.put("558664", "ee.ele31a@iesmanacor.cat");
        emailsGrups.put("558673", "ele31b@iesmanacor.cat");
        emailsGrups.put("558673", "ee.ele31b@iesmanacor.cat");
        emailsGrups.put("561735", "ele31c@iesmanacor.cat");
        emailsGrups.put("561735", "ee.ele31c@iesmanacor.cat");
        // IFC21
        emailsGrups.put("558639", "ifc21a@iesmanacor.cat");
        emailsGrups.put("558639", "ee.ifc21a@iesmanacor.cat");
        emailsGrups.put("558640", "ifc21b@iesmanacor.cat");
        emailsGrups.put("558640", "ee.ifc21b@iesmanacor.cat");
        emailsGrups.put("558642", "ifc21c@iesmanacor.cat");
        emailsGrups.put("558642", "ee.ifc21c@iesmanacor.cat");
        emailsGrups.put("558643", "ifc21d@iesmanacor.cat");
        emailsGrups.put("558643", "ee.ifc21d@iesmanacor.cat");
        emailsGrups.put("561101", "ifc21e@iesmanacor.cat");
        emailsGrups.put("561101", "ee.ifc21e@iesmanacor.cat");
        // IFC31
        emailsGrups.put("558645", "ifc31a@iesmanacor.cat");
        emailsGrups.put("558645", "ee.ifc31a@iesmanacor.cat");
        emailsGrups.put("558646", "ifc31b@iesmanacor.cat");
        emailsGrups.put("558646", "ee.ifc31b@iesmanacor.cat");
        emailsGrups.put("562556", "ifc31c@iesmanacor.cat");
        emailsGrups.put("562556", "ee.ifc31c@iesmanacor.cat");
        // IFC33
        emailsGrups.put("558658", "ifc33a@iesmanacor.cat");
        emailsGrups.put("558658", "ee.ifc33a@iesmanacor.cat");
        emailsGrups.put("558659", "ifc33b@iesmanacor.cat");
        emailsGrups.put("558659", "ee.ifc33b@iesmanacor.cat");
        //emailsGrups.put("471976", "ifc33c@iesmanacor.cat");
        // EEBASICA
        emailsGrups.put("557800", "esoeea@iesmanacor.cat");
        emailsGrups.put("557800", "ee.esoeea@iesmanacor.cat");

        for (Map.Entry email : emailsGrups.entries()) {
            System.out.println("Email" + email.getValue());
            GrupCorreu gc = grupCorreuService.findByEmail(email.getValue().toString());
            if (gc != null) {
                System.out.println("Entra. Grup:" + email.getKey());
                gc.setGestibGrup(email.getKey().toString());
                grupCorreuService.save(gc);
            }
        }

        return new ResponseEntity<>("Assignació de grups a grups de correu correcte", HttpStatus.OK);
    }

    @GetMapping("/gsuite/calendar/load")
    @Transactional
    public ResponseEntity<String> loadCalendarGrups() {

        Multimap<String, String> calendarsGrups = LinkedHashMultimap.create();

        // 1r ESO
        calendarsGrups.put("eso1a", "c_188dcf6qc5u7qgeqmehnnclofb74e4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso1b", "c_188apa4lu1l8gh61nc0ohc9hn50ns4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso1c", "c_18849i6pmv8jgh52go6qvuvocsmmm4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso1d", "c_188d8b7a3cv72g55m965hk5liphgi4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso1e", "c_1885ct6mfh8kggnuhp53v0oqhts8g4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso1f", "c_188ft83ij8ckqgp9jgqql630lfu844ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso1g", "c_188e9ovqeet9ejdqgeoe2ofv8nqjk4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso1h", "c_1880mf7khrf8cgs0kio18pal43e4e4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // 2n ESO
        calendarsGrups.put("eso2a", "c_1882usr9lehg6jmal3be65bta8aqa4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso2b", "c_188db946f8q0aikqkoo2l53kdgkd44ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso2c", "c_1885pu0b8m688gi4n9sm19sjv5s024ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso2d", "c_1889s4o1pj3lmhimj2s9ndhdir0ku4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso2e", "c_1881ao2f58eiiim8htbfa6b9r8d8a4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso2f", "c_188aa77as6f2ui5vnl7h0gu5nafk24ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso2g", "c_18855tr1sdeesh7vg0vp9ls4e8op84ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // 3r ESO
        calendarsGrups.put("eso3a", "c_188bi2gfq1pm0g0nhe11f0rg6qg4s4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso3b", "c_1888ti2kpombkhqsnq95q3rr6usuo4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso3c", "c_1889rp621rdc8j8onc1e4vcmc16924ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso3d", "c_1885v7l14rn60g8jg909e833oufbq4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso3e", "c_188bsbd2gc91aic9nic4437nbtrv24ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso3f", "c_1886et3cjs9oej43i2lspgbk0aohm4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // 4t ESO
        calendarsGrups.put("eso4a", "c_188fjpr8vb4g4gf9mu51r873q8tr64ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso4b", "c_188bplcv2127sjdhklpc90k0b57544ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso4c", "c_188e3ppqur5oci66kt4qejnvnsfv24ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso4d", "c_18862oop2r6u4g6knkuobcu1il2rc4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso4e", "c_188577oiaa9goganinmj56ms5e0nq4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso4f", "c_1888kp8aijiooh1lkmi4h7tkvkd5q4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // 1r Batx
        calendarsGrups.put("batx1a", "c_1883r33jar55ei6fh87a0ag9orb3u4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("batx1b", "c_1885citehufiajjugi1cbk0phlfkc4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("batx1c", "c_18889gkkg50h6jh1gmplmn4jbjmri4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("batx1d", "c_188af98o05rsgj4om2dn85prvosma4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("batx1e", "c_188arfv29u0peghhg97ksm16pj1504ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // 2n Batx
        calendarsGrups.put("batx2a", "c_1889dckj25jb2g0umd6ppnufhd9no4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("batx2b", "c_1889vk37te9g0hstlkg5fbq8eieii4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("batx2c", "c_188etp7jsqqamhlek913ndihgfuc04ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("batx2d", "c_188916scijs76gvfm0j0bbbo6v1c24ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // ADG21
        calendarsGrups.put("adg21a", "c_1889lvtsqf29qhlik4mn6tfponiio4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("adg21b", "c_188f36mphhe6ehbdjlh1q4vtts3g04ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("adg21c", "c_188289g04hk9mgjoj43n4djch7g6g4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // ADG32
        calendarsGrups.put("adg32a", "c_188b7b4o3n7n6i66j79v6vh4dmbim4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("adg32b", "c_18825qt5bsou2hidjtl864hnmpplc4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("adg32c", "c_18854779j6h00hitk7bmec5nmjpt04ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // COM11
        calendarsGrups.put("com11a", "c_188dd04g3dblkgk1m5bbb3jdpcso04ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("com11b", "c_18867mtv3pkkigeom7ieuq947oore4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("com11c", "c_188auhbas4b5chq2l0grpektjm9l04ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // COM21
        calendarsGrups.put("com21a", "c_18863jlabrbm0i6lijdlhk6m6ln184ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("com21b", "c_1886tlpnhq6fkj5ki1qrnccrngviu4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("com21c", "c_1880ek582a3n8hppmb2lf6q04dedk4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // COM31
        calendarsGrups.put("com31a", "c_18878mm6r74cihgbh575m2p3f0f044ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("com31b", "c_1889q9pl38seuidegi2t5jh5e9mks4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("com31c", "c_188bn6subrpq6gr4ir4gk9m9bo06s4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // TMV11
        calendarsGrups.put("tmv11a", "c_18859deji9ptggpkg0mvr77m3o6p24ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv11b", "c_188bvie2jf2q2i83m5baa5jp2rii04ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv11c", "c_188cmuf71f300g9emnif2u26rishq4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // TMV21
        calendarsGrups.put("tmv21a", "c_1887q0bdt5l1qgn8n47vbifvmeau04ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv21b", "c_1888sio4pg47qjaon7harovucuauc4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv21c", "c_188cotj3c4c08gqcn2djj2pllpblk4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv21d", "c_1883966d4eqd8isbnlv3o4le68to64ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // TMV22
        calendarsGrups.put("tmv22a", "c_188bluc0k9c2ci2pmnhmof1g7vglc4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv22b", "c_18814dqqf7etii37g886779gge4dq4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv22c", "c_1884i8fuuv6g0g6rk5ja64lqtgl464ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv22d", "c_1886o4rg6epcch51jraa2e95uc2n24ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv22e", "c_188atnqs9jq2cg2uk7n0glfgf07qi4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // TMV31
        calendarsGrups.put("tmv31a", "c_1881uvpg8kv62ivsimjji2c9p39404ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv31b", "c_18880rbja7rfuhvtio2iqevnatroe4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv31c", "c_1887el2qeimnmhm0mn8r2v6d2gudk4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // ELE21
        calendarsGrups.put("ele21a", "c_188akicvc3f68ggpk5jqr4lus8m064ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ele21b", "c_1882f9mlb37okj5clmkenvavhb94i4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ele21c", "c_18802mbffkp4ugpsibcmfbies5fm44ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // ELE31
        calendarsGrups.put("ele31a", "c_18811dt3rrrpojqngst2nlprl35vg4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ele31b", "c_1886k33ef0u7ui2bhrf75mu0423u24ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ele31c", "c_188egas7vvj8ggi6k357gvue96ntg4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // IFC21
        calendarsGrups.put("ifc21a", "c_18848d5tmddesjpgl9opsov5kitrc4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ifc21b", "c_18849epqhonr6ikogj1sh8733o4ti4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ifc21c", "c_1883s9vtdk8iihslgl8pjg6v3k40g4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ifc21d", "c_188f1cq0gc58ej1rm1j7733ku759m4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ifc21e", "c_188d8paija0cgiitgfoammh7bdl2s4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // IFC31
        calendarsGrups.put("ifc31a", "c_1887l4ipima70gpfn7a14bj55sfbu4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ifc31b", "c_18813l33g0cp8jcuhr2n3okoor5fe4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ifc31c", "c_188cb1inrkjgoj4jigfqie7nmbcrc4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // IFC33
        calendarsGrups.put("ifc33a", "c_188bugl5859cmjacmheh9ag7mhvba4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ifc33b", "c_188dunoes5a2uhfmhdavqidfofbgu4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ifc33c", "c_1880eg9rckn1aj58lgr3jc5r9v7404ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // EEBASICA
        calendarsGrups.put("esoeea", "c_188ed8a9m0go8iclg8ub31uv3ibhs4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");

        for (Map.Entry calendar : calendarsGrups.entries()) {
            System.out.println("Calendar" + calendar.getValue());
            Calendari c = calendariService.findByEmail(calendar.getValue().toString());
            if (c != null) {
                System.out.println("Entra. Calendar:" + calendar.getKey());
                c.setGestibGrup(calendar.getKey().toString());
                calendariService.save(c);
            }
        }

        return new ResponseEntity<>("Assignació de grups a calendari correcte", HttpStatus.OK);
    }


    @GetMapping("/gsuite/departaments/load")
    @Transactional
    public ResponseEntity<String> loadDepartamentsGrups() {
        Multimap<String, String> emailsDeps = LinkedHashMultimap.create();

        emailsDeps.put("1130", "dept.administracio@iesmanacor.cat");
        emailsDeps.put("1134", "dept.automocio@iesmanacor.cat");
        emailsDeps.put("1136", "dept.biologia@iesmanacor.cat");
        emailsDeps.put("1124", "dept.castella@iesmanacor.cat");
        emailsDeps.put("1125", "dept.catala@iesmanacor.cat");
        emailsDeps.put("1131", "dept.comerc@iesmanacor.cat");
        emailsDeps.put("1119", "dept.educaciofisica@iesmanacor.cat");
        emailsDeps.put("1132", "dept.electricitat@iesmanacor.cat");
        emailsDeps.put("1120", "dept.filosofia@iesmanacor.cat");
        emailsDeps.put("1121", "dept.fisicaiquimica@iesmanacor.cat");
        emailsDeps.put("1122", "dept.fol@iesmanacor.cat");
        emailsDeps.put("1133", "dept.informatica@iesmanacor.cat");
        emailsDeps.put("1137", "dept.llenguesestrangeres@iesmanacor.cat");
        emailsDeps.put("1126", "dept.matematiques@iesmanacor.cat");
        emailsDeps.put("1127", "dept.musica@iesmanacor.cat");
        emailsDeps.put("1117", "dept.orientacio@iesmanacor.cat");
        emailsDeps.put("1135", "dept.plastica@iesmanacor.cat");
        emailsDeps.put("1138", "dept.socials@iesmanacor.cat");
        emailsDeps.put("1128", "dept.tecnologia@iesmanacor.cat");
        emailsDeps.put("7269", "dept.llenguesiculturaclassiques@iesmanacor.cat");

        for (Map.Entry email : emailsDeps.entries()) {
            System.out.println("Email" + email.getValue());
            Departament d = departamentService.findByGestibIdentificador(email.getKey().toString());
            if (d != null) {
                System.out.println("Entra. Departament:" + email.getKey());
                d.setGsuiteEmail(email.getValue().toString());
                departamentService.save(d);
            }
        }

        return new ResponseEntity<>("Assignació de grups a departaments correcte", HttpStatus.OK);
    }

    @GetMapping("/gsuite/cursos/load")
    @Transactional
    public ResponseEntity<String> loadUnitatsOrganitzativesCursos() {
        Multimap<String, String> cursos = LinkedHashMultimap.create();

        cursos.put("62", "eso1");
        cursos.put("66", "batx1");
        cursos.put("67", "batx2");
        cursos.put("94", "eso3");
        cursos.put("95", "eso4");
        cursos.put("227", "adg32");
        cursos.put("272", "com31");
        cursos.put("290", "com21");
        cursos.put("356", "tmv11");
        cursos.put("372", "com11");
        cursos.put("563", "eso2");
        cursos.put("1060", "ele31");
        cursos.put("1068", "tmv22");
        cursos.put("1072", "ifc33");
        //cursos.put("1080", "");
        cursos.put("7039", "adg21");
        cursos.put("7051", "ifc31");
        cursos.put("7829", "ele21");
        cursos.put("7837", "ifc21");
        cursos.put("7845", "tmv21");
        cursos.put("7847", "tmv31");

        for (Map.Entry curs : cursos.entries()) {
            System.out.println("Curs" + curs.getValue());
            Curs c = cursService.findByGestibIdentificador(curs.getKey().toString());
            if (c != null) {
                System.out.println("Curs " + curs.getKey() + " " + curs.getValue());
                c.setGsuiteUnitatOrganitzativa(curs.getValue().toString());
                cursService.save(c);
            }
        }

        return new ResponseEntity<>("Assignació de grups a departaments correcte", HttpStatus.OK);
    }


}
