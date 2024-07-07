# CBDRA-IS: Centrality_Based_Network_Resource_Allocation_Approaches

# Abstract

Interdependent systems are facing an escalating number of cybersecurity threats. This paper explores security decision-making within these intricate and interdependent systems. We have developed a resource allocation framework aimed at bolstering the security of such systems, which are managed by a single rational (logical) defender. Our framework uses cyber attack graphs to model these systems and their vulnerabilities. We propose four defense mechanisms that integrate various network analysis algorithms, including degree centrality, betweenness centrality, harmonic centrality, TrustRank algorithm, Katz centrality, and an Average Based Node Ranking (ABNR). These algorithms help determine the importance of different critical assets within the system to provide insights into asset significance. The resource allocation mechanisms we employed draw on existing graph theories commonly used in graphical models, such as Adjacent Nodes, In-degree Nodes, Min-Cut Edges, and Markov Blanket. We combine each ranking algorithm with the four allocation techniques. Our methods demonstrate low sensitivity to the number of simultaneous attacks on interdependent systems. We validated our decision-making framework using fifteen attack graphs, which represent multiple real-world interdependent systems. We measured the security improvements achieved through our defense methods against four well-known resource allocation algorithms such as behavioral defender and risk-based defense. In most test cases, our framework achieved better resource allocation outcomes than the baselines. According to the results with integrating the Friedman statistical test, our approach's outcomes are superior. Our framework enhances security decision-making across various scenarios, including the top-1 attack path(s) and all attack paths corresponding to each entry-asset variation. We have made the full implementation of our resource allocation framework available to the research community.

# Framework

As shown in this figure, the framework takes as input an attack graph, which includes various entry nodes (points where attackers can initiate an attack) and asset nodes (critical components for the defender) along with their interconnections, highlighting the security vulnerabilities between these nodes. Given this attack graph, the Genetic Algorithm (GA) is used to generate the top-1 and all potential attack paths that attackers might exploit to compromise the system's assets. The GA incorporates a path encoding scheme that prevents revisiting nodes and encoding sink nodes in the paths provided by the algorithm. It also employs a multi-objective fitness function that considers asset loss to evaluate the feasibility of an attack path, determining whether it should survive or be replaced by a new one from the search space of the problem. Following the generation of potential attack paths, the defender proceeds to distribute her security resources across different attack paths, each connecting an entry node to a critical asset within the system. Our methodology for security resource allocation is based on key concepts from graph theory literature, such as in-degree nodes, adjacent nodes, Min-Cut edges, and the Markov Blanket. Furthermore, we utilize various node ranking algorithms, including the TrustRank algorithm and Katz centrality, to derive more detailed information about the system's assets (their importance) within the attack graph. This enables a proportional allocation of available security resources using these graph-theoretic principles, reflecting the importance of the assets and aiding the human operator. Once the resource allocation decisions are finalized, we calculate the relative reduction in expected security costs (determined as the relative difference between costs under our allocation methods and initial investment costs) to evaluate and analyze the effectiveness of the proposed framework

![Screenshot (494)](https://github.com/Mohammed-Ryiad-Eiadeh/Centrality_Based_Network_Resource_Allocation_Approaches/assets/93108547/baa51f18-c711-437d-b763-58550f0d19a8)

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
| SCADA [12] | 13 | 20 | 6 | Directed |
| DER.1 [13] | 22 | 32 | 6 | Directed |
| E-Commerce [14] | 20 | 32 | 4 | Directed |
| VOIP [14] | 22 | 35 | 4 | Directed |
| HG1 [15] | 7 | 10 | 2 | Directed |
| HG2 [15] | 15 | 22 | 5 | Directed |
| ABSNP [16] | 17 | 122 | 6 | Directed |
| ASFS3 [16] | 27 | 163 | 9 | Directed |
| ASS2009 [16] | 31 | 211 | 9 | Directed |
| AWS03 [16] | 42 | 152 | 15 | Directed |

Note: all of these datasets are stored in the project directory and is called dynamically so no need to set up their paths.

# Parameter Configuration of Our Experiments
We begin by outlining the key hyperparameters used in different components of our framework. The parameters for GA were chosen as follows: maximum iterations ($M=100$), population size (number of potential attack paths) ($N=500$), mating probability ($m_p=0.2$), mutation rate ($m_r=0.2$), and weight factor ($Wf=0.001$). The defender's security budget is fixed at $S=10$ for all experiments, and the maximum iterations for the TrustRank algorithm is set to 100, with epsilon ($\epsilon$) at 0.0001. Moreover, the initial investments over the edges of the ten used datasets are randomly generated within the range of (0,1). And for Friedman test, the significance level is set to $SL=0.05$. We emphasize that the advantages of our proposed defense (resource allocation) strategies are applicable for any security budget. For the behavioral defender baseline~\cite{Abdallah2020}, the behavioral level ($a$) is set to 0.5. All tests were performed using Java (JDK 17) on a machine equipped with an Intel® Core™ i7-8750H CPU @ 2.20GHz (12 CPUs), and 32768MB RAM.

# Comparison of CBDRA-IS and baseline systems on all datasets
