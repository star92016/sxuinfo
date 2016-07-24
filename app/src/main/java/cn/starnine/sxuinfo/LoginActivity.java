package cn.starnine.sxuinfo;


import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_user;
    private EditText et_passwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_login);
        setTitle(R.string.login);
        initView();

    }

    private void initView() {

        findViewById(R.id.btn_login).setOnClickListener(this);
        et_passwd = (EditText) findViewById(R.id.et_passwd);
        et_user = (EditText) findViewById(R.id.et_username);
        et_user.setText("2013241031");
        et_passwd.setText("324103");
    }

    private void login() {

        String user = et_user.getText().toString().trim();
        String pass = et_passwd.getText().toString().trim();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                break;
        }
    }


}
