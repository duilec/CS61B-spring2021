package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MyUtils {
    private static char[] numbers = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static char[] validChars = {':', 'N', 'L', 'Q', 'P', 'W', 'S', 'A', 'D'};

    private static boolean PRINT_TYPED_KEYS = true;

    // it is number?
    public static boolean isNumber(char c) {
        for (char num : numbers) {
            if (c == num) {
                return true;
            }
        }
        return false;
    }

    // it is valid number?
    public static boolean isValidChar(char c) {
        for (char validChar : validChars) {
            if (c == validChar) {
                return true;
            }
        }
        return false;
    }

    // get next typed key that must cast to upper
    public static char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                // cast to upper
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (PRINT_TYPED_KEYS) {
                    System.out.println(c);
                }
                return c;
            }
        }
    }

    // some methods about reading and writing file from proj2
    /** Return an object of type T read from FILE, casting it to EXPECTEDCLASS.
     *  Throws IllegalArgumentException in case of problems. */
    static <T extends Serializable> T readObject(File file,
                                                 Class<T> expectedClass) {
        try {
            ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(file));
            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        } catch (IOException | ClassCastException
                 | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Write OBJ to FILE. */
    static void writeObject(File file, Serializable obj) {
        writeContents(file, serialize(obj));
    }

    /** Write the result of concatenating the bytes in CONTENTS to FILE,
     *  creating or overwriting it as needed.  Each object in CONTENTS may be
     *  either a String or a byte array.  Throws IllegalArgumentException
     *  in case of problems. */
    static void writeContents(File file, Object... contents) {
        try {
            if (file.isDirectory()) {
                throw
                        new IllegalArgumentException("cannot overwrite directory");
            }
            BufferedOutputStream str =
                    new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Returns a byte array containing the serialized contents of OBJ. */
    static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            throw error("Internal error serializing commit.");
        }
    }

    /** Return a GitletException whose message is composed from MSG and ARGS as
     *  for the String.format method. */
    static BYOWException error(String msg, Object... args) {
        return new BYOWException(String.format(msg, args));
    }

    /** Return the concatentation of FIRST and OTHERS into a File designator,
     *  analogous to the {@link java.nio.file.Paths.#get(String, String[])}
     *  method. */
    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    /** Return the concatentation of FIRST and OTHERS into a File designator,
     *  analogous to the {@link java.nio.file.Paths.#get(String, String[])}
     *  method. */
    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }
}
