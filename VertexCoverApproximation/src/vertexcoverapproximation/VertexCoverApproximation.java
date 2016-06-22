package vertexcoverapproximation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.io.*;

public class VertexCoverApproximation {
	
	public static void main(String[] args) {
		Scanner inputFile = null;
		PrintStream outFile = null;
		try {
			outFile = new PrintStream(new File("approxOutput.txt"));
		} catch (FileNotFoundException e1) {
			System.out.println("File did not get created");
			System.exit(1);
		}

		try {
			inputFile = new Scanner(new File(args[0]));
		} catch (FileNotFoundException e) {
			System.out.println("File did not open");
			System.exit(1);
		}

		//Test
		//inputFile = new Scanner(System.in);
		String choice;
		choice = args[1];

		Graph g = new Graph();
		
		while(inputFile.hasNext()) {
			String v1 = inputFile.next();
			String v2 = inputFile.next();
			//System.out.println(v1 + " " + v2);
			if( v1.equals("stop"))
				break;
			g.addEdge(v1, v2);
			//System.out.println(g.hasEdge(v1, v2));
		}

		ArrayList<Vertex> c = null;

		//Test
		//choice = "1";


		if (choice.equals("0")){
			c = AppGreedyVert(g);
		} else if (choice.equals("1")) {
			c = AppGreedyEdge(g);
		} else {
			System.out.println("Incorrect Number for algorithm");
			System.exit(0);
		}
		

		for(int i=0; i < c.size(); i++) {
			outFile.println(c.get(i).getName());
			//outFile.println(i);
		}
	}
	
	public static ArrayList<Vertex> AppGreedyVert(Graph g) {
		ArrayList<Vertex> c = new ArrayList<Vertex>();
		System.out.println(g.numEdges());

		while(g.numEdges() > 0) {
			Vertex v = g.getHighVert();
			if(!c.contains(v)) {
				c.add(v);
			}
			//System.out.println(v.getName());
			//System.out.println(g.adjacentTo(v));
			
			ArrayList<Vertex> rmList = new ArrayList<Vertex>();
			
			TreeSet<Vertex> set = g.adjacentTo(v);
			for(Vertex t : set) {
				//System.out.println(v.getName() + " : " + t.getName());
				rmList.add(t);
			}
			
			for( Vertex t: rmList) {
				g.removeEdge(v.getName(), t.getName());
			}
			
			
			/**
			for(Iterator<Vertex> iterator = g.adjacentTo(v).iterator(); iterator.hasNext();) {
				System.out.println(v.getName() + " : " + iterator.next().getName());
				g.removeEdge(v.getName(), iterator.next().getName());
			}
			*/
		}
		return c;
	}

	public static ArrayList<Vertex> AppGreedyEdge(Graph g) {
		ArrayList<Vertex> c = new ArrayList<Vertex>();

		while(g.numEdges() > 0) {
			ArrayList<Edge> toRemove = new ArrayList<Edge>();

			for( Vertex i : g.getVertices()) {
				int flag = 0;
				for( Vertex j : g.adjacentTo(i)) {
					if( g.hasEdge(i.getName(), j.getName()) && flag == 0) {
						if( !c.contains(i)) {
							c.add(i);
						}
						if( !c.contains(j)) {
							c.add(j);
						}
						for( Vertex k : g.adjacentTo(j)) {
							if( g.hasEdge(j.getName(), k.getName())) {
								toRemove.add(new Edge(j, k));
								//g.removeEdge(j.getName(), k.getName());
							}
						}
						toRemove.add(new Edge(i, j));
						//g.removeEdge(i.getName(), j.getName());
						flag = 1;
					}
					if( flag == 1 && g.hasEdge(i.getName(), j.getName())) {
						toRemove.add(new Edge(i, j));
						//g.removeEdge(i.getName(), j.getName());
					}
				}
				break;
			}
			for( Edge e : toRemove ) {
				g.removeEdge(e.getFrom().getName(), e.getTo().getName());
			}
		}
		return c;
	}
}

class Edge {
	private Vertex to;
	private Vertex from;
	
	public Edge(Vertex from, Vertex to) {
		this.to = to;
		this.from = from;
	}
	
	public Vertex getTo() {
		return this.to;
	}
	
	public Vertex getFrom() {
		return this.from;
	}
}

class Graph {
	private HashMap<Vertex, TreeSet<Vertex>> myAdjList;
	private HashMap<String, Vertex> myVertices;
	private static final TreeSet<Vertex> EMPTY_SET= new TreeSet<Vertex>();
	private int myNumVertices;
	private int myNumEdges;
	
	public Graph() {
		myAdjList = new HashMap<Vertex, TreeSet<Vertex>>();
		myVertices = new HashMap<String, Vertex>();
		myNumVertices = 0;
		myNumEdges = 0;
	}
	
