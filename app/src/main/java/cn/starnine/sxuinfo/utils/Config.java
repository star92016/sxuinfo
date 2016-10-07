package cn.starnine.sxuinfo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于保存和获得持久化信息
 */
public class Config {
    /**
     * 初始化，仅被调用一次
     */
    public static void init(Context context){
        Config.context=context;
        cookie=context.getSharedPreferences("cookie",Context.MODE_PRIVATE);
        user=context.getSharedPreferences("user",Context.MODE_PRIVATE);
    }
    private static SharedPreferences cookie;
    private static SharedPreferences user;
    private static Context context;
    /**获得用户名
     * */
    public static String getUserName(){
        return user.getString("user","");
    }
    /**获得密码
     * */
    public static String getPassWd(){
        return user.getString("pass","");
    }
    /**获得文件允许的缓存时间
     * */
    public static long getOutCacheTime(){
        return user.getLong("outcache",1000*60*20);
    }
    /**设置文件允许的缓存时间
     * */
    public static void setOutCacheTime(long time){
        SharedPreferences.Editor editor=Config.user.edit();
        editor.putLong("outcache",time);
        editor.apply();
    }
    /**保存用户名密码
     * */
    public static void SetUserPass(String user,String pass){
        SharedPreferences.Editor editor=Config.user.edit();
        editor.putString("user",user);
        editor.putString("pass",pass);
        editor.apply();
    }
    /**获得详细页面替换规则
     * */
    public static Map<String,String> getDetailRe(){
        // TODO: 最好加保证数据的一致性，比如在set方法时更新相应的Bean对象
        return new HashMap<>();
    }
    /**获得Cookie，供底层调用
     * */
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
    /**设置Cookie，供底层调用
     * */
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
