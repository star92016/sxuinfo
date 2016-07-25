package cn.starnine.sxuinfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class HomeActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<String>{

    private SharedPreferences sp;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sp =getSharedPreferences("config", Context.MODE_PRIVATE);
        if(sp.getString("cookie","").equals("")){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
            return;
        }
        initView();
    }
    private ProgressDialog dialog;
   private ExpandableListAdapter adapter;
    private void initView() {
        tv_depart=(TextView) findViewById(R.id.tv_depart);
        tv_last_ip=(TextView)findViewById(R.id.tv_last_ip);
        tv_last_time=(TextView)findViewById(R.id.tv_last_time);
        tv_name=(TextView)findViewById(R.id.tv_name);
        tv_photo=(TextView)findViewById(R.id.tv_photo);
iv_photo=(ImageView)findViewById(R.id.iv_photo);
        elv_list=(ExpandableListView)findViewById(R.id.elv_list);
        setTitle("主界面");
        dialog=new ProgressDialog(this);
        dialog.setMessage("加载中");
        dialog.show();
        queue=Volley.newRequestQueue(this);

        String url="http://myportal.sxu.edu.cn";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, this,this){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {

                    Map<String, String> responseHeaders = response.headers;
                    String rawCookies = responseHeaders.get("Set-Cookie");
                    Message msg=new Message();
                    msg.what=3;
                    msg.obj=rawCookies==null?"":rawCookies;
                    handler.sendMessage(msg);
                    String dataString = new String(response.data, "UTF-8");
                    return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> localHashMap =new HashMap<>();
                localHashMap.put("User-Agent", "Mozilla/4.0 (compatibl; MSIE 5.5; Windows NT)");
                localHashMap.put("Cookie",sp.getString("cookie",""));
                return localHashMap;
            }
        };
        queue.add(stringRequest);
    }
    private Toast toast;
    public void toast(String str){

        if(toast==null) {
            toast = Toast.makeText(this,str,Toast.LENGTH_SHORT);
        }
        else
            toast.setText(str);
        toast.show();
    }
    @Override
    public void onErrorResponse(VolleyError volleyError) {

        if(dialog!=null)dialog.cancel();
        toast("网络错误或身份过期");
        finish();
        startActivity(new Intent(this,LoginActivity.class));
    }

    @Override
    public void onResponse(final String s) {

        new Thread(){
            public void run(){
                SXUInfo sxuinfo=new SXUInfo();
                int a,b;
                a=s.indexOf("<div id=\"welcomeMsg\">");
                if(a==-1){
                    handler.sendEmptyMessage(2);
                    return;
                }
                b=s.indexOf("</div>",a);
                sxuinfo.setName(s.substring(a+25,b).trim());
                a=s.indexOf("部门",b);
                b=s.indexOf("</li>",a);
                sxuinfo.setDepart(s.substring(a+7,b).trim());
                a=s.indexOf("上次登录时间",b);
                b=s.indexOf("<br>",a);
                sxuinfo.setLasttime(s.substring(a+11,b).trim());
                a=s.indexOf("上次登录 IP",b);
                b=s.indexOf("</ul>",a);
                sxuinfo.setLastip(s.substring(a+12,b).trim());
                a=s.indexOf("<img onError=\"this.src='images/defaultFace.jpg'\"");
                b=s.indexOf(" alt=\"\"",a);
                sxuinfo.setPhoto(s.substring(a+"<img onError=\"this.src='images/defaultFace.jpg'\"".length()+6,b-1).trim());

                int c=0;

                while((c=s.indexOf("portletFrame",c))>=0){
                    c+=10;
                    a=s.indexOf("<span>",c);
                    b=s.indexOf("</span>",c);
                    String name=s.substring(a+6,b);

                    if(name.equals("温馨提醒")||name.equals("个人信息")){
                        continue;
                    }
                    int end=s.indexOf("portletFrame",c);
                    Info info=new Info(name);

                    if(end>0){
                        b=c;
                        while((a = s.indexOf("rss-btn-close", b))<end) {

                            a = s.indexOf("title=", a);
                            b = s.indexOf("class=", a);
                            String title = s.substring(a + 7, b).trim();
                            title=title.substring(0,title.length()-1).trim();
                            a = s.indexOf("href=", b);
                            b = s.indexOf("target=", a);
                            String href = s.substring(a + 6, b).trim();
                            href=href.substring(0,href.length()-1).trim();
                            href= href.replaceAll("&amp;","&");
                            //Log.v("A",title);
                            info.add(title, href);
                        }
                    }else{
                        b=c;
                        while((a = s.indexOf("rss-btn-close", b))>0) {

                            a = s.indexOf("title=", a);
                            b = s.indexOf("class=", a);
                            String title = s.substring(a + 7, b).trim();
                            title=title.substring(0,title.length()-1).trim();
                            a = s.indexOf("href=", b);
                            b = s.indexOf("target=", a);
                            String href = s.substring(a + 6, b).trim();
                            href=href.substring(0,href.length()-1).trim();
                           href= href.replaceAll("&amp;","&");
                            info.add(title, href);

                        }
                    }
                    sxuinfo.infos.add(info);
                }


                Message msg=new Message();
                msg.what=1;
                msg.obj=sxuinfo;
                handler.sendMessage(msg);
            }
        }.start();

       // toast(s);
    }
