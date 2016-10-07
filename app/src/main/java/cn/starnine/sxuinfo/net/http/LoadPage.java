package cn.starnine.sxuinfo.net.http;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.net.URL;

import cn.starnine.sxuinfo.User;

/**
 * 用于加载页面，所有方法必须在UI线程调用
 */

public class LoadPage {
    public interface OnLoadPage{
        void onNetError();
        void onFinish(String html);
        void onNeedLogin();
    }
    private Context context;
    private int count;
    public LoadPage(Context context){
        this.context=context;
    }
    private void request(final String url, final OnLoadPage onLoadPage){
        try {
            URL url1=new URL(url);
            request(url1,onLoadPage);
        }catch (Exception e){
            e.printStackTrace();
            onLoadPage.onNetError();
        }

    }
    public void addRequest(final String url, final OnLoadPage onLoadPage){
        new LoadPage(context).request(url,onLoadPage);
    }
    public void addRequest(final URL url, final OnLoadPage onLoadPage){
        new LoadPage(context).request(url,onLoadPage);
    }
    private void request(final URL url, final OnLoadPage onLoadPage){
        count++;
        if(count>2)
            onLoadPage.onNetError();
        HttpClient httpClient=new HttpClient(context);
        httpClient.init(url,"GET",null);
        httpClient.start(new HttpClient.OnHttpClient(){
            @Override
            public void onLoadFinish(String html) {
                //首页特征判断
                if(html.indexOf("用户名")>0&&html.indexOf("设为首页")>0){
                    User.Login(new User.OnLogin() {
                        @Override
                        public void onSucess() {
                            request(url,onLoadPage);
                        }

                        @Override
                        public void onWrongUserPass() {
                            onLoadPage.onNeedLogin();
                        }

                        @Override
                        public void onNetError(String msg) {
                            onLoadPage.onNetError();
                        }
                    });
                }else{
                    int s,e;
                    s=html.indexOf("<");
                    e=html.lastIndexOf(">")+1;
                    onLoadPage.onFinish(html.substring(s,e));
                }
            }

            @Override
            public void onError(int code, String msg) {
                onLoadPage.onNetError();
            }
        });
    }
}
