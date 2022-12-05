package com.mkyong;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mkyong.BdResource.*;

@Path("/user")
public class UserResource {

    @Path("/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response create(String json) {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = null;
        try {
            map = mapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        String username = map.get("username");
        String pass = map.get("password");
        String fullname = map.get("fullname");

        OpenDB();

        PreparedStatement stm = null;
        try {
            stm = connection.prepareStatement("""
                        insert into user (username,password,fullname) values (?,?,?)
                        """);
            stm.setString(1, username); // para ?
            stm.setString(2, pass); // para ?
            stm.setString(3, fullname); // para ?

            int rs = stm.executeUpdate();

            CloseDB();
            if(rs > 0) return Response.status(200).entity("Conta criada").build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return Response.status(401).entity("Erro na criação da conta").build();
    }

    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(String json) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = null;
        try {
            map = mapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        String username = map.get("username");
        String pass = map.get("password");

        OpenDB();

        PreparedStatement stm = null;
        try {
            stm = connection.prepareStatement("""
                        select id from user where (username = ? AND password = ?)
                        """);

            stm.setString(1, username); // para ?
            stm.setString(2, pass); // para ?

            ResultSet rs = stm.executeQuery();

            if(!rs.next()) {
                CloseDB();
                return Response.status(401).entity("Password ou username errado").build();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        try {
            CloseDB();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return Response.status(200).entity("Login feito").build();
    }

    @Path("/delete")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response remove(String json) {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = null;
        try {
            map = mapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        String username = map.get("username");
        String pass = map.get("password");

        System.out.println(username);
        System.out.println(pass);

        OpenDB();

        PreparedStatement stm2 = null;
        PreparedStatement stm = null;
        PreparedStatement stm3 = null;

        try {
            stm3 = connection.prepareStatement("""
                       select id,name from movies where uploadedBy = ?
                        """);

            stm3.setString(1, username); // para ?
            ResultSet rs1 = stm3.executeQuery();
            List<Movie> movies = new ArrayList<Movie>();

            while(rs1.next()) {
                Movie m = new Movie();
                m.setId(rs1.getInt(1));
                m.setName(rs1.getString(2));
                movies.add(m);
            }

            deleteFiles(movies);

            stm2 = connection.prepareStatement("""
                       delete from movies where uploadedBy = ?
                        """);

            stm2.setString(1, username); // para ?
            stm2.executeUpdate();
            //System.out.println(rs2);

            stm = connection.prepareStatement("""
                       delete from user where username = ?
                        """);

            stm.setString(1, username); // para ?

            int rs = stm.executeUpdate();

            if(rs>0) {
                CloseDB();
                return Response.status(200).entity("Conta apagada").build();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return Response.status(401).entity("Password ou username errados").build();
    }

    private void deleteFiles(List<Movie> movies) throws SQLException {
        for(Movie i : movies) {
            PreparedStatement stm = null;

            File f = new File("/home/luna/3ºano/1ºsemestre/pdm/jax-rs/trabalho/jersey-jetty/src/resources/videos/teste.txt");
            f.delete();

            stm = connection.prepareStatement("""
                       delete from movies where id = ?
                        """);

            stm.setInt(1, i.id); // para ?
            stm.executeUpdate();
        }
    }
}
