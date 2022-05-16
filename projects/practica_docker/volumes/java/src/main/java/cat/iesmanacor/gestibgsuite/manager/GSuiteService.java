package cat.iesmanacor.gestibgsuite.manager;

import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import cat.iesmanacor.gestibgsuite.repository.gestib.UsuariRepository;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ArrayMap;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.directory.Directory;
import com.google.api.services.directory.DirectoryScopes;
import com.google.api.services.directory.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class GSuiteService {
    private final static String[] DOMAINS = {"iesmanacor.cat", "alumnes.iesmanacor.cat"};
    private final static String DOMAIN = "iesmanacor.cat";
    @Value("${gc.keyfile}")
    private String keyFile;
    @Value("${gc.adminUser}")
    private String adminUser;
    @Value("${centre.usuaris.passwordinicial}")
    private String passwordInicial;

    @Value("${gc.nomprojecte}")
    private String nomProjecte;

    @Autowired
    private UsuariRepository usuariRepository;


    //USUARIS
    public List<User> getUsers() throws IOException, GeneralSecurityException {
        String[] scopes = {DirectoryScopes.ADMIN_DIRECTORY_USER, DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY};
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Directory service = new Directory.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

        List<User> result = new ArrayList<>();

        for (String domain : DOMAINS) {
            Users query = service.users().list()
                    .setDomain(domain)
                    .execute();

            List<User> users = query.getUsers();
            String pageToken = query.getNextPageToken();

            //Resultat
            result.addAll(users);

            while (pageToken != null) {
                Users query2 = service.users().list()
                        .setDomain(domain)
                        .setPageToken(pageToken)
                        .execute();
                List<User> users2 = query2.getUsers();
                pageToken = query2.getNextPageToken();

                result.addAll(users2);
            }
        }
        return result;
    }

    public User getUserById(String id) throws GeneralSecurityException, IOException {
        String[] scopes = {DirectoryScopes.ADMIN_DIRECTORY_USER, DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY};
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Directory service = new Directory.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

        try {
            User user = service.users().get(id).execute();
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    public User createUser(String email, String nom, String cognoms, String personalID, String unitatOrganitzativa) throws GeneralSecurityException, IOException {
        String[] scopes = {DirectoryScopes.ADMIN_DIRECTORY_USER, DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY};
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Directory service = new Directory.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

        UserName userName = new UserName();
        userName.setGivenName(nom);
        userName.setFamilyName(cognoms);
        userName.setFullName(nom + ' ' + cognoms);

        ArrayMap userKey = new ArrayMap();
        userKey.set(0, "value", personalID);
        userKey.set(1, "type", "organization");

        List<ArrayMap> list = new ArrayList<>();
        list.add(userKey);


        User u = new User();
        u.setName(userName);
        u.setExternalIds(list);
        u.setPassword(this.passwordInicial);
        u.setChangePasswordAtNextLogin(true);
        u.setPrimaryEmail(email);
        u.setOrgUnitPath(unitatOrganitzativa);

        service.users().insert(u).execute();

        return u;
    }

    public User updateUser(String email, String nom, String cognoms, String personalID, String unitatOrganitzativa) {
        try {
            String[] scopes = {DirectoryScopes.ADMIN_DIRECTORY_USER, DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY};
            GoogleCredentials credentials = null;

            credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);

            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            Directory service = new Directory.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

            UserName userName = new UserName();
            userName.setGivenName(nom);
            userName.setFamilyName(cognoms);
            userName.setFullName(nom + ' ' + cognoms);

            ArrayMap userKey = new ArrayMap();
            userKey.set(0, "value", personalID);
            userKey.set(1, "type", "organization");

            List<ArrayMap> list = new ArrayList<>();
            list.add(userKey);

            Usuari usuari = usuariRepository.findUsuariByGsuiteEmail(email);

            User u = service.users().get(email).execute();
            u.setName(userName);
            u.setExternalIds(list);
            u.setPrimaryEmail(usuari.getGsuiteEmail());
            u.setOrgUnitPath(unitatOrganitzativa);

            service.users().update(email, u).execute();

            return u;
        } catch (IOException | GeneralSecurityException e) {
            log.error("Error actualitzant l'usuari " + email + " error: " + e.getMessage());
        }
        return null;
    }

    public User suspendreUser(String email, boolean suspes) throws GeneralSecurityException, IOException {
        String[] scopes = {DirectoryScopes.ADMIN_DIRECTORY_USER, DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY};
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Directory service = new Directory.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

        Usuari usuari = usuariRepository.findUsuariByGsuiteEmail(email);

        User u = service.users().get(email).execute();
        u.setSuspended(suspes);

        service.users().update(email, u).execute();

        return u;
    }

    public User resetPassword(String email, String password) throws IOException, GeneralSecurityException {
        log.info("Reset "+email+" amb el password "+password);
        String[] scopes = {DirectoryScopes.ADMIN_DIRECTORY_USER, DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY};
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Directory service = new Directory.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

        Usuari usuari = usuariRepository.findUsuariByGsuiteEmail(email);

        User u = service.users().get(email).execute();
        u.setPassword(password);
        u.setChangePasswordAtNextLogin(true);

        service.users().update(email, u).execute();

        return u;
    }

    //GRUPS
    public List<Group> getGroups() throws GeneralSecurityException, IOException {

        String[] scopes = {DirectoryScopes.ADMIN_DIRECTORY_GROUP, DirectoryScopes.ADMIN_DIRECTORY_GROUP_READONLY};
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Directory service = new Directory.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

        Groups query = service.groups().list()
                .setDomain(DOMAIN)
                .execute();
        List<Group> groups = query.getGroups();
        String pageToken = query.getNextPageToken();

        //Resultat
        List<Group> result = new ArrayList<>();
        result.addAll(groups);

        while (pageToken != null) {
            Groups query2 = service.groups().list()
                    .setDomain(DOMAIN)
                    .setPageToken(pageToken)
                    .execute();
            List<Group> groups2 = query2.getGroups();
            pageToken = query2.getNextPageToken();

            result.addAll(groups2);
        }

        return result;
    }

    public Group getGroupById(String idgrup) throws GeneralSecurityException, IOException {
        String[] scopes = {DirectoryScopes.ADMIN_DIRECTORY_GROUP, DirectoryScopes.ADMIN_DIRECTORY_GROUP_READONLY};
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Directory service = new Directory.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();


        Group group = service.groups().get(idgrup).execute();

        return group;

    }

    public List<Member> getMembers(String group) throws GeneralSecurityException, IOException {
        String[] scopes = {DirectoryScopes.ADMIN_DIRECTORY_GROUP, DirectoryScopes.ADMIN_DIRECTORY_GROUP_READONLY};
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Directory service = new Directory.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

        Members query = service.members().list(group).execute();
        List<Member> members = query.getMembers();
        String pageToken = query.getNextPageToken();

        //Resultat
        List<Member> result = new ArrayList<>();
        if (members != null) {
            result.addAll(members);
        }

        while (pageToken != null) {
            Members query2 = service.members().list(group)
                    .setPageToken(pageToken)
                    .execute();

            List<Member> members2 = query2.getMembers();
            pageToken = query2.getNextPageToken();

            result.addAll(members2);
        }

        return result;
    }

    public List<Group> getUserGroups(String emailUser) throws GeneralSecurityException, IOException {
        String[] scopes = {DirectoryScopes.ADMIN_DIRECTORY_GROUP, DirectoryScopes.ADMIN_DIRECTORY_GROUP_READONLY};
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Directory service = new Directory.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

        Groups query = service.groups().list()
                .setDomain(DOMAIN)
                .setUserKey(emailUser)
                .execute();
        List<Group> groups = query.getGroups();
        String pageToken = query.getNextPageToken();

        //Resultat
        List<Group> result = new ArrayList<>();
        if (groups != null) {
            result.addAll(groups);
        }

        while (pageToken != null) {
            Groups query2 = service.groups().list()
                    .setDomain(DOMAIN)
                    .setUserKey(emailUser)
                    .setPageToken(pageToken)
                    .execute();
            List<Group> groups2 = query2.getGroups();
            pageToken = query2.getNextPageToken();

            if (groups2 != null) {
                result.addAll(groups2);
            }
        }

        return result;
    }

    public void deleteMember(String emailUser, String emailGrup) {
        if (emailUser == null) {
            log.error("Error eliminant l'usuari. Email d'usuari null");
        } else if (emailGrup == null) {
            log.error("Error eliminant l'usuari. Email del grup null");
        } else {
            try {
                String[] scopes = {DirectoryScopes.ADMIN_DIRECTORY_GROUP, DirectoryScopes.ADMIN_DIRECTORY_GROUP_READONLY};
                GoogleCredentials credentials = null;

                credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);

                HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

                final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

                Directory service = new Directory.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

                service.members().delete(emailGrup, emailUser).execute();
                log.info("S'ha eliminat l'usuari " + emailUser + " del grup " + emailGrup);
            } catch (IOException | GeneralSecurityException e) {
                log.error("Error eliminant l'usuari. Usuari: " + emailUser + " Grup:" + emailGrup);
            }
        }
    }

    public void createMember(String emailUser, String emailGrup) {
        try {
            String[] scopes = {DirectoryScopes.ADMIN_DIRECTORY_GROUP, DirectoryScopes.ADMIN_DIRECTORY_GROUP_READONLY};
            GoogleCredentials credentials = null;

            credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);

            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            Directory service = new Directory.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

            Member m = new Member();
            m.setEmail(emailUser);
            m.setRole("MEMBER");
            service.members().insert(emailGrup, m).execute();
            log.info("S'ha afegit l'usuari " + emailUser + " al grup " + emailGrup);
        } catch (IOException | GeneralSecurityException e) {
            log.error("email: " + emailUser + "grup:" + emailGrup + " error:" + e.getMessage());
        }
    }

    public Group createGroup(String email, String nom, String descripcio) throws GeneralSecurityException, IOException {
        String[] scopes = {DirectoryScopes.ADMIN_DIRECTORY_GROUP, DirectoryScopes.ADMIN_DIRECTORY_GROUP_READONLY};
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Directory service = new Directory.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

        Group g = new Group();
        g.setEmail(email);
        g.setDescription(descripcio);
        g.setName(nom);
        service.groups().insert(g).execute();

        return g;
    }

    public Group updateGroup(String email, String nom, String descripcio) throws GeneralSecurityException, IOException {
        String[] scopes = {DirectoryScopes.ADMIN_DIRECTORY_GROUP, DirectoryScopes.ADMIN_DIRECTORY_GROUP_READONLY};
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Directory service = new Directory.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

        Group g = service.groups().get(email).execute();
        g.setDescription(descripcio);
        g.setName(nom);
        service.groups().update(email, g).execute();

        return g;
    }


    //CALENDARIS
    public List<CalendarResource> getCalendars() throws GeneralSecurityException, IOException {
        String[] scopes = {DirectoryScopes.ADMIN_DIRECTORY_RESOURCE_CALENDAR, DirectoryScopes.ADMIN_DIRECTORY_RESOURCE_CALENDAR_READONLY};
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Directory service = new Directory.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

        CalendarResources query = service.resources().calendars().list("my_customer").execute();
        List<CalendarResource> calendaris = query.getItems();
        String pageToken = query.getNextPageToken();


        //Resultat
        List<CalendarResource> result = new ArrayList<>();
        result.addAll(calendaris);

        while (pageToken != null) {
            CalendarResources query2 = service.resources().calendars().list(this.adminUser)
                    .setPageToken(pageToken)
                    .execute();

            List<CalendarResource> calendaris2 = query2.getItems();
            pageToken = query2.getNextPageToken();

            result.addAll(calendaris2);
        }

        return result;
    }

    public void insertUserCalendar(String emailUser, String emailCalendar) {
        try {
            String[] scopes = {CalendarScopes.CALENDAR, CalendarScopes.CALENDAR_READONLY};
            GoogleCredentials credentials = null;

            credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);

            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

            AclRule.Scope scope = new AclRule.Scope();
            scope.setType("user");
            scope.setValue(emailUser);

            AclRule aclRule = new AclRule();
            aclRule.setRole("reader");
            aclRule.setScope(scope);

            service.acl().insert(emailCalendar, aclRule).execute();

            System.out.println("S'ha afegit l'usuari " + emailUser + " al calendari " + emailCalendar);
        } catch (IOException | GeneralSecurityException e) {
            System.out.println("email: " + emailUser + " calendari: " + emailCalendar + " error: " + e.getMessage());
        }
    }

    //Devices
    public List<ChromeOsDevice> getChromeOSDevicesByUser(User user) throws GeneralSecurityException, IOException {
        String[] scopes = {DirectoryScopes.ADMIN_DIRECTORY_USER, DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY,DirectoryScopes.ADMIN_DIRECTORY_DEVICE_CHROMEOS,DirectoryScopes.ADMIN_DIRECTORY_DEVICE_CHROMEOS_READONLY};
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Directory service = new Directory.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

        ChromeOsDevices query = service.chromeosdevices().list(user.getCustomerId()).execute();
        List<ChromeOsDevice> chromes = query.getChromeosdevices();
        String pageToken = query.getNextPageToken();


        //Resultat
        List<ChromeOsDevice> result = new ArrayList<>();
        result.addAll(chromes);

        while (pageToken != null) {
            ChromeOsDevices query2 = service.chromeosdevices().list(user.getCustomerId())
                    .setPageToken(pageToken)
                    .execute();

            List<ChromeOsDevice> chromes2 = query2.getChromeosdevices();
            pageToken = query2.getNextPageToken();

            result.addAll(chromes2);
        }

        return result;
    }
}

