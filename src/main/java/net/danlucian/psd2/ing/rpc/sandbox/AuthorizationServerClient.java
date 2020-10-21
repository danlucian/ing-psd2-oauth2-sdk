package net.danlucian.psd2.ing.rpc.sandbox;

import net.danlucian.psd2.ing.rpc.Client;
import net.danlucian.psd2.ing.rpc.Interceptors;
import net.danlucian.psd2.ing.rpc.payload.ApplicationAccessToken;
import net.danlucian.psd2.ing.rpc.payload.Country;
import net.danlucian.psd2.ing.rpc.payload.PreflightUrl;
import net.danlucian.psd2.ing.security.ClientSecrets;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import javax.net.ssl.X509TrustManager;
import java.net.URL;
import java.util.Objects;

import static net.danlucian.psd2.ing.security.SecurityUtil.generateDigestAndConvert;
import static net.danlucian.psd2.ing.security.SecurityUtil.generateSignature;

public class AuthorizationServerClient extends Client implements Interceptors {

    public final static String URL = "https://api.sandbox.ing.com/oauth2/authorization-server-url";
    public final static String PATH = "/oauth2/authorization-server-url";

    public AuthorizationServerClient(ClientSecrets clientSecrets, String scopes) {
        super(clientSecrets, scopes);
    }

    /**
     * @param applicationAccessToken should not be null
     * @param redirectBackUrl should not be null
     * @param country should not be null
     */
    public PreflightUrl getPreflightUrl(final ApplicationAccessToken applicationAccessToken,
                                        final URL redirectBackUrl,
                                        final Country country) {
        Objects.requireNonNull(applicationAccessToken, "applicationAccessToken must not be null");
        Objects.requireNonNull(redirectBackUrl, "redirectBackUrl must not be null");
        Objects.requireNonNull(country, "country must not be null");

        final String digest = generateDigestAndConvert("");

        final String signature = generateSignature(
                clientSecrets.getClientSigningKey(),
                signingTemplate("get", "/oauth2/authorization-server-url" + "?scope=" + scopes, digest)
        );
        final String authorization =
                "keyId=\"5ca1ab1e-c0ca-c01a-cafe-154deadbea75\",algorithm=\"rsa-sha256\",headers=\"(request-target) date digest\",signature=\"" + signature + "\"";


        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(clientSecrets.getContext().getSocketFactory(), (X509TrustManager) clientSecrets.getTrustManager())
                .addInterceptor(loggingInterceptor())
                .build();

        HttpUrl.Builder httpBuilder = HttpUrl.parse(URL).newBuilder();
        httpBuilder.addEncodedQueryParameter("scope", scopes);

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .addHeader("Authorization", "Bearer " + applicationAccessToken.getAccessToken())
                .addHeader("Content-Type", "application/json")
                .addHeader("Digest", digest)
                .addHeader("Date", currentDate)
                .addHeader("Signature", authorization)
                .get()
                .build();

        PreflightUrl preflightUrl = executeRequest(client, request, PreflightUrl.class);
        preflightUrl.enrichUri(
                country,
                applicationAccessToken.getClientId(),
                redirectBackUrl.toString(),
                scopes
        );

        return preflightUrl;
    }
}
