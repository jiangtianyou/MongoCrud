package com.bruce.intellijplugin.generatesetter.utils.freemarker;

import com.intellij.ide.ui.EditorOptionsTopHitProvider;
import com.intellij.openapi.module.ResourceFileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * freemarker tool
 *
 * @author xuxueli 2018-05-02 19:56:00
 */
public class FreemarkerTool {

    public String processTemplateIntoString(Template template, Object model)
            throws IOException, TemplateException {
        StringWriter result = new StringWriter();
        template.process(model, result);
        return result.toString();
    }

    public String processString(String templateName, Object params){
        Configuration config = getConfig();
        Template template = null;
        try {
            template = config.getTemplate(templateName);
            String text = processTemplateIntoString(template, params);
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private  Configuration getConfig(){
        Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        try {
            cfg.setClassForTemplateLoading(this.getClass(),"/template/mongoVersion");
        } catch (Exception e) {
            e.printStackTrace();
        }
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return cfg;
    }

    // 测试FreeMarker的使用
    public static void main(String[] args) throws IOException, TemplateException {
        FreemarkerTool tool = new FreemarkerTool();
        Map<String, Object> map = new HashMap<>();
        map.put("packageName", "Test");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("className", "Test2");
        map.put("classInfo", map2);
        String result = tool.processString("dao.ftl", map);

//        FileUtils.write(new File(""));
        System.out.println(result);
    }



}
