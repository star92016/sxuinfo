package cn.starnine.sxuinfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.starnine.sxuinfo.utils.Analysis;
import cn.starnine.sxuinfo.utils.MyStringRequest;

public class DetailActivity extends BaseActivity implements MyStringRequest.MyResponse {
    private String title, href;
    private SharedPreferences sp;
    private TextView tv_head;
    private TextView tv_context;
    private RequestQueue queue;

    @Override
    public void beforeSetView() {
        title = getIntent().getStringExtra("title");
        href = getIntent().getStringExtra("href");
        if (title == null || title.equals("") || href == null || href.equals("")) {
            finish();
            return;
        }
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        if (sp.getString("cookie", "").equals("")) {
            finish();
            return;
        }
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_detail);
    }

    private ListView lv_list;

    public void initView() {
        setTitle(title);
        lv_list = (ListView) findViewById(R.id.lv_list);
        tv_context = (TextView) findViewById(R.id.tv_context);
        tv_head = (TextView) findViewById(R.id.tv_head);
        TextView tv = (TextView) findViewById(R.id.tv_title);
        tv.setText(title);
        dialog = new ProgressDialog(this);
        dialog.setMessage("加载中");
        dialog.show();
        queue = Volley.newRequestQueue(this);
        queue.add(new MyStringRequest(href, this));

    }

    private ProgressDialog dialog;

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        if (dialog != null) dialog.cancel();
        toast("网络错误或身份过期");
        finish();
        //startActivity(new Intent(this, LoginActivity.class));
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    article = (Analysis.Article) msg.obj;
                    tv_head.setText(article.header);
                    tv_context.setText(article.body);
                    if (dialog != null) dialog.cancel();
                    lv_list.setAdapter(new SimpleAdapter(DetailActivity.this, getData(), R.layout.lv_detail, new String[]{"name"}, new int[]{R.id.tv_name}));
                    lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        }
                    });
                    break;
                case 2:
                    tv_context.setText(msg.obj.toString());
                    if (dialog != null) dialog.cancel();
                    break;
            }
        }
    };

    private List<HashMap<String, String>> getData() {
        List<HashMap<String, String>> list = new ArrayList<>();
        for (Analysis.Article.Adder a : article.adder) {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", a.name);
            list.add(map);
        }
        return list;
    }

    private Analysis.Article article;

    @Override
    public void onResponse(final String s) {
        new Thread() {
            public void run() {
                Analysis.Article article = new Analysis(s).parser();
                Message msg = new Message();
                msg.what = 1;
                msg.obj = article;
                handler.sendMessage(msg);

            }
        }.start();
    }

    @Override
    public void onGetCookie(String cookie) {

    }

    @Override
    public String onSetCookie() {
        return sp.getString("cookie", "");
    }

    @Override
    public Map<String, String> getParams() {
        return null;
    }

    @Override
    public void onClick(View view) {

    }
}
