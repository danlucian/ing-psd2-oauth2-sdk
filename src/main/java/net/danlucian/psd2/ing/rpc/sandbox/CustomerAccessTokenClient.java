package net.danlucian.psd2.ing.rpc.sandbox;

import com.google.gson.Gson;
import net.danlucian.psd2.ing.exception.rpc.RequestFailure;
import net.danlucian.psd2.ing.exception.rpc.ResponseFailure;
import net.danlucian.psd2.ing.rpc.Client;
import net.danlucian.psd2.ing.rpc.payload.ApplicationAccessToken;
import net.danlucian.psd2.ing.rpc.payload.CustomerAccessToken;
import net.danlucian.psd2.ing.security.ClientSecrets;
import net.danlucian.psd2.ing.time.DateUtil;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

import static net.danlucian.psd2.ing.security.SecurityUtil.generateDigestAndConvert;
import static net.danlucian.psd2.ing.security.SecurityUtil.generateSignature;

public class CustomerAccessTokenClient extends Client {

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

        final String digest         = generateDigestAndConvert("grant_type=authorization_code&code=" + authCode + "&redirect_uri=");
        final String date           = DateUtil.getCurrentDateAsString();

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        printWriter.println("(request-target): post " + PATH);
        printWriter.println("date: " + date);
        printWriter.print("digest: " + digest);

        final String content = stringWriter.toString();

        final String signature = generateSignature(clientSecrets.getClientSigningKey(), content);
        final String authorization = "keyId=\"5ca1ab1e-c0ca-c01a-cafe-154deadbea75\",algorithm=\"rsa-sha256\",headers=\"(request-target) date digest\",signature=\"" + signature + "\"";

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(clientSecrets.getContext().getSocketFactory(), (X509TrustManager) clientSecrets.getTrustManager())
                .addInterceptor(interceptor)
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
                .addHeader("Date", date)
                .addHeader("Signature", authorization)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RequestFailure("Unexpected response: " + response);
            }
            if (response.body() == null) {
                throw new ResponseFailure("The response has no body: " + response);
            }

            return new Gson().fromJson(response.body().charStream(), CustomerAccessToken.class);
        } catch (IOException e) {
            throw new RequestFailure("Unexpected error during request: " + e.getMessage());
        }
    }
}
