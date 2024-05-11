import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class CompleteGraphDrawer extends JFrame {
    private JTextField nodesInput;
    private JButton drawButton;
    private JButton findMSTButton;
    private GraphPanel graphPanel;

    public CompleteGraphDrawer() {
        setTitle("Weighted Complete Graph Drawer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 650);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        nodesInput = new JTextField(10);
        drawButton = new JButton("Draw");
        findMSTButton = new JButton("Find MST");
        findMSTButton.setPreferredSize(new Dimension(120, 30)); // Set preferred size

        drawButton.addActionListener(new DrawButtonListener());
        findMSTButton.addActionListener(new FindMSTButtonListener());

        panel.add(new JLabel("Number of Nodes:"));
        panel.add(nodesInput);
        panel.add(drawButton);
        panel.add(findMSTButton);

        graphPanel = new GraphPanel();
        add(panel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
        add(findMSTButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private class DrawButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int numNodes = Integer.parseInt(nodesInput.getText());
                graphPanel.setNumNodes(numNodes);
                graphPanel.generateWeights();
                graphPanel.setDrawMST(false); // Use the setter here
                graphPanel.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(CompleteGraphDrawer.this, "Please enter a valid number.");
            }
        }
    }
    
    private class FindMSTButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (graphPanel.weights == null) {
                JOptionPane.showMessageDialog(CompleteGraphDrawer.this, "Draw a graph first!");
            } else {
                graphPanel.setDrawMST(true); // Use the setter here
                graphPanel.computeMST();
                graphPanel.repaint();
            }
        }
    }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> new CompleteGraphDrawer());
        }
    }

    class GraphPanel extends JPanel {
        private int numNodes;
        public int[][] weights;
        private int[] mstEdges; // Stores the indices of the nodes connecting MST edges
        
        public int[][] getWeights() {
            return weights;
        }
        
        public GraphPanel() {
            setBackground(Color.WHITE);
            numNodes = 0; // Initialize with zero nodes
            weights = null; // Initialize weights as null
            mstEdges = null; // No MST calculated initially
        }

        public void setNumNodes(int numNodes) {
            this.numNodes = numNodes;
            weights = new int[numNodes][numNodes]; // Initialize weights matrix when number of nodes is set
            mstEdges = new int[numNodes]; // Initialize MST edges array
        }

        public void computeMST() {
            if (weights == null || numNodes == 0) return;

            mstEdges = new int[numNodes]; // Index of the parent node for each node
            int[] key = new int[numNodes]; // Key values used to pick minimum weight edge in cut
            boolean[] inMST = new boolean[numNodes]; // True if vertex is included in MST

            // Initialize all keys as infinite, mstSet as false
            for (int i = 0; i < numNodes; i++) {
                key[i] = Integer.MAX_VALUE;
                inMST[i] = false;
            }

            // Always include first vertex in MST
            key[0] = 0; // Make key 0 so that this vertex is picked as first vertex
            mstEdges[0] = -1; // First node is root of MST

            for (int count = 0; count < numNodes - 1; count++) {
                // Pick the minimum key vertex from the set of vertices not yet included in MST
                int u = -1; // Index of the minimum key vertex
                int min = Integer.MAX_VALUE;
                for (int v = 0; v < numNodes; v++) {
                    if (!inMST[v] && key[v] < min) {
                        min = key[v];
                        u = v;
                    }
                }

                // Add the picked vertex to the MST Set
                inMST[u] = true;

                // Update key and mstEdges index of the adjacent vertices of the picked vertex
                for (int v = 0; v < numNodes; v++) {
                    // Update the key only if weights[u][v] is smaller than key[v]
                    if (weights[u][v] != 0 && !inMST[v] && weights[u][v] < key[v]) {
                        mstEdges[v] = u;
                        key[v] = weights[u][v];
                    }
                }
            }
        }


        public void generateWeights() {
            weights = new int[numNodes][numNodes];
            Random random = new Random();
            for (int i = 0; i < numNodes; i++) {
                for (int j = i + 1; j < numNodes; j++) {
                    int weight = random.nextInt(20) + 1; // Random integer between 1 and 20
                    weights[i][j] = weight;
                    weights[j][i] = weight; // Graph is undirected, so weight is symmetric
                }
            }
        }        

    private boolean drawMST = false;

    public void setDrawMST(boolean drawMST) {
        this.drawMST = drawMST;
    }
    
    public boolean isDrawMST() {
        return drawMST;
    }

    // Inside your GraphPanel's paintComponent
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        drawGraph(g2);
        if (isDrawMST()) drawMST(g2); // Use the getter method here
        g2.dispose();
    }

    private void drawGraph(Graphics2D g2) {
        int radius = 200; // change this to make the graph larger or smaller
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        double angleStep = 2 * Math.PI / numNodes;
    
        // Draw nodes
        for (int i = 0; i < numNodes; i++) {
            int x = (int) (centerX + radius * Math.cos(i * angleStep));
            int y = (int) (centerY + radius * Math.sin(i * angleStep));
            g2.fillOval(x - 5, y - 5, 10, 10);
        }
        
        // Set the alpha composite for transparency
        float alpha = 0.5f; // 50% transparent
        AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2.setComposite(alcom);
    
        // Draw edges (complete graph) with weights
        for (int i = 0; i < numNodes; i++) {
            for (int j = i + 1; j < numNodes; j++) {
                int x1 = (int) (centerX + radius * Math.cos(i * angleStep));
                int y1 = (int) (centerY + radius * Math.sin(i * angleStep));
                int x2 = (int) (centerX + radius * Math.cos(j * angleStep));
                int y2 = (int) (centerY + radius * Math.sin(j * angleStep));
    
                // Draw edge
                g2.drawLine(x1, y1, x2, y2);
    
                // Calculate position for weight label
                int labelX = (x1 + x2) / 2;
                int labelY = (y1 + y2) / 2;
                // Draw weight label
                g2.drawString(String.valueOf(weights[i][j]), labelX, labelY);
            }
        }
    }
    private void drawMST(Graphics2D g2) {
        int radius = 200; // change this to make the graph larger or smaller
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        double angleStep = 2 * Math.PI / numNodes;
    
        // Drawing MST edges with a different color
        if (mstEdges != null) { // Check if MST edges exist
            g2.setStroke(new BasicStroke(2)); // Thicker line for MST edges
            g2.setColor(Color.RED); // Red color for MST edges
            for (int v = 1; v < numNodes; v++) {
                int u = mstEdges[v];
                int x1 = (int) (centerX + radius * Math.cos(u * angleStep));
                int y1 = (int) (centerY + radius * Math.sin(u * angleStep));
                int x2 = (int) (centerX + radius * Math.cos(v * angleStep));
                int y2 = (int) (centerY + radius * Math.sin(v * angleStep));
                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }



        @Override
        public Dimension getPreferredSize() {
            return new Dimension(500, 500);
        }
    

    
}
