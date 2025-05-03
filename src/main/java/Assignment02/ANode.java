package Assignment02;

import java.util.*;

public record ANode(String conceptId, String representationId, String name, Collection<ANode> children, Collection<String> fileIds) {
    @Override
    public String toString() {
        return name + " (" + conceptId + ")";
    }
}

