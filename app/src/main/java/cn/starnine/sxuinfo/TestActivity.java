package cn.starnine.sxuinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cn.starnine.sxuinfo.parse.buildBean;
import cn.starnine.sxuinfo.utils.Config;

/**
 * 测试期间程序的入口在这里
 * 这个Activity用于测试各个功能，正式版本会删除
 * */
public class TestActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Config.init(getApplicationContext());
        User.init(getApplicationContext());
        init();
    }
    private EditText user,pass;
    private void init(){
        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        user= (EditText) findViewById(R.id.editText2);
        pass=(EditText)findViewById(R.id.editText);
        if(new buildBean(this).isNetAvailable())
            toast("网络正常");
        else
            toast("没有网络");
    }
    Toast toast;
public void toast(String msg){
    if(toast==null)
        toast=Toast.makeText(this,"",Toast.LENGTH_LONG);
    toast.setText(msg);
    toast.show();
}
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                User.Login(user.getText().toString(), pass.getText().toString(), new User.OnLogin() {
                    @Override
                    public void onSucess() {
                        toast("Ok");
                    }

                    @Override
                    public void onWrongUserPass() {
                        toast("Failure");
                    }

                    @Override
                    public void onNetError(String msg) {
toast("NetError"+msg);
                    }
                });
            case R.id.button2:
                User.Logout();
                break;
            case R.id.button3:
                startActivity(new Intent(this,HomeActivity.class));
                break;
        }
    }
}
