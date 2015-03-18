


import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

/**
 * Holds data for particular instance.
 */
public class Instance {
	
	public int label = -1;

	// attrCnt and attrTypeMap TOTALLY relies on DataSet 
	//gw: use two maps to hold numeric and nominal attributes.
	
	//public static int attrCnt;
	public Map<Integer, Double> numericAttrMap ; 
	public Map<Integer, String> nominalAttrMap ;
	//TODO
	//public Map<Integer, Integer> attrTypeMap; // 0 for num, 1 for nom
	//public Map<Integer, Map<String, Integer>> nominalAttrValPosMap; //  <attrID, attrValuePositions>

	public static final double eta = 1E-8;
	
	//TODO: watch for the finals; likely acts like a capture feature.
	public static Comparator<Instance> getAttrCompar(final int attrIndex, final int attrType, final DataSet data) {
		
		
		if(attrType == 0) {

			return new Comparator<Instance>() {

				public int compare(Instance ins0, Instance ins1) {
					if((ins0.numericAttrMap.get(attrIndex) - ins1.numericAttrMap.get(attrIndex)) < (0 - eta))
						return -1;
					else if((ins0.numericAttrMap.get(attrIndex) - ins1.numericAttrMap.get(attrIndex)) > (eta))
						return 1;
					else 
						return 0;

				}

			};
		}
		else if(attrType == 1) {
			
			
				return new Comparator<Instance>() {

				public int compare(Instance ins0, Instance ins1) {
					//ins0.nominalAttrMap.get(attrIndex)
					//int pos0 = Instance.nominalAttrValPosMap.get(attrIndex).get(ins0.nominalAttrMap.get(attrIndex));
					int pos0 = data.nominalAttrValPosMap.get(attrIndex).get(ins0.nominalAttrMap.get(attrIndex));
					//int pos1 = Instance.nominalAttrValPosMap.get(attrIndex).get(ins1.nominalAttrMap.get(attrIndex));
					int pos1 = data.nominalAttrValPosMap.get(attrIndex).get(ins1.nominalAttrMap.get(attrIndex));
					if((pos0- pos1) < 0)
						return -1;
					else if((pos0- pos1) > 0)
						return 1;
					else 
						return 0;

				}

			};

		}
		else {
			throw new RuntimeException("unknown type");
		}
	}
	
	public Instance() {
		label = -1;

		numericAttrMap = new HashMap<Integer, Double>(); 
		nominalAttrMap = new HashMap<Integer, String>();

		
	}
	
	
	

//	public void setAttribute(int attrIndex, int attrType, Double numVal, String nomVal) {
//		if(attrType == 0) {
//			setNumericAttribute(attrIndex, numVal);
//		}
//		else if(attrType == 1) {
//			setNominalAttribute(attrIndex, nomVal);
//		}
//		else 
//			System.out.println("Error: attrType should be 0 or 1");
//	}
	
	//gw: don't know how to return generics
//	public Attribute<T> getAttribute(int attrIndex, int attrType, Double numVal, String nomVal) {
//		if(attrType == 0) {
//			setNumericAttribute(attrIndex, numVal);
//		}
//		else if(attrType == 1) {
//			setNominalAttribute(attrIndex, nomVal);
//		}
//		else 
//			System.out.println("Error: attrType should be 0 or 1");
//	}	
	
	public void setNumericAttribute(int attrIndex, Double attrVal) {
		numericAttrMap.put(attrIndex, attrVal);
		//attrTypeMap.put(attrIndex, 0);
	}
	public Double getNumericAttribute(int attrIndex) {
		return numericAttrMap.get(attrIndex);
	}
	
	
	public void setNominalAttribute(int attrIndex, String attrVal) {
		nominalAttrMap.put(attrIndex, attrVal);
		//attrTypeMap.put(attrIndex, 1);
	}
	public String getNominalAttribute(int attrIndex) {
		return nominalAttrMap.get(attrIndex);
	}
	
	
	/**
	 * Add label value to the instance
	 */
	public void setLabel(int _label) {
		label = _label;
	}
}
