package visibility;

import trace.HBGNode;

import java.util.HashMap;
import java.util.Set;

public class LinVisibility {
    private HashMap<HBGNode, Set<HBGNode>> visibility = new HashMap<>();

    public void setVisibility(HashMap<HBGNode, Set<HBGNode>> visibility) {
        this.visibility = visibility;
    }

    public void cleanVisibility() {
        visibility = new HashMap<>();
    }

    public Set<HBGNode> getNodeVisibility(HBGNode node) {
        return visibility.get(node);
    }

    public void updateNodeVisibility(HBGNode node, Set<HBGNode> vis) {
        visibility.put(node, vis);
    }

    public String toString() {
        return visibility.toString();
    }
}
