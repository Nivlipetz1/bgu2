package Stock.common;

import java.util.HashMap;

public class Tree {

    private HashMap<Integer, Node> nodes;

    public Tree() 
    {
        this.nodes = new HashMap<Integer, Node>();
    }

    public HashMap<Integer, Node> getNodes() {
        return nodes;
    }
    public Node addNode(int identifier, Category c) {
        return this.addNode(identifier, 0, c);
    }
    public Node addNode(int identifier, int parent, Category c) {
        Node node = new Node(identifier, c);
        nodes.put(identifier, node);
        if(parent!=0)
        	nodes.get(parent).addChild(identifier);
        return node;
    }

}
