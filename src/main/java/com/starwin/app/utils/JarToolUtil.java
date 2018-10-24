package com.starwin.app.utils;

import java.io.File;

public class JarToolUtil {

    public static String getJarFilePath() {
        File file = getFile();
        return file == null ? null : file.getAbsolutePath();
    }

    public static String getJarDir() {
        File file = getFile();
        return file == null ? null : getFile().getParent();
    }

    private static File getFile() {
        String path = JarToolUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            path = java.net.URLDecoder.decode(path, "UTF-8"); // 转换处理中文及空格
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return new File(path);
    }
}
