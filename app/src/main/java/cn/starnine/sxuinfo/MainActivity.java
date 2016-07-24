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

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                startActivity(new Intent(this,LoginActivity.class));
                break;
            case R.id.btn_main:
                startActivity(new Intent(this,HomeActivity.class));
                break;
        }
    }
}
