import models.*;
import spark.ModelAndView;
import spark.Spark;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class App{
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        staticFileLocation("/public");
//get to display the default page interacting with the user
        get("/",(request, response) -> {
            Map<String,Object> model=new HashMap<String, Object>();
            return new ModelAndView(model,"index.hbs");
        },new HandlebarsTemplateEngine());

//<.....................................this route will display sightings to the user.................................
        get("/view/wildlife", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Animal> animals = Animal.all();
            model.put("animals", animals);
            List<EndangeredAnimal> endangeredAnimals = EndangeredAnimal.allEndangered();
            model.put("endangeredAnimals", endangeredAnimals);
            List<Sighting> sightings = Sighting.all();
            model.put("sightings", sightings);
            return new ModelAndView(model, "sightings-view.hbs");
        }, new HandlebarsTemplateEngine() );

// <................................this is the route to allow a user add a new animal to the list.......................
        get("/animals/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Animal> animals = Animal.all();
            model.put("animals", animals);
            List<EndangeredAnimal> endangeredAnimals = EndangeredAnimal.allEndangered();
            model.put("endangeredAnimals", endangeredAnimals);
            return new ModelAndView(model, "Non-Endangered-form.hbs");
        }, new HandlebarsTemplateEngine() );
//  <.....................this route will allow a user to view both endangered and non-endangered animals present on the list........
        get("/wildlife/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Animal> animals = Animal.all();
            model.put("animals", animals);
            String name = req.queryParams("name");
            List<EndangeredAnimal> endangeredAnimals = EndangeredAnimal.allEndangered();
            model.put("endangeredAnimals", endangeredAnimals);

            return new ModelAndView(model, "Wildlife-View.hbs");
        }, new HandlebarsTemplateEngine() );

        get("/endangered/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Animal> animals = Animal.all();
            model.put("animals", animals);
            List<EndangeredAnimal> endangeredAnimals = EndangeredAnimal.allEndangered();
            model.put("endangeredAnimals", endangeredAnimals);
            return new ModelAndView(model, "Endangered-form.hbs");
        }, new HandlebarsTemplateEngine() );

        //post: process a form to create new endangered animal
        post("/endangered", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String endangeredName = req.queryParams("name");
            String health = req.queryParams("health");
            String age = req.queryParams("age");
            EndangeredAnimal newEndangeredAnimal = new EndangeredAnimal(endangeredName, health, age);
            try{
                newEndangeredAnimal.save();
                res.redirect("/");
                return null;
            } catch (NullPointerException exception) {
                return new ModelAndView(model, "exceptions.hbs");
            }
        }, new HandlebarsTemplateEngine() );

        //get: delete all animals and endangered animals and sightings
        get("/animals/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            Animal.clearAll();
            EndangeredAnimal.clearAll();
            Sighting.clearAll();
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine() );

        //get: delete individual animal
        get("/animals/:animalId/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfAnimal = Integer.parseInt(req.params("animalId"));
            Animal.deleteById(idOfAnimal);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine() );

        //get: delete individual endangered animal
        get("/endangered/:animalId/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfAnimal = Integer.parseInt(req.params("animalId"));
            EndangeredAnimal.deleteById(idOfAnimal);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine() );

        //get: delete all sightings
        get("/sightings/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            Sighting.clearAll();
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine() );

        get("/endangered/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfAnimalToFind = Integer.parseInt(req.params("id"));
            EndangeredAnimal foundAnimal = EndangeredAnimal.findById(idOfAnimalToFind);
            model.put("animal", foundAnimal);
            List<Sighting> allSightingsByAnimal = foundAnimal.findSightings();
            model.put("sightings", allSightingsByAnimal);
            model.put("animals", Animal.all()); //refresh list of links for navbar
            model.put("endangeredAnimals", EndangeredAnimal.allEndangered());
            return new ModelAndView(model, "Endangered-details.hbs");
        }, new HandlebarsTemplateEngine() );

        //get: show a form to update animal
        get("/animals/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfAnimal = Integer.parseInt(req.params("id"));
            Animal foundAnimal = Animal.findById(idOfAnimal);
            model.put("editAnimal", foundAnimal);
            List<Animal> animals = Animal.all();
            model.put("animals", animals);
            List<EndangeredAnimal> endangeredAnimals = EndangeredAnimal.allEndangered();
            model.put("endangeredAnimals", endangeredAnimals);
            return new ModelAndView(model, "Non-Endangered-form.hbs");
        }, new HandlebarsTemplateEngine() );

        //post: process form to update animal
        post("/animals/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String animalName = req.queryParams("name");
            int idOfAnimal = Integer.parseInt(req.params("id"));
            Animal foundAnimal = Animal.findById(idOfAnimal);
            try{
                foundAnimal.update(animalName);
                res.redirect("/");
                return null;
            } catch (NullPointerException ex) {
                return new ModelAndView(model, "exceptions.hbs");
            }
        }, new HandlebarsTemplateEngine() );

        //get: show a form to update endangered animal
        get("/endangered/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfAnimal = Integer.parseInt(req.params("id"));
            EndangeredAnimal foundAnimal = EndangeredAnimal.findById(idOfAnimal);
            model.put("editEndangered", foundAnimal);
            List<Animal> animals = Animal.all();
            model.put("animals", animals);
            List<EndangeredAnimal> endangeredAnimals = EndangeredAnimal.allEndangered();
            model.put("endangeredAnimals", endangeredAnimals);
            return new ModelAndView(model, "Endangered-form.hbs");
        }, new HandlebarsTemplateEngine() );

        //post: process form to update endangered animal
        post("/endangered/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String animalName = req.queryParams("name");
            String animalHealth = req.queryParams("health");
            String animalAge = req.queryParams("age");
            int idOfAnimal = Integer.parseInt(req.params("id"));
            EndangeredAnimal foundAnimal = EndangeredAnimal.findById(idOfAnimal);
            try{
                foundAnimal.update(animalName, animalHealth, animalAge);
                res.redirect("/");
                return null;
            } catch (NullPointerException ex) {
                return new ModelAndView(model, "exceptions.hbs");
            }
        }, new HandlebarsTemplateEngine() );

        //get: delete individual sighting
        get("/animals/:animalId/sightings/:id/delete", (req, res) -> {
            int idOfSighting = Integer.parseInt(req.params("id"));
            Sighting sightingToDelete = Sighting.findById(idOfSighting);
            Sighting.deleteById(idOfSighting);
            res.redirect("/");
            return null;
        },new HandlebarsTemplateEngine() );

        //get: show a form to record new animal sighting
        get("/animals/sighting/new", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            List<Animal> animals = Animal.all();
            model.put("animals", animals);
            List<EndangeredAnimal> endangeredAnimals = EndangeredAnimal.allEndangered();
            model.put("endangeredAnimals", endangeredAnimals);
            return new ModelAndView(model, "Non-Endangered-sighting-form.hbs");
        }, new HandlebarsTemplateEngine() );

        //post: process a form to record new animal sighting
        post("/animalsighting", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Animal> allAnimals = Animal.all();
            model.put("animals", allAnimals);
            String name = req.queryParams("name");
            String location = req.queryParams("location");
            int animalId = Integer.parseInt(req.queryParams("animalId"));
            Sighting newSighting = new Sighting(location, name, animalId);
            try{
                newSighting.save();
                res.redirect("/");
                return null;
            } catch (NullPointerException ex) {
                return  new ModelAndView(model, "exceptions.hbs");
            }
        }, new HandlebarsTemplateEngine());

        //get: show a form to record new endangered animal sighting
        get("/endangered/sighting/new", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            List<Animal> animals = Animal.all();
            model.put("animals", animals);
            List<EndangeredAnimal> endangeredAnimals = EndangeredAnimal.allEndangered();
            model.put("endangeredAnimals", endangeredAnimals);
            return new ModelAndView(model, "Endangered-sighting-form.hbs");
        }, new HandlebarsTemplateEngine() );
