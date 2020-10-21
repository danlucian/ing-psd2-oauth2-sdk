package net.danlucian.psd2.ing.security;

import net.danlucian.psd2.ing.exception.FileException;
import net.danlucian.psd2.ing.exception.SecurityException;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.Objects;

import static net.danlucian.psd2.ing.security.SecurityUtil.*;

final public class ClientSecrets {

    private SSLContext context;
    private TrustManager trustManager;

    private X509Certificate clientTlsCert;
    private RSAPrivateKey   clientTlsKey;
    private X509Certificate clientSigningCert;
    private RSAPrivateKey   clientSigningKey;

    private X509Certificate sandboxTrustCertificate;
    private X509Certificate productionTrustCertificate;

    /**
     * @param clientCertificate should not be null
     * @param clientKey should not be null
     * @param signingCertificate should not be null
     * @param signingKey should not be null
     */
    public ClientSecrets(final byte[] clientCertificate,  final byte[] clientKey,
                         final byte[] signingCertificate, final byte[] signingKey) {
        Objects.requireNonNull(clientCertificate, "clientCertificate must not be null");
        Objects.requireNonNull(clientKey, "clientKey must not be null");
        Objects.requireNonNull(signingCertificate, "signingCertificate must not be null");
        Objects.requireNonNull(signingKey, "signingKey must not be null");

        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );

