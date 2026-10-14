import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class SmartFactoryUI {

    private static final Scanner SC = new Scanner(System.in);

    // Sample maintenance reports used throughout the console demo.
    private static final String[][] REPORTS = {
        {"R-01", "Press Unit", "Overheating detected during the morning shift."},
        {"R-02", "Conveyor Line", "Unusual vibration detected near the drive motor."},
        {"R-03", "Cooling Station", "Cooling flow is low and temperature is rising."},
        {"R-04", "Packaging Line", "Sensor failure stopped the packaging sequence."},
        {"R-05", "Conveyor Line", "Oil leakage detected near the motor housing."},
        {"R-06", "Press Unit", "Overheating detected again after restart."}
    };

    private static final String[] FAULT_TERMS = {
        "overheating",
        "vibration",
        "low cooling flow",
        "sensor failure",
        "oil leakage",
        "pressure drop",
        "belt misalignment",
        "motor noise"
    };

    public static void main(String[] args) {
        printBanner();

        while (true) {
            printMenu();
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1 -> searchMachineIds();
                case 2 -> findRepeatedPattern();
                case 3 -> detectFaultKeywords();
                case 4 -> searchProductionCodes();
                case 5 -> suggestFaultTerm();
                case 6 -> showReports();
                case 0 -> {
                    System.out.println("\nSmartFactory session ended. Have a productive day!");
                    SC.close();
                    return;
                }
                default -> System.out.println("\nPlease choose an option from 0 to 6.");
            }

            System.out.println();
            pause();
        }
    }

    private static void printBanner() {
        System.out.println("====================================================");
        System.out.println("       SMARTFACTORY | PRODUCTION ANALYTICS");
        System.out.println("====================================================");
        System.out.println("KMP, Z-Function, Aho-Corasick, Rabin-Karp and Edit Distance.");
        System.out.println("All records and capacities are illustrative demo data.");
    }

    private static void printMenu() {
        System.out.println("----------------------------------------------------");
        System.out.println("1. Search machine IDs               [KMP]");
        System.out.println("2. Find patterns in reports         [Z-Function]");
        System.out.println("3. Detect multiple fault keywords   [Aho-Corasick]");
        System.out.println("4. Search production/inventory codes [Rabin-Karp]");
        System.out.println("5. Correct a fault term typo        [Edit Distance]");
        System.out.println("6. View sample factory data");
        System.out.println("0. Exit");
        System.out.println("----------------------------------------------------");
    }

    private static void searchReports() {
        System.out.println("\n[REPORT SEARCH]");
        System.out.println("Example searches: overheating, vibration, sensor failure");
        String query = readNonEmpty("Enter a word or phrase to find: ").toLowerCase();

        int matches = 0;
        for (String[] report : REPORTS) {
            String text = report[2].toLowerCase();
            List<Integer> positions = kmp(text, query);

            if (!positions.isEmpty()) {
                matches++;
                System.out.println("\nReport " + report[0] + " | Area: " + report[1]);
                System.out.println("Finding: " + report[2]);
                System.out.println("Match position(s) in finding: " + positions);
            }
        }

        if (matches == 0) {
            System.out.println("\nNo reports matched \"" + query + "\".");
            System.out.println("Tip: try a shorter term such as \"motor\" or \"cooling\".");
        } else {
            System.out.println("\nSearch complete: " + matches + " report(s) matched.");
        }
    }

    private static void suggestFaultTerm() {
        System.out.println("\n[FAULT TERM CHECK]");
        System.out.println("Type a fault description or a short fault term.");
        System.out.println("Example: overheatng");
        String typed = readNonEmpty("Enter fault term: ").toLowerCase();

        String bestTerm = null;
        int bestDistance = Integer.MAX_VALUE;
        List<String> tiedTerms = new ArrayList<>();

        for (String candidate : FAULT_TERMS) {
            int distance = editDistance(typed, candidate);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestTerm = candidate;
                tiedTerms.clear();
                tiedTerms.add(candidate);
            } else if (distance == bestDistance) {
                tiedTerms.add(candidate);
            }
        }

        System.out.println("\nClosest registered fault term(s):");
        for (String candidate : tiedTerms) {
            System.out.println("  - " + candidate);
        }
        System.out.println("Minimum edits required: " + bestDistance);

        if (bestDistance == 0) {
            System.out.println("Result: Exact term match.");
        } else if (bestDistance <= 2) {
            System.out.println("Result: Likely typing variation. Please confirm before using it.");
        } else {
            System.out.println("Result: No close match. Review the fault description manually.");
        }
        System.out.println("\nAvailable fault terms: " + String.join(", ", FAULT_TERMS));
    }

    private static void optimizeProductionFlow() {
        System.out.println("\n[PRODUCTION FLOW ESTIMATE]");
        System.out.println("Sample production route and capacities (units per hour):");
        System.out.println("Raw Material Store -> Cutting Station : 12");
        System.out.println("Raw Material Store -> Inspection      : 8");
        System.out.println("Cutting Station -> Assembly           : 7");
        System.out.println("Cutting Station -> Inspection         : 3");
        System.out.println("Inspection -> Assembly                : 5");
        System.out.println("Assembly -> Packaging                 : 10");
        System.out.println("Inspection -> Packaging                : 4");

        // Named stations are mapped internally to graph indices.
        String[] stations = {
            "Raw Material Store", "Cutting Station",
            "Inspection", "Assembly", "Packaging"
        };

        int source = 0;
        int sink = 4;
        Dinic network = new Dinic(stations.length);

        network.addEdge(0, 1, 12);
        network.addEdge(0, 2, 8);
        network.addEdge(1, 3, 7);
        network.addEdge(1, 2, 3);
        network.addEdge(2, 3, 5);
        network.addEdge(3, 4, 10);
        network.addEdge(2, 4, 4);

        long maximumFlow = network.maxFlow(source, sink);

        System.out.println("\nSource: " + stations[source]);
        System.out.println("Destination: " + stations[sink]);
        System.out.println("Maximum estimated throughput: " + maximumFlow + " units/hour");
        System.out.println("Meaning: based on the sample capacities, this is the largest");
        System.out.println("flow that can move from raw materials to packaging per hour.");
        System.out.println("Note: these are demonstration values, not live factory measurements.");
    }

    private static void showReports() {
        System.out.println("\n[SAMPLE MAINTENANCE REPORTS]");
        for (String[] report : REPORTS) {
            System.out.println("\n" + report[0] + " | " + report[1]);
            System.out.println("  " + report[2]);
        }
    }

    // KMP: returns all zero-based positions where pattern occurs in text.
    private static List<Integer> kmp(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        if (pattern.isEmpty() || text.isEmpty() || pattern.length() > text.length()) {
            return result;
        }

        int[] lps = new int[pattern.length()];
        for (int i = 1, len = 0; i < pattern.length();) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }

        int i = 0;
        int j = 0;
        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == pattern.length()) {
                    result.add(i - j);
                    j = lps[j - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return result;
    }

    // Levenshtein edit distance: insertions, deletions and substitutions.
    private static int editDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[a.length()][b.length()];
    }


    // ------------------------- M2 EXTENSIONS: SA-IS + SUFFIX AUTOMATON -------------------------

    private static int[] suffixArraySAIS(String text) {
        int n = text.length();
        if (n == 0) return new int[0];
        int[] s = new int[n];
        int upper = 0;
        for (int i = 0; i < n; i++) { s[i] = text.charAt(i); upper = Math.max(upper, s[i]); }
        return saIs(s, upper);
    }

    // SA-IS induced sorting implementation for integer alphabets, adapted to the standard
    // LMS-substring recursion approach. Returns suffix positions in lexicographic order.
    private static int[] saIs(int[] s, int upper) {
        int n = s.length;
        if (n == 0) return new int[0];
        if (n == 1) return new int[]{0};
        if (n == 2) return s[0] < s[1] ? new int[]{0, 1} : new int[]{1, 0};

        int[] sa = new int[n];
        boolean[] ls = new boolean[n];
        for (int i = n - 2; i >= 0; i--)
            ls[i] = (s[i] == s[i + 1]) ? ls[i + 1] : (s[i] < s[i + 1]);

        int[] sumL = new int[upper + 2], sumS = new int[upper + 2];
        for (int i = 0; i < n; i++) {
            if (!ls[i]) sumS[s[i]]++;
            else sumL[s[i] + 1]++;
        }
        for (int i = 0; i <= upper; i++) {
            sumS[i] += sumL[i];
            sumL[i + 1] += sumS[i];
        }

        // Use an explicit helper to avoid mutable-capture complications.
        int[] lmsMap = new int[n + 1];
        Arrays.fill(lmsMap, -1);
        int m = 0;
        for (int i = 1; i < n; i++) if (!ls[i - 1] && ls[i]) lmsMap[i] = m++;
        int[] lms = new int[m];
        for (int i = 1, p = 0; i < n; i++) if (!ls[i - 1] && ls[i]) lms[p++] = i;

        Arrays.fill(sa, -1);
        int[] buf = sumS.clone();
        for (int d : lms) if (d < n) sa[buf[s[d]]++] = d;
        buf = sumL.clone();
        sa[buf[s[n - 1]]++] = n - 1;
        for (int i = 0; i < n; i++) {
            int v = sa[i];
            if (v >= 1 && !ls[v - 1]) sa[buf[s[v - 1]]++] = v - 1;
        }
        buf = sumL.clone();
        for (int i = n - 1; i >= 0; i--) {
            int v = sa[i];
            if (v >= 1 && ls[v - 1]) sa[--buf[s[v - 1] + 1]] = v - 1;
        }

        if (m > 0) {
            int[] sortedLms = new int[m];
            for (int i = 0, p = 0; i < n; i++) {
                int v = sa[i];
                if (v >= 0 && lmsMap[v] != -1) sortedLms[p++] = v;
            }
            int[] recS = new int[m];
            int recUpper = 0;
            recS[lmsMap[sortedLms[0]]] = 0;
            for (int i = 1; i < m; i++) {
                int left = sortedLms[i - 1], right = sortedLms[i];
                int leftEnd = (lmsMap[left] + 1 < m) ? lms[lmsMap[left] + 1] : n;
                int rightEnd = (lmsMap[right] + 1 < m) ? lms[lmsMap[right] + 1] : n;
                boolean same = leftEnd - left == rightEnd - right;
                if (same) {
                    while (left < leftEnd && s[left] == s[right]) { left++; right++; }
                    if (left == n || s[left] != s[right]) same = false;
                }
                if (!same) recUpper++;
                recS[lmsMap[sortedLms[i]]] = recUpper;
            }
            int[] recSA = saIs(recS, recUpper);
            for (int i = 0; i < m; i++) sortedLms[i] = lms[recSA[i]];
            Arrays.fill(sa, -1);
            buf = sumS.clone();
            for (int d : sortedLms) sa[buf[s[d]]++] = d;
            buf = sumL.clone();
            sa[buf[s[n - 1]]++] = n - 1;
            for (int i = 0; i < n; i++) {
                int v = sa[i];
                if (v >= 1 && !ls[v - 1]) sa[buf[s[v - 1]]++] = v - 1;
            }
            buf = sumL.clone();
            for (int i = n - 1; i >= 0; i--) {
                int v = sa[i];
                if (v >= 1 && ls[v - 1]) sa[--buf[s[v - 1] + 1]] = v - 1;
            }
        }
        return sa;
    }

    private static long countDistinctSubstringsSuffixAutomaton(String s) {
        class State { int len, link = -1; Map<Character, Integer> next = new HashMap<>(); }
        List<State> st = new ArrayList<>();
        st.add(new State());
        int last = 0;
        for (char c : s.toCharArray()) {
            int cur = st.size(); State cs = new State(); cs.len = st.get(last).len + 1; st.add(cs);
            int p = last;
            while (p != -1 && !st.get(p).next.containsKey(c)) { st.get(p).next.put(c, cur); p = st.get(p).link; }
            if (p == -1) cs.link = 0;
            else {
                int q = st.get(p).next.get(c);
                if (st.get(p).len + 1 == st.get(q).len) cs.link = q;
                else {
                    int clone = st.size(); State cl = new State(); cl.len = st.get(p).len + 1;
                    cl.link = st.get(q).link; cl.next.putAll(st.get(q).next); st.add(cl);
                    while (p != -1 && Objects.equals(st.get(p).next.get(c), q)) { st.get(p).next.put(c, clone); p = st.get(p).link; }
                    st.get(q).link = cs.link = clone;
                }
            }
            last = cur;
        }
        long total = 0;
        for (int i = 1; i < st.size(); i++) total += st.get(i).len - st.get(st.get(i).link).len;
        return total;
    }

    // ------------------------- M3 EXTENSIONS: ADVANCED DP -------------------------

    private static void advancedDpVariants() {
        String a = readNonEmpty("First entry (example MCH-10I): ").trim().toLowerCase(Locale.ROOT);
        String b = readNonEmpty("Intended entry (example MCH-101): ").trim().toLowerCase(Locale.ROOT);
        if (a.isEmpty() || b.isEmpty()) { System.out.println("Entries cannot be empty."); return; }
        System.out.println("Levenshtein distance: " + editDistance(a, b));
        System.out.println("Damerau-Levenshtein distance: " + damerauDistance(a, b));
        System.out.println("Weighted edit distance (substitution=2, insertion/deletion=1): " + weightedEditDistance(a, b));
        System.out.println("These distances can help rank likely corrections; the user should confirm before updating factory records.");

        int[] benefit = {8, 6, 5, 4};
        int[] cost = {3, 2, 2, 1};
        int budget = 5;
        System.out.println("Bitmask DP resource-selection example:");
        System.out.println("  Activities: calibration, sensor check, lubrication, inspection");
        System.out.println("  Costs: " + Arrays.toString(cost) + "; benefits: " + Arrays.toString(benefit) + "; budget: " + budget);
        int best = 0, bestMask = 0;
        for (int mask = 0; mask < (1 << benefit.length); mask++) {
            int c = 0, v = 0;
            for (int i = 0; i < benefit.length; i++) if ((mask & (1 << i)) != 0) { c += cost[i]; v += benefit[i]; }
            if (c <= budget && v > best) { best = v; bestMask = mask; }
        }
        List<String> selected = new ArrayList<>();
        String[] labels = {"calibration", "sensor check", "lubrication", "inspection"};
        for (int i = 0; i < labels.length; i++) if ((bestMask & (1 << i)) != 0) selected.add(labels[i]);
        System.out.println("  Best subset: " + selected + "; total benefit=" + best);
        System.out.println("Bitmask DP is appropriate for small resource/activity sets; exhaustive subset time is O(n*2^n).");
        int[] dimensions = {10, 30, 5, 60};
        System.out.println("Matrix-chain DP dimensions " + Arrays.toString(dimensions)
            + " -> minimum scalar multiplications: " + matrixChainCost(dimensions));
    }

    private static long matrixChainCost(int[] dimensions) {
        int n = dimensions.length - 1;
        long[][] dp = new long[n][n];
        for (int length = 2; length <= n; length++) {
            for (int i = 0; i + length - 1 < n; i++) {
                int j = i + length - 1;
                dp[i][j] = Long.MAX_VALUE / 4;
                for (int k = i; k < j; k++) {
                    long cost = dp[i][k] + dp[k + 1][j]
                        + (long) dimensions[i] * dimensions[k + 1] * dimensions[j + 1];
                    dp[i][j] = Math.min(dp[i][j], cost);
                }
            }
        }
        return dp[0][n - 1];
    }

    private static int damerauDistance(String a, String b) {
        int[][] d = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) d[i][0] = i;
        for (int j = 0; j <= b.length(); j++) d[0][j] = j;
        for (int i = 1; i <= a.length(); i++) for (int j = 1; j <= b.length(); j++) {
            int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
            d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), d[i - 1][j - 1] + cost);
            if (i > 1 && j > 1 && a.charAt(i - 1) == b.charAt(j - 2) && a.charAt(i - 2) == b.charAt(j - 1))
                d[i][j] = Math.min(d[i][j], d[i - 2][j - 2] + 1);
        }
        return d[a.length()][b.length()];
    }
    private static int weightedEditDistance(String a, String b) {
        int[][] d = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) d[i][0] = i;
        for (int j = 0; j <= b.length(); j++) d[0][j] = j;
        for (int i = 1; i <= a.length(); i++) for (int j = 1; j <= b.length(); j++) {
            int sub = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 2;
            d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), d[i - 1][j - 1] + sub);
        }
        return d[a.length()][b.length()];
    }

    // ------------------------- M4 EXTENSIONS: FORD-FULKERSON, EDMONDS-KARP, MIN-CUT -------------------------

    private static void compareFlowAlgorithms() {
        int n = 6, source = 0, sink = 5;
        int[][] capacity = new int[n][n];
        capacity[0][1]=16; capacity[0][2]=13; capacity[1][2]=10; capacity[2][1]=4;
        capacity[1][3]=12; capacity[3][2]=9; capacity[2][4]=14; capacity[4][3]=7;
        capacity[3][5]=20; capacity[4][5]=4;
        int[][] ffResidual = copyMatrix(capacity);
        int ff = fordFulkersonDfs(ffResidual, source, sink);
        int[][] ekResidual = copyMatrix(capacity);
        int ek = edmondsKarp(ekResidual, source, sink);
        Dinic dinic = new Dinic(n);
        for (int u=0;u<n;u++) for(int v=0;v<n;v++) if(capacity[u][v]>0) dinic.addEdge(u,v,capacity[u][v]);
        long di = dinic.maxFlow(source, sink);
        System.out.println("Demo flow network uses vertices S, A, B, C, D, T.");
        System.out.println("Ford-Fulkerson (DFS path selection) max flow: " + ff);
        System.out.println("Edmonds-Karp (BFS augmenting paths) max flow: " + ek);
        System.out.println("Dinic (level graph + blocking flow) max flow: " + di);
        System.out.println("All three should agree on the maximum flow for this network.");
        boolean[] reachable = residualReachable(ekResidual, source);
        List<String> cut = new ArrayList<>();
        String[] names={"S","A","B","C","D","T"};
        int cutCapacity=0;
        for(int u=0;u<n;u++) for(int v=0;v<n;v++)
            if(reachable[u] && !reachable[v] && capacity[u][v]>0) { cut.add(names[u]+"->"+names[v]+" ("+capacity[u][v]+")"); cutCapacity+=capacity[u][v]; }
        System.out.println("Min-cut edges after Edmonds-Karp: " + cut);
        System.out.println("Min-cut capacity: " + cutCapacity + " (must equal max flow).");
        System.out.println("Edmonds-Karp complexity: O(VE^2); Dinic general bound: O(V^2 E).");
    }
    private static int[][] copyMatrix(int[][] a) { int[][] b=new int[a.length][]; for(int i=0;i<a.length;i++) b[i]=a[i].clone(); return b; }
    private static int fordFulkersonDfs(int[][] residual, int s, int t) {
        int flow=0, n=residual.length;
        while(true) {
            boolean[] seen=new boolean[n]; int pushed=dfsResidual(residual,s,t,Integer.MAX_VALUE,seen);
            if(pushed==0) return flow; flow+=pushed;
        }
    }
    private static int dfsResidual(int[][] r,int u,int t,int f,boolean[] seen) {
        if(u==t)return f; seen[u]=true;
        for(int v=0;v<r.length;v++) if(!seen[v]&&r[u][v]>0) {
            int got=dfsResidual(r,v,t,Math.min(f,r[u][v]),seen);
            if(got>0){r[u][v]-=got;r[v][u]+=got;return got;}
        }
        return 0;
    }
    private static int edmondsKarp(int[][] residual,int s,int t) {
        int flow=0,n=residual.length;
        while(true) {
            int[] parent=new int[n]; Arrays.fill(parent,-1); parent[s]=s;
            Queue<Integer> q=new ArrayDeque<>();q.add(s);
            while(!q.isEmpty()&&parent[t]==-1) {
                int u=q.remove();
                for(int v=0;v<n;v++) if(parent[v]==-1&&residual[u][v]>0){parent[v]=u;q.add(v);}
            }
            if(parent[t]==-1)return flow;
            int add=Integer.MAX_VALUE;
            for(int v=t;v!=s;v=parent[v])add=Math.min(add,residual[parent[v]][v]);
            for(int v=t;v!=s;v=parent[v]){int u=parent[v];residual[u][v]-=add;residual[v][u]+=add;}
            flow+=add;
        }
    }
    private static boolean[] residualReachable(int[][] r,int s) {
        boolean[] seen=new boolean[r.length];Queue<Integer>q=new ArrayDeque<>();q.add(s);seen[s]=true;
        while(!q.isEmpty()){int u=q.remove();for(int v=0;v<r.length;v++)if(!seen[v]&&r[u][v]>0){seen[v]=true;q.add(v);}}
        return seen;
    }

    // ------------------------- M5 EXTENSIONS: SMALL SAT / 3-SAT SOLVER -------------------------

    private static void solveSatDemo() {
        System.out.println("Constraint example:");
        System.out.println("  (A OR B OR C) AND (NOT A OR NOT B OR C) AND (A OR NOT B OR NOT C)");
        System.out.println("Variables: A=Press runs, B=Packaging runs, C=Inspection runs.");
        // Each clause contains exactly three literals: this is a small 3-SAT instance.
        int[][] clauses = {{1,2,3},{-1,-2,3},{1,-2,-3}};
        int variables = 3, solutions = 0;
        for (int mask = 0; mask < (1 << variables); mask++) {
            boolean[] assignment = new boolean[variables + 1];
            for (int v=1;v<=variables;v++) assignment[v]=(mask & (1 << (v-1)))!=0;
            boolean all=true;
            for(int[] clause:clauses) {
                boolean sat=false;
                for(int lit:clause) sat |= lit>0 ? assignment[lit] : !assignment[-lit];
                all &= sat;
            }
            if(all) {
                solutions++;
                System.out.printf("Satisfying assignment: A=%s, B=%s, C=%s%n", assignment[1], assignment[2], assignment[3]);
            }
        }
        System.out.println("Satisfying assignments found: " + solutions);
        System.out.println("This is exhaustive SAT search for a tiny formula, O(2^n * numberOfClauses).");
        System.out.println("Reduction concepts: 3-SAT -> CLIQUE -> INDEPENDENT-SET -> VERTEX-COVER are theoretical polynomial reductions; they are not a proof that every production instance is easy to solve.");
    }


    // ------------------------- M1: MORE STRING ALGORITHMS -------------------------

    private static final String[][] MACHINE_DATA = {
        {"MCH-101", "Press Unit"}, {"MCH-102", "Conveyor Line"},
        {"MCH-203", "Cooling Station"}, {"MCH-204", "Packaging Line"},
        {"MCH-305", "Assembly Robot"}, {"MCH-306", "Inspection Unit"}
    };
    private static final String[] PRODUCTION_CODES = {
        "PRD-AX-2041", "PRD-BX-1088", "INV-STEEL-771",
        "INV-COPPER-225", "ORD-2026-0042", "ORD-2026-0099"
    };

    private static void searchMachineIds() {
        String query = readNonEmpty("Enter all/part of a machine ID (e.g. MCH-10): ").toLowerCase(Locale.ROOT);
        int found = 0;
        for (String[] machine : MACHINE_DATA) {
            if (!kmp(machine[0].toLowerCase(Locale.ROOT), query).isEmpty()) {
                System.out.println(machine[0] + " | " + machine[1]);
                found++;
            }
        }
        System.out.println("Machine matches: " + found + " | KMP time: O(n + m) per ID.");
    }

    private static void searchProductionCodes() {
        String query = readNonEmpty("Enter all/part of a production or inventory code: ").toLowerCase(Locale.ROOT);
        int found = 0;
        for (String code : PRODUCTION_CODES) {
            if (rabinKarpContains(code.toLowerCase(Locale.ROOT), query)) {
                System.out.println("Matched code: " + code);
                found++;
            }
        }
        System.out.println("Code matches: " + found + " | Rabin-Karp average: O(n + m); verifies hash matches.");
    }

    private static boolean rabinKarpContains(String text, String pattern) {
        if (pattern.isEmpty()) return true;
        if (pattern.length() > text.length()) return false;
        final long mod = 1_000_000_007L, base = 257L;
        long pHash = 0, wHash = 0, power = 1;
        for (int i = 0; i < pattern.length(); i++) {
            pHash = (pHash * base + pattern.charAt(i)) % mod;
            wHash = (wHash * base + text.charAt(i)) % mod;
            if (i < pattern.length() - 1) power = power * base % mod;
        }
        for (int i = 0; i <= text.length() - pattern.length(); i++) {
            if (pHash == wHash && text.regionMatches(i, pattern, 0, pattern.length())) return true;
            if (i < text.length() - pattern.length()) {
                wHash = (wHash - text.charAt(i) * power % mod + mod) % mod;
                wHash = (wHash * base + text.charAt(i + pattern.length())) % mod;
            }
        }
        return false;
    }

    private static void detectFaultKeywords() {
        System.out.println("Aho-Corasick multi-keyword scan:");
        int total = 0;
        for (String[] report : REPORTS) {
            List<String> hits = ahoMatches(report[2], Arrays.asList(FAULT_TERMS));
            if (!hits.isEmpty()) {
                System.out.println(report[0] + " | " + report[1] + " -> " + hits);
                total += hits.size();
            }
        }
        System.out.println("Fault keyword hits: " + total);
        System.out.println("Aho-Corasick scans text in O(text length + number of matches), after trie construction.");
    }

    private static class ACNode {
        Map<Character, ACNode> next = new HashMap<>();
        ACNode fail;
        Set<String> out = new LinkedHashSet<>();
    }
    private static List<String> ahoMatches(String text, List<String> patterns) {
        ACNode root = new ACNode();
        for (String pattern : patterns) {
            ACNode node = root;
            for (char c : pattern.toLowerCase(Locale.ROOT).toCharArray())
                node = node.next.computeIfAbsent(c, k -> new ACNode());
            node.out.add(pattern);
        }
        Queue<ACNode> q = new ArrayDeque<>();
        for (ACNode child : root.next.values()) { child.fail = root; q.add(child); }
        while (!q.isEmpty()) {
            ACNode cur = q.remove();
            for (Map.Entry<Character, ACNode> e : cur.next.entrySet()) {
                char c = e.getKey(); ACNode child = e.getValue(); ACNode f = cur.fail;
                while (f != root && !f.next.containsKey(c)) f = f.fail;
                child.fail = f.next.getOrDefault(c, root);
                child.out.addAll(child.fail.out);
                q.add(child);
            }
        }
        Set<String> found = new LinkedHashSet<>();
        ACNode state = root;
        for (char c : text.toLowerCase(Locale.ROOT).toCharArray()) {
            while (state != root && !state.next.containsKey(c)) state = state.fail;
            state = state.next.getOrDefault(c, root);
            found.addAll(state.out);
        }
        return new ArrayList<>(found);
    }

    private static void findRepeatedPattern() {
        String text = readNonEmpty("Enter report text: ").toLowerCase(Locale.ROOT);
        String pattern = readNonEmpty("Enter pattern to locate: ").toLowerCase(Locale.ROOT);
        String joined = pattern + '\u0001' + text;
        int[] z = zFunction(joined);
        int count = 0, start = pattern.length() + 1;
        for (int i = start; i < z.length; i++) if (z[i] >= pattern.length()) {
            System.out.println("Pattern found at index " + (i - start));
            count++;
        }
        System.out.println(count == 0 ? "No match." : "Occurrences: " + count);
        System.out.println("Z-Function complexity: O(n + m).");
    }
    private static int[] zFunction(String s) {
        int n=s.length(); int[] z=new int[n]; int l=0,r=0;
        for(int i=1;i<n;i++){
            if(i<=r) z[i]=Math.min(r-i+1,z[i-l]);
            while(i+z[i]<n && s.charAt(z[i])==s.charAt(i+z[i])) z[i]++;
            if(i+z[i]-1>r){l=i;r=i+z[i]-1;}
        }
        return z;
    }

    private static void indexAndCompareReports() {
        String text = readNonEmpty("Enter a report sentence to index: ").toLowerCase(Locale.ROOT);
        int[] sa = suffixArraySAIS(text);
        int[] lcp = lcpForSuffixArray(text, sa);
        System.out.println("SA-IS suffix array: " + Arrays.toString(sa));
        System.out.println("LCP/Kasai array:    " + Arrays.toString(lcp));
        int best=0, idx=-1;
        for(int i=1;i<lcp.length;i++) if(lcp[i]>best){best=lcp[i];idx=i;}
        if(best>0) System.out.println("Longest repeated substring: \""+text.substring(sa[idx],sa[idx]+best)+"\"");
        else System.out.println("No repeated substring found.");
        System.out.println("Suffix automaton distinct-substring count: "+countDistinctSubstringsSuffixAutomaton(text));
        System.out.println("SA-IS: O(n) for integer alphabets; LCP/Kasai: O(n).");
    }
    private static int[] lcpForSuffixArray(String s,int[] sa) {
        int n=s.length(),h=0;int[] rank=new int[n],lcp=new int[n];
        for(int i=0;i<n;i++)rank[sa[i]]=i;
        for(int i=0;i<n;i++){
            int r=rank[i];if(r==0)continue;int j=sa[r-1];
            while(i+h<n&&j+h<n&&s.charAt(i+h)==s.charAt(j+h))h++;
            lcp[r]=h;if(h>0)h--;
        }
        return lcp;
    }

    // ------------------------- M4/M5: MATCHING AND VERTEX COVER -------------------------

    private static void assignWorkers() {
        String[] workers={"Worker A","Worker B","Worker C","Worker D"};
        String[] machines={"Press Unit","Conveyor","Inspection","Packaging"};
        int[][] eligible={{1,1,0,0},{0,1,1,0},{1,0,1,1},{0,0,1,1}};
        int[] owner=new int[machines.length];Arrays.fill(owner,-1);int assigned=0;
        for(int w=0;w<workers.length;w++) if(matchWorker(w,eligible,new boolean[machines.length],owner)) assigned++;
        System.out.println("Maximum eligible assignments: "+assigned);
        for(int m=0;m<owner.length;m++) if(owner[m]>=0)System.out.println(workers[owner[m]]+" -> "+machines[m]);
        System.out.println("Bipartite matching augmenting-path implementation: O(VE).");
    }
    private static boolean matchWorker(int w,int[][] e,boolean[] seen,int[] owner){
        for(int m=0;m<e[w].length;m++)if(e[w][m]==1&&!seen[m]){
            seen[m]=true;
            if(owner[m]==-1||matchWorker(owner[m],e,seen,owner)){owner[m]=w;return true;}
        }
        return false;
    }

    private static void coverConflicts() {
        String[] acts={"Press maintenance","Conveyor calibration","Packaging run","Inspection cycle","Robot service"};
        int[][] edges={{0,1},{1,2},{2,3},{3,4},{0,4}};
        boolean[] covered=new boolean[edges.length];Set<Integer> cover=new LinkedHashSet<>();
        for(int i=0;i<edges.length;i++)if(!covered[i]){
            int u=edges[i][0],v=edges[i][1];cover.add(u);cover.add(v);
            for(int j=0;j<edges.length;j++)if(edges[j][0]==u||edges[j][1]==u||edges[j][0]==v||edges[j][1]==v)covered[j]=true;
        }
        System.out.println("Conflict edges:");
        for(int[] e:edges)System.out.println("  "+acts[e[0]]+" conflicts with "+acts[e[1]]);
        System.out.println("Selected 2-approximate vertex cover:");
        for(int i:cover)System.out.println("  "+acts[i]);
        System.out.println("Every conflict edge touches a selected activity. The cover size is at most twice optimal.");
    }

    // ------------------------- M6: RANDOMIZED AND PARALLEL ALGORITHMS -------------------------

    private static void randomizedAndParallelDemo() {
        int[] priorities={48,12,75,33,91,26,54,18,62,40};
        System.out.println("Priority scores before randomized QuickSort: "+Arrays.toString(priorities));
        randomizedQuickSort(priorities,0,priorities.length-1,new Random());
        System.out.println("Sorted scores: "+Arrays.toString(priorities));
        int[] stream={18,22,19,31,28,24,36,30,27,41,38,33};
        System.out.println("Reservoir sample (k=4): "+Arrays.toString(reservoirSample(stream,4,new Random())));
        int[] hourly={10,12,8,14,9,11,13,7};
        System.out.println("Hourly production: "+Arrays.toString(hourly));
        System.out.println("Exclusive prefix scan: "+Arrays.toString(exclusiveScan(hourly)));
        long total=Arrays.stream(hourly).parallel().asLongStream().sum();
        System.out.println("Parallel Reduce total: "+total);
        System.out.println("QuickSort average O(n log n), worst O(n^2); reservoir sample O(k) memory.");
        System.out.println("Blelloch scan uses O(n) work; Parallel Reduce aggregates values using parallel streams.");
        int work = 1000, span = 140;
        System.out.println("Brent's theorem: T_P <= W/P + S, where W is work and S is critical-path span.");
        for (int processors : new int[]{1, 2, 4, 8})
            System.out.printf("  P=%d processors: upper-bound estimate %.1f time units%n",
                processors, (double) work / processors + span);
    }
    private static void randomizedQuickSort(int[] a,int lo,int hi,Random r){
        if(lo>=hi)return;int pi=lo+r.nextInt(hi-lo+1),pivot=a[pi];swap(a,pi,hi);int p=lo;
        for(int i=lo;i<hi;i++)if(a[i]<=pivot)swap(a,p++,i);swap(a,p,hi);
        randomizedQuickSort(a,lo,p-1,r);randomizedQuickSort(a,p+1,hi,r);
    }
    private static void swap(int[] a,int i,int j){int t=a[i];a[i]=a[j];a[j]=t;}
    private static int[] reservoirSample(int[] stream,int k,Random r){
        int[] out=Arrays.copyOf(stream,Math.min(k,stream.length));
        for(int i=k;i<stream.length;i++){int j=r.nextInt(i+1);if(j<k)out[j]=stream[i];}
        return out;
    }
    private static int[] exclusiveScan(int[] input){
        int n=1;while(n<input.length)n<<=1;int[] a=new int[n];System.arraycopy(input,0,a,0,input.length);
        for(int d=1;d<n;d<<=1)for(int i=0;i<n;i+=d<<1)a[i+(d<<1)-1]+=a[i+d-1];
        a[n-1]=0;
        for(int d=n>>1;d>=1;d>>=1)for(int i=0;i<n;i+=d<<1){
            int left=i+d-1,right=i+(d<<1)-1,t=a[left];a[left]=a[right];a[right]+=t;
        }
        return Arrays.copyOf(a,input.length);
    }

    private static void millerRabinDemo() {
        long n;
        while(true){try{n=Long.parseLong(readNonEmpty("Enter integer to test (example 1000003): "));break;}
            catch(NumberFormatException e){System.out.println("Enter a valid whole number.");}}
        if(n<2){System.out.println(n+" is not prime.");return;}
        boolean prime=isPrime64(n);
        System.out.println(n+(prime?" is prime for the tested deterministic 64-bit bases.":" is composite."));
        System.out.println("Miller-Rabin is used for large-number primality testing, not as a core factory scheduling algorithm.");
    }
    private static boolean isPrime64(long n){
        for(long p:new long[]{2,3,5,7,11,13,17,19,23,29,31,37})if(n%p==0)return n==p;
        long d=n-1;int s=0;while((d&1)==0){d>>=1;s++;}
        for(long a:new long[]{2,325,9375,28178,450775,9780504,1795265022}){
            if(a%n==0)continue;long x=modPow(a%n,d,n);if(x==1||x==n-1)continue;
            boolean passed=false;for(int r=1;r<s;r++){x=mulMod(x,x,n);if(x==n-1){passed=true;break;}}
            if(!passed)return false;
        }return true;
    }
    private static long modPow(long a,long e,long m){long r=1;while(e>0){if((e&1)!=0)r=mulMod(r,a,m);a=mulMod(a,a,m);e>>=1;}return r;}
    private static long mulMod(long a,long b,long m){
        long r=0;a%=m;while(b>0){if((b&1)!=0)r=r>=m-a?r-(m-a):r+a;a=a>=m-a?a-(m-a):a+a;b>>>=1;}return r;
    }


    // Focused option 8: Damerau-Levenshtein distance (includes adjacent transposition).
    private static void damerauOnly() {
        String a = readNonEmpty("Enter typed entry (e.g. MCH-10I): ").toLowerCase(Locale.ROOT);
        String b = readNonEmpty("Enter intended entry (e.g. MCH-101): ").toLowerCase(Locale.ROOT);
        System.out.println("Damerau-Levenshtein distance: " + damerauDistance(a, b));
        System.out.println("This distance treats a neighboring-character transposition as one edit.");
    }

    // Focused option 14: demonstrate only randomized QuickSort.
    private static void randomizedQuickSortOnly() {
        int[] priorities = {48, 12, 75, 33, 91, 26, 54, 18, 62, 40};
        System.out.println("Production priority scores before sorting: " + Arrays.toString(priorities));
        randomizedQuickSort(priorities, 0, priorities.length - 1, new Random());
        System.out.println("Sorted priority scores: " + Arrays.toString(priorities));
        System.out.println("Randomized QuickSort: expected O(n log n), worst case O(n^2).");
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SC.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SC.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Input cannot be empty.");
        }
    }

    private static void pause() {
        System.out.print("Press Enter to return to the menu...");
        SC.nextLine();
    }

    private static class Dinic {
        private static class Edge {
            int to;
            int reverseIndex;
            long capacity;

            Edge(int to, int reverseIndex, long capacity) {
                this.to = to;
                this.reverseIndex = reverseIndex;
                this.capacity = capacity;
            }
        }

        private final List<Edge>[] graph;
        private final int[] level;
        private final int[] nextEdge;

        @SuppressWarnings("unchecked")
        Dinic(int nodeCount) {
            graph = new List[nodeCount];
            for (int i = 0; i < nodeCount; i++) {
                graph[i] = new ArrayList<>();
            }
            level = new int[nodeCount];
            nextEdge = new int[nodeCount];
        }

        void addEdge(int from, int to, long capacity) {
            Edge forward = new Edge(to, graph[to].size(), capacity);
            Edge reverse = new Edge(from, graph[from].size(), 0);
            graph[from].add(forward);
            graph[to].add(reverse);
        }

        private boolean buildLevels(int source, int sink) {
            Arrays.fill(level, -1);
            Queue<Integer> queue = new ArrayDeque<>();
            queue.add(source);
            level[source] = 0;

            while (!queue.isEmpty()) {
                int current = queue.remove();
                for (Edge edge : graph[current]) {
                    if (edge.capacity > 0 && level[edge.to] == -1) {
                        level[edge.to] = level[current] + 1;
                        queue.add(edge.to);
                    }
                }
            }
            return level[sink] != -1;
        }

        private long sendFlow(int current, int sink, long flowLimit) {
            if (current == sink) {
                return flowLimit;
            }

            for (; nextEdge[current] < graph[current].size(); nextEdge[current]++) {
                Edge edge = graph[current].get(nextEdge[current]);

                if (edge.capacity > 0 && level[edge.to] == level[current] + 1) {
                    long pushed = sendFlow(edge.to, sink, Math.min(flowLimit, edge.capacity));
                    if (pushed > 0) {
                        edge.capacity -= pushed;
                        graph[edge.to].get(edge.reverseIndex).capacity += pushed;
                        return pushed;
                    }
                }
            }
            return 0;
        }

        long maxFlow(int source, int sink) {
            long totalFlow = 0;
            while (buildLevels(source, sink)) {
                Arrays.fill(nextEdge, 0);
                long pushed;
                while ((pushed = sendFlow(source, sink, Long.MAX_VALUE)) > 0) {
                    totalFlow += pushed;
                }
            }
            return totalFlow;
        }
    }
}
