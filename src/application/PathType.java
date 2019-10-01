package application;

public enum PathType {

    SHORTEST_ROUTE("shortest route"),
    EASIEST_ROUTE("easiest route"),
    SAFEST_ROUTE("safest route");


    String desc;

    PathType(String desc) {
        this.desc = desc;
    }
}
