import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.*;
import java.awt.Label;
import java.lang.Math;

/**
 * Fill in the implementation details of the class DecisionTree using this file.
 * Any methods or secondary classes that you want are fine but we will only
 * interact with those methods in the DecisionTree framework.
 * 
 * You must add code for the 1 member and 4 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
	private DecTreeNode root;
	// ordered list of class labels
	private List<String> labels;
	// ordered list of attributes
	private List<String> attributes;
	// map to ordered discrete values taken by attributes
	private Map<String, List<String>> attributeValues;
	// map for getting the index
	private HashMap<String, Integer> label_inv;
	private HashMap<String, Integer> attr_inv;

	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl() {
		// no code necessary this is void purposefully
	}

	/**
	 * Build a decision tree given only a training set.
	 * 
	 * @param train:
	 *            the training set
	 */
	DecisionTreeImpl(DataSet train) {
		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		// copy of list of attributes that will be modified in built
		// List<String> temp = attributes;

		// TODO: Homework requirement, learn the decision tree here
		// Get the list of instances via train.instances
		// You should write a recursive helper function to build the tree
		//
		// this.labels contains the possible labels for an instance
		// this.attributes contains the whole set of attribute names
		// train.instances contains the list of instances
		root = buildtree(null, train.instances, attributes, root);

	}

	private DecTreeNode buildtree(String prnt_v, List<Instance> ex, List<String> attr, DecTreeNode prnt) {
		// minimum conditional Entropy
		double min_ce = (double) Integer.MAX_VALUE;
		// current conditional Entropy
		double curr_ce = 0.0;
		String def_lbl = "";
		boolean terminal = false;
		// best attribute
		String q = "";
		if (ex.isEmpty()) {
			def_lbl = prnt.label;
			terminal = true;
		} else if (sameLabel(ex) || attr.isEmpty()) {
			def_lbl = majorityLabel(ex);
			terminal = true;
		} else {
			def_lbl = majorityLabel(ex);
			for (String a : attr) {
				curr_ce = conditionalEntropy(ex, a);
				if (curr_ce < min_ce) {
					min_ce = curr_ce;
					q = a;
				}
			}

		}
		DecTreeNode node = new DecTreeNode(def_lbl, q, prnt_v, terminal);
		if (!terminal) {
			for (String v : attributeValues.get(q)) {
				List<Instance> v_ex = new ArrayList<Instance>();
				int index = getAttributeIndex(q);
				for (Instance e : ex) {
					if (e.attributes.get(index).equals(v)) {
						// v_ex = subset of examples with q == v
						v_ex.add(e);
					}
				}
				List<String> sub = new ArrayList<String>(attr);
				sub.remove(q);
				DecTreeNode subtree = buildtree(v, v_ex, sub, node);
				node.addChild(subtree);
			}
		}
		return node;
	}

	boolean sameLabel(List<Instance> instances) {
		// Suggested helper function
		// returns if all the instances have the same label
		// labels are in instances.get(i).label
		// TODO
		String temp = instances.get(0).label;
		// debug:couter equals 0 not 1
		int counter = 0;
		for (Instance a : instances) {
			if (a.label.equals(temp)) {
				counter++;
			}
		}
		if (counter >= instances.size()) {
			return true;
		}
		return false;
	}

	String majorityLabel(List<Instance> instances) {
		// Suggested helper function
		// returns the majority label of a list of examples
		// list of votes corresponding to index of each label in the label list
		int votes[] = new int[labels.size()];
		String freq = instances.get(0).label;
		int max = 0;
		int max_index = 0;

		for (Instance a : instances) {
			votes[getLabelIndex(a.label)]++;
		}
		for (int i = 0; i < votes.length; i++) {
			if (votes[i] > max) {
				max = votes[i];
				max_index = i;
			}
		}
		freq = labels.get(max_index);
		return freq;
	}

	double entropy(List<Instance> instances) {
		// Suggested helper function
		// returns the Entropy of a list of examples
		// TODO
		// Occurrence of each label
		int[] occur = new int[labels.size()];
		// weight of each label
		double[] weights = new double[labels.size()];
		double sum = 0.0;
		if (instances.isEmpty()) {
			return 0.0;
		}

		for (Instance e : instances) {
			occur[getLabelIndex(e.label)]++;
		}
		for (int i = 0; i < labels.size(); i++) {
			if (occur[i] != 0) {
				weights[i] = (double) occur[i] / (double) instances.size();
				sum -= weights[i] * ((double) Math.log(weights[i])) / (double) (Math.log(2));
			}
		}
		return sum;
	}

	double conditionalEntropy(List<Instance> instances, String attr) {
		// Suggested helper function
		// returns the conditional entropy of a list of examples, given the
		// attribute attr
		// TODO
		double sum = 0.0;
		List<String> values = attributeValues.get(attr);
		for (String v : values) {
			List<Instance> v_ex = new ArrayList<Instance>();
			// number of times the value v occurs
			double count = 0.0;
			for (Instance e : instances) {
				if (e.attributes.get(getAttributeIndex(attr)) == v) {
					count++;
					v_ex.add(e);
				}
				;
			}

			sum += entropy(v_ex) * ((double) count / (double) instances.size());

		}

		return sum;
	}

	double InfoGain(List<Instance> instances, String attr) {
		// Suggested helper function
		// returns the info gain of a list of examples, given the attribute attr
		return entropy(instances) - conditionalEntropy(instances, attr);
	}

	@Override
	public String classify(Instance instance) {
		// TODO: Homework requirement
		// The tree is already built, when this function is called
		// this.root will contain the learnt decision tree.
		// write a recusive helper function, to return the predicted label of
		// instance
		String pred = "";
		pred = predict(root, instance).label;
		return pred;
	}

	// return a leaf node
	private DecTreeNode predict(DecTreeNode node, Instance instance) {
		// DecTreeNode(String _label, String _attribute, String
		// _parentAttributeValue, boolean _terminal) {
		if (!node.terminal) {
			String v = instance.attributes.get(getAttributeIndex(node.attribute));
			for (DecTreeNode child : node.children) {
				if (v.equals(child.parentAttributeValue)) {
					node = predict(child, instance);
				}
			}
		}
		return node;
	}

	@Override
	public void rootInfoGain(DataSet train) {
		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;

		for (String attr : attributes) {
			System.out.format(attr + " %.5f\n", InfoGain(train.instances, attr));
		}

	}

	@Override
	public void printAccuracy(DataSet test) {
		// TODO: Homework requirement
		// Print the accuracy on the test set.
		// The tree is already built, when this function is called
		// You need to call function classify, and compare the predicted labels.
		// List of instances: test.instances
		// getting the real label: test.instances.get(i).label

		String pred = "";
		int cor = 0;
		for (Instance inst : test.instances) {
			pred = classify(inst);
			if (pred.equals(inst.label)) {
				cor++;
			}
		}
		System.out.format("%.5f\n", (double) cor / (double) test.instances.size());
		return;
	}

	@Override
	/**
	 * Print the decision tree in the specified format Do not modify
	 */
	public void print() {

		printTreeNode(root, null, 0);
	}

	/**
	 * Prints the subtree of the node with each line prefixed by 4 * k spaces.
	 * Do not modify
	 */
	public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < k; i++) {
			sb.append("    ");
		}
		String value;
		if (parent == null) {
			value = "ROOT";
		} else {
			int attributeValueIndex = this.getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
			value = attributeValues.get(parent.attribute).get(attributeValueIndex);
		}
		sb.append(value);
		if (p.terminal) {
			sb.append(" (" + p.label + ")");
			System.out.println(sb.toString());
		} else {
			sb.append(" {" + p.attribute + "?}");
			System.out.println(sb.toString());
			for (DecTreeNode child : p.children) {
				printTreeNode(child, p, k + 1);
			}
		}
	}

	/**
	 * Helper function to get the index of the label in labels list
	 */
	private int getLabelIndex(String label) {
		if (label_inv == null) {
			this.label_inv = new HashMap<String, Integer>();
			for (int i = 0; i < labels.size(); i++) {
				label_inv.put(labels.get(i), i);
			}
		}
		return label_inv.get(label);
	}

	/**
	 * Helper function to get the index of the attribute in attributes list
	 */
	private int getAttributeIndex(String attr) {
		if (attr_inv == null) {
			this.attr_inv = new HashMap<String, Integer>();
			for (int i = 0; i < attributes.size(); i++) {
				attr_inv.put(attributes.get(i), i);
			}
		}
		return attr_inv.get(attr);
	}

	/**
	 * Helper function to get the index of the attributeValue in the list for
	 * the attribute key in the attributeValues map
	 */
	private int getAttributeValueIndex(String attr, String value) {
		for (int i = 0; i < attributeValues.get(attr).size(); i++) {
			if (value.equals(attributeValues.get(attr).get(i))) {
				return i;
			}
		}
		return -1;
	}
}
