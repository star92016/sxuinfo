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
}	
