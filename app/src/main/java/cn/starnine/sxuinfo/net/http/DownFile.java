package cn.starnine.sxuinfo.net.http;

import android.content.Context;

import java.io.File;
import java.net.URL;

/**
 * Created by licheng on 2016/10/6 0006.
 */

public class DownFile {
    public interface OnLoadPage{
        void onError();
        void onProcess(int process);
        void onFinish();
    }

    private Context context;
    public DownFile(Context context){
        this.context=context;
    }
    private void addRequest(final URL url, final File file,final OnLoadPage onDownFile){
        /**
         TODO 这里有点问题，万一用户没有登陆下载文件下载到的是网页，传入的文件是目录同时没有获得文件名将返回错误
        */
        HttpClient httpClient=new HttpClient(context);
        httpClient.init(url,"GET",null);
        httpClient.setFile(file);
        httpClient.start(new HttpClient.OnHttpClient(){
            @Override
            public void onDownLoadFinish() {
                onDownFile.onFinish();
            }

            @Override
            public void onDownLoadProcess(int process) {
                onDownFile.onProcess(process);
            }

            @Override
            public void onError(int code, String msg) {
                onDownFile.onError();
            }
        });
    }
}
