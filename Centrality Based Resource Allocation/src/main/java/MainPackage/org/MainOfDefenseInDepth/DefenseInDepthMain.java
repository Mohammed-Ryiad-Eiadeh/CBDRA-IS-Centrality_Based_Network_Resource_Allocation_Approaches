package MainPackage.org.MainOfDefenseInDepth;

import Attack_Defence_Graph.org.Graph;
import Attack_Defence_Graph.org.GraphData;
import ConcurrentAttacks.org.ConcurrentAttack;
import CostFunction.org.CostFunction;
import Defender.org.Defenders;

/**
 * This class is used as the main class for the defense in depth
 * <pre>
 *     Validating and Restoring Defense in Depth Using Attack Graphs, Richard Lippmann et all, 2006
 * </pre>
 */
public class DefenseInDepthMain {
    public static void main(String[] args) {
        // Select the test case or the graph; construct the defenders; construct the adjacency matrix; display the graph.
        var task = new GraphData(Graph.SCADA_rand);
        var attackDefenceGraph = task.getAttackDefenceGraph();
        var AdjMat = task.getAdjacencyMatrix(attackDefenceGraph);
        var assetLossVec = task.getNodeAssetsLossValues();
        //task.Display(AdjMat);

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
        // This code segment is referred to get the number of all edges in the graph
        var numOfEdges = task.getNumberOfEdges();
        var budget = Defenders.spareBudget_D / numOfEdges;

        // This code segment is referred to allocate spare defenders investments on all edges equally
        for (var i = 0; i < AdjMat.length; i++) {
            for (var j = 0; j < AdjMat[0].length; j++) {
                if (attackDefenceGraph[i][j].totalInvest() > 0) {
                    var edge = attackDefenceGraph[i][j];
                    edge.setInvest_D(edge.addSpareInvestFor_D(budget));
                }
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

class DefenseInDepthAllPathsMain {
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
        for (var asset : assets) {
            var pathsToThisAsset = concurrentAttacks.get(asset);
            var cost = 0.0;
            for (var paths : pathsToThisAsset) {
                for (var path : paths) {
                    cost += costFunctionBeforeAllocation.computeCost(path);
                }
            }
            costsBeforeAllocation += cost / concurrentAttacks.get(asset).size();
            costsBeforeAllocation += costsBeforeAllocation / pathsToThisAsset.size();
        }
        System.out.println("Before the allocation, the cost of these attacks approaching the assets successfully is : "
                + costsBeforeAllocation);

        // Set the spare budget of security resources for each defender
        Defenders.spareBudget_D = 10d;

        var startTime = System.currentTimeMillis();
        // This code segment is referred to get the number of all edges in the graph
        var numOfEdges = task.getNumberOfEdges();
        var budget = Defenders.spareBudget_D / numOfEdges;

        // This code segment is referred to allocate spare defenders investments on all edges equally
        for (var i = 0; i < AdjMat.length; i++) {
            for (var j = 0; j < AdjMat[0].length; j++) {
                if (attackDefenceGraph[i][j].totalInvest() > 0) {
                    var edge = attackDefenceGraph[i][j];
                    edge.setInvest_D(edge.addSpareInvestFor_D(budget));
                }
            }
        }
        var endTime = System.currentTimeMillis();
        // Update the adjacency matrix
        AdjMat = task.getAdjacencyMatrix(attackDefenceGraph);
        var costFunctionAfterAllocation = new CostFunction(AdjMat, assetLossVec);
        var costsAfterAllocation = 0.0d;
        for (var asset : assets) {
            var pathsToThisAsset = concurrentAttacks.get(asset);
            var cost = 0.0;
            for (var paths : pathsToThisAsset) {
                for (var path : paths) {
                    cost += costFunctionAfterAllocation.computeCost(path);
                }
            }
            costsAfterAllocation += cost / concurrentAttacks.get(asset).size();
            costsAfterAllocation += costsAfterAllocation / pathsToThisAsset.size();
        }
        System.out.println("After the allocation, the cost of these attacks approaching the assets successfully is : "
                + costsAfterAllocation);
        System.out.println("The relative reduction of the cost of these attacks approaching the assets successfully is : "
                + Math.abs(costsBeforeAllocation - costsAfterAllocation) / costsBeforeAllocation * 100 + "\t%");
    }
}