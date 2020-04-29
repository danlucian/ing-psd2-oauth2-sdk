package net.danlucian.psd2.ing.rpc.sandbox;

import com.google.gson.Gson;
import net.danlucian.psd2.ing.exception.rpc.RequestFailure;
import net.danlucian.psd2.ing.exception.rpc.ResponseFailure;
import net.danlucian.psd2.ing.rpc.Client;
import net.danlucian.psd2.ing.rpc.payload.ApplicationAccessToken;
import net.danlucian.psd2.ing.security.ClientSecrets;
import net.danlucian.psd2.ing.time.DateUtil;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static net.danlucian.psd2.ing.security.SecurityUtil.*;

public class AppAccessTokenClient extends Client {

    public final static String URL = "https://api.sandbox.ing.com/oauth2/token";
    public final static String PATH = "/oauth2/token";

    public AppAccessTokenClient(ClientSecrets clientSecrets) {
        super(clientSecrets);
    }

    public ApplicationAccessToken getToken() throws RequestFailure {
        final String signatureKeyId = generateSerialNumber(clientSecrets.getClientSigningCert());
        final String digest         = generateDigestAndConvert("grant_type=client_credentials");
        final String date           = DateUtil.getCurrentDateAsString();

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        printWriter.println("(request-target): post " + PATH);
        printWriter.println("date: " + date);
        printWriter.print("digest: " + digest);

        final String content = stringWriter.toString();

        final String signature = generateSignature(clientSecrets.getClientSigningKey(), content);
        final String authorization = "Signature keyId=\"SN=" + signatureKeyId + "\",algorithm=\"rsa-sha256\",headers=\"(request-target) date digest\",signature=\"" + signature + "\"";

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
            .sslSocketFactory(clientSecrets.getContext().getSocketFactory(), (X509TrustManager) clientSecrets.getTrustManager())
            .addInterceptor(interceptor)
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
                .addHeader("Date", date)
                .addHeader("TPP-Signature-Certificate", generatePrettifiedFromX509(clientSecrets.getClientSigningCert()) + " :")
                .addHeader("authorization", authorization)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RequestFailure("Unexpected response: " + response);
            }
            if (response.body() == null) {
                throw new ResponseFailure("The response has no body: " + response);
            }

            return new Gson().fromJson(response.body().charStream(), ApplicationAccessToken.class);
        } catch (IOException e) {
            throw new RequestFailure("Unexpected error during request: " + e.getMessage());
        }
    }
}

