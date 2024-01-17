package com.jpacman.model.edumode;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.jpacman.model.edumode.graph.Edge;

public class TSPNode {
	List<Point> path;
	List<Edge> pathEdges;

	double gCost;
	double hCost;
	double fCost;

	// used in TSPSolver only
	public TSPNode(List<Point> currentPath, List<Edge> pathEdges, double gCost, double hCost) {
		this.path = new ArrayList<Point>(currentPath);
		this.pathEdges = new ArrayList<Edge>(pathEdges);
		this.gCost = gCost;
		this.hCost = hCost;
		this.fCost = this.gCost + this.hCost;
	}

	public static Comparator<TSPNode> fCostSorter = new Comparator<TSPNode>() {
		@Override
		public int compare(TSPNode n0, TSPNode n1) {
			if (n0.fCost < n1.fCost) {
				return -1;
			} else if (n0.fCost == n1.fCost) {
				return 0;
			} else { // n0.fCost > n1.fCost
				return 1;
			}
		}
	};
}
