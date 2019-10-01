package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.plaf.metal.OceanTheme;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;

//import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.PrivateKeyResolver;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class Controller {

	private static final String MAP_FILE = "map.map";
	 Graph graph;
	 Node<City> start;
	 String end;
	@FXML
	private Slider slider;
	@FXML
	private ScrollPane map_scrollpane;
	@FXML
	private MenuButton map_pin1,map_pin2;
	@FXML
	private ImageView imageView;
	@FXML
	private Pane mypane;

	@FXML
	private Button establishB;

	@FXML
	private ComboBox<String> select1;

	@FXML
	private ComboBox<String> select2;

	@FXML
	private ComboBox<String> wayPointInput;
	
	@FXML
	private ComboBox<String> avoidPointInput;

	public List<Node<City>> path;
	public List<List<Node<City>>> allPaths;
 
	Group zoomGroup;


	@FXML
	public void initialize() {
		loadMap();
        List<String> allCityNames = graph.getAllCityNames();

        slider.setMin(0.5);
        slider.setMax(1.5);
        slider.setValue(1.0);
        slider.valueProperty().addListener((o, oldValue, newValue) -> zoom((Double) newValue));

        select1.getItems().addAll(allCityNames);
        select2.getItems().addAll(allCityNames);
        wayPointInput.getItems().addAll(allCityNames);
        avoidPointInput.getItems().addAll(allCityNames);

        // Wrap scroll content in a Group so ScrollPane re-computes scroll bars
        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(map_scrollpane.getContent());
        map_scrollpane.setContent(contentGroup);


        select1.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            if (newValue != null) {
                modelSelected1(newValue);
            }
        });

        select2.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            if (newValue != null) {
                modelSelected2(newValue);
            }
        });

        System.out.println(mypane.getChildren().size());

	}

	 private void modelSelected1(String newValue) {
	        City city = graph.findCityByName(newValue);
	        if (null != city) {
	            map_pin1.setLayoutX(city.getX() - 24);
	            map_pin1.setLayoutY(city.getY() - 60);
	        }
	    }
	    
	    private void modelSelected2(String newValue) {
	        City city = graph.findCityByName(newValue);
	        if (null != city) {
	            map_pin2.setLayoutX(city.getX() - 24);
	            map_pin2.setLayoutY(city.getY() - 60);
	        }
	    }

	@FXML
	public void zoomIn(ActionEvent event) {
		//	    System.out.println("airportapp.Controller.zoomIn");
		double sliderVal = slider.getValue();
		slider.setValue(sliderVal += 0.1);
	}

	@FXML
	public void zoomOut(ActionEvent event) {
		//	    System.out.println("airportapp.Controller.zoomOut");
		double sliderVal = slider.getValue();
		slider.setValue(sliderVal + -0.1);
	}


	private void zoom(double scaleValue) {
		//	    System.out.println("airportapp.Controller.zoom, scaleValue: " + scaleValue);
		double scrollH = map_scrollpane.getHvalue();
		double scrollV = map_scrollpane.getVvalue();
		zoomGroup.setScaleX(scaleValue);
		zoomGroup.setScaleY(scaleValue);
		map_scrollpane.setHvalue(scrollH);
		map_scrollpane.setVvalue(scrollV);
	}

	@FXML
	public void pinMove()
	{
		imageView.setOnMouseDragged(e -> {
			System.out.println("X:"+ e.getX()+ "Y:"+e.getY());
			// move the pin and set it's info
			map_pin1.setLayoutX(e.getX()-24);
			map_pin1.setLayoutY(e.getY()-60);

		}); 

	}

	
	
	public void loadMap() {
        try {
            graph = new Graph();
            graph.loadMap(MAP_FILE);

            City city;
            for (int z = 0; z < graph.cityList.size(); z++) {
                city = graph.cityList.get(z);
                Circle circle = new Circle(city.getX(), city.getY(), 5, Color.RED);
                mypane.getChildren().add(circle);
            }
        } catch (IOException e) {
            System.err.println("Load map error.");
            throw new RuntimeException(e);
        }
    }





	public void generatingSinglePathP() {
		if(mypane.getChildren().size()>15)
		{
			mypane.getChildren().remove(15,mypane.getChildren().size());
		}
		String from = select1.getValue();
        String to = select2.getValue();

        System.out.println("The  path from " + from + " to " + to);

		System.out.println("--------------------------------------------------------------");
		
		
		List<Node<City>> path=graph.findPathDepthFirst(from,null,to);
		for(Node<City> n : path) 
			System.out.println(n.node.getCityName());



		if(path != null) {
			for(int i=0;i<path.size()-1;i++) {
				drawP(path.get(i).node.x,path.get(i).node.y,path.get(i+1).node.x,path.get(i+1).node.y);

			}
		}
	}

	public  void generatingallpath() {
		if(mypane.getChildren().size()>15)
		{
			mypane.getChildren().remove(15,mypane.getChildren().size());
		}
		String from = select1.getValue();
        String to = select2.getValue();

        System.out.println("The multiple path from " + from + " to " + to);

        System.out.println("-------------------------------------");

		allPaths=graph.findAllPathsDepthFirst(from, null, to);
		int pCount=1;
		if(allPaths != null) {
		for(List<Node<City>> p : allPaths) {
			System.out.println("\nPath "+(pCount++)+"\n--------");
			for(Node<City> n : p)
				System.out.println(n.node.getCityName());
		}
		}
		for(List<Node<City>> pp : allPaths ) {

			if(pp != null) {

				for(int i=0;i<pp.size()-1;i++) {
					drawP(pp.get(i).node.x,pp.get(i).node.y,pp.get(i+1).node.x,pp.get(i+1).node.y);
				}

			}
		}
	}



	public void wayPointIncludeFunc() {
		if(mypane.getChildren().size()>15)
			mypane.getChildren().remove(15, mypane.getChildren().size());
		String waypointCity = "";
		waypointCity = wayPointInput.getSelectionModel().getSelectedItem();
		List<Node<City>> newPath = new ArrayList<>();
		newPath = null;
		boolean flag = false;
		System.out.println("\n\n--------------------------------------------------");

		System.out.println(waypointCity);
		System.out.println();
		System.out.println();

		for(int i= 0;i<allPaths.size()-1;i++) {
			flag =false;
			for(int k =0;k<allPaths.get(i).size()-1;k++) {
				if(allPaths.get(i).get(k).node.getCityName().equals(waypointCity)) {
					flag=true;
					break;
				}
			}
			if(flag) {
				newPath = allPaths.get(i);
				break;

			}
		}

		System.out.println();
		System.out.println();

		if(newPath==null)
			newPath = path;
		System.out.println(newPath.size());
		for(int i= 0;i<newPath.size()-1;i++)
			System.out.println(newPath.get(i).node.getCityName());


		if( newPath != null) {
			for(int i=0;i<newPath.size()-1;i++) {
				drawP(newPath.get(i).node.getX(),newPath.get(i).node.getY(),newPath.get(i+1).node.getX(),newPath.get(i+1).node.getY());
			}
		}

		System.out.println("\n\n------------------------------------------");
	}


	public void avoidPointFunc() {
		if(mypane.getChildren().size()>15)
			mypane.getChildren().remove(15, mypane.getChildren().size());
		String cityToAvoid = "";
		cityToAvoid = avoidPointInput.getSelectionModel().getSelectedItem();
		List<Node<City>> newPath = new ArrayList<>();
		newPath = null;
		boolean flag = true;
		System.out.println("\n\n----------------------------------------------------");

		System.out.println(cityToAvoid);
		System.out.println();

		for(int i= 0;i<allPaths.size()-1;i++) {
			flag =true;
			for(int k =0;k<allPaths.get(i).size()-1;k++) {
				if(allPaths.get(i).get(k).node.getCityName().equals(cityToAvoid)) {
					flag=false;
					break;
				}
			}
			if(flag) {
				newPath = allPaths.get(i);
				break;

			}
		}

		System.out.println();
		System.out.println();

		if(newPath==null)
			newPath = path;
		System.out.println(newPath.size());
		for(int i= 0;i<newPath.size()-1;i++)
			System.out.println(newPath.get(i).node.getCityName());


		if( newPath != null) {
			for(int i=0;i<newPath.size()-1;i++) {
				drawP(newPath.get(i).node.getX(),newPath.get(i).node.getY(),newPath.get(i+1).node.getX(),newPath.get(i+1).node.getY());
			}
		}

		System.out.println("\n\n--------------------------------------------");
	}

	
	
	

	public void shortestPath()
	{
		if(mypane.getChildren().size()>15)
		{
			mypane.getChildren().remove(15,mypane.getChildren().size());
		}
		String from = select1.getValue();
        String to = select2.getValue();

        System.out.println("The cheapest path from " + from + " to " + to);
        System.out.println("using Dijkstra's algorithm:");
        System.out.println("-------------------------------------");

        DistancePath cpa = graph.findBestPathDijkstr(from, to, PathType.SHORTEST_ROUTE);

        for (Node<City> n : cpa.pathList) {
            System.out.println(n.node.getCityName());
        }
        
    	if(cpa.pathList!= null) {

			for(int i=0;i<cpa.pathList.size()-1;i++) {
				drawP(cpa.pathList.get(i).node.x,cpa.pathList.get(i).node.y,cpa.pathList.get(i+1).node.x,cpa.pathList.get(i+1).node.y);
			}
		}
        System.out.println("\nThe total path Distance is: " + cpa.pathDistance);

	}


	public void safestPath()
	{
		if(mypane.getChildren().size()>15)
		{
			mypane.getChildren().remove(15,mypane.getChildren().size());
		}
		String from = select1.getValue();
        String to = select2.getValue();

        System.out.println("The safest path from " + from + " to " + to);
        System.out.println("using Dijkstra's algorithm:");
        System.out.println("-------------------------------------");

        DistancePath cpa = graph.findBestPathDijkstr(from, to, PathType.SAFEST_ROUTE);

        for (Node<City> n : cpa.pathList) {
            System.out.println(n.node.getCityName());
        }
        
    	if(cpa.pathList!= null) {

			for(int i=0;i<cpa.pathList.size()-1;i++) {
				drawP(cpa.pathList.get(i).node.x,cpa.pathList.get(i).node.y,cpa.pathList.get(i+1).node.x,cpa.pathList.get(i+1).node.y);
			}
		}
        System.out.println("\nThe total path safety is: " + cpa.pathSafety);

	}



	public void easiestPath()
	{
		if(mypane.getChildren().size()>15)
		{
			mypane.getChildren().remove(15,mypane.getChildren().size());
		}
		String from = select1.getValue();
        String to = select2.getValue();

        System.out.println("The easiest path from " + from + " to " + to);
        System.out.println("using Dijkstra's algorithm:");
        System.out.println("-------------------------------------");

        DistancePath cpa = graph.findBestPathDijkstr(from, to, PathType.EASIEST_ROUTE);

        for (Node<City> n : cpa.pathList) {
            System.out.println(n.node.getCityName());
        }
        
    	if(cpa.pathList!= null) {

			for(int i=0;i<cpa.pathList.size()-1;i++) {
				drawP(cpa.pathList.get(i).node.x,cpa.pathList.get(i).node.y,cpa.pathList.get(i+1).node.x,cpa.pathList.get(i+1).node.y);
			}
		}
        System.out.println("\nThe total path easy is: " + cpa.pathEasy);

	}





	public void drawP(double fromX, double fromY, double toX, double toY) {
		Line line = new Line(fromX,fromY,toX,toY);
		line.setStroke(Color.DARKRED);
		line.setStrokeWidth(3);
		line.getStrokeDashArray().setAll(25d, 20d, 5d, 20d);
		double maxOffset = line.getStrokeDashArray().stream().reduce(0d, (a,b) -> a+b);
		Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(line.strokeDashOffsetProperty(), 0, Interpolator.LINEAR)),
					new KeyFrame(Duration.seconds(2), new KeyValue(line.strokeDashOffsetProperty(), maxOffset, Interpolator.LINEAR)));
		timeline.setCycleCount(timeline.INDEFINITE);
		timeline.setRate(-1);
		timeline.play();
		mypane.getChildren().add(line);
	}


	public static void traverseGraphDepthFirst(Node<City> from, List<Node<City>> encountered ){
		System.out.println(from.node.cityName);
		if(encountered==null) encountered=new ArrayList<>(); //First node so create new (empty) encountered list
		encountered.add(from);
		for(Node<City> adjNode : encountered)
			if(!encountered.contains(adjNode)) traverseGraphDepthFirst(adjNode, encountered );
	}



}
