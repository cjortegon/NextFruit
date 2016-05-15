package co.edu.icesi.nextfruit.util;

import java.util.ArrayList;

public class GroupManager {

	private ArrayList<Object> objects;
	private ArrayList<double[]> values;
	private int[] groups;
	private double[] maximumDeviation;
	private int [][] adjacencyMatrix;
	private int numberOfGroups;

	public GroupManager(double[] maximumDeviation) {
		this.maximumDeviation = maximumDeviation;
		this.objects = new ArrayList<>();
		this.values = new ArrayList<>();
	}

	public void add(Object object, double[] values) {
		this.objects.add(object);
		this.values.add(values);
	}
	
	public int getNumberOfGroups() {
		return numberOfGroups;
	}
	
	public <E> ArrayList<E> getGroups(int group) {
		ArrayList<E> selectedGroup = new ArrayList<>();
		for (int i = 0; i < groups.length; i++) {
			if(groups[i] == group) {
				selectedGroup.add((E)objects.get(i));
			}
		}
		return selectedGroup;
	}

	public void makeGroups() {
		adjacencyMatrix = new int[objects.size()][objects.size()];
		for (int i = 0; i < objects.size(); i++) {
			for (int j = i + 1; j < objects.size(); j++) {
				boolean connection = true;
				for (int k = 0; k < maximumDeviation.length; k++) {
					if(Math.abs(values.get(i)[k]-values.get(j)[k]) > maximumDeviation[k]) {
						connection = false;
						break;
					}
				}
				if(connection) {
					adjacencyMatrix[i][j] = 1;
					adjacencyMatrix[j][i] = 1;
				} else {
					adjacencyMatrix[i][j] = Integer.MAX_VALUE;
					adjacencyMatrix[j][i] = Integer.MAX_VALUE;
				}
			}
		}
		for (int k = 0; k < adjacencyMatrix.length; k++) {
			for (int i = 0; i < adjacencyMatrix.length; i++) {
				for (int j = 0; j < adjacencyMatrix.length; j++) {
					if(adjacencyMatrix[i][k] != Integer.MAX_VALUE && adjacencyMatrix[k][j] != Integer.MAX_VALUE) {
						if (adjacencyMatrix[i][k] + adjacencyMatrix[k][j] < adjacencyMatrix[i][j]) {
							adjacencyMatrix[i][j] = adjacencyMatrix[i][k] + adjacencyMatrix[k][j];
						}
					}
				}
			}
		}
		numberOfGroups = 0;
		groups = new int[adjacencyMatrix.length];
		for (int i = 0; i < adjacencyMatrix.length; i++) {
			if(groups[i] == 0) {
				numberOfGroups ++;
				groups[i] = numberOfGroups;
				for (int j = 0; j < adjacencyMatrix.length; j++) {
					if(adjacencyMatrix[i][j] != Integer.MAX_VALUE) {
						groups[j] = numberOfGroups;
					}
				}
			}
		}
	}
}
