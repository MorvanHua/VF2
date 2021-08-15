package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import graph.Edge;
import graph.Graph;
import graph.Node;

public class Vf2 {
    //输入查询图和目标图，返回子图集合
    public ArrayList<int[]> matchGraphSetWithQuery(Graph targetGraph, Graph queryGraph) {
        ArrayList<int[]> stateSet = new ArrayList<int[]>();
        State state = new State(targetGraph, queryGraph);
        matchRecursive(state, stateSet, targetGraph, queryGraph);
        return stateSet;
    }

    //匹配
    private void matchRecursive(State state, ArrayList<int[]> stateSet, Graph targetGraph, Graph queryGraph) {

        if (state.depth == queryGraph.nodes.size()) {    // 找到一个匹配子图
            state.matched = true;
            int[] arr = Arrays.copyOf(state.core_2,state.core_2.length);
            stateSet.add(arr);
        } else {    // 继续寻找子图
            ArrayList<Pair<Integer, Integer>> candidatePairs = genCandidatePairs(state, targetGraph, queryGraph);
            for (Pair<Integer, Integer> entry : candidatePairs) {
                if (checkFeasibility(state, entry.getKey(), entry.getValue())) {
                    state.extendMatch(entry.getKey(), entry.getValue()); // 深入下一个可能节点
                    matchRecursive(state, stateSet, targetGraph, queryGraph);
                    state.backtrack(entry.getKey(), entry.getValue()); // 返回前一个状态
                }
            }
        }
    }

    //返回待筛选的匹配对
    private ArrayList<Pair<Integer, Integer>> genCandidatePairs(State state, Graph targetGraph, Graph queryGraph) {
        ArrayList<Pair<Integer, Integer>> pairList = new ArrayList<Pair<Integer, Integer>>();

        if (!state.T1out.isEmpty() && !state.T2out.isEmpty()) { //判断是否有未配对且作为外边的另一端节点
            int queryNodeIndex = -1;
            for (int i : state.T2out) {
                queryNodeIndex = Math.max(i, queryNodeIndex);
            }
            for (int i : state.T1out) {
                pairList.add(new Pair<Integer,Integer>(i, queryNodeIndex));
            }
            return pairList;
        } else if (!state.T1in.isEmpty() && !state.T2in.isEmpty()) { //判断是否有未配对且作为内边的另一端节点
            int queryNodeIndex = -1;
            for (int i : state.T2in) {
                queryNodeIndex = Math.max(i, queryNodeIndex);
            }
            for (int i : state.T1in) {
                pairList.add(new Pair<Integer,Integer>(i, queryNodeIndex));
            }
            return pairList;
        } else { //无起点，无终点，从未配对节点集合中配对
            int queryNodeIndex = -1;
            for (int i : state.unmapped2) {
                queryNodeIndex = Math.max(i, queryNodeIndex);
            }
            for (int i : state.unmapped1) {
                pairList.add(new Pair<Integer,Integer>(i, queryNodeIndex));
            }
            return pairList;
        }
    }

    private Boolean checkFeasibility(State state , int targetNodeIndex , int queryNodeIndex) {
        //标签原则，标签必须相同
        if (state.targetGraph.nodes.get(targetNodeIndex).label !=
                state.queryGraph.nodes.get(queryNodeIndex).label){
            return false;
        }

        //将待匹配节点 内边和外边 另一端的节点分成三类:一类是配对的节点，一类是未配对但处理过的节点，一类是完全未处理过的节点
        //前任和后继原则，若一图中待配对节点与已配对节点有内边或外边，则另一图中待配对节点与对应的已配对节点也必须有对应的内边或外边
        if (!checkPredAndSucc(state, targetNodeIndex, queryNodeIndex)){
            return false;
        }

        //起点和终点原则，目标图中待配对节点的内边或外边另一端未配对但已处理的节点个数必须大于或等于查询图
        if (!checkInAndOut(state, targetNodeIndex, queryNodeIndex)){
            return false;
        }

        //检查新节点原则，目标图中待配对节点的内边或外边另一端完全未处理的节点个数必须大于或等于查询图
        if (!checkNew(state, targetNodeIndex, queryNodeIndex)){
            return false;
        }

        return true;
    }