// <.............this route will handle exeptions ..................
        post("/animals", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String animalName = req.queryParams("name");
            Animal newAnimal = new Animal(animalName);
            try{
                newAnimal.save();
                res.redirect("/");
                return null;
            } catch (NullPointerException exception) {
                return new ModelAndView(model, "exceptions.hbs");
            }
        }, new HandlebarsTemplateEngine() );

        //route to record a new endangered sighting and handle it's exceptions...................
        post("/endangeredsighting", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String rangerName = req.queryParams("rangerName");
            String location = req.queryParams("location");
            int idOfAnimal = Integer.parseInt(req.queryParams("animalId"));
            Sighting newSighting = new Sighting(location, rangerName, idOfAnimal);
            try{
                newSighting.save();
                res.redirect("/");
                return null;
            } catch (NullPointerException ex) {
                return  new ModelAndView(model, "exceptions.hbs");
            }
        }, new HandlebarsTemplateEngine() );
//path that will display each details of sightings.........................................
        get("/animals/:animal_id/sightings/:sighting_id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfSightingToFind = Integer.parseInt(req.params("sighting_id"));
            Sighting foundSighting = Sighting.findById(idOfSightingToFind);
            int idOfAnimalToFind = Integer.parseInt(req.params("animal_id"));
            Animal foundAnimal = Animal.findById(idOfAnimalToFind);
            EndangeredAnimal foundEndangeredAnimal = EndangeredAnimal.findById(idOfAnimalToFind);
            model.put("animal", foundAnimal);
            model.put("endangered", foundEndangeredAnimal);
            model.put("sighting", foundSighting);

            model.put("animals", Animal.all());
            List<EndangeredAnimal> endangeredAnimals = EndangeredAnimal.allEndangered();
            model.put("endangeredAnimals", endangeredAnimals);
            return new ModelAndView(model, "Sighting-details.hbs");
        }, new HandlebarsTemplateEngine());

        get("/animals/sighting/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfSightingToEdit = Integer.parseInt(req.params("id"));
            Sighting editSighting = Sighting.findById(idOfSightingToEdit);
            model.put("editSighting", editSighting);
            List<Animal> animals = Animal.all();
            model.put("animals", animals);
            List<EndangeredAnimal> endangeredAnimals = EndangeredAnimal.allEndangered();
            model.put("endangeredAnimals", endangeredAnimals);
            return new ModelAndView(model, "Non-Endangered-sighting-form.hbs");
        }, new HandlebarsTemplateEngine());

        //post: process form to update a sighting
