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
import java.util.Calendar;
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
                        System.out.println("Wiadomosc od usera'a: "+USER_ID+"=  "+data);
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

    private Connection MakeConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        String serverName = "localhost";
        String mydatabase = "mydatabase";
        String url = "jdbc:mysql://" + "localhost" + "/" + "dzik";
        return DriverManager.getConnection(url, "root", "dzikidzik");


    }

    private JSONObject CreateAnswer(JSONObject message){
        String message_type="";
        try {
            message_type = message.getString("message_type");
        } catch (JSONException e) {return GetErrorJSON("NoMessageType");}

        switch (message_type){
            case "LoginRequest" : return LoginRequest(message);
            case "GetBasicData" : return GetBasicData();
            case "UpdateClientData" : return UpdateClientData(message);
            case "RegisterNewClient" : return RegisterNewClient(message);
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

            Connection connection = MakeConnection();

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

    private JSONObject GetBasicData(){

        if (USER_ID == -1)return GetErrorJSON("NotLogged");

        try {

            Connection connection = MakeConnection();

            //budowanie i realizowanie zapytania
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM USER_DATA WHERE USER_ID ='"+USER_ID+"'";
            ResultSet rs = stmt.executeQuery(sql);

            //przetwarzanie odpowiedzi z bazy
            if(!rs.next()){
                Map<String, String> data = new LinkedHashMap<>();
                data.put("message_type", "GetBasicData");
                data.put("Name", rs.getString("*"));
                data.put("Lastname",  rs.getString("*"));
                return new JSONObject(data);}

            Map<String, String> data = new LinkedHashMap<>();
            data.put("message_type", "GetBasicData");
            data.put("Name", rs.getString("NAME"));
            data.put("Lastname",  rs.getString("LASTNAME"));
            return new JSONObject(data);

        } catch (ClassNotFoundException|SQLException e) {return GetErrorJSON("ServerError");}
    }

    private JSONObject RegisterNewClient(JSONObject message){
        try {
            Connection connection = MakeConnection();
            String login = message.getString("login");
            String password = message.getString("password");
            String imie = message.getString("name");
            String nazwisko = message.getString("lastname");

            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM logins WHERE LOGIN ='"+login+"'";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){return GetErrorJSON("LoginTaken");}

            sql = "INSERT INTO `logins` (`USER_ID`, `LOGIN`, `PASSWORD`) VALUES (NULL,'"+login+" ','"+password+"')";
            stmt.executeUpdate(sql);

            System.out.println("1");

            sql = "SELECT * FROM logins WHERE LOGIN ='"+login+"'";
            rs = stmt.executeQuery(sql);
            rs.next();
            String User_ID = rs.getString("USER_ID");

            sql = "INSERT INTO `USER_DATA` (`USER_ID`, `NAME`, `LASTNAME`) VALUES ('"+User_ID+"', '"+imie+"', '"+nazwisko+"')";
            stmt.executeUpdate(sql);

            Map<String, String> data = new LinkedHashMap<>();
            data.put("message_type", "RegisterNewClient");
            return new JSONObject(data);

        } catch (SQLException|ClassNotFoundException|JSONException e) {;return GetErrorJSON("ServerError");}
    }

    private JSONObject UpdateClientData(JSONObject message){
        try {
            if(USER_ID==-1){return GetErrorJSON("NotLogged");}

            Connection connection = MakeConnection();
            String imie = message.getString("name");
            String nazwisko = message.getString("lastname");

            Statement stmt = connection.createStatement();
            String sql = "UPDATE user_data set NAME = '"+imie+"', LASTNAME = '"+nazwisko+"' where USER_ID = '"+USER_ID+"'";
            stmt.executeQuery(sql);

            Map<String, String> data = new LinkedHashMap<>();
            data.put("message_type", "UpdateClientData");
            return new JSONObject(data);

        } catch (SQLException|ClassNotFoundException|JSONException e) {return GetErrorJSON("ServerError");}
    }

}
