package cn.starnine.sxuinfo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;
import java.util.Map;

/**
 * 用于保存和获得持久化信息
 */
public class Config {
    /**
     * 初始化，仅被调用一次
     */
    public static void init(Context context){
        cookie=context.getSharedPreferences("cookie",Context.MODE_PRIVATE);
        user=context.getSharedPreferences("user",Context.MODE_PRIVATE);
    }
    private static SharedPreferences cookie;
    private static SharedPreferences user;

    public static String getUserName(){
        return user.getString("user","");
    }
    public static String getPassWd(){
        return user.getString("pass","");
    }

    public static long getOutCacheTime(){
        return user.getLong("outcache",1000*60*20);
    }
    public static void setOutCacheTime(long time){
        SharedPreferences.Editor editor=Config.user.edit();
        editor.putLong("outcache",time);
        editor.apply();
    }
    public static void SetUserPass(String user,String pass){
        SharedPreferences.Editor editor=Config.user.edit();
        editor.putString("user",user);
        editor.putString("pass",pass);
        editor.apply();
    }

    public static String getCookie(){
        Map<String,?> map=cookie.getAll();
        StringBuilder builder=new StringBuilder();
        for(String key:map.keySet()){
            builder.append(key+"=");
            builder.append(map.get(key).toString()+"; ");
        }
        if(builder.length()>2){
            builder.setLength(builder.length()-2);
        }
        return builder.toString();
    }
    public static void putCookie(String str){
        SharedPreferences.Editor editor=cookie.edit();
        String []array=str.split("; ");
        for(String s:array){
            String []a2=s.split("=");
            if(a2[0].equals("iPlanetDirectoryPro")||a2[0].equals("JSESSIONID")){
                editor.putString(a2[0],a2[1]);
            }
        }
        editor.apply();
    }
}
