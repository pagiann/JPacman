package com.jpacman.model.edumode;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class GraphicalSearchTree {
	public static final int xOffset = 30;
	public static final int yOffset = 30;
	public static final int xGapBetweenSiblings = 10;
	public static final int xGapBetweenSubtrees = 35;
	public static final int yGapBetweenParentAndChildren = 70;
	public static final int gapBetweenNodeAndStringLabel = 6;
	public static final int nodeTileSize = 30;

	private Node rootNode;
	private List<ArrayList<Node>> nodesPerLevel;
	private List<Node> paintableNodes;
	private int height;
	private int numberOfLeafNodes;

	private int maxDrawWidth;
	private int maxDrawHeight;

	int currentMaxXDrawCoordinate;
	int currentMinYDrawCoordinate;
	List<Node> startNodesMarkedForRelocation;

	public GraphicalSearchTree() {
		rootNode = new Node();
		nodesPerLevel = new ArrayList<ArrayList<Node>>();
		paintableNodes = new ArrayList<Node>();
		height = 0;
		numberOfLeafNodes = 0;
		maxDrawWidth = 0;
		maxDrawHeight = 0;
		currentMaxXDrawCoordinate = 0;
		currentMinYDrawCoordinate = 0;
		startNodesMarkedForRelocation = new ArrayList<Node>();
	}

	public GraphicalSearchTree(Node rootNode) {
		this();
		this.rootNode = new Node(rootNode.getCoordinates(), "1");
		this.rootNode.children = rootNode.getChildren();
		this.rootNode.childNodesEdgesDrawCoordinates.clear();
	}

	public void computePaintableTreeCoordinates() {
		// System.out.println("INSIDE computePaintableTreeCoordinates() METHOD");
		sortNodesPerLevel();

		maxDrawHeight = height * yGapBetweenParentAndChildren + gapBetweenNodeAndStringLabel + yOffset;
		maxDrawWidth = 0;

		int currentDepth = height - 1;

		int xDrawCoord = 0;
		int yDrawCoord = maxDrawHeight;

		currentMaxXDrawCoordinate = xOffset;
		currentMinYDrawCoordinate = maxDrawHeight;

		if (height == 0) { // means we have only the root node
			rootNode.drawCoordinates = new Point(currentMaxXDrawCoordinate, currentMinYDrawCoordinate);
			rootNode.childNodesEdgesDrawCoordinates.clear();
			paintableNodes.add(rootNode);
			return;
		}

		ArrayList<Node> startLevelParentNodes = new ArrayList<Node>(nodesPerLevel.get(currentDepth));

		// System.out.println("\nSTART OF ALGORITHM - 1st PHASE\n");
		// Initial phase of the algorithm (draw coordinates computation of nodes in last
		// two levels)
		while (!startLevelParentNodes.isEmpty()) {
			Node currentParentNode = startLevelParentNodes.remove(0);

			List<Node> childNodes = currentParentNode.getChildren();
			int numberOfChildren = childNodes.size();
			// System.out.println("numberOfChildren = " + numberOfChildren);

			for (int i = 0; i < numberOfChildren; i++) {
				// System.out.println("childNode = " + childNodes.get(i).getId());
				xDrawCoord = currentMaxXDrawCoordinate;
				if (i > 0) {
					xDrawCoord += xGapBetweenSiblings;
				}
				childNodes.get(i).drawCoordinates = new Point(xDrawCoord, yDrawCoord);
				paintableNodes.add(childNodes.get(i));
				currentMaxXDrawCoordinate = xDrawCoord + nodeTileSize;
			}

			computePositionOfParentNodeAndItsEdges(currentParentNode, childNodes);
			currentMaxXDrawCoordinate += xGapBetweenSubtrees;
			if (currentMaxXDrawCoordinate > maxDrawWidth) {
				maxDrawWidth = currentMaxXDrawCoordinate;
			}
		}

		// System.out.println("\nCONTINUATION OF ALGORITHM - 2st PHASE\n");
		// Second phase of the algorithm (till the root node)
		currentDepth = height - 2;
		ArrayList<Node> currentLevelNodes;
		startNodesMarkedForRelocation.clear();
		while (currentDepth >= 0) {
			// System.out.println("\ncurrentDepth = " + currentDepth + "\n");

			currentMaxXDrawCoordinate = xOffset;
			currentMinYDrawCoordinate = maxDrawHeight - (height - currentDepth - 1) * yGapBetweenParentAndChildren;
			currentLevelNodes = new ArrayList<Node>(nodesPerLevel.get(currentDepth));

			while (!currentLevelNodes.isEmpty()) {
				Node currentNode = currentLevelNodes.remove(0);
				computePositionOfParentNodeAndItsEdges(currentNode, currentNode.getChildren());
				currentMaxXDrawCoordinate += xGapBetweenSubtrees;
				if (currentMaxXDrawCoordinate > maxDrawWidth) {
					maxDrawWidth = currentMaxXDrawCoordinate;
				}
			}

			for (Node startNode : startNodesMarkedForRelocation) {
				int index = nodesPerLevel.get(currentDepth).indexOf(startNode);
				int end = nodesPerLevel.get(currentDepth).size();
				relocateSubtrees(nodesPerLevel.get(currentDepth).subList(index, end));
			}
			startNodesMarkedForRelocation.clear();

			currentDepth--;
		}

		// reverse paintableNodes list if the nodes must be painted from to top to
		// bottom
		// Collections.reverse(paintableNodes);
		// System.out.println("maxDrawWidth = " + maxDrawWidth);
	}

	private void sortNodesPerLevel() {
		ArrayList<Node> currentLevelList = new ArrayList<Node>();
		ArrayList<Node> tempList = new ArrayList<Node>();

		tempList.add(rootNode);
		nodesPerLevel.add(new ArrayList<Node>(tempList));

		while (true) {
			while (!tempList.isEmpty()) {
				Node currentNode = tempList.remove(0);
				for (Node childNode : currentNode.getChildren()) {
					currentLevelList.add(childNode);
				}
				// set any leaf nodes
				if (currentNode.getChildren().isEmpty()) {
					currentNode.setLeaf(true);
					numberOfLeafNodes++;
				}
			}

			if (currentLevelList.isEmpty()) {
				break;
			} else {
				nodesPerLevel.add(new ArrayList<Node>(currentLevelList));
				tempList = new ArrayList<Node>(currentLevelList);
				currentLevelList.clear();
			}
		}
	}

	private void computePositionOfParentNodeAndItsEdges(Node parentNode, List<Node> childNodes) {
		int numberOfChildNodes = childNodes.size();
		// System.out.println("parentNode id = " + parentNode.getId());
		// System.out.println("childNodes = " + childNodes);
		Node firstChildNode;
		Node secondChildNode;
		Node thirdChildNode;
		Node fourthChildNode;

		Point firstChildNodeDrawCoordinates;
		Point secondChildNodeDrawCoordinates;
		Point thirdChildNodeDrawCoordinates;
		Point fourthChildNodeDrawCoordinates;
		float x1, x2, y1, y2;

		Point coordinates;
		// System.out.println("currentMaxXDrawCoordinate = " +
		// currentMaxXDrawCoordinate);

		// clears any node edges added in previous run
		parentNode.childNodesEdgesDrawCoordinates = new ArrayList<Line2D.Float>();

		switch (numberOfChildNodes) {
			case 0:
				coordinates = new Point(currentMaxXDrawCoordinate,
						currentMinYDrawCoordinate - yGapBetweenParentAndChildren);
				// System.out.println("coordinates = " + coordinates);
				// System.out.println("parentNode = " + parentNode.getId());
				parentNode.drawCoordinates = coordinates;
				paintableNodes.add(parentNode);
				if (parentNode.getDepth() < height - 1) {
					checkIfSubtreeRelocationIsRequired(parentNode);
				}
				currentMaxXDrawCoordinate = coordinates.x + nodeTileSize;
				break;
			case 1:
				firstChildNode = childNodes.get(0); // get the 1st (child) node
				firstChildNodeDrawCoordinates = firstChildNode.getDrawCoordinates();
				x1 = firstChildNodeDrawCoordinates.x + nodeTileSize / 2;
				y1 = firstChildNodeDrawCoordinates.y - yGapBetweenParentAndChildren + nodeTileSize;
				x2 = x1;
				y2 = firstChildNodeDrawCoordinates.y;
				// add the 1st and only edge
				firstChildNode.parentEdgeDrawCoordinates = new Line2D.Float(x1, y1, x2, y2);
				parentNode.childNodesEdgesDrawCoordinates.add(firstChildNode.parentEdgeDrawCoordinates);
				coordinates = new Point(firstChildNodeDrawCoordinates.x,
						firstChildNodeDrawCoordinates.y - yGapBetweenParentAndChildren);
				// System.out.println("coordinates = " + coordinates);
				// System.out.println("parentNode = " + parentNode.getId());
				parentNode.drawCoordinates = coordinates;
				paintableNodes.add(parentNode);
				if (parentNode.getDepth() < height - 1) {
					checkIfSubtreeRelocationIsRequired(parentNode);
				}
				currentMaxXDrawCoordinate = coordinates.x + nodeTileSize;
				// System.out.println("1 - currentMaxXDrawCoordinate = " +
				// currentMaxXDrawCoordinate);
				break;
			case 2:
				firstChildNode = childNodes.get(0); // get the 1st (child) node
				secondChildNode = childNodes.get(1); // get the 2nd (child) node
				firstChildNodeDrawCoordinates = firstChildNode.getDrawCoordinates();
				secondChildNodeDrawCoordinates = secondChildNode.getDrawCoordinates();
				int xDrawCoordForParentWithTwoChildren = firstChildNodeDrawCoordinates.x
						+ ((secondChildNodeDrawCoordinates.x + nodeTileSize - firstChildNodeDrawCoordinates.x) / 2)
						- (nodeTileSize / 2);
				x1 = xDrawCoordForParentWithTwoChildren;
				y1 = firstChildNodeDrawCoordinates.y - yGapBetweenParentAndChildren + nodeTileSize;
				x2 = firstChildNodeDrawCoordinates.x + nodeTileSize / 2;
				y2 = firstChildNodeDrawCoordinates.y;
				// add the 1st edge
				firstChildNode.parentEdgeDrawCoordinates = new Line2D.Float(x1, y1, x2, y2);
				parentNode.childNodesEdgesDrawCoordinates.add(firstChildNode.parentEdgeDrawCoordinates);
				x1 = xDrawCoordForParentWithTwoChildren + nodeTileSize;
				y1 = secondChildNodeDrawCoordinates.y - yGapBetweenParentAndChildren + nodeTileSize;
				x2 = secondChildNodeDrawCoordinates.x + nodeTileSize / 2;
				y2 = secondChildNodeDrawCoordinates.y;
				// add the 2nd edge
				secondChildNode.parentEdgeDrawCoordinates = new Line2D.Float(x1, y1, x2, y2);
				parentNode.childNodesEdgesDrawCoordinates.add(secondChildNode.parentEdgeDrawCoordinates);
				coordinates = new Point(xDrawCoordForParentWithTwoChildren,
						firstChildNodeDrawCoordinates.y - yGapBetweenParentAndChildren);
				// System.out.println("coordinates = " + coordinates);
				// System.out.println("parentNode = " + parentNode.getId());
				parentNode.drawCoordinates = coordinates;
				paintableNodes.add(parentNode);
				if (parentNode.getDepth() < height - 1) {
					checkIfSubtreeRelocationIsRequired(parentNode);
					// currentMaxXDrawCoordinate = coordinates.x + nodeTileSize;
					// System.out.println("4 - currentMaxXDrawCoordinate = " +
					// currentMaxXDrawCoordinate);
				}
				if (parentNode.getDepth() == height - 1) {
					currentMaxXDrawCoordinate = secondChildNodeDrawCoordinates.x + nodeTileSize;
				} else {
					currentMaxXDrawCoordinate = coordinates.x + nodeTileSize;
				}
				// currentMaxXDrawCoordinate = coordinates.x + nodeTileSize;
				break;
			case 3:
				firstChildNode = childNodes.get(0); // get the 1st (child) node
				secondChildNode = childNodes.get(1); // get the 2nd (child) node
				thirdChildNode = childNodes.get(2); // get the 3rd (child) node
				firstChildNodeDrawCoordinates = firstChildNode.getDrawCoordinates();
				secondChildNodeDrawCoordinates = secondChildNode.getDrawCoordinates();
				thirdChildNodeDrawCoordinates = thirdChildNode.getDrawCoordinates();
				int xDrawCoordForParentWithThreeChildren = firstChildNodeDrawCoordinates.x
						+ ((thirdChildNodeDrawCoordinates.x + nodeTileSize - firstChildNodeDrawCoordinates.x) / 2)
						- (nodeTileSize / 2);
				int yCoordinatesOfParent = secondChildNodeDrawCoordinates.y - yGapBetweenParentAndChildren
						+ nodeTileSize;
				x1 = xDrawCoordForParentWithThreeChildren;
				y1 = yCoordinatesOfParent;
				x2 = firstChildNodeDrawCoordinates.x + nodeTileSize / 2;
				y2 = firstChildNodeDrawCoordinates.y;
				// add the 1st edge
				firstChildNode.parentEdgeDrawCoordinates = new Line2D.Float(x1, y1, x2, y2);
				parentNode.childNodesEdgesDrawCoordinates.add(firstChildNode.parentEdgeDrawCoordinates);
				x1 = xDrawCoordForParentWithThreeChildren + nodeTileSize / 2;
				y1 = yCoordinatesOfParent;
				x2 = secondChildNodeDrawCoordinates.x + nodeTileSize / 2;
				y2 = secondChildNodeDrawCoordinates.y;
				// add the 2nd edge
				secondChildNode.parentEdgeDrawCoordinates = new Line2D.Float(x1, y1, x2, y2);
				parentNode.childNodesEdgesDrawCoordinates.add(secondChildNode.parentEdgeDrawCoordinates);
				x1 = xDrawCoordForParentWithThreeChildren + nodeTileSize;
				y1 = yCoordinatesOfParent;
				x2 = thirdChildNodeDrawCoordinates.x + nodeTileSize / 2;
				y2 = thirdChildNodeDrawCoordinates.y;
				// add the 3rd edge
				thirdChildNode.parentEdgeDrawCoordinates = new Line2D.Float(x1, y1, x2, y2);
				parentNode.childNodesEdgesDrawCoordinates.add(thirdChildNode.parentEdgeDrawCoordinates);
				coordinates = new Point(xDrawCoordForParentWithThreeChildren,
						secondChildNodeDrawCoordinates.y - yGapBetweenParentAndChildren);
				// System.out.println("coordinates = " + coordinates);
				// System.out.println("parentNode = " + parentNode.getId());
				parentNode.drawCoordinates = coordinates;
				paintableNodes.add(parentNode);
				if (parentNode.getDepth() < height - 1) {
					checkIfSubtreeRelocationIsRequired(parentNode);
				}
				if (parentNode.getDepth() == height - 1) {
					currentMaxXDrawCoordinate = thirdChildNodeDrawCoordinates.x + nodeTileSize;
				} else {
					currentMaxXDrawCoordinate = coordinates.x + nodeTileSize;
				}
				// currentMaxXDrawCoordinate = coordinates.x + nodeTileSize;
				break;
			case 4:
				firstChildNode = childNodes.get(0); // get the 1st (child) node
				secondChildNode = childNodes.get(1); // get the 2nd (child) node
				thirdChildNode = childNodes.get(2); // get the 3rd (child) node
				fourthChildNode = childNodes.get(3); // get the 4th (child) node
				firstChildNodeDrawCoordinates = firstChildNode.getDrawCoordinates();
				secondChildNodeDrawCoordinates = secondChildNode.getDrawCoordinates();
				thirdChildNodeDrawCoordinates = thirdChildNode.getDrawCoordinates();
				fourthChildNodeDrawCoordinates = fourthChildNode.getDrawCoordinates();
				int xDrawCoordForParentWithFourChildren = firstChildNodeDrawCoordinates.x
						+ ((fourthChildNodeDrawCoordinates.x + nodeTileSize - firstChildNodeDrawCoordinates.x) / 2)
						- (nodeTileSize / 2);
				int yCoordinatesOfParent2 = secondChildNodeDrawCoordinates.y - yGapBetweenParentAndChildren
						+ nodeTileSize;
				x1 = xDrawCoordForParentWithFourChildren;
				y1 = yCoordinatesOfParent2;
				x2 = firstChildNodeDrawCoordinates.x + nodeTileSize / 2;
				y2 = firstChildNodeDrawCoordinates.y;
				// add the 1st edge
				firstChildNode.parentEdgeDrawCoordinates = new Line2D.Float(x1, y1, x2, y2);
				parentNode.childNodesEdgesDrawCoordinates.add(firstChildNode.parentEdgeDrawCoordinates);
				x1 = xDrawCoordForParentWithFourChildren + nodeTileSize / 3;
				y1 = yCoordinatesOfParent2;
				x2 = secondChildNodeDrawCoordinates.x + nodeTileSize / 3;
				y2 = secondChildNodeDrawCoordinates.y;
				// add the 2nd edge
				secondChildNode.parentEdgeDrawCoordinates = new Line2D.Float(x1, y1, x2, y2);
				parentNode.childNodesEdgesDrawCoordinates.add(secondChildNode.parentEdgeDrawCoordinates);
				x1 = xDrawCoordForParentWithFourChildren + 2 * nodeTileSize / 3;
				y1 = yCoordinatesOfParent2;
				x2 = thirdChildNodeDrawCoordinates.x + 2 * nodeTileSize / 3;
				y2 = thirdChildNodeDrawCoordinates.y;
				// add the 3rd edge
				thirdChildNode.parentEdgeDrawCoordinates = new Line2D.Float(x1, y1, x2, y2);
				parentNode.childNodesEdgesDrawCoordinates.add(thirdChildNode.parentEdgeDrawCoordinates);
				x1 = xDrawCoordForParentWithFourChildren + nodeTileSize;
				y1 = yCoordinatesOfParent2;
				x2 = fourthChildNodeDrawCoordinates.x + nodeTileSize / 2;
				y2 = fourthChildNodeDrawCoordinates.y;
				// add the 4th edge
				fourthChildNode.parentEdgeDrawCoordinates = new Line2D.Float(x1, y1, x2, y2);
				parentNode.childNodesEdgesDrawCoordinates.add(fourthChildNode.parentEdgeDrawCoordinates);
				coordinates = new Point(xDrawCoordForParentWithFourChildren,
						secondChildNodeDrawCoordinates.y - yGapBetweenParentAndChildren);
				parentNode.drawCoordinates = coordinates;
				paintableNodes.add(parentNode);
				if (parentNode.getDepth() < height - 1) {
					checkIfSubtreeRelocationIsRequired(parentNode);
				}
				if (parentNode.getDepth() == height - 1) {
					currentMaxXDrawCoordinate = fourthChildNodeDrawCoordinates.x + nodeTileSize;
				} else {
					currentMaxXDrawCoordinate = coordinates.x + nodeTileSize;
				}
				// currentMaxXDrawCoordinate = coordinates.x + nodeTileSize;
				break;
			default:
				System.err.println(
						"Error: node has an invalid number of children! (" + parentNode.getChildren().size() + ")");
		}
	}

	private void checkIfSubtreeRelocationIsRequired(Node parentNode) {
		Point coordinates = parentNode.getDrawCoordinates();

		// System.out.println("2 - currentMaxXDrawCoordinate = " +
		// currentMaxXDrawCoordinate);
		// System.out.println("=> coordinates.x = " + coordinates.x);

		int difference = coordinates.x - (currentMaxXDrawCoordinate - xGapBetweenSubtrees);
		// System.out.println("=> difference = " + difference);
		if (difference < xGapBetweenSubtrees) {
			// System.out.println("coordinates.x - (currentMaxXDrawCoordinate -
			// xGapBetweenSubtrees) = " +
			// (coordinates.x - (currentMaxXDrawCoordinate - xGapBetweenSubtrees)));
			int distance;
			if (difference < 0) {
				distance = -difference + xGapBetweenSubtrees;
			} else {
				if (difference > xGapBetweenSubtrees) {
					distance = xGapBetweenSubtrees - (coordinates.x - currentMaxXDrawCoordinate);
				} else {
					distance = xGapBetweenSubtrees - difference;
				}
			}
			parentNode.dxRelocationDistance = distance;
			startNodesMarkedForRelocation.add(parentNode);

			// System.out.println("=> distance = " + distance);
			// System.out.println("=> nodeMarkedForRelocation = " + parentNode.getId());
		}
	}

	private void relocateSubtrees(List<Node> currentRootNodesOfSubtrees) {
		// System.out.println("RELOCATING SUBTREES\n");

		int dxDistance = currentRootNodesOfSubtrees.get(0).getDxRelocationDistance();
		// System.out.println("dxDistance = " + dxDistance);

		for (Node node : currentRootNodesOfSubtrees) {
			rightRelocation(node, dxDistance);
		}

		maxDrawWidth += dxDistance;
	}

	private void rightRelocation(Node node, int dx) {
		// System.out.println("node: " + node.getId() + ", node's depth:" +
		// node.getDepth());
		// System.out.println("parent Of Node = " + node.getParent().getCoordinates());
		// for (Node child : node.getChildren()) {
		// System.out.println("child = " + child.getId());
		// }
		Point coordinates = node.getDrawCoordinates();
		coordinates.translate(dx, 0);
		// reposition node tile
		node.drawCoordinates = coordinates;
		if (node.isLeaf()) {
			// System.out.println("Leaf node, returning...");
			return;
		}

		for (Line2D.Float edge : node.childNodesEdgesDrawCoordinates) {
			// reposition node's edges
			edge.x1 += dx;
			edge.x2 += dx;
		}

		List<Node> childNodes = node.getChildren();

		for (Node childNode : childNodes) {
			rightRelocation(childNode, dx);
		}
	}

	public Node getRootNode() {
		return rootNode;
	}

	public void setRootNode(Node rootNode) {
		this.rootNode = rootNode;
	}

	public int getHeight() {
		return height;
	}

	public int getNumberOfLeafNodes() {
		return numberOfLeafNodes;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public List<Node> getPaintableNodes() {
		return paintableNodes;
	}

	public int getMaxDrawWidth() {
		return maxDrawWidth;
	}

	public int getMaxDrawHeight() {
		return maxDrawHeight;
	}
}
