package net.danlucian.psd2.ing.rpc.production.openbanking;

import net.danlucian.psd2.ing.exception.rpc.RequestFailure;
import net.danlucian.psd2.ing.rpc.Client;
import net.danlucian.psd2.ing.rpc.Interceptors;
import net.danlucian.psd2.ing.rpc.payload.ApplicationAccessToken;
import net.danlucian.psd2.ing.security.ClientSecrets;
import net.danlucian.psd2.ing.time.DateUtil;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import javax.net.ssl.X509TrustManager;

import static net.danlucian.psd2.ing.security.SecurityUtil.generateSignature;

public class AppAccessTokenClient extends Client implements Interceptors {

    public final static String URL = "https://api.ing.com/oauth2/token";

    private final String clientId;

    /**
     * @param clientSecrets must not be null
     * @param clientId should not be null
     */
    public AppAccessTokenClient(ClientSecrets clientSecrets, String clientId) {
        super(clientSecrets);
        this.clientId = clientId;
    }

    public ApplicationAccessToken getToken() throws RequestFailure {
        final String currentDate = DateUtil.getCurrentDateAsString();
        final String signature = generateSignature(
                clientSecrets.getClientSigningKey(),
                signingTemplate("post", "/oauth2/token", currentDate)
        );
        final String authorization = "Signature keyId=\"" + clientId + "\",algorithm=\"rsa-sha256\",headers=\"(request-target) date digest\",signature=\"" + signature + "\"";

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(clientSecrets.getContext().getSocketFactory(), (X509TrustManager) clientSecrets.getTrustManager())
                .addInterceptor(loggingInterceptor())
                .hostnameVerifier((hostname, session) -> true)
                .build();

        RequestBody body = new FormBody.Builder()
                .addEncoded("grant_type", "client_credentials")
                .build();

        Request request = new Request.Builder()
                .url(URL)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Digest", digest)
                .addHeader("Date", currentDate)
                .addHeader("authorization", authorization)
                .post(body)
                .build();

        return executeRequest(client, request, ApplicationAccessToken.class);
    }

}
