package graph;

import java.awt.Label;
import java.util.ArrayList;

public class Graph {
    public String name;
    public ArrayList<Node> nodes = new ArrayList<Node>(); // 存储该图所有节点
    public ArrayList<Edge> edges = new ArrayList<Edge>(); // 存储该图所有边

    private int[][] adjacencyMatrix; // 邻接矩阵
    private boolean adjacencyMatrixUpdateNeeded = true; // 判断邻接矩阵是否需要更新

    public Graph(String name) {
        this.name = name;
    }

    public void addNode(int id, int label) {
        nodes.add(new Node(this, id, label));
    }

    public void addEdge(Node source, Node target, int label) {
        edges.add(new Edge(this, source, target, label));
    }

    public void addEdge(int sourceId, int targetId, int label) {
        this.addEdge(this.nodes.get(sourceId), this.nodes.get(targetId), label);
    }

    //返回邻接矩阵
    public int[][] getAdjacencyMatrix() {

        if (this.adjacencyMatrixUpdateNeeded) {

            int k = this.nodes.size();
            this.adjacencyMatrix = new int[k][k];
            for (int i = 0 ; i < k ; i++)
                for (int j = 0 ; j < k ; j++)
                    this.adjacencyMatrix[i][j] = -1;

            for (Edge e : this.edges) {
                this.adjacencyMatrix[e.source.id][e.target.id] = e.label;
            }
            this.adjacencyMatrixUpdateNeeded = false;
        }
        return this.adjacencyMatrix;
    }
}