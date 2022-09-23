package mn180452d.diplomski.solvers.set_cover;

import mn180452d.diplomski.Graph;
import mn180452d.diplomski.Node;
import mn180452d.diplomski.solvers.Solver;

import java.util.*;
import java.util.stream.Collectors;

public class TrivialSolver implements Solver
{
    private List<Node> dominatingSet;
    private long timeSpentSolving;

    public TrivialSolver()
    {
        dominatingSet = new ArrayList<>();
        timeSpentSolving = -1;
    }

    protected void RestoreSolution(List<Integer> solution, HashMap<Integer, Integer> fixIndexes)
    {
        for(int i = 0; i < solution.size(); i++)
            solution.set(i, fixIndexes.get(solution.get(i)));
    }

    protected List<List<Integer>> Take(List<List<Integer>> setOfSets, List<Integer> toBeTaken, HashMap<Integer, Integer> fixIndexesMap)
    {
        List<List<Integer>> newSetOfSets = new ArrayList<>();
        int j = 0;
        int i = 0;
        for(List<Integer> tmpSet : setOfSets)
        {
            if(tmpSet != toBeTaken)
            {
                List<Integer> modifiedSet = tmpSet.stream().filter(integer -> !toBeTaken.contains(integer)).collect(Collectors.toList());
                if(!modifiedSet.isEmpty())
                {
                    newSetOfSets.add(modifiedSet);
                    fixIndexesMap.put(j, i);
                    j++;
                }
            }
            i++;
        }
        return newSetOfSets;
    }

    protected List<List<Integer>> Remove(List<List<Integer>> setOfSets, List<Integer> toBeTaken, HashMap<Integer, Integer> fixIndexesMap)
    {
        List<List<Integer>> newSetOfSets = new ArrayList<>();
        int j = 0;
        int i = 0;
        for(List<Integer> tmpSet : setOfSets)
        {
            if(tmpSet != toBeTaken)
            {
                newSetOfSets.add(tmpSet);
                fixIndexesMap.put(j, i);
                j++;
            }
            i++;
        }
        return newSetOfSets;
    }

    protected List<Integer> GetMaxCardinality(List<List<Integer>> setOfSets)
    {
        List<Integer> maxCardinality = setOfSets.get(0);
        for(List<Integer> tmpSet : setOfSets)
            if(tmpSet.size() > maxCardinality.size())
                maxCardinality = tmpSet;
        return maxCardinality;
    }

    protected List<Integer> MSC(List<List<Integer>> setOfSets, List<Integer> universe)
    {
        if(setOfSets.isEmpty())
        {
            if(universe.isEmpty())
                return new ArrayList<>();
            return null;
        }
        List<Integer> maxCardinality = GetMaxCardinality(setOfSets);
        int indexMaxCardinality = setOfSets.indexOf(maxCardinality);

        HashMap<Integer, Integer> fixIndexesMap = new HashMap<>();
        List<Integer> newUniverse = universe.stream().filter(integer -> !maxCardinality.contains(integer)).collect(Collectors.toList());
        List<List<Integer>> newSetOfSets = Take(setOfSets, maxCardinality, fixIndexesMap);
        List<Integer> C1 = MSC(newSetOfSets, newUniverse);
        if(C1 != null)
        {
            RestoreSolution(C1, fixIndexesMap);
            C1.add(indexMaxCardinality);
        }

        fixIndexesMap = new HashMap<>();
        newUniverse = universe;
        newSetOfSets = Remove(setOfSets, maxCardinality, fixIndexesMap);
        List<Integer> C2 = MSC(newSetOfSets, newUniverse);
        if(C2 != null)
            RestoreSolution(C2, fixIndexesMap);

        if(C1 == null && C2 == null)
            return null;
        if(C1 == null)
            return C2;
        if(C2 == null)
            return C1;
        return C1.size() > C2.size() ? C2 : C1;
    }

    @Override
    public boolean Solve()
    {
        try
        {
            long timeStart = System.nanoTime();
            Graph currentGraph = Graph.GetInstance();
            List<Integer> universe = new ArrayList<>();
            for(Node currentNode : currentGraph.GetAllNodes())
                universe.add(currentNode.GetId());
            List<List<Integer>> setOfSets = SetCoverUtil.CreateSetsFromGraph(currentGraph, true);
            List<Integer> solution = MSC(setOfSets, universe);
            if(solution != null)
            {
                int i = 0;
                for(List<Integer> set : setOfSets)
                {
                    if(solution.contains(i))
                        dominatingSet.add(SetCoverUtil.GetNodeFromSet(set));
                    i++;
                }
            }
            timeSpentSolving = System.nanoTime() - timeStart;
            return true;
        }
        catch(Exception e)
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
        return "Basic set cover";
    }

    @Override
    public void Reset()
    {
        dominatingSet = new ArrayList<>();
        timeSpentSolving = -1;
    }
}
