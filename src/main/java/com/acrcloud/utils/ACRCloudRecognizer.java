/**
 *
 *  @author qinxue.pan E-mail: xue@acrcloud.com
 *  @version 1.0.0
 *  @create 2015.10.01
 *  
 **/

/*
Copyright 2015 ACRCloud Recognizer v1.0.0

This module can recognize ACRCloud by most of audio/video file. 
        Audio: mp3, wav, m4a, flac, aac, amr, ape, ogg ...
        Video: mp4, mkv, wmv, flv, ts, avi ...
*/

package com.acrcloud.utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

// import commons-codec-<version>.jar, download from http://commons.apache.org/proper/commons-codec/download_codec.cgi


@Service
public class ACRCloudRecognizer {

    @Value("${acrcloud.host}")
    private String host;

    @Value("${acrcloud.accessKey}")
    private String accessKey;

    @Value("${acrcloud.secret}")
    private String accessSecret;

    private int timeout = 5 * 1000; // ms
    private boolean debug = false;


    /**
      *
      *  recognize by wav audio buffer(RIFF (little-endian) data, WAVE audio, Microsoft PCM, 16 bit, mono 8000 Hz) 
      *
      *  @param wavAudioBuffer query audio buffer
      *  @param wavAudioBufferLen the length of wavAudioBuffer
      *  
      *  @return result 
      *
      **/
    public String recognize(byte[] wavAudioBuffer, int wavAudioBufferLen)
    {
        String result = ACRCloudStatusCode.NO_RESULT;
        try {
            byte[] fp = ACRCloudExtrTool.createFingerprint(wavAudioBuffer, wavAudioBufferLen, false);
            if (fp == null) {
                return ACRCloudStatusCode.DECODE_AUDIO_ERROR;
            }
            if (fp.length <= 0) {
                return ACRCloudStatusCode.NO_RESULT;
            }
            result = this.doRecogize(fp);
        } catch (Exception e) {
            e.printStackTrace();
            result = ACRCloudStatusCode.UNKNOW_ERROR;
        }
        return result;
    }

    /**
      *
      *  recognize by buffer of (Audio/Video file)
      *          Audio: mp3, wav, m4a, flac, aac, amr, ape, ogg ...
      *          Video: mp4, mkv, wmv, flv, ts, avi ...
      *
      *  @param fileBuffer query buffer
      *  @param fileBufferLen the length of fileBufferLen 
      *  @param startSeconds skip (startSeconds) seconds from from the beginning of fileBuffer
      *  
      *  @return result 
      *
      **/
    public String recognizeByFileBuffer(byte[] fileBuffer, int fileBufferLen, int startSeconds)
    {
        String result = ACRCloudStatusCode.NO_RESULT;
        try {
            byte[] fp = ACRCloudExtrTool.createFingerprintByFileBuffer(fileBuffer, fileBufferLen, startSeconds, 12, false);
            if (fp == null) {
                return ACRCloudStatusCode.DECODE_AUDIO_ERROR;
            }
            if (fp.length <= 0) {
                return ACRCloudStatusCode.NO_RESULT;
            }
            result = this.doRecogize(fp);
        } catch (Exception e) {
            e.printStackTrace();
            result = ACRCloudStatusCode.UNKNOW_ERROR;
        }
        return result;
    }

    /**
      *
      *  recognize by file path of (Audio/Video file)
      *          Audio: mp3, wav, m4a, flac, aac, amr, ape, ogg ...
      *          Video: mp4, mkv, wmv, flv, ts, avi ...
      *
      *  @param filePath query file path
      *  @param startSeconds skip (startSeconds) seconds from from the beginning of (filePath)
      *  
      *  @return result 
      *
      **/
    public String recognizeByFile(String filePath, int startSeconds)
    {
        String result = ACRCloudStatusCode.NO_RESULT;
        try {
            byte[] fp = ACRCloudExtrTool.createFingerprintByFile(filePath, startSeconds, 12, false);
            if (fp == null) {
                return ACRCloudStatusCode.DECODE_AUDIO_ERROR;
            }
            if (fp.length <= 0) {
                return ACRCloudStatusCode.NO_RESULT;
            }
            result = this.doRecogize(fp);
        } catch (Exception e) {
            e.printStackTrace();
            result = ACRCloudStatusCode.UNKNOW_ERROR;
        }
        return result;
    }
 
