import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class klient {


    public static void main(String[] args) {

        String currentDir = System.getProperty("user.dir")+"/testkeysore.p12";
        System.out.println(currentDir);
        System.setProperty("javax.net.ssl.keyStore",currentDir);
        System.setProperty("javax.net.ssl.keyStorePassword","dzikidzik");
        System.setProperty("javax.net.ssl.keyStoreType","PKCS12");
        System.setProperty("javax.net.ssl.trustStore",currentDir);
        System.setProperty("javax.net.ssl.trustStorePassword","dzikidzik");
        System.setProperty("javax.net.ssl.trustStoreType","PKCS12");

        SSLConnector sslConnector = SSLConnector.getInstance();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(sslConnector.sslsocket.getInputStream()));
            PrintWriter pw = new PrintWriter(sslConnector.sslsocket.getOutputStream());
            pw.println("What is she?");
            pw.close();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();}
    }
}
