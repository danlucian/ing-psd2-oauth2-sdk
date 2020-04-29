package net.danlucian.psd2.ing.exception.rpc;

public class RequestFailure extends RuntimeException {
    public RequestFailure(String message) {
        super(message);
    }
}
