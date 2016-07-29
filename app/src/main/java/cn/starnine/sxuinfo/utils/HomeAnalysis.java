package cn.starnine.sxuinfo.utils;

import android.os.Handler;
import android.os.Message;

import java.util.Vector;

/**
 * Created by licheng on 16-7-29.
 */
public class HomeAnalysis {
    public interface OnParse{
        void onParseError();
        void onParseFinished(SXUInfo sxuInfo);
    }
    public static void Parse(final String s,final OnParse onParse){
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        onParse.onParseFinished((SXUInfo) msg.obj);
                        break;
                    case 2:
                        onParse.onParseError();
                        break;
                }
            }
        };
        new Thread(){
            public void run(){
                HomeAnalysis.SXUInfo sxuinfo = new SXUInfo();
                int a, b;
                a = s.indexOf("<div id=\"welcomeMsg\">");
                if (a == -1) {
                    handler.sendEmptyMessage(2);
                    return;
                }
                b = s.indexOf("</div>", a);
                sxuinfo.setName(s.substring(a + 25, b).trim());
                a = s.indexOf("部门", b);
                b = s.indexOf("</li>", a);
                sxuinfo.setDepart(s.substring(a + 7, b).trim());
                a = s.indexOf("上次登录时间", b);
                b = s.indexOf("<br>", a);
                sxuinfo.setLasttime(s.substring(a + 11, b).trim());
                a = s.indexOf("上次登录 IP", b);
                b = s.indexOf("</ul>", a);
                sxuinfo.setLastip(s.substring(a + 12, b).trim());
                a = s.indexOf("<img onError=\"this.src='images/defaultFace.jpg'\"");
                b = s.indexOf(" alt=\"\"", a);
                sxuinfo.setPhoto(s.substring(a + "<img onError=\"this.src='images/defaultFace.jpg'\"".length() + 6, b - 1).trim());

                int c = 0;

                while ((c = s.indexOf("portletFrame", c)) >= 0) {
                    c += 10;
                    a = s.indexOf("<span>", c);
                    b = s.indexOf("</span>", c);
                    String name = s.substring(a + 6, b);

                    if (name.equals("温馨提醒") || name.equals("个人信息")) {
                        continue;
                    }
                    int end = s.indexOf("portletFrame", c);
                    Info info =new Info(name);

                    if (end > 0) {
                        b = c;
                        while ((a = s.indexOf("rss-btn-close", b)) < end) {

                            a = s.indexOf("title=", a);
                            b = s.indexOf("class=", a);
                            String title = s.substring(a + 7, b).trim();
                            title = title.substring(0, title.length() - 1).trim();
                            a = s.indexOf("href=", b);
                            b = s.indexOf("target=", a);
                            String href = s.substring(a + 6, b).trim();
                            href = href.substring(0, href.length() - 1).trim();
                            href = href.replaceAll("&amp;", "&");
                            //Log.v("A",title);
                            info.add(title, href);
                        }
                    } else {
                        b = c;
                        while ((a = s.indexOf("rss-btn-close", b)) > 0) {

                            a = s.indexOf("title=", a);
                            b = s.indexOf("class=", a);
                            String title = s.substring(a + 7, b).trim();
                            title = title.substring(0, title.length() - 1).trim();
                            a = s.indexOf("href=", b);
                            b = s.indexOf("target=", a);
                            String href = s.substring(a + 6, b).trim();
                            href = href.substring(0, href.length() - 1).trim();
                            href = href.replaceAll("&amp;", "&");
                            info.add(title, href);

                        }
                    }
                    sxuinfo.infos.add(info);
                }


                Message msg = new Message();
                msg.what = 1;
                msg.obj = sxuinfo;
                handler.sendMessage(msg);
            }
        }.start();
    }
    public static class Info {
        private String name;
        private Vector<Item> vec;

        public Info(String name) {
            vec = new Vector<>();
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Vector<Item> getAll() {
            return vec;
        }

        public Item get(int id) {
            return vec.get(id);
        }

        public void add(String title, String href) {
            vec.add(new Item(title, href));
        }

        public class Item {
            private String title, href;

            public Item(String title, String href) {
                this.title = title;
                this.href = href;
            }

            public String getTitle() {
                return title;
            }

            public String getHref() {
                return href;
            }
        }
    }

    public static class SXUInfo {
        public Vector<Info> infos;
        private String name;
        private String depart, lasttime, lastip, photo;

        public SXUInfo() {
            infos = new Vector<>();
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
