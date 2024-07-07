package Attack_Defence_Graph.org;

import java.nio.file.Path;

/**
 * Thi enumeration includes the value id for each graph problem we have
 */
public enum Graph {Figure3, SCADA_rand, DER_rand, e_commerce_rand, VOIP_rand,
    ABSNP_rand, ASFS3_rand, ASS2009_rand, HG1_rand, HG2_rand, AWS03_rand,
    RealGraph1, RealGraph2, RealGraph3, RealGraph4, RealGraph5;

    /**
     * This method is used to retrieve the path of the graph problem
     * @return The path of the graph problem
     */
    public Path getPath() {
        String directory = System.getProperty("user.dir") + "\\Datasets";
        return switch (this) {
            case SCADA_rand -> Path.of(directory, "\\SCADA_rand.txt");
            case DER_rand -> Path.of(directory, "\\DER_rand.txt");
            case e_commerce_rand -> Path.of(directory, "\\e_commerce_rand.txt");
            case VOIP_rand -> Path.of(directory, "\\VOiP_rand.txt");
            case ABSNP_rand -> Path.of(directory, "\\ABSNP_rand.txt");
            case ASFS3_rand -> Path.of(directory, "\\ASFS3_rand.txt");
            case ASS2009_rand -> Path.of(directory, "\\ASS2009_rand.txt");
            case HG1_rand -> Path.of(directory, "\\HG1_rand.txt");
            case HG2_rand -> Path.of(directory, "\\HG2_rand.txt");
            case AWS03_rand -> Path.of(directory, "\\AWS03_rand.txt");
            case Figure3 -> Path.of(directory, "\\Figure3.txt");
            case RealGraph1 -> Path.of(directory, "\\RealGraph1.txt");
            case RealGraph2 -> Path.of(directory, "\\RealGraph2.txt");
            case RealGraph3 -> Path.of(directory, "\\RealGraph3.txt");
            case RealGraph4 -> Path.of(directory, "\\RealGraph4.txt");
            case RealGraph5 -> Path.of(directory, "\\RealGraph5.txt");
        };
    }
}
