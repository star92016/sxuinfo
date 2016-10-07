package cn.starnine.sxuinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import cn.starnine.sxuinfo.bean.ItemInfo;
import cn.starnine.sxuinfo.bean.MainInfo;

public class MainItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }
    Toast toast;
    public void toast(String msg){
        if(toast==null)
            toast=Toast.makeText(this,"",Toast.LENGTH_LONG);
        toast.setText(msg);
        toast.show();
    }
    private ListView listView;
    private MainInfo.BlockInfo blockinfo;
    private void init() {
        blockinfo=(MainInfo.BlockInfo)getIntent().getSerializableExtra("block");
        setTitle(blockinfo.getTitle());
        listView=(ListView)findViewById(R.id.lv_1);
        String []strs=blockinfo.toStrings();
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,strs));
        findViewById(R.id.tv_load).setVisibility(View.GONE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==blockinfo.size()){
                    String title=blockinfo.getTitle();
                    String moreurl=blockinfo.getMoreUrl().toString();
                    Intent intent=new Intent(MainItemActivity.this,MoreItemActivity.class);
                    intent.putExtra("title",title);
                    intent.putExtra("url",moreurl);
                    startActivity(intent);
                }else{
                    Intent intent=new Intent(MainItemActivity.this,DetailActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("item",blockinfo.get(position));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }
}
