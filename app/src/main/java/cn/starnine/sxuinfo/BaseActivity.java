package cn.starnine.sxuinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


/**
 * Created by licheng on 16-7-29.
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{
    protected SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        if(!beforeSetView())return;
        setContentView();
        initView();
    }
    public boolean beforeSetView(){
        return true;
    }
    public abstract void setContentView();
    public abstract void initView();
    private Toast toast;

    public void toast(String str) {

        if (toast == null) {
            toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        } else
            toast.setText(str);
        toast.show();
    }
}
