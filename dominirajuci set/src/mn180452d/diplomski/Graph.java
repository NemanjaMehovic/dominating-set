package mn180452d.diplomski;

import java.io.File;
import java.util.*;

public class Graph
{
    private static Graph instance = null;

    public static Graph InitializeGraph(String InputFileName)
    {
        Node.ResetIdCounter();
        instance = new Graph(InputFileName);
        return instance;
    }

    public static Graph GetInstance() throws Exception
    {
        if (instance == null)
            throw new Exception("No graph initialized.");
        return instance;
    }

    private HashMap<String, Node> allNodes;
    private List<Set<Node>> allEdges;

    private Graph(String InputFileName)
    {
        allNodes = new HashMap<>();
        allEdges = new ArrayList<>();
        File file = new File(InputFileName);
        if (!file.exists())
        {
            System.err.println("File " + InputFileName + " doesn't exist.");
            return;
        }
        try (Scanner input = new Scanner(file))
        {
            int numberOfEdges = 0;
            while (input.hasNextLine())
            {
                String data = input.nextLine();
                String[] ids = data.split(" ");
                if (ids.length != 2)
                    throw new Exception("Input file bad format.");
                if (ids[0].equals(ids[1]))
                    continue;
                Node firstNode = GetCreateNode(ids[0]);
                Node secondNode = GetCreateNode(ids[1]);
                if (firstNode.AddNeighbour(secondNode))
                {
                    numberOfEdges++;
                    Set<Node> newEdge = new HashSet<>();
                    newEdge.add(firstNode);
                    newEdge.add(secondNode);
                    allEdges.add(newEdge);
                    secondNode.AddNeighbour(firstNode);
                }
            }
        }
        catch (Exception e)
        {
            allNodes = new HashMap<>();
            allEdges = new ArrayList<>();
            e.printStackTrace();
        }
    }

    private Node GetCreateNode(String id)
    {
        Node tmp;
        if (allNodes.containsKey(id))
            tmp = allNodes.get(id);
        else
        {
            tmp = new Node(id);
            allNodes.put(id, tmp);
        }
        return tmp;
    }

    public int GetNumberOfEdges()
    {
        return allEdges.size();
    }

    public List<Node> GetAllNodes()
    {
        return Collections.unmodifiableList(new ArrayList<>(allNodes.values()));
    }

    public List<Set<Node>> GetAllEdges()
    {
        return Collections.unmodifiableList(allEdges);
    }
}
