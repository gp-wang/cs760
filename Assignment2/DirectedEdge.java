

public class DirectedEdge { 
    private final int u;
    private final int v;
    private final double weight;

    public DirectedEdge(int u, int v, double weight) {
    	if( u < 0 || v < 0 || Double.isNaN(weight)) throw new IllegalArgumentException("Edge():Illegal argument");
        this.u = u;
        this.v = v;
        this.weight = weight;
    }


    public int getHead() {
        return u;
    }


    public int getTail() {
        return v;
    }


    public double getWeight() {
        return weight;
    }


}