

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
	private final int nodeCnt;
	private int edgeCnt;
	private Map<Integer, List<Edge>> adj;

	public Graph(int nodeCnt) {
		if (nodeCnt < 0)
			throw new IllegalArgumentException("Graph: Illegal Argument");
		this.nodeCnt = nodeCnt;
		this.edgeCnt = 0;
		adj = new HashMap<Integer, List<Edge>>();
		for (int v = 0; v < nodeCnt; v++) {
			adj.put(v, new ArrayList<Edge>());
		}
	}

	public int getNodeCnt() {
		return nodeCnt;
	}

	public int getEdgeCnt() {
		return edgeCnt;
	}

	public void addEdge(Edge e) {
		int v = e.getOneNode();
		int w = e.getOtherNode(v);
		if (v < 0 || v >= nodeCnt || w < 0 || w >= nodeCnt)
			throw new IndexOutOfBoundsException("Graph: Vertex out of range");
		adj.get(v).add(e);
		adj.get(w).add(e);
		edgeCnt++;
	}

	public Iterable<Edge> adj(int v) {
		if (v < 0 || v >= nodeCnt)
			throw new IndexOutOfBoundsException("Graph: Vertex out of range");
		return adj.get(v);
	}

}
