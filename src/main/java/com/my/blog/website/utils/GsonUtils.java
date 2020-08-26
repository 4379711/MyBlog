package com.my.blog.website.utils;

import com.google.gson.Gson;

/**
 * json转换工具
 * @author liuyalong
 */
public class GsonUtils {

    private static final Gson gson = new Gson();

    public static String toJsonString(Object object){
      return object==null?null:gson.toJson(object);
    }
}
