package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Graph {

    List<Node<City>> nodeList;
    List<City> cityList;

    public Graph() {
        this.cityList = new ArrayList<>();
        this.nodeList = new ArrayList<>();
    }

    public void loadMap(String mapFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(mapFile));

        //ignore the invalid lines
        String line = reader.readLine();
        while (shouldIgnore(line)) {
            line = reader.readLine();
        }

        //load cities
        City city;
        int numCities = Integer.parseInt(line);
        for (int i = 0; i < numCities; ) {
            line = reader.readLine();
            if (shouldIgnore(line)) {
                continue;
            }
            city = parseAsCity(line);
            if (null != city) {
                cityList.add(city);
                nodeList.add(new Node<>(city));
            }
            ++i;
        }

        line = reader.readLine();
        while (shouldIgnore(line)) {
            line = reader.readLine();
        }

        //load edges
        int realNumEdges = 0;
        Map<String, Node<City>> cityNodeMap = getCityNodeMap();
        int numEdges = Integer.parseInt(line);
        for (int i = 0; i < numEdges; ) {
            line = reader.readLine();
            if (shouldIgnore(line)) {
                continue;
            }
            boolean validEdge = createUndirectedEdge(line, cityNodeMap);
            if (validEdge) {
                ++realNumEdges;
            }
            ++i;
        }

        reader.close();
        System.out.println("Load map finished.");
        System.out.println("Number of cities:" + cityList.size());
        System.out.println("Number of edges:" + realNumEdges);
    }


    public DistancePath findBestPathDijkstr(String startCity, String lookingfor, PathType pathType) {
        Map<String, Node<City>> cityNodeMap = getCityNodeMap();
        Node<City> startNode = cityNodeMap.get(startCity);
        DistancePath dp = new DistancePath();
        List<Node<City>> encountered = new ArrayList<>(), unencountered = new ArrayList<>();
        startNode.nodeValue = 0;
        unencountered.add(startNode);
        Node<City> currentNode;

        do {
            currentNode = unencountered.remove(0);
            encountered.add(currentNode);

            if (currentNode.node.equals(lookingfor)) {
                dp.pathList.add(currentNode);
                dp.pathDistance = currentNode.nodeValue;
                dp.pathEasy = currentNode.nodeValue;
                dp.pathSafety = currentNode.nodeValue;

                while (currentNode != startNode) {
                    boolean foundPrevPathNode = false;
                    for (Node<City> n : encountered) {
                        for (Edge e : n.adjList) {
                            int nodeValue = e.getNodeValue(pathType);
                            if (e.destNode == currentNode && currentNode.nodeValue - nodeValue == n.nodeValue) {
                                dp.pathList.add(0, n);
                                currentNode = n;
                                foundPrevPathNode = true;
                                break;
                            }
                        }
                        if (foundPrevPathNode) {
                            break;
                        }
                    }
                }

                for (Node<City> n : encountered) {
                    n.nodeValue = Integer.MAX_VALUE;
                }
                for (Node<City> n : unencountered) {
                    n.nodeValue = Integer.MAX_VALUE;
                }
                return dp;
            }

            for (Edge e : currentNode.adjList) {
                if (!encountered.contains(e.destNode)) {
                    int nodeValue = e.getNodeValue(pathType);
                    e.destNode.nodeValue = Integer.min(e.destNode.nodeValue, currentNode.nodeValue + nodeValue);
                    unencountered.add(e.destNode);
                }
            }
            unencountered.sort(Comparator.comparingInt(n -> n.nodeValue));

        } while (!unencountered.isEmpty());
        return null;

    }

    public List<List<Node<City>>> findAllPathsDepthFirst(String from, List<Node<?>> encountered, String lookingfor) {
    	 Map<String, Node<City>> cityNodeMap = getCityNodeMap();
         Node<City> from1 = cityNodeMap.get(from);
    	
    	List<List<Node<City>>> result = null, temp2;
        if (from1.node.equals(lookingfor)) { //Found it
            List<Node<City>> temp = new ArrayList<>(); //Create new single solution path list
            temp.add(from1); //Add current node to the new single path list
            result = new ArrayList<>(); //Create new "list of lists" to store path permutations
            result.add(temp); //Add the new single path list to the path permutations list
            return result; //Return the path permutations list
        }
        if (encountered == null) encountered = new ArrayList<>(); //First node so create new (empty) encountered list
        encountered.add(from1); //Add current node to encountered list
        for (Edge ed : from1.adjList) {
            if (!encountered.contains(ed.destNode)) {
                temp2 = findAllPathsDepthFirst(ed.destNode.node.getCityName(), new ArrayList<>(encountered), lookingfor); //Use clone of encountered list
                //for recursive call!
                if (temp2 != null) { //Result of the recursive call contains one or more paths to the solution node
                    for (List<Node<City>> x : temp2) //For each partial path list returned
                        x.add(0, from1); //Add the current node to the front of each path list
                    if (result == null)
                        result = temp2; //If this is the first set of solution paths found use it as the result
                    else result.addAll(temp2); //Otherwise append them to the previously found paths
                }
            }
        }
        return result;
    }
    
    
    
    public List<Node<City>> findPathDepthFirst(String from, List<Node<City>> encountered, String lookingfor){
    	  Map<String, Node<City>> cityNodeMap = getCityNodeMap();
          Node<City> from1 = cityNodeMap.get(from);
    	
    	List<Node<City>> result;
		if(from1.node.equals(lookingfor)) { //Found it
			result=new ArrayList<>(); //Create new list to store the path info (any List implementation could be used)
			result.add(from1); //Add the current node as the only/last entry in the path list
			return result; //Return the path list
		}
		if(encountered==null) encountered=new ArrayList<>(); //First node so create new (empty) encountered list
		encountered.add(from1);
		for(Node<City> adjNode : from1.nodeListNode)
			if(!encountered.contains(adjNode)) {
				result=findPathDepthFirst(adjNode.node.getCityName(),encountered,lookingfor);
				if(result!=null) { //Result of the last recursive call contains a path to the solution node
					result.add(0,from1); //Add the current node to the front of the path list
					return result; //Return the path list
				}
			}
		return null;
	}
    
    
    
    

    public City findCityByName(String cityName) {
        for (City city : cityList) {
            if (city.cityName.equals(cityName)) {
                return city;
            }
        }

        return null;
    }

    public List<String> getAllCityNames() {
        return cityList.stream().map(City::getCityName).collect(Collectors.toList());
    }


    private Map<String, Node<City>> getCityNodeMap() {
        return nodeList.stream().collect(Collectors.toMap(node -> node.node.cityName, Function.identity()));
    }

    private City parseAsCity(String cityInfo) {
        String[] split = cityInfo.split(",");
        if (3 != split.length) {
            System.err.println("invalid city info:" + cityInfo);
            return null;
        }
        String name = split[0];
        Double x = parseAsDouble(split[1]);
        Double y = parseAsDouble(split[2]);
        if (null == x || null == y) {
            System.err.println("invalid city info:" + cityInfo);
            return null;
        }

        return new City(name, x, y);
    }

    /**
     * ignore the blank lines or lines start with "#"
     *
     * @param line the given str
     * @return
     */
    private boolean shouldIgnore(String line) {
        if (null == line || line.trim().length() == 0) {
            return true;
        }
        return line.startsWith("#");
    }

    private boolean createUndirectedEdge(String edgeInfo, Map<String, Node<City>> cityNodeMap) {
        String[] split = edgeInfo.split(",");
        if (5 != split.length) {
            System.err.println("invalid edge info:" + edgeInfo);
            return false;
        }

        Node<City> node1 = cityNodeMap.get(split[0]);
        Node<City> node2 = cityNodeMap.get(split[1]);
        Integer distance = parseAsInt(split[2]);
        Integer safety = parseAsInt(split[3]);
        Integer easy = parseAsInt(split[4]);
        if (null == node1) {
            System.err.println("Invalid city:" + split[0]);
            return false;
        }
        if (null == node2) {
            System.err.println("Invalid city:" + split[1]);
            return false;
        }
        if (null == distance) {
            System.err.println("Invalid distance value:" + split[2]);
            return false;
        }
        if (null == safety) {
            System.err.println("Invalid safety value:" + split[3]);
            return false;
        }
        if (null == easy) {
            System.err.println("Invalid easy value:" + split[4]);
            return false;
        }
        node1.connectToNodeUndirected(node2, distance, safety, easy);
        return true;
    }

    private Integer parseAsInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseAsDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (Exception e) {
            return null;
        }
    }
}
