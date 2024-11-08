package MainPackage.org.MainOfBetweennessCentrality.org;

import Attack_Defence_Graph.org.Graph;
import Attack_Defence_Graph.org.GraphData;
import ConcurrentAttacks.org.ConcurrentAttack;
import CostFunction.org.CostFunction;
import Defender.org.Defenders;
import GraphAnalysisMethods.org.MarkovBlanket;
import GraphCentralityMethods.org.BetweennessCentralityMethod;

import java.util.Collection;
import java.util.Objects;

public class BetweennessCentralityWithMBMain {
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

        // Asset loss vector
        var assetLossVector = task.getNodeAssetsLossValues();

        // Set the spare budget of security resources for each defender
        Defenders.spareBudget_D = 10d;

        var startTime = System.currentTimeMillis();

        // This code segment is referred to rank each asset according to betweenness centrality algorithm
        var betweennessCentrality = new BetweennessCentralityMethod(AdjMat, assetLossVector);
        var scores = betweennessCentrality.getScores();

        // This code segment is referred to capture the connections among each asset and its Markov blanket nodes
        var MarkovBlanket = new MarkovBlanket(AdjMat, assetLossVector);
        var nodes = MarkovBlanket.retrieveNodeOfMarkovBlanket();

        // Set the spare budget of security resources for each defender
        var budget = Defenders.spareBudget_D;

        // This code segment is referred to allocate the resources according to each assets' rank and its Markov blanket
        for (var assetNode : nodes.keySet()) {
            var allNeighbors = nodes.get(assetNode);
            for (var type : allNeighbors.keySet()) {
                var nodeList = allNeighbors.get(type);
                for (var nod : nodeList) {
                    var edge = (Objects.equals(type, "Children")) ? attackDefenceGraph[assetNode - 1][nod - 1] :
                            attackDefenceGraph[nod - 1][assetNode - 1];
                    var sizeOfCapturedNodesByMB = nodes.get(assetNode).values().stream().
                            flatMap(Collection::stream).toList().size();
                    var currentAssetCutOfTotalBudget = budget * scores.get(assetNode) / sizeOfCapturedNodesByMB;
                    edge.setInvest_D(edge.addSpareInvestFor_D(currentAssetCutOfTotalBudget));
                    budget -= currentAssetCutOfTotalBudget;
                }
                // System.out.println(assetNode + "\t" + type + "\t" + allNeighbors.get(type));
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

class BetweennessCentralityWithMBAllPathsMain {
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
            var concurrentAttacks = concurrentAttackers.getAllPaths();

            // Calculate the cost for the generated paths before allocating the resources
            var assets = concurrentAttacks.keySet();
            var costFunctionBeforeAllocation = new CostFunction(AdjMat, assetLossVec);
            var CostsBeforeAllocation = 0.0d;
            for (var asset : assets) {
                var pathsToThisAsset = concurrentAttacks.get(asset);
                var cost = 0.0;
                for (var paths : pathsToThisAsset) {
                    for (var path : paths) {
                        cost += costFunctionBeforeAllocation.computeCost(path);
                    }
                }
                CostsBeforeAllocation += cost / concurrentAttacks.get(asset).size();
                CostsBeforeAllocation += CostsBeforeAllocation / pathsToThisAsset.size();
            }
            System.out.println("Before the allocation, the cost of these attacks approaching the assets successfully is : "
                    + CostsBeforeAllocation);

            var startTime = System.currentTimeMillis();

            // Set the spare budget of security resources for each defender
            Defenders.spareBudget_D = 10d;

            // This code segment is referred to rank each asset according to betweenness centrality algorithm
            var betweennessCentrality = new BetweennessCentralityMethod(AdjMat, assetLossVec);
            var scores = betweennessCentrality.getScores();

            // This code segment is referred to capture the connections among each asset and its Markov blanket nodes
            var MarkovBlanket = new MarkovBlanket(AdjMat, assetLossVec);
            var nodes = MarkovBlanket.retrieveNodeOfMarkovBlanket();

            // Set the spare budget of security resources for each defender
            var budget = Defenders.spareBudget_D;

            // This code segment is referred to allocate the resources according to each assets' rank and its Markov blanket
            for (var assetNode : nodes.keySet()) {
                var allNeighbors = nodes.get(assetNode);
                for (var type : allNeighbors.keySet()) {
                    var nodeList = allNeighbors.get(type);
                    for (var nod : nodeList) {
                        var edge = (Objects.equals(type, "Children")) ? attackDefenceGraph[assetNode - 1][nod - 1] :
                                attackDefenceGraph[nod - 1][assetNode - 1];
                        var sizeOfCapturedNodesByMB = nodes.get(assetNode).values().stream().
                                flatMap(Collection::stream).toList().size();
                        var currentAssetCutOfTotalBudget = budget * scores.get(assetNode) / sizeOfCapturedNodesByMB;
                        edge.setInvest_D(edge.addSpareInvestFor_D(currentAssetCutOfTotalBudget));
                        budget -= currentAssetCutOfTotalBudget;
                    }
                    // System.out.println(assetNode + "\t" + type + "\t" + allNeighbors.get(type));
                }
            }
            var endTime = System.currentTimeMillis();
            // Update the adjacency matrix
            AdjMat = task.getAdjacencyMatrix(attackDefenceGraph);
            var costFunctionAfterAllocation = new CostFunction(AdjMat, assetLossVec);
            var CostsAfterAllocation = 0.0d;
            for (var asset : assets) {
                var pathsToThisAsset = concurrentAttacks.get(asset);
                var cost = 0.0;
                for (var paths : pathsToThisAsset) {
                    for (var path : paths) {
                        cost += costFunctionAfterAllocation.computeCost(path);
                    }
                }
                CostsAfterAllocation += cost / concurrentAttacks.get(asset).size();
                CostsAfterAllocation += CostsAfterAllocation / pathsToThisAsset.size();
            }
            System.out.println("After the allocation, the cost of these attacks approaching the assets successfully is : "
                    + CostsAfterAllocation);
            System.out.println("The relative reduction of the cost of these attacks approaching the assets successfully is : "
                    + Math.abs(CostsBeforeAllocation - CostsAfterAllocation) / CostsBeforeAllocation * 100 + "\t%");
            System.out.println("The duration time of the allocation process took : " + (endTime - startTime) + " ms");
    }
}
