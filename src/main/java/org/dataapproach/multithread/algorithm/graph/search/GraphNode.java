package org.dataapproach.multithread.algorithm.graph.search;


/**
 * @author sarath
 *
 */
public class GraphNode {
	public boolean visited;
	public GraphNode[] neighbors;
	public int value;
	
	public GraphNode(int v) {
        this.value = v;
        this.visited = false;
    }
}
