package cn.starnine.sxuinfo.bean;

import java.io.Serializable;
import java.net.URL;

public class ItemInfo implements Serializable{
	private String title;
	private String time;
	private boolean isRead;
	private URL url;
	private String depart;

	public ItemInfo(String title, String time, URL url) {
		this.title = title;
		this.time = time;
		this.url = url;
	}

	public ItemInfo(String title, String time, URL url, String depart) {
		this.title = title;
		this.time = time;
		this.url = url;
		this.depart = depart;
	}

	public String getTitle() {
		return title;
	}

	public String getTime() {
		return time;
	}

	public String getDepart() {
		return depart;
	}

	public URL getUrl() {
		return url;
	}

	public String toString() {
		return "title:" + title + "\ntime:" + time + "\ndepart:" + depart
				+ "\nurl:" + url + "\nisread:" + isRead;
	}
}
