package cat.iesmanacor.gestibgsuite.manager;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;


@Service
public class GMailService {
    private final static String DOMAIN = "iesmanacor.cat";
    @Value("${gc.projectid}")
    private String projectId;
    @Value("${gc.keyfile}")
    private String keyFile;
    @Value("${gc.adminUser}")
    private String adminUser;

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to       email address of the receiver
     * @param from     email address of the sender, the mailbox account
     * @param subject  subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException
     */
    private static MimeMessage createEmail(String to,
                                          String from,
                                          String subject,
                                          String bodyText,
                                           boolean isHTML)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        if(isHTML) {
            email.setText(bodyText, "utf-8", "html");
        } else {
            email.setText(bodyText);
        }


        return email;
    }

    /**
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException
     * @throws MessagingException
     */
    private static Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param service      Authorized Gmail API instance.
     * @param userId       User's email address. The special value "me"
     *                     can be used to indicate the authenticated user.
     * @param emailContent Email to be sent.
     * @return The sent message
     * @throws MessagingException
     * @throws IOException
     */
    private static Message sendMessage(Gmail service,
                                      String userId,
                                      MimeMessage emailContent)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        return message;
    }

    public void helloWorld() throws MessagingException, IOException, GeneralSecurityException {
        System.out.println("Provant Gmail...");
        String[] scopes = {GmailScopes.GMAIL_COMPOSE};
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser).createDelegated("notificacions@iesmanacor.cat");
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).build();

        String body = "Prova cos missatge<br> salt de línia i &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; tabulació 3";
        body += "<br><br> Avís: Aquest missatgé és automàtic. No contesti aquest missatge. Si vol adreçar-se a l'administrador enviï un correu a jgalmes1@iesmanacor.cat. Gràcies!";
        body += "<table><tr><td>Hola</td></tr></table>";
        MimeMessage mimeMessage = createEmail("joan.galmes@gmail.com", "notificacions@iesmanacor.cat", "Prova email", body,true);
        Message email = sendMessage(service, "notificacions@iesmanacor.cat", mimeMessage);
    }


    public void sendMessage(String assumpte, String missatge, String to, boolean isHTML) throws MessagingException, IOException, GeneralSecurityException {
        String[] scopes = {GmailScopes.GMAIL_COMPOSE};
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser).createDelegated("notificacions@iesmanacor.cat");
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).build();

        String body = missatge;
        if(isHTML){
            body += "<br><br> Avís: Aquest missatgé és automàtic. No contesti aquest missatge. Si vol adreçar-se a l'administrador enviï un correu a jgalmes1@iesmanacor.cat. Gràcies!";
        } else {
            body += "\n\n Avís: Aquest missatgé és automàtic. No contesti aquest missatge. Si vol adreçar-se a l'administrador enviï un correu a jgalmes1@iesmanacor.cat. Gràcies!";
        }
        MimeMessage mimeMessage = createEmail(to, "notificacions@iesmanacor.cat", assumpte, body,isHTML);
        Message email = sendMessage(service, "notificacions@iesmanacor.cat", mimeMessage);
    }

    public void sendMessage(String assumpte, String missatge, String to) throws MessagingException, IOException, GeneralSecurityException {
        sendMessage(assumpte,missatge,to,true);
    }


    }

