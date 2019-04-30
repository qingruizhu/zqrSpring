package com.zqr.study.spring.framework.beans.support;

import com.zqr.study.spring.framework.beans.config.ZqrBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @Description:
 * @Auther: qingruizhu
 * @Date: 2019-04-18 16:32
 */
public class ZqrBeanDefinitionReader {

    //存储注册的bean的class路径的容器
    private ArrayList<String> registyBeanClasses = new ArrayList<String>();
    //配置文件信息
    private Properties config =  new Properties();
    //固定配置文件中的key，相当于xml的规范
    private final String SCAN_PACKAGE = "scanPackage";

    public ZqrBeanDefinitionReader(String... locations) {
        //通过url定位到配置文件，然后转化为文件流
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
        //is转为config
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //扫描配置的路径下的Class文件
        doScanner(config.getProperty(SCAN_PACKAGE));

    }
    /**
     * @Description: 扫描配置路径下的文件，并把class的名称放入到registyBeanClasses中
     * @Author qingruizhu
     * @Date 17:39 2019-04-18
     **/
    private void doScanner(String scanPackage) {
        //根据项目的绝对路径获取
        String scanPath = scanPackage.replaceAll("\\.", "/");
        scanPath= "/" + scanPath;
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource(scanPath);
        URL url = this.getClass().getClassLoader().getResource("/"+scanPackage.replaceAll("\\.","/"));
        File classPath = new File(url.getFile());
        File[] files = classPath.listFiles();
        for (File file:files) {
            if (file.isDirectory()){
                doScanner(scanPackage+"."+file.getName());
            }else{
                if (file.getName().endsWith(".class")){
                    String className = scanPackage+"."+file.getName().replaceAll(".class","");
                    registyBeanClasses.add(className);
                }
            }

        }
    }

    //把配置文件中扫描到的所有的配置信息转化为ZqrBeanDefinition对象，方便之后IOC操作
    public List<ZqrBeanDefinition> loadBeanDefinitions(){
        ArrayList<ZqrBeanDefinition> definitions = new ArrayList<ZqrBeanDefinition>();
        try {
            for (String className: registyBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                if (beanClass.isInterface()) continue;
                ZqrBeanDefinition beandefinition = doCreateBeanDefinition(beanClass.getName(), toLowerFirstCase(beanClass.getSimpleName()));
                definitions.add(beandefinition);
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> inter: interfaces) {
                    definitions.add(doCreateBeanDefinition(beanClass.getName(),inter.getName()));
                }
            }
        }catch (Exception e){
            e.fillInStackTrace();
        }
        return definitions;
    }

    private ZqrBeanDefinition doCreateBeanDefinition(String beanClassName, String factoryBeanName) {
        ZqrBeanDefinition definition = new ZqrBeanDefinition();
        ZqrBeanDefinition zqrBeanDefinition = new ZqrBeanDefinition();
        zqrBeanDefinition.setBeanClassName(beanClassName);
        zqrBeanDefinition.setFactoryBeanName(factoryBeanName);
        return  zqrBeanDefinition;
    }

    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }


    public Properties getConfig() {
        return config;
    }
}
