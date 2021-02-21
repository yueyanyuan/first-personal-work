package ����ͳ�Ʒֲ���Ϣͼ;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
 
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import org.json.JSONException;
import org.json.JSONObject;//�ɸ�����Ҫ����jar
 
public class JavaDemo {
    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent =  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
     
    public static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
     
    //�����������KEY
    public static final String APICODE ="a227d1fabb0a4967882497f7df57aeaf";
 
    //1.API����
    public static void getRequest(){
        String result =null;
        String url ="https://api.yonyoucloud.com/apis/dst/ncov/wholeworld";//����ӿڵ�ַ
        String method = "GET";
        String paramFormat = "form";
        Map<String, Object> params = new HashMap<String, Object>();//�������
 
        Map<String, Object> headerParams = new HashMap<String, Object>();//����ͷ����
        headerParams.put("apicode", APICODE);//APICODE
 
        try {
            result = net(url, params, headerParams, method, paramFormat);
            System.out.println(result);
            save(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    /**
     *
     * @param strUrl �����ַ
     * @param params �������
     * @param method ���󷽷�
     * @return  ���������ַ���
     * @throws Exception
     */
    public static String net(String strUrl, Map<String,Object> params, Map<String,Object> headerParams,String method, String paramFormat) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            String contentType = null;
            if(headerParams.containsKey("Content-Type"))
                contentType = headerParams.get("Content-Type").toString();
                 
            StringBuffer sb = new StringBuffer();
            if(method==null || method.equals("GET")){
                strUrl = strUrl+"?"+urlencode(params);
            }
             
            trustAllHttpsCertificates();
            HttpsURLConnection.setDefaultHostnameVerifier(DO_NOT_VERIFY);
             
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if(method==null || method.equals("GET")){
                conn.setRequestMethod("GET");
            }else{
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", userAgent);
            for (String i : headerParams.keySet()) {
                conn.setRequestProperty(i, headerParams.get(i).toString());
            }
            if("form".equals(paramFormat) && !"application/x-www-form-urlencoded".equals(contentType) && !"application/xml".equals(contentType)) {
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            }
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params!= null && method.equals("POST")) {
                try {
                    OutputStream out = conn.getOutputStream();
                    if("form".equals(paramFormat)) {
                        if("application/x-www-form-urlencoded".equals(contentType))
                            out.write(urlencode(params).getBytes("utf-8"));
                        else if("application/xml".equals(contentType))
                            out.write(xmlencode(params).getBytes("utf-8"));
                        else
                            out.write(jsonencode(params).getBytes("utf-8"));
                    } else
                        out.write(params.toString().getBytes("utf-8"));
                     
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }
    
    private static void save(String data) {
        BufferedWriter writer = null;
        File file = new File("global_epidemic_statistics.json");
        //����ļ������ڣ����½�һ��
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //д��
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,false), "UTF-8"));
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("���ݱ���ɹ���");
    }

     
    //��map��תΪ���������
    public static String urlencode(Map<String,Object>data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                if(("").equals(i.getKey())) {
                    sb.append(URLEncoder.encode(i.getValue()+"","UTF-8"));
                } else {
                    sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
     
     //��map��תΪ���������
    public static String jsonencode(Map<String,Object>data) throws JSONException {
        JSONObject jparam = new JSONObject();
        for (Map.Entry i : data.entrySet())
            jparam.put((String) i.getKey(), i.getValue());
          
        return jparam.toString();
    }
     
    //��map��תΪ���������
    public static String xmlencode(Map<String,Object>data) {
        StringBuffer xmlData = new StringBuffer(); 
        xmlData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        for (Map.Entry i : data.entrySet())
             xmlData.append("<" + i.getKey() + ">" + i.getValue() + "</" + i.getKey() + ">");
         
        return xmlData.toString();
    }
     
        static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
 
        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }
 
        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }
 
        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
 
        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }
 
    private static void trustAllHttpsCertificates() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
     
    public static void main(String[] args) {
        getRequest();
    }
}