//        post("/animalsighting/:id", (req, res) -> {
//            Map<String, Object> model = new HashMap<>();
//            List<Animal> allAnimals = Animal.all();//refresh navbar list
//            model.put("animals", allAnimals);
//            List<EndangeredAnimal> allEndangeredAnimals = EndangeredAnimal.allEndangered();//refresh navbar list
//            model.put("endangeredAnimals", allEndangeredAnimals);
//
//            String name = req.queryParams("name");
//            String location = req.queryParams("location");
//            int animalId = Integer.parseInt(req.queryParams("animalId"));
//            int idOfSightingToEdit = Integer.parseInt(req.params("id"));
//            try{
//                Sighting.update(idOfSightingToEdit, location, name, animalId);
//                res.redirect("/");
//                return null;
//            } catch (NullPointerException ex) {
//                return  new ModelAndView(model, "exceptions.hbs");
//            }
//        }, new HandlebarsTemplateEngine());

        //get: show form to update a sighting of endangered animal
        get("/endangered/sighting/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfSightingToEdit = Integer.parseInt(req.params("id"));
            Sighting editSighting = Sighting.findById(idOfSightingToEdit);
            model.put("editSighting", editSighting);
            List<Animal> animals = Animal.all();
            model.put("animals", animals);
            List<EndangeredAnimal> endangeredAnimals = EndangeredAnimal.allEndangered();
            model.put("endangeredAnimals", endangeredAnimals);
            return new ModelAndView(model, "Endangered-sighting-form.hbs");
        }, new HandlebarsTemplateEngine());

        //post: process form to update a sighting of endangered animal
        post("/endangeredsighting/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Animal> allAnimals = Animal.all();//refresh navbar list
            model.put("animals", allAnimals);
            List<EndangeredAnimal> allEndangeredAnimals = EndangeredAnimal.allEndangered();//refresh navbar list
            model.put("endangeredAnimals", allEndangeredAnimals);

            String name = req.queryParams("name");
            String location = req.queryParams("location");
            int animalId = Integer.parseInt(req.queryParams("animalId"));
            int idOfSightingToEdit = Integer.parseInt(req.params("id"));
            try{
                Sighting.update(idOfSightingToEdit, location, name, animalId);
                res.redirect("/");
                return null;
            } catch (NullPointerException ex) {
                return  new ModelAndView(model, "exceptions.hbs");
            }
        }, new HandlebarsTemplateEngine());
    }
}