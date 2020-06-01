package trace;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

public class HBGNode {
    private Invocation invocation;
    private List<HBGNode> nexts = new ArrayList<>();
    private List<HBGNode> prevs = new ArrayList<>();
    private int threshold = 0;

    public HBGNode() {
        ;
    }

    public HBGNode(Invocation invocation, int id) {
        this.invocation = invocation;
        invocation.setId(id);
    }



    public boolean checkThreshold() {
        return threshold == prevs.size();
    }

    public void increaseThreshlod() {
        for (HBGNode node : nexts) {
            node.threshold++;
        }
    }

    public void decreaseThreshlod() {
        for (HBGNode node : nexts) {
            node.threshold--;
        }
    }

    public void addNextNode(HBGNode next) {
        nexts.add(next);
    }
    public void addPrevNode(HBGNode prev) {
        prevs.add(prev);
    }

    public int getId() {
        return invocation.getId();
    }

    public List<HBGNode> getNexts() {
        return nexts;
    }

    public List<HBGNode> getPrevs() {
        return prevs;
    }

    public Invocation getInvocation() {
        return invocation;
    }

//    public Set<HBGNode> vis(Linearization prefixLin) {
//        String visibility = Invocation.visibility.get(getInvocation().getMethodName());
//        if (visibility.equals("COMPLETE")) {
//            return new CompleteVisibilityPredicate().vis(prefixLin);
//        } else if (visibility.equals("CAUSAL")) {
//            return new CausalVisibilityPredicate().vis(prefixLin);
//        } else if (visibility.equals("PEER")) {
//            return new PeerVisibilityPredicate().vis(prefixLin);
//        } else if (visibility.equals("MONOTONIC")) {
//            return new MonotonicVisibilityPredicate().vis(prefixLin);
//        } else if (visibility.equals("BASIC")) {
//            return new BasicVisibilityPredicate().vis(prefixLin);
//        } else {
//            return null;
//        }
//    }

    @Override
    public String toString() {
       String temp = "\"INVOCATION\":" + JSON.toJSONString(invocation);
//       + ", {\"NEXTS\":[";
//       for (HBGNode n : nexts) {
//           temp += JSON.toJSONString(n.getInvocation()) + " ";
//       }
//       return temp + "]}";
        return temp;
    }

    @Override
    public int hashCode() {
        return getId();
    }
}