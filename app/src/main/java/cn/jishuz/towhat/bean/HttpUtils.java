package cn.jishuz.towhat.bean;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import cn.jishuz.towhat.bean.ChatMessage.Type;

public class HttpUtils {
    private static final String URL = "http://www.tuling123.com/openapi/api";
    private static final String API_KEY = "cda46beb4ac340d8b494f52edc8a9e5a";

    /**
     * 发送一个消息，得到返回的消息
     * @param msg
     * @return
     */
    public static ChatMessage sendMessage(String msg)
    {
        ChatMessage chatMessage = new ChatMessage();
        String jsonRes = doGet(msg);
        Gson gson = new Gson();
        Result result = null;
        try
        {
            result = gson.fromJson(jsonRes, Result.class);
            chatMessage.setMsg(result.getText());
            if(result.getUrl() == null){
                chatMessage.setImageUrl("");
            }else{
                chatMessage.setImageUrl(result.getUrl());
            }
//            Log.i("chatMessage",chatMessage.getImageUrl());
        } catch (Exception e)
        {
            chatMessage.setMsg("服务器繁忙，请稍候再试");
        }
        chatMessage.setDate(new Date());
        chatMessage.setType(Type.INCOMING);
        return chatMessage;
    }

    public static String doGet(String msg)
    {
        String result = "";
        String url = setParams(msg);
        ByteArrayOutputStream baos = null;
        InputStream is = null;
        try
        {
            java.net.URL urlNet = new java.net.URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlNet
                    .openConnection();
            conn.setReadTimeout(5 * 1000);
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            is = conn.getInputStream();
            int len = -1;
            byte[] buf = new byte[128];
            baos = new ByteArrayOutputStream();

            while ((len = is.read(buf)) != -1)
            {
                baos.write(buf, 0, len);
            }
            baos.flush();
            result = new String(baos.toByteArray());
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (baos != null)
                    baos.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                if (is != null)
                {
                    is.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static String setParams(String msg)
    {
        String url = "";
        try
        {
            url = URL + "?key=" + API_KEY + "&info="
                    + URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return url;
    }

}
