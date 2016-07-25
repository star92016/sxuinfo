package cn.starnine.sxuinfo;



import java.util.Stack;
import java.util.Vector;

/**
 * Created by licheng on 16-7-25.
 */
public class Analysis {
    public class Item{
        String name="";
        String id="";
        String cclass="";
    }

    private String s;
    private int index=0;
    private int len;
    private int state=0;
    private boolean instr=false;
    public Analysis(String s){
        this.s=s.toLowerCase();
        len=s.length();
        stack=new Stack<Item>();
    }
    private Stack<Item> stack;
    private Item getcurrent(){
        if(stack.isEmpty()){
            return new Item();
        }else{
            return stack.peek();
        }
    }
    public Article parser(){
        char ch;
        Article article=new Article();
        String name="";
        Item cur;
        String cclass="",id="";
        Item item = new Item();
        while(index<len) {
            ch=s.charAt(index);

            switch (state) {
                case 0:
                    cur=getcurrent();
                    if(cur.name.equals("script")){

                        if(ch=='<'){
                            String tmp=s.substring(index,index+4);
                            if(tmp.startsWith("</s")){

                                state=8;
                                index+=2;
                                break;
                            }
                        }
                        index++;
                    }else if(ch==' '||ch=='　'||ch=='\n'||ch=='\t'||ch=='\r'){
                        index++;
                    }else if(ch=='<'){
                        index++;
                        state=1;
                    }else{


                        if(cur.name.equals("title")||cur.name.equals("style")){
                        }else if(cur.id.equals("footermsg")||cur.cclass.equals("bulletin-title")){

                        }else if(cur.cclass.equals("bulletin-info")) {
                            article.header+=ch;
                        }else if(cur.name.equals("a")){
                            int len=article.adder.size();
                            Article.Adder adder=null;
                            if(len==0||!(adder=article.adder.get(len-1)).href.equals(cur.id)){
                                adder=article. new Adder();
                                adder.name+=ch;
                                adder.href=cur.id;
                                article.adder.add(adder);
                            }else{
                                adder.name+=ch;
                            }
                        }else{
                            article.body+=ch;


                        }
                        index++;
                    }
                    break;
                case 1://<
                    if(instr){
                        if(ch=='\"'){
                            instr=!instr;
                        }
                        index++;
                    }else if(ch==' '||ch=='\n'||ch=='\t'||ch=='\r'){
                        index++;
                    }else if(ch=='!'){
                        index++;
                        state=2;
                    }else if(ch=='/'){
                        name="";
                        state=8;
                        index++;
                    }else {
                        state=9;
                        //no index++
                    }
                    break;
                case 2://<!
                    if(ch=='-'){
                        index++;
                        state=3;
                    }else{
                        index++;
                        state=4;
                    }
                    break;
                case 3://<!-
                    if(ch=='-'){
                        index++;
                        state=5;
                    }else{
                        index++;
                        state=4;
                    }
                    break;
                case 4://<!D...
                    if(instr){
                        if(ch=='\"')instr=!instr;
                    }else if(ch=='>'){
                        state=0;
                    }
                    index++;
                    break;
                case 5://<!--
                    if(instr) {
                        if(ch=='\"'){
                            instr=!instr;
                        }else{
                        }
                    }else if(ch=='\"'){
                        instr=!instr;
                    }else if(ch=='-'){
                        state=6;
                    }
                    index++;
                    break;
                case 6://<!--...-
                    if(ch=='-'){
                        state=7;
                    }else{
                        state=5;
                    }
                    index++;
                    break;
                case 7://<!--...--
                    if(ch=='>'){
                        state=0;
                    }else{
                        state=5;
                    }
                    index++;
                    break;
                case 8://</name
                    if(ch==' '){
                        index++;
                    }else if(ch>='a'&&ch<='z'){
                        name+=ch;
                        index++;
                    }else{
                        state=12;

                    }

                    break;
                case 9://<name
                    if(ch>='a'&&ch<='z'){
                        name+=ch;
                        index++;
                    }else{

                        state=10;
                        item=new Item();

                    }

                    break;
                case 10://<name ....?
                    if(instr){
                        if(ch=='\"'){
                            instr=!instr;
                        }
                    }else if(ch<='z'&&ch>='a'){
                        String tmp=s.substring(index, index+7);
                        if(name.equals("a")){
                            if(tmp.startsWith("href=")){
                                id="";
                                state=14;
                            }
                        }else
                        if(ch=='c'){
                            if(tmp.startsWith("class=")){
                                cclass="";
                                state=13;
                            }
                        }else if(ch=='i'){
                            if(tmp.startsWith("id=")){
                                id="";
                                state=14;
                            }
                        }
                    }else if(ch=='/'){
                        state=11;
                    }else if(ch=='>'){
                        //TODO push <br>no push...
                        if(name.equals("br")){
                            article.body+='\n';

                        }else if(name.equals("meta")){

                        }else{
                            item.name=name;

                            stack.push(item);

                        }
                        name="";
                        state=0;
                    }else if(ch=='\"'){
                        instr=!instr;
                    }
                    index++;
                    break;
                case 11:
                    if(ch==' '||ch=='\n'||ch=='\t'||ch=='\r'){
                        index++;
                    }else if(ch=='>'){
                        //TODO save <br/>

                        article.body+='\n';
                        name="";
                        state=0;
                        index++;
                    }else{
                        state=10;
                        //no index++
                    }
                    break;
                case 12:
                    if(ch=='>'){
                        //TODO pop
                        if(name.equals(getcurrent().name)){

                            stack.pop();
                        }else{

                            int i,len=stack.size();
                            for(i=stack.size()-1;i>=0;i--){
                                if(stack.get(i).name.equals(name)){
                                    break;
                                }
                            }

                            if(i>=0){
                                i=len-i;
                                while(i>0){
                                    i--;
                                    stack.pop();
                                }
                            }

                        }
                        name="";
                        state=0;
                    }
                    index++;
                    break;
                case 13:
                    if(instr){
                        if(ch!='\"')
                            cclass+=ch;
                        else{
                            instr=!instr;
                            item.cclass=cclass;
                            state=10;
                        }
                    }else if(ch=='\"'){
                        instr=!instr;
                    }
                    index++;
                    break;
                case 14:
                    if(instr){
                        if(ch!='\"')
                            id+=ch;
                        else{
                            instr=!instr;
                            item.id=id;
                            state=10;
                        }
                    }else if(ch=='\"'){
                        instr=!instr;
                    }
                    index++;
                    break;
            }
        }
        article.format();
        return article;

    }
    public class Article{
        public String header="";
        public String body="";
        public Vector<Adder> adder=new Vector<Adder>();
        public void format(){
            header=header.replaceAll("&nbsp;"," ");
            while(body.startsWith("\n")){
                body=body.substring(1);
            }
            while(body.endsWith("\n")){
                body=body.substring(0,body.length()-1);
            }
            if(body.startsWith("字号：t|t")){
                body=body.substring(6);
            }
            for(Adder a:adder){
                a.href="http://myportal.sxu.edu.cn/"+a.href;
            }
            body=body.replaceAll("&nbsp;","");
            body=body.replaceAll("&apos;","'");
            body=body.replaceAll("&quot;","\"");
            body=body.replaceAll("&gt;",">");
            body=body.replaceAll("&lt;","<");
            body=body.replaceAll("&ldquo;","“");
            body=body.replaceAll("&rdquo;","”");
        }
        public class Adder{
            public String name="";
            public String href="";
        }
    }

}

