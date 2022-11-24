package com.mkyong;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

import static com.mkyong.BdResource.*;

@Path("/user")
public class UserResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response create(String json) throws JsonProcessingException, SQLException {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(json, Map.class);

        String username = map.get("username");
        String pass = map.get("pass");
        String fullname = map.get("fullname");

        OpenDB();

        PreparedStatement stm = connection.prepareStatement("""
                    insert into user (username,password,fullname) values (?,?,?)
                    """);
        stm.setString(1, username); // para ?
        stm.setString(2, pass); // para ?
        stm.setString(3, fullname); // para ?
        int rs = stm.executeUpdate();

        try {
            CloseDB();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        if(rs > 0) return Response.status(200).entity("Conta criada").build();
        return Response.status(401).entity("Erro na criação da conta").build();
    }

   /* @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(String json) throws JsonProcessingException, SQLException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(json, Map.class);

        String username = map.get("username");
        String pass = map.get("pass");

        OpenDB();

        PreparedStatement stm = connection.prepareStatement("""
                    select password from user where username = ?
                    """);

        stm.setString(1, username); // para ?
        ResultSet rs = stm.executeQuery();

        CloseDB();

        if(!rs.next()) return Response.status(401).entity("Password ou username errados").build();

        if(Objects.equals(pass, rs.getString(1)))
            return Response.status(200).entity("Login feito").build();
        return Response.status(401).entity("Password ou username errados").build();
    } */
}
