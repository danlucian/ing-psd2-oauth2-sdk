package net.danlucian.psd2.ing.rpc.payload;

import com.google.gson.annotations.SerializedName;

public class CustomerAccessToken {
    @SerializedName("access_token")             private String accessToken;
    @SerializedName("refresh_token")            private String refreshToken;
    @SerializedName("token_type")               private String tokenType;
    @SerializedName("scope")                    private String scope;
    @SerializedName("expires_in")               private Long expiresIn;
    @SerializedName("refresh_token_expires_in") private Long refreshTokenExpiresIn;

    public CustomerAccessToken(String accessToken, String refreshToken, String tokenType,
                               String scope, Long expiresIn, Long refreshTokenExpiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.scope = scope;
        this.expiresIn = expiresIn;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Long getRefreshTokenExpiresIn() {
        return refreshTokenExpiresIn;
    }

    public void setRefreshTokenExpiresIn(Long refreshTokenExpiresIn) {
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }
}
