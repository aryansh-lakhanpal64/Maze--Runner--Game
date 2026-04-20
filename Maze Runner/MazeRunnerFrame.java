import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

class MazeRunnerFrame extends JFrame {
    private final MazePanel mazePanel;
    private JComboBox<String> levelBox;
    private JComboBox<String> algorithmBox;
    private JComboBox<String> editModeBox;
    private JTextArea infoArea;
    private JLabel statusLabel;
    private JSlider speedSlider;
    private JLabel currentLevelLabel;

    MazeRunnerFrame() {
        super("Maze Runner - Phase 3");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));
        getContentPane().setBackground(new Color(11, 18, 32));

        MazeModel initialMaze = MazeModel.generate("Easy 1");
        mazePanel = new MazePanel(initialMaze);
        mazePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        add(createTopBanner(), BorderLayout.NORTH);
        add(mazePanel, BorderLayout.CENTER);
        add(createSidebar(), BorderLayout.EAST);

        mazePanel.setOnSearchFinished(this::updateReport);

        editModeBox.addActionListener(e -> mazePanel.setEditMode((String) editModeBox.getSelectedItem()));
        algorithmBox.addActionListener(e -> mazePanel.setAlgorithm((String) algorithmBox.getSelectedItem()));
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                mazePanel.setAnimationSpeed(speedSlider.getValue());
            }
        });

        mazePanel.setEditMode((String) editModeBox.getSelectedItem());
        mazePanel.setAlgorithm((String) algorithmBox.getSelectedItem());
        mazePanel.setAnimationSpeed(speedSlider.getValue());

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    private JPanel createTopBanner() {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(15, 23, 42));
        banner.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("Maze Runner: A Level-Based Pathfinding Game");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel subtitle = new JLabel("BFS • DFS • Dijkstra | Java Swing GUI | Animated Path Visualization");
        subtitle.setForeground(new Color(191, 219, 254));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(subtitle);

        currentLevelLabel = new JLabel("Current Level: Easy 1");
        currentLevelLabel.setForeground(new Color(216, 180, 254));
        currentLevelLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        banner.add(left, BorderLayout.WEST);
        banner.add(currentLevelLabel, BorderLayout.EAST);
        return banner;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(340, 0));
        sidebar.setBackground(new Color(15, 23, 42));
        sidebar.setBorder(new EmptyBorder(14, 14, 14, 14));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        sidebar.add(createSectionLabel("Controls"));
        sidebar.add(Box.createVerticalStrut(8));

        levelBox = new JComboBox<>(new String[]{
                "Easy 1", "Easy 2", "Easy 3", "Easy 4", "Easy 5",
                "Medium 1", "Medium 2", "Medium 3", "Medium 4", "Medium 5",
                "Hard 1", "Hard 2", "Hard 3", "Hard 4", "Hard 5"
        });
        algorithmBox = new JComboBox<>(new String[]{"BFS", "DFS", "Dijkstra"});
        editModeBox = new JComboBox<>(new String[]{"Set Start", "Set End", "Wall", "Mud", "Erase"});

        styleCombo(levelBox);
        styleCombo(algorithmBox);
        styleCombo(editModeBox);

        JButton generateButton = createButton("Generate Maze", new Color(37, 99, 235));
        JButton runButton = createButton("Run", new Color(168, 85, 247));
        JButton resetButton = createButton("Reset", new Color(100, 116, 139));

        sidebar.add(createFieldBlock("Level", levelBox));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createFieldBlock("Algorithm", algorithmBox));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createFieldBlock("Edit Mode", editModeBox));
        sidebar.add(Box.createVerticalStrut(14));

        JPanel buttonRow = new JPanel(new GridLayout(3, 1, 8, 8));
        buttonRow.setOpaque(false);
        buttonRow.add(generateButton);
        buttonRow.add(runButton);
        buttonRow.add(resetButton);
        sidebar.add(buttonRow);

        sidebar.add(Box.createVerticalStrut(16));
        sidebar.add(createSectionLabel("Speed"));
        sidebar.add(Box.createVerticalStrut(8));

        speedSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 10, 6);
        speedSlider.setBackground(new Color(15, 23, 42));
        speedSlider.setForeground(Color.WHITE);
        speedSlider.setMajorTickSpacing(3);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(false);
        speedSlider.setMaximumSize(new Dimension(300, 50));
        sidebar.add(speedSlider);

        sidebar.add(Box.createVerticalStrut(16));
        sidebar.add(createSectionLabel("Legend"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createLegend());

        sidebar.add(Box.createVerticalStrut(16));
        sidebar.add(createSectionLabel("Stats"));
        sidebar.add(Box.createVerticalStrut(8));

        infoArea = new JTextArea(10, 20);
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        infoArea.setBackground(new Color(30, 41, 59));
        infoArea.setForeground(Color.WHITE);
        infoArea.setBorder(new EmptyBorder(8, 8, 8, 8));
        sidebar.add(infoArea);

        sidebar.add(Box.createVerticalStrut(10));
        statusLabel = new JLabel("Select a mode and click on the maze.");
        statusLabel.setForeground(new Color(191, 219, 254));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sidebar.add(statusLabel);

        levelBox.addActionListener(e -> {
            String level = (String) levelBox.getSelectedItem();
            currentLevelLabel.setText("Current Level: " + level);
        });

        generateButton.addActionListener(e -> {
            String level = (String) levelBox.getSelectedItem();
            mazePanel.setMaze(MazeModel.generate(level));
            mazePanel.setEditMode((String) editModeBox.getSelectedItem());
            mazePanel.setAlgorithm((String) algorithmBox.getSelectedItem());
            mazePanel.setAnimationSpeed(speedSlider.getValue());
            infoArea.setText("");
            statusLabel.setText(level + " maze generated. Set Start and End, then run the algorithm.");
        });

        runButton.addActionListener(e -> {
            mazePanel.setAlgorithm((String) algorithmBox.getSelectedItem());
            mazePanel.runSearch();
            statusLabel.setText("Running " + algorithmBox.getSelectedItem() + "...");
        });

        resetButton.addActionListener(e -> {
            String level = (String) levelBox.getSelectedItem();
            mazePanel.setMaze(MazeModel.generate(level));
            mazePanel.setEditMode((String) editModeBox.getSelectedItem());
            mazePanel.setAlgorithm((String) algorithmBox.getSelectedItem());
            mazePanel.setAnimationSpeed(speedSlider.getValue());
            infoArea.setText("");
            statusLabel.setText("Maze reset.");
        });

        return sidebar;
    }

    private JPanel createLegend() {
        JPanel legend = new JPanel();
        legend.setLayout(new GridLayout(6, 1, 6, 6));
        legend.setOpaque(false);

        legend.add(createLegendItem(new Color(34, 197, 94), "Start"));
        legend.add(createLegendItem(new Color(239, 68, 68), "End"));
        legend.add(createLegendItem(new Color(15, 15, 15), "Wall"));
        legend.add(createLegendItem(new Color(147, 197, 253), "Visited"));
        legend.add(createLegendItem(new Color(168, 85, 247), "Final Path"));
        legend.add(createLegendItem(new Color(127, 86, 48), "Mud"));

        return legend;
    }

    private JPanel createLegendItem(Color color, String name) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        item.setOpaque(false);

        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(18, 18));
        box.setBackground(color);
        box.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        JLabel label = new JLabel(name);
        label.setForeground(Color.WHITE);

        item.add(box);
        item.add(label);
        return item;
    }

    private JPanel createFieldBlock(String labelText, JComboBox<String> box) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setAlignmentX(0f);

        box.setMaximumSize(new Dimension(300, 32));
        box.setAlignmentX(0f);

        block.add(label);
        block.add(Box.createVerticalStrut(6));
        block.add(box);
        return block;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(216, 180, 254));
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setAlignmentX(0f);
        return label;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setBackground(new Color(30, 41, 59));
        combo.setForeground(Color.WHITE);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        combo.setOpaque(true);

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setBackground(isSelected ? new Color(51, 65, 85) : new Color(30, 41, 59));
                c.setForeground(Color.WHITE);
                return c;
            }
        });
    }

    private JButton createButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(220, 36));
        button.setMaximumSize(new Dimension(300, 36));
        return button;
    }

    private void updateReport() {
        SearchResult r = mazePanel.getLastResult();
        if (r == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("Algorithm: ").append(r.algorithmName).append("\n");
        sb.append("Path Found: ").append(r.pathFound ? "Yes" : "No").append("\n");
        sb.append("Visited Nodes: ").append(r.visitedNodes).append("\n");
        sb.append("Peak Frontier: ").append(r.peakFrontier).append("\n");
        sb.append(String.format("Execution Time: %.3f ms%n", r.getExecutionTimeMillis()));

        if (r.pathFound) {
            if ("Dijkstra".equals(r.algorithmName)) {
                sb.append("Total Cost: ").append(r.totalCostOrSteps).append("\n");
            } else {
                sb.append("Path Steps: ").append(r.totalCostOrSteps).append("\n");
            }
        } else {
            sb.append("No path could be found.\n");
        }

        infoArea.setText(sb.toString());
        statusLabel.setText(r.pathFound ? "Path found successfully." : "No path found.");
    }
}