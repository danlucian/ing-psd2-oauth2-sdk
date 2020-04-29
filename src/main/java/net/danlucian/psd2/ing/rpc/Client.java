package net.danlucian.psd2.ing.rpc;

import net.danlucian.psd2.ing.security.ClientSecrets;

import java.util.Objects;

public abstract class Client {

    protected final ClientSecrets clientSecrets;
    protected final String scopes;

    /**
     * @param clientSecrets should not be null
     * @param scopes should not be null
     */
    public Client(final ClientSecrets clientSecrets, final String scopes) {
        this.clientSecrets = Objects.requireNonNull(clientSecrets, "clientSecrets must not be null");
        this.scopes = Objects.requireNonNull(scopes, "scopes must not be null");;
    }

    /**
     * @param clientSecrets should not be null
     */
    public Client(final ClientSecrets clientSecrets) {
        this.clientSecrets = Objects.requireNonNull(clientSecrets, "clientSecrets must not be null");
        this.scopes = "";
    }
}
