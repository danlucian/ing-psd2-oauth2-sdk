package net.danlucian.psd2.ing.rpc.sandbox;

import net.danlucian.psd2.ing.rpc.payload.ApplicationAccessToken;
import net.danlucian.psd2.ing.rpc.payload.Country;
import net.danlucian.psd2.ing.rpc.payload.PreflightUrl;
import net.danlucian.psd2.ing.security.ClientSecrets;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;

public class AuthorizationServerClientTest {

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
    public void testAuthorizationServerClient() throws MalformedURLException {
        // prepare the client secrets
        final ClientSecrets clientSecrets = new ClientSecrets(
                clientCertificate,
                clientKey,
                signingCertificate,
                signingKey
        );
        final String scopes = "customer-details%3Aprofile%3Aview+customer-details%3Aemail%3Aview+customer-details%3Aaddress%3Aview";

        // fetch an application access token that is required for  the next call
        AppAccessTokenClient appAccessTokenClient = new AppAccessTokenClient(clientSecrets);
        ApplicationAccessToken applicationAccessToken = appAccessTokenClient.getToken();

        // construct an authorization server client and fetch the preflight url
        AuthorizationServerClient authorizationServerClient = new AuthorizationServerClient(clientSecrets, scopes);
        PreflightUrl preflightUrl = authorizationServerClient.getPreflightUrl(applicationAccessToken, new URL("https://www.ing.ro"), Country.Romania);

        assertNotNull(preflightUrl.getLocation());
    }
}
