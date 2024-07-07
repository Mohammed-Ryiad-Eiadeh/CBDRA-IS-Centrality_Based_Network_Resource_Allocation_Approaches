package MainPackage.org.MainOFABNR.org;

import Attack_Defence_Graph.org.Graph;
import Attack_Defence_Graph.org.GraphData;
import ConcurrentAttacks.org.ConcurrentAttack;
import CostFunction.org.CostFunction;
import Defender.org.Defenders;
import GraphAnalysisMethods.org.InDegreeNodes;
import GraphCentralityMethods.org.BetweennessCentralityMethod;
import GraphCentralityMethods.org.DegreeCentralityMethod;
import GraphCentralityMethods.org.HarmonicCentralityMethod;
import GraphCentralityMethods.org.katzCentralityMethod;
import RankingAlgorithms.org.TrustRankAlgorithm;

import java.util.HashMap;

public class ABNRWithInDegreeMain {
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

        // This code segment is referred to rank each asset according to degree centrality algorithm
        var degreeCentrality = new DegreeCentralityMethod(AdjMat, assetLossVec);
        var scoresDC = degreeCentrality.getScores();

        // This code segment is referred to rank each asset according to betweenness centrality algorithm
        var betweennessCentrality = new BetweennessCentralityMethod(AdjMat, assetLossVec);
        var scoresBC = betweennessCentrality.getScores();

        // This code segment is referred to rank each asset according to harmonic centrality algorithm
        var harmonicCentrality = new HarmonicCentralityMethod(AdjMat, assetLossVec);
        var scoresHC = harmonicCentrality.getScores();

        // This code segment is referred to rank each asset according to katz centrality algorithm
        var katzCentrality = new katzCentralityMethod(AdjMat, assetLossVec);
        var scoresKC = katzCentrality.getScores();

        // This code segment is referred to rank each asset according to trust rank algorithm
        var trustRank = new TrustRankAlgorithm(AdjMat, assetLossVec, 100);
        var scoresTR = trustRank.getScores();

        var scoresABNR = new HashMap<Integer, Double>();
        for (var i = 0; i < scoresDC.entrySet().size(); i++) {
            var keyNode = scoresDC.keySet().toArray(Integer[]::new)[i];
            if (scoresHC.get(keyNode).isNaN())
                scoresHC.replace(keyNode, 0.0);
            if (scoresBC.get(keyNode).isNaN())
                scoresBC.replace(keyNode, 0.0);
            var sumOfRanks = scoresDC.get(keyNode) + scoresBC.get(keyNode) + scoresHC.get(keyNode) + scoresKC.get(keyNode) + scoresTR.get(keyNode);
            scoresABNR.put(keyNode, sumOfRanks / 5);
        }

        // This code segment is referred to capture the connections among each asset and its neighbor nodes
        var IDN = new InDegreeNodes(AdjMat, assetLossVec);
        var nodes = IDN.retrieveInDegreeNodes();

        // Set the spare budget of security resources for each defender
        var budget = Defenders.spareBudget_D;

        // This code segment is referred to allocate spare defenders investments on paths involved in "in" and "out" degree edges
        for (var assetNode : nodes.keySet()) {
            var parents = nodes.get(assetNode);
            for (var nod : parents) {
                var edge = attackDefenceGraph[nod - 1][assetNode - 1];
                var sizeParents = nodes.get(assetNode).stream().toList().size();
                var currentAssetCutOfTotalBudget = budget * scoresABNR.get(assetNode) / sizeParents;
                edge.setInvest_D(edge.addSpareInvestFor_D(currentAssetCutOfTotalBudget));
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

class TrustRankWithInDegreeAllPathsMain {
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

            // This code segment is referred to rank each asset according to degree centrality algorithm
            var degreeCentrality = new DegreeCentralityMethod(AdjMat, assetLossVec);
            var scoresDC = degreeCentrality.getScores();

            // This code segment is referred to rank each asset according to betweenness centrality algorithm
            var betweennessCentrality = new BetweennessCentralityMethod(AdjMat, assetLossVec);
            var scoresBC = betweennessCentrality.getScores();

            // This code segment is referred to rank each asset according to harmonic centrality algorithm
            var harmonicCentrality = new HarmonicCentralityMethod(AdjMat, assetLossVec);
            var scoresHC = harmonicCentrality.getScores();

            // This code segment is referred to rank each asset according to katz centrality algorithm
            var katzCentrality = new katzCentralityMethod(AdjMat, assetLossVec);
            var scoresKC = katzCentrality.getScores();

            // This code segment is referred to rank each asset according to trust rank algorithm
            var trustRank = new TrustRankAlgorithm(AdjMat, assetLossVec, 100);
            var scoresTR = trustRank.getScores();

            var scoresABNR = new HashMap<Integer, Double>();
            for (var i = 0; i < scoresDC.entrySet().size(); i++) {
                var keyNode = scoresDC.keySet().toArray(Integer[]::new)[i];
                if (scoresHC.get(keyNode).isNaN())
                    scoresHC.replace(keyNode, 0.0);
                if (scoresBC.get(keyNode).isNaN())
                    scoresBC.replace(keyNode, 0.0);
                var sumOfRanks = scoresDC.get(keyNode) + scoresBC.get(keyNode) + scoresHC.get(keyNode) + scoresKC.get(keyNode) + scoresTR.get(keyNode);
                scoresABNR.put(keyNode, sumOfRanks / 5);
            }

            // This code segment is referred to capture the connections among each asset and its neighbor nodes
            var IDN = new InDegreeNodes(AdjMat, assetLossVec);
            var nodes = IDN.retrieveInDegreeNodes();

            // Set the spare budget of security resources for each defender
            var budget = Defenders.spareBudget_D;

            // This code segment is referred to allocate spare defenders investments on paths involved in "in" and "out" degree edges
            // This code segment is referred to allocate spare defenders investments on paths involved in "in" and "out" degree edges
            for (var assetNode : nodes.keySet()) {
                var parents = nodes.get(assetNode);
                for (var nod : parents) {
                    var edge = attackDefenceGraph[nod - 1][assetNode - 1];
                    var sizeParents = nodes.get(assetNode).stream().toList().size();
                    var currentAssetCutOfTotalBudget = budget * scoresABNR.get(assetNode) / sizeParents;
                    edge.setInvest_D(edge.addSpareInvestFor_D(currentAssetCutOfTotalBudget));
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