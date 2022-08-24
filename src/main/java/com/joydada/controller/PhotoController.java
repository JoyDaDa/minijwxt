package com.joydada.controller;


import com.alibaba.fastjson.JSONArray;
import com.joydada.common.GlobalResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;


@Controller
public class PhotoController {

    @ResponseBody
    @GetMapping("/wx/swiper")
    public GlobalResult getSwiper() {
        JSONArray ReturnJson = new JSONArray();
        Map<String,String> map1= new HashMap();
        Map<String,String> map2= new HashMap();
        String myurl = "https://www.joydada.top";
//        url.put("url1",myurl+"/images/1.png");
//        url.put("url2",myurl+"/images/2.png");
        map1.put(new String("url"),myurl+"/images/1.png");
        map2.put(new String("url"),myurl+"/images/2.png");

        ReturnJson.add(map1);
        ReturnJson.add(map2);
        return GlobalResult.build(200,"ok",ReturnJson);
    }
}
