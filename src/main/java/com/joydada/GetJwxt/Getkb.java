package com.joydada.GetJwxt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.joydada.common.GlobalResult;
import com.joydada.pojo.User;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Getkb {

    public GlobalResult getTimetable(User user,int xqm, int xnm) throws IOException {
//        Request URL: http://220.178.71.156:9100/jwglxt/kbcx/xskbcx_cxXsgrkb.html?gnmkdm=N2151&su=1904010251
//        Request Method: POST
        Map<String, String> cookie = new HashMap<>();
        cookie.put("JSESSIONID",user.getJwxtCookie().substring(0,user.getJwxtCookie().indexOf("=")));
        cookie.put("route",user.getJwxtCookie().substring(user.getJwxtCookie().indexOf("=")+1));

        //构造请求体
        Map<String,String> datas = new HashMap<>();
        datas.put("xnm",String.valueOf(xnm));
        datas.put("xqm",String.valueOf(xqm * xqm * 3));
        datas.put("kzlx","ck");
        Connection connection = Jsoup.connect("http://218.104.78.106:9100/jwglxt/kbcx/xskbcx_cxXsgrkb.html?gnmkdm=N2151&su="+user.getUsername());
//        System.out.println("http://220.178.71.156:9100/jwglxt/kbcx/xskbcx_cxXsgrkb.html?gnmkdm=N2151&su="+user.getUsername());
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
        connection.header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        connection.header("Accept-Encoding", "gzip, deflate");
        connection.header("Accept", "*/*");
        connection.header("Host","220.178.71.156:9100");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
        connection.header("Referer","http://218.104.78.106:9100/jwglxt/kbcx/xskbcx_cxXskbcxIndex.html?gnmkdm=N2151&layout=default&su="+user.getUsername());
        Connection.Response response = connection.cookies(cookie).method(Connection.Method.POST)
                .data(datas).ignoreContentType(true).ignoreHttpErrors(true).timeout(5000).execute();
        //教务系统服务器可能宕机。。。
        if (response.statusCode() != 200){
            System.out.println(response.statusCode());
            return GlobalResult.errorMsg("教务系统服务器出错");
        }

        //cookies过期
        Document parse = Jsoup.parse(response.body());
        if(parse.getElementById("home") != null){
            return GlobalResult.build(201,"err","cookies_outtime");
        }

        JSONObject jsonObject = JSON.parseObject(response.body());
        JSONArray kbList = jsonObject.getJSONArray("kbList");
//        System.out.println(kbList.toString());

//        JSONArray ReturnJson = new JSONArray();
//
//        for (Iterator iterator = kbList.iterator(); iterator.hasNext();) {
//            JSONObject lesson = (JSONObject) iterator.next();
//            JSONObject object = new JSONObject();
//            object.put("xqj",lesson.getString("xqj"));
//            object.put("skjc",lesson.getString("jcs").substring(0,1));
//            object.put("skcd","2");
//            object.put("kcmc",lesson.getString("kcmc"));
//            object.put("cdmc",lesson.getString("cdmc"));
//            ReturnJson.add(object);
//        }
//        System.out.println(ReturnJson);
        return GlobalResult.build(200,"success",kbList);
    }
}
