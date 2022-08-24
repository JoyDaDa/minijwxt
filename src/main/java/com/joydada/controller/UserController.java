package com.joydada.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.joydada.GetJwxt.Getcj;
import com.joydada.GetJwxt.Getkb;
import com.joydada.GetJwxt.Getkjs;
import com.joydada.common.GlobalResult;
import com.joydada.common.JwxtLogin;
import com.joydada.common.WechatUtil;
import com.joydada.mapper.UserMapper;
import com.joydada.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * @author lastwhisper
 * @desc
 * @email gaojun56@163.com
 */
@Controller
public class UserController<classroom> {

    @Autowired
    private UserMapper userMapper;

    /**
     * 微信用户登录详情
     */
    @PostMapping("wx/login")
    @ResponseBody
    public GlobalResult user_login(@RequestParam(value = "code", required = false) String code,
                                   @RequestParam(value = "username", required = false) String username,
                                   @RequestParam(value = "password", required = false) String password/*,
                                   @RequestParam(value = "rawData", required = false) String rawData,
                                   @RequestParam(value = "signature", required = false) String signature,
                                   @RequestParam(value = "encrypteData", required = false) String encrypteData,
                                   @RequestParam(value = "iv", required = false) String iv*/) throws Exception {
        // 用户非敏感信息：rawData
        // 签名：signature
        //JSONObject rawDataJson = JSON.parseObject(rawData);
        // 1.接收小程序发送的code
        // 2.开发者服务器 登录凭证校验接口 appi + appsecret + code
        JSONObject SessionKeyOpenId = WechatUtil.getSessionKeyOrOpenId(code);
        // 3.接收微信接口服务 获取返回的参数
        String openid = SessionKeyOpenId.getString("openid");
        String sessionKey = SessionKeyOpenId.getString("session_key");


        // 4.校验签名 小程序发送的签名signature与服务器端生成的签名signature2 = sha1(rawData + sessionKey)
//        String signature2 = DigestUtils.sha1Hex(rawData + sessionKey);
//        if (!signature.equals(signature2)) {
//            return GlobalResult.build(500, "签名校验失败", null);
//        }
        // 5.根据返回的User实体类，判断用户是否是新用户，是的话，将用户信息存到数据库；不是的话，更新最新登录时间
        User user = this.userMapper.selectById(openid);
        // uuid生成唯一key，用于维护微信小程序用户与服务端的会话
        String skey = UUID.randomUUID().toString();
        //获取jwxt_cookie
        GlobalResult login = JwxtLogin.login(username, password);
        if(login.getStatus()!=200){ //说明教务系统服务端bug
            return login;
        }
        Object data = login.getData();
        if (user == null && data != null) {
            // 用户信息入库
//            String nickName = rawDataJson.getString("nickName");
//            String avatarUrl = rawDataJson.getString("avatarUrl");
//            String gender = rawDataJson.getString("gender");
//            String city = rawDataJson.getString("city");
//            String country = rawDataJson.getString("country");
//            String province = rawDataJson.getString("province");

            user = new User();
            user.setOpenId(openid);
            user.setSkey(skey);
            user.setCreateTime(new Date());
            user.setLastVisitTime(new Date());
            user.setSessionKey(sessionKey);
            user.setUsername(username);
            user.setPassword(password);
            user.setJwxtCookie(data.toString());
//            user.setCity(city);
//            user.setProvince(province);
//            user.setCountry(country);
//            user.setAvatarUrl(avatarUrl);
//            user.setGender(Integer.parseInt(gender));
//            user.setNickName(nickName);

            this.userMapper.insert(user);
        } else {
            // 已存在，更新用户登录时间
            user.setLastVisitTime(new Date());
            // 重新设置会话skey
            user.setSkey(skey);
            //重新设置jwxt_cookies
            user.setJwxtCookie(data.toString());
            this.userMapper.updateById(user);
            //
        }
        //encrypteData比rowData多了appid和openid
        //JSONObject userInfo = WechatUtil.getUserInfo(encrypteData, sessionKey, iv);
        //6. 把新的skey返回给小程序
        GlobalResult result = GlobalResult.build(200, null, skey);
        return result;
    }

    @ResponseBody
    @GetMapping("/wx/getcj")
    public GlobalResult getcj(@RequestParam(value = "skey", required = false) String skey,
                              @RequestParam(value = "year", required = false) int year,
                              @RequestParam(value = "term", required = false) int term) throws Exception {
        //由skey获取用户，判断用户的cookie是否有效 并更新lastvisittime
        //cookie无效可能 1.是在别处登录 2.超出有效时间
        Map<String, Object> map = new HashMap<>();
        map.put("skey", skey);
        List<User> userList = userMapper.selectByMap(map);
        User user = userList.get(0);

        GlobalResult result = new Getcj().querySource(user,year,term);
        if (result.getStatus() == 201) {
            //Cookies_outtime
            GlobalResult login = JwxtLogin.login(user.getUsername(), user.getPassword());
            user.setLastVisitTime(new Date());
            user.setJwxtCookie((String) login.getData());
            this.userMapper.updateById(user);
            return new Getcj().querySource(user,year,term);

        }else {
            user.setLastVisitTime(new Date());
            this.userMapper.updateById(user);
            return result;
        }

    }

    @ResponseBody
    @GetMapping("/wx/getkb")
    public GlobalResult getkb(@RequestParam(value = "skey", required = false) String skey,
                              @RequestParam(value = "xnm", required = false) int xnm,
                              @RequestParam(value = "xqm", required = false) int xqm) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("skey", skey);
        List<User> userList = userMapper.selectByMap(map);
        if (userList.size() != 0) {
            User user = userList.get(0);
            user.setLastVisitTime(new Date());
            this.userMapper.updateById(user);

            GlobalResult timetable = new Getkb().getTimetable(user, xqm, xnm);

            if (timetable.getStatus() == 201) {
                //Cookies_outtime
                GlobalResult login = JwxtLogin.login(user.getUsername(), user.getPassword());
                user.setLastVisitTime(new Date());
                user.setJwxtCookie((String) login.getData());
                this.userMapper.updateById(user);
                return new Getkb().getTimetable(user, xqm, xnm);
            } else {
                return timetable;
            }
        }else {
            return GlobalResult.build(000,"0",0);
        }
    }

    @ResponseBody
    @GetMapping("/wx/getkjs")
    public GlobalResult getkjs(@RequestParam(value = "skey", required = false) String skey,
                               @RequestParam(value = "xnm", required = false) int xnm,
                               @RequestParam(value = "xqm", required = false) int xqm,
                               @RequestParam(value = "zcd", required = false) String zcd,
                               @RequestParam(value = "jcd", required = false) String jcd,
                               @RequestParam(value = "xqj", required = false) String xqj ) throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put("skey", skey);
        List<User> userList = userMapper.selectByMap(map);
        if (userList.size() != 0) {
            User user = userList.get(0);
            user.setLastVisitTime(new Date());
            this.userMapper.updateById(user);

            GlobalResult classroom = new Getkjs().getClassroom(user, xnm, xqm,zcd,jcd,xqj);

            if (classroom.getStatus() == 201) {
                //Cookies_outtime
                GlobalResult login = JwxtLogin.login(user.getUsername(), user.getPassword());
                user.setLastVisitTime(new Date());
                user.setJwxtCookie((String) login.getData());
                this.userMapper.updateById(user);
                return new Getkjs().getClassroom(user, xnm, xqm,zcd,jcd,xqj);
            } else {
                return classroom;
            }
        }else {
            return GlobalResult.build(0,"not user",0);
        }

    }


}
