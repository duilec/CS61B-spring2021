package capers;

import java.io.File;
import java.io.Serializable;

import static capers.Utils.*;

/** Represents a dog that can be serialized.
 * @author Huang Jinhong
*/

// read guide of lab one by one!
// To enable this feature(Serializable) for a given class in Java,
// this simply involves implementing the java.io.Serializable interface
public class Dog implements Serializable{ // TODO

    /** Folder that dogs live in. */
    // TODO Hint: look at the `join` function in Utils
    static final File DOG_FOLDER = join(".capers", "dogs");;

    /** Age of dog. */
    private int age;
    /** Breed of dog. */
    private String breed;
    /** Name of dog. */
    private String name;

    /**
     * Creates a dog object with the specified parameters.
     * @param name Name of dog
     * @param breed Breed of dog
     * @param age Age of dog
     */
    public Dog(String name, String breed, int age) {
        this.age = age;
        this.breed = breed;
        this.name = name;
    }

    /**
     * Reads in and deserializes a dog from a file with name NAME in DOG_FOLDER.
     *
     * @param name Name of dog to load
     * @return Dog read from file
     */
    public static Dog fromFile(String name)  {
        // TODO (hint: look at the Utils file)
        File DOG_FILE = join(DOG_FOLDER, name);
        // or use: File DOG_FILE = join(".capers", "dogs", name);
        // class Dog must "implements Serializable"
        return readObject(DOG_FILE, Dog.class);
    }

    /**
     * Increases a dog's age and celebrates!
     */
    public void haveBirthday() {
        age += 1;
        System.out.println(toString());
        System.out.println("Happy birthday! Woof! Woof!");
    }

    /**
     * Saves a dog to a file for future use.
     */
    public void saveDog() {
        // TODO (hint: don't forget dog names are unique)
        if (!DOG_FOLDER.exists()){
            DOG_FOLDER.mkdir();
        }
        File DOG_FILE = join(DOG_FOLDER, name);
        // class Dog "implements Serializable" in class Dog
        writeObject(DOG_FILE, this);
    }

    @Override
    public String toString() {
        return String.format(
            "Woof! My name is %s and I am a %s! I am %d years old! Woof!",
            name, breed, age);
    }

}


