package net.danlucian.psd2.ing.rpc.sandbox;

import com.google.gson.Gson;
import net.danlucian.psd2.ing.exception.rpc.RequestFailure;
import net.danlucian.psd2.ing.exception.rpc.ResponseFailure;
import net.danlucian.psd2.ing.rpc.Client;
import net.danlucian.psd2.ing.rpc.payload.ApplicationAccessToken;
import net.danlucian.psd2.ing.rpc.payload.Country;
import net.danlucian.psd2.ing.rpc.payload.PreflightUrl;
import net.danlucian.psd2.ing.security.ClientSecrets;
import net.danlucian.psd2.ing.time.DateUtil;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Objects;

import static net.danlucian.psd2.ing.security.SecurityUtil.generateDigestAndConvert;
import static net.danlucian.psd2.ing.security.SecurityUtil.generateSignature;

public class AuthorizationServerClient extends Client {

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

        final String digest         = generateDigestAndConvert("");
        final String date           = DateUtil.getCurrentDateAsString();

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        printWriter.println("(request-target): get " + PATH  + "?scope=" + scopes);
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

        HttpUrl.Builder httpBuilder = HttpUrl.parse(URL).newBuilder();
        httpBuilder.addEncodedQueryParameter("scope", scopes);

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .addHeader("Authorization", "Bearer " + applicationAccessToken.getAccessToken())
                .addHeader("Content-Type", "application/json")
                .addHeader("Digest", digest)
                .addHeader("Date", date)
                .addHeader("Signature", authorization)
                .get()
                .build();


        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RequestFailure("Unexpected response: " + response);
            }
            if (response.body() == null) {
                throw new ResponseFailure("The response has no body: " + response);
            }

            PreflightUrl preflightUrl = new Gson().fromJson(response.body().charStream(), PreflightUrl.class);
            preflightUrl.enrichUri(
                    country,
                    applicationAccessToken.getClientId(),
                    redirectBackUrl.toString(),
                    scopes
            );

            return preflightUrl;
        } catch (IOException e) {
            throw new RequestFailure("Unexpected error during request: " + e.getMessage());
        }
    }
}
