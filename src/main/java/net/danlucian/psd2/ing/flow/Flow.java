package net.danlucian.psd2.ing.flow;

import net.danlucian.psd2.ing.rpc.payload.ApplicationAccessToken;
import net.danlucian.psd2.ing.rpc.payload.Country;
import net.danlucian.psd2.ing.rpc.payload.CustomerAccessToken;
import net.danlucian.psd2.ing.rpc.payload.PreflightUrl;

import java.net.URL;

public interface Flow {

    ApplicationAccessToken getAppAccessToken();

    PreflightUrl getPreflightUrl(final ApplicationAccessToken applicationAccessToken,
                                 final URL redirectBackUrl,
                                 final Country country);

    CustomerAccessToken getCustomerAccessToken(final ApplicationAccessToken applicationAccessToken,
                                               final String authCode);
}
