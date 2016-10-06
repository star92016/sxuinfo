package cn.starnine.sxuinfo;


import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import cn.starnine.sxuinfo.net.http.LoadPage;
import cn.starnine.sxuinfo.parse.buildBean;

public class HomeActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }
TextView tv1;
    Toast toast;
    public void toast(String msg){
        if(toast==null)
            toast=Toast.makeText(this,"",Toast.LENGTH_LONG);
        toast.setText(msg);
        toast.show();
    }
    private void init() {
        tv1=(TextView)findViewById(R.id.tv_1);
        new buildBean(this).buildMainInfo(new buildBean.OnBuildBean() {
            @Override
            public void onNoNetWork() {
                toast("onnet");
            }

            @Override
            public void onNeedLogin() {
                toast("needlogin");
            }

            @Override
            public void onOk(Serializable s) {
                toast(s.toString());
            }
        });
    }

}
