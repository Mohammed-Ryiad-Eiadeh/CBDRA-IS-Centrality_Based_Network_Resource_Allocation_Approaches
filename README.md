# CBDRA-IS: Centrality_Based_Network_Resource_Allocation_Approaches

# Abstract

Interdependent systems are facing an escalating number of cybersecurity threats. This paper explores security decision-making within these intricate and interdependent systems. We have developed a resource allocation framework aimed at bolstering the security of such systems, which are managed by a single rational (logical) defender. Our framework uses cyber attack graphs to model these systems and their vulnerabilities. We propose four defense mechanisms that integrate various network analysis algorithms, including degree centrality, betweenness centrality, harmonic centrality, TrustRank algorithm, Katz centrality, and an Average Based Node Ranking (ABNR). These algorithms help determine the importance of different critical assets within the system to provide insights into asset significance. The resource allocation mechanisms we employed draw on existing graph theories commonly used in graphical models, such as Adjacent Nodes, In-degree Nodes, Min-Cut Edges, and Markov Blanket. We combine each ranking algorithm with the four allocation techniques. Our methods demonstrate low sensitivity to the number of simultaneous attacks on interdependent systems. We validated our decision-making framework using fifteen attack graphs, which represent multiple real-world interdependent systems. We measured the security improvements achieved through our defense methods against four well-known resource allocation algorithms such as behavioral defender and risk-based defense. In most test cases, our framework achieved better resource allocation outcomes than the baselines. According to the results with integrating the Friedman statistical test, our approach's outcomes are superior. Our framework enhances security decision-making across various scenarios, including the top-1 attack path(s) and all attack paths corresponding to each entry-asset variation. We have made the full implementation of our resource allocation framework available to the research community.

# Framework

As shown in this figure, the framework takes as input an attack graph, which includes various entry nodes (points where attackers can initiate an attack) and asset nodes (critical components for the defender) along with their interconnections, highlighting the security vulnerabilities between these nodes. Given this attack graph, the Genetic Algorithm (GA) is used to generate the top-1 and all potential attack paths that attackers might exploit to compromise the system's assets. The GA incorporates a path encoding scheme that prevents revisiting nodes and encoding sink nodes in the paths provided by the algorithm. It also employs a multi-objective fitness function that considers asset loss to evaluate the feasibility of an attack path, determining whether it should survive or be replaced by a new one from the search space of the problem. Following the generation of potential attack paths, the defender proceeds to distribute her security resources across different attack paths, each connecting an entry node to a critical asset within the system. Our methodology for security resource allocation is based on key concepts from graph theory literature, such as in-degree nodes, adjacent nodes, Min-Cut edges, and the Markov Blanket. Furthermore, we utilize various node ranking algorithms, including the TrustRank algorithm and Katz centrality, to derive more detailed information about the system's assets (their importance) within the attack graph. This enables a proportional allocation of available security resources using these graph-theoretic principles, reflecting the importance of the assets and aiding the human operator. Once the resource allocation decisions are finalized, we calculate the relative reduction in expected security costs (determined as the relative difference between costs under our allocation methods and initial investment costs) to evaluate and analyze the effectiveness of the proposed framework

![Screenshot (533)](https://github.com/user-attachments/assets/f25f163d-29a0-4652-b00b-52b5a6e54f8b)

# Fitness Function

$F_2(P) = \max_{P \in P_m} \big(\exp\big(-\sum_{(v_i,v_j)\in P} {x_{i,j}}\big) + Wf\sum_{v_m\in P} L_m\big).$
   
   $P$ is the given attack path.

   $P_m$ is a set of attack paths.

   $v_i,v_j$ are the nodes in $P$.

   $L_m$ is the loss corresponding to node $v_m$

   $Wf$ is the weight factor lies in [0,1]
   
This function accounts for the total asset loss that the system will lose if the attack is occured successfully.

# Our Contribution

1) We propose a method for allocating security resources for a defender of interconnected systems, where the protected assets are interdependent. We demonstrate how \name influences decision-making regarding system security, and quantify the percentage of improvement attributable to our resource allocation method.

2) We utilize six node ranking algorithms aimed at enhancing decisions related to resource allocation. These algorithms are integrated with three defense strategies utilizing concepts such as adjacent nodes, in-degree nodes, Markov blanket, and min-cut edges. They are tailored to mitigate threats posed by various attack models targeting the security of interconnected systems.

3) We employ the Genetic Algorithm (GA), a type of evolutionary computation, to identify probable attack paths from entry nodes used by attackers to critical assets. This approach ensures reasonable time complexity. Specifically, GA is utilized to identify the top-1 attack path and all potential attack paths from each entry node to its corresponding critical asset, mimicking real-world scenarios involving concurrent attacks. We introduce a multi-objective fitness function for GA, which evaluates the total estimated financial loss associated with each attack path.

4) We evaluate our proposed defense strategies for interconnected systems using fifteen attack graphs. We compare the effectiveness of four resource allocation methods on these graphs: 'defense in depth', 'the behavioral defender', 'risk-based defense', and 'min-cut edges'.

