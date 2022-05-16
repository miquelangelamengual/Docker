package cat.iesmanacor.gestibgsuite.api;

import cat.iesmanacor.gestibgsuite.manager.AuthService;
import cat.iesmanacor.gestibgsuite.manager.TokenManager;
import cat.iesmanacor.gestibgsuite.manager.UsuariService;
import cat.iesmanacor.gestibgsuite.model.Notificacio;
import cat.iesmanacor.gestibgsuite.model.NotificacioTipus;
import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@Slf4j
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UsuariService usuariService;

    @Autowired
    private TokenManager tokenManager;

    @Value("${gc.adminUser}")
    private String administrador;

    @PostMapping("/auth/google/login")
    public ResponseEntity loginUserGoogle(@RequestBody String token) throws GeneralSecurityException, IOException {

        log.info("Token:" + token);

        GoogleIdToken idToken = authService.verifyGoogleUser(token);

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            Usuari usuari = usuariService.findByEmail(email);

            if ((emailVerified && usuari != null) || email.equals(this.administrador)) {
                System.out.println("create token with e-mail: " + email);
                return new ResponseEntity<>(tokenManager.createToken(email), HttpStatus.OK);
            }
        } else {
            Notificacio notificacio = new Notificacio();
            notificacio.setNotifyMessage("Token erroni");
            notificacio.setNotifyType(NotificacioTipus.ERROR);

            return new ResponseEntity<>(notificacio, HttpStatus.UNAUTHORIZED);
        }

        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Usuari no autoritzat!");
        notificacio.setNotifyType(NotificacioTipus.ERROR);

        return new ResponseEntity<>(notificacio, HttpStatus.UNAUTHORIZED);
    }
}
