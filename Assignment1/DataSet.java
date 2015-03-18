


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

/**
 * This class organizes the information of a data set into simple structures.
 *
 * Do not modify.
 * 
 */
public class DataSet {

	//attributes
	public int attrCnt = 0;
	public Map<Integer, Integer> attrTypeMap= null; // 0 for numeric, 1 for nom, discard real, integer etc
	public Map<Integer, String> attrNameMap= null; // indexed 
	public Map<Integer, List<String>> nominalAttrValMap= null; //  <attrID, attrValues>
	// numeric attributes value need no map
	
	public Map<Integer, Map<String, Integer>> nominalAttrValPosMap; //  <attrID, attrValuePositions>

	// label
	public Map<Integer, String> labelNameMap= null; // indexed by 0 and 1 

	// instances, use Hashmap for fast random access. In building tree, represent ins by int
	public int insCnt;
	public Map<Integer, Instance> instances = null; // ordered list of instances



	public DataSet() {
		attrCnt = 0;
		attrTypeMap= new HashMap<Integer, Integer> (); 
		attrNameMap= new HashMap<Integer, String> (); 
		nominalAttrValMap= new HashMap<Integer, List<String>> (); 
		nominalAttrValPosMap = new HashMap<Integer, Map<String, Integer>> ();
		labelNameMap= new HashMap<Integer, String> ();
		insCnt = 0;
		instances = new HashMap<Integer, Instance> (); 

	}
	
	// randommly make new dataset by picking up cnt instances using stratify method randomly.
	public DataSet(DataSet data, int cnt) {
		attrCnt = data.attrCnt;
		attrTypeMap= data.attrTypeMap; 
		attrNameMap= data.attrNameMap;
		nominalAttrValMap= data.nominalAttrValMap;
		nominalAttrValPosMap = data.nominalAttrValPosMap;
		labelNameMap= data.labelNameMap;
		insCnt = cnt;
		//instances = new HashMap<Integer, Instance> (); 
		
		int [] labels = new int[2];

		for (Entry<Integer, Instance> ent : data.instances.entrySet()) {
			Instance ins = ent.getValue();
			++ labels[ins.label];
			
		}
		// get label ratio
		double ratio = 1.0*labels[0]/labels[1];
		
		int [] newLabels = new int[2];
		
		newLabels[0]=(int) Math.floor(cnt / (1.0 + 1.0 / ratio));
		newLabels[1]=cnt - newLabels[0];
		
		// pick labels randomly.
		
		// get label = 0 instances's index
		Map<Integer, Integer> labelIndexMap0 = new HashMap<Integer,Integer>();
		Map<Integer, Integer> labelIndexMap1 = new HashMap<Integer,Integer>();
		int cnt0=0, cnt1 = 0;
		//traverse thru instance
		
		for (Entry<Integer, Instance> ent : data.instances.entrySet()) {
			int i = ent.getKey();
			Instance ins = ent.getValue();
			if(ins.label == 0) {
				labelIndexMap0.put(cnt0,i);
				cnt0++;
			}
			else if(ins.label == 1) {
				labelIndexMap1.put(cnt1,i);
				cnt1++;
			}
			else {
				throw new RuntimeException("Unknown label");
			}
			
		}
		Set<Integer> newLabelIndexSet0 = new HashSet<Integer>();
		Set<Integer> newLabelIndexSet1 = new HashSet<Integer>();
		while(newLabelIndexSet0.size()<10) {
			int i0 = randInt(0, cnt0 - 1);
			newLabelIndexSet0.add(i0);
		}
		
		while(newLabelIndexSet1.size()<10) {
			int i0 = randInt(0, cnt1 - 1);
			newLabelIndexSet1.add(i0);
		}

		// build final inst list
		Map<Integer, Instance> finalInstances = new HashMap<Integer, Instance>();
		int j = 0;
		for (Integer integer : newLabelIndexSet0)  {
			finalInstances.put(j, data.instances.get(labelIndexMap0.get(integer)));

			j++;
		}
		for (Integer integer : newLabelIndexSet1) {

			finalInstances.put(j, data.instances.get(labelIndexMap1.get(integer)));
			j++;
		}
		
		instances = finalInstances;
		
		// for debug, print out selected instances
		for (Entry<Integer, Instance> ent : finalInstances.entrySet()) {
			Instance ins = ent.getValue();
			
			System.out.println();
			for(int k = 0; k < attrCnt; ++k) {
				if(attrTypeMap.get(k) == 0)
					System.out.print(ins.getNumericAttribute(k) + ",");
				else
					System.out.print(ins.getNominalAttribute(k) + ",");
			}
			System.out.print(ins.label);
				
		}
		System.out.println();
		
		
		
	}
	
	public static int randInt(int min, int max) {

		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	public void addAttribute(String line) {

		//get name
		int first = line.indexOf("'");
		int second = line.indexOf("'", first + 1);
		String attrName =new String(line.substring(first + 1, second)); // not including '

		line = line.substring(second + 1).trim();

		// test for label
		if(attrName.toLowerCase().equals("class")) {
			addLabel(line);
			return;
		}

		// confirmed an attribute
		

		// test for numerical or nominal
		if(!line.substring(0,1).equals("{")) {
			// numeric
			attrCnt ++;
			attrTypeMap.put(attrCnt - 1, 0); // 0 for num
			attrNameMap.put(attrCnt - 1, attrName);
		}
		else {
			// nominal
			
			// preparation
			line = line.replace("{","").trim();
			line = line.replace("}","").trim();
			String[] splitline = line.split(", ");

			// nominal list
			List<String> attrValList = new ArrayList<String>();
			Map<String, Integer> valPosMap = new HashMap<String, Integer>(); 
			for (int i = 0; i < splitline.length; i++) {
				String attrVal = new String(splitline[i].trim()); 
				attrValList.add(attrVal);
				valPosMap.put(attrVal, i);
			}
			
			// update dataset
			attrCnt ++;
			attrTypeMap.put(attrCnt - 1, 1); // 0 for nominal
			attrNameMap.put(attrCnt - 1, attrName);
			nominalAttrValMap.put(attrCnt - 1, attrValList);
			
			
			nominalAttrValPosMap.put(attrCnt - 1, valPosMap);
		}
		
		
		

	}

	public void addLabel(String line) {

		line = line.replace("{","").trim();
		line = line.replace("}","").trim();

		String[] splitline = line.split(", ");

		labelNameMap.put(0, new String(splitline[0]));
		labelNameMap.put(1, new String(splitline[1]));

	}	
	
	/**
	 * Add instance to collection.
	 */
	public void addInstance(String line) {

		Instance instance = new Instance();

		String[] splitline = line.split(",");
		for(int i = 0; i < splitline.length - 1; i ++) {
			if(attrTypeMap.get(i) == 0) {
				instance.setNumericAttribute(i, Double.parseDouble(splitline[i]));
			}
			else if(attrTypeMap.get(i) == 1) {
				instance.setNominalAttribute(i, new String(splitline[i]));
			}
			else
				System.out.println("error: unknown attribute");
			
		}
		
		// set label
		if(splitline[splitline.length - 1].equals(labelNameMap.get(0))) {
			instance.setLabel(0);
		}
		else if(splitline[splitline.length - 1].equals(labelNameMap.get(1))) {
			instance.setLabel(1);
		}
		else
			System.out.println("error: Unknown label");

		
		
		instances.put(insCnt,instance);
		++ insCnt;
	}




}
