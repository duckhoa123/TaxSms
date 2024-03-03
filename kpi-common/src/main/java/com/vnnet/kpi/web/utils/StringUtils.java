package com.vnnet.kpi.web.utils;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class StringUtils {

    public static boolean isBlank(String value) {
        return value == null || "".equals(value) || "null".equals(value) || "undefined".equals(value);
    }

    public static String random(int length) {
//		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        String NumericString = "0123456789";
        int n = length;
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (NumericString.length() * Math.random());
            sb.append(NumericString.charAt(index));
        }
        return sb.toString();
    }

    public static String thousandSeparator(long n, String ch) {
        // Counting number of digits
        int l = (int) Math.floor(Math.log10(n)) + 1;
        StringBuffer str = new StringBuffer("");
        int count = 0;
        long r = 0;
        // Checking if number of digits is greater than 3
        if (l > 3) {
            for (int i = l - 1; i >= 0; i--) {
                r = n % 10;
                n = n / 10;
                count++;
                if (((count % 3) == 0) && (i != 0)) {
                    // Parsing String value of Integer
                    str.append(String.valueOf(r));
                    // Appending the separator
                    str.append(ch);
                } else
                    str.append(String.valueOf(r));
            }
            str.reverse();
        }
        // If digits less than equal to 3, directly print n
        else
            str.append(String.valueOf(n));
        return str.toString();
    }

    public static String getTinyUrl(String url)  {
        try {
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(10000)
                    .setConnectTimeout(10000)
                    .setConnectionRequestTimeout(10000)
                    .build();
            CloseableHttpClient httpclient =  HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
            try {
                HttpHost proxy = new HttpHost("10.2.64.57", 8080, "http");
                RequestConfig config = RequestConfig.custom().setProxy(proxy).build();

                JSONObject params = new JSONObject();
                params.put("url", url);

                HttpPost post = new HttpPost("https://api.tinyurl.com/create");
                post.setEntity(new StringEntity(params.toString(), StandardCharsets.UTF_8));
                post.setHeader("Content-Type", "application/json");
                post.setHeader("Accept", "application/json");
                post.setHeader("Authorization", "Bearer tToFcumb8NcrgxyEOZZgB0qZla0LHJASOnTZscZyDPR7hBzIOnSiotr0JfUs");
                post.setConfig(config);
                CloseableHttpResponse response = null;
                try {
                    response = httpclient.execute(post);
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

                    String line = "";
                    String json = "";
                    while ((line = rd.readLine()) != null) {
                        json += line;
                    }
                    rd.close();

                    if (!StringUtils.isBlank(json)) {
                        JSONObject object = new JSONObject(json);
                        if (object.get("code").toString().equals("0")) {
                            JSONObject object1 = object.getJSONObject("data");
                            return object1.get("tiny_url").toString();
                        }
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                finally {
                    if (response != null)
                        response.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                if (httpclient != null) {
                    try {
                        httpclient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }
}
