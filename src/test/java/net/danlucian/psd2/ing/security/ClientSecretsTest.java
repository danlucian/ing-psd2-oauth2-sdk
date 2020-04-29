package net.danlucian.psd2.ing.security;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;

public class ClientSecretsTest {

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
    public void testClientSecretsCreation() {
        ClientSecrets clientSecrets = new ClientSecrets(
                clientCertificate,
                clientKey,
                signingCertificate,
                signingKey
        );

        assertNotNull(clientSecrets);

        assertNotNull(clientSecrets.getContext());

        assertNotNull(clientSecrets.getClientSigningKey());
        assertNotNull(clientSecrets.getClientSigningCert());
    }
}
