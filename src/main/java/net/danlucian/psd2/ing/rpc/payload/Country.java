package net.danlucian.psd2.ing.rpc.payload;

public enum Country {
    Austria("AT"),
    Belgium("BE"),
    Czech_Republic("CZ"),
    Germany("DE"),
    France("FR"),
    Italy("IT"),
    Luxembourg("LU"),
    Netherlands("NL"),
    Romania("RO"),
    Spain("ES"),
    Poland("PL"),
    Wholesale_Banking("WB");

    private String code;

    Country(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
