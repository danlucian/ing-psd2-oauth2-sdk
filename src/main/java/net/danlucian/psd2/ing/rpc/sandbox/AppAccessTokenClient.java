package net.danlucian.psd2.ing.rpc.sandbox;

import net.danlucian.psd2.ing.exception.rpc.RequestFailure;
import net.danlucian.psd2.ing.rpc.Client;
import net.danlucian.psd2.ing.rpc.Interceptors;
import net.danlucian.psd2.ing.rpc.payload.ApplicationAccessToken;
import net.danlucian.psd2.ing.security.ClientSecrets;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import javax.net.ssl.X509TrustManager;

import static net.danlucian.psd2.ing.security.SecurityUtil.*;

public class AppAccessTokenClient extends Client implements Interceptors {

    public final static String URL = "https://api.sandbox.ing.com/oauth2/token";

    /**
     * @param clientSecrets must not be null
     */
    public AppAccessTokenClient(ClientSecrets clientSecrets) {
        super(clientSecrets);
    }

    public ApplicationAccessToken getToken() throws RequestFailure {
        final String signatureKeyId = generateSerialNumber(clientSecrets.getClientSigningCert());

        final String signature = generateSignature(
                clientSecrets.getClientSigningKey(),
                signingTemplate("post", "/oauth2/token")
        );
        final String authorization =
                "Signature keyId=\"SN=" + signatureKeyId + "\",algorithm=\"rsa-sha256\",headers=\"(request-target) date digest\",signature=\"" + signature + "\"";

        final OkHttpClient client = new OkHttpClient.Builder()
            .sslSocketFactory(clientSecrets.getContext().getSocketFactory(), (X509TrustManager) clientSecrets.getTrustManager())
            .addInterceptor(loggingInterceptor())
            .hostnameVerifier((hostname, session) -> true)
            .build();

        final RequestBody body = new FormBody.Builder()
                .addEncoded("grant_type", "client_credentials")
                .build();

        final Request request = new Request.Builder()
                .url(URL)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Digest", digest)
                .addHeader("Date", currentDate)
                .addHeader("TPP-Signature-Certificate", generatePrettifiedFromX509(clientSecrets.getClientSigningCert()) + " :")
                .addHeader("authorization", authorization)
                .post(body)
                .build();

        return executeRequest(client, request, ApplicationAccessToken.class);
    }
}

