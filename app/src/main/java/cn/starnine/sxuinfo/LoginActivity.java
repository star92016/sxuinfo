package cn.starnine.sxuinfo;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import java.util.logging.LogRecord;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener,Response.ErrorListener,Response.Listener<String>{

    private EditText et_user;
    private EditText et_passwd;
  private  SharedPreferences  sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sp=getSharedPreferences("config", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login);
        initView();

    }

    private void initView() {

        findViewById(R.id.btn_login).setOnClickListener(this);
        et_passwd = (EditText) findViewById(R.id.et_passwd);
        et_user = (EditText) findViewById(R.id.et_username);

        et_user.setText(sp.getString("user",""));
        et_passwd.setText(sp.getString("pass",""));
    }

    private void login() {
        if(dialog==null)
        dialog=new ProgressDialog(this);
        dialog.setTitle("加载中");
        dialog.setMessage("正在登录");

        dialog.show();
        handler.sendEmptyMessageDelayed(2,20000);
        RequestQueue queue=Volley.newRequestQueue(this);
        String url="http://myportal.sxu.edu.cn/userPasswordValidate.portal";
        final String user = et_user.getText().toString().trim();
        final String pass = et_passwd.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, this,this){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {

                    Map<String, String> responseHeaders = response.headers;
                    String rawCookies = responseHeaders.get("Set-Cookie");
                    Message msg=new Message();
                    msg.what=1;
                    msg.obj=rawCookies==null?"":rawCookies;
                    handler.sendMessageDelayed(msg,5000);
                    String dataString = new String(response.data, "UTF-8");
                    return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> hashmap=new HashMap<>();
                 hashmap.put("Login.Token1",user);

                  hashmap.put("Login.Token2",pass);
                 hashmap.put("goto","http://myportal.sxu.edu.cn/loginSuccess.portal");
                  hashmap.put("gotoOnFail","http://myportal.sxu.edu.cn/loginFailure.portal");
                return hashmap;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> localHashMap =new HashMap<>();
                localHashMap.put("User-Agent", "Mozilla/4.0 (compatibl; MSIE 5.5; Windows NT)");
                return localHashMap;
            }
        };

        queue.add(stringRequest);

    }

    Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if(msg.obj.toString().equals("")){
                        if(dialog!=null)dialog.cancel();
                        toast("用户名或密码错误");
                    }else{
                        if(dialog!=null)dialog.cancel();
                        toast("登录成功");
                        SharedPreferences.Editor edit=sp.edit();
                        edit.putString("user",et_user.getText().toString().trim());
                        edit.putString("pass",et_passwd.getText().toString().trim());
                        edit.putString("cookie",msg.obj.toString());
                        edit.commit();
                        finish();
                    }
                    break;
                case 2:
                    if(dialog.isShowing()){
                        toast(
                                "请求超时"
                        );
                    }
                    break;
            }
        }
    };
    private ProgressDialog dialog;
    private Toast toast;
    public void toast(String str){

        if(toast==null) {
            toast = Toast.makeText(this,str,Toast.LENGTH_SHORT);
        }
        else
            toast.setText(str);
        toast.show();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:

                login();
                break;
        }
    }


    @Override
    public void onErrorResponse(VolleyError volleyError) {
        if(dialog!=null)dialog.cancel();
        toast("网络错误");
    }

    @Override
    public void onResponse(String s) {

    }
}
