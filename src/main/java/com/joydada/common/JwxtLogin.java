package com.joydada.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.joydada.Helper.RSAEncoder;
import com.joydada.Helper.B64;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwxtLogin {

    public static GlobalResult login(String xh,String pwd) throws Exception {
        //访问教务系统，开始得到cookie
        //分析请求参数，需要csrftoken，yhm, mm
        //请求登陆页,获得csrftoken
        Connection connection = Jsoup.connect("http://218.104.78.106:9100/jwglxt/xtgl/login_slogin.html?language=zh_CN&_t="+new Date().getTime());
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
        Connection.Response response = connection.followRedirects(true).execute();
        //教务系统服务器可能宕机。。。
        if (response.statusCode() != 200){
            return GlobalResult.errorMsg("教务系统服务器出错");
        }
        Map<String, String> cookies = response.cookies();
        Document document = Jsoup.parse(response.body());
        String csrftoken = document.getElementById("csrftoken").val();
//        System.out.println(csrftoken);

        //yhm=xh
        String yhm = xh;

        //获取公钥并加密密码
        connection = Jsoup.connect("http://218.104.78.106:9100/jwglxt/xtgl/login_getPublicKey.html?" +
                "time="+ new Date().getTime());
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
        response = connection.cookies(cookies).ignoreContentType(true).followRedirects(true).execute();

        JSONObject jsonObject = JSON.parseObject(response.body());
        String modulus = jsonObject.getString("modulus");
        String exponent = jsonObject.getString("exponent");
        String password = RSAEncoder.RSAEncrypt(pwd, B64.b64tohex(modulus), B64.b64tohex(exponent));
        String mm = B64.hex2b64(password);
//        System.out.println(mm);

//        System.out.println("登录前提交服务器的"+cookies);
        //提交服务器，登陆完成
        connection = Jsoup.connect("http://218.104.78.106:9100/jwglxt/xtgl/login_slogin.html");
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
        connection.data("csrftoken",csrftoken);
        connection.data("yhm",yhm);
        connection.data("mm",mm);
        connection.data("mm",mm);
        Connection.Response response1 = connection.cookies(cookies).ignoreContentType(true)
                .method(Connection.Method.POST).execute();

        document = Jsoup.parse(response1.body());

        //二次构造cookies
        String seccesscookies = response1.cookies().get("JSESSIONID")+"="+cookies.get("route");
//        Map<String, String> seccesscookies = new HashMap<>();
//        seccesscookies.put("JSESSIONID",response1.cookies().get("JSESSIONID"));
//        seccesscookies.put("route",cookies.get("route"));

        if(document.getElementById("tips") == null){
            //登录成功
            return GlobalResult.build(200, null, seccesscookies);
        }else{
            return GlobalResult.build(201, "失败", "账号或密码错误");
        }
    }

}



