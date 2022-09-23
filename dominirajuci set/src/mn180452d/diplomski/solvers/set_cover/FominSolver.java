package mn180452d.diplomski.solvers.set_cover;

import EdmondsMatchingKeithSchwarz.EdmondsMatching;
import EdmondsMatchingKeithSchwarz.UndirectedGraph;
import mn180452d.diplomski.Node;

import java.util.*;
import java.util.stream.Collectors;

public class FominSolver extends TrivialSolver
{
    protected List<Integer> BranchOne(List<List<Integer>> setOfSets, List<Integer> universe)
    {
        int indexOfSetWithFrequencyOneElement = -1;
        List<Integer> SetWithFrequencyOneElement = null;
        Set<Integer> realUniverse = new HashSet<>();
        for(List<Integer> tmpSet:setOfSets)
            realUniverse.addAll(tmpSet);
        for (Integer e : realUniverse)
        {
            int frequency = 0;
            int i = 0;
            for (List<Integer> set : setOfSets)
            {
                if (set.contains(e))
                {
                    frequency++;
                    indexOfSetWithFrequencyOneElement = i;
                    SetWithFrequencyOneElement = set;
                }
                i++;
            }
            if (frequency == 1)
                break;
            indexOfSetWithFrequencyOneElement = -1;
            SetWithFrequencyOneElement = null;
        }
        if (indexOfSetWithFrequencyOneElement != -1)
        {
            final List<Integer> finalSetWithFrequencyOneElement = SetWithFrequencyOneElement;
            HashMap<Integer, Integer> fixIndexesMap = new HashMap<>();
            List<Integer> newUniverse = universe.stream().filter(integer -> !finalSetWithFrequencyOneElement.contains(integer)).collect(Collectors.toList());
            List<List<Integer>> newSetOfSets = Take(setOfSets, SetWithFrequencyOneElement, fixIndexesMap);
            List<Integer> setCover = MSC(newSetOfSets, newUniverse);
            if (setCover != null)
            {
                RestoreSolution(setCover, fixIndexesMap);
                setCover.add(indexOfSetWithFrequencyOneElement);
            }
            return setCover;
        }
        return null;
    }

    protected List<Integer> BranchTwo(List<List<Integer>> setOfSets, List<Integer> universe)
    {
        List<Integer> Q = null;
        List<Integer> R = null;
        for (List<Integer> setI : setOfSets)
        {
            for (List<Integer> setJ : setOfSets)
                if (setI != setJ && setI.containsAll(setJ))
                {
                    Q = setI;
                    R = setJ;
                    break;
                }
            if (Q != null)
                break;
        }
        if (Q != null)
        {
            HashMap<Integer, Integer> fixIndexesMap = new HashMap<>();
            List<List<Integer>> newSetOfSets = Remove(setOfSets, R, fixIndexesMap);
            List<Integer> setCover = MSC(newSetOfSets, universe);
            if (setCover != null)
                RestoreSolution(setCover, fixIndexesMap);
            return setCover;
        }
        return null;
    }

    protected List<Integer> BranchThree(List<List<Integer>> setOfSets, List<Integer> universe)
    {
        List<Integer> maxCardinality =GetMaxCardinality(setOfSets);
        if (maxCardinality.size() <= 2)
        {
            UndirectedGraph<Integer> graphForEdmondsMatching = new UndirectedGraph<>();
            for (Integer tmp : universe)
                graphForEdmondsMatching.addNode(tmp);
            for (List<Integer> tmp : setOfSets)
                if (tmp.size() == 2)
                    graphForEdmondsMatching.addEdge(tmp.get(0), tmp.get(1));
            graphForEdmondsMatching = EdmondsMatching.maximumMatching(graphForEdmondsMatching);

            List<Integer> setCover = new ArrayList<>();
            Set<Set<Integer>> sets = new HashSet<>();
            HashMap<Set<Integer>, Integer> indexMap = new HashMap<>();
            int i = 0;
            for (List<Integer> tmpList : setOfSets)
            {
                Set<Integer> tmpSet = new HashSet<>(tmpList);
                if (!sets.contains(tmpSet))
                {
                    indexMap.put(tmpSet, i);
                    sets.add(tmpSet);
                }
                i++;
            }
            for (Integer vertex : graphForEdmondsMatching)
            {
                Set<Integer> edges = new HashSet<>(graphForEdmondsMatching.edgesFrom(vertex));
                edges.add(vertex);
                for (Set<Integer> check : sets)
                {
                    if (check.equals(edges) && !setCover.contains(indexMap.get(check)))
                        setCover.add(indexMap.get(check));
                }
            }
            return setCover;
        }
        return null;
    }

    protected List<Integer> CallTrivialSolutionCheck(List<List<Integer>> setOfSets, List<Integer> universe)
    {
        return super.MSC(setOfSets, universe);
    }

    @Override
    protected List<Integer> MSC(List<List<Integer>> setOfSets, List<Integer> universe)
    {
        if (setOfSets.isEmpty())
        {
            if (universe.isEmpty())
                return new ArrayList<>();
            return null;
        }
        List<Integer> setCover;

        setCover = BranchOne(setOfSets, universe);
        if(setCover != null)
            return setCover;

        setCover = BranchTwo(setOfSets, universe);
        if(setCover != null)
            return setCover;

        setCover = BranchThree(setOfSets, universe);
        if(setCover != null)
            return setCover;

        return CallTrivialSolutionCheck(setOfSets, universe);
    }

    @Override
    public String toString()
    {
        return "Fomin set cover algorithm";
    }
}
