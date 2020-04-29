package net.danlucian.psd2.ing.security;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

final public class SecurityUtil {

    public static byte[] parseDERFromPEM(final byte[] pem, final String beginDelimiter, final String endDelimiter) {
        String data = new String(pem);
        String[] tokens = data.split(beginDelimiter);
        tokens = tokens[1].split(endDelimiter);

        return DatatypeConverter.parseBase64Binary(tokens[0]);
    }

    public static RSAPrivateKey generatePrivateKeyFromDER(final byte[] keyBytes)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");

        return (RSAPrivateKey) factory.generatePrivate(spec);
    }

    public static X509Certificate generateCertificateFromDER(final byte[] certBytes) throws CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");

        return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));
    }

    public static String generatePrettifiedFromX509(X509Certificate certificate) {
        String BEGIN_CERT   = "-----BEGIN CERTIFICATE-----";
        String END_CERT     = "-----END CERTIFICATE-----";
        String LINE_SEPARATOR = System.getProperty("line.separator");

        final Base64.Encoder encoder = Base64.getMimeEncoder(64, LINE_SEPARATOR.getBytes());

        byte[] rawCrtText;
        try {
             rawCrtText = certificate.getEncoded();
        } catch (CertificateEncodingException e) {
            throw new SecurityException("Error while trying to create the prettified (text) version of the certificate: " + e.getMessage());
        }

        final String encodedCertText = new String(encoder.encode(rawCrtText));

        String result = encodedCertText.replaceAll("\\s+", "");
        result = BEGIN_CERT + result + END_CERT;

        result = result.replaceAll("\t", "");
        result = result.replaceAll("\r", "");
        result = result.replaceAll("\n", "");

        return result;
    }

    public static String generateDigestAndConvert(final String payload) {
        String result = "SHA-256=";
        MessageDigest digest;

        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
            result += Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("Error while trying to create the message digest: " + e.getMessage());
        }

        return result;
    }

    public static String generateSignature(final RSAPrivateKey privateKey, final String content) {
        byte[] signatureBytes;

        try {
            byte[] data = content.getBytes(StandardCharsets.UTF_8);
            Signature sig = Signature.getInstance("SHA256WithRSA");
            sig.initSign(privateKey);
            sig.update(data);

            signatureBytes = sig.sign();
        } catch (GeneralSecurityException e) {
            throw new SecurityException("Error while trying to create the signature: " + e.getMessage());
        }

        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    public static String generateSerialNumber(final X509Certificate certificate) {
        return certificate
                .getSerialNumber()
                .toString(16)
                .toUpperCase();
    }
}
