package runner;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import algorithm.*;
import graph.*;

public class Show {

    public static void main(String[] args) throws FileNotFoundException {

        /*Path graphPath = Paths.get("C:\\Users\\刘德华\\Desktop", "grami_paper_graph.lg");
        Path queryPath = Paths.get("C:\\Users\\刘德华\\Desktop", "pattern_grami_paper_1.lg");*/
        Path graphPath = Paths.get("C:\\Users\\刘德华\\Desktop", "queryGraph1.txt");
        Path queryPath = Paths.get("C:\\Users\\刘德华\\Desktop", "queryGraph0.txt");

        Graph targetGraph = loadGraphSetFromFile(graphPath, "targetGraph ");
        Graph queryGraph = loadGraphSetFromFile(queryPath, "queryGraph ");
        Vf2 vf2 = new Vf2();
        ArrayList<int[]> stateSet = vf2.matchGraphSetWithQuery(targetGraph, queryGraph);
        if (stateSet.isEmpty()) {
            System.out.println("Cannot find a map for: " + queryGraph.name);
        } else {
            for(int[] arr2 : stateSet){
                for (int q = 0 ; q < arr2.length ; q++) {
                    System.out.print("(" + arr2[q] + "-" + q + ") ");
                }
                System.out.println();
            }
            System.out.println("Found " + stateSet.size() + " maps for: " + queryGraph.name); //输出匹配到的子图个数
        }
    }

    //加载文件
    private static Graph loadGraphSetFromFile(Path inpath, String namePrefix) throws FileNotFoundException{
        Scanner scanner = new Scanner(inpath.toFile());
        Graph graph = null;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.startsWith("t")) {
                String graphId = line.split(" ")[2];
                graph = new Graph(namePrefix + graphId);
            } else if (line.startsWith("v")) {
                String[] lineSplit = line.split(" ");
                int nodeId = Integer.parseInt(lineSplit[1]);
                int nodeLabel = Integer.parseInt(lineSplit[2]);
                graph.addNode(nodeId, nodeLabel);
            } else if (line.startsWith("e")) {
                String[] lineSplit = line.split(" ");
                int sourceId = Integer.parseInt(lineSplit[1]);
                int targetId = Integer.parseInt(lineSplit[2]);
                int edgeLabel = Integer.parseInt(lineSplit[3]);
                graph.addEdge(sourceId, targetId, edgeLabel);
            } else if (line.equals("")) {
                continue;
            }
        }
        scanner.close();
        return graph;
    }
}

