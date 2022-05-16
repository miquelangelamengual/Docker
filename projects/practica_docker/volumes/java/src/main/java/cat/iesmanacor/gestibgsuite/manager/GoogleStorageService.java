package cat.iesmanacor.gestibgsuite.manager;

import com.google.api.services.storage.StorageScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.GeneralSecurityException;


@Service
public class GoogleStorageService {
    private final static String DOMAIN = "iesmanacor.cat";
    @Value("${gc.projectid}")
    private String projectId;
    @Value("${gc.keyfile}")
    private String keyFile;
    @Value("${gc.adminUser}")
    private String adminUser;

    @Value("${server.tmp}")
    private String pathTemporal;

    @Value("${gc.storage.bucketname}")
    private String bucketName;


    public void uploadObject(String objectName, String filePath) throws IOException, GeneralSecurityException {
        String[] scopes = {StorageScopes.DEVSTORAGE_READ_WRITE};
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).setCredentials(credentials).build().getService();
        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        //Per arxius petits podem fer simplement un storage.create
        //storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));

        //Per arxius grans hem de pujar-ho amb "chunks"
        try (WriteChannel writer = storage.writer(blobInfo)) {
            File uploadFrom = new File(filePath);
            byte[] buffer = new byte[10_240];
            try (InputStream input = Files.newInputStream(uploadFrom.toPath())) {
                int limit;
                while ((limit = input.read(buffer)) >= 0) {
                    writer.write(ByteBuffer.wrap(buffer, 0, limit));
                }
            }
        }
    }


}

