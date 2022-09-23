package mn180452d.diplomski.solvers;

import mn180452d.diplomski.Graph;
import mn180452d.diplomski.Node;

import java.util.*;

public class GreedySolver implements Solver
{

    private List<Node> dominatingSet;
    private PriorityQueue<Node> priorityQueue;
    private long timeSpentSolving;

    public GreedySolver()
    {
        Comparator<Node> customNodeComparator = new Comparator<Node>()
        {
            @Override
            public int compare(Node o1, Node o2)
            {
                return o2.GetHowManyWouldBeDominated() - o1.GetHowManyWouldBeDominated();
            }
        };
        dominatingSet = new ArrayList<>();
        priorityQueue = new PriorityQueue<>(customNodeComparator);
        timeSpentSolving = -1;
    }

    @Override
    public boolean Solve()
    {
        try
        {
            long timeStart = System.nanoTime();
            List<Node> resetNodes = new ArrayList<>();
            priorityQueue.addAll(Graph.GetInstance().GetAllNodes());
            do
            {
                Node newNodeToAddToSet = priorityQueue.poll();
                newNodeToAddToSet.SetDominated(true);
                dominatingSet.add(newNodeToAddToSet);
                resetNodes.add(newNodeToAddToSet);

                Set<Node> changedGreedyValueSet = new HashSet<>();
                for(Node neighbour : newNodeToAddToSet.GetConnected())
                {
                    neighbour.SetDominated(true);
                    resetNodes.add(neighbour);
                    changedGreedyValueSet.add(neighbour);
                    changedGreedyValueSet.addAll(neighbour.GetConnected());
                }
                changedGreedyValueSet.remove(newNodeToAddToSet);

                priorityQueue.removeAll(changedGreedyValueSet);
                priorityQueue.addAll(changedGreedyValueSet);
            }while(!Solver.CheckIfDominating(dominatingSet));

            for(Node tmp : resetNodes)
                tmp.SetDominated(false);
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
        return dominatingSet;
    }

    @Override
    public long GetTimeSpent()
    {
        return timeSpentSolving;
    }

    @Override
    public String toString()
    {
        return "Greedy algorithm";
    }

    @Override
    public void Reset()
    {
        dominatingSet = new ArrayList<>();
        timeSpentSolving = -1;
        priorityQueue.clear();
    }
}
