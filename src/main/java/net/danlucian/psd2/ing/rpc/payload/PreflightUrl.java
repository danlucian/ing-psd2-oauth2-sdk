package net.danlucian.psd2.ing.rpc.payload;

import com.google.gson.annotations.SerializedName;

public class PreflightUrl {
    @SerializedName("location") private String location;

    public PreflightUrl(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public PreflightUrl enrichUri(final Country country,
                                  final String clientId,
                                  final String redirectBackUrl,
                                  final String scopes) {
        this.location = this.location
                + "/" + country.getCode().toLowerCase()
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectBackUrl
                + "&scope=" + scopes
                    .replaceAll("%3A", ":")
                    .replaceAll("\\+", "%20");
        return this;
    }
}
