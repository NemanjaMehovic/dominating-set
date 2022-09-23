package mn180452d.diplomski.solvers.set_cover;


import java.util.*;
import java.util.stream.Collectors;

public class RooijSolver extends FominSolver
{

    private HashMap<Integer, Integer> FrequencyMapCalc(List<List<Integer>> setOfSets, List<Integer> universe)
    {
        HashMap<Integer, Integer> frequencyMap = new HashMap<>();
        Set<Integer> realUniverse = new HashSet<>();
        for (List<Integer> tmpSet : setOfSets)
            realUniverse.addAll(tmpSet);
        for (Integer e : realUniverse)
        {
            int frequency = 0;
            for (List<Integer> set : setOfSets)
                if (set.contains(e))
                    frequency++;
            frequencyMap.put(e, frequency);
        }
        return frequencyMap;
    }

    protected List<Integer> BranchFour(List<List<Integer>> setOfSets, List<Integer> universe)
    {
        HashMap<Integer, Set<Set<Integer>>> subSetOfElements = new HashMap<>();
        for (Integer e : universe)
        {
            Set<Set<Integer>> setOfE = new HashSet<>();
            for (List<Integer> tmpSet : setOfSets)
                if (tmpSet.contains(e))
                {
                    Set<Integer> containsE = new HashSet<>(tmpSet);
                    setOfE.add(containsE);
                }
            subSetOfElements.put(e, setOfE);
        }
        Integer elementToRemove = null;
        for (Integer e1 : universe)
        {
            Set<Set<Integer>> subSetOfE1 = subSetOfElements.get(e1);
            for (Integer e2 : universe)
            {
                Set<Set<Integer>> subSetOfE2 = subSetOfElements.get(e2);
                if (e1 != e2 && subSetOfE2.containsAll(subSetOfE1))
                {
                    elementToRemove = e2;
                    break;
                }
            }
            if (elementToRemove != null)
                break;
        }
        if (elementToRemove != null)
        {
            HashMap<Integer, Integer> fixIndexesMap = new HashMap<>();
            List<Integer> toRemove = new ArrayList<>();
            toRemove.add(elementToRemove);
            List<Integer> newUniverse = universe.stream().filter(integer -> !toRemove.contains(integer)).collect(Collectors.toList());
            List<List<Integer>> newSetOfSets = Take(setOfSets, toRemove, fixIndexesMap);
            List<Integer> setCover = MSC(newSetOfSets, newUniverse);
            if (setCover != null)
                RestoreSolution(setCover, fixIndexesMap);
            return setCover;
        }
        return null;
    }

    protected List<Integer> BranchFive(List<List<Integer>> setOfSets, List<Integer> universe)
    {
        HashMap<Integer, Integer> frequencyMap = FrequencyMapCalc(setOfSets, universe);
        List<Integer> setToTake = null;
        int indexSetToTake = 0;
        int i = 0;
        for (List<Integer> r : setOfSets)
        {
            Set<Integer> setR = new HashSet<>();
            Set<Integer> unionOfQ = new HashSet<>();
            for (Integer e : r)
                if (frequencyMap.get(e).equals(2))
                {
                    for (List<Integer> tmpSet : setOfSets)
                        if (tmpSet.contains(e))
                            unionOfQ.addAll(tmpSet);
                    setR.add(e);
                }
            unionOfQ = unionOfQ.stream().filter(integer -> !r.contains(integer)).collect(Collectors.toSet());
            if (unionOfQ.size() < setR.size())
            {
                setToTake = r;
                indexSetToTake = i;
                break;
            }
            i++;
        }
        if (setToTake != null)
        {
            HashMap<Integer, Integer> fixIndexesMap = new HashMap<>();
            final List<Integer> finalSetToTake = setToTake;
            List<Integer> newUniverse = universe.stream().filter(integer -> !finalSetToTake.contains(integer)).collect(Collectors.toList());
            List<List<Integer>> newSetOfSets = Take(setOfSets, setToTake, fixIndexesMap);
            List<Integer> setCover = MSC(newSetOfSets, newUniverse);
            if (setCover != null)
            {
                RestoreSolution(setCover, fixIndexesMap);
                setCover.add(indexSetToTake);
            }
            return setCover;
        }
        return null;
    }

    protected List<Integer> BranchSix(List<List<Integer>> setOfSets, List<Integer> universe)
    {
        HashMap<Integer, Integer> frequencyMap = FrequencyMapCalc(setOfSets, universe);
        List<Integer> r = null;
        int rIndex = 0;
        int i = 0;
        for (List<Integer> tmpSet : setOfSets)
        {
            if (tmpSet.size() == 2 && frequencyMap.get(tmpSet.get(0)) == 2 && frequencyMap.get(tmpSet.get(1)) == 2)
            {
                r = tmpSet;
                rIndex = i;
                break;
            }
            i++;
        }
        if (r != null)
        {
            List<Integer> Q = new ArrayList<>();
            List<List<Integer>> R1R2 = new ArrayList<>();
            List<Integer> indexesR1R2 = new ArrayList<>();
            i = 0;
            for (List<Integer> tmpSet : setOfSets)
            {
                if ((tmpSet.contains(r.get(0)) || tmpSet.contains(r.get(1))) && tmpSet != r)
                {
                    Q.addAll(tmpSet);
                    R1R2.add(tmpSet);
                    indexesR1R2.add(i);
                }
                i++;
            }
            Q.remove(r.get(0));
            Q.remove(r.get(1));

            final List<Integer> finalR = r;
            HashMap<Integer, Integer> fixIndexesMap = new HashMap<>();
            List<List<Integer>> newSetOfSets = new ArrayList<>();
            List<Integer> newUniverse = universe.stream().filter(integer -> !finalR.contains(integer)).collect(Collectors.toList());

            i = 0;
            int j = 0;
            for (List<Integer> tmpSet : setOfSets)
            {
                if (tmpSet != r && tmpSet != R1R2.get(0) && tmpSet != R1R2.get(1))
                {
                    newSetOfSets.add(tmpSet);
                    fixIndexesMap.put(j, i);
                    j++;
                }
                i++;
            }

            int QIndex = newSetOfSets.size();
            newSetOfSets.add(Q);

            List<Integer> setCover = MSC(newSetOfSets, newUniverse);
            if (setCover != null)
            {
                if (setCover.contains(QIndex))
                {
                    setCover.remove(new Integer(QIndex));
                    RestoreSolution(setCover, fixIndexesMap);
                    setCover.add(indexesR1R2.get(0));
                    setCover.add(indexesR1R2.get(1));
                }
                else
                {
                    RestoreSolution(setCover, fixIndexesMap);
                    setCover.add(rIndex);
                }
            }
            return setCover;
        }
        return null;
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
        if (setCover != null)
            return setCover;

        setCover = BranchTwo(setOfSets, universe);
        if (setCover != null)
            return setCover;

        setCover = BranchFour(setOfSets, universe);
        if (setCover != null)
            return setCover;

        setCover = BranchFive(setOfSets, universe);
        if (setCover != null)
            return setCover;

        setCover = BranchSix(setOfSets, universe);
        if (setCover != null)
            return setCover;

        setCover = BranchThree(setOfSets, universe);
        if (setCover != null)
            return setCover;

        return CallTrivialSolutionCheck(setOfSets, universe);
    }

    @Override
    public String toString()
    {
        return "Rooij set cover algorithm";
    }
}
