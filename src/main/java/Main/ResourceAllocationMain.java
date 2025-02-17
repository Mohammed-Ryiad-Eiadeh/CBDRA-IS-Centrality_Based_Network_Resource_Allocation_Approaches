package Main;

import Attack_Defence_Graph.org.GraphData;
import ConcurrentAttacks.org.ConcurrentAttack;
import ResourceAllocationsApproaches.org.AllocationApproaches;
import StoreRetrieveHashmap.org.StoreDataAsTable;
import org.tribuo.util.Util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResourceAllocationMain implements StoreDataAsTable {
    public static void main(String[] args) {
        // Select all test cases or the graphs as once.
        var pathURL = System.getProperty("user.dir") + "\\Data With 1s Investments";

        // Initialize a list to store the names of attack graphs
        List<String> listOfAttackGraphs = new ArrayList<>();

        // Define the path to the folder containing the datasets
        Path folder = Path.of(pathURL);

        // Check if the folder exists and is a directory
        // Iterate over the files in this directory and store their names into the list
        if (Files.exists(folder) && Files.isDirectory(folder)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folder)) {
                for (Path path : directoryStream) {
                    listOfAttackGraphs.add(path.getFileName().toString());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        // Define a string array to store the name of the allocation approach as the header of the performance matrix
        var headers = new String[31];

        // Define a hashmap that maps each graph with the allocation approaches sorted for it
        var mapGraphsToAllocationSortedMethods = new HashMap<String, HashMap<String, Double>>();

        // define the duration time object
        var durationTime = 0L;

        // Iterate over all graph cases we have
        for (String graphCase : listOfAttackGraphs) {
            graphCase = graphCase.replace(".txt", "");
            var task = new GraphData(Path.of(pathURL + "\\" + graphCase + ".txt"));
            var attackDefenceGraph = task.getAttackDefenceGraph();
            var AdjMat = task.getAdjacencyMatrix(attackDefenceGraph);
            var assetLossVec = task.getNodeAssetsLossValues();

            // Generate paths by genetic algorithm from each entry node to each asset
            var concurrentAttackers = new ConcurrentAttack(AdjMat,
                assetLossVec,
                500,
                0.2,
                0.4,
                0.6,
                100);
            var concurrentAttacks =  concurrentAttackers.getAllPaths();

            // Define the security Budget
            var resources = 10;

            // Define a hashmap that maps the allocation to their cost relative reduction
            var mapAllocationMethodToRelativeCostReduction = new HashMap<String, Double>();

            // Define and initialize the array to hold the relative cost reduction from each allocation approach
            double[] scoresRow = new double[31];

            // Fetch the current time in ms
            var startTime = System.currentTimeMillis();

            // Calculate the expected relative reduction in the cost after allocating resources using risk based defense
            var riskBasedDefence = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            riskBasedDefence.callRiskBasedDefense();
            headers[0] = "riskBasedDefence";
            scoresRow[0] = riskBasedDefence.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[0], scoresRow[0]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using defense in depth
            var defenceInDepth = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            defenceInDepth.callDefenseInDepth();
            headers[1] = "defenceInDepth";
            scoresRow[1] = defenceInDepth.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[1], scoresRow[1]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using behavioral defender
            var behavioralDefender = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            behavioralDefender.callBehavioralDefender();
            headers[2] = "behavioralDefender";
            scoresRow[2] = behavioralDefender.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[2], scoresRow[2]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using behavioral defender
            var minCut = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            minCut.callMinCut();
            headers[3] = "minCut";
            scoresRow[3] = minCut.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[3], scoresRow[3]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using betweenness centrality + In-Degree nodes
            var betweennessCentralityWithInDegreeNodes = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            betweennessCentralityWithInDegreeNodes.callCentrality(AllocationApproaches.LinkageType.In_Degree_Nodes, AllocationApproaches.Centrality.Betweenness);
            headers[4] = "betweennessCentralityWithInDegreeNodes";
            scoresRow[4] = betweennessCentralityWithInDegreeNodes.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[4], scoresRow[4]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using betweenness centrality + Adjacent nodes
            var betweennessCentralityWithAdjacentNodes = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            betweennessCentralityWithAdjacentNodes.callCentrality(AllocationApproaches.LinkageType.Adjacent_Nodes, AllocationApproaches.Centrality.Betweenness);
            headers[5] = "betweennessCentralityWithAdjacentNodes";
            scoresRow[5] = betweennessCentralityWithAdjacentNodes.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[5], scoresRow[5]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using betweenness centrality + Markov blanket
            var betweennessCentralityWithMarkovBlanket = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            betweennessCentralityWithMarkovBlanket.callCentrality(AllocationApproaches.LinkageType.Markov_Blanket, AllocationApproaches.Centrality.Betweenness);
            headers[6] = "betweennessCentralityWithMarkovBlanket";
            scoresRow[6] = betweennessCentralityWithMarkovBlanket.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[6], scoresRow[6]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using betweenness centrality + Min Cut Edges
            var betweennessCentralityWithMinCut = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            betweennessCentralityWithMinCut.callCentrality(AllocationApproaches.LinkageType.Min_Cut_Edges, AllocationApproaches.Centrality.Betweenness);
            headers[7] = "betweennessCentralityWithMinCut";
            scoresRow[7] = betweennessCentralityWithMinCut.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[7], scoresRow[7]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using harmonic centrality + In-Degree nodes
            var harmonicCentralityWithInDegreeNodes = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            harmonicCentralityWithInDegreeNodes.callCentrality(AllocationApproaches.LinkageType.In_Degree_Nodes, AllocationApproaches.Centrality.Harmonic);
            headers[8] = "harmonicCentralityWithInDegreeNodes";
            scoresRow[8] = harmonicCentralityWithInDegreeNodes.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[8], scoresRow[8]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using harmonic centrality + Adjacent nodes
            var harmonicCentralityWithAdjacentNodes = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            harmonicCentralityWithAdjacentNodes.callCentrality(AllocationApproaches.LinkageType.Adjacent_Nodes, AllocationApproaches.Centrality.Harmonic);
            headers[9] = "harmonicCentralityWithAdjacentNodes";
            scoresRow[9] = harmonicCentralityWithAdjacentNodes.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[9], scoresRow[9]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using harmonic centrality + Markov blanket
            var harmonicCentralityWithMarkovBlanket = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            harmonicCentralityWithMarkovBlanket.callCentrality(AllocationApproaches.LinkageType.Markov_Blanket, AllocationApproaches.Centrality.Harmonic);
            headers[10] = "harmonicCentralityWithMarkovBlanket";
            scoresRow[10] = harmonicCentralityWithMarkovBlanket.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[10], scoresRow[10]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using harmonic centrality + Min Cut Edges
            var harmonicCentralityWithMinCut = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            harmonicCentralityWithMinCut.callCentrality(AllocationApproaches.LinkageType.Min_Cut_Edges, AllocationApproaches.Centrality.Harmonic);
            headers[11] = "harmonicCentralityWithMinCut";
            scoresRow[11] = harmonicCentralityWithMinCut.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[11], scoresRow[11]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using degree centrality + In-Degree nodes
            var degreeCentralityWithInDegreeNodes = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            degreeCentralityWithInDegreeNodes.callCentrality(AllocationApproaches.LinkageType.In_Degree_Nodes, AllocationApproaches.Centrality.Degree);
            headers[12] = "degreeCentralityWithInDegreeNodes";
            scoresRow[12] = degreeCentralityWithInDegreeNodes.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[12], scoresRow[12]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using degree centrality + Adjacent nodes
            var degreeCentralityWithAdjacentNodes = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            degreeCentralityWithAdjacentNodes.callCentrality(AllocationApproaches.LinkageType.Adjacent_Nodes, AllocationApproaches.Centrality.Degree);
            headers[13] = "degreeCentralityWithAdjacentNodes";
            scoresRow[13] = degreeCentralityWithAdjacentNodes.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[13], scoresRow[13]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using degree centrality + Markov blanket
            var degreeCentralityWithMarkovBlanket = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            degreeCentralityWithMarkovBlanket.callCentrality(AllocationApproaches.LinkageType.Markov_Blanket, AllocationApproaches.Centrality.Degree);
            headers[14] = "degreeCentralityWithMarkovBlanket";
            scoresRow[14] = degreeCentralityWithMarkovBlanket.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[14], scoresRow[14]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using degree centrality + Min Cut Edges
            var degreeCentralityWithMinCut = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            degreeCentralityWithMinCut.callCentrality(AllocationApproaches.LinkageType.Min_Cut_Edges, AllocationApproaches.Centrality.Degree);
            headers[15] = "degreeCentralityWithMinCut";
            scoresRow[15] = degreeCentralityWithMinCut.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[15], scoresRow[15]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using katz centrality + In-Degree nodes
            var katzCentralityWithInDegreeNodes = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            katzCentralityWithInDegreeNodes.callCentrality(AllocationApproaches.LinkageType.In_Degree_Nodes, AllocationApproaches.Centrality.Katz);
            headers[16] = "katzCentralityWithInDegreeNodes";
            scoresRow[16] = katzCentralityWithInDegreeNodes.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[16], scoresRow[16]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using degree centrality + Adjacent nodes
            var katzCentralityWithAdjacentNodes = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            katzCentralityWithAdjacentNodes.callCentrality(AllocationApproaches.LinkageType.Adjacent_Nodes, AllocationApproaches.Centrality.Katz);
            headers[17] = "katzCentralityWithAdjacentNodes";
            scoresRow[17] = katzCentralityWithAdjacentNodes.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[17], scoresRow[17]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using katz centrality + Markov blanket
            var katzCentralityWithMarkovBlanket = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            katzCentralityWithMarkovBlanket.callCentrality(AllocationApproaches.LinkageType.Markov_Blanket, AllocationApproaches.Centrality.Katz);
            headers[18] = "katzCentralityWithMarkovBlanket";
            scoresRow[18] = katzCentralityWithMarkovBlanket.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[18], scoresRow[18]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using katz centrality + Min Cut Edges
            var katzCentralityWithMinCut = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            katzCentralityWithMinCut.callCentrality(AllocationApproaches.LinkageType.Min_Cut_Edges, AllocationApproaches.Centrality.Katz);
            headers[19] = "katzCentralityWithMinCut";
            scoresRow[19] = katzCentralityWithMinCut.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[19], scoresRow[19]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using katz centrality + Min Cut Edges
            var katzCentralityWithProspectBasedInDegree = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            katzCentralityWithProspectBasedInDegree.callCentrality(AllocationApproaches.LinkageType.In_Degree_Nodes_With_Behavioral_Distribution, AllocationApproaches.Centrality.Katz);
            headers[20] = "katzCentralityWithCognitiveProspectBasedInDegree";
            scoresRow[20] = katzCentralityWithProspectBasedInDegree.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[20], scoresRow[20]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using TrustRank centrality + In-Degree nodes
            var TRCentralityWithInDegreeNodes = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            TRCentralityWithInDegreeNodes.callCentrality(AllocationApproaches.LinkageType.In_Degree_Nodes, AllocationApproaches.Centrality.TrustRank);
            headers[21] = "TRCentralityWithInDegreeNodes";
            scoresRow[21] = TRCentralityWithInDegreeNodes.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[21], scoresRow[21]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using TrustRank centrality + Adjacent nodes
            var TRCentralityWithAdjacentNodes = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            TRCentralityWithAdjacentNodes.callCentrality(AllocationApproaches.LinkageType.Adjacent_Nodes, AllocationApproaches.Centrality.TrustRank);
            headers[22] = "TRCentralityWithAdjacentNodes";
            scoresRow[22] = TRCentralityWithAdjacentNodes.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[22], scoresRow[22]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using TrustRank centrality + Markov blanket
            var TRCentralityWithMarkovBlanket = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            TRCentralityWithMarkovBlanket.callCentrality(AllocationApproaches.LinkageType.Markov_Blanket, AllocationApproaches.Centrality.TrustRank);
            headers[23] = "TRCentralityWithMarkovBlanket";
            scoresRow[23] = TRCentralityWithMarkovBlanket.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[23], scoresRow[23]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using TrustRank centrality + Min Cut Edges
            var TRCentralityWithMinCut = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            TRCentralityWithMinCut.callCentrality(AllocationApproaches.LinkageType.Min_Cut_Edges, AllocationApproaches.Centrality.TrustRank);
            headers[24] = "TRCentralityWithMinCut";
            scoresRow[24] = TRCentralityWithMinCut.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[24], scoresRow[24]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using TrustRank centrality + Min Cut Edges
            var TRCentralityWithCognitiveProspectBasedInDegree = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            TRCentralityWithCognitiveProspectBasedInDegree.callCentrality(AllocationApproaches.LinkageType.In_Degree_Nodes_With_Behavioral_Distribution, AllocationApproaches.Centrality.TrustRank);
            headers[25] = "TRCentralityWithCognitiveProspectBasedInDegree";
            scoresRow[25] = TRCentralityWithCognitiveProspectBasedInDegree.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[25], scoresRow[25]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using TrustRank centrality + In-Degree nodes
            var ABNRWithInDegreeNodes = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            ABNRWithInDegreeNodes.callCentrality(AllocationApproaches.LinkageType.In_Degree_Nodes, AllocationApproaches.Centrality.ABNR);
            headers[26] = "ABNRWithInDegreeNodes";
            scoresRow[26] = ABNRWithInDegreeNodes.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[26], scoresRow[26]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using TrustRank centrality + Adjacent nodes
            var ABNRWithAdjacentNodes = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            ABNRWithAdjacentNodes.callCentrality(AllocationApproaches.LinkageType.Adjacent_Nodes, AllocationApproaches.Centrality.ABNR);
            headers[27] = "ABNRWithAdjacentNodes";
            scoresRow[27] = ABNRWithAdjacentNodes.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[27], scoresRow[27]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using TrustRank centrality + Markov blanket
            var ABNRWithMarkovBlanket = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            ABNRWithMarkovBlanket.callCentrality(AllocationApproaches.LinkageType.Markov_Blanket, AllocationApproaches.Centrality.ABNR);
            headers[28] = "ABNRWithMarkovBlanket";
            scoresRow[28] = ABNRWithMarkovBlanket.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[28], scoresRow[28]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using TrustRank centrality + Min Cut Edges
            var ABNRWithMinCut = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            ABNRWithMinCut.callCentrality(AllocationApproaches.LinkageType.Min_Cut_Edges, AllocationApproaches.Centrality.ABNR);
            headers[29] = "ABNRWithMinCut";
            scoresRow[29] = ABNRWithMinCut.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[29], scoresRow[29]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            var lossAverseRankWithInDegreeNodes = new AllocationApproaches(task, attackDefenceGraph, AdjMat, concurrentAttacks, assetLossVec, resources);
            lossAverseRankWithInDegreeNodes.callCentrality(AllocationApproaches.LinkageType.In_Degree_Nodes, AllocationApproaches.Centrality.LossAversionRank);
            headers[30] = "LossAverseRankWithInDegreeNodes";
            scoresRow[30] = lossAverseRankWithInDegreeNodes.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[30], scoresRow[30]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Fetch the current time in ms
            var endTime = System.currentTimeMillis();

            // calculate the duration time for each graph
            System.out.println("allocation for graph :\t" + graphCase + "\thas been carried out");
            durationTime += (endTime - startTime);
        }
        // Duration time for resource allocation over all graphs
        System.out.println("\nThe time of applying all of our resource allocation is : " + Util.formatDuration(0, durationTime));

        // Notify fo the completion of the execution and store the result in Excel file
        System.out.println("The allocation process has been completed");
        StoreDataAsTable.storeDataFromHashMap("Results", headers, mapGraphsToAllocationSortedMethods);
    }
}