    private Boolean checkPredAndSucc(State state, int targetNodeIndex , int queryNodeIndex) {

        Node targetNode = state.targetGraph.nodes.get(targetNodeIndex);
        Node queryNode = state.queryGraph.nodes.get(queryNodeIndex);
        int[][] targetAdjacency = state.targetGraph.getAdjacencyMatrix();
        int[][] queryAdjacency = state.queryGraph.getAdjacencyMatrix();

        for (Edge e : targetNode.inEdges) {
            if (state.core_1[e.source.id] > -1) {
                if (queryAdjacency[state.core_1[e.source.id]][queryNodeIndex] == -1){
                    return false;
                } else if (queryAdjacency[state.core_1[e.source.id]][queryNodeIndex] != e.label) {
                    return false;
                }
            }
        }

        for (Edge e : queryNode.inEdges) {
            if (state.core_2[e.source.id] > -1) {
                if (targetAdjacency[state.core_2[e.source.id]][targetNodeIndex] == -1){
                    return false;
                } else if (targetAdjacency[state.core_2[e.source.id]][targetNodeIndex] != e.label){
                    return false;
                }
            }
        }

        for (Edge e : targetNode.outEdges) {
            if (state.core_1[e.target.id] > -1) {
                if (queryAdjacency[queryNodeIndex][state.core_1[e.target.id]] == -1){
                    return false;
                } else if (queryAdjacency[queryNodeIndex][state.core_1[e.target.id]] != e.label) {
                    return false;
                }
            }
        }

        for (Edge e : queryNode.outEdges) {
            if (state.core_2[e.target.id] > -1) {
                if (targetAdjacency[targetNodeIndex][state.core_2[e.target.id]] == -1){
                    return false;
                } else if (targetAdjacency[targetNodeIndex][state.core_2[e.target.id]] != e.label) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkInAndOut(State state, int targetNodeIndex , int queryNodeIndex) {

        Node targetNode = state.targetGraph.nodes.get(targetNodeIndex);
        Node queryNode = state.queryGraph.nodes.get(queryNodeIndex);

        int targetPredCnt = 0, targetSucCnt = 0;
        int queryPredCnt = 0, querySucCnt = 0;

        for (Edge e : targetNode.inEdges){
            if (state.inT1in(e.source.id)){
                targetPredCnt++;
            }
        }
        for (Edge e : targetNode.outEdges){
            if (state.inT1in(e.target.id)){
                targetSucCnt++;
            }
        }
        for (Edge e : queryNode.inEdges){
            if (state.inT2in(e.source.id)){
                queryPredCnt++;
            }
        }
        for (Edge e : queryNode.outEdges){
            if (state.inT2in(e.target.id)){
                querySucCnt++;
            }
        }
        if (targetPredCnt < queryPredCnt || targetSucCnt < querySucCnt){
            return false;
        }

        for (Edge e : targetNode.inEdges){
            if (state.inT1out(e.source.id)){
                targetPredCnt++;
            }
        }
        for (Edge e : targetNode.outEdges){
            if (state.inT1out(e.target.id)){
                targetSucCnt++;
            }
        }
        for (Edge e : queryNode.inEdges){
            if (state.inT2out(e.source.id)){
                queryPredCnt++;
            }
        }
        for (Edge e : queryNode.outEdges){
            if (state.inT2out(e.target.id)){
                querySucCnt++;
            }
        }
        if (targetPredCnt < queryPredCnt || targetSucCnt < querySucCnt){
            return false;
        }

        return true;
    }

    private boolean checkNew(State state, int targetNodeIndex , int queryNodeIndex){

        Node targetNode = state.targetGraph.nodes.get(targetNodeIndex);
        Node queryNode = state.queryGraph.nodes.get(queryNodeIndex);

        int targetPredCnt = 0, targetSucCnt = 0;
        int queryPredCnt = 0, querySucCnt = 0;
        for (Edge e : targetNode.inEdges){
            if (state.inN1Tilde(e.source.id)){
                targetPredCnt++;
            }
        }
        for (Edge e : targetNode.outEdges){
            if (state.inN1Tilde(e.target.id)){
                targetSucCnt++;
            }
        }
        for (Edge e : queryNode.inEdges){
            if (state.inN2Tilde(e.source.id)){
                queryPredCnt++;
            }
        }
        for (Edge e : queryNode.outEdges){
            if (state.inN2Tilde(e.target.id)){
                querySucCnt++;
            }
        }
        if (targetPredCnt < queryPredCnt || targetSucCnt < querySucCnt){
            return false;
        }

        return true;
    }
}