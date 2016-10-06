package cn.starnine.sxuinfo.net.http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;




public class HttpRequest {
	public static abstract class OnHttpRequest{
		public void onReciveResponse(){}
		public void onLoadFinish(String html){}
		public void onDownLoadFinish(){}
		public void onDownLoadProcess(int process){}
		public void onError(int code,String msg){}
	}
	private URL url;
	private String method="GET";
	private HashMap<String,String> param;
	private File file=null;
	private String encode="utf-8";
	private Socket socket;
	private HashMap<String, String> request;
	private HashMap<String, String> response;
	private int responseCode;
	private int filesize;
	/**
	 * @param url 请求地址
	 * */
	public HttpRequest(URL url){
		this.url=url;
	}
	/**
	 * @param url 请求地址
	 * @param method 请求方法
	 * @param param 请求参数
	 * */
	public HttpRequest(URL url,String method,HashMap<String,String> param){
		this.url=url;
		this.method=method;
		this.param=param;
	}
	/**
	 * @param method 请求方法
	 * */
	public void setMethod(String method){
		this.method=method;
	}
	
	/**
	 * @return 获得请求头参数
	 * */
	public HashMap<String, String> getRequest(){
		return request;
	}
	/**
	 * @param request 设置请求头参数
	 * */
	public void setRequest(HashMap<String, String> request){
		this.request=request;
	}
	/**
	 * @param param 设置请求参数
	 * */
	public void setParam(HashMap<String,String> param){
		this.param=param;
	}
	/**
	 * @param file 设置输出文件
	 * */
	public void setOutFile(File file){
		this.file=file;
	}
	/**
	 * @return 获得响应码
	 * */
	public int getResponseCode(){
		return responseCode;
	}
	/**
	 * @return 获得文件大小，仅在设置输出文件是有效
	 * */
	public int getFileSize(){
		return filesize;
	}
	/**
	 * @return 获得响应参数
	 * */
	public HashMap<String, String> getResponse(){
		return response;
	}
	private String parseParam() throws Exception{
		if(param==null||param.size()==0)
			return null;
		StringBuilder builder=new StringBuilder();
		for(String s:param.keySet()){
			builder.append(s);
			builder.append("=");
			builder.append(URLEncoder.encode(param.get(s),"utf-8"));
			builder.append("&");
		}
		builder.setLength(builder.length()-1);
		return builder.toString();
	}
	/**
	 * 开始访问
	 * @param onHttpRequest 回调函数集合
	 * */
	public void start(OnHttpRequest onHttpRequest){
		int port=80;
		if(url.getPort()>0)
			port=url.getPort();
		try {
			Log.d(this.getClass().getName(),url.getHost()+port);
			socket=new Socket(url.getHost(),port);
			Log.d(this.getClass().getName(),"1");
			if(!url.getProtocol().equals("http"))
				throw new Exception("Not Http Protocol");
			if(!(method.equals("GET")||method.equals("POST"))){
				throw new Exception("Wrong Method");
			}
			Log.d(this.getClass().getName(),"2");
			PrintStream printstream=new PrintStream(socket.getOutputStream());
			StringBuilder bulider = new StringBuilder();
			String getPos="/";
			boolean hasWen=false;
			if(url.getPath()==null||url.getPath().equals("")){
				getPos="/";
			}else{
				getPos=url.getPath();
			}
			if(url.getQuery()!=null&&!url.getQuery().equals("")){
				hasWen=true;
				getPos+=("?"+url.getQuery());
			}
			if(method.equals("GET")){
				if(param!=null&&param.size()!=0){
					if(hasWen)
						getPos+="&";
					else
						getPos+="?";
					getPos+=parseParam();
				}
				bulider.append("GET "+getPos+" HTTP/1.1\r\n");
			}else{
				bulider.append("POST "+getPos+" HTTP/1.1\r\n");
			}
			bulider.append("Host: "+url.getHost()+"\r\n");
			for(String key:request.keySet()){
				bulider.append(key);
				bulider.append(": ");
				bulider.append(request.get(key));
				bulider.append("\r\n");
			}
			if(method.equals("POST")){
				bulider.append("Content-Type: application/x-www-form-urlencoded\r\n");
				bulider.append("Content-Length: "+parseParam().length()+"\r\n");
			}
			bulider.append("\r\n");
			if(method.equals("POST")){
				bulider.append(parseParam());
			}
			printstream.print(bulider.toString());
			printstream.flush();
			
			InputStream in=socket.getInputStream();
			String str;
			ByteArrayOutputStream bo = new ByteArrayOutputStream(); 
			int state=0;
			while(true){
				byte b=(byte)in.read();
				if(b=='\r'){
					bo.write('\n');
					b=(byte)in.read();
					if(b=='\n'){
						if(state==1){
							break;
						}
						state=1;
					}
				}else{
					state=0;
					bo.write(b);
				}
			}
			BufferedReader is=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bo.toByteArray())));
			if((str=is.readLine())!=null){
				//get status code at here
				String []strArray=str.split(" ");
				if(strArray.length!=3||!strArray[0].equals("HTTP/1.1")){
					throw new Exception("Wrong Response");
				}
				responseCode=Integer.parseInt(strArray[1]);
				
				response=new HashMap<String, String>();
				while((str=is.readLine())!=null){
					if(str.equals(""))
						break;
					strArray=str.split(": ");
					if(strArray.length!=2){
						throw new Exception("Wrong Response");
					}
					response.put(strArray[0], strArray[1]);
					//System.out.println(strArray[0]+": "+strArray[1]);
				}
				onHttpRequest.onReciveResponse();
				//onHttpResponse.onGetResponse(response);
				if(file==null){
					is=new BufferedReader(new InputStreamReader(in,encode));
					bulider=new StringBuilder();
					while((str=is.readLine())!=null){
						bulider.append(str);
						bulider.append("\n");
					}
					onHttpRequest.onLoadFinish(bulider.toString());
					//onHttpResponse.onFinish(bulider.toString());
				}else{
					if((str=response.get("Content-Disposition"))!=null){
						if(str.indexOf("filename=\"")>0){
							str=str.substring(str.indexOf("\"",str.indexOf("filename=\""))+1);
							StringBuilder sb3=new StringBuilder();
							for(int i=0;i<str.length();i++){
								char ch=str.charAt(i);
								if(ch=='"')
									break;
								sb3.append(ch);
							}
							if(file.isDirectory()){
								file=new File(file, sb3.toString().trim());
							}
							System.out.println(sb3.toString());
						}
					}
					if((str=response.get("Content-Length"))!=null){
						filesize=Integer.parseInt(str);
					}
					byte[] buf=new byte[1024];
					int lastprocess=0;
					int total=0;
					int len;
					FileOutputStream fout=new FileOutputStream(file);
					if(response.get("Transfer-Encoding")==null){
					while((len=in.read(buf, 0, 1024))>0){
						fout.write(buf, 0, len);
						total+=len;
						//System.out.println("filesize"+filesize+"total:"+total);
						
						if(filesize!=0){
							int process=(int)(total*100.0/filesize);
							
							if(process>lastprocess){
								onHttpRequest.onDownLoadProcess(process);
								//onHttpResponse.onProcess(process);
							}
						}
					}
					}else{
						if(!response.get("Transfer-Encoding").equals("chunked")){
							fout.close();
							throw new Exception("Unknown Transfer-Encoding");
						}
						int tt;
						len=0;
						state=2;
						StringBuilder sb2=new StringBuilder();
						while((tt=in.read())>=0){
							//System.out.println("len="+len+" state="+state+" tt="+tt);
							if(len>0&&state==0){
								fout.write(tt);
								len--;
								continue;
							}
							if(tt=='\r'&&state==0){
								state=1;
							}else if(tt=='\n'&&state==1){
								state=2;
							}else if(state==2){
								int tmp=-1;
								if(tt>='0'&&tt<='9'){
									tmp=tt-'0';
								}else if(tt>='a'&&tt<='f'){
									tmp=tt-'a'+10;
								}else if(tt>='A'&&tt<='F'){
									tmp=tt-'A'+10;
								}
								if(tmp!=-1){
									len=len*16+tmp;
								}else{
									state=3;
								}
							}
							if(state==3&&tt!='\r'){
								sb2.append((char)tt);
							}
							if(state==3&&tt=='\r'){
								state=4;
							}else if(state==4&&tt=='\n'){
								state=0;
								System.out.println(sb2.toString());
								sb2=new StringBuilder();
							}
						}
						//System.out.println("len="+len+" state="+state+" tt="+tt);
						
					}
					fout.close();
					onHttpRequest.onDownLoadFinish();
				}
				
			}
			printstream.close();
			is.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			onHttpRequest.onError(1,e.getMessage());
		} catch(Exception e){
			e.printStackTrace();
			onHttpRequest.onError(2,e.getMessage());
		}finally{
			try {
				if(socket!=null)
					socket.close();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				onHttpRequest.onError(3,e.getMessage());
			}
		}
	}
}
