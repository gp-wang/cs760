///////////////////////////////////////////////////////////////////////////////
// Title:            CS760 HW 2
// 
// Semester:         CS760 Spring 2015
//
// Author:           Gaopeng Wang
// Email:            gwang63@wisc.edu
// CS Login:         gaopeng

//////////////////////////// 80 columns wide //////////////////////////////////


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class BayesLearner {

	public final static int PSEUDO_COUNT = 1;

	public final static double POS_ETA = +1E-8;
	public final static double NEG_ETA = -1E-8;
	public final static double neginf = -1E8;
	public static String alg; // = 10;
	public static int mode = 1; // 1 for all instances, 2 for 25, 3 50, 4 100
	public Map<Integer, Map<Integer, Double>> I; // memoization

	public Map<Integer, List<Double>> NBp0;
	public Map<Integer, List<Double>> NBp1;
	public double NBpy0;
	public double NBpy1;

	// learnt TAN knowledge: graph structure and probability
	public Map<Integer, List<Integer>> graphParentNodes;
	private Map<String, Double> TANprob;

	/**
	 * Runs the tests for HW4.
	 */
	public static void main(String[] args) {

		if (args.length != 3) {
			System.out
					.println("usage: bayes <trainFilename> <testFilename> <n|t>");
			System.exit(-1);
		}

		// Turn text into array
		DataSet trainSet = createDataSet(args[0], 1);
		DataSet testSet = createDataSet(args[1], 1);
		alg = new String(args[2]);

		BayesLearner lnr = new BayesLearner();

		if (mode == 1) {
			// use all training set

			if (alg.equals("n")) {

				lnr.learnNB(trainSet, true);

				System.out.println();
				lnr.predictNB(testSet, trainSet, lnr.graphParentNodes);

			} else if (alg.equals("t")) {

				lnr.learnTAN(trainSet);
				// Separate output
				System.out.println();

				lnr.predictTAN(testSet, trainSet, lnr.graphParentNodes);

			}

		} else if (mode == 2) {
			// use 25 of training set
			int trainSize = 25;
			double temp = 0.0;
			int rounds = 4;

			temp = learnStratified(trainSet, testSet, lnr, trainSize, temp,
					rounds);

			double avgAcc = temp / rounds;
			System.out.println("Accuracy: " + avgAcc);
			// get avg accuracy

		} else if (mode == 3) {
			int trainSize = 50;
			double temp = 0.0;
			int rounds = 4;

			temp = learnStratified(trainSet, testSet, lnr, trainSize, temp,
					rounds);

			double avgAcc = temp / rounds;
			System.out.println("Accuracy: " + avgAcc);
			// get avg accuracy
		} else if (mode == 4) {
			int trainSize = 100;
			double temp = 0.0;
			int rounds = 4;

			temp = learnStratified(trainSet, testSet, lnr, trainSize, temp,
					rounds);

			double avgAcc = temp / rounds;
			System.out.println("Accuracy: " + avgAcc);
			// get avg accuracy
		}
	}

	private static double learnStratified(DataSet trainSet, DataSet testSet,
			BayesLearner lnr, int trainSize, double temp, int rounds) {
		for (int i = 0; i < rounds; ++i) {
			// pick random ins set
			DataSet trainSet_new = new DataSet(trainSet, trainSize);

			int correctCnt = 0;
			// learn, test, record accuracy
			if (alg.equals("n")) {
				lnr.learnNB(trainSet_new, true);

				correctCnt = lnr.predictNB(testSet, trainSet_new,
						lnr.graphParentNodes);
			} else if (alg.equals("t")) {
				lnr.learnTAN(trainSet_new);
				correctCnt = lnr.predictTAN(testSet, trainSet_new,
						lnr.graphParentNodes);
			}



			temp += (1.0 * correctCnt / testSet.instances.size());
			trainSet_new = null;
		}
		return temp;
	}

	/**
	 * Converts from text file format to DataSet format.
	 * 
	 */
	private static DataSet createDataSet(String file, int modeFlag) {

		DataSet set = new DataSet();
		BufferedReader in;

		boolean isDataRegion = false;
		try {
			in = new BufferedReader(new FileReader(file));

			int count = 0; // gw: for counting attr
			String line;
			while (in.ready()) {
				line = in.readLine();

				if (line.length() < 1 || line.substring(0, 1).equals("%"))
					continue;
				else if (line.length() >= 10
						&& line.substring(0, 10).toLowerCase()
								.equals("@attribute")) {

					line = line.substring(11).trim();
					set.addAttribute(line);
					count++;

				} else if (line.length() >= 5
						&& line.substring(0, 5).toLowerCase().equals("@data")) {
					isDataRegion = true;
					continue;
				} else if (isDataRegion)
					set.addInstance(line);
				else {
					continue;
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		return set;
	}

	public int predictNB(DataSet testSet, DataSet trainSet,
			Map<Integer, List<Integer>> tanParentMap) {

		return predictTAN(testSet, trainSet, tanParentMap);
	}

	public int predictTAN(DataSet testSet, DataSet trainSet,
			Map<Integer, List<Integer>> tanParentMap) {

		int success_count = 0;
		for (Entry<Integer, Instance> ent : testSet.instances.entrySet()) {
			Instance testExample = ent.getValue();

			// (testSet, trainSet, tan_graph, label0)
			double labelProb_0 = getProb_label(testSet, trainSet, tanParentMap,
					0, testExample);
			double labelProb_1 = getProb_label(testSet, trainSet, tanParentMap,
					1, testExample);

			double label0_score = 1.0 * (labelProb_0)
					/ (labelProb_0 + labelProb_1);
			double label1_score = 1.0 * (labelProb_1)
					/ (labelProb_0 + labelProb_1);



			double largerProb = 0.0;
			if (label0_score >= label1_score) {
				largerProb = label0_score;
			} else {
				largerProb = label1_score;
			}

			int trueLabel = testExample.label;

			int predictedLabel = -1;
			if (label0_score >= label1_score) {
				predictedLabel = 0;
			} else {
				predictedLabel = 1;
			}

			if (trueLabel == predictedLabel) {
				success_count++;
			}

			System.out.println(testSet.labelNameMap.get(predictedLabel) + " "
					+ testSet.labelNameMap.get(trueLabel) + " " + largerProb);

		}
		System.out.println();
		System.out.println(success_count);

		return success_count;
	}

	private double getProb_label(DataSet testSet, DataSet trainSet,
			Map<Integer, List<Integer>> tanParentMap, int label,
			Instance testExample) {

		int cntTotalExamples = trainSet.instances.size();

		int cntExamples_label = trainSet.getCount_label(label);

		double label_cond_prob_score = 1.0 * (cntExamples_label + 1)
				/ (cntTotalExamples + 2);

		double prob = 1.0;
		prob *= label_cond_prob_score;

		for (int f = 0; f < testSet.attrCnt; ++f) {
			double curr_f_prob = getProb_feature_on_label(f, testExample,
					label, tanParentMap, testSet, trainSet);

			prob *= curr_f_prob;

		}

		return prob;

	}

	private double getProb_feature_on_label(int feature, Instance testExample,
			int label, Map<Integer, List<Integer>> tanParentMap,
			DataSet testSet, DataSet trainSet) {

		String feature_value = testExample.getFeatureValue(feature, testSet);
		List<Integer> feature_parents = tanParentMap.get(feature);

		Map<Integer, String> parent_feature_val_map = new HashMap<Integer, String>();
		Map<Integer, String> feature_val_map = null;

		if (!feature_parents.isEmpty()) {
			for (Integer f : feature_parents) {
				// reject the class node as parent, because the class label is
				// already accounted for in the form of functino parameter
				if (f == trainSet.attrCnt)
					continue;
				parent_feature_val_map.put(f,
						testExample.getFeatureValue(f, testSet));
			}

			feature_val_map = new HashMap<Integer, String>(
					parent_feature_val_map);
			// feature_value is at beginning
			feature_val_map.put(feature, feature_value);

		}

		double cnt_parentFeatures_label = trainSet.getCount_features_label(
				label, parent_feature_val_map);
		double cnt_features_label = trainSet.getCount_features_label(label,
				feature_val_map);

		int num_feature_values = trainSet.nominalAttrValMap.get(feature).size();

		double prob = 1.0 * (cnt_features_label + 1)
				/ (cnt_parentFeatures_label + num_feature_values);

		return (prob);

	}

	//
	public void learnNB(DataSet data, boolean printOut) {

		//
		// knowledge repository
		// p0 for label0, p1 for label1
		Map<Integer, List<Double>> p0 = new HashMap<Integer, List<Double>>();
		Map<Integer, List<Double>> p1 = new HashMap<Integer, List<Double>>();
		double py0 = 0.0, py1 = 0.0;

		int sum0 = 0, sum1 = 0;
		for (int i = 0; i < data.attrCnt; ++i) {

			List<Integer> sum0_j = new ArrayList<Integer>(), sum1_j = new ArrayList<Integer>();

			sum0 = 0;
			sum1 = 0;

			for (int j = 0; j < data.nominalAttrValMap.get(i).size(); ++j) {
				// DONE: verify that this does not include the class label

				int cnt0_j = 0, cnt1_j = 0;
				for (int k = 0; k < data.instances.size(); ++k) {
					Instance ins = data.instances.get(k);

					if (ins.label == 0
							&& ins.getNominalAttribute(i).equals(
									data.nominalAttrValMap.get(i).get(j))) {
						++cnt0_j;
						++sum0;
					} else if (ins.label == 1
							&& ins.getNominalAttribute(i).equals(
									data.nominalAttrValMap.get(i).get(j))) {
						++cnt1_j;
						++sum1;
					} else
						;

				}
				sum0_j.add(cnt0_j);
				sum1_j.add(cnt1_j);

			}
			List<Double> p0_i = new ArrayList<Double>(), p1_i = new ArrayList<Double>();


			int num_feature_value = data.nominalAttrValMap.get(i).size();

			// calculate probability, using +1 smoothing
			for (int j = 0; j < data.nominalAttrValMap.get(i).size(); ++j) {

				p0_i.add(1.0 * (sum0_j.get(j) + 1) / (sum0 + num_feature_value));
				p1_i.add(1.0 * (sum1_j.get(j) + 1) / (sum1 + num_feature_value));
			}
			p0.put(i, p0_i);
			p1.put(i, p1_i);
		}

		int all_labels = 2;
		py0 = 1.0 * (sum0 + 1) / (sum0 + all_labels);
		py1 = 1.0 * (sum1 + 1) / (sum1 + all_labels);

		// pass learned knowledge out
		NBp0 = p0;
		NBp1 = p1;
		NBpy0 = py0;
		NBpy1 = py1;

		// early return if no need to print out
		if (!printOut) {
			return;
		}

		// construct tree structure and print out

		int v_class = data.attrCnt;
		// add edge from Y to feature nodes
		int featureNodeCnt = data.attrCnt;

		Digraph dg = new Digraph(featureNodeCnt + 1);

		for (int i = 0; i < featureNodeCnt; ++i) {
			// DONE: make di-edge e
			DirectedEdge e = new DirectedEdge(v_class, i, 0.0);
			dg.addEdge(e);
		}

		// fill in the conditional probability of each node based on the
		// parent info in the constructed graph
		// parent adjacency list
		Map<Integer, List<Integer>> parentNodes = new HashMap<Integer, List<Integer>>();
		for (int i = 0; i < featureNodeCnt; ++i) {
			int v = i;
			if (!parentNodes.containsKey(v)) {
				parentNodes.put(v, new ArrayList<Integer>());
			}

			for (DirectedEdge e : dg.getIncidenceEdges(v)) {
				parentNodes.get(v).add(e.getHead());
			}
			// DONE; sort the arraylist: parentNodes.get(v)
			// to ensure order for later key look-up. where the key string is
			// built in order
			Collections.sort(parentNodes.get(v));
		}

		this.graphParentNodes = parentNodes;

		// print out Tree structure, including the class node
		for (Entry<Integer, List<Integer>> ent : graphParentNodes.entrySet()) {
			int attrIndex = ent.getKey();
			List<Integer> parentList = ent.getValue();

			String attrName = data.attrNameMap.get(attrIndex);
			System.out.print(attrName + " ");
			for (Integer pi : parentList) {
				String parentAttrName = null;
				if (pi != data.attrCnt) {
					parentAttrName = data.attrNameMap.get(pi);
				} else {
					parentAttrName = new String("class");
				}
				System.out.print(parentAttrName + " ");
			}
			System.out.print("\n");
		}

		return;

	}

	public void learnTAN(DataSet data) {

		// use learnNB to populate postier probabilities
		// pass in false to avoid print out tree structure
		learnNB(data, false);

		// TAN
		int nodeCnt = data.attrCnt + 1; // include class node
		Graph G = new Graph(nodeCnt);

		int featureNodeCnt = data.attrCnt;
		for (int i = 0; i < featureNodeCnt; ++i) {
			for (int j = 0; j < i; ++j) {
				double weight = I_Xi_Xj_on_Y(i, j, data);
				Edge e = new Edge(i, j, weight);
				G.addEdge(e);
			}
		}



		// prim
		Prim mst = new Prim(G);
		// debug:
		// for (Edge e : mst.edges()) {
		// StdOut.println(e);
		// }
		// StdOut.printf("%.5f\n", mst.weight());

		Graph treeGraph = new Graph(G.getNodeCnt());
		for (Edge e : mst.getMST()) {
			int u = e.getOneNode(), v = e.getOtherNode(u);
			Edge e1 = new Edge(u, v, 1.0); // dummy weight 1.0
			treeGraph.addEdge(e1);

		}

		// make digraph
		Digraph dg = makeDigraph(treeGraph);

		int v_class = data.attrCnt;
		// add edge from Y to feature nodes
		for (int i = 0; i < featureNodeCnt; ++i) {
			// DONE: make di-edge e
			DirectedEdge e = new DirectedEdge(v_class, i, 0.0);
			dg.addEdge(e);
		}

		//  fill in the conditional probability of each node based on the
		// parent info in the constructed graph
		// parent adjacency list
		Map<Integer, List<Integer>> parentNodes = new HashMap<Integer, List<Integer>>();
		for (int i = 0; i < featureNodeCnt; ++i) {
			int v = i;
			if (!parentNodes.containsKey(v)) {
				parentNodes.put(v, new ArrayList<Integer>());
			}

			for (DirectedEdge e : dg.getIncidenceEdges(v)) {
				parentNodes.get(v).add(e.getHead());
			}
			// DONE; sort the arraylist: parentNodes.get(v)
			// to ensure order for later key look-up. where the key string is
			// built in order
			Collections.sort(parentNodes.get(v));
		}

		this.graphParentNodes = parentNodes;

		// print out Tree structure, including the class node
		for (Entry<Integer, List<Integer>> ent : graphParentNodes.entrySet()) {
			int attrIndex = ent.getKey();
			List<Integer> parentList = ent.getValue();

			String attrName = data.attrNameMap.get(attrIndex);
			System.out.print(attrName + " ");
			for (Integer pi : parentList) {
				String parentAttrName = null;
				if (pi != data.attrCnt) {
					parentAttrName = data.attrNameMap.get(pi);
				} else {
					parentAttrName = new String("class");
				}
				System.out.print(parentAttrName + " ");
			}
			System.out.print("\n");
		}

		// using bayes graph, populate keys
		// String key: e.g. "v=value1|p0=value1,p1=value0,y=1"
		// List<String> keys: enumerate all possible keys's
		List<String> keys = new ArrayList<String>();
		for (int i = 0; i < featureNodeCnt; ++i) {
			// for each feature Attr: x_i
			List<Integer> parents = parentNodes.get(i);
			for (int j = 0; j < data.nominalAttrValMap.get(i).size(); ++j) {
				// for each value of feature Attr: x_i=v_j|
				String val = data.nominalAttrValMap.get(i).get(j);

				List<String> tempKeys = getKeys(i, val, parents, data);
				for (String s : tempKeys) {
					keys.add(s);
				}
			}
		}

		Map<String, Double> p = new HashMap<String, Double>();
		for (String key : keys) {

			int key_v_attr = getVAttr(key);
			String key_v_val = getVVal(key);
			String key_cond = getCond(key);
			int sum_cond = 0, cnt_cond = 0;

			for (Entry<Integer, Instance> ent : data.instances.entrySet()) {
				Instance ins = ent.getValue();

				if (checkCondMatch(key_cond, ins, data)) {
					++sum_cond;
					if (checkValMatch(key_v_attr, key_v_val, ins, data)) {
						++cnt_cond;
					}
				}

			}

			int num_feature_value = data.nominalAttrValMap.get(key_v_attr)
					.size();
			double prob = 1.0 * (1 + cnt_cond) / (num_feature_value + sum_cond);

			p.put(key, prob);
		}

		this.TANprob = p;

		return;

	}

	private boolean checkValMatch(int key_v_attr, String key_v_val,
			Instance ins, DataSet data) {

		if (!key_v_val.equals(ins.nominalAttrMap.get(key_v_attr))) {
			return false;
		}

		return true;
	}

	private boolean checkCondMatch(String key_cond, Instance ins, DataSet data) {


		// key_cond: p0=value1,p1=value0,y=1
		Map<Integer, String> map = new HashMap<Integer, String>();

		String[] str1 = key_cond.split(",");
		for (int i = 1; i < str1.length; ++i) {
			int attr = Integer.parseInt(str1[i].split("=")[0]);
			String val = new String(str1[i].split("=")[1]);

			map.put(attr, val);
		}

		// create a mockup instance that treat label as one nominal value
		Instance dummy = new Instance(ins);
		dummy.nominalAttrMap.put(data.attrCnt,
				(new Integer(dummy.label).toString()));

		for (Entry<Integer, String> ent : map.entrySet()) {

			if (!ent.getValue().equals(dummy.nominalAttrMap.get(ent.getKey()))) {
				return false;
			}
		}

		return true;
	}

	// "v=value1|p0=value1,p1=value0,y=1"
	public int getVAttr(String key) {
		return Integer.parseInt(key.split("=")[0]);
	}

	public String getVVal(String key) {
		return new String(key.split("\\|")[0].split("=")[1]);
	}

	public String getCond(String key) {
		return key.split("\\|")[1];
	}

	// given a node, and its parents, and dataset, produce a list of
	// strings
	// that includes all combinations of the value of the node and the parents's
	// node
	// Note: parents should include class label before passed in.
	public List<String> getKeys(int attr_index, String attr_val,
			List<Integer> parents, DataSet data) {

		Node root = new Node();
		makeTree(root, parents, data);

		List<String> keys = new ArrayList<String>();
		getList(root, new String(attr_index + "=" + attr_val + "|"), keys);

		return keys;
	}

	public void getList(Node nd, String str, List<String> l) {
		if (nd.isLeaf()) {
			l.add(str);
			return;
		}

		for (Node nd1 : nd.children) {

			getList(nd1, new String(str + "," + nd1.attr + "=" + nd1.val), l);
		}
	}

	public void makeTree(Node nd, List<Integer> l, DataSet data) {
		if (l.isEmpty()) {
			// mark leaf
			nd.leaf = true;
			return;
		}

		nd.children = new ArrayList<Node>();

		List<Integer> l1 = new ArrayList<Integer>(l);
		int pv = l1.remove(0);

		if (pv == data.attrCnt) {
			List<String> labels = new ArrayList<String>();
			labels.add((new Integer(0)).toString());
			labels.add((new Integer(1)).toString());

			// pv is class node
			for (String val : labels) {
				Node nd1 = new Node();
				nd1.attr = pv;
				nd1.val = val;
				nd1.parent = nd;
				nd.children.add(nd1);

				makeTree(nd1, l1, data);
			}

		} else {
			for (String val : data.nominalAttrValMap.get(pv)) {
				Node nd1 = new Node();
				nd1.attr = pv;
				nd1.val = val;
				nd1.parent = nd;
				nd.children.add(nd1);

				makeTree(nd1, l1, data);
			}
		}

	}

	public Digraph makeDigraph(Graph g) {

		// assume the last node is isolated, and other node form a tree

		// always pick node 0 as root
		int featureNodeCnt = g.getNodeCnt() - 1;

		int v_new = 0;
		Set<Integer> visited = new HashSet<Integer>();
		Set<Integer> frontier = new HashSet<Integer>();
		Set<Integer> unvisited = new HashSet<Integer>();

		// init
		visited.add(v_new);
		frontier.add(v_new);
		for (int i = 0; i < featureNodeCnt; ++i) {
			if (i != v_new) {
				unvisited.add(i);
			}
		}

		Digraph dg = new Digraph(g.getNodeCnt());

		while (visited.size() != featureNodeCnt) {

			// for every node in frontier, check adjacent nodes,
			// if not already visited, add edge from that frontier node to its
			// adjacent node, and put that adjacent node into frontier
			// after finishing all adj nodes, pop the old frontier node out,
			List<Integer> frontierDelList = new ArrayList<Integer>();
			List<Integer> frontierAddList = new ArrayList<Integer>();
			for (Integer i : frontier) {
				frontierDelList.add(i);
				for (Edge e : g.adj(i)) {
					if (!visited.contains(e.getOtherNode(i))) {
						DirectedEdge de = new DirectedEdge(i,
								e.getOtherNode(i), e.getWeight());
						dg.addEdge(de);

						visited.add(e.getOtherNode(i)); 

						frontierAddList.add(e.getOtherNode(i));
					}
				}
			}
			for (Integer i : frontierAddList) {
				frontier.add(i);
			}

			for (Integer i : frontierDelList) {
				frontier.remove(i);
			}
		}

		return dg;
	}

	public double I_Xi_Xj_on_Y(int xi, int xj, DataSet data) {

		// DONE: memoization using a nested map
		if (I == null) {
			I = new HashMap<Integer, Map<Integer, Double>>();
		}

		if (!I.containsKey(xi)) {
			I.put(xi, new HashMap<Integer, Double>());
		}

		if (I.get(xi).containsKey(xj)) {
			return I.get(xi).get(xj);
		} else {

			double sum = 0.0;
			for (int i = 0; i < data.nominalAttrValMap.get(xi).size(); ++i) {
				for (int j = 0; j < data.nominalAttrValMap.get(xj).size(); ++j) {
					// x_i, and x_j
					sum += (P_ij_y(xi, i, xj, j, 0, data)
							* log2(P_ij_y0(xi, i, xj, j, data)
									/ (P_i_y0(xi, i, data) * P_j_y0(xj, j, data))) + P_ij_y(
							xi, i, xj, j, 1, data)
							* log2(P_ij_y1(xi, i, xj, j, data)
									/ (P_i_y1(xi, i, data) * P_j_y1(xj, j, data))));

				}
			}
			I.get(xi).put(xj, sum);
			return sum;
		}
	}

	// xi is attrIndex, i is valIndex of that attribute
	public double P_ij_y(int xi, int i, int xj, int j, int y, DataSet data) {

		String str_i = data.nominalAttrValMap.get(xi).get(i), str_j = data.nominalAttrValMap
				.get(xj).get(j);
		int cnt = 0, cnt_ijy = 0;

		for (Entry<Integer, Instance> ent : data.instances.entrySet()) {
			Instance ins = ent.getValue();

			++cnt;

			if (ins.getNominalAttribute(xi).equals(str_i)
					&& ins.getNominalAttribute(xj).equals(str_j)
					&& ins.label == y) {
				++cnt_ijy;
			}

		}

		int all_xi = data.nominalAttrValMap.get(xi).size(), all_xj = data.nominalAttrValMap
				.get(xj).size(), all_y = 2;

		double prob_ijy = 1.0 * (cnt_ijy + PSEUDO_COUNT)
				/ (cnt + PSEUDO_COUNT * all_xi * all_xj * all_y);
		return prob_ijy;
	}

	public double P_ij_y0(int xi, int i, int xj, int j, DataSet data) {

		String str_i = data.nominalAttrValMap.get(xi).get(i), str_j = data.nominalAttrValMap
				.get(xj).get(j);
		int cnt_y0 = 0, cnt_ij = 0;

		for (Entry<Integer, Instance> ent : data.instances.entrySet()) {
			Instance ins = ent.getValue();
			if (ins.label == 0) {
				++cnt_y0;

				if (ins.getNominalAttribute(xi).equals(str_i)
						&& ins.getNominalAttribute(xj).equals(str_j)) {
					++cnt_ij;
				}
			}
		}

		int all_xi = data.nominalAttrValMap.get(xi).size(), all_xj = data.nominalAttrValMap
				.get(xj).size();

		double prob_ij_y0 = 1.0 * (cnt_ij + PSEUDO_COUNT)
				/ (cnt_y0 + PSEUDO_COUNT * all_xi * all_xj);
		return prob_ij_y0;
	}

	public double P_ij_y1(int xi, int i, int xj, int j, DataSet data) {

		String str_i = data.nominalAttrValMap.get(xi).get(i), str_j = data.nominalAttrValMap
				.get(xj).get(j);
		int cnt_y1 = 0, cnt_ij = 0;

		for (Entry<Integer, Instance> ent : data.instances.entrySet()) {
			Instance ins = ent.getValue();
			if (ins.label == 1) {
				++cnt_y1;

				if (ins.getNominalAttribute(xi).equals(str_i)
						&& ins.getNominalAttribute(xj).equals(str_j)) {
					++cnt_ij;
				}
			}
		}

		int all_xi = data.nominalAttrValMap.get(xi).size(), all_xj = data.nominalAttrValMap
				.get(xj).size();

		double prob_ij_y1 = 1.0 * (cnt_ij + PSEUDO_COUNT)
				/ (cnt_y1 + PSEUDO_COUNT * all_xi * all_xj);

		return prob_ij_y1;

	}

	public double P_i_y0(int xi, int i, DataSet data) {

		String str_i = data.nominalAttrValMap.get(xi).get(i);
		int cnt_y0 = 0, cnt_i = 0;

		for (Entry<Integer, Instance> ent : data.instances.entrySet()) {
			Instance ins = ent.getValue();
			if (ins.label == 0) {
				++cnt_y0;

				if (ins.getNominalAttribute(xi).equals(str_i)) {
					++cnt_i;
				}
			}
		}

		int all_xi = data.nominalAttrValMap.get(xi).size();

		double prob_i_y0 = 1.0 * (cnt_i + PSEUDO_COUNT)
				/ (cnt_y0 + PSEUDO_COUNT * all_xi);
		return prob_i_y0;

	}

	public double P_i_y1(int xi, int i, DataSet data) {
		String str_i = data.nominalAttrValMap.get(xi).get(i);
		int cnt_y1 = 0, cnt_i = 0;

		for (Entry<Integer, Instance> ent : data.instances.entrySet()) {
			Instance ins = ent.getValue();
			if (ins.label == 1) {
				++cnt_y1;

				if (ins.getNominalAttribute(xi).equals(str_i)) {
					++cnt_i;
				}
			}
		}

		int all_xi = data.nominalAttrValMap.get(xi).size();

		double prob_i_y0 = 1.0 * (cnt_i + PSEUDO_COUNT)
				/ (cnt_y1 + PSEUDO_COUNT * all_xi);
		return prob_i_y0;

	}

	public double P_j_y0(int xj, int j, DataSet data) {
		return P_i_y0(xj, j, data);
	}

	public double P_j_y1(int xj, int j, DataSet data) {
		return P_i_y1(xj, j, data);
	}

	// helper functions
	public double log2(double a) {
		return Math.log10(a) / Math.log10(2.0);
	}

}
