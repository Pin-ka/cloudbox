package com.pinKa.cloud.server;

import java.sql.*;

public class AuthService {
    private static Connection connection;
    private static Statement stmt;

    public static void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection= DriverManager.getConnection("jdbc:sqlite:bd.db");
            stmt=connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getNickByLoginAndPass(String login,String pass){
        String sql=String.format("SELECT nick FROM logins\n"+
                "WHERE login='%s'\n"+
                "AND password='%s'",login,pass);
        ResultSet rs=null;
        try {
            rs=stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  static boolean addUser(String login,String pass,String nick){
        String verify=String.format("SELECT login FROM logins\n"+
                "WHERE nick='%s'",nick);
        ResultSet rs=null;
        try {
            rs=stmt.executeQuery(verify);
            if (!rs.next()) {
                String sql=String.format("INSERT INTO logins(login,password,nick)"+
                        "VALUES ('%s','%s','%s')",login,pass,nick);
                stmt.execute(sql);
                return true;
            }else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
