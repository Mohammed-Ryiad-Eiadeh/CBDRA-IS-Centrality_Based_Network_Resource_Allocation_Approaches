package GraphCentralityMethods.org;

import java.util.ArrayList;
import java.util.HashMap;

public class RankBasedLossAversion {
    private final double[][] adjMatrix;
    private final double[] assetLossVec;
    private final float alpha;

    /**
     * This constructor is used to initialize the adjacency matrix that is going to be passed to average based node ranking
     * @param adjMatrix The adjacency matrix which represents the attack-defence graph
     * @param lossVector The assets' loss values as a vector
     * @param alpha The parameter that captures the diminishing sensitivity to changes in value
     */
    public RankBasedLossAversion(double[][] adjMatrix, double[] lossVector, float alpha) {
        if (adjMatrix == null) {
            throw new IllegalArgumentException("The matrix is null!");
        }
        if (lossVector == null) {
            throw new IllegalArgumentException("The loss vector is null");
        }
        if (alpha <= 0 || alpha >=1) {
            this.alpha = 0.5f;
        } else {
            this.alpha = alpha;
        }
        this.adjMatrix = adjMatrix;
        this.assetLossVec = lossVector;
    }

    /**
     * This method is used to call the betweenness centrality
     * @return The scores of the assets (ranks)
     */
    public HashMap<Integer, Double> getScores() {
        HashMap<Integer, Double> mapNodeToLossAvers_RankBased = new HashMap<>();
        for (int nodeIndex = 0; nodeIndex < assetLossVec.length; nodeIndex++) {
            if (assetLossVec[nodeIndex] > 0) {
                int asset = nodeIndex + 1;
                ArrayList<Integer> parents = getInDegree(asset);
                double assetsIncomingEdgesWeightsSeverity = parents
                        .stream()
                        .mapToDouble(incoming -> Math.exp(-adjMatrix[incoming - 1][asset - 1]))
                        .reduce(1.0, (a, b) -> a * b);
                double lossAversion = assetsIncomingEdgesWeightsSeverity * Math.pow(assetLossVec[asset - 1], alpha);
                mapNodeToLossAvers_RankBased.put(asset, lossAversion);
            }
        }
        double totalAssetsLoss = mapNodeToLossAvers_RankBased.values().stream().mapToDouble(Double::doubleValue).sum();
        mapNodeToLossAvers_RankBased.replaceAll((K, V) -> V / totalAssetsLoss);
        return mapNodeToLossAvers_RankBased;
    }

    /**
     * This method is used to get the in degree nodes for the given node.
     * @param node The node of interest.
     * @return List of in degree nodes.
     */
    private ArrayList<Integer> getInDegree(int node) {
        ArrayList<Integer> parentsNodes = new ArrayList<>();
        for (int i = 0; i < adjMatrix.length; i++) {
            if (adjMatrix[i][node - 1] > 0) {
                parentsNodes.add(i + 1);
            }
        }
        return parentsNodes;
    }
}