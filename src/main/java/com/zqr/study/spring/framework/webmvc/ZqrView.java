package com.zqr.study.spring.framework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Auther: qingruizhu
 * @Date: 2019-04-26 15:48
 */
public class ZqrView {

    public final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private File viewFile;

    public ZqrView(File viewFile) {
        this.viewFile = viewFile;
    }

    public String getDEFAULT_CONTENT_TYPE() {
        return DEFAULT_CONTENT_TYPE;
    }

    public void render(Map<String,?> model, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        StringBuffer sb = new StringBuffer();
        RandomAccessFile randomFile = new RandomAccessFile(this.viewFile, "r");
        try {
            String line = null;
            while (null != (line = randomFile.readLine())) {
                line = new String(line.getBytes("ISO-8859-1"), "utf-8");
                Pattern pattern = Pattern.compile("¥\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String paramName = matcher.group();
                    paramName = paramName.replaceAll("¥\\{|\\}", "");
                    Object paramValue = model.get(paramName);
                    if (null == paramValue) continue;
                    //要把¥{}中间的这个字符串给取出来
                    line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                    matcher = pattern.matcher(line);
                }
                sb.append(line);
            }

        } finally {
            randomFile.close();
        }
    }
    //处理特殊字符
    public static String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\")
                .replace("*", "\\*")
                .replace("+", "\\+")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("^", "\\^")
                .replace("$", "\\$")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("?", "\\?")
                .replace(",", "\\,")
                .replace(".", "\\.")
                .replace("&", "\\&");
    }
}
