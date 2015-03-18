

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Digraph {
	private final int nodeCnt;
	private int edgeCnt;
	private Map<Integer, List<DirectedEdge>> emanatingEdges;
	private Map<Integer, List<DirectedEdge>> incidenceEdges;

	public Digraph(int nodeCnt) {
		if (nodeCnt < 0)
			throw new IllegalArgumentException("Digraph: illegal argument");
		this.nodeCnt = nodeCnt;
		this.edgeCnt = 0;
		emanatingEdges = new HashMap<Integer, List<DirectedEdge>>();
		incidenceEdges = new HashMap<Integer, List<DirectedEdge>>();
		for (int v = 0; v < nodeCnt; v++) {
			emanatingEdges.put(v, new ArrayList<DirectedEdge>());
			incidenceEdges.put(v, new ArrayList<DirectedEdge>());
		}
	}

	public int getNodeCnt() {
		return nodeCnt;
	}

	public int getEdgeCnt() {
		return edgeCnt;
	}

	public void addEdge(DirectedEdge e) {
		int u = e.getHead();
		emanatingEdges.get(u).add(e);
		int v = e.getTail();
		incidenceEdges.get(v).add(e);
		edgeCnt++;
	}

	public Iterable<DirectedEdge> getEmanatingEdges(int u) {
		if (u < 0 || u >= nodeCnt)
			throw new IndexOutOfBoundsException(
					"DiGraph: illeagal index argument");
		return emanatingEdges.get(u);
	}

	public Iterable<DirectedEdge> getIncidenceEdges(int v) {
		if (v < 0 || v >= nodeCnt)
			throw new IndexOutOfBoundsException(
					"DiGraph: illeagal index argument");
		return incidenceEdges.get(v);
	}

}
