package cn.starnine.sxuinfo.utils;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by licheng on 16-7-29.
 */
public class MyStringRequest extends StringRequest{
    public interface MyResponse extends Response.Listener<String>,Response.ErrorListener{
        void onGetCookie(String cookie);
        String onSetCookie();
        Map<String, String> getParams();
    }
    public MyStringRequest(String url, MyResponse response) {
        super(Method.POST, url, response, response);
        myResponse=response;
    }
    private MyResponse myResponse;
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            Map<String, String> responseHeaders = response.headers;
            String rawCookies = responseHeaders.get("Set-Cookie");
            rawCookies=rawCookies==null?"":rawCookies;
            myResponse.onGetCookie(rawCookies);
            String dataString = new String(response.data, "UTF-8");
            return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String,String> localHashMap =new HashMap<>();
        localHashMap.put("User-Agent", "Mozilla/4.0 (compatibl; MSIE 5.5; Windows NT)");
        String cookie=myResponse.onSetCookie();
        if(cookie==null)cookie="";
        localHashMap.put("Cookie",cookie);
        return localHashMap;
    }
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String,String> map=myResponse.getParams();
        if(map==null)map=new HashMap<>();
        return map;
    }
}
