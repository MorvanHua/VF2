package graph;

public class Edge {
    public Graph graph;

    public Node source; //起点
    public Node target; //终点
    public int label;

    //创建新边
    public Edge(Graph g, Node source, Node target, int label) {
        this.graph = g;
        this.source = source;
        source.outEdges.add(this); //将其作为起点外边
        this.target = target; //
        target.inEdges.add(this); //将其作为终点内边
        this.label = label;
    }
}
