import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class SupportSSL extends Thread {
    static private SSLSocket sslsocket;
    public SupportSSL(SSLSocket sslsocket){
        super("SupportSSL");
        this.sslsocket = sslsocket;
    }
    public SupportSSL(){
        super("SupportSSL");
    }
    @Override
    public void run() {


        while (true) {
            try {

                BufferedReader br = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));
                PrintWriter pw = new PrintWriter(sslsocket.getOutputStream());

                String data = br.readLine();
                if(!(data ==null))
                System.out.println(data);

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
