

public class Edge implements Comparable<Edge> { 

    private final int u;
    private final int v;
    private final double weight;

    public Edge(int u, int v, double weight) {
    	if( u < 0 || v < 0 || Double.isNaN(weight)) throw new IllegalArgumentException("Edge():Illegal argument");
        this.u = u;
        this.v = v;
        this.weight = weight;
    }


    public double getWeight() {
        return weight;
    }


    public int getOneNode() {
        return u;
    }


    public int getOtherNode(int vertex) {
        if      (vertex == u) return v;
        else if (vertex == v) return u;
        else throw new IllegalArgumentException("Edge: Illegal argument");
    }


    public int compareTo(Edge that) {
        if      (this.getWeight() < that.getWeight()) return +1; 
        else if (this.getWeight() > that.getWeight()) return -1; 
        else                                    return  0;
    }

 
}
