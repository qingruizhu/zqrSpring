package com.zqr.study.spring.framework.webmvc;

import java.util.Map;

/**
 * @Description:
 * @Auther: qingruizhu
 * @Date: 2019-04-26 11:16
 */
public class ZqrModelAndView {

    private String viewName;
    private Map<String,?> model;

    public ZqrModelAndView(String viewName) {
        this(viewName, null);
    }

    public ZqrModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
