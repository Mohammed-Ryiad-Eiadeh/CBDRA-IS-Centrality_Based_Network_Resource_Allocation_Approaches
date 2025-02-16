package GraphCentralityMethods.org;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ABNR {
    private final double[][] adjMatrix;
    private final double[] assetLossVec;
    private final int maxIter_forTR;

    /**
     * This constructor is used to initialize the adjacency matrix that is going to be passed to average based node ranking
     * @param adjMatrix The adjacency matrix which represents the attack-defence graph
     * @param lossVector The assets' loss values as a vector
     * @param maxIter_forTR The max iteration where TR should be enforced to stop if it is not converged before it
     */
    public ABNR(double[][] adjMatrix, double[] lossVector, int maxIter_forTR) {
        this.maxIter_forTR = maxIter_forTR;
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
        // Create executor service with virtual thread factory
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Get the ranks based on Betweenness Centrality
        Callable<HashMap<Integer, Double>> BC = () -> {
            BetweennessCentralityMethod betweennessCentralityMethod = new BetweennessCentralityMethod(adjMatrix, assetLossVec);
            return betweennessCentralityMethod.getScores();
        };

        // Get the ranks based on Degree Centrality
        Callable<HashMap<Integer, Double>> DC = () -> {
            DegreeCentralityMethod degreeCentralityMethod = new DegreeCentralityMethod(adjMatrix, assetLossVec);
            return degreeCentralityMethod.getScores();
        };

        // Get the ranks based on Harmonic Centrality
        Callable<HashMap<Integer, Double>> HC = () -> {
            HarmonicCentralityMethod harmonicCentralityMethod = new HarmonicCentralityMethod(adjMatrix, assetLossVec);
            return harmonicCentralityMethod.getScores();
        };

        // Get the ranks based on Katz Centrality
        Callable<HashMap<Integer, Double>> KC = () -> {
            KatzCentralityMethod katzCentralityMethod = new KatzCentralityMethod(adjMatrix, assetLossVec);
            return katzCentralityMethod.getScores();
        };

        // Get the ranks based on TrustRank
        Callable<HashMap<Integer, Double>> TR = () -> {
            TrustRankAlgorithm trustRankAlgorithm = new TrustRankAlgorithm(adjMatrix, assetLossVec, TrustRankAlgorithm.SeedVecInitializer.WeightFactorBased, maxIter_forTR);
            return trustRankAlgorithm.getScores();
        };

        // Define the hashmap to store the asset with its ranks
        HashMap<Integer, Double> mapAssetToScores = new HashMap<>();

        // Get the ranks from all methods into one block (handled via hash map)
        try {
            List<Future<HashMap<Integer, Double>>> results = executorService.invokeAll(List.of(BC, DC, HC, KC, TR));
            for (Future<HashMap<Integer, Double>> result : results) {
                for (Map.Entry<Integer, Double> entry : result.get().entrySet()) {
                    if (mapAssetToScores.containsKey(entry.getKey())) {
                        mapAssetToScores.replace(entry.getKey(), mapAssetToScores.get(entry.getKey()) + entry.getValue());
                    } else {
                        mapAssetToScores.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // Shut down the service
        executorService.shutdown();

        // do normalization
        double sumOfScores = mapAssetToScores.values().stream().mapToDouble(val -> val).sum();
        mapAssetToScores.replaceAll((K, V) -> V / sumOfScores);
        return mapAssetToScores;
    }
}
