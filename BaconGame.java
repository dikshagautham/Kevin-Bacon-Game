import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import javax.swing.JFileChooser;
import net.datastructures.Edge;
import net.datastructures.Vertex;

/** Bacon Game file
 * 
 * @author Diksha Gautham
 *
 * Plays the "Kevin Bacon" game with a list of actors, a list of movies, and a list of movies and actors
 * 
 */

public class BaconGame<V, E> {

	private NamedAdjacencyMapGraph<V,E> baconGraph; //the full, undirected bacon graph
	private NamedAdjacencyMapGraph<V,E> BFSsearchTree; // the directed BFS graph
	private HashMap<V, Integer> distanceMap; // keys: actor names. value: bacon number
	
	/**
	 * Constructor to play the bacon game 
	 * Makes an undirected bacon graph
	 * Then makes a BFS tree
	 * @param root, the root of the BFS tree
	 * 
	 */
	public BaconGame(NamedAdjacencyMapGraph<V, E> myGraph, V root) throws IOException {
		baconGraph = myGraph;		
		makeBFSTree(root);
	}
	
	
	
	/**
	 * Make a full, undirected Bacon Graph
	 * @returns an undirected NamedAdjacencyMapGraph where 	vertices are actor names, edges are movie names  
	 * 
	 */
	public static NamedAdjacencyMapGraph<String,String> makeBaconGraph() throws IOException {
		NamedAdjacencyMapGraph<String, String> baconGraph = new NamedAdjacencyMapGraph<String,String>(false);
		String filepath_1 = "InputFiles/actors.txt"; 
		String filepath_2 = "InputFiles/movies.txt"; 
		String filepath_3 = "InputFiles/movie-actors.txt"; 

		HashMap<String, String> actorIDNameMap = makeStringToStringMap(filepath_1); 		// map the actor IDs to actor names
		HashMap<String, String> movieIDMap = makeStringToStringMap(filepath_2);		// map the movie IDs to movie names 
		HashMap<String, ArrayList<String>> movieActorMap = makeStringToListMap(filepath_3); // Make a map mapping the movie IDs to actor names

		
		// Make a vertex for each actor in the actor list, and insert into the bacon graph:
    for (String actorName : actorIDNameMap.values())
    	baconGraph.insertVertex(actorName);

		// For each movie 
    for (String movieID : movieActorMap.keySet()) {
		// get the movie's name     	
      String movieName = movieIDMap.get(movieID);

		// Go through all the actors in its actor list
    	ArrayList<String> myActors = movieActorMap.get(movieID);
    	
    	// Add an edge between that actor and all the actors following it
      for (int i = 0; i < myActors.size()-1; i++) {
        for (int j = i+1; j < myActors.size(); j++) {
          String actor1Name = actorIDNameMap.get(myActors.get(i));
          String actor2Name = actorIDNameMap.get(myActors.get(j)); 
          
          // If there's already an edge between the two actors, catch the exception and ignore it 
          try {
            baconGraph.insertEdge(actor1Name, actor2Name, movieName);
          }
          catch (IllegalArgumentException e) {
            // Don't do anything here
          }
        }
      }     
    }
		return baconGraph;
	}
	
	
	
	/** Constructs a BFS search tree from the bacon graph
	 * Stores the distances from each vertex to the root in the distanceMap 
	 * @param root 
	 * @return the BFS tree 
	 */
	
	public NamedAdjacencyMapGraph<V, E> makeBFSTree(V root) {
		
		distanceMap = new HashMap<V, Integer>();
		// initialize our BFS search tree 
		BFSsearchTree = new NamedAdjacencyMapGraph<V, E>(true);
		// Add the root vertex to the tree 
		BFSsearchTree.insertVertex(root);
		
		// distance of the root vertex is zero 
		distanceMap.put(root, 0);
		
		// initialize a queue to hold the vertices already in the graph
		Queue<Vertex<V>> visited = new LinkedList<Vertex<V>>();
		
		// initialize queue to contain only the start vertex, the root
		visited.add(baconGraph.getVertex(root));
		
		// while the queue is not empty: 
		while (!visited.isEmpty()) {
			Vertex<V> actorA = visited.remove();  // remove the top element from the queue (actorA) 
			V Aname = actorA.getElement(); // store its name 
			
			// for each actor in a same movie as actorA
			for (Edge<E> movie: baconGraph.outgoingEdges(actorA)) {
				Vertex<V> actorB = baconGraph.opposite(actorA, movie); // get the actor at the end of that edge
				V Bname = actorB.getElement();
				
				// if that actor is NOT already in our tree, add it and add the edge from A to B  
				if (!BFSsearchTree.vertexInGraph(Bname)) {
					BFSsearchTree.insertVertex(Bname);
					BFSsearchTree.insertEdge(Bname, Aname, movie.getElement()); // A's back pointer is B
					
					// enqueue B into our queue 
					visited.add(actorB);
					
					// B's distance is A's distance + 1. Add it to our distance map
					int AsDistance = distanceMap.get(Aname);
					distanceMap.put(Bname, AsDistance+1);
				}
									
			}
		}
		return BFSsearchTree;

	}
	
	/** Constructs a path from the given actor to the root vertex 
	 * Returns an ArrayList of edges from the actor to the root 
	 * @param actor vertex to start from
	 * @return root
	 */
	
