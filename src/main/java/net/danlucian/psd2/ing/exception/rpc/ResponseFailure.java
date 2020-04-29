package net.danlucian.psd2.ing.exception.rpc;

public class ResponseFailure extends RuntimeException {
    public ResponseFailure(String message) {
        super(message);
    }
}
