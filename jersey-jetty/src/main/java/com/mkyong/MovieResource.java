package com.mkyong;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.mkyong.BdResource.*;

@Path("/movie")
public class MovieResource {

    @Path("/search/{movieName}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Movie> search(@PathParam("movieName") String name) {  //argumento vem do path

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

    @Path("/delete/{movieId}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response delete(@PathParam("movieId") int id) {

        OpenDB();

        PreparedStatement stm2 = null;
        try {
            stm2 = connection.prepareStatement("""
                       delete from movies where id = ?
                        """);

            stm2.setInt(1, id); // % -> qualuqer coisa antes ou depois do nome
            int rs = stm2.executeUpdate();

            if(rs > 0) return Response.status(200).entity("Filme apagado").build();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return Response.status(401).entity("Filme não encontrado").build();
    }

    @Path("/upload")
    @POST
   // @Produces(MediaType.)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload (@FormDataParam("moviename") String moviename,
                            @FormDataParam("username") String username,
                            @FormDataParam("movieFile") InputStream file){

        OpenDB();

        PreparedStatement stm = null;
        try {
            stm = connection.prepareStatement("""
                    insert into movies (name,uploadedBy) values (?,?)
                        """);

            stm.setString(1, moviename); // para ?
            stm.setString(2, username); // para ?
            int rs2 = stm.executeUpdate();

            if(rs2>0) {
                CloseDB();
                return Response.status(200).entity("Upload feito").build();

            }

            CloseDB();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return Response.status(401).entity("Filme não foi adicionado").build();
    }

    private void saveFile(InputStream is, String fileLocation) throws FileNotFoundException {                     // Function to save the file into the hard disk
        //LOGGER.info("Physically storing the video on {}", fileLocation);
        try {
            OutputStream os = new FileOutputStream(new File(fileLocation));
            byte[] buffer = new byte[256];
            int bytes = 0;
            while ((bytes = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytes);
            }
            //LOGGER.info("Done");
            os.close();
            is.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
