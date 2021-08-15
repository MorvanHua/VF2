package graph;

import java.util.ArrayList;

public class Node {
    public Graph graph;

    public int id ; //节点的编号
    public int label;

    public ArrayList<Edge> outEdges = new ArrayList<Edge>(); // 存储该节点的外边
    public ArrayList<Edge> inEdges = new ArrayList<Edge>(); // 存储该节点的内边

    //创建新节点
    public Node(Graph g, int id, int label) {
        this.graph = g;
        this.id = id;
        this.label = label;
    }
}
