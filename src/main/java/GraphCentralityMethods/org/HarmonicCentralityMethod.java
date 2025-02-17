package GraphCentralityMethods.org;

import org.jgrapht.alg.scoring.HarmonicCentrality;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.HashMap;
import java.util.Map;

public class HarmonicCentralityMethod {
    private final double[][] adjMatrix;
    private final double[] assetLossVec;

    /**
     * This constructor is used to initialize the adjacency matrix that is going to be passed to harmonic centrality
     * @param adjMatrix The adjacency matrix which represents the attack-defence graph
     * @param lossVector The assets' loss values as a vector
     */
    public HarmonicCentralityMethod(double[][] adjMatrix, double[] lossVector) {
        if (adjMatrix == null) {
            throw new IllegalArgumentException("The matrix is null!");
        }
        if (lossVector == null) {
            throw new IllegalArgumentException("The loss vector is null");
        }
        this.adjMatrix = adjMatrix;
        this.assetLossVec = lossVector;
    }

    /**
     * This method is used to call the betweenness centrality
     * @return The scores of the assets (ranks)
     */
    public HashMap<Integer, Double> getScores() {
        DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        double[][] Data = this.adjMatrix;
        for (int node = 0; node < Data[0].length; node++) {
            graph.addVertex(node);
        }
        for (int node = 0; node < Data.length; node++) {
            for (int nod = 0; nod < Data[0].length; nod++) {
                if (Data[node][nod] > 0) {
                    graph.addEdge(node, nod);
                    graph.setEdgeWeight(node, nod, Math.exp(-Data[node][nod]));
                }
            }
        }
        // call the BetweennessCentralityMethod approach
        HarmonicCentrality<Integer, DefaultWeightedEdge> harmonicCentrality = new HarmonicCentrality<>(graph);
        Map<Integer, Double> mapNodeToCentrality = harmonicCentrality.getScores();
        // get and store the centrality
        HashMap<Integer, Double> mapAssetToScores = new HashMap<>();
        for (int node = 0; node < Data[0].length; node++) {
            if (assetLossVec[node] > 0) {
                mapAssetToScores.put(node + 1, mapNodeToCentrality.get(node));
            }
        }
        // do normalization
        double sumOfDegrees = mapAssetToScores.values().stream().mapToDouble(val -> val).sum();
        mapAssetToScores.replaceAll((K, V) -> V / sumOfDegrees);
        mapAssetToScores.entrySet().forEach(entry -> {
            if (entry.getValue().isNaN()) {
                entry.setValue(0D);
            }
        });
        return mapAssetToScores;
    }
}