private ImageView iv_photo;
    private ExpandableListView elv_list;
    private TextView tv_name,tv_depart,tv_last_time,tv_last_ip,tv_photo;
private void loadphoto(){
    ImageRequest imageRequest = new ImageRequest(
            "http://myportal.sxu.edu.cn/"+sxuinfo.getPhoto(),
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

    }){
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String,String> localHashMap =new HashMap<>();
            localHashMap.put("User-Agent", "Mozilla/4.0 (compatibl; MSIE 5.5; Windows NT)");
            localHashMap.put("Cookie",sp.getString("cookie",""));
            return localHashMap;
        }
    };
    queue.add(imageRequest);
}
    private SXUInfo sxuinfo;
    private Handler handler=new Handler(){



        @Override
        public void handleMessage(Message msg) {
            String str;
            switch (msg.what){
                case 1:
                    if(dialog!=null)dialog.cancel();
                    sxuinfo=(SXUInfo)msg.obj;
                    tv_last_ip.setText(sxuinfo.getLastip());
                    tv_name.setText(sxuinfo.getName());
                    tv_last_time.setText(sxuinfo.getLasttime());
                    tv_depart.setText(sxuinfo.getDepart());
                    tv_photo.setText(sxuinfo.getPhoto());
                    loadphoto();
                    loadinfo();

                    break;
                case 2:
                    if(dialog!=null)dialog.cancel();
                    toast("身份过期");
                    finish();
                    startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                    break;
                case 3:
                    str=sp.getString("cookie","");
                    if(str.startsWith("JSESSIONID")){
                        break;
                    }else {
                        String tmp=msg.obj.toString();
                        tmp=tmp.substring(0,tmp.lastIndexOf(";")+1)+" "+str;
                        SharedPreferences.Editor edit=sp.edit();
                        edit.putString("cookie",tmp);
                        edit.apply();
                    }


                    break;
            }
        }
    };

    private void loadinfo() {
        adapter=new BaseExpandableListAdapter(){

            @Override
            public int getGroupCount() {
                return sxuinfo.infos.size();
            }

            @Override
            public int getChildrenCount(int i) {
                return sxuinfo.infos.get(i).vec.size();
            }

            @Override
            public Object getGroup(int i) {
                return sxuinfo.infos.get(i);
            }

            @Override
            public Object getChild(int i, int i1) {
                return sxuinfo.infos.get(i).vec.get(i1);
            }

            @Override
            public long getGroupId(int i) {
                return i;
            }

            @Override
            public long getChildId(int i, int i1) {
                return i*i1;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
                View v=LinearLayout.inflate(getApplicationContext(),R.layout.elv_group_home,null);
                ((TextView)v.findViewById(R.id.tv_name)).setText(sxuinfo.infos.get(i).name);
                return v;
            }

            @Override
            public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

                View v=LinearLayout.inflate(getApplicationContext(),R.layout.elv_child_home,null);
                ((TextView)v.findViewById(R.id.tv_name)).setText(sxuinfo.infos.get(i).vec.get(i1).title);
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
               Intent intent=new Intent(HomeActivity.this,DetailActivity.class);
                intent.putExtra("title",sxuinfo.infos.get(i).vec.get(i1).title);
                intent.putExtra("href","http://myportal.sxu.edu.cn/"+sxuinfo.infos.get(i).vec.get(i1).href);
                startActivity(intent);
                return false;
            }
        });
    }

    class Info{
        private String name;
        private Vector<Item> vec;
        public Info(String name){
            vec=new Vector<>();
            this.name=name;
        }
        public String getName() {
            return name;
        }

        public Vector<Item> getAll() {
            return vec;
        }

        public Item get(int id){
            return vec.get(id);
        }

        public void add(String title,String href) {
            vec.add(new Item(title,href));
        }

        public class Item{
            private String title,href;
            public Item(String title,String href){
                this.title=title;
                this.href=href;
            }

            public String getTitle() {
                return title;
            }

            public String getHref() {
                return href;
            }
        }
    }
    class SXUInfo{
        public Vector<Info> infos;
        private String name;
        private String depart,lasttime,lastip,photo;
        public SXUInfo(){
            infos=new Vector<>();
        }
        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDepart() {
            return depart;
        }

        public void setDepart(String depart) {
            this.depart = depart;
        }

        public String getLastip() {
            return lastip;
        }

        public void setLastip(String lastip) {
            this.lastip = lastip;
        }

        public String getLasttime() {
            return lasttime;
        }

        public void setLasttime(String lasttime) {
            this.lasttime = lasttime;
        }
    }
}
