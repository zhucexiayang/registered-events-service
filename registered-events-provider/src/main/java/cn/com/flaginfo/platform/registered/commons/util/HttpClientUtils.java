package cn.com.flaginfo.platform.registered.commons.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpClientUtils {
    private  static CloseableHttpClient client;
    private static RequestConfig requestConfig;
    public static String doPostWithNoParamsName(String content,String url) {
        String result = null;
        try {
            client = HttpClients.createDefault();
            requestConfig = RequestConfig.custom().setConnectTimeout(30* 1000).setSocketTimeout(30 * 1000).build();
            HttpResponse response = null;
            HttpPost postMethod = new HttpPost(url);
            StringEntity se = new StringEntity(content, "utf-8");
            se.setContentEncoding("utf-8");
            se.setContentType("application/json");
            postMethod.setEntity(se);
            postMethod.setConfig(requestConfig);
            // HttpClient client = new DefaultHttpClient();
            response = client.execute(postMethod);
            result = EntityUtils.toString(response.getEntity(), "utf-8");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
             client.getConnectionManager().shutdown();
        }
        return result;
    }

}
