package models;

import org.sql2o.Connection;
import org.sql2o.Sql2oException;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
public class Sighting {
    private int id;
    private String location;
    private String rangerName;
    private int animalId;
    private Timestamp createdAt;

    public Sighting( String location, String rangerName, int animalId) {
        this.location = location;
        this.rangerName = rangerName;
        this.animalId = animalId;
    }

    public int getId() {

        return id;
    }

    public String getLocation() {

        return location;
    }

    public String getRangerName() {
        return rangerName; }

    public int getAnimalId() {

        return animalId;
    }

    public Timestamp getCreatedAt()
    {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sighting sighting = (Sighting) o;
        return id == sighting.id &&
                animalId == sighting.animalId &&
                Objects.equals(location, sighting.location) &&
                Objects.equals(rangerName, sighting.rangerName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, location, rangerName, animalId);
    }

    public void save() {
        if((this.location == null  || this.location.trim().isEmpty()) || (this.rangerName == null || this.rangerName.trim().isEmpty())){
            throw new NullPointerException("location and ranger name cannot be null or empty");
        }
        try(Connection con = DB.sql2o.open()) {
            String sql = "INSERT INTO sightings (location, rangerName, animalId, createdAt) VALUES (:location, :rangerName, :animalId, now());";
            this.id = (int) con.createQuery(sql, true)
                    .addParameter("location", this.location)
                    .addParameter("rangerName", this.rangerName)
                    .addParameter("animalId", this.animalId)
                    .executeUpdate()
                    .getKey();
        }catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    public static List<Sighting> all() {
        String sql = "SELECT * FROM sightings;";
        try(Connection con = DB.sql2o.open()) {
            return con.createQuery(sql)
                    .executeAndFetch(Sighting.class);
        }
    }

    public static void update(int id, String location, String rangerName, int animalId) {
        if((location == null || location.trim().isEmpty()) || (rangerName == null || rangerName.trim().isEmpty()) ){
            throw new NullPointerException("location and ranger name cannot be null or empty");
        }
        String sql = "UPDATE sightings SET (location, rangerName, animalId) = (:location, :rangerName, :animalId) WHERE id=:id;";
        try(Connection con = DB.sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("location",location)
                    .addParameter("rangerName", rangerName)
                    .addParameter("animalId", animalId)
                    .addParameter("id", id)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    public static Sighting findById(int id) {
        String sql = "SELECT * FROM sightings WHERE id = :id;";
        try(Connection con = DB.sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Sighting.class);
        }
    }

    public static void clearAll() {
        String sql = "DELETE FROM sightings;";
        try(Connection con = DB.sql2o.open()){
            con.createQuery(sql)
                    .executeUpdate();
        }catch (Sql2oException ex){
            System.out.println(ex);
        }
    }

    public static void deleteById(int id) {
        String sql = "DELETE FROM sightings WHERE id = :id;";
        try(Connection con = DB.sql2o.open()){
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }catch (Sql2oException ex){
            System.out.println(ex);
        }
    }
}
