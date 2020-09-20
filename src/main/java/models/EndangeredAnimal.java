package models;


import org.sql2o.Connection;
import org.sql2o.Sql2oException;

import java.util.List;
import java.util.Objects;
//EndangeredAnimal inherits the Animal class.....................................................................
public class EndangeredAnimal extends Animal{
    private String health;
    private String age;

    private static final String DATABASE_TYPE = "Endangered Animals";
    public EndangeredAnimal(String name, String health, String age) {
        super(name);
        this.health = health;
        this.age = age;
        this.type = DATABASE_TYPE;
    }

    public String getHealth()
    {
        return health;
    }

    public String getAge() {

        return age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EndangeredAnimal that = (EndangeredAnimal) o;
        return Objects.equals(health, that.health) &&
                Objects.equals(age, that.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), health, age);
    }

    @Override
    public void save() {
        if((this.name == null || this.name.trim().isEmpty()) || (this.health == null || this.health.trim().isEmpty()) || (this.age == null || this.age.trim().isEmpty())){
            throw new NullPointerException("name , health and age cannot be empty");//null pointer exception
        }
        String sql = "INSERT INTO animals (type, name, health, age) VALUES (:type, :name, :health, :age);";
        try(Connection con = DB.sql2o.open()) {
           this.id = (int) con.createQuery(sql,true)
                    .addParameter("name", this.getName())
                    .addParameter("health", this.getHealth())
                    .addParameter("age", this.getAge())
                    .addParameter("type", this.getType())
                    .executeUpdate()
                    .getKey();
        }catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    public static List<EndangeredAnimal> allEndangered() {
        String sql = "SELECT * FROM animals WHERE type = :type;";
        try(Connection con = DB.sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("type", DATABASE_TYPE)
                    .throwOnMappingFailure(false)
                    .executeAndFetch(EndangeredAnimal.class);
        }
    }

    public static EndangeredAnimal findById (int id) {
        String sql = "SELECT * FROM animals WHERE id = :id AND type = :type;";
        try (Connection con = DB.sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id", id)
                    .addParameter("type", DATABASE_TYPE)
                    .throwOnMappingFailure(false)
                    .executeAndFetchFirst(EndangeredAnimal.class);
        }
    }

    public static void clearAll() {
        String sql = "DELETE FROM animals WHERE type = :type;";
        try(Connection con = DB.sql2o.open()){
            con.createQuery(sql)
                    .addParameter("type", DATABASE_TYPE)
                    .executeUpdate();
        }catch (Sql2oException ex){
            System.out.println(ex);
        }
    }

    public void update (String name, String health, String age){
        if(name == null || health == null || age == null){
            throw new NullPointerException("You need to fill in your details!!");//null pointer exception
        }
        String sql = "UPDATE animals SET (name, health, age) = (:name, :health, :age) WHERE id = :id;";
        try(Connection con = DB.sql2o.open()){
            con.createQuery(sql)
                    .addParameter("name", name)
                    .addParameter("health", health)
                    .addParameter("age", age)
                    .addParameter("id", this.getId())
                    .executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }
}
