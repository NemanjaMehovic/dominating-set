package mn180452d.diplomski.solvers.set_cover;

import mn180452d.diplomski.Graph;
import mn180452d.diplomski.Node;

import java.util.*;

public class SetCoverUtil
{
    private static HashMap<List<Integer>, Node> setNodeConnection;

    public static List<List<Integer>> CreateSetsFromGraph(Graph graph)
    {
        return CreateSetsFromGraph(graph, false);
    }

    public static List<List<Integer>> CreateSetsFromGraph(Graph graph, boolean init)
    {
        if(init)
            setNodeConnection = new HashMap<>();
        List<List<Integer>> s = new ArrayList<>();
        for (Node currentNode : graph.GetAllNodes())
        {
            List<Integer> currentSet = new ArrayList<>();
            currentSet.add(currentNode.GetId());
            for(Node neighbour : currentNode.GetConnected())
                currentSet.add(neighbour.GetId());
            if(init)
                setNodeConnection.put(currentSet, currentNode);
            s.add(currentSet);
        }
        return s;
    }

    public static Node GetNodeFromSet(List<Integer> set)
    {
        if(setNodeConnection == null || !setNodeConnection.containsKey(set))
            return null;
        return  setNodeConnection.get(set);
    }
}
