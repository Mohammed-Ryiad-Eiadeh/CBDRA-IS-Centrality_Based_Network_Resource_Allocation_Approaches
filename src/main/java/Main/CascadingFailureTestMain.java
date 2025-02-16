package Main;

import Attack_Defence_Graph.org.GraphData;
import ConcurrentAttacks.org.ConcurrentAttack;
import Defender.org.Defenders;
import ResourceAllocationsApproaches.org.AllocationApproaches;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CascadingFailureTestMain {
    public static void main(String[] args) {
        var graphPath = Path.of(System.getProperty("user.dir"), "\\Datasets With Random Investments\\VOiP.txt");
        if (Files.exists(graphPath)) {
            var graphCase = new GraphData(graphPath);
            var defenderMatrix = graphCase.getAttackDefenceGraph();
            var adjMatrix = graphCase.getAdjacencyMatrix(defenderMatrix);
            var assetLossVec = graphCase.getNodeAssetsLossValues();

            var attackPathsLerner = new ConcurrentAttack(adjMatrix,
                    assetLossVec,
                    500,
                    0.2,
                    0.4,
                    0.6,
                    100);
            var allAttackPaths = attackPathsLerner.getAllPaths();

            var defenderResources = 10;

            var katzCentralityWithInDegreeNodes = new AllocationApproaches(graphCase,
                    defenderMatrix,
                    adjMatrix,
                    allAttackPaths,
                    assetLossVec,
                    9/*defenderResources / 2*/);
            katzCentralityWithInDegreeNodes.callCentrality(AllocationApproaches.LinkageType.In_Degree_Nodes, AllocationApproaches.Centrality.Katz);
            var costReductionBeforeCascadingFailure = katzCentralityWithInDegreeNodes.getExpectedCostReduction();
            System.out.println("before : " + costReductionBeforeCascadingFailure);

            defenderMatrix = katzCentralityWithInDegreeNodes.getDefendersMatrix();
            adjMatrix = graphCase.getAdjacencyMatrix(defenderMatrix);

            System.out.println(Arrays.toString(assetLossVec));

            // Nodes to fail
            var assetsToFail = List.of(5);
            for (var potentialAsset : assetsToFail) {
                if (potentialAsset >= 0 && assetLossVec[potentialAsset] > 0) {
                    var outerNodes = getOutgoingNodes(adjMatrix, potentialAsset);
                    System.out.println(outerNodes);
                    var innerNodes = getIncomingNodes(adjMatrix, potentialAsset);
                    // re-distribute the loss for new assets
                    var sumOfNeighborsWeights = 0d;
                    for (var outNode : outerNodes) {
                        sumOfNeighborsWeights += Math.exp(-adjMatrix[potentialAsset][outNode]);
                    }
                    // remove connections with out-degree nodes
                    for (var outNode : outerNodes) {
                        assetLossVec[outNode] += assetLossVec[outNode] + assetLossVec[potentialAsset] * (Math.exp(-adjMatrix[potentialAsset][outNode]) / sumOfNeighborsWeights);
                        defenderMatrix[potentialAsset][outNode] = new Defenders(0);
                    }
                    // remove connections with in-degree nodes
                    for (var innerNode : innerNodes) {
                        defenderMatrix[innerNode][potentialAsset] = new Defenders(0);
                    }
                    adjMatrix = graphCase.getAdjacencyMatrix(defenderMatrix);
                }
                assetLossVec[potentialAsset] = 0d;
            }

            System.out.println(Arrays.toString(assetLossVec));

            attackPathsLerner = new ConcurrentAttack(adjMatrix,
                    assetLossVec,
                    500,
                    0.2,
                    0.4,
                    0.6,
                    100);
            allAttackPaths = attackPathsLerner.getAllPaths();

            katzCentralityWithInDegreeNodes = new AllocationApproaches(graphCase,
                    defenderMatrix,
                    adjMatrix,
                    allAttackPaths,
                    assetLossVec,
                    1/*defenderResources / 2*/);
            katzCentralityWithInDegreeNodes.callCentrality(AllocationApproaches.LinkageType.In_Degree_Nodes, AllocationApproaches.Centrality.Katz);
            costReductionBeforeCascadingFailure = katzCentralityWithInDegreeNodes.getExpectedCostReduction();
            System.out.println("after : " + costReductionBeforeCascadingFailure);
        } else {
            throw new IllegalArgumentException("The given path is not existed");
        }
    }

    private static List<Integer> getOutgoingNodes(double[][] adjMatrix, int asset) {
        List<Integer> outNodes = new ArrayList<>();
        for (int node = 0; node < adjMatrix.length; node++) {
            if (adjMatrix[asset][node] > 0) {
                outNodes.add(node);
            }
        }
        return outNodes;
    }

    private static List<Integer> getIncomingNodes(double[][] adjMatrix, int asset) {
        List<Integer> incomingNodes = new ArrayList<>();
        for (int node = 0; node < adjMatrix.length; node++) {
            if (adjMatrix[node][asset] > 0) {
                incomingNodes.add(node);
            }
        }
        return incomingNodes;
    }
}
