


import java.util.ArrayList;
import java.util.List;


/**
 * Possible class for internal organization of a decision tree.
 * Included to show standardized output method, print().
 * 
 * You can add extra attributes in this class if you want.
 * 
 */
//for a root node, parentAttribute is null
//for a terminal node, attribute is null
public class DecTreeNode {

	public int label;

	//this records the upper level's decision of attribute for splitting
	public int attrIndex;
	public int attrType;
	//public Double numericAttrValThres;
	public String numericAttrValThres; 
	//public int nominalAttrValIndex;
	// TODO: chk whether need to replce index with val 
	public String nominalAttrVal;
	
	public List<DecTreeNode> children; // indicate leaf or not

	DecTreeNode() {
		
		label = -1;
		
		attrIndex = -1;
		attrType = -1;
		//nominalAttrValIndex = -1;
		numericAttrValThres = null;
		nominalAttrVal = null;
		children = null; // indicate leaf

	}

	/**
	 * Add child to the node.
	 * 
	 * For printing to be consistent, children should be added
	 * in order of the attribute values as specified in the
	 * dataset.
	 */
	public void addChild(DecTreeNode child) {
		if (children == null) {
			children = new ArrayList<DecTreeNode>();
		}
		children.add(child);
	}
	
	public void print(int k, DataSet data) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < k; i++) {
			sb.append("|    ");
		}
		
		String nodeAttrName = data.attrNameMap.get(attrIndex);
		String nodeAttrVal = null;
		if (attrType == 0)
			nodeAttrVal = numericAttrValThres;
		else
			nodeAttrVal = new String(" = " + nominalAttrVal);
		String nodeInfo = new String(nodeAttrName + nodeAttrVal);
		
		sb.append(nodeInfo);
		//sb.append(": "+attribute);
		// if terminal
		if (label != -1) {
			sb.append(": " + data.labelNameMap.get(label));
			System.out.println(sb.toString());
		} else {
			System.out.println(sb.toString());
			for(DecTreeNode child: children) {
				child.print(k+1, data);
			}
		}
	}
	
	

}
