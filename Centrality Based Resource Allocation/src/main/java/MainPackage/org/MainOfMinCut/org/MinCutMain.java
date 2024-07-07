package MainPackage.org.MainOfMinCut.org;

import Attack_Defence_Graph.org.Graph;
import Attack_Defence_Graph.org.GraphData;
import ConcurrentAttacks.org.ConcurrentAttack;
import CostFunction.org.CostFunction;
import Defender.org.Defenders;
import GraphAnalysisMethods.org.MinCutEdges;
import Severity.org.Severity;

import java.util.ArrayList;

public class MinCutMain {
    public static void main(String[] args) {
        // Select the test case or the graph; construct the defenders; construct the adjacency matrix; display the graph.
        var task = new GraphData(Graph.SCADA_rand);
        var attackDefenceGraph = task.getAttackDefenceGraph();
        var AdjMat = task.getAdjacencyMatrix(attackDefenceGraph);
        var assetLossVec = task.getNodeAssetsLossValues();
        task.Display(AdjMat);

        // Generate paths by genetic algorithm from each entry node to each asset
        var concurrentAttackers = new ConcurrentAttack(AdjMat,
                assetLossVec,
                500,
                0.2,
                0.4,
                0.6,
                100);
        var concurrentAttacks = concurrentAttackers.getPaths();

        // Calculate the cost for the generated paths before allocating the resources
        var assets = concurrentAttacks.keySet();
        var costFunctionBeforeAllocation = new CostFunction(AdjMat, assetLossVec);
        var CostsBeforeAllocation = 0.0d;
        for (var asset : assets) {
            var pathsToThisAsset = concurrentAttacks.get(asset);
            for (var path : pathsToThisAsset) {
                CostsBeforeAllocation += costFunctionBeforeAllocation.computeCost(path);
            }
            CostsBeforeAllocation += CostsBeforeAllocation / pathsToThisAsset.size();
        }
        System.out.println("Before the allocation, the cost of these attacks approaching the assets successfully is : "
                + CostsBeforeAllocation);

        // Set the spare budget of security resources for each defender
        Defenders.spareBudget_D = 10d;

        var startTime = System.currentTimeMillis();
        // This code segment is referred to capture the min-cut edges among each asset-asset
        var MCE = new MinCutEdges(AdjMat, assetLossVec);
        var nodes = MCE.getMinCutEdges();

        // Divide the spare budget of security resources over the number of edges
        var size = nodes.values().stream().mapToInt(ArrayList::size).sum();
        var budget = Defenders.spareBudget_D / size;

        // This code segment is referred to allocate spare defenders investments on paths involved in "in" and "out" degree edges
        for (var assetNode : nodes.keySet()) {
            var parents = nodes.get(assetNode);
            for (var nod : parents) {
                var edge = attackDefenceGraph[nod.GetStart() - 1][nod.GetEnd() - 1];
                edge.setInvest_D(edge.addSpareInvestFor_D(budget));
            }
        }
        var endTime = System.currentTimeMillis();
        // Update the adjacency matrix
        AdjMat = task.getAdjacencyMatrix(attackDefenceGraph);
        var costFunctionAfterAllocation = new CostFunction(AdjMat, assetLossVec);
        var CostsAfterAllocation = 0.0d;
        for (var asset : assets) {
            var pathsToThisAsset = concurrentAttacks.get(asset);
            for (var path : pathsToThisAsset) {
                CostsAfterAllocation += costFunctionAfterAllocation.computeCost(path);
            }
            CostsAfterAllocation += CostsAfterAllocation / pathsToThisAsset.size();
        }
        System.out.println("After the allocation, the cost of these attacks approaching the assets successfully is : "
                + CostsAfterAllocation);
        System.out.println("The relative reduction of the cost of these attacks approaching the assets successfully is : "
                + Math.abs(CostsBeforeAllocation - CostsAfterAllocation) / CostsBeforeAllocation * 100 + "\t%");
        System.out.println("The duration time of the allocation process took : " + (endTime - startTime) + " ms");
    }
}

