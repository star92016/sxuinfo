package cn.starnine.sxuinfo.parse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.starnine.sxuinfo.bean.DetailInfo;
import cn.starnine.sxuinfo.bean.MainInfo;
import cn.starnine.sxuinfo.bean.MoreInfo;


public class Parse {
	public static final String index = "c:\\Users\\licheng\\Desktop\\sxuinfo\\index.html";
	public static final String more = "c:\\Users\\licheng\\Desktop\\sxuinfo\\两办通知.txt";
	public static final String detail = "c:\\Users\\licheng\\Desktop\\sxuinfo\\具体.html";

	public static void main(String[] args) {
		String str = new Parse().getFromFile(detail);
		print(Parse.buildDetailInfo(str,null).toString());
		//print(buildMoreInfo(str).toString());
	}

	public static void print(String str) {
		System.out.println(str);
	}

	public static MainInfo buildMainInfo(String str) {
		Pattern p = Pattern
				.compile("<div\\sclass=\"portlet\"(\\w|\\W)+?<span>(.+?)</span>");
		Matcher m = p.matcher(str);
		MainInfo mainInfo = new MainInfo();
		while (m.find()) {
			if (m.group(2).equals("个人信息") || m.group(2).equals("温馨提醒"))
				continue;
			mainInfo.addBlock(m.group(2), m.start(2));
		}
		p = Pattern
				.compile("<div class=\"clipMore\">(\\w|\\W)+?href=\"(.+?)\"");
		m = p.matcher(str);
		while (m.find()) {
			try {
				mainInfo.addMore(m.group(2).trim());
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		p = Pattern
				.compile("<span class=\"rss-time\">(\\w|\\W)+?</span>((\\w|\\W)+?)</span>(\\w|\\W)+?title='(.+?)'.+?href=\"(.+?)\"");
		m = p.matcher(str);
		while (m.find()) {
			try {
				mainInfo.addItem(m.group(5),
						m.group(2).replaceAll("(&nbsp;|\\s)", ""), m.group(6),
						m.start());
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		return mainInfo;
	}

	public static DetailInfo buildDetailInfo(String str, Map<String,String> map){
		DetailInfo detailInfo=null;
		str=str.replaceAll("<!--(\\w|\\W)*?-->", "");
		str=str.replaceAll("<script(\\w|\\W)*?</script>", "");
		int cstart=0,cend=0;
		Pattern p = Pattern
				.compile("<div class=\"bulletin-title\">((\\w|\\W)+?)</div>");
		String title=null,info=null,time=null,who=null,depart=null;
		String content=null;
		int reads=0;
		Matcher m = p.matcher(str);
		if(m.find()){
			title=m.group(1).replaceAll("\\s", "");
		}
		
		p = Pattern
				.compile("<div class=\"bulletin-info\">((\\w|\\W)+?)<span(\\w|\\W)+?</div>");
		m = p.matcher(str);
		if(m.find()){
			cstart=m.end();
			info=m.group(1).replaceAll("(&nbsp;|\\s)", "");
			String []infos=info.split("\\|");
			for(String s:infos){
				String []ss=s.split("：");
				if(ss.length!=2)
					throw new RuntimeException("ss.length<2");
				if(ss[0].equals("发布时间")){
					time=ss[1];
				}else if(ss[0].equals("发布人")){
					who=ss[1];
				}else if(ss[0].equals("发布部门")){
					depart=ss[1];
				}else if(ss[0].equals("阅读人数")){
					reads=Integer.parseInt(ss[1]);
				}
			}
		}
		detailInfo=new DetailInfo(title, time, who, depart, reads, content);
		p = Pattern
				.compile("<div class=\"bulletin-info\">((\\d|\\D)+?)(<span|</table)");
		m = p.matcher(str);
		if(m.find()&&m.find()){
			//print("od");
			cend=m.start();
			String ss=m.group(1);
			p = Pattern
					.compile("<a.+?href=\"(.+?)\"(\\d|\\D)+?>(.+?)</a>");
			m = p.matcher(ss);
			while(m.find()){
				detailInfo.addFile(m.group(3).trim(), m.group(1).trim());
			}
		}
		if(cend==0){
			str=str.substring(cstart).replaceAll("<(\\d|\\D)+?>","");
		}else{
			str=str.substring(cstart, cend).replaceAll("<(\\d|\\D)+?>","");
		}
		str=str.replaceAll("(&nbsp;| )", "");
		detailInfo.setContent(str,map);
		return detailInfo;
	}
	public static MoreInfo buildMoreInfo(String str) {
		MoreInfo moreInfo = new MoreInfo();
		Pattern p = Pattern
				.compile("<span class=\"rss-time\">(\\w|\\W)+?>(.+?)</span>(\\w|\\W)+?>(.+?)</span>(\\w|\\W)+?<a class=\"rss-title\"(\\w|\\W)+?href=\"(.+?)\"(\\w|\\W)+?>(.+?)</a>");
		Matcher m = p.matcher(str);
		while (m.find()) {
			moreInfo.addItemInfo(m.group(9).replaceAll("\\s", ""), m.group(4)
					.replaceAll("(&nbsp;|\\s)", ""),
					m.group(7).replaceAll("&amp;", "&"), m.group(2));
		}
		p = Pattern
				.compile("<div class=\"pagination-info clearFix\">(\\w|\\W)+?<span(\\w|\\W)+?>((\\w|\\W)+?)</span>(\\w|\\W)+?<a.+?href='(.+?)'(\\w|\\W)+?</a>(\\w|\\W)+?<div(\\w|\\W)+?>(\\d+)</div>");
		m = p.matcher(str);

		if (m.find()) {
			String[] array = m.group(3).replaceAll("\\s", "").split("/");
			moreInfo.initPage(Integer.parseInt(array[1]),
					Integer.parseInt(array[0]), Integer.parseInt(m.group(10)),"http://myportal.sxu.edu.cn/"
							+ m.group(6).replaceAll("&amp;", "&"));
		}
		p = Pattern
				.compile("<td width=20%  class=\"bulletin_group\"((\\w|\\W)+?)</table>");
		m = p.matcher(str);
		if(m.find()){
			String str2=m.group(1);
			p = Pattern
					.compile("<a(\\w|\\W)+?href=\"(.+?)\"(\\w|\\W)*?>((\\w|\\W)+?)</a>");
			m = p.matcher(str2);
			while(m.find()){
				moreInfo.addGroup(m.group(4).replaceAll("\\[按分类\\]",""), m.group(2).replaceAll("&amp;", "&"));
			}
		}
		return moreInfo;
	}

	public String getFromFile(String file) {
		StringBuilder builder = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "utf-8"));
			String line;
			while ((line = br.readLine()) != null) {
				builder.append(line);
				builder.append("\n");
			}
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		return builder.toString();
	}
}
