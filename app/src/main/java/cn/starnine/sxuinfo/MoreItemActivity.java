package cn.starnine.sxuinfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import cn.starnine.sxuinfo.bean.MoreInfo;
import cn.starnine.sxuinfo.parse.buildBean;

public class MoreItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_item);
        init();
    }
    private String title;
    private URL url;
    Toast toast;
    public void toast(String msg){
        if(toast==null)
            toast=Toast.makeText(this,"",Toast.LENGTH_LONG);
        toast.setText(msg);
        toast.show();
    }
    private MoreInfo moreInfo;
    private ListView lv_1;
    private void init() {
        title=getIntent().getStringExtra("title");
        setTitle(title);
        lv_1=(ListView)findViewById(R.id.lv_1);
        try {
            url=new URL(getIntent().getStringExtra("url"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new buildBean(this).buildMoreInfo(url, new buildBean.OnBuildBean() {
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
                moreInfo=(MoreInfo)s;
                findViewById(R.id.tv_load).setVisibility(View.GONE);
                lv_1.setAdapter(new ArrayAdapter<String>(MoreItemActivity.this,android.R.layout.simple_list_item_1,moreInfo.toStrings()));
                lv_1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(MoreItemActivity.this,DetailActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("item",moreInfo.get(position));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onError(String msg) {
                toast("错误:"+msg);
            }
        });
    }

}
