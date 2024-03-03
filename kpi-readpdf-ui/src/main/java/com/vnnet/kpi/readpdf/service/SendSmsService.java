package com.vnnet.kpi.readpdf.service;

import com.vnnet.kpi.web.model.SysMessagDt;
import com.vnnet.kpi.web.persistence.SysMessagDtMapper;
import com.vnnet.kpi.web.persistence.SysMessagExMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

@Service
@Transactional
public class SendSmsService {

    public static boolean isRuning = false;
    public static String TOKEN = "";

    private static String SENDER = "CUCTHUE_HP";
    private static String USER_NAME = "cucthuehpcskh";
    private static String PASSWORD = "123456";

    public int sendSMS(String isdn, String content) {
        try {
//            System.setProperty("http.proxyHost", "10.2.64.57");
//            System.setProperty("http.proxyPort", "8080");
            String token = getToken();
            return sendSMS(token, isdn, content);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 500;
    }

    private String getToken() {
        HttpURLConnection con = null;
        try {
            String url = "http://45.121.27.83/smsg/login.jsp?userName=" + USER_NAME + "&password=" + PASSWORD + "&bindMode=T";
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setReadTimeout(10000);
            con.setConnectTimeout(10000);
            con.setDoInput(true);
            con.setDoOutput(true);
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                JSONObject object = new JSONObject(response.toString());
                return object.getString("sid");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (con != null)
                con.disconnect();
        }
        return "";
    }

    private int sendSMS(String token, String isdn, String content) {
        HttpURLConnection con = null;
        try {
            String message = URLEncoder.encode(content, "UTF-8");
            String url = "http://45.121.27.83/smsg/send_2.jsp?enCoding=ALPHA_UCS2&sid=" + token + "&sender=" + SENDER + "&recipient=" + isdn + "&content=" + message;
            URL obj = new URL(url);

            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setReadTimeout(10000);
            con.setConnectTimeout(10000);
            con.setDoInput(true);
            con.setDoOutput(true);
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine = "";
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject object = new JSONObject(response.toString());
                return object.getInt("status");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (con != null)
                con.disconnect();
        }
        return 500;
    }

}
