package com.mvp.plugin.dependent.tools;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionTool {

    public static String filterException(Exception e) {
        StringBuffer sb = new StringBuffer();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            e.printStackTrace(pw);
        } finally {
            pw.close();
        }
        String[] exceptions = sw.toString().split("Caused by: ");
        for (int i = 0; i < exceptions.length; i++) {
            if (!exceptions[i].startsWith("java.lang.RuntimeException: java.lang.reflect.InvocationTargetException") &&
                    !exceptions[i].startsWith("java.lang.reflect.InvocationTargetException")) {
                sb.append(exceptions[i]);
            }
        }
        return sb.toString();
    }
}
