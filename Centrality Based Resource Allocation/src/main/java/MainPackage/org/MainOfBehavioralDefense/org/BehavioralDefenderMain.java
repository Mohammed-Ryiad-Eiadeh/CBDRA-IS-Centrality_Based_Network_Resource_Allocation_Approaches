package MainPackage.org.MainOfBehavioralDefense.org;

import Attack_Defence_Graph.org.Graph;
import Attack_Defence_Graph.org.GraphData;
import BehavioralDefense.org.BehavioralDefender;
import ConcurrentAttacks.org.ConcurrentAttack;
import CostFunction.org.CostFunction;
import Defender.org.Defenders;
import Severity.org.Severity;

/**
 * This class is used as the main class for the first scenario according to behavioral defender
 */
public class BehavioralDefenderMain {
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

        var startTime = System.currentTimeMillis();

        // Set the spare budget of security resources for each defender
        Defenders.spareBudget_D = 10d;

        // This code segment is referred to allocate spare defenders investments on the most potential path (top-1) according to behavioral defender
        // Set the spare budget of security resources for each defender
        var budget = Defenders.spareBudget_D;

        // Apply the behavioral defender
        var behavioralDefender = new BehavioralDefender(AdjMat, 0.5f);
        var newWeights = behavioralDefender.applyBehavioralDefendingForResourceAllocation();
        var sumOfProbWeighting = 0.0d;
        for (double[] newWeight : newWeights) {
            for (var j = 0; j < newWeights[0].length; j++) {
                sumOfProbWeighting += newWeight[j];
            }
        }
        for (var i = 0; i < AdjMat.length; i++) {
            for (var j = 0; j < AdjMat[0].length; j++) {
                if (AdjMat[i][j] > 0) {
                    var edge = attackDefenceGraph[i][j];
                    edge.setInvest_D(edge.addSpareInvestFor_D(newWeights[i][j] / sumOfProbWeighting * budget));
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

class BehavioralDefenderAllPathsMain {
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

        var startTime = System.currentTimeMillis();

        // Set the spare budget of security resources for each defender
        Defenders.spareBudget_D = 10d;

        // This code segment is referred to allocate spare defenders investments on the most potential path (top-1) according to behavioral defender
        // Set the spare budget of security resources for each defender
        var budget = Defenders.spareBudget_D;

        // Apply the behavioral defender
        var behavioralDefender = new BehavioralDefender(AdjMat, 0.5f);
        var newWeights = behavioralDefender.applyBehavioralDefendingForResourceAllocation();
        var sumOfProbWeighting = 0.0d;
        for (double[] newWeight : newWeights) {
            for (var j = 0; j < newWeights[0].length; j++) {
                sumOfProbWeighting += newWeight[j];
            }
        }
        for (var i = 0; i < AdjMat.length; i++) {
            for (var j = 0; j < AdjMat[0].length; j++) {
                if (AdjMat[i][j] > 0) {
                    var edge = attackDefenceGraph[i][j];
                    edge.setInvest_D(edge.addSpareInvestFor_D(newWeights[i][j] / sumOfProbWeighting * budget));
                }
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