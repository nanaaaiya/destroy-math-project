// Prim's MST for adjacency list representation of graph

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;


public class Prim {
	static class Graph {
		int V;
		ArrayList<ArrayList<Node>> adj;

		// Inner class to represent an edge (destination and weight)
		static class Node {
			int dest;
			int weight;

			Node(int dest, int weight) {
				this.dest = dest;
				this.weight = weight;
			}
		}

		Graph(int V) {
			this.V = V;
			adj = new ArrayList<>(V);
			for (int i = 0; i < V; i++)
				adj.add(new ArrayList<>());
		}


		
        // FUNCTIONS TO SUPPORT AUTO EDGE GENERATOR
		// add an undirected edge between two vertices with given weight
		public void addEdge(int src, int dest, int weight) {
			adj.get(src).add(new Node(dest, weight));
			adj.get(dest).add(new Node(src, weight));
		}
        // Add a random weight to a edge
		private int generateWeight() {
			Random weight = new Random();
			return weight.nextInt(0, 30);
		}
		
		private ArrayList<ArrayList<Integer>> print_2element_subsets(int n, int current_pos, int[] current_digits, int count) {
			ArrayList<ArrayList<Integer>> outerArray = new ArrayList<>();
			print_all_subset_with_two_elements(n, current_pos, current_digits, count, outerArray);
			return outerArray;
		}
		private int[][] getSubsets(ArrayList<ArrayList<Integer>> outerArray) {
			int[][] array = new int[outerArray.size()][];
			for (int i = 0; i < outerArray.size(); i++) {
				ArrayList<Integer> innerArray = outerArray.get(i);
				array[i] = new int[innerArray.size()];
				for (int j = 0; j < innerArray.size(); j++) {
					array[i][j] = innerArray.get(j);
				}
			}
			return array;
		}
	    private void print_all_subset_with_two_elements(int n, int current_pos, int[] current_digits, int count, ArrayList<ArrayList<Integer>> outerArray) {
			if (count == 2) { // Ensure exactly 2 elements in the subset
				ArrayList<Integer> array = new ArrayList<>();
				//System.out.print("{");
				for (int i = 0; i < n; i++) 
					if (current_digits[i] == 1)
						//System.out.print(" " + (i + 1));
						array.add(i);
				outerArray.add(array);
				//System.out.println("}");
			} else if (current_pos < n) {
				// Recursively generate subsets with two elements
				current_digits[current_pos] = 1; // Include the current element
				print_all_subset_with_two_elements(n, current_pos + 1, current_digits, count + 1, outerArray);
				current_digits[current_pos] = 0; // Exclude the current element
				print_all_subset_with_two_elements(n, current_pos + 1, current_digits, count, outerArray);
			}
		}
		

		// FIND MST USING PRIM
		public void primMST() {
			int[] parent = new int[V];
			int[] key = new int[V];
			boolean[] inMST = new boolean[V];

			for (int i = 0; i < V; i++) {
				parent[i] = -1;		 // Array to store the parent node of each vertex in the MST
				key[i] = Integer.MAX_VALUE; // store the min key value for each vertex (is infinite initially.)
				inMST[i] = false;	 // track if the vertex is in the MST or not
			}

            // Step 2: Iniitialise minHeap with 1st vertex as root
			PriorityQueue<Node> minHeap = new PriorityQueue<>((a, b) -> a.weight - b.weight); // minHeap - determine the priority based on the 
            // value of weight
            // if weight a is larger => b has higher priority and vice versa
            // if weight a == weight b then determine the priority based on the natural ordering of Node object

			key[0] = 0;	 // Start the MST from vertex 0
			minHeap.add(new Node(0, key[0]));

			while (!minHeap.isEmpty()) {
                // Extract min value node from minHeap
				Node u = minHeap.poll(); 
				int uVertex = u.dest; // let extracted vertex be u
				inMST[uVertex] = true;

				// Traverse through all adjacent vertices of u (the extracted vertex) and update their key values
				for (Node v : adj.get(uVertex)) {
					int vVertex = v.dest;
					int weight = v.weight;

					// If v is not yet included in MST and weight of u-v is less than key value of v 
					if (!inMST[vVertex] && weight < key[vVertex]) {
                        // update key value and parent of v
						parent[vVertex] = uVertex;
						key[vVertex] = weight;
						minHeap.add(new Node(vVertex, key[vVertex]));
					}
				}
			}

			printMST(parent);
            //drawGraph(); // This will draw the whole graph in a window
			drawMST(parent); // This will draw whole graph with MST in another window
		}

		
		// Function to print the edges of the Minimum Spanning Tree - just to double check
		public void printMST(int[] parent) {
			System.out.println("Edges of Minimum Spanning Tree:");
			for (int i = 1; i < V; i++) {
				System.out.println(parent[i] + " - " + i);
			}
		}

