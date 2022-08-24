package com.joydada.GetJwxt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;

public class Getxycx {

    public JSONArray getxy(String xh, Map<String,String> cookies) throws IOException {

        Connection connection = Jsoup.connect("http://218.104.78.106:9100/jwglxt/xsxy/xsxyqk_cxXsxyqkIndex.html?gnmkdm=N105515&layout=default&su="+xh);
        connection.header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        connection.header("Accept-Encoding","gzip, deflate");
        connection.header("Accept-Language","zh-CN,zh;q=0.9");
        connection.header("Cache-Control","max-age=0");
        connection.header("Host","218.104.78.106:9100");
        connection.header("Referer","http://218.104.78.106:9100/jwglxt/xtgl/index_initMenu.html");
        connection.header("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36 SLBrowser/7.0.0.4071 SLBChan/10");
        Connection.Response response = connection.cookies(cookies).ignoreContentType(true).execute();
        Document doc = response.parse();
        Elements rows = doc.select("div[style=margin-bottom:0px;margin-top: 20px]").get(0).select("font");
//        System.out.println(rows);

        JSONObject ReturnJson = new JSONObject();
        JSONArray jsonArray = new JSONArray();


        if (rows.size() == 1) {
            System.out.print("");
        }else {

            Element row0 = rows.get(0);
//            System.out.println(row0.select("font").get(0).text());    //刘文豪同学，您的课程修读情况：(统计时间2021-05-29 21:28:06之前有效)
            ReturnJson.put("xx1",row0.select("font").get(0).text());

            Element row1 = rows.get(1);
//            System.out.println(row1.select("font").get(0).text());    //当前所有课程平均学分绩点（GPA）：3.38
            ReturnJson.put("xx2",row1.select("font").get(0).text());


            Element row3 = rows.get(3);
//            System.out.println(row3.select("font").get(0).text());   //计划总课程 45门 通过28门，未通过 0门；未修3门；
            Element row5 = rows.get(5);
//            System.out.println(row5.select("font").get(0).text());   //在读14门！
            ReturnJson.put("xx3",row3.select("font").get(0).text()+row5.select("font").get(0).text());
//            ReturnJson.put("xx4",row5.select("font").get(0).text());

            Element row6 = rows.get(6);
//            System.out.println(row6.select("font").get(0).text());   //计划外： 通过4门，


            Element row7 = rows.get(7);
//            System.out.println(row7.select("font").get(0).text());   //未通过 0门
            ReturnJson.put("xx4",row6.select("font").get(0).text()+row7.select("font").get(0).text());
//            ReturnJson.put("xx6",row7.select("font").get(0).text());
            jsonArray.add(ReturnJson);

        }

        return jsonArray;

    }
}
