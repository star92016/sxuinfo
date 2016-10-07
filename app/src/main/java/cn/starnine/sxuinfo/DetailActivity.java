package cn.starnine.sxuinfo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import android.support.v7.app.AppCompatActivity;
import cn.starnine.sxuinfo.bean.DetailInfo;
import cn.starnine.sxuinfo.bean.ItemInfo;
import cn.starnine.sxuinfo.parse.buildBean;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        init();
    }
    Toast toast;
    public void toast(String msg){
        if(toast==null)
            toast=Toast.makeText(this,"",Toast.LENGTH_LONG);
        toast.setText(msg);
        toast.show();
    }
    private DetailInfo detailInfo;
    private ItemInfo itemInfo;
    private TextView tv_head;
    private TextView tv_content;
    private ListView lv_1;
    private void init() {
        tv_head=(TextView)findViewById(R.id.tv_head);
        tv_content=(TextView)findViewById(R.id.tv_content);
        lv_1=(ListView)findViewById(R.id.lv_1);
        itemInfo=(ItemInfo)getIntent().getSerializableExtra("item");
        setTitle(itemInfo.getTitle());
        new buildBean(this).buildDetailInfo(itemInfo.getUrl(), new buildBean.OnBuildBean() {
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
                detailInfo=(DetailInfo)s;
                tv_head.setText(detailInfo.getHead());
                tv_content.setText(detailInfo.getContent());

            }

            @Override
            public void onError(String msg) {
                toast("错误："+msg);
            }
        });
    }
}
