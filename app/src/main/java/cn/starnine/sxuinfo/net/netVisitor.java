package cn.starnine.sxuinfo.net;

import android.os.Handler;
import java.io.File;
import cn.starnine.sxuinfo.User;

/**
 * 访问网页
 */

public class netVisitor {
    /**
     * 获得实例
     */
    public static netVisitor getInstance(User user) throws Exception{
        if(user==null)throw new Exception("No Permission");
        return null;
    }
    private netVisitor(User user){}
    /**
     * 访问指定网址的网页
     * @param url 网址
     * @return 返回网页
     * */
    public String visit(String url) throws Exception{
        throw new Exception("Not Login");
    }
    /**
     * 下载文件，可以在新线程调用，通过handler获得下载进度
     * handler.sendEmptyMessage(0-100)
     * */
    public void downFile(File file, String url, Handler handler){
    }
}
