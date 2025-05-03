package Assignment02;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TreeLoader {

    public static ANode load(String partsListFile, String elementPartsFile, String inclusionRelationsFile) throws IOException {
        Map<String, ANode> idToNodeBase = new HashMap<>();                    // conceptId -> base ANode without children
        Map<String, List<String>> parentToChildren = new HashMap<>();         // parentId -> list of childIds

        // Load parts
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getResourceAsStream(partsListFile), StandardCharsets.UTF_8))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t");
                if (tokens.length >= 3) {
                    String conceptId = tokens[0];
                    String representationId = tokens[1];
                    String name = tokens[2];
                    idToNodeBase.put(conceptId, new ANode(conceptId, representationId, name, new ArrayList<>(), new ArrayList<>()));
                }
            }
        }

        // Load file IDs
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getResourceAsStream(elementPartsFile), StandardCharsets.UTF_8))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t");
                if (tokens.length >= 3) {
                    String conceptId = tokens[0];
                    String fileId = tokens[2];
                    ANode baseNode = idToNodeBase.get(conceptId);
                    if (baseNode != null) {
                        List<String> updatedFileIds = new ArrayList<>(baseNode.fileIds());
                        updatedFileIds.add(fileId);
                        idToNodeBase.put(conceptId, new ANode(baseNode.conceptId(), baseNode.representationId(), baseNode.name(), baseNode.children(), updatedFileIds));
                    }
                }
            }
        }

        // Load parent-child relationships
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getResourceAsStream(inclusionRelationsFile), StandardCharsets.UTF_8))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t");
                if (tokens.length >= 4) {
                    String parentId = tokens[0];
                    String childId = tokens[2];
                    parentToChildren.computeIfAbsent(parentId, k -> new ArrayList<>()).add(childId);
                }
            }
        }

        // Identify the root
        Set<String> allChildren = new HashSet<>();
        for (List<String> childrenList : parentToChildren.values()) {
            allChildren.addAll(childrenList);
        }
        String rootId = null;
        for (String id : idToNodeBase.keySet()) {
            if (!allChildren.contains(id)) {
                rootId = id;
                break;
            }
        }
        if (rootId == null) {
            throw new IllegalStateException("No root found");
        }

        // Recursively build the full tree
        return buildTree(rootId, idToNodeBase, parentToChildren);
    }

    private static ANode buildTree(String nodeId, Map<String, ANode> baseNodes, Map<String, List<String>> parentToChildren) {
        ANode baseNode = baseNodes.get(nodeId);
        if (baseNode == null) {
            return null;
        }
        List<ANode> children = new ArrayList<>();
        List<String> childIds = parentToChildren.get(nodeId);
        if (childIds != null) {
            for (String childId : childIds) {
                ANode childNode = buildTree(childId, baseNodes, parentToChildren);
                if (childNode != null) {
                    children.add(childNode);
                }
            }
        }
        return new ANode(baseNode.conceptId(), baseNode.representationId(), baseNode.name(), children, baseNode.fileIds());
    }

    private static InputStream getResourceAsStream(String filename) throws FileNotFoundException {
        InputStream is = TreeLoader.class.getClassLoader().getResourceAsStream(filename);
        if (is == null) {
            throw new FileNotFoundException("Resource not found: " + filename);
        }
        return is;
    }
}
