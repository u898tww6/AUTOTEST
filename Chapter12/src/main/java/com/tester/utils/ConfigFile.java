package com.tester.utils;

import com.tester.model.InterfaceName;

import java.util.Locale;
import java.util.ResourceBundle;

public class ConfigFile {

    private static ResourceBundle bundle= ResourceBundle.getBundle("application", Locale.CHINA);;
    //工具类直接用静态方法，下面这个工具类用来实现URL链接的拼接
    public static String getUrl(InterfaceName name){
        String address = bundle.getString("test.url");
        String uri = "";//这个uri用来判断依次赋值
        String testUrl;//最终的测试地址
        if(name == InterfaceName.GETUSERLIST){
            uri =bundle.getString("getUserList.uri");
        }

        if(name == InterfaceName.LOGIN){
            uri = bundle.getString("login.uri");
        }

        if(name == InterfaceName.UPDATEUSERINFO){
            uri = bundle.getString("updateUserInfo.uri");
        }

        if(name == InterfaceName.GETUSERINFO){
            uri = bundle.getString("getUserInfo.uri");
        }

        if(name == InterfaceName.ADDUSERINFO){
            uri = bundle.getString("addUser.uri");
        }
        testUrl = address + uri;
        return testUrl;
    }
}
