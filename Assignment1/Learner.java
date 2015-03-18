



import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

//public class ParserTester {
public class Learner {

	public final static double eta = 1E-3;
	public final static double neginf = -1E8;
	public static int m ; //= 10;
	public static int mode = 1; // 1 for all instances, 2 for 25, 3 50, 4 100
	
	
	public DecTreeNode tree;

	public Learner() {
		this.tree = null;
	}

	/**
	 * Runs the tests for HW4.
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("usage: dt-learn <trainFilename> <testFilename> m" );
			System.exit(-1);
		}

		// Turn text into array
		DataSet trainSet = createDataSet(args[0], 1);
		DataSet testSet = createDataSet(args[1], 1); 
		m = Integer.parseInt(args[2]);
		
		

		Learner lnr = new Learner();
		DecTreeNode tree = null;
		 
		if(mode == 1) {
			tree = lnr.Learn(trainSet);

			for (DecTreeNode nd : tree.children) {
				nd.print(0, trainSet);
			}
			//tree.print(0, trainSet);
			System.out.println();

			double accuracy = lnr.classify(tree, testSet);
			System.out.println("Accuracy: " + accuracy);
		}
		else if(mode == 2) {
			int trainSize = 25;
			double temp = 0.0;
			int rounds = 10;
			
			for(int i = 0; i < rounds; ++i) {
				// pick random ins set
				DataSet trainSet_25 = new DataSet(trainSet, trainSize);
				// learn, test, record accuracy
				tree = lnr.Learn(trainSet_25);

//				for (DecTreeNode nd : tree.children) {
//					nd.print(0, trainSet);
//				}
				//tree.print(0, trainSet);
//				System.out.println();

				double accuracy = lnr.classify(tree, testSet);
				temp += accuracy;
				trainSet_25 = null;
			}
			
			double avgAcc = temp / 10;
			System.out.println("Accuracy: " + avgAcc);
			// get avg accuracy

		} 
		else if(mode == 3) {
						int trainSize = 50;
			double temp = 0.0;
			int rounds = 10;
			
			for(int i = 0; i < rounds; ++i) {
				// pick random ins set
				DataSet trainSet_50 = new DataSet(trainSet, trainSize);
				// learn, test, record accuracy
				tree = lnr.Learn(trainSet_50);

				for (DecTreeNode nd : tree.children) {
					nd.print(0, trainSet);
				}
				tree.print(0, trainSet);
				System.out.println();

				double accuracy = lnr.classify(tree, testSet);
				temp += accuracy;
			}
			
			double avgAcc = temp / 10;
			System.out.println("Accuracy: " + avgAcc);
			// get avg accuracy
		}
		else if(mode == 4) {
						int trainSize = 100;
			double temp = 0.0;
			int rounds = 10;
			
			for(int i = 0; i < rounds; ++i) {
				// pick random ins set
				DataSet trainSet_100 = new DataSet(trainSet, trainSize);
				// learn, test, record accuracy
				tree = lnr.Learn(trainSet_100);

				for (DecTreeNode nd : tree.children) {
					nd.print(0, trainSet);
				}
				tree.print(0, trainSet);
				System.out.println();

				double accuracy = lnr.classify(tree, testSet);
				temp += accuracy;
				trainSet_100 = null;
			}
			
			double avgAcc = temp / 10;
			System.out.println("Accuracy: " + avgAcc);
			// get avg accuracy
		}
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

			int count = 0; //gw: for counting attr
			String line;
			while (in.ready()) {
				line = in.readLine(); 

				if(line.length() < 1 || line.substring(0,1).equals( "%" ))
					continue;
				else if (line.length() >= 10 && line.substring(0, 10).toLowerCase().equals("@attribute")) {

					line = line.substring(11).trim(); 
					set.addAttribute(line);
					count++;

				}
				else if (line.length() >= 5 && line.substring(0, 5).toLowerCase().equals("@data")) {
					isDataRegion = true;
					continue;
				}
				else if(isDataRegion)
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

	public Double classify(DecTreeNode dt, DataSet data){
		
		int total = 0, correct = 0;
		
		for (Entry<Integer, Instance> ent : data.instances.entrySet()) {
			Instance ins = ent.getValue();
			total ++;
			
			if(predict(dt, ins) == ins.label) {
				correct ++;
			}
			
		}

		return 1.0 * correct / total;
	}
	
	public int predict(DecTreeNode dt, Instance ins) {
		
		DecTreeNode nd = dt;
		// ROOT case
		
		
		// normal case
		
		while(nd.label == -1) {
			
			int branchNo = 0;
			// get correct branch to go
			
			// it has a children; this children's attrIndex shows the splitted Attr, and its attrType shows the type

			DecTreeNode ch0 = nd.children.get(0);
			int attrIndex = ch0.attrIndex, attrType = ch0.attrType;
			if(attrType == 0) {
				double insAttrVal = ins.getNumericAttribute(attrIndex);
				double thres = Double.parseDouble(ch0.numericAttrValThres.substring(4)); // " <= 123.000"
				
				if(insAttrVal <= thres) {
					//return ch0.label;
					nd = ch0;
				}
				else
				{
					//return nd.children.get(1).label;
					nd = nd.children.get(1);
				}
			}
			else {
				//loop all branches
				for (DecTreeNode ch : nd.children) {
					if(ch.nominalAttrVal.equals(ins.getNominalAttribute(attrIndex))) {
						//return ch.label;
						nd = ch;
						break;
					}
				}
			}

			
		}
		
		
		
		return nd.label;
	}


	// use thres as the lower bound x >= thres

	public DecTreeNode ID3( 
			List<Instance> examples, 
			int targetAttrIndex, int targetAttrType, String targetNumericAttrThresStr, String targetNominalAttrVal, 
			Set<Integer> attributes,
			DataSet data
			) {

		// watch for empty examples
		if(examples.size() == 0) {
			throw new RuntimeException("Empty examples."); 
		}

		DecTreeNode root = new DecTreeNode();
					
		
		int[] labelCnt = new int[2];
		labelCnt[0] = 0;
		labelCnt[1] = 0;

		for(Instance ins : examples) {
			++labelCnt[ins.label];
		}

		// stopping criteria i, ii, iv
		// same label for all
		// OR
		// empty Attribute list
		if( (labelCnt[0] == 0 || labelCnt[1] == 0)
				|| examples.size() < m
				|| attributes.size() <= 0) {

			// leaf node need label
			if(labelCnt[0] >= labelCnt[1] )
				root.label = 0;
			else
				root.label = 1;

			root.attrIndex = targetAttrIndex;
			root.attrType = targetAttrType;

			if(root.attrType == 0) {
				root.numericAttrValThres = targetNumericAttrThresStr;
			}
			else {
				root.nominalAttrVal = targetNominalAttrVal;
			}
			
			root.children = null;

			return root;
		}

		//TODO: impl get Best attr.
		// TODO: find a better way for outputThres
		// likely data is removable
		List<Double> outputThres = new ArrayList<Double>();
		int A = 
				getBestAttr(
						examples,
						attributes,
						data,
						outputThres
						);
		
		// stopping criteria iii
		if( A == -1) {
			// leaf node need label
			if(labelCnt[0] > labelCnt[1] )
				root.label = 0;
			else
				root.label = 1;

			root.attrIndex = targetAttrIndex;
			root.attrType = targetAttrType;

			if(root.attrType == 0) {
				root.numericAttrValThres = targetNumericAttrThresStr;
			}
			else {
				root.nominalAttrVal = targetNominalAttrVal;
			}
			
			root.children = null;

			return root;
		}
		
		
		// for root node
		int decisionAttrIndex= A;
		int decisionAttrType = data.attrTypeMap.get(decisionAttrIndex);
		


		// 1. get an list of examples with sorted order in THAT attribute.
		// Sort by the decision attribute. Works for both attr types 
		List<Instance> list1 = new ArrayList<Instance>(examples);

		Collections.sort(list1, Instance.getAttrCompar(decisionAttrIndex, decisionAttrType, data));
		
		//DecTreeNode subtree = null;
		
		String thresStrGlobal = null, nominalGlobal = null;

		// create subtree
		if(decisionAttrType == 0) {
			
			//--
			int attrIndex = decisionAttrIndex; 
			double thres = outputThres.get(0);

			Map<Integer, List<Instance>> thresInsListMap = new HashMap<Integer, List<Instance>>();

			List<Instance> insList = new ArrayList<Instance>();
			thresInsListMap.put(0, insList);
						
			int i = 0, branchCnt = 0;
			boolean exceedthres = false;
			
			for(Instance ins : list1) {

				if(ins.numericAttrMap.get(attrIndex) > thres && !exceedthres) {
					branchCnt ++;
					insList = new ArrayList<Instance>();
					thresInsListMap.put(branchCnt, insList);
					exceedthres = true;
				}
				
				insList.add(ins);
				++i;
			}
			

			
			// 4. For each branch...
			
			for(i = 0; i < thresInsListMap.size(); ++i) {
				// so, this is a new tree branch
				// already have the subset of instances for this branch
				insList= thresInsListMap.get(i);
				
				String thresStr = null;
				if (i == 0) {
					thresStr = new String(" <= " + Double.toString(thres));
				}
				else
					thresStr = new String(" > " + Double.toString(thres));
				
				// stopping criteria
				if(insList.size() <= 0) {
					DecTreeNode leaf = new DecTreeNode();
					
					// leaf node need label
					leaf.label = majorityVote(examples);

					leaf.attrIndex = decisionAttrIndex;
					leaf.attrType = decisionAttrType;
					leaf.numericAttrValThres = thresStr; //this branch's value is all bigger than this threshold
					leaf.children = null; // indicate leaf
					root.addChild(leaf);
				}
				else {
					Set<Integer> remainAttributes = new HashSet<Integer>(attributes);
					//remainAttributes.remove(decisionAttrIndex);
					DecTreeNode subtree = ID3(
							insList, 
							decisionAttrIndex, decisionAttrType, thresStr, new String("N/A"), 
							remainAttributes,
							data);

					// -- update root's info
					root.addChild(subtree);
					root.attrIndex = targetAttrIndex;
					root.attrType = targetAttrType;
					//root.label // no label for internal node
					root.numericAttrValThres = targetNumericAttrThresStr;
					root.nominalAttrVal = targetNominalAttrVal;

				}
				
				
			}

		}
		else if(decisionAttrType == 1) {
			//--
			int attrIndex = decisionAttrIndex; 


			Map<Integer, List<Instance>> branchInsListMap = new HashMap<Integer, List<Instance>>();
			Map<Integer, String> branchValMap= new HashMap<Integer, String>();
			List<Instance> insList = new ArrayList<Instance>();
			branchInsListMap.put(0, insList);
			branchValMap.put(0, list1.get(0).getNominalAttribute(decisionAttrIndex));
			
			// ******************************************************************************************
			int tempCnt = 0;
			for (String nomValue: data.nominalAttrValMap.get(attrIndex)) {
				List<Instance> tempList = new ArrayList<Instance>();
				for(Instance ins : list1) { 
					if(ins.nominalAttrMap.get(attrIndex).equals(nomValue)) {
						tempList.add(ins);
					}
				}
				branchInsListMap.put(tempCnt, tempList);
				branchValMap.put(tempCnt, nomValue);
				tempCnt ++;
			}
			
			
			// ****************************************************************************************** 
			//--
			
// ****************************************************************************************** 			
//			String currVal = null, prevVal = null;
//
//
//			int i = 0, branchCnt = 0;
//			for(Instance ins : list1) {
//
//				currVal = list1.get(i).getNominalAttribute(decisionAttrIndex);
//				
//				//this block handles per value of attr					
//				if( i !=0 && !(currVal.equals(prevVal)) ) {
//					branchCnt ++;
//					insList = new ArrayList<Instance>();
//					branchInsListMap.put(branchCnt, insList);
//					branchValMap.put(branchCnt, currVal);
//				}
//
//				insList.add(ins);
//				
//				// update
//				prevVal = currVal;
//				++i;
//			}
// ****************************************************************************************** 

			
			
			// 4. For each branch...
			int i = 0;
			for(i = 0; i < branchInsListMap.size(); ++i) {
				// already have the subset of instances for this branch
				insList= branchInsListMap.get(i);
				String nominal = branchValMap.get(i);
				
				// keep for now. likely should agree with requirement of hw
				if(insList.size() <= 0) {
					DecTreeNode leaf = new DecTreeNode();

					leaf.label = majorityVote(examples);

					leaf.attrIndex = decisionAttrIndex;
					leaf.attrType = decisionAttrType;
					leaf.nominalAttrVal= branchValMap.get(i); //this branch's value is all bigger than this threshold
					leaf.children = null; // indicate leaf
					root.addChild(leaf);
				}
				else {

					Set<Integer> remainAttributes = new HashSet<Integer>(attributes);
					// TODO: test
					//remainAttributes.remove(decisionAttrIndex);
					// index for that value in its attr's all possible values
					//int nominalValIndex = data.nominalAttrValPosMap.get(decisionAttrIndex).get(nominal);
					DecTreeNode subtree = ID3(
							insList, 
							decisionAttrIndex, decisionAttrType, new String(">neginf"), new String(nominal), 
							remainAttributes,
							data);
					
					// -- update root info
					root.addChild(subtree);

					root.attrIndex = targetAttrIndex; 
					root.attrType = targetAttrType;
					//root.label
					
					// this level's info
					root.numericAttrValThres = targetNumericAttrThresStr;
					root.nominalAttrVal = targetNominalAttrVal;
					
				}


			}

		}
		else {
			throw new RuntimeException("Unknown attrType."); 
		}

		return root;
	}
	
	
	private int majorityVote(List<Instance> examples) {
		int [] labels = new int[2];
		
		for (Instance ins : examples) {
			++ labels[ins.label];
		}
		
		if(labels[0] >= labels[1])
			return 0;
		else
			return 1;
	}

	//helper functions
	public double log2(double a) {
		return Math.log10(a)/Math.log10(2.0);
	}

	
	private int getBestAttr(List<Instance> examples, Set<Integer> attributes, 
			DataSet data,
			List<Double> outputThresList) {
		// TODO Auto-generated method stub
		
		// overall op: calculate overall entropy
		int [] labels = new int[2];
		for (Instance ins : examples) {
			labels[ins.label] ++;
		}
		
		double p0 = (labels[0]*1.0/(labels[0] + labels[1])), p1 = 1.0 - p0;
		//double H_D_Y = 0.0 - p0 * log2(p0) - p1 * log2(p1);
		double H_D_Y = 0.0 - plogp(p0) - plogp(p1);
		
		// --
		Map<Integer, Double> attrGainMap =  new HashMap<Integer, Double>();
		// only relevant for numeric attr
		Map<Integer, Double> numericAttrThresMap =  new HashMap<Integer, Double>();

		
		
		for (Integer ia : attributes) {
			// common op for num and nom: 
			// get # of vals for that attr
			
			// --sort
			List<Instance> list1 = new ArrayList<Instance>(examples);
			Collections.sort(list1, Instance.getAttrCompar(ia, data.attrTypeMap.get(ia), data));

			

			
			
			if(data.attrTypeMap.get(ia) == 0) {
				// numeric
				
				int currLabel = -1, prevLabel = -1;
				Double currVal = neginf, prevVal = neginf;

				//--
				int currCnt = 0, thresCnt = 0;
				int[] currLabelCnt  = new int[2];
				currLabelCnt[0] = 0;
				currLabelCnt[1] = 0;
				Map<Integer, Double> insThresMap = new HashMap<Integer, Double>();
				Map<Integer, Double> insGainMap =  new HashMap<Integer, Double>();
				
				
				double currThres = neginf;
				boolean fastForward = false;
				int i = 0;
				for(Instance ins : list1) {
					currLabel = list1.get(i).label;
					currVal = list1.get(i).getNumericAttribute(ia);
					
					// reset fast Forward flag if diff val
					if(Math.abs(currVal - prevVal) > eta) {
						fastForward = false;
					} 

					// works for i = 0 case, and single Label case
					if( (!fastForward) && (i != 0) && (currLabel != prevLabel) ) {
						
						// though label diff, chk for diff Val
						if(Math.abs(currVal - prevVal) < eta) {	//
							// set flag
							fastForward = true;
							
							// move update here for the first iteration after flag
							++ currCnt;
							++ currLabelCnt[ins.label]; 

							if( !fastForward )
								prevLabel = currLabel;
							prevVal = currVal;
							++i;
							
							continue;
						}

						// get thres as the lower boundary of this branch
						if( Math.abs(prevVal - neginf) < eta) {
							
							insThresMap.put(thresCnt, neginf);
						}
						else { 
							currThres = 0.5 * (prevVal + currVal);
							insThresMap.put(thresCnt, currThres);
							
							
						}
						
						// calculate Info Gain for this thres
						double pa_true = (1.0*currLabelCnt[0]/(currLabelCnt[0] + currLabelCnt[1])), 
								pb_true = 1.0 - pa_true,
							H_D_Y_true = 0.0 - plogp(pa_true) - plogp(pb_true),
							p_true = 1.0*(currLabelCnt[0] + currLabelCnt[1]) / (labels[0] + labels[1]);
						double pa_false = (1.0*(labels[0] - currLabelCnt[0])/( labels[0] + labels[1] - (currLabelCnt[0] + currLabelCnt[1]) )), 
								pb_false = 1.0 - pa_false,
								H_D_Y_false = 0.0 - plogp(pa_false) - plogp(pb_false),
								p_false = 1.0 - p_true;
						
						double H_D_Y_ia = (p_true * H_D_Y_true + p_false * H_D_Y_false),
								gain_D_ia = H_D_Y - H_D_Y_ia;
						insGainMap.put(thresCnt, gain_D_ia);
						
						++ thresCnt;
					}

					// update
					++ currCnt;
					++ currLabelCnt[ins.label]; 
					
					if( !fastForward )
						prevLabel = currLabel; // now this label is only for counting non-same value's label
					prevVal = currVal;
					++i;
				}

				// -- update for last segment
				// calculate Info Gain for this thres
				// <thres? == true
				double pa_true = (1.0*currLabelCnt[0]/(currLabelCnt[0] + currLabelCnt[1])), 
						pb_true = 1.0 - pa_true,
						H_D_Y_true = 0.0 - plogp(pa_true) - plogp(pb_true),
						p_true = 1.0*(currLabelCnt[0] + currLabelCnt[1]) / (labels[0] + labels[1]);
				double pa_false = (1.0*(labels[0] - currLabelCnt[0])/( labels[0] + labels[1] - (currLabelCnt[0] + currLabelCnt[1]) )), 
						pb_false = 1.0 - pa_false,
						H_D_Y_false = 0.0 - plogp(pa_false) - plogp(pb_false),
						p_false = 1.0 - p_true;

				double H_D_Y_ia = (p_true * H_D_Y_true + p_false * H_D_Y_false),
						gain_D_ia = H_D_Y - H_D_Y_ia;
				insGainMap.put(thresCnt, gain_D_ia);
				
				
				Double maxGain = neginf;
				int maxGainIndex = 0;
				for(i = 0; i < insGainMap.size(); ++ i ) {
					if(insGainMap.get(i) > maxGain ) {
						maxGain =insGainMap.get(i);
						maxGainIndex = i;
					}
				}
				
				//--
				attrGainMap.put(ia, maxGain);
				numericAttrThresMap.put(ia, insThresMap.get(maxGainIndex));
				
			}
			else if(data.attrTypeMap.get(ia) == 1) {
				// nominal
				
				// ALG:
				// for each value
					// cnt label = +, label = -, calc H_D_Y_value
				// sum H_D_Y_value
				// H_D_Y - sum = gain
				
				//--
				
		
				//--
				String currVal = null, prevVal = null;
				int[] currLabelCnt  = new int[2];
				currLabelCnt[0] = 0;
				currLabelCnt[1] = 0;

				// overall entropy for that attribute ia 
				Double ent_ia = 0.0;
				
				int i = 0;
				for(Instance ins : list1) {

					currVal = list1.get(i).getNominalAttribute(ia);
					
					
					//----
					//this block handles per value of attr					
					if( i !=0 && !(currVal.equals(prevVal)) ) {

						// calculate Info Gain for this thres
						double pa_oneVal = 1.0*currLabelCnt[0]/(currLabelCnt[0] + currLabelCnt[1]), 
								pb_oneVal = 1.0 - pa_oneVal,
								H_D_Y_oneVal = 0.0 - plogp(pa_oneVal) - plogp(pb_oneVal),
								p_oneVal = 1.0 * (currLabelCnt[0] + currLabelCnt[1]) / (labels[0] + labels[1]);
						
						ent_ia += (p_oneVal * H_D_Y_oneVal); 
						
						currLabelCnt[0] = 0;
						currLabelCnt[1] = 0;
					}

					// update
					++ currLabelCnt[ins.label]; 
					
					prevVal = currVal;
					++i;
				}
				
				//-- update for last segment
				double pa_oneVal = 1.0*currLabelCnt[0]/(currLabelCnt[0] + currLabelCnt[1]), 
						pb_oneVal = 1.0 - pa_oneVal,
						H_D_Y_oneVal = 0.0 - plogp(pa_oneVal) - plogp(pb_oneVal),
						p_oneVal = 1.0 * (currLabelCnt[0] + currLabelCnt[1]) / (labels[0] + labels[1]);

				ent_ia += (p_oneVal * H_D_Y_oneVal); 

				currLabelCnt[0] = 0;
				currLabelCnt[1] = 0;

				// gain
				double gain_ia = H_D_Y - ent_ia;
				
				attrGainMap.put(ia, gain_ia);

			}
			else {
				throw new RuntimeException("err: Unknonw datatype");
			}
			
			
		}
		
		Double maxGain = neginf;
		int maxGainIndex = Integer.MAX_VALUE;
		
		for(Entry<Integer, Double> ent : attrGainMap.entrySet()) {
			int i = ent.getKey();
			double gain = ent.getValue();
			if(( attrGainMap.get(i) - maxGain ) > eta) {
				maxGain = gain;
				maxGainIndex = i;
			}
			else if( Math.abs(maxGain - attrGainMap.get(i)) <= eta && i < maxGainIndex){
				maxGain = gain;
				maxGainIndex = i;
			}
		}

		if(data.attrTypeMap.get(maxGainIndex) == 0)
			outputThresList.add(numericAttrThresMap.get(maxGainIndex));
		
		if(maxGain > 0)
			return maxGainIndex;
		else
			return -1;
	}
	
	private double plogp(double p) {
		double eta = 1E-8;
		if(Math.abs(p) < eta) 
			return 0.0;
		else
			return p * log2(p);
		
	}

	//
	public DecTreeNode Learn(DataSet data){
		
		
		List<Instance> instanceList = new ArrayList<Instance>();
		Map<Integer, Instance> instanceMap = data.instances;
		for(int i = 0; i < instanceMap.size(); ++i) {
			Instance ins = instanceMap.get(i);
			instanceList.add(ins);
		}
		
		Set<Integer> attributeSet = new HashSet<Integer>();
		for (int i = 0; i < data.attrCnt; i++) {
			attributeSet.add(i);
		}
		
		return ID3(
				instanceList,
				-1, -1, new String("root"), new String("root"), 
				attributeSet,
				data);
	
	
		
	}

}
