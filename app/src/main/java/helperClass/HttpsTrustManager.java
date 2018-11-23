package helperClass;

import android.util.Log;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by rio.issac on 5/12/2015.
 */
public class HttpsTrustManager implements TrustManager, X509TrustManager {

    private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[]{};
    private static TrustManager[] trustManagers;

    public static SSLContext getSSLContext() {
        final TrustManager[] trustAllCerts =
                new TrustManager[] {new HttpsTrustManager()};

        try {
            final SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, trustAllCerts, new SecureRandom());
            return context;

        } catch (Exception exc) {
            Log.e("CertHostTruster", "Unable to initialize the Trust Manager to trust all the "
                    + "SSL certificates and HTTPS hosts.", exc);
            return null;
        }
    }
    public static HostnameVerifier getAllHostnamesVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                if (hostname.equals("demo.mariapps.com"))
                    return true;
                else
                    return false;
            }
        };
    }
    public static void allowAllSSL() {
        final TrustManager[] trustAllCerts =
                new TrustManager[] {new HttpsTrustManager()};

        try {
            final SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(getAllHostnamesVerifier());

        } catch (Exception exc) {
            Log.e("CertHostTruster", "Unable to initialize the Trust Manager to trust all the "
                    + "SSL certificates and HTTPS hosts.", exc);
        }
//        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//
//            @Override
//            public boolean verify(String arg0, SSLSession arg1) {
//                return true;
//            }
//
//        });
//
//        SSLContext context = null;
//        if (trustManagers == null) {
//            trustManagers = new TrustManager[]{new HttpsTrustManager()};
//        }
//
//        try {
//            context = SSLContext.getInstance("TLS");
//            context.init(null, trustManagers, new SecureRandom());
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }
//
//        HttpsURLConnection.setDefaultSSLSocketFactory(context
//                .getSocketFactory());

    }

    @Override
    public void checkClientTrusted(
            X509Certificate[] x509Certificates, String s)
            throws CertificateException {

    }

    @Override
    public void checkServerTrusted(
            X509Certificate[] x509Certificates, String s)
            throws CertificateException {
        try {
            x509Certificates[0].checkValidity();
        } catch (Exception e) {
            throw new CertificateException("Certificate not valid or trusted.");
        }
    }

    public boolean isClientTrusted(X509Certificate[] chain) {
        return true;
    }

    public boolean isServerTrusted(X509Certificate[] chain) {
        return true;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return _AcceptedIssuers;
    }

}