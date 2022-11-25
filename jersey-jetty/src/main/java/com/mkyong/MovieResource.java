package com.mkyong;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mkyong.BdResource.*;

@Path("/movie")
public class MovieResource {

    @Path("/search/{movieName}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Movie> search(@PathParam("movieName") String name) {

        OpenDB();

        PreparedStatement stm = null;
        try {
            stm = connection.prepareStatement("""
                       select id,name from movies 
                       where name like ?
                       order by id, name
                        """);

            stm.setString(1, "%" + name + "%"); // % -> qualuqer coisa antes ou depois do nome
            ResultSet rs = stm.executeQuery();
            List<Movie> movies = new ArrayList<Movie>();

            while(rs.next()) {
                Movie m = new Movie();
                m.setId(rs.getInt(1));
                m.setName(rs.getString(2));
                movies.add(m);
            }

            if(movies.isEmpty()) System.out.printf("nenhum filme encontrado");

            return movies;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
