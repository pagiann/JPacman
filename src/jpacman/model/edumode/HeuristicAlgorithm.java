package jpacman.model.edumode;

import java.awt.Point;
import java.text.DecimalFormat;

import jpacman.util.Utility;

public abstract class HeuristicAlgorithm extends SearchAlgorithm
{
    public static final int MANHATTAN_DISTANCE = 0;
    public static final int CHEBYSHEV_DISTANCE = 1;
    public static final int OCTILE_DISTANCE = 2;
    public static final int EUCLIDEAN_DISTANCE = 3;

    public static final String MANHATTAN_HEURISTIC_STRING = "Manhattan heuristic";
    public static final String CHEBYSHEV_HEURISTIC_STRING = "Chebyshev heuristic";
    public static final String OCTILE_HEURISTIC_STRING = "Octile heuristic";
    public static final String EUCLIDEAN_HEURISTIC_STRING = "Euclidean heuristic";

    protected int heuristicFunction;
    protected String heuristicFunctionAsString;
    protected DecimalFormat decimalFormatForHeuristic;

    protected HeuristicAlgorithm()
    {
	super();
	heuristicFunction = EUCLIDEAN_DISTANCE;
	heuristicFunctionAsString = EUCLIDEAN_HEURISTIC_STRING;
	decimalFormatForHeuristic = new DecimalFormat("0.000");
    }

    protected double computeHeuristicCost(Point point1, Point point2)
    {
	switch (heuristicFunction) {
	    case MANHATTAN_DISTANCE:
		heuristicFunctionAsString = MANHATTAN_HEURISTIC_STRING;
		return Utility.computeManhattanDistance(point1, point2);
	    case CHEBYSHEV_DISTANCE:
		heuristicFunctionAsString = CHEBYSHEV_HEURISTIC_STRING;
		return Utility.computeChebyshevDistance(point1, point2);
	    case OCTILE_DISTANCE:
		heuristicFunctionAsString = OCTILE_HEURISTIC_STRING;
		return Utility.computeOctileDistance(point1, point2);
	    case EUCLIDEAN_DISTANCE:
		heuristicFunctionAsString = EUCLIDEAN_HEURISTIC_STRING;
		return Utility.computeEuclideanDistance(point1, point2);
	    default:
		return 0.0;
	}
    }

    public int getHeuristicFunction()
    {
	return heuristicFunction;
    }

    public void setHeuristicFunction(int heuristicFunction)
    {
	this.heuristicFunction = heuristicFunction;
    }

    public String getHeuristicFunctionAsString()
    {
	return heuristicFunctionAsString;
    }

    public void setHeuristicFunctionAsString(String heuristicFunctionAsString)
    {
	this.heuristicFunctionAsString = heuristicFunctionAsString;
    }
}
