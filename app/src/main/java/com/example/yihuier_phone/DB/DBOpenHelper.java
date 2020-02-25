package com.example.yihuier_phone.DB;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static android.content.ContentValues.TAG;

public class DBOpenHelper {

    /**
     * 连接数据库
     */




    public static Connection getConn() {
        Connection conn = null;
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 反复尝试连接，直到连接成功后退出循环
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(10000);  // 每隔0.1秒尝试连接
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.toString());
                    }

                    // 2.设置好IP/端口/数据库名/用户名/密码等必要的连接信息
                    String ip = "47.96.104.134";
                    int port = 3306;
                    String dbName = "mysql";
                    String url = "jdbc:mysql://" + ip + ":" + port
                            + "/" + dbName; // 构建连接mysql的字符串
                    String user = "root";
                    String password = "Sayello.123";

                    // 3.连接JDBC
                    // 3.连接JDBC
                    Connection conn = null;
                    try {
                        conn = DriverManager.getConnection(url, user, password);
                        Log.i(TAG, "远程连接成功!");
                    } catch (SQLException e) {
                        Log.e(TAG, "远程连接失败!");
                    }


                }
            }
        });
        thread.start();
        return conn;
    }
       /* String ip = "47.96.104.134";
        int port = 3306;
        String dbName = "bz_meeting";
        String url = "jdbc:mysql://" + ip + ":" + port + "/" + dbName; // 构建连接mysql的字符串
        String user = "root";
        String password = "Sayello.123";
        try {

            conn =  DriverManager.getConnection(url, user, password);//获取连接
            Log.d("TestDBConn", "远程连接成功！");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;*/


        /**
         * 关闭数据库
         * */

        public static void closeAll (Connection conn, PreparedStatement ps){
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

        /**
         * 关闭数据库
         * */

        public static void closeAll (Connection conn, PreparedStatement ps, ResultSet rs){
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }
