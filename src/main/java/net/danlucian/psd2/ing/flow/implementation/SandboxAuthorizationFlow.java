package net.danlucian.psd2.ing.flow.implementation;

import net.danlucian.psd2.ing.flow.Flow;
import net.danlucian.psd2.ing.rpc.payload.ApplicationAccessToken;
import net.danlucian.psd2.ing.rpc.payload.Country;
import net.danlucian.psd2.ing.rpc.payload.CustomerAccessToken;
import net.danlucian.psd2.ing.rpc.payload.PreflightUrl;
import net.danlucian.psd2.ing.rpc.sandbox.AppAccessTokenClient;
import net.danlucian.psd2.ing.rpc.sandbox.AuthorizationServerClient;
import net.danlucian.psd2.ing.rpc.sandbox.CustomerAccessTokenClient;
import net.danlucian.psd2.ing.security.ClientSecrets;

import java.net.URL;

public class SandboxAuthorizationFlow implements Flow {

    private final AppAccessTokenClient appAccessTokenClient;
    private final AuthorizationServerClient authorizationServerClient;
    private final CustomerAccessTokenClient customerAccessTokenClient;

    public SandboxAuthorizationFlow(ClientSecrets clientSecrets, String scopes) {
        this.appAccessTokenClient       = new AppAccessTokenClient(clientSecrets);
        this.authorizationServerClient  = new AuthorizationServerClient(clientSecrets, scopes);
        this.customerAccessTokenClient  = new CustomerAccessTokenClient(clientSecrets, scopes);
    }

    @Override
    public ApplicationAccessToken getAppAccessToken() {
        return appAccessTokenClient.getToken();
    }

    @Override
    public PreflightUrl getPreflightUrl(final ApplicationAccessToken applicationAccessToken,
                                        final URL redirectBackUrl,
                                        final Country country) {
        return authorizationServerClient.getPreflightUrl(applicationAccessToken, redirectBackUrl, country);
    }

    @Override
    public CustomerAccessToken getCustomerAccessToken(final ApplicationAccessToken applicationAccessToken, final String authCode) {
        return customerAccessTokenClient.getToken(applicationAccessToken, authCode);
    }
}
