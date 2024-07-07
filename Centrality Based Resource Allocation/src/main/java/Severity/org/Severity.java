package Severity.org;

import java.util.List;

/**
 * This class is used to creat an object of class severity to calculate the severity of an attack path
 */
public class Severity {
    private final double[][] adjMatrix;

    /**
     * This constructor is used to initialize the required parameters for this class
     * @param AdjMat The matrix which represents the attack graph
     */
    public Severity(double[][] AdjMat) {
        if (AdjMat == null) {
            throw new IllegalArgumentException("The matrix is null!");
        }
        this.adjMatrix = AdjMat;
    }

    /**
     * This method is used to calculate the severity of the given attack path
     * @param attackPath The attack path to calculate its severity
     * @return The severity of the considered attack path
     */
    public double getSeverity(List<Integer> attackPath) {
        double successfulAttackProb = 1;
        for (int i = 0; i < attackPath.size() - 1; i++) {
            successfulAttackProb *= Math.exp(-adjMatrix[attackPath.get(i) - 1][attackPath.get(i + 1) - 1]);
        }
        return successfulAttackProb;
    }
}
