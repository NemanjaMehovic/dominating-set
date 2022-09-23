package mn180452d.diplomski;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node
{
    private static int globalIdCounter = 0;

    public static void ResetIdCounter()
    {
        globalIdCounter = 0;
    }

    private int id;
    private String name;
    private List<Node> connected;
    private boolean isDominated;

    public Node(String name)
    {
        this.name = name;
        this.connected = new ArrayList<>();
        this.id = globalIdCounter++;
    }

    public boolean AddNeighbour(Node neighbour)
    {
        if (connected.contains(neighbour))
            return false;
        connected.add(neighbour);
        return true;
    }

    public int NumberOfNeighbours()
    {
        return connected.size();
    }

    public String GetName()
    {
        return name;
    }

    public List<Node> GetConnected()
    {
        return Collections.unmodifiableList(connected);
    }

    public int GetId()
    {
        return id;
    }

    public void SetDominated(boolean isDominated)
    {
        this.isDominated = isDominated;
    }

    public int GetHowManyWouldBeDominated()
    {
        int count = isDominated ? 0 : 1;
        for(Node neighbour : connected)
            if(!neighbour.isDominated)
                count++;
        return count;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(name);
        builder.append(" -> ");
        for(Node tmp : connected)
            builder.append(tmp.name).append(" ");
        return builder.toString();
    }
}
