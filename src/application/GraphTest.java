package application;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GraphTest {
	private static final String MAP_FILE = "test.map";

    private static Graph graph;
	

	@Before
	public void setUp() throws Exception {
		 if (null == graph) {
	            graph = new Graph();
	            graph.loadMap(MAP_FILE);
	        }
	}

	@Test
    public void loadMap() {
       assertEquals(graph.cityList.size(), 6);
       assertEquals(graph.nodeList.size(), 6);

        City cityA = graph.cityList.get(0);
        City cityB = graph.cityList.get(1);
        City cityC = graph.cityList.get(2);
        City cityD = graph.cityList.get(3);
        City cityE = graph.cityList.get(4);
        City cityF = graph.cityList.get(5);

       assertEquals(cityA.cityName, "A");
       assertEquals(cityB.cityName, "B");
       assertEquals(cityC.cityName, "C");
       assertEquals(cityD.cityName, "D");
       assertEquals(cityE.cityName, "E");
       assertEquals(cityF.cityName, "F");

        List<Edge> adjListOfA = graph.nodeList.get(0).adjList;
       assertEquals(adjListOfA.size(), 2);
       assertEquals(adjListOfA.get(0).destNode.node.cityName, cityB.cityName);
       assertEquals(adjListOfA.get(0).distance, 6);
       assertEquals(adjListOfA.get(1).destNode.node.cityName, cityC.cityName);
       assertEquals(adjListOfA.get(1).distance, 3);
    }
	
	 @Test
	    public void findShortestPathDijkstr() {
	        City cityA = graph.cityList.get(0);
	        City cityC = graph.cityList.get(2);
	        City cityD = graph.cityList.get(3);
	        City cityF = graph.cityList.get(5);

	        DistancePath bestPath = graph.findBestPathDijkstr(cityA.cityName, cityF.cityName, PathType.SHORTEST_ROUTE);
	        assertNotNull(bestPath);
	        assertEquals(bestPath.pathDistance, 9);

	        List<Node<City>> pathList = bestPath.pathList;
	        assertEquals(pathList.size(), 4);

	        assertEquals(pathList.get(0).node, cityA);
	        assertEquals(pathList.get(1).node, cityC);
	        assertEquals(pathList.get(2).node, cityD);
	        assertEquals(pathList.get(3).node, cityF);
	    }
	 
	 @Test
	    public void findEsiestPathDijkstr() {
		    City cityA = graph.cityList.get(0);
	        City cityC = graph.cityList.get(2);
	        City cityD = graph.cityList.get(3);
	        City cityF = graph.cityList.get(5);

	        DistancePath eestPath = graph.findBestPathDijkstr(cityA.cityName, cityF.cityName, PathType.EASIEST_ROUTE);
	        assertNotNull(eestPath);
	        assertEquals(eestPath.pathEasy, 9);

	        List<Node<City>> pathList = eestPath.pathList;
	        assertEquals(pathList.size(), 4);

	        assertEquals(pathList.get(0).node, cityA);
	        assertEquals(pathList.get(1).node, cityC);
	        assertEquals(pathList.get(2).node, cityD);
	        assertEquals(pathList.get(3).node, cityF);
	    }
	 
	 @Test
	    public void findSafestPathDijkstr() {
	        City cityA = graph.cityList.get(0);
	        City cityC = graph.cityList.get(2);
	        City cityD = graph.cityList.get(3);
	        City cityF = graph.cityList.get(5);

	        DistancePath sestPath = graph.findBestPathDijkstr(cityA.cityName, cityF.cityName, PathType.SAFEST_ROUTE);
	        assertNotNull(sestPath);
	        assertEquals(sestPath.pathSafety, 9);

	        List<Node<City>> pathList = sestPath.pathList;
	        assertEquals(pathList.size(), 4);

	        assertEquals(pathList.get(0).node, cityA);
	        assertEquals(pathList.get(1).node, cityC);
	        assertEquals(pathList.get(2).node, cityD);
	        assertEquals(pathList.get(3).node, cityF);
	    }
	 
}
