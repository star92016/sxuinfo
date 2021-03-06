package cn.starnine.sxuinfo.bean;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainInfo implements Serializable{
	public static String toUnique(URL url){
		return "MainInfo";
	}

	public static class BlockInfo implements Serializable{
		private String title;
		private List<ItemInfo> itemInfos = new ArrayList<ItemInfo>();

		public String getTitle() {
			return title;
		}

		public String toString() {
			//return "title:"+title+"size:"+itemInfos.size()+"more:"+moreUrl;
			return "title:" + title + "\n" + itemInfos.toString()+"\nclipMore:"+moreUrl;
		}
		public String[] toStrings(){
			String[] strs=new String[itemInfos.size()+1];
			for (int i=0;i<itemInfos.size();i++){
				strs[i]=itemInfos.get(i).getTitle()+"("+itemInfos.get(i).getTime()+")";
			}
			strs[strs.length-1]="更多...";
			return strs;
		}
		public int size(){
			return itemInfos.size();
		}
		public ItemInfo get(int i){
			return itemInfos.get(i);
		}
		private int pos;

		public BlockInfo(String title, int pos) {
			this.title = title;
			this.pos = pos;
		}
		private URL moreUrl;

		public URL getMoreUrl() {
			return moreUrl;
		}
	}
	public String[] toBlockStr(){
		String[] strings=new String[blockInfos.size()];
		for (int i=0;i<blockInfos.size();i++){
			strings[i]=blockInfos.get(i).title;
		}
		return strings;
	}
	public BlockInfo get(int i){
		return blockInfos.get(i);
	}

	private List<BlockInfo> blockInfos = new ArrayList<MainInfo.BlockInfo>();

	public String toString() {
		return blockInfos.toString();
	}
	private int morepos=0;
	public final void addMore(String url) throws Exception {
		URL u = new URL("http://myportal.sxu.edu.cn/"
				+ url.replaceAll("&amp;","&"));
		if(morepos>=blockInfos.size())
			throw new Exception("Too Much clipMore");
		blockInfos.get(morepos).moreUrl=u;
		morepos++;
	}

	public final void addBlock(String title, int pos) {
		blockInfos.add(new BlockInfo(title, pos));
	}

	public final void addItem(String title, String time, String href, int pos)
			throws Exception {
		for (int i = 0; i < blockInfos.size(); i++) {
			if (pos > blockInfos.get(i).pos) {
				continue;
			} else {
				if (i <= 0) {
					throw new Exception("Couldn't find place to place");
				} else {
					blockInfos.get(i - 1).itemInfos.add(new ItemInfo(title,
							time, new URL("http://myportal.sxu.edu.cn/"
									+ href.replaceAll("&amp;", "&"))));
					return;
				}
			}
		}
		blockInfos.get(blockInfos.size()-1).itemInfos.add(new ItemInfo(title,
				time, new URL("http://myportal.sxu.edu.cn/"
						+ href.replaceAll("&amp;", "&"))));
	}
}
