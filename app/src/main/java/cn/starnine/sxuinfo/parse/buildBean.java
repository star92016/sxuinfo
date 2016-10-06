package cn.starnine.sxuinfo.parse;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import cn.starnine.sxuinfo.User;
import cn.starnine.sxuinfo.bean.DetailInfo;
import cn.starnine.sxuinfo.bean.MainInfo;
import cn.starnine.sxuinfo.net.http.LoadPage;
import cn.starnine.sxuinfo.utils.Config;

/**
 * 完成构建Bean的任务，同时如果文件中有缓存且未超时
 * 将不会重新向网络请求直接从文件读取，如果用户关闭网络将尽可能直接返回网络数据
 */

public class buildBean {
    private Context context;
    public buildBean(Context context){
        this.context=context;
    }
    /**
     * 判断网络
     * */
    public boolean isNetAvailable(){
        ConnectivityManager connectivityManager=
                (ConnectivityManager) context.
                        getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager==null)
            return false;
        NetworkInfo[] networkInfo=connectivityManager.getAllNetworkInfo();
        for(NetworkInfo n:networkInfo){
            if(n.getState() == NetworkInfo.State.CONNECTED)
                return true;
        }
        return false;
    }
    public interface OnBuildBean{
        void onNoNetWork();
        void onNeedLogin();
        void onOk(Serializable s);
    }

    /**
     * 完成构建Bean的任务，同时如果文件中有缓存且未超时
     * 将不会重新向网络请求直接从文件读取，如果用户关闭网络将尽可能直接返回网络数据
     */
    private void buildInfo(final URL url,final OnBuildBean onBuildBean,Class<?> cls){
        Method method=null;
        try {
            method=cls.getMethod("toUnique",URL.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        File file1= null;
        try {
            file1 = new File(context.getFilesDir(),method.invoke(url).toString());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        final File file=file1;
        if(!isNetAvailable()){
            if(file.exists()){
                try {
                    ObjectInputStream oi=new ObjectInputStream(new FileInputStream(file));
                    Object obj= oi.readObject();
                    oi.close();
                    onBuildBean.onOk((Serializable) obj);

                } catch (Exception e) {
                    e.printStackTrace();
                    onBuildBean.onNoNetWork();
                }
            }
        }else {
            if(file.exists()&&new Date().getTime()-file.lastModified()< Config.getOutCacheTime()){
                try {
                    ObjectInputStream oi=new ObjectInputStream(new FileInputStream(file));
                    Object obj= oi.readObject();
                    oi.close();
                    onBuildBean.onOk((Serializable) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                    //这不太可能发生
                    onBuildBean.onNoNetWork();
                }
            }else {
                new LoadPage(context).addRequest(url, new LoadPage.OnLoadPage() {
                    @Override
                    public void onNetError() {
                        onBuildBean.onNoNetWork();
                    }

                    @Override
                    public void onFinish(String html) {

                        Object obj= null;
                        try {
                            ObjectOutputStream oo=new ObjectOutputStream(new FileOutputStream(file));
                            obj = Parse.buildMainInfo(html);
                            oo.writeObject(obj);
                            oo.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        onBuildBean.onOk((Serializable) obj);
                    }

                    @Override
                    public void onNeedLogin() {
                        onBuildBean.onNeedLogin();
                    }
                });
            }

        }
    }
    public void buildMainInfo(final OnBuildBean onBuildBean){
        try {
            buildInfo(new URL(User.INDEX),onBuildBean,MainInfo.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void buildMoreInfo(URL url,OnBuildBean onBuildBean){
        buildInfo(url,onBuildBean,MainInfo.class);
    }
    public void buildDetailInfo(URL url,OnBuildBean onBuildBean){
        buildInfo(url,onBuildBean, DetailInfo.class);
    }
}