	public ArrayList<Edge<E>> createPath(V actor) {
		
		// if the actor is in the graph, get its path to the root:  
		if (BFSsearchTree.vertexInGraph(actor)) {
			ArrayList<Edge<E>> path = new ArrayList<Edge<E>>();
	
			// Get the vertex object containing the actor's name 
			Vertex<V> startVertex = BFSsearchTree.getVertex(actor);
			// Create an iterator over the outgoing edges from the starting actor 
			Iterator<Edge<E>> iterator = BFSsearchTree.outgoingEdges(startVertex).iterator();
			
			// While the iterator has a next, we're not at the root yer: 
			while (iterator.hasNext()) {
				// Add that next edge to the path 
				Edge<E> nextEdge = iterator.next();
				path.add(nextEdge);
				
				// Get the new start vertex, the vertex after this edge,  
				startVertex = BFSsearchTree.opposite(startVertex, nextEdge);			
				
				// Set our iterator to iterate on that vertex  
				iterator = BFSsearchTree.outgoingEdges(startVertex).iterator();				
			}
			return path;
			
		}

		// If the actor wasn't in the graph return null and print an error message
		System.out.println("That actor wasn't in our database");
		return null;
		
	}
		
		
	/** Helper method to read our input text files and turn them into string to string maps 
	 * Keys: a string of everything before the | sign. Values: a string of everything after the | 
	 * @returns a map with key/value pairs described above 
	 * @throws IOException
	 */
	
	public static HashMap<String, String> makeStringToStringMap(String filePath) throws IOException {
		 HashMap<String, String> targetMap = new HashMap<String, String>();

		// make a buffered reader of our file at the file path that was passed in
		 BufferedReader inputFile = new BufferedReader(new FileReader(filePath));   		 

		 // Read all the lines in the file 
			String line = null;
			while ((line = inputFile.readLine()) != null) {

				int pipeLineIndex = line.indexOf("|");
				String key = line.substring(0,pipeLineIndex); // get whatever is to the right of the pipeline (key)
				String value = line.substring(pipeLineIndex + 1, line.length()); //get whatever is to the left of the pipeline (value)
				 

				// put these values in our map
				targetMap.put(key, value);				

			}
		inputFile.close();

		return targetMap;
	}

	
	/** Helper method to map movie IDs to an array list of actor IDs
	  * Key: movie ID. Value: array list of actor IDs 
	  * @return targetMap, filled with key/value pairs
	  * @throws IOException 
	  */
	 public static HashMap<String,ArrayList<String>> makeStringToListMap(String filePath) throws IOException {
		 HashMap<String, ArrayList<String>> targetMap = new HashMap<String, ArrayList<String>> ();
		 BufferedReader inputFile = new BufferedReader(new FileReader(filePath));   		 
		 
		 // Read all the lines in the file 
			String line = null;
			while ((line = inputFile.readLine()) != null) {
				
				int pipeLineIndex = line.indexOf("|");
				String key = line.substring(0,pipeLineIndex); // get whatever is to the right of the pipeline (key)
				String value = line.substring(pipeLineIndex + 1, line.length()); //get whatever is to the left of the pipeline (value)
				
				
				// if the movie is in the map, just add its actors to its list 
				if (targetMap.containsKey(key)){ 
					targetMap.get(key).add(value); 

				}
				// otherwise, make a new array list and add the key-value pair to the list 
				else {
					ArrayList<String> myActors = new ArrayList<String>();
					myActors.add(value);
					targetMap.put(key, myActors);
				}
			}
			
		inputFile.close();
		return targetMap;
	 }

/** Helper method to bring up a file chooser
	 */
	 public static String getFilePath() {
	   // Create a file chooser.
	   JFileChooser fc = new JFileChooser();
	   int returnVal = fc.showOpenDialog(null);
	   if (returnVal == JFileChooser.APPROVE_OPTION) {
	     File file = fc.getSelectedFile();
	     String pathName = file.getAbsolutePath();
	     return pathName;
	   }
	   else
	     return "";
	  }
	 
	 
	 
public static void main(String[] args) throws IOException {
	NamedAdjacencyMapGraph<String, String> baconGraph = makeBaconGraph();
	String root = "Kevin Bacon";
	BaconGame<String,String> myGame = new BaconGame<String, String>(baconGraph, root);
	
	System.out.println("Enter the name of an actor:");
	// Get the name of the actor 
  Scanner input = new Scanner(System.in);
  String actorName = input.nextLine(); 
  
  // If you entered the name of the root, print an error message 
  if (actorName.equals(root)){
  	System.out.println(root + "'s " + root + " number is zero!");
  	return;
  }
  
  // If the actor is not in the bacon graph, print an error 
  else if (!myGame.baconGraph.vertexInGraph(actorName)) {
  	System.out.print("That actor is not in our database");
  	return;
  }
  
  // If it's not in the BFS graph, print this error message 
  else if (!(myGame.BFSsearchTree.vertexInGraph(actorName))) {
    System.out.println(actorName +  root + "'s number is infinity. It has no path to " + root);	
  }

  // If the vertex is in the BFS graph, get its bacon number and print the path 
  else {
    int baconNumber = myGame.distanceMap.get(actorName);
    ArrayList<Edge<String>> pathToBacon = myGame.createPath(actorName);
    System.out.println(actorName + "'s" + root + "'s number is " + baconNumber);
    System.out.println(actorName + "'s path to " + root + " is the following: " );
    for (Edge<String> e : pathToBacon) {
    	System.out.println(myGame.BFSsearchTree.endVertices(e)[0].getElement() + " appeared in " + e.getElement() + " with " + myGame.BFSsearchTree.endVertices(e)[1].getElement());
    }
}
  
}

}
  





	

