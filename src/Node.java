import java.util.HashSet;

public class Node {
    private String data;
    private Node parent;
    private HashSet<Node> adjNodes;
    private boolean isVisited;

    public Node(String data) { // constructs new node with String data and HashSet of Nodes adjNodes
        this.data = data;
        adjNodes = new HashSet<Node>();
    }

    public String data() { // gets data of this node
        return data;
    }

    public Node parent() {
        return parent;
    }

    public void setParent(Node p) {
        parent = p;
    }

    public HashSet<Node> adjNodes() { // gets adjacent nodes to this node
        return adjNodes;
    }

    public boolean isVisited() { // gets whether node is visited or not
        return isVisited;
    }

    public void visit() {
        isVisited = true;
    }

    @Override
    public boolean equals(Object o) { // equality must be OVERRIDEN, not overloaded - so need to use Object as param
        if (!(o instanceof Node)) { // if object is not a Node, return false
            return false;
        }
        Node n = (Node) o; // cast Object o to Node n; guaranteed to work since we checked if o was a Node
        return this.data.equals(n.data); // this was the issue! Nodes were not seen as equal because data did not refer
                                         // to same place in memory... only needs to refer to same series of chars
    }

    @Override
    public int hashCode() { // sets nodes with same data to have same hashCode
        return data.hashCode();
    }
}

// efficient way to associate 2 values with one another
// hashmap is like a digraph

// a------------b
// |
// c

// use hashmap to associate each Node with array of adjacent Nodes
// (Node node, Node[] adjNodes)