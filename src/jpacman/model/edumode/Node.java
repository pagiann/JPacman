package jpacman.model.edumode;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jpacman.model.Maze;

public class Node
{
    public static final Color CURRENT_NODE_COLOR = new Color(0x6666FF);
    public static final Color NEIGHBOR_NODE_COLOR = new Color(0x99CCFF);
    public static final Color CURRENT_NEIGHBOR_NODE_COLOR = new Color(0xCC99FF);
    public static final Color UNEXPANDED_NODE_COLOR = new Color(0xFFBC44);

    public static volatile boolean defaultBackgroundColorShading = true;

    Point coordinates; // the coordinates value of the node
    Node parent; // the parent node of this node
    List<Node> children; // the child nodes of this node
    int depth; // the depth of the node
    boolean leaf; // true only if the node is a leaf

    double gCost;
    double hCost;
    double fCost;

    // variables that are used in the graphical search tree
    String id;
    Point drawCoordinates;
    Line2D.Float parentEdgeDrawCoordinates;
    List<Line2D.Float> childNodesEdgesDrawCoordinates = new ArrayList<Line2D.Float>();
    Color backgroundColor;
    int dxRelocationDistance;

    public Node()
    {
	coordinates = new Point();
	parent = null;
	children = null;
	depth = 0;
	leaf = false;
	gCost = hCost = fCost = 0.0;
	id = "";
	drawCoordinates = null;
	parentEdgeDrawCoordinates = null;
	childNodesEdgesDrawCoordinates = null;
	backgroundColor = Color.WHITE;
	dxRelocationDistance = 0;
    }

    public Node(Point coordinates, String id)
    {
	this.coordinates = coordinates;
	this.id = id;
    }

    public Node(Point coordinates, Node parent, int depth)
    {
	this.coordinates = coordinates;
	this.parent = parent;
	children = new ArrayList<Node>();
	this.depth = depth;
	leaf = false;
	id = "";
	backgroundColor = Color.WHITE;
    }

    public Node(Point coordinates, Node parent, int depth, double gCost)
    {
	this(coordinates, parent, depth);
	this.gCost = gCost;
    }

    public Node(Point coordinates, Node parent, int depth, double gCost, double hCost)
    {
	this(coordinates, parent, depth);
	this.gCost = gCost;
	this.hCost = hCost;
	this.fCost = this.gCost + this.hCost;
    }

    public static Comparator<Node> hCostSorter = new Comparator<Node>() {
	@Override
	public int compare(Node n0, Node n1)
	{
	    if (n0.hCost < n1.hCost) {
		return -1;
	    } else if (n0.hCost == n1.hCost) {
		return 0;
	    } else { // n0.hCost > n1.hCost
		return 1;
	    }
	}
    };

    public static Comparator<Node> fCostSorter = new Comparator<Node>() {
	@Override
	public int compare(Node n0, Node n1)
	{
	    if (n0.fCost < n1.fCost) {
		return -1;
	    } else if (n0.fCost == n1.fCost) {
		return 0;
	    } else { // n0.fCost > n1.fCost
		return 1;
	    }
	}
    };

    @Override
    public int hashCode()
    {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((coordinates == null) ? 0 : coordinates.hashCode());
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object object)
    {
	if (this == object) {
	    return true;
	}

	if (object instanceof Node) {
	    Node node = (Node) object;
	    if (coordinates != null && coordinates.equals(node.coordinates)) {
		return true;
	    }
	}

	return false;
    }

    @Override
    public String toString()
    {
	return "Node [coordinates=" + coordinates + ", parent=" + parent + ", children=" + children + ", depth=" + depth + ", leaf=" + leaf + ", gCost=" + gCost + ", hCost=" + hCost + ", fCost=" + fCost + ", id=" + id + ", drawCoordinates=" + drawCoordinates + ", edgesDrawCoordinates=" + childNodesEdgesDrawCoordinates + ", dxRelocationDistance=" + dxRelocationDistance + "]";
    }

    public Point getCoordinates()
    {
	return coordinates;
    }

    public String getMazeGridLocation()
    {
	return new String("(" + ((coordinates.x / Maze.TILE) + 1) + "," + ((coordinates.y / Maze.TILE) + 1) + ")");
    }

    public void setCoordinates(Point coordinates)
    {
	this.coordinates = coordinates;
    }

    public Node getParent()
    {
	return parent;
    }

    public void setParent(Node parent)
    {
	this.parent = parent;
    }

    public List<Node> getChildren()
    {
	return children;
    }

    public void setChildren(List<Node> children)
    {
	this.children = children;
    }

    public int getDepth()
    {
	return depth;
    }

    public void setDepth(int depth)
    {
	this.depth = depth;
    }

    public boolean isLeaf()
    {
	return leaf;
    }

    public void setLeaf(boolean leaf)
    {
	this.leaf = leaf;
    }

    public String getID()
    {
	return id;
    }

    public void setID(String id)
    {
	this.id = id;
    }

    public double getFCost()
    {
	return fCost;
    }

    public void setFCost(double fCost)
    {
	this.fCost = fCost;
    }

    public double getGCost()
    {
	return gCost;
    }

    public void setGCost(double gCost)
    {
	this.gCost = gCost;
    }

    public double getHCost()
    {
	return hCost;
    }

    public void setHCost(double hCost)
    {
	this.hCost = hCost;
    }

    public Point getDrawCoordinates()
    {
	return drawCoordinates;
    }

    public Line2D.Float getParentEdgeDrawCoordinates()
    {
	return parentEdgeDrawCoordinates;
    }

    public List<Line2D.Float> getChildNodesEdgesDrawCoordinates()
    {
	return childNodesEdgesDrawCoordinates;
    }

    public Color getBackgroundColor()
    {
	return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor)
    {
	this.backgroundColor = backgroundColor;
    }

    public int getDxRelocationDistance()
    {
	return dxRelocationDistance;
    }
}
