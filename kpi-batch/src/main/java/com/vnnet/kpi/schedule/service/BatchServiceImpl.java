package com.vnnet.kpi.schedule.service;

import com.vnnet.kpi.web.config.Constants;
import com.vnnet.kpi.web.model.*;
import com.vnnet.kpi.web.persistence.SysMessagDtMapper;
import com.vnnet.kpi.web.persistence.SysMessagExMapper;
import com.vnnet.kpi.web.persistence.SysUserMapper;
import com.vnnet.kpi.web.service.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.checkerframework.checker.units.qual.A;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.cert.X509Certificate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;


@Service
@Transactional
public class BatchServiceImpl implements BatchService {

    private static final Logger logger = LoggerFactory.getLogger(BatchServiceImpl.class);

    public static boolean isRuning = false;
    public static String TOKEN = "";

    private static String SENDER = "CUCTHUE_HP";
    private static String USER_NAME = "cucthuehpcskh";
    private static String PASSWORD = "123456";

    @Autowired
    private SysMessagDtMapper sysMessagDtMapper;
    @Autowired
    private SysMessagExMapper sysMessagExMapper;

    @Override
    public void process() {
        System.setProperty("http.proxyHost", "10.2.64.57");
        System.setProperty("http.proxyPort", "8080");
        if (!isRuning) {
            isRuning = true;
            loadDataFromDB();
            isRuning = false;
        }
    }

    private void loadDataFromDB() {
        try {
            List<SysMessagDt> sysMessagDts = sysMessagExMapper.sendMessage();
            if (sysMessagDts != null && sysMessagDts.size() > 0) {
                getToken();
                for (SysMessagDt messagDt : sysMessagDts) {
                    sendSMS(messagDt);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getToken() {
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
                TOKEN = object.getString("sid");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (con != null)
                con.disconnect();
        }
    }

    private void sendSMS(SysMessagDt messagDt) {
        HttpURLConnection con = null;
        try {
            String message = URLEncoder.encode(messagDt.getSmsContent(), "UTF-8");
            String url = "http://45.121.27.83/smsg/send_2.jsp?enCoding=ALPHA_UCS2&sid=" + TOKEN + "&sender=" + SENDER + "&recipient=" + messagDt.getCalledNumber() + "&content=" + message;
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
                int status = object.getInt("status");
                if (status == 200)
                    messagDt.setStatus((byte) 1);
                else
                    messagDt.setStatus((byte) 2);
                messagDt.setSendResult(response.toString());
                sysMessagDtMapper.updateByPrimaryKeySelective(messagDt);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (con != null)
                con.disconnect();
        }
    }
    private  void check (){

    }

}
