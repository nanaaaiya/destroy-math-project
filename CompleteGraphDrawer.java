import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class CompleteGraphDrawer extends JFrame {
    private JTextField nodesInput;
    private JButton drawButton;
    private GraphPanel graphPanel;

    public CompleteGraphDrawer() {
        setTitle("Weighted Complete Graph Drawer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        nodesInput = new JTextField(10);
        drawButton = new JButton("Draw");
        drawButton.addActionListener(new DrawButtonListener());

        panel.add(new JLabel("Number of Nodes:"));
        panel.add(nodesInput);
        panel.add(drawButton);

        graphPanel = new GraphPanel();
        add(panel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private class DrawButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = nodesInput.getText();
            int numNodes = Integer.parseInt(input);
            graphPanel.setNumNodes(numNodes);
            graphPanel.generateWeights();
            graphPanel.repaint();
        }
    }

    private class GraphPanel extends JPanel {
        private int numNodes;
        private int[][] weights;

        public GraphPanel() {
            setBackground(Color.WHITE);
        }

        public void setNumNodes(int numNodes) {
            this.numNodes = numNodes;
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

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int radius = 100;
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            double angleStep = 2 * Math.PI / numNodes;

            // Draw nodes
            for (int i = 0; i < numNodes; i++) {
                int x = (int) (centerX + radius * Math.cos(i * angleStep));
                int y = (int) (centerY + radius * Math.sin(i * angleStep));
                g.fillOval(x - 5, y - 5, 10, 10);
            }

            // Draw edges (complete graph) with weights
            for (int i = 0; i < numNodes; i++) {
                for (int j = i + 1; j < numNodes; j++) {
                    int x1 = (int) (centerX + radius * Math.cos(i * angleStep));
                    int y1 = (int) (centerY + radius * Math.sin(i * angleStep));
                    int x2 = (int) (centerX + radius * Math.cos(j * angleStep));
                    int y2 = (int) (centerY + radius * Math.sin(j * angleStep));

                    // Draw edge
                    g.drawLine(x1, y1, x2, y2);

                    // Calculate position for weight label
                    int labelX = (x1 + x2) / 2;
                    int labelY = (y1 + y2) / 2;
                    // Draw weight label
                    g.drawString(String.valueOf(weights[i][j]), labelX, labelY);
                }
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, 400);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CompleteGraphDrawer());
    }
}
