package com.itheima.test;

import com.pinyougou.util.HttpClient;

public class HttpClientTest {
    public static void main(String[] args) throws Exception {
        HttpClient httpClient=new HttpClient("https://www.baidu.com");
        httpClient.setHttps(true);
        httpClient.setXmlParam("");
        httpClient.post();
        String content = httpClient.getContent();
        System.out.println(content);
    }
}