5) Our entire framework is developed in Java, following object-oriented programming. The implementation integrates robust and extensively tested libraries for executing graph-theoretic algorithms. The efficiency of our implementation is primarily attributed to the utilization of various optimized data structures.

# Datasets We Used In Our Work

For our assessment, we used ten distinct attack graphs, each symbolizing a different interdependent system and network structure. We divided these datasets into three groups. The first group contains four attack graphs from real-world interconnected systems, namely DER.1, SCADA, E-commerce, and VOIP. Signifies an attack step, and we consider every edge to be directional. The second group consists of two graph typologies, referred to as HG1 and HG2, which were introduced in earlier studies. The third group includes four datasets from a renowned interactive scientific graph data repository, named aves-sparrow-social-2009 (ASC2009), aves-sparrowlyon-flock-season3 (ASFS3), aves-weaver-social-03 (AWS03), and aves-barn-swallow-non-physical (ABSNP). This repository is a network data collection produced by top-tier US niversities.

| System | # Nodes | # Edges | # Critical Assets | Graph Type |
| --- | --- | --- | --- | --- |
| SCADA | 13 | 20 | 6 | Directed |
| DER.1 | 22 | 32 | 6 | Directed |
| E-Commerce | 20 | 32 | 4 | Directed |
| VOIP | 22 | 35 | 4 | Directed |
| HG1 | 7 | 10 | 2 | Directed |
| HG2 | 15 | 22 | 5 | Directed |
| ABSNP | 17 | 122 | 6 | Directed |
| ASFS3 | 27 | 163 | 9 | Directed |
| ASS2009 | 31 | 211 | 9 | Directed |
| AWS03 | 42 | 152 | 15 | Directed |

Note: all of these datasets are stored in the project directory and is called dynamically so no need to set up their paths.

# Parameter Configuration of Our Experiments
We begin by outlining the key hyperparameters used in different components of our framework. The parameters for GA were chosen as follows: maximum iterations ($M=100$), population size (number of potential attack paths) ($N=500$), mating probability ($m_p=0.2$), mutation rate ($m_r=0.2$), and weight factor ($Wf=0.001$). The defender's security budget is fixed at $S=10$ for all experiments, and the maximum iterations for the TrustRank algorithm is set to 100, with epsilon ($\epsilon$) at 0.0001. Moreover, the initial investments over the edges of the ten used datasets are randomly generated within the range of (0,1). And for Friedman test, the significance level is set to $SL=0.05$. We emphasize that the advantages of our proposed defense (resource allocation) strategies are applicable for any security budget. For the behavioral defender baseline, the behavioral level ($a$) is set to 0.5. All tests were performed using Java (JDK 17) on a machine equipped with an Intel® Core™ i7-8750H CPU @ 2.20GHz (12 CPUs), and 32768MB RAM.

# Comparison of CBDRA-IS and baseline systems on all datasets

Comparison among the best resource allocation approaches corresponding to each ranking method and the baseline algorithms. We also include the ABNR with in-degree nodes. For the top-1 attack path, degree centrality with in-degree nodes shows superior performance among all approaches, as it is ranked first six times and provided the highest sum and mean of rank. For all attack paths, Katz centrality with in-degree nodes shows superior performance as it provided the highest sum of rank and mean of rank.

## Comparison among the Best Resource Allocation Approaches

