package cn.starnine.sxuinfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;
import cn.starnine.sxuinfo.data.MyConfig;
import cn.starnine.sxuinfo.utils.MyStringRequest;
import cn.starnine.sxuinfo.utils.HomeAnalysis;
import cn.starnine.sxuinfo.utils.HomeAnalysis.*;

public class HomeActivity extends BaseActivity implements MyStringRequest.MyResponse,OnParse {

    private SharedPreferences sp;
    private RequestQueue queue;

    @Override
    public void beforeSetView() {
        super.beforeSetView();
        if (sp.getString("cookie", "").equals("")) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_home);
    }

    private ProgressDialog dialog;
    private ExpandableListAdapter adapter;

    public void initView() {
        tv_depart = (TextView) findViewById(R.id.tv_depart);
        tv_last_ip = (TextView) findViewById(R.id.tv_last_ip);
        tv_last_time = (TextView) findViewById(R.id.tv_last_time);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_photo = (TextView) findViewById(R.id.tv_photo);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        elv_list = (ExpandableListView) findViewById(R.id.elv_list);
        dialog = new ProgressDialog(this);
        dialog.setMessage("加载中");
        dialog.setCancelable(false);
        dialog.show();
        queue = Volley.newRequestQueue(this);
        queue.add(new MyStringRequest(MyConfig.HomeUrl, this));
    }


    @Override
    public void onResponse(final String s) {
        HomeAnalysis.Parse(s,this);
    }

    private ImageView iv_photo;
    private ExpandableListView elv_list;
    private TextView tv_name, tv_depart, tv_last_time, tv_last_ip, tv_photo;

    private void loadphoto() {
        ImageRequest imageRequest = new ImageRequest(
               MyConfig.HomeUrl+"/"+ sxuinfo.getPhoto(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        iv_photo.setImageBitmap(response);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                iv_photo.setImageResource(R.drawable.login_icon_account);
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> localHashMap = new HashMap<>();
                localHashMap.put("User-Agent", "Mozilla/4.0 (compatibl; MSIE 5.5; Windows NT)");
                localHashMap.put("Cookie", sp.getString("cookie", ""));
                return localHashMap;
            }
        };
        queue.add(imageRequest);
    }

    private HomeAnalysis.SXUInfo sxuinfo;

    private void loadinfo() {
        adapter = new BaseExpandableListAdapter() {

            @Override
            public int getGroupCount() {
                return sxuinfo.infos.size();
            }

            @Override
            public int getChildrenCount(int i) {
                return sxuinfo.infos.get(i).getAll().size();
            }

            @Override
            public Object getGroup(int i) {
                return sxuinfo.infos.get(i);
            }

            @Override
            public Object getChild(int i, int i1) {
                return sxuinfo.infos.get(i).getAll().get(i1);
            }

            @Override
            public long getGroupId(int i) {
                return i;
            }

            @Override
            public long getChildId(int i, int i1) {
                return i * i1;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
                View v = LinearLayout.inflate(getApplicationContext(), R.layout.elv_group_home, null);
                ((TextView) v.findViewById(R.id.tv_name)).setText(sxuinfo.infos.get(i).getName());
                return v;
            }

            @Override
            public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
                View v = LinearLayout.inflate(getApplicationContext(), R.layout.elv_child_home, null);
                ((TextView) v.findViewById(R.id.tv_name)).setText(sxuinfo.infos.get(i).getAll().get(i1).getTitle());
                return v;
            }
            @Override
            public boolean isChildSelectable(int i, int i1) {
                return true;
            }
        };
        elv_list.setAdapter(adapter);
        elv_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
                intent.putExtra("title", sxuinfo.infos.get(i).getAll().get(i1).getTitle());
                intent.putExtra("href", MyConfig.HomeUrl+"/" + sxuinfo.infos.get(i).getAll().get(i1).getHref());
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onGetCookie(String cookie) {
        //TODO 这里需要每次都保存cookie
        String str = sp.getString("cookie", "");
        if (str.startsWith("JSESSIONID")) {

        } else {
            String tmp = cookie;
            tmp = tmp.substring(0, tmp.lastIndexOf(";") + 1) + " " + str;
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("cookie", tmp);
            edit.apply();
        }
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
    public void onParseError() {
        //TODO 需要出现几率小
        if (dialog != null) dialog.cancel();
        toast("身份过期");
        finish();
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        //TODO 需要为用户登录一次//当然需要判断是不是真的网断了
        if (dialog != null) dialog.cancel();
        toast("网络错误或身份过期");
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
    @Override
    public void onParseFinished(SXUInfo sxuInfo) {
        if (dialog != null) dialog.cancel();
        sxuinfo = sxuInfo;
        tv_last_ip.setText(sxuinfo.getLastip());
        tv_name.setText(sxuinfo.getName());
        tv_last_time.setText(sxuinfo.getLasttime());
        tv_depart.setText(sxuinfo.getDepart());
        tv_photo.setText(sxuinfo.getPhoto());
        loadphoto();
        loadinfo();
    }
}