        try {
            this.createClientCertificates(clientCertificate, clientKey);
            this.createSigningCertificates(signingCertificate, signingKey);
            this.createTrustCertificate();
            this.createSSLContext();
        } catch (IOException e) {
            throw new FileException("Error while trying to load the certificate files: " + e.getMessage());
        } catch (GeneralSecurityException e) {
            throw new SecurityException("Error while trying to create the certificates: " + e.getMessage());
        }
    }

    /**
     * @param clientCertificate should not be null
     * @param clientKey should not be null
     * @param signingCertificate should not be null
     * @param signingKey should not be null
     */
    public ClientSecrets(final File clientCertificate,  final File clientKey,
                         final File signingCertificate, final File signingKey) {
        Objects.requireNonNull(clientCertificate, "clientCertificate must not be null");
        Objects.requireNonNull(clientKey, "clientKey must not be null");
        Objects.requireNonNull(signingCertificate, "signingCertificate must not be null");
        Objects.requireNonNull(signingKey, "signingKey must not be null");

        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );

        try {
            this.createClientCertificates(clientCertificate, clientKey);
            this.createSigningCertificates(signingCertificate, signingKey);
            this.createTrustCertificate();
            this.createSSLContext();
        } catch (IOException e) {
            throw new FileException("Error while trying to load the certificate files: " + e.getMessage());
        } catch (GeneralSecurityException e) {
            throw new SecurityException("Error while trying to create the certificates: " + e.getMessage());
        }
    }

    /**
     * @param clientTlsCert should not be null
     * @param clientTlsKey should not be null
     * @param clientSigningCert should not be null
     * @param clientSigningKey should not be null
     */
    public ClientSecrets(final X509Certificate clientTlsCert,    final RSAPrivateKey clientTlsKey,
                         final X509Certificate clientSigningCert,final RSAPrivateKey clientSigningKey) {
        Objects.requireNonNull(clientTlsCert, "clientTlsCert must not be null");
        Objects.requireNonNull(clientTlsKey, "clientTlsKey must not be null");
        Objects.requireNonNull(clientSigningCert, "clientSigningCert must not be null");
        Objects.requireNonNull(clientSigningKey, "clientSigningKey must not be null");

        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );

        this.clientTlsCert = clientTlsCert;
        this.clientTlsKey = clientTlsKey;
        this.clientSigningCert = clientSigningCert;
        this.clientSigningKey = clientSigningKey;

        try {
            this.createTrustCertificate();
            this.createSSLContext();
        } catch (IOException e) {
            throw new FileException("Error while trying to load the certificate files: " + e.getMessage());
        } catch (GeneralSecurityException e) {
            throw new SecurityException("Error while trying to create the certificates: " + e.getMessage());
        }
    }

    public SSLContext getContext() {
        return context;
    }

    public TrustManager getTrustManager() {
        return trustManager;
    }

    public X509Certificate getClientSigningCert() {
        return clientSigningCert;
    }

    public RSAPrivateKey getClientSigningKey() {
        return clientSigningKey;
    }

    private void createClientCertificates(final File clientCertificate, final File clientKey)
            throws IOException, GeneralSecurityException {

        byte[] clientCertBytes = parseDERFromPEM(
                Files.readAllBytes(clientCertificate.toPath()),
                "-----BEGIN CERTIFICATE-----",
                "-----END CERTIFICATE-----");
        byte[] clientKeyBytes = parseDERFromPEM(
                Files.readAllBytes(clientKey.toPath()),
                "-----BEGIN RSA PRIVATE KEY-----",
                "-----END RSA PRIVATE KEY-----");

        this.clientTlsCert = generateCertificateFromDER(clientCertBytes);
        this.clientTlsKey = generatePrivateKeyFromDER(clientKeyBytes);

    }

    private void createClientCertificates(final byte[] clientCertificate,  final byte[] clientKey)
            throws GeneralSecurityException {

        byte[] clientCertBytes = parseDERFromPEM(
                clientCertificate,
                "-----BEGIN CERTIFICATE-----",
                "-----END CERTIFICATE-----");
        byte[] clientKeyBytes = parseDERFromPEM(
                clientKey,
                "-----BEGIN RSA PRIVATE KEY-----",
                "-----END RSA PRIVATE KEY-----");

        this.clientTlsCert = generateCertificateFromDER(clientCertBytes);
        this.clientTlsKey = generatePrivateKeyFromDER(clientKeyBytes);

    }

    private void createSigningCertificates(final File signingCertificate, final File signingKey)
            throws IOException, GeneralSecurityException {

        byte[] signingCertBytes = parseDERFromPEM(
                Files.readAllBytes(signingCertificate.toPath()),
                "-----BEGIN CERTIFICATE-----",
                "-----END CERTIFICATE-----");
        byte[] signingKeyBytes = parseDERFromPEM(
                Files.readAllBytes(signingKey.toPath()),
                "-----BEGIN RSA PRIVATE KEY-----",
                "-----END RSA PRIVATE KEY-----");

        this.clientSigningCert = generateCertificateFromDER(signingCertBytes);
        this.clientSigningKey =  generatePrivateKeyFromDER(signingKeyBytes);

    }

    private void createSigningCertificates(final byte[] signingCertificate, final byte[] signingKey)
            throws GeneralSecurityException {

        byte[] signingCertBytes = parseDERFromPEM(
                signingCertificate,
                "-----BEGIN CERTIFICATE-----",
                "-----END CERTIFICATE-----");
        byte[] signingKeyBytes = parseDERFromPEM(
                signingKey,
                "-----BEGIN RSA PRIVATE KEY-----",
                "-----END RSA PRIVATE KEY-----");

        this.clientSigningCert = generateCertificateFromDER(signingCertBytes);
        this.clientSigningKey =  generatePrivateKeyFromDER(signingKeyBytes);

    }

    private byte[] loadCertificate(String fileName) throws IOException {
        InputStream is = ClientSecrets.class.getResourceAsStream(fileName);
        byte[] targetArray = new byte[is.available()];
        is.read(targetArray);
        is.close();

        return parseDERFromPEM(
                targetArray,
                "-----BEGIN CERTIFICATE-----",
                "-----END CERTIFICATE-----");
    }

    private void createTrustCertificate()
            throws IOException, CertificateException {

        byte[] sandboxTrustCertBytes    = loadCertificate("/api.sandbox.ing.com.cer");
        byte[] productionTrustCertBytes = loadCertificate("/api.ing.com.cer");

        this.sandboxTrustCertificate = generateCertificateFromDER(sandboxTrustCertBytes);
        this.productionTrustCertificate = generateCertificateFromDER(productionTrustCertBytes);
    }

    private void createSSLContext()
            throws  IOException, GeneralSecurityException {
        this.context = SSLContext.getInstance("SSL");

        final String keyStorePassword = "changeme";

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null);
        keyStore.setCertificateEntry("trust-sandbox", sandboxTrustCertificate);
        keyStore.setCertificateEntry("trust-production", productionTrustCertificate);
        keyStore.setCertificateEntry("cert-sandbox", clientTlsCert);
        keyStore.setKeyEntry(
                "key-sandbox",
                this.clientTlsKey,
                keyStorePassword.toCharArray(),
                new Certificate[]{this.clientTlsCert});

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(keyStore, keyStorePassword.toCharArray());

        KeyManager[] km = kmf.getKeyManagers();

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);
        this.trustManager = tmf.getTrustManagers()[0];

        this.context.init(km, tmf.getTrustManagers(), new SecureRandom());
    }
}