| **Dataset / System** | **Degree Centrality with In-Degree Nodes** | **Betweenness Centrality with Min-Cut Edges** | **Harmonic Centrality with Min-Cut Edges** | **Katz Centrality with In-Degree Nodes** | **TrustRank with In-Degree Nodes** | **ABNR** | **Defense in Depth** | **Behavioral Decision Making** | **Risk-Based Defense** | **Min-Cut** |
|----------------------|-------------------------------------------|----------------------------------------------|-------------------------------------------|----------------------------------------|-----------------------------------|---------|----------------------|--------------------------------|-----------------------|-------------|
|                      |                                           |                                              |                                           | **Top-1 Attack Path**                  |                                   |         |                      |                                |                       |             |
| **SCADA**            | 56.27                                     | **67.47**                                    | **67.47**                                 | 51.99                                  | 51.07                             | 59.9    | 39.35               | 51.59                          | 47.86                 | **67.47**   |
| **DER.1**            | 69.94                                     | 25.19                                       | 25.19                                    | **74.26**                              | 74.24                             | 70.2    | 26.84               | 27.73                          | 28.33                 | 25.2        |
| **E-Commerce**       | **67.08**                                 | 0                                            | 0                                         | 60.16                                  | 59.95                             | 46.75   | 26.84               | 27.46                          | 28.65                 | 38.15       |
| **VOIP**             | **71.35**                                 | 0                                            | 0                                         | 70.44                                  | 69.76                             | 54.36   | 24.85               | 29.03                          | 30.06                 | 45.95       |
| **HG1**              | **85.21**                                 | 0                                            | 63.21                                     | 81.55                                  | 81.42                             | 74.53   | 63.21               | 66.78                          | 68.46                 | 63.21       |
| **HG2**              | **89.34**                                 | 37.81                                       | 37.81                                    | 61.65                                  | 61.86                             | 56.97   | 34.08               | 35.65                          | 35.88                 | 37.81       |
| **ABSNP**            | **7.39**                                  | 4.13                                        | 4.06                                     | 6.8                                    | 6.32                              | 4.15    | 5.15                | 6.94                           | 7.09                  | 4.13        |
| **ASFS3**            | **2.42**                                  | 2.06                                        | 1.84                                     | 1.96                                   | 1.89                              | 1.74    | 0.85                | 0.95                           | 1                     | 1.97        |
| **ASS2009**          | 4.90                                      | 5.16                                        | 4.99                                     | 3.89                                   | 3.92                              | 4.31    | 1.27                | 1.62                           | 1.81                  | **5.57**    |
| **AWS03**            | 8.60                                      | **19.77**                                   | 19.89                                    | 12.07                                  | 12.16                             | 8       | 4.86                | 5.31                           | 5.41                  | **19.77**   |
| **Rank First**       | **6**                                     | 2                                            | 1                                         | 1                                     | 0                                 | 0       | 0                    | 0                              | 0                     | 3           |
| **Sum of Rank**      | **85**                                    | 48.5                                        | 45.5                                     | 74                                     | 69                                | 61      | 23                  | 38                             | 45                    | 61          |
| **Mean Rank**        | **8.5**                                   | 4.85                                        | 4.55                                     | 7.4                                    | 6.9                               | 6.1     | 2.3                 | 3.8                            | 4.5                   | 6.1         |
|                      |                                           |                                              |                                           | **All Attack Paths**                 |                                   |         |                      |                                |                       |             |
| **SCADA**            | 45.69                                     | **66.57**                                   | 50.89                                    | 58.57                                  | 58.06                             | 63.69   | 39.35               | 43.27                          | 42.76                 | 50.89       |
| **DER.1**            | 52.78                                     | 66.04                                       | 25.69                                    | 72.4                                   | **72.11**                         | 70.52   | 26.84               | 27.55                          | 27.97                 | 25.69       |
| **E-Commerce**       | **67.08**                                 | 0                                            | 0                                         | 55.12                                  | 54.84                             | 43.15   | 26.84               | 25.45                          | 25.74                 | 38.78       |
| **VOIP**             | **71.35**                                 | 0                                            | 0                                         | 67.83                                  | 67.36                             | 52.05   | 24.85               | 28.25                          | 28.82                 | 39.79       |
| **HG1**              | 77                                        | 0                                            | 71.41                                    | **84.78**                              | 84.69                             | 78.98   | 63.21               | 65.83                          | 66.75                 | 71.41       |
| **HG2**              | 50.96                                     | 40.9                                        | 39.35                                    | 59.98                                  | **60.2**                          | 58.26   | 34.08               | 35.31                          | 35.34                 | 39.35       |
| **ABSNP**            | 11.2                                      | 5.05                                        | **14.24**                                | 11.36                                  | 11.07                             | 9.37    | 6.7                 | 7.02                           | 7.25                  | 14.3        |
| **ASFS3**            | 4.19                                      | 6.79                                        | 6.85                                     | 6.29                                   | 6.51                              | 6.74    | 2.44                | 2.48                           | 2.49                  | **6.86**    |
| **ASS2009**          | 5.15                                      | 6.88                                        | 12.05                                    | 6.91                                   | 7.1                               | 7.22    | 2.28                | 2.41                           | 2.5                   | **12.16**   |
| **AWS03**            | 8.06                                      | 3.72                                        | 20.22                                    | 11.54                                  | 11.62                             | 8.5     | 4.61                | 4.77                           | 4.91                  | **20.14**   |
| **Rank First**       | 2                                         | 1                                            | 1                                         | 1                                     | 2                                 | 0       | 0                    | 0                              | 0                     | **3**       |
| **Sum of Rank**      | 64                                        | 42                                          | 57                                       | **81**                                 | 78                                | 73      | 21                  | 29                             | 37                    | 68          |
| **Mean Rank**        | 6.4                                       | 4.2                                         | 5.7                                      | **8.1**                                | 7.8                               | 7.3     | 2.1                 | 2.9                            | 3.7                   | 6.8         |

# How To Run The Code (read carefully please)

1) Download intellIJ IDEA latest version
2) Dounload JDK 17 or higher
3) Set up the environment variable for the bin folder of the JDK 17+
4) Open the IDEA
5) Open the project
6) Make sure you are connected to the internet
7) Wait while the IDEA download all the libraries that are included as dependencies in the pom XML file
8) Go to the main package. Then you will find many packages where each one is corresponding to different approach
9) Go to the approach you want to run
10) Set up the desired hyperparameters
11) Run the file to see the results.

# References

For the mean of being so accurate, you will find all references of the used approaches and the used datasets in the paper

# Contact With Authors

Send email to the following authors for any question about this work, and it is our pleasure to ansawer your question.

Mohammad Aleiadeh, mraleiad@iu.edu or maleiade@purdue.edu

dr. Mustafa Abdallah, mabdall@iu.edu or abdalla0@purdue.edu

Note: Authors are arranged alphabetically.
