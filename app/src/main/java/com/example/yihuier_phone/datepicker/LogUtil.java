package com.example.yihuier_phone.datepicker;

import android.util.Log;

/**
 * 打印log的工具类
 *
 * @author andrliu
 */
public class LogUtil {

    private static String sTAG = "kl==";
    // 日志总开关
    private static Boolean sLogOpen = true;

    public static String getTAG() {
        return sTAG;
    }

    public static void setTAG(String tag) {
        sTAG = tag;
    }

    /**
     * 日志是否打开
     *
     * @return
     */
    public static boolean isLogOpen() {
        return sLogOpen;
    }

    public static void setLogOpen(boolean open) {
        sLogOpen = open;
    }

    /**
     * log级别的枚举
     */
    public enum LOG_ENUM {
        INFO, WARN, ERROR
    }

    public static void log(String msg) {
        if (sLogOpen) {
            Log.i(sTAG, msg);
        }
    }

    /**
     * 根据log级别打印log信息
     *
     * @param level log级别
     * @param msg   log信息
     */
    public static void log(LOG_ENUM level, String msg) {
        if (sLogOpen) {
            switch (level) {
                case INFO:
                    Log.i(sTAG, msg);
                    break;
                case WARN:
                    Log.w(sTAG, msg);
                    break;
                case ERROR:
                    Log.e(sTAG, msg);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 记录消息
     *
     * @param msg
     */
    public static void i(String msg) {
        if (sLogOpen) {
            Log.i(sTAG, msg);
        }
    }

    /**
     * 记录消息
     *
     * @param msg
     */
    public static void i(String tag, String msg) {
        if (sLogOpen) {
            Log.i(tag, msg);
        }
    }

    /**
     * 记录Debug
     *
     * @param msg
     */
    public static void d(String msg) {
        if (sLogOpen) {
            Log.d(sTAG, msg);
        }
    }

    /**
     * 记录警告
     *
     * @param msg
     */
    public static void w(String msg) {
        if (sLogOpen) {
            Log.w(sTAG, msg);
        }
    }

    /**
     * 记录错误
     *
     * @param msg
     */
    public static void e(String msg) {
        if (sLogOpen) {
            Log.e(sTAG, msg);
        }
    }

    /**
     * 记录错误
     *
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (sLogOpen) {
            Log.e(tag, msg);
        }
    }
}
