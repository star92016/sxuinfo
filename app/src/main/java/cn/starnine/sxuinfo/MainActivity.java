package cn.starnine.sxuinfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView(){
      findViewById(R.id.btn_login).setOnClickListener(this);
       findViewById(R.id.btn_main).setOnClickListener(this);
findViewById(R.id.btn_detail).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.btn_login:
                startActivity(new Intent(this,LoginActivity.class));
                break;
            case R.id.btn_main:
                startActivity(new Intent(this,HomeActivity.class));
                break;
            case R.id.btn_detail:
                intent=new Intent(this,DetailActivity.class);
                intent.putExtra("title","标题");
                intent.putExtra("href","http://myportal.sxu.edu.cn/detach.portal?.pmn=view&action=bulletinBrowser&.ia=false&.pen=pe1736&bulletinId=4dd267b7-4a2c-11e6-87c8-897fa1874628");
                startActivity(intent);
                break;
        }
    }
}
