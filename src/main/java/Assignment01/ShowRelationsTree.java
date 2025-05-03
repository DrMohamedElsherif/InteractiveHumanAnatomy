package Assignment01;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ShowRelationsTree {
    public static void main(String[] args) {
        // Path to file in resources folder
        String filePath = "partof_inclusion_relation_list.txt";

        try (InputStream inputStream = ShowRelationsTree.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new IOException("File not found in resources: " + filePath);
            }

            // Load relations file
            List<Relation> relations = Relation.loadFromInputStream(inputStream);

            ANode root = ANode.createTree(relations);
            root.printAllPaths(args);

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.err.println("Please verify the file exists at: " + filePath);
            System.err.println();

        }
    }
}
