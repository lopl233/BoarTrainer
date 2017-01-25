

import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public class klient {
    public static boolean isLogged=false;
    private static SSLConnector sslConnector;

    public static boolean logIn(String Login,String Password) {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "LoginRequest");
        data.put("login", Login);
        data.put("password", Password);
        JSONObject message = new JSONObject(data);
        String messageString = message.toString();
        try {
            PrintWriter pw = null;

            pw = new PrintWriter(sslConnector.sslsocket.getOutputStream());
            pw.write(messageString);
            pw.write("\n");
            pw.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(sslConnector.sslsocket.getInputStream()));
            String serverAnswer = br.readLine();

            JSONObject JSONanswer = new JSONObject(serverAnswer);
            String wynik = JSONanswer.getString("message_type");
            if(!wynik.equals("LoginRequest")){return false;}
            boolean islogged = JSONanswer.getBoolean("islogged");
            isLogged=islogged;
            return  islogged;
            } catch (IOException|JSONException e) {
                System.out.println(e);
                return false;
            }
    }//koniec funkcji logowania

    public static void GetData() {
    Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "GetBasicData");
    JSONObject message = new JSONObject(data);
    String messageString = message.toString();
        try {
        PrintWriter pw = null;

        pw = new PrintWriter(sslConnector.sslsocket.getOutputStream());
        pw.write(messageString);
        pw.write("\n");
        pw.flush();

        BufferedReader br = new BufferedReader(new InputStreamReader(sslConnector.sslsocket.getInputStream()));
        String serverAnswer = br.readLine();

        JSONObject JSONanswer = new JSONObject(serverAnswer);
        System.out.println(JSONanswer.getString("message_type"));
        System.out.println(JSONanswer.getString("Name"));
        System.out.println(JSONanswer.getString("Lastname"));


    } catch (IOException|JSONException e) {
        System.out.println(e);
    }
}

    public static void Register(String login, String password, String name, String lastname) {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "RegisterNewClient");
        data.put("login", login);
        data.put("password", password);
        data.put("name", name);
        data.put("lastname", lastname);
        JSONObject message = new JSONObject(data);
        String messageString = message.toString();
        try {
            PrintWriter pw = null;

            pw = new PrintWriter(sslConnector.sslsocket.getOutputStream());
            pw.write(messageString);
            pw.write("\n");
            pw.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(sslConnector.sslsocket.getInputStream()));
            String serverAnswer = br.readLine();

            JSONObject JSONanswer = new JSONObject(serverAnswer);
            System.out.println(JSONanswer.getString("message_type"));
            System.out.println(JSONanswer.getString("error_type"));


        } catch (IOException|JSONException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {

        String currentDir = System.getProperty("user.dir")+"/testkeysore.p12";
        System.setProperty("javax.net.ssl.keyStore",currentDir);
        System.setProperty("javax.net.ssl.keyStorePassword","dzikidzik");
        System.setProperty("javax.net.ssl.keyStoreType","PKCS12");
        System.setProperty("javax.net.ssl.trustStore",currentDir);
        System.setProperty("javax.net.ssl.trustStorePassword","dzikidzik");
        System.setProperty("javax.net.ssl.trustStoreType","PKCS12");

        sslConnector = SSLConnector.getInstance();
        try {
            sslConnector.sslsocket.startHandshake();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(logIn("admin", "haslo"));
        Register("admin","haslo","dziki","dzik");
        GetData();
    }
}
