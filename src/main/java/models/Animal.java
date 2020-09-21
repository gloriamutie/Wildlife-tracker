package models;

import org.sql2o.Connection;
import org.sql2o.Sql2oException;

import java.util.List;
import java.util.Objects;

public class Animal {
    public int id;
    public String name;
    public String type;

//<....................this is my constant................................................................................>
public static final String ANIMAL_TYPE = "Non-endangered";
    public Animal(String name) {


        this.name = name;
        this.type = ANIMAL_TYPE;
    }

    public int getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    public  String getType() {

        return this.type;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return id == animal.id &&
                Objects.equals(name, animal.name) &&
                Objects.equals(type, animal.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type);
    }

    public void save () {
        if( this.name == null || this.name.trim().isEmpty()){
            throw new NullPointerException("name cannot be null");
        }
        String sql = "INSERT INTO animals (type, name) VALUES (:type, :name);";
        try(Connection con = DB.sql2o.open()){
            this.id = (int) con.createQuery(sql, true)
                    .addParameter("type", this.type)
                    .addParameter("name", this.name)
                    .executeUpdate()
                    .getKey();
        }
    }

    public static List<Animal> all() {
        String sql = "SELECT * FROM animals WHERE type = :type;";
        try(Connection con = DB.sql2o.open()) {
           return con.createQuery(sql)
                   .addParameter("type",ANIMAL_TYPE)
                   .throwOnMappingFailure(false)
                  .executeAndFetch(Animal.class);
        }
    }

    public static Animal findById (int id) {
        String sql = "SELECT * FROM animals WHERE id = :id AND type = :type;";
        try (Connection con = DB.sql2o.open()){
            return con.createQuery(sql)
                    .addParameter("id", id)
                    .addParameter("type", ANIMAL_TYPE)
                    .throwOnMappingFailure(false)
                    .executeAndFetchFirst(Animal.class);
        }
    }

    public  void update( String name){
        if( name == null || name.trim().isEmpty()){
            throw new NullPointerException("name cannot be null or empty");
        }
        String sql = "UPDATE animals SET name = :name WHERE id = :id;";
        try(Connection con = DB.sql2o.open()){
            con.createQuery(sql)
                    .addParameter("name", name)
                    .addParameter("id", this.id)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    public static void clearAll() {
        String sql = "DELETE FROM animals WHERE type = :type;";
        try(Connection con = DB.sql2o.open()){
            con.createQuery(sql)
                    .addParameter("type", ANIMAL_TYPE)
                    .executeUpdate();
        }catch (Sql2oException ex){
            System.out.println(ex);
        }
    }

    public static void deleteById(int id) {
        String sql = "DELETE FROM animals WHERE id = :id;";
        try(Connection con = DB.sql2o.open()){
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }catch (Sql2oException ex){
            System.out.println(ex);
        }
    }

    public List<Sighting> findSightings() {
        String sql = "SELECT * FROM sightings WHERE animalId = :animalId;";
        try (Connection con = DB.sql2o.open()){
            return con.createQuery(sql)
                    .addParameter("animalId", this.id)
                    .executeAndFetch(Sighting.class);
        }
    }
}
