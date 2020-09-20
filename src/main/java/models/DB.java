package models;

import org.sql2o.*;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static models.Environment.PASS_WORD;
import static models.Environment.USER_NAME;


public class DB {
    private static URI dbUri;
    public static Sql2o sql2o;
//    postgresql-shaped-10606
//\


    static {

        try {
            if (System.getenv("DATABASE_URL") == null) {
                dbUri = new URI("postgres://localhost:5432/wildlife_tracker");
            } else {
                dbUri = new URI(System.getenv("DATABASE_URL"));
            }

            int port = dbUri.getPort();
            String host = dbUri.getHost();
            String path = dbUri.getPath();
            String username = (dbUri.getUserInfo() == null) ? USER_NAME : dbUri.getUserInfo().split(":")[0];
            String password = (dbUri.getUserInfo() == null) ? PASS_WORD: dbUri.getUserInfo().split(":")[1];

            sql2o = new Sql2o("jdbc:postgresql://" + host + ":" + port + path, username, password);
        } catch (URISyntaxException e ) {

        }
    }
}