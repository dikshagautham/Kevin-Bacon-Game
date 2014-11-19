import net.datastructures.*;
import java.util.Map;
import java.util.HashMap;

/**
 * NamedAdjacencyMapGraph.java
 * Subclass of AdjacencyMapGraph that allows accessing vertices according
 * to their names.  Names are actually anything of the generic type V.
 * 
 * Note that vertices have to be added to the map when they are added to the
 * graph and removed from the map when they are removed from the graph. 
 * Doing this requires overriding insertVertex and removeVertex.
 * 
 * @author Tom Cormen
 * @author Scot Drysdale added override of removeVertex, throwing an exception
 * when adding a name already in the graph, and a testing program.  Made some
 * other smaller changes.
 *
 * @param <V> generic type for vertices
 * @param <E> generic type for edges
 */

public class NamedAdjacencyMapGraph<V,E> extends AdjacencyMapGraph<V,E> {
  // Maintain a correspondence from a vertex name to the Vertex object.
  Map<V,Vertex<V>> vertexMap;
  
  public NamedAdjacencyMapGraph(boolean directed) {
    super(directed);
    vertexMap = new HashMap<V,Vertex<V>>();
  }
  
  /** Overrides the inherited insertVertex method to also insert into the 
   *  vertexMap.  
   */
  public Vertex<V> insertVertex(V name) throws IllegalArgumentException {
  		if(!vertexMap.containsKey(name)) {
  			Vertex<V> newVertex = super.insertVertex(name);
  			vertexMap.put(name, newVertex);
  			return newVertex;
  		}
  		else
  			throw new IllegalArgumentException("Duplicate vertex name");
  }
  
  /** Override of removeVertex.
   * Removes a vertex and all its incident edges from the graph. 
   * Also removes the vertex name from the map of vertices.
   * @param the vertex to be removed
   */
  public void removeVertex(Vertex<V> v) throws IllegalArgumentException {
  		vertexMap.remove(v.getElement());
  		removeVertex(v);
  }
  
  /** Return the Vertex object corresponding to a name, or null if
   * no corresponding object. */
  public Vertex<V> getVertex(V name) {
    return vertexMap.get(name);
  }
  
  /** Return true if a Vertex with the name is in the graph. */
  public boolean vertexInGraph(V name) {
    return vertexMap.containsKey(name);
  }
  
  /** Insert an edge based on the names of its vertices. */
  public Edge<E> insertEdge(V uName, V vName, E element)
      throws IllegalArgumentException {
    return insertEdge(getVertex(uName), getVertex(vName), element);
  }
  
  /** Return the edge from uName to vName, or null if they are not adjacent. */
  public Edge<E> getEdge(V uName, V vName) throws IllegalArgumentException {
    return getEdge(getVertex(uName), getVertex(vName));
  }
  
  // A testing method
  public static void main(String [] args) {
  		boolean isDirected = false;
  		NamedAdjacencyMapGraph<String, String> baconGraph;
  		
  		do {
  			baconGraph = new NamedAdjacencyMapGraph<String, String>(isDirected);

  			System.out.println("\nisDirected = " + isDirected); 
  			
  			baconGraph.insertVertex("Kevin Bacon");
  			baconGraph.insertVertex("Laura Linney");
  			baconGraph.insertVertex("Tom Hanks");
  			baconGraph.insertVertex("Liam Neeson");
  			baconGraph.insertEdge("Laura Linney","Kevin Bacon", "Mystic River");
  			baconGraph.insertEdge("Liam Neeson", "Laura Linney", "Kinsey");
  			baconGraph.insertEdge( "Tom Hanks", "Kevin Bacon", "Apollo 13");

  			System.out.println("\nvertexInGraph for Laura Linney = " + 
  					baconGraph.vertexInGraph("Laura Linney"));

  			System.out.println("\nvertexInGraph for L. Linney = " + 
  					baconGraph.vertexInGraph("L. Linney"));

  			System.out.println("\ngetEdge between Laura Linney and Tom Hanks = " + 
  					baconGraph.getEdge("Laura Linney", "Tom Hanks"));
  			
  			System.out.println("\ngetEdge between Laura Linney and Kevin Bacon = " + 
  					baconGraph.getEdge("Laura Linney", "Kevin Bacon").getElement());
  			
  			Edge<String> e = baconGraph.getEdge("Kevin Bacon", "Laura Linney");
  			if(e == null)
  				System.out.println("\nNo edge between Kevin Bacon and Laura Linney");
  			else
  				System.out.println("\ngetEdge between between Kevin Bacon and Laura Linney = " + 
    					e.getElement()); 
  				

  			System.out.println("\nInDegree of Laura Linney = " + 
  					baconGraph.inDegree(baconGraph.getVertex("Laura Linney")));

  			System.out.println("\nOutDegree of Laura Linney = " +
  					baconGraph.outDegree(baconGraph.getVertex("Laura Linney")));

  			System.out.println("\nEdges into to Laura Linney:");
   		for(Edge<String> edge : baconGraph.incomingEdges(baconGraph.getVertex("Laura Linney"))) 
  				System.out.println(edge.getElement());

  			System.out.println("\nEdges out of to Laura Linney:");
  			for(Edge<String> edge : baconGraph.outgoingEdges(baconGraph.getVertex("Laura Linney"))) 
  				System.out.println(edge.getElement()); 

  			System.out.println("\nThe entire graph:");
  			for(Vertex<String> vertex : baconGraph.vertices()) {

  				System.out.println("\nEdges into " + vertex.getElement() + ":");
  				for(Edge<String> edge : baconGraph.incomingEdges(vertex)) 
  					System.out.println(edge.getElement()); 

  				System.out.println("\nEdges out of " + vertex.getElement() + ":");
  				for(Edge<String> edge : baconGraph.outgoingEdges(vertex)) 
  					System.out.println(edge.getElement());  				
  			}  
  			
  			baconGraph.removeVertex(baconGraph.getVertex("Kevin Bacon"));
  			
  			System.out.println("\nCalled removeVertex for Kevin Bacon");
  			System.out.println("getVertex for Kevin Bacon returns: " +
  				baconGraph.getVertex("Kevin Bacon"));
  			
  			System.out.println("\nThe entire graph after Kevin Bacon removed:");
  			for(Vertex<String> vertex : baconGraph.vertices()) {

  				System.out.println("\nEdges into " + vertex.getElement() + ":");
  				for(Edge<String> edge : baconGraph.incomingEdges(vertex)) 
  					System.out.println(edge.getElement()); 

  				System.out.println("\nEdges out of " + vertex.getElement() + ":");
  				for(Edge<String> edge : baconGraph.outgoingEdges(vertex)) 
  					System.out.println(edge.getElement());  				
  			}  
  			isDirected = !isDirected;
  		}	while(isDirected);
  		
  		try{
  			baconGraph.insertVertex("Laura Linney");
  		}
  		catch(IllegalArgumentException ex) {
  			System.out.println("\nCaught exception for duplicate vertex name: " +
  		     ex.getMessage());
  		}
  }
}
 