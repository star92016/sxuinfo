package cn.starnine.sxuinfo;


import android.app.ProgressDialog;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.Map;

import cn.starnine.sxuinfo.utils.MyStringRequest;

//TODO 完成更多页面的解析
public class MoreActivity extends BaseActivity implements MyStringRequest.MyResponse{
    String href;
    private RequestQueue queue;
    @Override
    public boolean beforeSetView() {
        href=getIntent().getStringExtra("href");
        if(href==null||href.equals("")) {
            toast("异常");
            finish();
            return false;
        }
        return super.beforeSetView();
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_more);
    }

    @Override
    public void initView() {
        String title=getIntent().getStringExtra("title");
        setTitle(title);
        dialog = new ProgressDialog(this);
        dialog.setMessage("加载中");
        dialog.setCancelable(false);
        dialog.show();
        queue= Volley.newRequestQueue(this);
        queue.add(new MyStringRequest(href,this));
    }

    @Override
    public void onClick(View view) {

    }
    private ProgressDialog dialog;
    @Override
    public void onGetCookie(String cookie) {

    }

    @Override
    public String onSetCookie() {
        return sp.getString("cookie", "");
    }

    @Override
    public Map<String, String> getParams() {
        return null;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        toast("网络错误");
        if(dialog!=null)dialog.cancel();
    }

    @Override
    public void onResponse(String s) {
        if(dialog!=null)dialog.cancel();
        ((TextView)findViewById(R.id.tv_context)).setText(s);
    }
}
