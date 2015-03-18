

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Prim {

	private Map<Integer, Boolean> visited;
	private PriorityQueue<Edge> pq;
	private double totalMSTWeight;
	private Queue<Edge> mst;
	
	public Prim(Graph G) {
		mst = new LinkedList<Edge>();
		pq = new PriorityQueue<Edge>();
		visited = new HashMap<Integer, Boolean>();
		for (int v = 0; v < G.getNodeCnt(); v++) {
			if ((!visited.containsKey(v)) || (!visited.get(v)))
				primBuildMST(G, v);
		}
	}

	private void primBuildMST(Graph G, int s) {
		markVisited(G, s);
		while (!pq.isEmpty()) {
			Edge e = pq.poll();
			int v = e.getOneNode(), w = e.getOtherNode(v);
			if ((visited.containsKey(v) && visited.get(v))
					&& (visited.containsKey(w) && visited.get(w)))
				continue;
			mst.add(e);
			totalMSTWeight += e.getWeight();
			if ((!visited.containsKey(v)) || (!visited.get(v)))
				markVisited(G, v);
			if ((!visited.containsKey(w)) || (!visited.get(w)))
				markVisited(G, w);
		}
	}

	private void markVisited(Graph G, int u) {
		visited.put(u, new Boolean(true));
		for (Edge e : G.adj(u))
			if ((!visited.containsKey(e.getOtherNode(u)))
					|| (!visited.get(e.getOtherNode(u))))
				pq.add(e);
	}

	public Iterable<Edge> getMST() {
		return mst;
	}

	public double getTotalWeight() {
		return totalMSTWeight;
	}

}