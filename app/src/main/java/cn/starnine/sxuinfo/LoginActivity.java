package cn.starnine.sxuinfo;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;
import cn.starnine.sxuinfo.data.MyConfig;
import cn.starnine.sxuinfo.utils.DESTest;
import cn.starnine.sxuinfo.utils.MyStringRequest;


public class LoginActivity extends BaseActivity implements MyStringRequest.MyResponse{

    private EditText et_user;
    private EditText et_passwd;
    private RequestQueue queue;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_login);
    }

    public void initView() {
        setTitle(R.string.login);
        findViewById(R.id.btn_login).setOnClickListener(this);
        et_passwd = (EditText) findViewById(R.id.et_passwd);
        et_user = (EditText) findViewById(R.id.et_username);
        et_user.setText(sp.getString("user", ""));
        et_passwd.setText(DESTest.decrypt(sp.getString("pass", "")));
        queue=Volley.newRequestQueue(this);
    }

    private void login() {
        if (dialog == null)
            dialog = new ProgressDialog(this);
        dialog.setTitle("加载中");
        dialog.setMessage("正在登录");
        dialog.setCancelable(false);
        dialog.show();
        handler.sendEmptyMessageDelayed(2, 20000);
        queue.add(new MyStringRequest(MyConfig.LoginUrl,this));
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    break;
                case 2:
                    if (dialog.isShowing()) {
                        toast("请求超时");
                        dialog.cancel();
                    }
                    break;
            }
        }
    };
    private ProgressDialog dialog;

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
        if (dialog != null) dialog.cancel();
        toast("网络错误");
    }

    @Override
    public void onResponse(String s) {

    }

    @Override
    public void onGetCookie(String cookie) {
        Looper.prepare();
        if (cookie.equals("")) {
            if (dialog != null) dialog.cancel();
            toast("用户名或密码错误");
        } else {
            if (dialog != null) dialog.cancel();
            toast("登录成功");
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("user", et_user.getText().toString().trim());
            edit.putString("pass", DESTest.encrypt(et_passwd.getText().toString().trim()));
            edit.putString("cookie", cookie);
            edit.apply();
            finish();
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
        }
        Looper.loop();
    }

    @Override
    public String onSetCookie() {
        return null;
    }

    @Override
    public Map<String, String> getParams() {
         String user = et_user.getText().toString().trim();
       String pass = et_passwd.getText().toString().trim();
        Map<String, String> hashmap = new HashMap<>();
        hashmap.put("Login.Token1", user);
        hashmap.put("Login.Token2", pass);
        hashmap.put("goto", "http://myportal.sxu.edu.cn/loginSuccess.portal");
        hashmap.put("gotoOnFail", "http://myportal.sxu.edu.cn/loginFailure.portal");
        return hashmap;
    }

}
