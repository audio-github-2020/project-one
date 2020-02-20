package com.itheima;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class freemarker {
    public static void main(String[] args) throws Exception {
        //准备数据模型  可以是任何Java数据模型，也可以是JavaBean，推荐使用Map
        Map<String, Object> datamap = new HashMap<String, Object>();
        datamap.put("username", "小包");
        datamap.put("now", "2020-2-19 22：52");

        //配置freemarker
        //创建Configuration
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        //设置模板路径
        configuration.setDirectoryForTemplateLoading
            (new File("D:/TrainingforJava/java0204/project-one/pinyougou/freemarker-demo/src/main/resources"));
        //设置编码
        configuration.setDefaultEncoding("UTF-8");
        //创建模板对象
        Template template = configuration.getTemplate("test.ftl");
        //指定输出文件
        Writer writer = new FileWriter("D:/1.html");
        //合成输出
        template.process(datamap, writer);
        //关闭资源
        writer.flush();
        writer.close();
    }
}
