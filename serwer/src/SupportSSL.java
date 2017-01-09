import java.sql.*;


import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketException;
import java.sql.DriverManager;
import java.util.LinkedHashMap;
import java.util.Map;

public class SupportSSL extends Thread {
    private SSLSocket sslsocket;
    private int USER_ID = -1;

    public SupportSSL(SSLSocket sslsocket) {
        super("SupportSSL");
        this.sslsocket = sslsocket;
    }

    public SupportSSL() {
        super("SupportSSL");
    }

    @Override
    public void run() {


        while (true) {
            try {

                BufferedReader br = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));
                PrintWriter pw = new PrintWriter(sslsocket.getOutputStream());

                String data = br.readLine();
                if (!(data == null))
                    try {
                        System.out.println(data);
                        JSONObject clientRequest = new JSONObject(data);
                        String message_type = clientRequest.getString("message_type");
                        if (message_type.equals("LoginRequest")) {
                            pw.println(LoginRequest(clientRequest));
                            pw.flush();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


            } catch (SocketException ioe) {
                return;
            } catch (IOException ioe) {
                return;
            }
        }
    }

    private JSONObject LoginRequest(JSONObject klientRequest) {
        try {
            String login = klientRequest.getString("login");
            String password = klientRequest.getString("password");


//            String driverName = "org.gjt.mm.mysql.Driver";
//            Class.forName("com.mysql.jdbc");
//            String serverName = "localhost";
//            String mydatabase = "mydatabase";
//            String url = "jdbc:mysql://" + "localhost" + "/" + "dzik";
//            Connection connection = DriverManager.getConnection(url, "root", "");


            Map<String, String> data = new LinkedHashMap<>();
            data.put("message_type", "LoginRequest");
            data.put("islogged", "true");
            return new JSONObject(data);

        } catch (JSONException e) {
            Map<String, String> data = new LinkedHashMap<>();
            data.put("message_type", "LoginRequest");
            data.put("islogged", "false");
            return new JSONObject(data);
//        } catch (ClassNotFoundException e) {
//            System.out.println(e);
//            Map<String, String> data = new LinkedHashMap<>();
//            data.put("message_type", "LoginRequest");
//            data.put("islogged", "false");
//            return new JSONObject(data);
//        } catch (SQLException e) {
//            Map<String, String> data = new LinkedHashMap<>();
//            data.put("message_type", "LoginRequest");
//            data.put("islogged", "false");
//            return new JSONObject(data);
//        }
        }
    }
}