package cn.starnine.sxuinfo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.Serializable;
import cn.starnine.sxuinfo.bean.MainInfo;
import cn.starnine.sxuinfo.parse.buildBean;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }
;
    Toast toast;
    public void toast(String msg){
        if(toast==null)
            toast=Toast.makeText(this,"",Toast.LENGTH_LONG);
        toast.setText(msg);
        toast.show();
    }
    private ListView listView;
    private void init() {
        listView=(ListView)findViewById(R.id.lv_1);

        new buildBean(this).buildMainInfo(new buildBean.OnBuildBean() {
            @Override
            public void onNoNetWork() {
                toast("没有网络");
            }

            @Override
            public void onNeedLogin() {
                toast("请先登录");
            }

            @Override
            public void onOk(Serializable s) {
                final MainInfo mainInfo=(MainInfo)s;
                String strs[]=mainInfo.toBlockStr();
                findViewById(R.id.tv_load).setVisibility(View.GONE);
                listView.setAdapter(new ArrayAdapter<String>(HomeActivity.this,android.R.layout.simple_list_item_1,strs));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //toast(mainInfo.get(position).toString());
                        Intent intent=new Intent(HomeActivity.this,MainItemActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("block",mainInfo.get(position));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onError(String msg) {
                toast("错误："+msg);
            }
        });
    }

}
