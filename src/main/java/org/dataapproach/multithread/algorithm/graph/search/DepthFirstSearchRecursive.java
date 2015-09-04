package org.dataapproach.multithread.algorithm.graph.search;

/**
 * 
 * https://blog.nraboy.com/2015/04/various-graph-search-algorithms-using-java/
 * 
 * @author sarath
 *
 */
public class DepthFirstSearchRecursive {
	
	public static void main(String[] args) {
		 
        GraphNode n1 = new GraphNode(1);
        GraphNode n2 = new GraphNode(2);
        GraphNode n3 = new GraphNode(3);
        GraphNode n4 = new GraphNode(4);
        GraphNode n5 = new GraphNode(5);
        GraphNode n6 = new GraphNode(6);
        GraphNode n7 = new GraphNode(7);
 
        n1.neighbors = new GraphNode[] {n2, n4, n5};
        n2.neighbors = new GraphNode[] {n1, n3, n4};
        n3.neighbors = new GraphNode[] {n2, n4, n7};
        n4.neighbors = new GraphNode[] {n1, n2, n3, n5, n6, n7};
        n5.neighbors = new GraphNode[] {n1, n4, n6};
        n6.neighbors = new GraphNode[] {n4, n5, n7};
        n7.neighbors = new GraphNode[] {n3, n4, n6};
        
        DFS(n1);
 
    }
	
	public static void DFS(GraphNode node) {
	    System.out.println(node.value);
	    node.visited = true;
	    for(GraphNode w : node.neighbors) {
	        if(!w.visited) {
	            DFS(w);
	        }
	    }
	}

}

