package cn.starnine.sxuinfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;
import cn.starnine.sxuinfo.data.MyConfig;
import cn.starnine.sxuinfo.utils.DESTest;
import cn.starnine.sxuinfo.utils.MyStringRequest;
import cn.starnine.sxuinfo.utils.HomeAnalysis;
import cn.starnine.sxuinfo.utils.HomeAnalysis.*;


//TODO 需要退出登录吗
public class HomeActivity extends BaseActivity implements MyStringRequest.MyResponse,OnParse {

    private RequestQueue queue;
    private long exitTime;

    @Override
    public boolean beforeSetView() {
        DESTest.initkey(this);
        if (sp.getString("cookie", "").equals("")) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return false;
        }
        return super.beforeSetView();
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
                return sxuinfo.infos.get(i).getAll().size()+1;
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
                if(i1==getChildrenCount(i)-1){
                    ((TextView) v.findViewById(R.id.tv_name)).setText("更多...");
                    ((TextView) v.findViewById(R.id.tv_time)).setText("");
                }else {
                    ((TextView) v.findViewById(R.id.tv_name)).setText(sxuinfo.infos.get(i).getAll().get(i1).getTitle());
                    ((TextView) v.findViewById(R.id.tv_time)).setText(sxuinfo.infos.get(i).getAll().get(i1).getTime());
                }return v;
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
                if(i1==adapter.getChildrenCount(i)-1){
                    Intent intent=new Intent(HomeActivity.this,MoreActivity.class);
                    intent.putExtra("href",sxuinfo.infos.get(i).getMore());
                    intent.putExtra("title","更多 "+sxuinfo.infos.get(i).getName());
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
                    intent.putExtra("title", sxuinfo.infos.get(i).getAll().get(i1).getTitle());
                    intent.putExtra("href", MyConfig.HomeUrl+"/" + sxuinfo.infos.get(i).getAll().get(i1).getHref());
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onGetCookie(String cookie) {

        Log.e("AA","savecookie");
        String str = sp.getString("cookie", "");
        if (str.startsWith("JSESSIONID")) {
            //TODO JSESSIONID可能会失效 尽量重设
            Log.e("AA","nosavecookie");
            return;

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
    public void tryLoginAgain(){
        queue.add(new MyStringRequest(MyConfig.LoginUrl, new MyStringRequest.MyResponse() {
            @Override
            public void onGetCookie(String cookie) {
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("cookie", cookie);
                edit.apply();
                queue.add(new MyStringRequest(MyConfig.HomeUrl, HomeActivity.this));
            }

            @Override
            public String onSetCookie() {
                return null;
            }

            @Override
            public Map<String, String> getParams() {
                String user = sp.getString("user","");
                String pass = DESTest.decrypt(sp.getString("pass",""));
                Map<String, String> hashmap = new HashMap<>();
                hashmap.put("Login.Token1", user);
                hashmap.put("Login.Token2", pass);
                hashmap.put("goto", "http://myportal.sxu.edu.cn/loginSuccess.portal");
                hashmap.put("gotoOnFail", "http://myportal.sxu.edu.cn/loginFailure.portal");
                return hashmap;
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                toast("网络错误");
            }

            @Override
            public void onResponse(String s) {
                Log.e("AA","response");
            }
        }));
    }
private int trytimes=0;
    @Override
    public void onParseError() {
        //出现几率小

        if(trytimes>0){
            if (dialog != null) dialog.cancel();
            toast("身份过期");
            finish();
             startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            return;
        }
        trytimes++;
        tryLoginAgain();
       // toast("身份过期");
        //finish();
       // startActivity(new Intent(HomeActivity.this, LoginActivity.class));
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        //TODO 需要判断是不是真的网断了
        if (dialog != null) dialog.cancel();
        //if(neterror)
        if(trytimes>0){
            if (dialog != null) dialog.cancel();
            toast("身份过期");
            finish();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            return;
        }
        trytimes++;
        tryLoginAgain();
       //toast("网络错误或身份过期");
        //finish();
        //startActivity(new Intent(this, LoginActivity.class));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,1,1,"退出登录");
        menu.add(1,2,2,"设置");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor=sp.edit();
        switch (item.getItemId()){
            case 1:
                editor.putString("pass","");
                finish();
                startActivity(new Intent(this,LoginActivity.class));
                break;
            case 2:
                toast("菜单");
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                // 返回键功能的实现
                exitTime = System.currentTimeMillis();
                toast("再按一次退出程序");
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
