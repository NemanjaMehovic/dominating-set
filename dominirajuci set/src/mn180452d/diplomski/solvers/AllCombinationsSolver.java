package mn180452d.diplomski.solvers;

import mn180452d.diplomski.Graph;
import mn180452d.diplomski.Node;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllCombinationsSolver implements Solver
{

    private List<Node> dominatingSet;
    private long timeSpentSolving;

    public AllCombinationsSolver()
    {
        dominatingSet = new ArrayList<>();
        timeSpentSolving = -1;
    }

    @Override
    public boolean Solve()
    {
        try
        {
            long timeStart = System.nanoTime();
            List<Node> allNodes = Graph.GetInstance().GetAllNodes();
            dominatingSet = allNodes;
            long val = (long) Math.pow(2, allNodes.size());
            for(long i = 1; i < val; i++)
            {
                List<Node> vals = new ArrayList<>();
                for (int j = 0; j < allNodes.size(); j++)
                    if (((i >> j) & 1) == 1)
                        vals.add(allNodes.get(j));
                if (vals.size() >= dominatingSet.size())
                    continue;
                if (Solver.CheckIfDominating(vals))
                    dominatingSet = vals;
            }
            timeSpentSolving = System.nanoTime() - timeStart;
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Node> GetDominatingSet()
    {
        return Collections.unmodifiableList(dominatingSet);
    }

    @Override
    public long GetTimeSpent()
    {
        return timeSpentSolving;
    }

    @Override
    public void Reset()
    {
        dominatingSet = new ArrayList<>();
        timeSpentSolving = -1;
    }

    @Override
    public String toString()
    {
        return "All combinations check";
    }
}
