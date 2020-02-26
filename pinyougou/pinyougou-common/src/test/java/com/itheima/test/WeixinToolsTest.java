package com.itheima.test;

import com.github.wxpay.sdk.WXPayUtil;

import java.util.HashMap;
import java.util.Map;

public class WeixinToolsTest {
    public static void main(String[] args) throws Exception {

        //生成随机字符串
        String s = WXPayUtil.generateNonceStr();
        System.out.println(s);
        //62fa2ed695dd4e43850808a2293011a7

        //Map->XML
        Map<String,String> dataMap=new HashMap<>();
        dataMap.put("name","red");
        dataMap.put("age","17");

        String xml = WXPayUtil.mapToXml(dataMap);
        System.out.println(xml);
        /*<?xml version="1.0" encoding="UTF-8" standalone="no"?>
         <xml>
         <name>red</name>
         <age>17</age>
         </xml>
        */


        //XML->Map
        Map<String, String> stringStringMap = WXPayUtil.xmlToMap(xml);
        System.out.println(stringStringMap);
        //{name=red, age=17}


        //Map->XML 带签名
        Map<String,String> dataMap2=new HashMap<>();
        dataMap2.put("name","red");
        dataMap2.put("age","17");

        String xml2 = WXPayUtil.generateSignedXml(dataMap2,"1212");
        System.out.println(xml2);
        /*
        <?xml version="1.0" encoding="UTF-8" standalone="no"?>
        <xml>
        <name>red</name>
        <sign>F2579E2E1CA854A511D81C7948236CA4</sign>
        <age>17</age>
        </xml>*/


    }
}
