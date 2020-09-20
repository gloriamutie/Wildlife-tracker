package models;

import dbRule.DatabaseRule;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class AnimalTest {
    @Rule
    public DatabaseRule database = new DatabaseRule();//database rule
//<..............test for correct instantiation.............................
    @Test
    public void AnimalInstantiatesCorrectly_true() {
        Animal newAnimal = setNewAnimal();
        assertEquals(true, newAnimal instanceof Animal);
    }
//<.............testing if getname method returns correct name........................
    @Test
    public void getNameReturnsCorrectName_String() {
        Animal newAnimal = setNewAnimal();
        assertEquals("Zebra", newAnimal.getName());
    }
//<..................testing if getType method returns correct animal type............................................
    @Test
    public void getTypeReturnsCorrectAnimalType() {
        Animal newAnimal = setNewAnimal();
        assertEquals("Non-endangered", newAnimal.getType());
    }
//<............................overidding....................................
    @Test
    public void equals_returnsTrueIfNamesAreEqual() {
        Animal newAnimal = setNewAnimal();
        Animal otherAnimal = new Animal("Zebra");
        assertTrue(newAnimal.equals(otherAnimal));
    }
//<.........................animalsAre saved to database...................................................................
    @Test
    public void save_animalsAreSavedCorrectlyInDatabase() {
        Animal newAnimal = new Animal("Zebra");
        newAnimal.save();
        assertEquals(true, Animal.all().get(0).equals(newAnimal));
    }
//<........................save() assigns Id............................................
    @Test
    public void saveAssignsIdToAnimal() {
        Animal  firstAnimal = setNewAnimal();
        firstAnimal.save();
        Animal savedAnimal = Animal.all().get(0);
        assertEquals(savedAnimal.getId(), firstAnimal.getId());
    }
//<.........................all animal instances are returned.............................................................................
    @Test
    public void allReturnsAllInstancesOfAnimal_true() {
        Animal firstAnimal = setNewAnimal();
        firstAnimal.save();
        Animal secondAnimal = new Animal("Baboon");
        secondAnimal.save();
        assertEquals(true, Animal.all().get(0).equals(firstAnimal));
        assertEquals(true, Animal.all().get(1).equals(secondAnimal));
    }
//<...............................

    @Test
    public void findByIdReturnsCorrectInstanceOfAnimal_true() {
        Animal newAnimal = new Animal("Lion");
        newAnimal.save();
        Animal savedAnimal = Animal.findById(newAnimal.getId());
        assertEquals(newAnimal, savedAnimal);
    }

    @Test(expected = NullPointerException.class)
    public void update_throwsExceptionIfNameNull(){
        Animal testAnimal = new Animal("Baboon");
        testAnimal.save();
        testAnimal.update(null);
    }

    @Test
    public void update_nameCannotBeNull(){
        Animal testAnimal = new Animal("Lion");
        testAnimal.save();
        try {
            testAnimal.update(null);
            Animal updatedAnimal = Animal.findById(testAnimal.getId());
            assertTrue(Animal.findById(testAnimal.getId()).equals(updatedAnimal));
        } catch (NullPointerException exception){ System.out.println(exception);}
    }

    @Test
    public void updateAnimalInstanceIsUpdatedCorrectly_true() {
        Animal newAnimal = setNewAnimal();
        String originalName = newAnimal.getName();
        newAnimal.save();
        newAnimal.update("monkey");
        Animal updatedAnimal = Animal.findById(newAnimal.getId());
        assertNotEquals(originalName, updatedAnimal.getName());
    }

    @Test
    public void clearAllClearsAllInstances_true() {
      Animal newAnimal = setNewAnimal();
      newAnimal.save();
      Animal.clearAll();
      assertEquals(0, Animal.all().size());
    }
    @Test
    public  void findSightingsReturnsSightingsRelatedToAnimalInstance_true(){
        Animal firstAnimal = setNewAnimal();
        firstAnimal.save();
        Sighting firstSighting = new Sighting("Zone A", "Peter", firstAnimal.getId());
        firstSighting.save();
        Sighting secondSighting = new Sighting("Forest Clearing", "Maureen", firstAnimal.getId());
        secondSighting.save();
        assertEquals(2, firstAnimal.findSightings().size());
        assertTrue(firstAnimal.findSightings().contains(firstSighting));
        assertTrue(firstAnimal.findSightings().contains(secondSighting));
    }
    @Test
    public void deleteByIdDeletesCorrectAnimal_true() {
        Animal firstAnimal = setNewAnimal();
        firstAnimal.save();
        Animal secondAnimal = new Animal("elephant");
        secondAnimal.save();
        Animal.deleteById(firstAnimal.getId());
        assertEquals(1, Animal.all().size());
        assertTrue(Animal.all().contains(secondAnimal));
    }

    @Test(expected = NullPointerException.class)
    public void saveThrowsExceptionIfNameNull_true(){
        Animal testAnimal = new Animal(null);//name
        testAnimal.save();
    }

    @Test
    public void saveCatchesExeptions_true(){
        Animal testAnimal = new Animal(null);
        try {
            testAnimal.save();
            assertTrue(Animal.findById(testAnimal.getId()).equals(testAnimal));
        } catch (NullPointerException exception){ System.out.println(exception);}
    }


//<.............helper.....................................................
    private Animal setNewAnimal(){
        return new Animal("Zebra");
    }
}