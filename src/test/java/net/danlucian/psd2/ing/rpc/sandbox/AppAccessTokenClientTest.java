package net.danlucian.psd2.ing.rpc.sandbox;


import net.danlucian.psd2.ing.rpc.payload.ApplicationAccessToken;
import net.danlucian.psd2.ing.security.ClientSecrets;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;

public class AppAccessTokenClientTest {

    private File clientCertificate;
    private File clientKey;
    private File signingCertificate;
    private File signingKey;

    @Before
    public void loadFiles() {
        ClassLoader classLoader = getClass().getClassLoader();

        clientCertificate =     new File(classLoader.getResource("example_eidas_client_tls.cer").getFile());
        clientKey =             new File(classLoader.getResource("example_eidas_client_tls.key").getFile());
        signingCertificate =    new File(classLoader.getResource("example_eidas_client_signing.cer").getFile());
        signingKey =            new File(classLoader.getResource("example_eidas_client_signing.key").getFile());
    }

    @Test
    public void testAppAccessTokenClient() {
        // prepare the client secrets
        final ClientSecrets clientSecrets = new ClientSecrets(
                clientCertificate,
                clientKey,
                signingCertificate,
                signingKey
        );

        // get a client instance and fetch the token
        AppAccessTokenClient appAccessTokenClient = new AppAccessTokenClient(clientSecrets);
        ApplicationAccessToken applicationAccessToken = appAccessTokenClient.getToken();

        assertNotNull(applicationAccessToken.getAccessToken());
    }
}
