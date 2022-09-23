package mn180452d.diplomski.solvers;

import mn180452d.diplomski.Graph;
import mn180452d.diplomski.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface Solver
{
    static boolean CheckIfDominating(List<Node> setToCheck)
    {
        try
        {
            Set<Node> allDominatedNodes = new HashSet<>();
            for(Node tmpNode : setToCheck)
            {
                allDominatedNodes.add(tmpNode);
                allDominatedNodes.addAll(tmpNode.GetConnected());
            }
            return allDominatedNodes.containsAll(Graph.GetInstance().GetAllNodes());
        }
        catch (Exception e)
        {
            System.err.println(e);
            return false;
        }
    }

    boolean Solve();

    List<Node> GetDominatingSet();

    long GetTimeSpent();

    void Reset();
}
