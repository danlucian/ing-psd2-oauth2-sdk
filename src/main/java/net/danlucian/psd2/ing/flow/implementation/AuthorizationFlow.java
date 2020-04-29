package net.danlucian.psd2.ing.flow.implementation;

import net.danlucian.psd2.ing.exception.NotImplementedException;
import net.danlucian.psd2.ing.flow.Flow;
import net.danlucian.psd2.ing.rpc.payload.ApplicationAccessToken;
import net.danlucian.psd2.ing.rpc.payload.Country;
import net.danlucian.psd2.ing.rpc.payload.CustomerAccessToken;
import net.danlucian.psd2.ing.rpc.payload.PreflightUrl;

import java.net.URL;

public class AuthorizationFlow implements Flow {

    @Override
    public ApplicationAccessToken getAppAccessToken()
            throws NotImplementedException {
        throw new NotImplementedException("Method not implemented yet!");
    }

    @Override
    public PreflightUrl getPreflightUrl(ApplicationAccessToken applicationAccessToken, URL redirectBackUrl, Country country)
            throws NotImplementedException {
        throw new NotImplementedException("Method not implemented yet!");
    }

    @Override
    public CustomerAccessToken getCustomerAccessToken(ApplicationAccessToken applicationAccessToken, String authCode)
            throws NotImplementedException {
        throw new NotImplementedException("Method not implemented yet!");
    }
}
