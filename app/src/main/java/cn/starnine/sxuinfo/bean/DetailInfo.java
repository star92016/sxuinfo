package cn.starnine.sxuinfo.bean;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetailInfo implements Serializable{


	public static class FileInfo implements Serializable{
		private String name;
		private URL url;
		public FileInfo(String name,String url){
			this.name=name;
			try {
				this.url=new URL("http://myportal.sxu.edu.cn/"+url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String toString() {
		return "DetailInfo [title=" + title + ", time=" + time + ", who=" + who
				+ ", depart=" + depart + ", readTimes=" + readTimes
				+ ", content=" + content + ", fileInfos=" + fileInfos + "]";
	}
	public void setContent(String content,Map<String,String> map){
		if (map!=null)
			for(String key:map.keySet()){
				content=content.replaceAll(key,map.get(key));
			}
		this.content=content;
	}
	public void replaceWith(Map<String,String> map){
		if(map!=null){
			for(String str:map.keySet()){
				content=content.replaceAll(str, map.get(str));
			}
		}
	}
	
	public void addFile(String name,String url){
		fileInfos.add(new FileInfo(name,url));
	}
	
	public DetailInfo(URL url,String title, String time, String who, String depart,
			int readTimes, String content) {
		this.url=url;
		this.title = title;
		this.time = time;
		this.who = who;
		this.depart = depart;
		this.readTimes = readTimes;
		this.content = content;
	}

	private String title;
	private String time;
	private String who;
	private String depart;
	private int readTimes;
	private String content;
	private List<FileInfo> fileInfos=new ArrayList<DetailInfo.FileInfo>();
	private URL url;

	public String getContent() {
		return content;
	}

	public String getHead() {
		return "部门："+depart+" 发布时间："+time+" 阅读人数："+readTimes;
	}
	public static String toUnique(URL url){
		String str=url.toString();
		StringBuilder builder=new StringBuilder();
		str=str.split("\\?")[1];
		String[] array=str.split("&");
		for (String s:array){
			String []s1=s.split("=");
			if (s1.length!=2)throw new RuntimeException("Wrong Url");
			if (s1[0].equals(".pen"))
				builder.append(s1[1]);
			else if(s1[0].equals("bulletinId")){
				s1[1]=s1[1].replaceAll("-","");
				builder.append(s1[1]);
			}
		}
		return builder.toString();
	}
}
