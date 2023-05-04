package DataClasee;

import java.lang.reflect.InvocationTargetException;

public class DataFactory {
    public DataManipulation createDataManipulation(String arg) {
        String name;
        if (arg.toLowerCase().contains("file")) {
            name = "DataClasee.FileManipulation";
        } else if (arg.toLowerCase().contains("database")) {
            name = "DataClasee.DatabaseManipulation";
        } else {
            throw new IllegalArgumentException("Illegal Argument:" + arg);
        }
        try {
            return (DataManipulation) Class.forName(name).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
