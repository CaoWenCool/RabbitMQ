package com.demo.rabbitm1javaapi.util;

import java.util.ResourceBundle;

/**
 * @author: admin
 * @create: 2019/3/27
 * @update: 10:50
 * @version: V1.0
 * @detail:
 **/
public class ResourceUtil {

    private  static final ResourceBundle resourceBundle;

    static {
        resourceBundle = ResourceBundle.getBundle("config");
    }

    public static String getKey(String key){
        return resourceBundle.getString(key);
    }
}
