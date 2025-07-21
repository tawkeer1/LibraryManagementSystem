package library.example.admin;

import java.lang.reflect.Field;
import java.util.List;

public class AdminReportGenerator {

    public static void generateReport(String title, List<?> objects) {
        System.out.println("========== " + title.toUpperCase() + " ==========");
        for (Object obj : objects) {
            printObjectDetails(obj);
            System.out.println("-----------------------------------");
        }
    }

    private static void printObjectDetails(Object obj) {
        Class<?> clazz = obj.getClass();
        System.out.println("Class: " + clazz.getSimpleName());

        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true); // allow private field access
                try {
                    Object value = field.get(obj);
                    System.out.printf("%-20s: %s%n", field.getName(), value);
                } catch (IllegalAccessException e) {
                    System.out.println(field.getName() + ": [ACCESS DENIED]");
                }
            }
            clazz = clazz.getSuperclass(); // also include parent fields
        }
    }
}
