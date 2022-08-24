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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Getcj {
//    请求 URL: http://220.178.71.156:9100/jwglxt/cjcx/cjcx_cxXsgrcj.html?doType=query&gnmkdm=N305005&su=1904010249
//    请求方法: POST
    public GlobalResult querySource(User user, int year, int term) throws IOException {
        Map<String, String> cookie = new HashMap<>();
        cookie.put("JSESSIONID",user.getJwxtCookie().substring(0,user.getJwxtCookie().indexOf("=")));
        cookie.put("route",user.getJwxtCookie().substring(user.getJwxtCookie().indexOf("=")+1));

        //构造请求体
        Map<String,String> datas = new HashMap<>();
        datas.put("xnm",String.valueOf(year));
        datas.put("xqm",String.valueOf(term * term * 3));
        datas.put("_search","false");
        datas.put("nd",String.valueOf(new Date().getTime()));
        datas.put("queryModel.showCount","20");
        datas.put("queryModel.currentPage","1");
        datas.put("queryModel.sortName","");
        datas.put("queryModel.sortOrder","asc");
        datas.put("time","1");
//        System.out.println("http://220.178.71.156:9100/jwglxt/cjcx/cjcx_cxDgXscj.html?doType=query&gnmkdm=N305005&su="+user.getUsername());
        Connection connection = Jsoup.connect("http://218.104.78.106:9100/jwglxt/cjcx/cjcx_cxDgXscj.html?doType=query&gnmkdm=N305005&su="+user.getUsername());
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
        Connection.Response response = connection.cookies(cookie).method(Connection.Method.POST)
                .data(datas).ignoreContentType(true).execute();

        //教务系统服务器可能宕机
        if (response.statusCode() != 200){
                return GlobalResult.errorMsg("教务系统服务器出错");
        }

        Document parse = Jsoup.parse(response.body());
        //cookies过期
        if(parse.getElementById("home") != null){
            return GlobalResult.build(201,"err","cookies_outtime");
        }

        JSONObject jsonObject = JSON.parseObject(response.body());
        JSONArray items = jsonObject.getJSONArray("items");
        return GlobalResult.build(200,"success",items);
    }

}
