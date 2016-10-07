package cn.starnine.sxuinfo.parse;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

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
import cn.starnine.sxuinfo.bean.MoreInfo;
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
        void onError(String msg);
    }

    /**
     * 完成构建Bean的任务，同时如果文件中有缓存且未超时
     * 将不会重新向网络请求直接从文件读取，如果用户关闭网络将尽可能直接返回网络数据
     */
    private void buildInfo(final URL url, final OnBuildBean onBuildBean, final Class<?> cls){
        Method method=null;
        try {
            method=cls.getMethod("toUnique",URL.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            onBuildBean.onError(e.getMessage());
            return;
        }
        File file1= null;
        try {
            file1 = new File(context.getFilesDir(),method.invoke(null,url).toString());
        } catch (Exception e) {
            e.printStackTrace();
            onBuildBean.onError(e.getMessage());
            return;
        }
        final File file=file1;
        if(!isNetAvailable()){
            if(file.exists()){
                readFromCache(file, new OnHandCache() {
                    @Override
                    public void onOk(Serializable s) {
                        onBuildBean.onOk(s);
                    }

                    @Override
                    public void onError() {
                        onBuildBean.onError("File Wrong");
                    }
                });
            }
        }else {
            if(file.exists()&&new Date().getTime()-file.lastModified()< Config.getOutCacheTime()){
                readFromCache(file, new OnHandCache() {
                    @Override
                    public void onOk(Serializable s) {
                        onBuildBean.onOk(s);
                    }

                    @Override
                    public void onError() {
                        onBuildBean.onError("File Wrong");
                    }
                });
            }else {
                new LoadPage(context).addRequest(url, new LoadPage.OnLoadPage() {
                    @Override
                    public void onNetError() {
                        onBuildBean.onNoNetWork();
                    }

                    @Override
                    public void onFinish(String html) {
                        writeToCache(url, cls, html, file, new OnHandCache() {
                            @Override
                            public void onOk(Serializable s) {
                                onBuildBean.onOk(s);
                            }

                            @Override
                            public void onError() {
                                onBuildBean.onError("File Write Wrong");
                            }
                        });
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
        buildInfo(url,onBuildBean, MoreInfo.class);
    }
    public void buildDetailInfo(URL url,OnBuildBean onBuildBean){
        buildInfo(url,onBuildBean, DetailInfo.class);
    }
    public void clearCache(){
        File file=context.getFilesDir();
        long outtime=Config.getOutCacheTime();
        long now=new Date().getTime();
        for(File f:file.listFiles()){
            if (f.isFile()&&now-f.lastModified()>outtime){
                f.delete();
            }
        }
    }
    /**
     * 强制清除全部缓存
     * */
    public void clearAllCache(){
        File file=context.getFilesDir();
        for (File f:file.listFiles()){
            if(f.isFile())
                f.delete();
        }
    }
    public long cacheSize(){
        long size=0;
        File file=context.getFilesDir();
        for (File f:file.listFiles()){
            size+=f.length();
        }
        return size;
    }
    private interface OnHandCache{
        void onOk(Serializable s);
        void onError();
    }
    private void readFromCache(final File file, final OnHandCache onHandCache){
        final Handler h=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==1)
                    onHandCache.onOk((Serializable) msg.obj);
                else if(msg.what==2){
                    onHandCache.onError();
                }
            }
        };
        new Thread(){
            public void run(){
                try {
                    ObjectInputStream oi=new ObjectInputStream(new FileInputStream(file));
                    Object o=oi.readObject();
                    oi.close();
                    Message m=new Message();
                    m.what=1;
                    m.obj=o;
                    h.sendMessage(m);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message m=new Message();
                    m.what=2;
                    h.sendMessage(m);
                }
            }
        }.start();
    }
    private void writeToCache(final URL url, final Class cls, final String str, final File file, final OnHandCache onHandCache){
        final Handler h=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==1)
                    onHandCache.onOk((Serializable) msg.obj);
                else if (msg.what==2)
                    onHandCache.onError();
            }
        };
        new Thread(){
            public void run(){
                try {
                    Object obj=Parse.buildBean(url,str,cls);
                    ObjectOutputStream oo=new ObjectOutputStream(new FileOutputStream(file));
                    oo.writeObject(obj);
                    oo.close();
                    Message m=new Message();
                    m.what=1;
                    m.obj=obj;
                    h.sendMessage(m);
                } catch (IOException e) {
                    e.printStackTrace();
                    h.sendEmptyMessage(2);
                }
            }
        }.start();
    }
}
