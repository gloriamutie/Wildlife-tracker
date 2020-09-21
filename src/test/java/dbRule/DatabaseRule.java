package dbRule;

import org.junit.rules.ExternalResource;
import models.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static models.Environment.PASS_WORD;
import static models.Environment.USER_NAME;

public class DatabaseRule extends ExternalResource {
    @Override
    protected void before(){
//        postgresql-convex-73880
        DB.sql2o=new Sql2o("jdbc:postgresql://localhost:5432/wildlife_tracker", USER_NAME, PASS_WORD);
       //DB.sql2o = new Sql2o( "jdbc:postgresql://ec2-107-22-7-9.compute-1.amazonaws.com:5432/d1kbjemjrcsd8c", "srnevkhjceqgaq", "c9ed9db83d24c7f5398310d281032fe0c14f3f4e4886a2f950111f3679b8c3cc");
    }

    @Override
    protected  void after () {
        try (Connection con = DB.sql2o.open()){
            String deleteAnimalsQuery = "DELETE FROM animals *;";
            String deleteSightingsQuery = "DELETE FROM sightings *;";
            con.createQuery(deleteAnimalsQuery).executeUpdate();
            con.createQuery(deleteSightingsQuery).executeUpdate();
        }
    }
}
