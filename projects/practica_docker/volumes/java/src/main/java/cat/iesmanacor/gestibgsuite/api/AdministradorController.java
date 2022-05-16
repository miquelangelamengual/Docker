package cat.iesmanacor.gestibgsuite.api;

import cat.iesmanacor.gestibgsuite.manager.GoogleStorageService;
import cat.iesmanacor.gestibgsuite.manager.TokenManager;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@Slf4j
public class AdministradorController {

    @Autowired
    private GoogleStorageService googleStorageService;

    @Autowired
    private TokenManager tokenManager;

    @Value("${spring.datasource.username}")
    private String userDatabase;

    @Value("${spring.datasource.password}")
    private String passwordDatabase;

    @Value("${server.database.name}")
    private String nameDatabase;

    @Value("${server.tmp}")
    private String pathTemporal;

    @Value("${gc.projectid}")
    private String projectId;

    @Value("${gc.storage.bucketname}")
    private String bucketName;

    @Value("${gc.adminUser}")
    private String adminEmail;

    @GetMapping("/administrator/backupdatabase")
    public void backupDatabaseAuth(HttpServletRequest request) throws GeneralSecurityException, IOException, InterruptedException {
        Claims claims = tokenManager.getClaims(request);
        String myEmail = (String) claims.get("email");

        if (myEmail.equals(adminEmail)) {
            this.backupDatabase();
        }
    }

    /*
        Second Minute Hour Day-of-Month
        second, minute, hour, day(1-31), month(1-12), weekday(1-7) SUN-SAT
        0 0 1 * * * = a les 1AM de cada dia
         */
    @Scheduled(cron = "0 0 1 * 1,2,3,4,5,6,9,10,11,12 1,3,5")
    public void backupDatabase() throws InterruptedException, IOException, GeneralSecurityException {
        LocalDate localDate = LocalDate.now();
        String today = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String dump = "mysqldump -u" + userDatabase + " -p" + passwordDatabase + " " + nameDatabase + " > " + pathTemporal + "backup" + today + ".sql";
        String[] cmdarray = {"/bin/sh", "-c", dump};
        Process p = Runtime.getRuntime().exec(cmdarray);
        if (p.waitFor() == 0) {
            // Everything went fine

            // The ID of your GCS object
            String objectName = "backup-" + today;

            // The path to your file to upload
            String filePath = pathTemporal + "backup" + today + ".sql";

            googleStorageService.uploadObject(objectName, filePath);

            log.info("File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);

            String deleteBackup = "rm -fr" + pathTemporal + "*.sql";
            String[] cmdarrayDeleteBackup = {"/bin/sh", "-c", deleteBackup};
            Process pDeleteBackup = Runtime.getRuntime().exec(cmdarrayDeleteBackup);
            if (pDeleteBackup.waitFor() == 0) {
                // Everything went fine
                log.info("Arxius SQL esborrats amb èxit");
            } else {
                // Something went wrong
                log.error("Error esborrant els arxius SQL");
            }
        } else {
            // Something went wrong
            log.error("Error fent el backup");
        }
    }

}