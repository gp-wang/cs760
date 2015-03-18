

import java.util.List;

public class Node {
	int attr;
	String val;
	
	
	boolean leaf;
	public List<Node> children;
	public Node parent;

	public Node() {
		
		this.val = null;
		this.leaf = false;
		this.children = null;
		this.parent = null;
	}
	
	public boolean isLeaf(){
		return leaf;
	}
}
