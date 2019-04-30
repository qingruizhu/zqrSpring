package com.zqr.study.spring.framework.webmvc;

import java.io.File;
import java.util.Locale;

/**
 * @Description:
 *          设计这个类的主要目的是:
 *          1.讲一个静态文件变为一个动态文件
 *          2.根据用户传送参数不同，产生不同的结果
 *          3.最终输出字符串，交给 Response 输出
 * @Auther: qingruizhu
 * @Date: 2019-04-22 10:13
 *
 */
public class ZqrViewResolver {
    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    private File templateRootDir;
    private String viewName;

    public ZqrViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir = new File(templateRootPath);
    }

    public ZqrView resolveView(String viewName, Locale locale){
        this.viewName = viewName;
        if (null == viewName || "".equals(viewName)) {
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : viewName + DEFAULT_TEMPLATE_SUFFIX;
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", ""));
        return new ZqrView(templateFile);
    }


}
