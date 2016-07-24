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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class HomeActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<String>{

    private SharedPreferences sp;

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
    private void initView() {
        tv_depart=(TextView) findViewById(R.id.tv_depart);
        tv_last_ip=(TextView)findViewById(R.id.tv_last_ip);
        tv_last_time=(TextView)findViewById(R.id.tv_last_time);
        tv_name=(TextView)findViewById(R.id.tv_name);
        tv_photo=(TextView)findViewById(R.id.tv_photo);

        setTitle("主界面");
        dialog=new ProgressDialog(this);
        dialog.setMessage("加载中");
        dialog.show();
        RequestQueue queue=Volley.newRequestQueue(this);

        String url="http://myportal.sxu.edu.cn";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, this,this){
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
        if(dialog!=null)dialog.cancel();
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
                            Log.v("A",title);
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

    private TextView tv_name,tv_depart,tv_last_time,tv_last_ip,tv_photo;


    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    SXUInfo sxuinfo=(SXUInfo)msg.obj;
                    tv_last_ip.setText(sxuinfo.getLastip());
                    tv_name.setText(sxuinfo.getName());
                    tv_last_time.setText(sxuinfo.getLasttime());
                    tv_depart.setText(sxuinfo.getDepart());
                    tv_photo.setText(sxuinfo.getPhoto());
                    toast(sxuinfo.infos.get(0).get(0).getTitle()+sxuinfo.infos.get(0).get(0).getHref());
                    break;
                case 2:
                    toast("身份过期");
                    finish();
                    startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                    break;
            }
        }
    };
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
