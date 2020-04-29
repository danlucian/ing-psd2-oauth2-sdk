package net.danlucian.psd2.ing.rpc.payload;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApplicationAccessToken {
    @SerializedName("access_token") private String  accessToken;
    @SerializedName("expires_in")   private Long    expiration;
    @SerializedName("scope")        private String  scope;
    @SerializedName("token_type")   private String  tokenType;
    @SerializedName("keys")         private List<Key> keys;
    @SerializedName("client_id")    private String  clientId;

    public ApplicationAccessToken(String accessToken, Long expiration, String scope,
                                  String tokenType, List<Key> keys, String clientId) {
        this.accessToken = accessToken;
        this.expiration = expiration;
        this.scope = scope;
        this.tokenType = tokenType;
        this.keys = keys;
        this.clientId = clientId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public List<Key> getKeys() {
        return keys;
    }

    public void setKeys(List<Key> keys) {
        this.keys = keys;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}

class Key {
    @SerializedName("kty")  private String kty;
    @SerializedName("n")    private String n;
    @SerializedName("e")    private String e;
    @SerializedName("use")  private String use;
    @SerializedName("alg")  private String alg;
    @SerializedName("x5t")  private String x5t;

    public Key(String kty, String n, String e, String use, String alg, String x5t) {
        this.kty = kty;
        this.n = n;
        this.e = e;
        this.use = use;
        this.alg = alg;
        this.x5t = x5t;
    }

    public String getKty() {
        return kty;
    }

    public void setKty(String kty) {
        this.kty = kty;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getAlg() {
        return alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }

    public String getX5t() {
        return x5t;
    }

    public void setX5t(String x5t) {
        this.x5t = x5t;
    }
}
