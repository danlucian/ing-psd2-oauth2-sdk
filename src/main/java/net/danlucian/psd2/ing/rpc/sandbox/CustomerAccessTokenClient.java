package net.danlucian.psd2.ing.rpc.sandbox;

import net.danlucian.psd2.ing.rpc.Client;
import net.danlucian.psd2.ing.rpc.Interceptors;
import net.danlucian.psd2.ing.rpc.payload.ApplicationAccessToken;
import net.danlucian.psd2.ing.rpc.payload.CustomerAccessToken;
import net.danlucian.psd2.ing.security.ClientSecrets;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import javax.net.ssl.X509TrustManager;
import java.util.Objects;

import static net.danlucian.psd2.ing.security.SecurityUtil.generateDigestAndConvert;
import static net.danlucian.psd2.ing.security.SecurityUtil.generateSignature;

public class CustomerAccessTokenClient extends Client implements Interceptors {

    public final static String URL = "https://api.sandbox.ing.com/oauth2/token";
    public final static String PATH = "/oauth2/token";

    public CustomerAccessTokenClient(ClientSecrets clientSecrets, String scopes) {
        super(clientSecrets, scopes);
    }

    /**
     * @param applicationAccessToken should not be null
     * @param authCode should not be null
     */
    public CustomerAccessToken getToken(ApplicationAccessToken applicationAccessToken, String authCode) {
        Objects.requireNonNull(applicationAccessToken, "applicationAccessToken must not be null");
        Objects.requireNonNull(authCode, "authCode must not be null");

        final String digest = generateDigestAndConvert("grant_type=authorization_code&code=" + authCode + "&redirect_uri=");

        final String signature = generateSignature(
                clientSecrets.getClientSigningKey(),
                signingTemplate("post", "/oauth2/token", digest)
        );
        final String authorization =
                "keyId=\"5ca1ab1e-c0ca-c01a-cafe-154deadbea75\",algorithm=\"rsa-sha256\",headers=\"(request-target) date digest\",signature=\"" + signature + "\"";

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(clientSecrets.getContext().getSocketFactory(), (X509TrustManager) clientSecrets.getTrustManager())
                .addInterceptor(loggingInterceptor())
                .build();

        RequestBody body = new FormBody.Builder()
                .addEncoded("grant_type", "authorization_code")
                .addEncoded("code", authCode)
                .addEncoded("redirect_uri", "")
                .build();

        Request request = new Request.Builder()
                .url(URL)
                .addHeader("Authorization", "Bearer " + applicationAccessToken.getAccessToken())
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Digest", digest)
                .addHeader("Date", currentDate)
                .addHeader("Signature", authorization)
                .post(body)
                .build();

        return executeRequest(client, request, CustomerAccessToken.class);
    }
}
