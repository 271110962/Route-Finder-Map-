package application;

public class Edge {

	public Node<City> destNode;
	public int distance;
	public int safety;
	public int easy;
	City city;
	
	public Edge(Node<City>destNode, int distance,int safety,int easy)
	{
		this.destNode=destNode;
		this.distance=distance;
		this.safety=safety;
		this.easy=easy;
	}
	
    public int getNodeValue(PathType pathType) {
        switch (pathType) {
            case SHORTEST_ROUTE:
                return this.distance;
            case SAFEST_ROUTE:
                return this.safety;
            case EASIEST_ROUTE:
                return this.easy;
            default:
                return 0;
        }
    }
}
