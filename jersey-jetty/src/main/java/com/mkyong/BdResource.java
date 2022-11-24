package com.mkyong;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Path("/bd")
public class BdResource {
    protected static Connection connection;
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String bd() throws SQLException {
        OpenDB();
        String a = getUserPass("luna");
        CloseDB();
        return a;
    }

    public static void OpenDB() {
        System.out.println("Opening Database");
        String user = "root";
        String password = "mypass";
        String url = "jdbc:mysql://localhost:3306/netflixpp?serverTimezone=UTC"; // mudar nome bd
        String driver = "com.mysql.cj.jdbc.Driver";
        try {
            Class.forName(driver); // Directly import Mysql Driver in order to access the dataBase
        } catch (Exception ex) {
            System.out.println("Error  " +  ex);
        }
        try { // Establish Connection to the dataBase
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
        //if(true) throw new RuntimeException("Simulated Error!");
        try {
            System.out.println("Connection valid: " + connection.isValid(5));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void CloseDB() throws SQLException{
        System.out.println("Closing Database");
        connection.close();
        System.out.println("Connections is valid: " + connection.isValid(5));
    }

    public static String getUserPass(String user){
        try{
            PreparedStatement stm = connection.prepareStatement("""
                    select password from user where username = ?
                    """);
            stm.setString(1, user); // para ?
            ResultSet rs = stm.executeQuery();
            if(!rs.next()) return null;
            else return rs.getString(1);
        } catch (SQLException e) {
            System.out.println("Exception thrown: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}