class MinCutAllPathsMain {
    public static void main(String[] args) {
        // Select the test case or the graph; construct the defenders; construct the adjacency matrix; display the graph.
        var task = new GraphData(Graph.SCADA_rand);
        var attackDefenceGraph = task.getAttackDefenceGraph();
        var AdjMat = task.getAdjacencyMatrix(attackDefenceGraph);
        var assetLossVec = task.getNodeAssetsLossValues();
        task.Display(AdjMat);

        // Generate paths by genetic algorithm from each entry node to each asset
        var concurrentAttackers = new ConcurrentAttack(AdjMat,
                assetLossVec,
                500,
                0.2,
                0.4,
                0.6,
                100);
        var concurrentAttacks = concurrentAttackers.getAllPaths();

        // Calculate the cost and the severity for the generated paths before allocating the resources
        var assets = concurrentAttacks.keySet();
        var costFunctionBeforeAllocation = new CostFunction(AdjMat, assetLossVec);
        var costsBeforeAllocation = 0.0d;
        var severityFunctionBeforeAllocation = new Severity(AdjMat);
        var severityBeforeAllocation = 0.0d;
        for (var asset : assets) {
            var pathsToThisAsset = concurrentAttacks.get(asset);
            var cost = 0.0;
            var severity = 0.0;
            for (var paths : pathsToThisAsset) {
                for (var path : paths) {
                    cost += costFunctionBeforeAllocation.computeCost(path);
                    severity += severityFunctionBeforeAllocation.getSeverity(path);
                }
            }
            costsBeforeAllocation += cost / concurrentAttacks.get(asset).size();
            costsBeforeAllocation += costsBeforeAllocation / pathsToThisAsset.size();
            severityBeforeAllocation += severity / concurrentAttacks.get(asset).size();
            severityBeforeAllocation += severityBeforeAllocation / pathsToThisAsset.size();
        }
        System.out.println("Before the allocation, the cost of these attacks approaching the assets successfully is : "
                + costsBeforeAllocation);
        System.out.println("Before the allocation, the severity of these attacks approaching the assets successfully is : "
                + severityBeforeAllocation);

        // Set the spare budget of security resources for each defender
        Defenders.spareBudget_D = 10d;

        var startTime = System.currentTimeMillis();

        // This code segment is referred to capture the min-cut edges among each asset-asset
        var MCE = new MinCutEdges(AdjMat, assetLossVec);
        var nodes = MCE.getMinCutEdges();

        // Divide the spare budget of security resources over the number of edges
        var size = nodes.values().stream().mapToInt(ArrayList::size).sum();
        var budget = Defenders.spareBudget_D / size;

        // This code segment is referred to allocate spare defenders investments on paths involved in "in" and "out" degree edges
        for (var assetNode : nodes.keySet()) {
            var parents = nodes.get(assetNode);
            for (var nod : parents) {
                var edge = attackDefenceGraph[nod.GetStart() - 1][nod.GetEnd() - 1];
                edge.setInvest_D(edge.addSpareInvestFor_D(budget));
            }
        }
        var endTime = System.currentTimeMillis();
        // Update the adjacency matrix
        AdjMat = task.getAdjacencyMatrix(attackDefenceGraph);
        var costFunctionAfterAllocation = new CostFunction(AdjMat, assetLossVec);
        var costsAfterAllocation = 0.0d;
        var severityFunctionAfterAllocation = new Severity(AdjMat);
        var severityAfterAllocation = 0.0d;
        for (var asset : assets) {
            var pathsToThisAsset = concurrentAttacks.get(asset);
            var cost = 0.0;
            var severity = 0.0;
            for (var paths : pathsToThisAsset) {
                for (var path : paths) {
                    cost += costFunctionAfterAllocation.computeCost(path);
                    severity += severityFunctionAfterAllocation.getSeverity(path);
                }
            }
            costsAfterAllocation += cost / concurrentAttacks.get(asset).size();
            costsAfterAllocation += costsAfterAllocation / pathsToThisAsset.size();
            severityAfterAllocation += severity / concurrentAttacks.get(asset).size();
            severityAfterAllocation += severityAfterAllocation / pathsToThisAsset.size();
        }
        System.out.println("After the allocation, the cost of these attacks approaching the assets successfully is : "
                + costsAfterAllocation);
        System.out.println("The relative reduction of the cost of these attacks approaching the assets successfully is : "
                + Math.abs(costsBeforeAllocation - costsAfterAllocation) / costsBeforeAllocation * 100 + "\t%");

        System.out.println("After the allocation, the severity of these attacks approaching the assets successfully is : "
                + severityAfterAllocation);
        System.out.println("The relative reduction of the severity of these attacks approaching the assets successfully is : "
                + Math.abs(severityBeforeAllocation - severityAfterAllocation) / severityBeforeAllocation * 100 + "\t%");
        System.out.println("The duration time of the allocation process took : " + (endTime - startTime) + " ms");
    }
}