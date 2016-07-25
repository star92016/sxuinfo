package cn.starnine.sxuinfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<String>{
    private String title,href;
    private SharedPreferences sp;
private TextView tv_head;
    private TextView tv_context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        title=getIntent().getStringExtra("title");
        href=getIntent().getStringExtra("href");
        if(title==null||title.equals("")||href==null||href.equals("")){
            finish();
            return;
        }
        sp =getSharedPreferences("config", Context.MODE_PRIVATE);
        if(sp.getString("cookie","").equals("")){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
            return;
        }
        initView();
    }

    private void initView() {
        setTitle(title);

        tv_context=(TextView)findViewById(R.id.tv_context);
        tv_head=(TextView)findViewById(R.id.tv_head);
        dialog=new ProgressDialog(this);
        dialog.setMessage("加载中");
        dialog.show();
        RequestQueue queue= Volley.newRequestQueue(this);

        String url=href;

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, this,this){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> localHashMap =new HashMap<>();
                localHashMap.put("User-Agent", "Mozilla/4.0 (compatibl; MSIE 5.5; Windows NT)");
                localHashMap.put("Cookie",sp.getString("cookie",""));
                return localHashMap;
            }
        };
        queue.add(stringRequest);
    }

    private Toast toast;
    public void toast(String str){

        if(toast==null) {
            toast = Toast.makeText(this,str,Toast.LENGTH_SHORT);
        }
        else
            toast.setText(str);
        toast.show();
    }
    private ProgressDialog dialog;
    @Override
    public void onErrorResponse(VolleyError volleyError) {

        if(dialog!=null)dialog.cancel();
        toast("网络错误或身份过期");
        finish();
        startActivity(new Intent(this,LoginActivity.class));
    }
    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    article=(Analysis.Article)msg.obj;
                    tv_head.setText(article.header);
                    tv_context.setText(article.body);
                    break;
            }
        }
    };
   private  Analysis.Article article;
    @Override
    public void onResponse(final String s) {
        new Thread(){
            public void run(){
                Analysis.Article article= new Analysis(s).parser();
                Message msg=new Message();
                msg.what=1;
                msg.obj=article;
                handler.sendMessage(msg);
            }
        }.start();
    }

}
