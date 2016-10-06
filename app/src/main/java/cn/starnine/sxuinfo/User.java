package cn.starnine.sxuinfo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import cn.starnine.sxuinfo.net.http.HttpClient;
import cn.starnine.sxuinfo.utils.Config;

/**
 * 这个类用于用户管理，包括密码的保存登陆等
 */
public class User {
    public static final String INDEX = "http://myportal.sxu.edu.cn/";
    public static final String LOGIN = "http://myportal.sxu.edu.cn/userPasswordValidate.portal";
    public static final String LOGOUT = "http://myportal.sxu.edu.cn/destroySSOToken.portal?" +
            "goto=http%3A%2F%2Fmyportal.sxu.edu.cn%2Findex.portal";

    private static Context context;

    private User(){}
    public static void init(Context context){
        User.context=context;
    }

    /**
     * 保存用户名密码后的登陆或者已经登陆直接返回
     * 第一次登陆或密码更改有异常
     *@param onLogin 回调函数，用户名密码采用上次保存的
     * @return 返回网页
     */
    public static void Login(final OnLogin onLogin){
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        onLogin.onNetError(msg.obj.toString());
                        break;
                    case 2:
                        onLogin.onSucess();
                        break;
                    case 3:
                        onLogin.onWrongUserPass();
                        break;
                }
            }
        };
        new Thread(){
            @Override
            public void run() {
                HttpClient httpClient=new HttpClient();
                final  Message msg=new Message();
                HashMap<String, String> hashmap=new HashMap<String, String>();
                hashmap.put("Login.Token1",Config.getUserName());
                hashmap.put("Login.Token2",Config.getPassWd());
                hashmap.put("goto","http://myportal.sxu.edu.cn/loginSuccess.portal");
                hashmap.put("gotoOnFail","http://myportal.sxu.edu.cn/loginFailure.portal");
                try {
                    httpClient.init(new URL(User.LOGIN),"POST",hashmap);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what=1;
                    msg.obj=e.getMessage();
                    handler.sendMessage(msg);
                }
                httpClient.start(new HttpClient.OnHttpClient(){
                    @Override
                    public void onLoadFinish(String html) {
                        super.onLoadFinish(html);
                        if(html.indexOf("handleLoginFailure")>0)
                            handler.sendEmptyMessage(3);
                        else if(html.indexOf("handleLoginSuccessed")>0)

                            handler.sendEmptyMessage(2);
                        else{
                            msg.what=1;
                            msg.obj="htmlerror";
                            handler.sendMessage(msg);
                        }

                    }

                    @Override
                    public void onError(int code, String msg1) {
                        super.onError(code, msg1);
                        msg.what=1;
                        msg.obj="Code:"+code+msg1;
                        handler.sendMessage(msg);

                    }
                });
            }
        }.start();
    }

    public static void Logout() {
        new Thread(){
            public void run(){
                HttpClient httpClient=new HttpClient();
                try {
                    httpClient.init(new URL(LOGOUT),"GET",null);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                httpClient.start(null);
            }
        }.start();
    }

    public interface OnLogin{
        void onSucess();
        void onWrongUserPass();
        void onNetError(String msg);
    }

    /**
     * 使用用户名密码登陆，登陆成功会保存用户名密码
     *  @param user 用户名
     * @param pass 密码
     * @param onLogin 回调函数
     * @return 返回网页
     */
    public static void Login(String user, String pass,OnLogin onLogin) {
        Config.SetUserPass(user,pass);
        Login(onLogin);
    }

}
