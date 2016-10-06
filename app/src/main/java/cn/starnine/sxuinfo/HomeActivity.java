package cn.starnine.sxuinfo;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

import cn.starnine.sxuinfo.net.http.HttpClient;
import cn.starnine.sxuinfo.net.http.LoadPage;

public class HomeActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }
TextView tv1;

    private void init() {
        tv1=(TextView)findViewById(R.id.tv_1);
        LoadPage l=new LoadPage(this);
        l.request(User.INDEX, new LoadPage.OnLoadPage() {
            @Override
            public void onNetError() {

            }

            @Override
            public void onFinish(String html) {
                tv1.setText(html);
            }

            @Override
            public void onNeedLogin() {

            }
        });
    }

}
