package com.yaxionh.common;

import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.SSLException;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

public class HttpHelper {

    private static HttpRequestRetryHandler retryHandler() {
        return (exception, executionCount, context) -> {

            System.out.println("try request: " + executionCount);

            if (executionCount >= 5) {
                // Do not retry if over max retry count
                return false;
            }
            if (exception instanceof InterruptedIOException) {
                // Timeout
                return false;
            }
            if (exception instanceof UnknownHostException) {
                // Unknown host
                return false;
            }
            if (exception instanceof SSLException) {
                // SSL handshake exception
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            if (idempotent) {
                // Retry if the request is considered idempotent
                return true;
            }
            return false;
        };
    }

    public static String getHtmlFromDQDUrl(String strUrl) {
        CloseableHttpClient client = HttpClients.custom().setRetryHandler(retryHandler()).build();
        // Execute the method.
        int statusCode = 0;
        String res = "";
        try {
            HttpGet request = new HttpGet(strUrl);
            request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            request.setHeader("Accept-Encoding", "gzip, deflate, br");
            request.setHeader("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7");
            request.setHeader("Cache-Control", "max-age=0");
            request.setHeader("Connection", "keep-alive");
            //request.setHeader("Cookie", "dqduid=ChOLeF0yPROCtC9mDJveAg==; __51cke__=; Hm_lvt_662abe3e1ab2558f09503989c9076934=1563573526,1563611754; laravel_session=eyJpdiI6ImpJdzJuanFCRDlxT25OY2NnQ2ZUa1QwQnptek10Y3pZajQ2bVFQTW5NY2M9IiwidmFsdWUiOiJXa1c5UWpQS25RYmFlY0wxemFiQ2gwTGNwb2NKVW5JdHdPbGJUaEhpeHhyRTdYR2Y0RWw5YnRcL1pNXC9zXC9JWktjUWxJT2pJYmdueUxNYmtNWTQxb0Z4dz09IiwibWFjIjoiNDZhZWYxOTM3M2JhNGUzYzFiMDg0ZmNlOWY2NDhhYjRmOGQ4NGYyMWI5Y2ZmZWVlYjYzZTc1ZGY3YzVkYjBlNCJ9; __tins__17824121=%7B%22sid%22%3A%201563611753004%2C%20%22vd%22%3A%203%2C%20%22expires%22%3A%201563613894411%7D; __51laig__=26; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%2216c0c3e9e6f901-0d810d703ff767-37647e07-1296000-16c0c3e9e70b46%22%2C%22%24device_id%22%3A%2216c0c3e9e6f901-0d810d703ff767-37647e07-1296000-16c0c3e9e70b46%22%2C%22props%22%3A%7B%22%24latest_referrer%22%3A%22%22%2C%22%24latest_referrer_host%22%3A%22%22%7D%7D; Hm_lpvt_662abe3e1ab2558f09503989c9076934=1563612095");
            request.setHeader("Host", "www.dongqiudi.com");
            request.setHeader("Referer", "https://www.dongqiudi.com/");
            request.setHeader("Upgrade-Insecure-Requests", "1");
            request.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");


            System.out.println("Executing request " + request.getRequestLine());
            HttpResponse response = client.execute(request);

            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println("----------------------------------------");
            res = result.toString();
            System.out.println(res);
            System.out.println("----------------------------------------");
            System.out.println("Request finished");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //client.close();
        }
        return res;
    }

    public static void download(String urlString, String filename, String savePath) throws Exception {
        // 构造URL
        URL url = new URL(urlString);
        // 打开连接
        URLConnection con = url.openConnection();
        //设置请求超时为5s
        con.setConnectTimeout(5*1000);
        // 输入流
        InputStream is = con.getInputStream();

        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;
        // 输出的文件流
        File sf=new File(savePath);
        if (!sf.exists()) {
            sf.mkdirs();
        }
        OutputStream os = new FileOutputStream(sf.getPath()+"/"+filename);
        // 开始读取
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        // 完毕，关闭所有链接
        os.close();
        is.close();
    }
}