        //TODO: ADD VISUAZLISATION 
        //  DRAW FUNCTIONS - demo
        public void drawGraph() {
            JFrame frame = new JFrame("Graph");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setVisible(true);
        
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
        
                    // Draw all edges and their weights
                    for (int i = 0; i < V; i++) {
                        for (Node node : adj.get(i)) {
                            int src = i;
                            int dest = node.dest;
                            int weight = node.weight;
                            int x1 = (src % 4) * 150 + 50;
                            int y1 = (src / 4) * 150 + 50;
                            int x2 = (dest % 4) * 150 + 50;
                            int y2 = (dest / 4) * 150 + 50;
                            g.setColor(Color.DARK_GRAY);
                            g.drawLine(x1, y1, x2, y2);
                            g.drawString(Integer.toString(weight), (x1 + x2) / 2, (y1 + y2) / 2);
                        }
                    }
        
                    // Draw vertices
                    for (int i = 0; i < V; i++) {
                        int x = (i % 4) * 150 + 50;
                        int y = (i / 4) * 150 + 50;
                        g.fillOval(x - 5, y - 5, 10, 10);
                        g.drawString(Integer.toString(i), x - 5, y - 5);
                    }
                }
            };
        
            frame.add(panel);
        }
        
        public void drawMST(int[] parent) {
            JFrame frame = new JFrame("Minimum Spanning Tree");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setVisible(true);
        
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
        
                    // Draw all edges and their weights
                    for (int i = 0; i < V; i++) {
                        for (Node node : adj.get(i)) {
                            int src = i;
                            int dest = node.dest;
                            int weight = node.weight;
                            int x1 = (src % 3) * 150 + 50;
                            int y1 = (src / 3) * 150 + 50;
                            int x2 = (dest % 3) * 150 + 50;
                            int y2 = (dest / 3) * 150 + 50;
                            g.drawLine(x1, y1, x2, y2);
                            g.drawString(Integer.toString(weight), (x1 + x2) / 2, (y1 + y2) / 2);
                            /*int weightX = (x1 + x2) / 2;
                            int weightY = (y1 + y2) / 2;
                            g.drawString(Integer.toString(weight), weightX, weightY);*/
                        }
                    }
        
                    // Draw vertices
                    for (int i = 0; i < V; i++) {
                        int x = (i % 3) * 150 + 50;
                        int y = (i / 3) * 150 + 50;
                        g.fillOval(x - 5, y - 5, 10, 10);
                        g.drawString(Integer.toString(i), x - 5, y - 5);
                    }
        
                    // Highlight MST edges
                    for (int i = 1; i < V; i++) {
                        int parentVertex = parent[i];
                        if (parentVertex != -1) { // Check if there is a parent for the current vertex
                            int x1 = (i % 3) * 150 + 50;
                            int y1 = (i / 3) * 150 + 50;
                            int x2 = (parentVertex % 3) * 150 + 50;
                            int y2 = (parentVertex / 3) * 150 + 50;
                            g.setColor(Color.GREEN); // Highlight MST edges in red
                            g.drawLine(x1, y1, x2, y2);
                            //g.setColor(Color.BLACK); // Reset color back to black
                            /*int weightX = (x1 + x2) / 2;
                            int weightY = (y1 + y2) / 2;
                            g.drawString(Integer.toString(adj.get(i).get(parentVertex).weight), weightX, weightY);
                            g.setColor(Color.BLACK); // Reset color back to black*/
                        }
                    }
                }
            };
        
            frame.add(panel);
        }
        


		
	}

	public static void main(String[] args) {
		// GET USER'S INPUT (number of vertices)
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of vertices (V) [add the limit number here]: ");
        int V = scanner.nextInt();
        
        
        // AUTO EDGE GENERATOR
		Graph g = new Graph(V);
		int[] current_digits = new int[V];
        ArrayList<ArrayList<Integer>> powerSet = g.print_2element_subsets(V, 0, current_digits,0);
		int[][] subsets = g.getSubsets(powerSet);
		for (int i = 0; i < subsets.length; i++) {
			System.out.println(Arrays.toString(subsets[i]));
		}

		// Create connections
		System.out.println("len: " + subsets.length);
		for (int i = 0; i < subsets.length; i++) {
			int src = subsets[i][0];
			int dest = subsets[i][1];
			int weight = g.generateWeight();
			/*System.out.println("source: " + src);
			System.out.println("dest: " + dest);
			System.out.println("weight: " + weight);
			System.out.println();*/
			g.addEdge(src, dest, weight);
		}
		g.primMST();
	}
}
