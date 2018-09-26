package edu.ucsf.rbvi.gxaReader.internal.model;


public class GXACluster {
	final GXAManager gxaManager;

	// The suggested k value
	int k = 0;

	// The number of K values provided
	int nK = 0;

	// The lowest K value
	int minK = 0;

	// The clusters
	int[][] clusters;

	public GXACluster(GXAManager gxaManager) {
		this.gxaManager = gxaManager;
	}

	public int[] getCluster() {
		return getCluster(k);
	}

	public int[] getCluster(int kClust) {
		return clusters[kClust-minK];
	}
}
