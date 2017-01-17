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
                        pw.println(CreateAnswer(clientRequest));
                        pw.flush();
                    } catch (JSONException e) {
                        pw.println(GetErrorJSON("JSONPARSE"));
                        pw.flush();
                }


            } catch (SocketException ioe) {return;
            } catch (IOException ioe) {return;}
        }//koniec while'a
    }//koniec run'a

    private JSONObject CreateAnswer(JSONObject message){
        String message_type="";
        try {
            message_type = message.getString("message_type");
        } catch (JSONException e) {return GetErrorJSON("NoMessageType");}

        switch (message_type){
            case "LoginRequest" : return LoginRequest(message);
            //case "inny request ..." return jakas funkcja ...
            default : return GetErrorJSON("WrongMessageType");
        }
    }

    private JSONObject GetErrorJSON(String type){
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "ERROR");
        data.put("error_type", type);
        return new JSONObject(data);
    }

    private JSONObject LoginRequest(JSONObject klientRequest) {
        try {
            String login = klientRequest.getString("login");
            String password = klientRequest.getString("password");

            //tworzenie polaczenia z baza
            Class.forName("com.mysql.jdbc.Driver");
            String serverName = "localhost";
            String mydatabase = "mydatabase";
            String url = "jdbc:mysql://" + "localhost" + "/" + "dzik";
            Connection connection = DriverManager.getConnection(url, "root", "");

            //budowanie i realizowanie zapytania
            Statement stmt = null;
            stmt = connection.createStatement();
            String sql = "SELECT USER_ID,PASSWORD FROM logins";
            ResultSet rs = stmt.executeQuery(sql);

            //przetwarzanie odpowiedzi z bazy
            if(!rs.next()){
                Map<String, String> data = new LinkedHashMap<>();
                data.put("message_type", "LoginRequest");
                data.put("islogged", "false");
                return new JSONObject(data);}

                String pass = rs.getString("PASSWORD");
                if(!pass.equals(password)){
                    Map<String, String> data = new LinkedHashMap<>();
                    data.put("message_type", "LoginRequest");
                    data.put("islogged", "false");
                    return new JSONObject(data);}

            Map<String, String> data = new LinkedHashMap<>();
            USER_ID = rs.getInt("USER_ID");
            data.put("message_type", "LoginRequest");
            data.put("islogged", "true");
            return new JSONObject(data);

        } catch (JSONException|ClassNotFoundException|SQLException e) {return GetErrorJSON("ServerError");}
    }
}
