package cn.starnine.sxuinfo.bean;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MoreInfo implements Serializable{
	private List<ItemInfo> itemInfos=new ArrayList<ItemInfo>();
	private int totalpages;
	private int totalrecords;
	private int currentpages;
	private String pagebefore;
	private String pageafter;
	private Map<String,URL> group=new LinkedHashMap<String,URL>();
	public void initPage(int totalpages,int totalrecords,int currentpages,String url){
		this.totalpages=totalpages;
		this.totalrecords=totalrecords;
		this.currentpages=currentpages;
		int t=url.indexOf("pageIndex=");
		pagebefore=url.substring(0, t+10);
		pageafter=url.substring(url.indexOf("&",t));
	}
	public void addGroup(String title,String url){
		try {
			group.put(title,new URL("http://myportal.sxu.edu.cn/"+url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	public String toString() {
		//return itemInfos.toString();
		return currentpages+"/"+totalpages+"/"+totalrecords+pagebefore+" "+pageafter+itemInfos.size()+"\n"+group.toString();
	}
	public void addItemInfo(String title, String time,String url,String depart){
		try {
			itemInfos.add(new ItemInfo(title, time,new URL("http://myportal.sxu.edu.cn/"+url.trim()), depart));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
