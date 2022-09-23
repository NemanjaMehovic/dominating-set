package mn180452d.diplomski;

import mn180452d.diplomski.solvers.AllCombinationsSolver;
import mn180452d.diplomski.solvers.GreedySolver;
import mn180452d.diplomski.solvers.Solver;
import mn180452d.diplomski.solvers.set_cover.FominSolver;
import mn180452d.diplomski.solvers.set_cover.RooijSolver;
import mn180452d.diplomski.solvers.set_cover.TrivialSolver;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Main
{
    private static final StringBuilder stringBuilder = new StringBuilder();

    private static void print()
    {
        print("", true);
    }

    private static void print(String s)
    {
        print(s, true);
    }

    private static void print(String s, boolean flag)
    {
        stringBuilder.append(s);
        System.out.print(s);
        if (flag)
        {
            stringBuilder.append(System.lineSeparator());
            System.out.println();
        }
    }

    private static void saveToFile(String fileName)
    {
        File file = new File(fileName);
        file = new File("results\\" + file.getName() + System.currentTimeMillis() + ".log");
        try (FileOutputStream out = new FileOutputStream(file))
        {
            out.write(stringBuilder.toString().getBytes());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void PrintSolverResult(Solver solver)
    {
        print(solver.toString());
        solver.Solve();
        List<Node> tmp = solver.GetDominatingSet();
        for (Node tmpNode : tmp)
            print(tmpNode.toString());
        print("Time in nanoseconds : " + solver.GetTimeSpent());
        print();
    }

    public static void main(String[] args)
    {
        try
        {
            if (args.length < 1)
            {
                System.out.println("Enter file location that contains a graph as program argument.");
                return;
            }
            Graph.InitializeGraph(args[0]);

            print(args[0]);
            List<Node> allNodes = Graph.GetInstance().GetAllNodes();
            Node maxDegreesNode = allNodes.get(0);
            double avgNumberOfNeighbours = 0;
            for (Node tmpNode : allNodes)
            {
                print(tmpNode.toString());
                if (tmpNode.NumberOfNeighbours() > maxDegreesNode.NumberOfNeighbours())
                    maxDegreesNode = tmpNode;
                avgNumberOfNeighbours += tmpNode.NumberOfNeighbours();
            }
            avgNumberOfNeighbours = avgNumberOfNeighbours / allNodes.size();
            print("Number of nodes: " + allNodes.size());
            print("Number of edges: " + Graph.GetInstance().GetNumberOfEdges());
            print("Maximum degrees: " + maxDegreesNode.GetConnected().size());
            print("Average degrees: " + avgNumberOfNeighbours);


            final int NUMBER_OF_ITERATIONS = 11;
            Solver[] solvers = {new GreedySolver(), new FominSolver(), new RooijSolver(), new TrivialSolver(), new AllCombinationsSolver()};
            long[][] timeValues = new long[solvers.length][NUMBER_OF_ITERATIONS];
            for (int i = 0; i < solvers.length; i++)
            {
                print("Solving: " + solvers[i].toString());
                for (int j = 0; j < NUMBER_OF_ITERATIONS; j++)
                {
                    if (Graph.GetInstance().GetAllNodes().size() <= 40 || i < (solvers.length - 2))
                    {
                        print("Iteration: " + j);
                        print("Time: " + System.currentTimeMillis());
                        if (j != (NUMBER_OF_ITERATIONS - 1))
                            solvers[i].Solve();
                        else
                            PrintSolverResult(solvers[i]);
                        timeValues[i][j] = solvers[i].GetTimeSpent();
                        solvers[i].Reset();
                    }
                    else
                        timeValues[i][j] = Long.MAX_VALUE;
                }
            }
            long[][] timeValuesAccountForJit = new long[solvers.length][NUMBER_OF_ITERATIONS - 1];
            for (int i = 0; i < solvers.length; i++)
                System.arraycopy(timeValues[i], 1, timeValuesAccountForJit[i], 0, NUMBER_OF_ITERATIONS - 1);

            for (int i = 0; i < solvers.length; i++)
                Arrays.sort(timeValuesAccountForJit[i]);

            for (int i = 0; i < solvers.length; i++)
            {
                print(solvers[i].toString());
                double avg = 0;
                boolean flag = true;
                for (int j = 0; j < (NUMBER_OF_ITERATIONS - 1); j++)
                {
                    if (timeValuesAccountForJit[i][j] != Long.MAX_VALUE)
                    {
                        print(timeValuesAccountForJit[i][j] + " ", false);
                        avg += timeValuesAccountForJit[i][j];
                    }
                    else
                    {
                        flag = false;
                        break;
                    }
                }
                if (flag)
                {
                    print();
                    avg = avg / (NUMBER_OF_ITERATIONS - 1);
                    int medianIndex1 = 0;
                    int medianIndex2 = 0;
                    if((NUMBER_OF_ITERATIONS - 1) % 2 == 0)
                    {
                        medianIndex1 = (NUMBER_OF_ITERATIONS - 1)/2;
                        medianIndex2 = medianIndex1 - 1;
                    }
                    else
                        medianIndex1 = medianIndex2 = (NUMBER_OF_ITERATIONS - 1)/2;
                    double median = (timeValuesAccountForJit[i][medianIndex1] + timeValuesAccountForJit[i][medianIndex2]) / 2.0;
                    print(String.format("Median value: %f", median));
                    print(String.format("Average value: %f", avg));
                    print("Min value: " + timeValuesAccountForJit[i][0]);
                }
                else
                    print("Graph is too large to find a median, average or min value for using this method.");
                print();
            }
            saveToFile(args[0]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
