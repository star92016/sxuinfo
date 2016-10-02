package cn.starnine.sxuinfo;

import android.content.Context;

/**
 * 这个类用于用户管理，包括密码的保存登陆等
 */
public class User {
    /**
     * 获得实例
     * */
    public static User getInstance(Context context){
        if(null==instance){
            instance=new User(context);
        }
        return instance;
    }
    private static User instance;
    private Context context;
    private User(Context context){
        this.context=context;
    }
    /**
     * 保存用户名密码后的登陆或者已经登陆直接返回
     * 第一次登陆或密码更改有异常
     * @return 返回网页
     * */
    public String Login() throws Exception{
        throw new Exception("Wrong username or password");
    }
    /**
     * 使用用户名密码登陆，登陆成功会保存用户名密码
     * @return 返回网页
     * */
    public String Login(String userName,String passWd) throws Exception{
        return null;
    }

}