    private String doRecogize(byte[] fp) {
        String method = "POST";
        String httpURL = "/v1/identify";
        String dataType = "fingerprint";
        String sigVersion = "1";
        String timestamp = getUTCTimeSeconds();

        String reqURL = "http://" + host + httpURL;

        String sigStr = method + "\n" + httpURL + "\n" + accessKey + "\n" + dataType + "\n" + sigVersion + "\n" + timestamp;
        String signature = encryptByHMACSHA1(sigStr.getBytes(), this.accessSecret.getBytes());

        Map<String, Object> postParams = new HashMap<String, Object>();
        postParams.put("access_key", this.accessKey);
        postParams.put("sample_bytes", fp.length + "");
        postParams.put("sample", fp);
        postParams.put("timestamp", timestamp);
        postParams.put("signature", signature);
        postParams.put("data_type", dataType);
        postParams.put("signature_version", sigVersion);

        String res = postHttp(reqURL, postParams, this.timeout);

        return res;
    }

    private String encodeBase64(byte[] bstr) {
        Base64 base64 = new Base64();
        return new String(base64.encode(bstr));
    }

    private String encryptByHMACSHA1(byte[] data, byte[] key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data);
            return encodeBase64(rawHmac);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    private String getUTCTimeSeconds() {  
        Calendar cal = Calendar.getInstance();   
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);   
        int dstOffset = cal.get(Calendar.DST_OFFSET);    
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));    
        return cal.getTimeInMillis()/1000 + "";
    }  

    private String postHttp(String posturl, Map<String, Object> params, int timeOut) {
        String res = "";
        String BOUNDARYSTR = "*****2015.10.01.acrcloud.rec.copyright." + System.currentTimeMillis() + "*****";
        String BOUNDARY = "--" + BOUNDARYSTR + "\r\n";
        String ENDBOUNDARY = "--" + BOUNDARYSTR + "--\r\n\r\n";
        String stringKeyHeader = BOUNDARY + "Content-Disposition:form-data;name=\"%s\"" + "\r\n\r\n%s\r\n";
        String filePartHeader = BOUNDARY + "Content-Disposition: form-data;name=\"%s\";filename=\"%s\"\r\n" + "Content-Type:application/octet-stream\r\n\r\n";		
        URL url = null;
        HttpURLConnection conn = null;
        BufferedOutputStream out = null;
        BufferedReader reader = null;
        ByteArrayOutputStream postBufferStream = new ByteArrayOutputStream();
        try {
            for (String key : params.keySet()) {
                Object value = params.get(key);
                if (value instanceof String || value instanceof Integer) {
                    postBufferStream.write(String.format(stringKeyHeader, key, (String)value).getBytes());
                } else if (value instanceof byte[]) {
                    postBufferStream.write(String.format(filePartHeader, key, key).getBytes());
                    postBufferStream.write((byte[]) value);
                    postBufferStream.write("\r\n".getBytes());
                }
            }
            postBufferStream.write(ENDBOUNDARY.getBytes());
            
            url = new URL(posturl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(timeOut);
            conn.setReadTimeout(timeOut);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Content-type", "multipart/form-data;boundary=" + BOUNDARYSTR);
            conn.connect();

            out = new BufferedOutputStream(conn.getOutputStream());

            out.write(postBufferStream.toByteArray());
            out.flush();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String tmpRes = "";
                while ((tmpRes = reader.readLine()) != null) {
                    if (tmpRes.length() > 0)
                        res = res + tmpRes;
                }
            } else {
                System.out.println("http error response code " + responseCode);
                res = ACRCloudStatusCode.HTTP_ERROR;
            }
        } catch (Exception e) {
            e.printStackTrace();
            res = ACRCloudStatusCode.HTTP_ERROR;
        } finally {
            try {
                if (postBufferStream != null) {
                    postBufferStream.close();
                    postBufferStream = null;
                }
            } catch (Exception e) {}

            try {
                if (out != null) {
                    out.close();
                    out = null;
                }
            } catch (Exception e) {}

            try {
                if (reader != null) {
                    reader.close();
                    reader = null;
                }
            } catch (Exception e) {}

            try {
                if (conn != null) {
                    conn.disconnect();
                    conn = null;
                }
            } catch (Exception e) {}
        }
        return res;
    }

}

class ACRCloudStatusCode
{
    public static String HTTP_ERROR = "{\"status\":{\"msg\":\"Http Error\", \"code\":3000}}";
    public static String NO_RESULT = "{\"status\":{\"msg\":\"No Result\", \"code\":1001}}";
    public static String DECODE_AUDIO_ERROR = "{\"status\":{\"msg\":\"Can not decode audio data\", \"code\":2005}}";
    public static String JSON_ERROR = "{\"status\":{\"msg\":\"json error\", \"code\":2002}}";
    public static String UNKNOW_ERROR = "{\"status\":{\"msg\":\"unknow error\", \"code\":2010}}";
}
