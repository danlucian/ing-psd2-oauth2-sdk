package net.danlucian.psd2.ing.rpc;

import com.google.gson.Gson;
import net.danlucian.psd2.ing.exception.rpc.RequestFailure;
import net.danlucian.psd2.ing.exception.rpc.ResponseFailure;
import net.danlucian.psd2.ing.security.ClientSecrets;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

import static net.danlucian.psd2.ing.security.SecurityUtil.generateDigestAndConvert;

public abstract class Client {

    protected final ClientSecrets clientSecrets;
    protected final String scopes;

    protected final String digest = generateDigestAndConvert("grant_type=client_credentials");

    /**
     * @param clientSecrets must not be null
     * @param scopes must not be null
     */
    public Client(final ClientSecrets clientSecrets, final String scopes) {
        this.clientSecrets = Objects.requireNonNull(clientSecrets, "clientSecrets must not be null");
        this.scopes = Objects.requireNonNull(scopes, "scopes must not be null");;
    }

    /**
     * @param clientSecrets must not be null
     */
    public Client(final ClientSecrets clientSecrets) {
        this.clientSecrets = Objects.requireNonNull(clientSecrets, "clientSecrets must not be null");
        this.scopes = "";
    }

    protected String signingTemplate(final String httpVerb, final String uri, final String currentDate) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        printWriter.print("(request-target): " + httpVerb + " " + uri + "\n");
        printWriter.print("date: " + currentDate + "\n");
        printWriter.print("digest: " + digest);

        return stringWriter.toString();
    }

    protected String signingTemplate(final String httpVerb, final String uri, final String currentDate, final String customDigest) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        printWriter.print("(request-target): " + httpVerb + " " + uri + "\n");
        printWriter.print("date: " + currentDate + "\n");
        printWriter.print("digest: " + customDigest);

        return stringWriter.toString();
    }

    protected <T> T executeRequest(OkHttpClient client, Request request, Class<T> responseClass) {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RequestFailure("Unexpected response: " + response);
            }
            if (response.body() == null) {
                throw new ResponseFailure("The response has no body: " + response);
            }

            return new Gson().fromJson(response.body().charStream(), responseClass);
        } catch (IOException e) {
            throw new RequestFailure("Unexpected error during request: " + e.getMessage());
        }
    }
}
