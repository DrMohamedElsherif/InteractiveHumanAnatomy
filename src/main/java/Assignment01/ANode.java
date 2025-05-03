package Assignment01;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ANode(String id, String name, List<ANode> children) {
    public static ANode createTree(List<Relation> relations) {
        Map<String, ANode> nodeMap = new HashMap<>();
        Map<String, Boolean> isChild = new HashMap<>();

        // We Build the Tree from bottom up
        // First Create all nodes and track children
        for (Relation relation : relations) {
            nodeMap.putIfAbsent(relation.parentId(),
                    new ANode(relation.parentId(), relation.parentName(), new ArrayList<>()));

            nodeMap.putIfAbsent(relation.childId(),
                    new ANode(relation.childId(), relation.childName(), new ArrayList<>()));

            isChild.put(relation.childId(), true);
            isChild.putIfAbsent(relation.parentId(), false);
        }

        // Second Build tree structure
        for (Relation relation : relations) {
            ANode parent = nodeMap.get(relation.parentId());
            ANode child = nodeMap.get(relation.childId());
            parent.children().add(child);
        }

        // Find root node (this should never be a child)
        ANode root = null;
        for (var entry : isChild.entrySet()) {
            if (!entry.getValue()) {
                root = nodeMap.get(entry.getKey());
                break;
            }
        }

        if (root == null) {
            throw new IllegalStateException("No root node found");
        }

        System.out.println("Tree: " + countNodes(root));
        return root;
    }

    private static int countNodes(ANode node) {
        if (node == null) return 0;
        int count = 1;
        for (ANode child : node.children()) {
            count += countNodes(child);
        }
        return count;
    }

    public void printAllPaths(String[] filters) {
        printAllPaths(this, new ArrayList<>(), filters);
    }

    private void printAllPaths(ANode node, List<ANode> currentPath, String[] filters) {
        currentPath.add(node);

        if (node.children().isEmpty()) {
            if (matchesFilters(currentPath, filters)) {
                System.out.println(formatPath(currentPath));
            }
        } else {
            for (ANode child : node.children()) {
                printAllPaths(child, new ArrayList<>(currentPath), filters);
            }
        }
    }

    private String formatPath(List<ANode> path) {
        return path.stream()
                .map(ANode::name)
                .collect(Collectors.joining(" -> "));
    }

    private boolean matchesFilters(List<ANode> path, String[] filters) {
        if (filters == null || filters.length == 0) return true;

        String pathString = path.stream()
                .map(ANode::name)
                .collect(Collectors.joining(" "));

        for (String filter : filters) {
            if (!pathString.toLowerCase().contains(filter.toLowerCase())) {
                return false;
            }
        }
        return true;
    }
}