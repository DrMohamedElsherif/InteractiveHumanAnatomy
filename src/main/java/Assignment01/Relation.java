package Assignment01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public record Relation(String parentId, String parentName, String childId, String childName) {

    // Read file from InputStream
    public static List<Relation> loadFromInputStream(InputStream inputStream) throws IOException {
        List<Relation> relations = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 4) {
                    relations.add(new Relation(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        }

        System.out.println("Relations: " + relations.size());
        return relations;
    }
}