	public Vertex addVertex(String name) {
		Vertex v;
		v = myVertices.get(name);
		if (v == null) {
			v = new Vertex(name);
			myVertices.put(name, v);
			myAdjList.put(v, new TreeSet<Vertex>());
			myNumVertices++;
		}
		return v;
	}

	public Vertex getHighVert() {
		HashMap.Entry<String, Vertex> temp = myVertices.entrySet().iterator().next();
		Vertex high = temp.getValue();
		
		Set<String> keySet = myVertices.keySet();
		Iterator<String> keySetIterator = keySet.iterator();		
		while (keySetIterator.hasNext()) {
			Vertex v = getVertex(keySetIterator.next());
			//System.out.println(v.getName() + " : " + v.getEdge());
			//System.out.println("high: " + high.getName() + " : " + high.getEdge());
			if( v.getEdge() > high.getEdge())
				high = v;
			//System.out.println("New High: " + high.getName());
		}
		
		/**
		for(Entry<String, Vertex> entry: myVertices.entrySet()) {
			if(entry.getValue().getEdge() > high.getEdge())
				high = entry.getValue();
		}
		*/
		return high;
	}

	public Vertex getVertex(String name) {
		return myVertices.get(name);
	}
	
	public boolean hasVertex(String name) {
		return myVertices.containsKey(name);
	}
	
	public boolean hasEdge(String from, String to) {
		if (!hasVertex(from) || !hasVertex(to))
			return false;
		/**
		System.out.println("hasEdge");
		System.out.println(from);
		System.out.println(to);
		System.out.println(myVertices.get(to).getName());
		*/
		//System.out.println(myAdjList.get(myVertices.get(from)).last().getName());
		return (myAdjList.get(myVertices.get(from))).contains(myVertices.get(to));
	}

	public void addEdge(String from, String to) {
		Vertex v, w;
		if (hasEdge(from, to))
			return;
		myNumEdges++;
		if ((v = getVertex(from)) == null) 
			v = addVertex(from);

		if ((w = getVertex(to)) == null)
			w = addVertex(to);
		//System.out.println(v.getName());
		//System.out.println(w.getName());
		//System.out.println("--------");
		v.addEdge();
		w.addEdge();
		myAdjList.get(v).add(w);
		myAdjList.get(w).add(v);
	}
	
	public void removeEdge(String from, String to) {
		if (!hasEdge(from, to))
			return;
		myNumEdges--;
		
		/**
		 * NOT SURE IF THIS IS NEEDED
		 ****************************
		if (getVertex(from) != null)
			removeVertex(from);
		if (getVertex(to) != null)
			removeVertex(to);
		 ****************************
		 */
		//System.out.println(from.toString());
		//System.out.println(myAdjList.containsKey(getVertex(from)));
		
		myAdjList.get(getVertex(from)).remove(getVertex(to));
		getVertex(from).removeEdge();
		myAdjList.get(getVertex(to)).remove(getVertex(from));
		getVertex(to).removeEdge();

		if(getVertex(from).getEdge() == 0) {
			myAdjList.remove(getVertex(from));
			myVertices.remove(from);
		}
		
		if (getVertex(to).getEdge() == 0) {
			myAdjList.remove(getVertex(to));
			myVertices.remove(to);
		}
	}
	
	public TreeSet<Vertex> adjacentTo(String name) {
		if (!hasVertex(name))
			return EMPTY_SET;
		return myAdjList.get(getVertex(name));
	}
	
	public TreeSet<Vertex> adjacentTo(Vertex v) {
		if (!myAdjList.containsKey(v))
			return EMPTY_SET;
		return myAdjList.get(v);
	}
	
	public Iterable<Vertex> getVertices() {
		return myVertices.values();
	}
	
	public int numVertices() {
		return myNumVertices;
	}
	
	public int numEdges() {
		return myNumEdges;
	}
}

class Vertex implements Comparable<Vertex> {

	private String name;
	private int edges;
	
	@Override
	public int compareTo(Vertex o) {
		int v;
		
		v = this.name.compareTo(o.getName());
		if (v != 0) return v;
		
		v = compareInt(Integer.parseInt(this.name), Integer.parseInt(o.getName()));
		if (v != 0) return v;
		return 0;
	}
	
	public static int compareInt(int i1, int i2) {
		if (i1 < i2) {
			return -1;
		} else if (i1 > i2) {
			return +1;
		} else {
			return 0;
		}
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof Vertex) && (this.compareTo((Vertex) o) == 0);
	}
	
	public Vertex(String name) {
		this.name = name;
		this.edges = 0;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void addEdge() {
		this.edges++;
	}
	
	public void removeEdge() {
		this.edges--;
	}
	
	public int getEdge() {
		return edges;
	}
}