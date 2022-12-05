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
public class MovieResource extends Thread{

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

        PreparedStatement stm;
        PreparedStatement stm2 = null;
        try {
            stm = connection.prepareStatement("""
                       select name from movies where id = ?
                        """);
            stm.setInt(1,id);
            ResultSet rs1 = stm.executeQuery();

            if(!rs1.next()) {
                CloseDB();
                return Response.status(401).entity("filme não encontrado").build();
            }

            String name = rs1.getString(1) + "_" + id;

            stm2 = connection.prepareStatement("""
                       delete from movies where id = ?
                        """);

            stm2.setInt(1, id); // % -> qualquer coisa antes ou depois do nome
            int rs = stm2.executeUpdate();

            if(rs > 0) {
                CloseDB();

                script(1, name);
               // script(2, name);

                return Response.status(200).entity("Filme apagado").build();
            }

            CloseDB();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
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
                            @FormDataParam("movieFile") InputStream file,
                            @FormDataParam("poster") InputStream poster,
                            @FormDataParam("year") int year,
                            @FormDataParam("director") String direc,
                            @FormDataParam("totaltime") int t,
                            @FormDataParam("genre") String g){

        //System.out.println("/home/luna/3ºano/1ºsemestre/pdm/jax-rs/trabalho/jersey-jetty/src/resources/videos/high/"+ moviename );

        OpenDB();

        PreparedStatement stm1 = null;
        PreparedStatement stm = null;

        try {

            stm1 = connection.prepareStatement("""
                    SELECT AUTO_INCREMENT from information_schema.TABLES
                    where TABLE_SCHEMA = "netflixpp" and TABLE_NAME= "movies";
                        """);

            ResultSet rs = stm1.executeQuery();

            if(!rs.next()) {
                CloseDB();
                return Response.status(401).entity("erro no upload").build();
            }

            //int i = rs.getInt(1);
            //System.out.println(rs.getInt(1) );
            //String name = moviename + "_" + rs.getInt(1) ;
            String pMovie = "/home/luna/3ºano/1ºsemestre/pdm/jax-rs/trabalho/jersey-jetty/src/resources/videos/high/"+ moviename + "_" + rs.getInt(1)  + ".txt";
            String pPoster = "/home/luna/3ºano/1ºsemestre/pdm/jax-rs/trabalho/jersey-jetty/src/resources/imgs/" + moviename + "-poster"+ "_" + rs.getInt(1) + ".txt";

            stm = connection.prepareStatement("""
                    insert into movies 
                    (name,path,poster,year,director,totaltime,genre,uploadedBy) 
                    values (?,?,?,?,?,?,?,?)
                        """);

            stm.setString(1, moviename); // para ?
            stm.setString(2, pMovie); // para ?
            stm.setString(3, pPoster); // para ?
            stm.setInt(4, year); // para ?
            stm.setString(5, direc); // para ?
            stm.setInt(6, t); // para ?
            stm.setString(7, g); // para ?
            stm.setString(8, username); // para ?

            int rs2 = stm.executeUpdate();

            if(rs2>0) {
                CloseDB();

                saveFile(file, pMovie);
                script(0, pMovie);

                saveFile(poster, pPoster);
                script(0, pPoster);


                //File f = new File("/home/luna/3ºano/1ºsemestre/pdm/jax-rs/trabalho/jersey-jetty/src/resources/videos/teste.txt");
                //if(f.exists()) System.out.println("ficheiro criado");

                return Response.status(200).entity("Upload feito").build();
            }

            CloseDB();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Response.status(401).entity("Filme não foi adicionado").build();
    }

    private void saveFile(InputStream is, String fileLocation) throws FileNotFoundException {  // Function to save the file into the hard disk
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

    private void script(int n, String movie) throws IOException {
        //System.out.println(movie);
        String[] shell;
        if (n==0) {
            shell = new String[]{"sh", "/home/luna/3ºano/1ºsemestre/pdm/jax-rs/trabalho/jersey-jetty/src/resources/scripts/uploadMovieBucket.sh", movie};
        } else if(n == 1){
           shell = new String[]{"sh", "/home/luna/3ºano/1ºsemestre/pdm/jax-rs/trabalho/jersey-jetty/src/resources/scripts/deleteMovieBucket.sh", movie};
        } else {
            shell = new String[]{"sh", "/home/luna/3ºano/1ºsemestre/pdm/jax-rs/trabalho/jersey-jetty/src/resources/scripts/deleteMovieBucket2.sh", movie};
        }
        Process proc = Runtime.getRuntime().exec(shell);
        BufferedReader read = new BufferedReader(new InputStreamReader(
                proc.getInputStream()));
        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
