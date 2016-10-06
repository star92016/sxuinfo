package cn.starnine.sxuinfo.net.http;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import cn.starnine.sxuinfo.utils.Config;

public class HttpClient {
	public static void main(String[] args) {
		HttpClient h=new HttpClient();
		HashMap<String, String> hashmap=new HashMap<String, String>();
		hashmap.put("Login.Token1","2013241031");

       hashmap.put("Login.Token2","324103");
       hashmap.put("goto","http://myportal.sxu.edu.cn/loginSuccess.portal");
       hashmap.put("gotoOnFail","http://myportal.sxu.edu.cn/loginFailure.portal");
		try {
			h.init(new URL("http://myportal.sxu.edu.cn/destroySSOToken.portal?goto=http%3A%2F%2Fmyportal.sxu.edu.cn%2Findex.portal"), "GET",null);
		} catch (MalformedURLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		//h.setFile(new File("d:\\"));
		h.setCookie("Path=/; JSESSIONID=00006Zp2VogHlXLuZhmOx5L-wa5:16nljukd0; iPlanetDirectoryPro=AQIC5wM2LY4SfczPppCo87CvqyBMRgivHbZc3ZkXEVS8LrI%3D%40AAJTSQACMDI%3D%23");
		h.start(new OnHttpClient(){

			@Override
			public void onGetCookie(String cookie) {
				System.out.println("Cookie  \n"+cookie);
			}

			@Override
			public void onLoadFinish(String html) {
				System.out.println(html);
			}

			@Override
			public void onDownLoadFinish() {
				System.out.println("下载完成");
			}

			@Override
			public void onDownLoadProcess(int process) {
				System.out.println("下载了"+process+"%");
			}
			
		});
	}
	
	public static class OnHttpClient{
		public void onGetCookie(String cookie){}
		public void onLoadFinish(String html){}
		public void onDownLoadFinish(){}
		public void onDownLoadProcess(int process){}
	}
	/**
	 * 设置cookie
	 * */
	public void setCookie(String cookie) {
		this.cookie=cookie;
		request.put("Cookie",cookie);
	}

	/**
	 * 获得cookie
	 * */
	public String getCookie() {
		return cookie;
	}
	public void setFile(File file){
		if(httprequest==null)
			throw new RuntimeException("Call init first");
		httprequest.setOutFile(file);
	}
	private String cookie = null;
	private HttpRequest httprequest;
	public void init(URL url,String method,HashMap<String,String> param){
		httprequest=new HttpRequest(url, method, param);
		request.put("Connection", "close");
		request.put("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		request.put(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
		setCookie(Config.getCookie());
	}
	private HashMap<String,String> request=new HashMap<String, String>();
	public void start(final OnHttpClient onHttpClient) {
		if(httprequest==null)
			throw new RuntimeException("Call init first");
		httprequest.setRequest(request);
		httprequest.start(new HttpRequest.OnHttpRequest() {
			public void onReciveResponse() {
				HashMap<String,String> res=httprequest.getResponse();
				if(res!=null&&res.get("Set-Cookie")!=null){
					cookie=res.get("Set-Cookie");
                    Config.putCookie(cookie);
					if(onHttpClient!=null)
						onHttpClient.onGetCookie(cookie);
				}
			}
			public void onLoadFinish(String html) {
				if(onHttpClient!=null)
					onHttpClient.onLoadFinish(html);
			}
			public void onDownLoadFinish() {
				if(onHttpClient!=null)
					onHttpClient.onDownLoadFinish();
			}
			public void onDownLoadProcess(int process) {
				if(onHttpClient!=null)
					onHttpClient.onDownLoadProcess(process);
			}
			
		});
	}
}
