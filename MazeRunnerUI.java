import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class MazeRunnerUI {
    private static final Color BACKGROUND = new Color(28, 34, 31);
    private static final Color PANEL = new Color(44, 49, 42);
    private static final Color FLOOR = new Color(65, 76, 62);
    private static final Color FLOOR_TEXTURE = new Color(76, 86, 68);
    private static final Color WALL_SHADOW = new Color(26, 29, 26);
    private static final Color WALL = new Color(179, 151, 103);
    private static final Color ACCENT = new Color(232, 187, 91);
    private static final Color TEXT = new Color(237, 231, 210);
    private static final Color MUTED = new Color(171, 170, 148);

    private final MazeRunner game;
    private final JFrame window;
    private final MazeView mazeView;
    private final JLabel statusLabel;
    private final JLabel timerLabel;
    private final JLabel scoreLabel;
    private final Timer countdownTimer;
    private JButton startGameButton;
    private JButton freePlayButton;
    private int secondsRemaining = 60;
    private int completedMazes;
    private boolean freePlayMode;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MazeRunnerUI(new MazeRunner()).show());
    }

    public MazeRunnerUI(MazeRunner game) {
        this.game = game;
        statusLabel = createLabel("Choose a game mode to enter the labyrinth.", 14, TEXT);
        timerLabel = createLabel("01:00", 22, ACCENT);
        scoreLabel = createLabel("0", 22, TEXT);
        countdownTimer = new Timer(1000, event -> updateCountdown());

        window = new JFrame("Maze Runner");
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                countdownTimer.stop();
            }
        });
        window.setMinimumSize(new Dimension(820, 660));

        JPanel root = new JPanel(new BorderLayout(22, 18));
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        root.add(createHeader(), BorderLayout.NORTH);

        mazeView = new MazeView();
        JPanel content = new JPanel(new BorderLayout(22, 0));
        content.setOpaque(false);
        content.add(mazeView, BorderLayout.CENTER);
        content.add(createControls(), BorderLayout.EAST);
        root.add(content, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(statusLabel, BorderLayout.CENTER);
        footer.add(createButtons(), BorderLayout.EAST);
        root.add(footer, BorderLayout.SOUTH);

        window.setContentPane(root);
        installKeyBindings();
        window.setSize(1040, 790);
        window.setLocationRelativeTo(null);
    }

    public void show() {
        window.setVisible(true);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBlock.setOpaque(false);
        titleBlock.add(createLabel("MAZERUNNER", 27, TEXT, Font.SERIF));
        titleBlock.add(createLabel("ESCAPE AS MANY LABYRINTHS AS YOU CAN", 11, ACCENT));

        JPanel scoreBlock = new JPanel(new GridLayout(2, 2, 12, 2));
        scoreBlock.setOpaque(false);
        scoreBlock.add(createLabel("TIME", 10, MUTED));
        scoreBlock.add(createLabel("MAZES", 10, MUTED));
        scoreBlock.add(timerLabel);
        scoreBlock.add(scoreLabel);

        header.add(titleBlock, BorderLayout.WEST);
        header.add(scoreBlock, BorderLayout.EAST);
        return header;
    }

    private JPanel createControls() {
        JPanel controls = new JPanel();
        controls.setLayout(new javax.swing.BoxLayout(controls, javax.swing.BoxLayout.Y_AXIS));
        controls.setBackground(PANEL);
        controls.setBorder(BorderFactory.createEmptyBorder(20, 18, 20, 18));
        controls.setPreferredSize(new Dimension(210, 0));

        JLabel heading = createLabel("MOVE", 12, ACCENT);
        heading.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        controls.add(heading);
        controls.add(javax.swing.Box.createVerticalStrut(14));
        addKeyHint(controls, "UP", "W or UP ARROW");
        addKeyHint(controls, "LEFT", "A or LEFT ARROW");
        addKeyHint(controls, "DOWN", "S or DOWN ARROW");
        addKeyHint(controls, "RIGHT", "D or RIGHT ARROW");

        controls.add(javax.swing.Box.createVerticalStrut(22));
        JLabel legend = createLabel("P  YOU     E  EXIT", 12, TEXT);
        legend.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        controls.add(legend);
        controls.add(javax.swing.Box.createVerticalGlue());

        JLabel quitHint = createLabel("Q TO QUIT", 11, MUTED);
        quitHint.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        controls.add(quitHint);
        return controls;
    }

    private void addKeyHint(JPanel parent, String direction, String keys) {
        JLabel label = createLabel(direction + "   " + keys, 11, TEXT);
        label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        parent.add(label);
        parent.add(javax.swing.Box.createVerticalStrut(9));
    }

    private JPanel createButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        startGameButton = new JButton("Start Game");
        styleButton(startGameButton, true);
        startGameButton.addActionListener(event -> startTimedGame());
        freePlayButton = new JButton("Free Play");
        styleButton(freePlayButton, false);
        freePlayButton.addActionListener(event -> startFreePlay());
        JButton quit = new JButton("Quit");
        styleButton(quit, false);
        quit.setFocusable(false);
        quit.addActionListener(event -> closeWindow());
        buttons.add(startGameButton);
        buttons.add(freePlayButton);
        buttons.add(quit);
        return buttons;
    }

    private void styleButton(JButton button, boolean primary) {
        button.setFocusable(false);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setForeground(primary ? BACKGROUND : TEXT);
        button.setBackground(primary ? ACCENT : PANEL);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary ? ACCENT : WALL, 1),
                BorderFactory.createEmptyBorder(9, 16, 9, 16)));
        button.setOpaque(true);
    }

    private JLabel createLabel(String text, int size, Color color) {
        return createLabel(text, size, color, Font.SANS_SERIF);
    }

    private JLabel createLabel(String text, int size, Color color, String fontFamily) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font(fontFamily, Font.BOLD, size));
        return label;
    }

    private void startTimedGame() {
        startGame(false);
    }

    private void startFreePlay() {
        startGame(true);
    }

    private void startGame(boolean freePlay) {
        countdownTimer.stop();
        freePlayMode = freePlay;
        secondsRemaining = 60;
        completedMazes = 0;
        game.startNewGame();
        startGameButton.setEnabled(freePlay);
        freePlayButton.setEnabled(!freePlay);
        statusLabel.setText(freePlay
                ? "Free Play is on. Find the exit at your own pace."
                : "The labyrinth is open. Find the exit.");
        refreshScoreboard();
        mazeView.repaint();
        if (!freePlay) {
            countdownTimer.start();
        }
    }

    private void updateCountdown() {
        secondsRemaining--;
        refreshScoreboard();
        if (secondsRemaining <= 0) {
            secondsRemaining = 0;
            countdownTimer.stop();
            game.finishGame();
            statusLabel.setText("Time is up. You completed " + completedMazes + " mazes.");
            startGameButton.setEnabled(true);
            freePlayButton.setEnabled(true);
            mazeView.repaint();
        }
    }

    private void refreshScoreboard() {
        timerLabel.setText(freePlayMode
                ? "FREE"
                : String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60));
        scoreLabel.setText(Integer.toString(completedMazes));
    }

    private void installKeyBindings() {
        bindKey(KeyEvent.VK_W, Direction.UP);
        bindKey(KeyEvent.VK_UP, Direction.UP);
        bindKey(KeyEvent.VK_S, Direction.DOWN);
        bindKey(KeyEvent.VK_DOWN, Direction.DOWN);
        bindKey(KeyEvent.VK_A, Direction.LEFT);
        bindKey(KeyEvent.VK_LEFT, Direction.LEFT);
        bindKey(KeyEvent.VK_D, Direction.RIGHT);
        bindKey(KeyEvent.VK_RIGHT, Direction.RIGHT);

        window.getRootPane().getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), "quit");
        window.getRootPane().getActionMap().put("quit", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                closeWindow();
            }
        });
    }

    private void closeWindow() {
        countdownTimer.stop();
        window.dispose();
    }

    private void bindKey(int keyCode, Direction direction) {
        String actionName = "move-" + direction.name() + "-" + keyCode;
        window.getRootPane().getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(keyCode, 0), actionName);
        window.getRootPane().getActionMap().put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                movePlayer(direction);
            }
        });
    }

    private void movePlayer(Direction direction) {
        if (!game.isGameActive()) {
            return;
        }
        if (!game.playMove(direction)) {
            statusLabel.setText("A wall blocks that way.");
        } else if (game.hasPlayerEscaped()) {
            completedMazes++;
            game.generateNextMaze();
            statusLabel.setText("Exit found. A new labyrinth awaits.");
            refreshScoreboard();
        } else {
            statusLabel.setText("Keep searching for the exit.");
        }
        mazeView.repaint();
    }

    private class MazeView extends JPanel {
        private static final long serialVersionUID = 1L;

        private MazeView() {
            setBackground(BACKGROUND);
            setPreferredSize(new Dimension(650, 620));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth() - 36, getHeight() - 36) / game.getBoard().getSize();
            if (size <= 0) {
                g.dispose();
                return;
            }
            int boardPixels = size * game.getBoard().getSize();
            int left = (getWidth() - boardPixels) / 2;
            int top = (getHeight() - boardPixels) / 2;
            g.setColor(FLOOR);
            g.fillRoundRect(left - 7, top - 7, boardPixels + 14, boardPixels + 14, 10, 10);
            g.setStroke(new BasicStroke(Math.max(2, size / 13f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            for (int row = 0; row < game.getBoard().getSize(); row++) {
                for (int column = 0; column < game.getBoard().getSize(); column++) {
                    drawTile(g, left + column * size, top + row * size, size, row, column);
                }
            }
            g.setColor(WALL_SHADOW);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(left - 7, top - 7, boardPixels + 14, boardPixels + 14, 10, 10);

            if (!game.isGameActive()) {
                g.setComposite(AlphaComposite.SrcOver.derive(0.82f));
                g.setColor(BACKGROUND);
                g.fillRoundRect(left, top, boardPixels, boardPixels, 8, 8);
                g.setComposite(AlphaComposite.SrcOver);
                String message = game.isGameOver() ? "TIME IS UP" : "PRESS START GAME";
                g.setColor(ACCENT);
                g.setFont(new Font(Font.SERIF, Font.BOLD, 22));
                int messageWidth = g.getFontMetrics().stringWidth(message);
                g.drawString(message, left + (boardPixels - messageWidth) / 2, top + boardPixels / 2);
            }
            g.dispose();
        }

        private void drawTile(Graphics2D g, int x, int y, int size, int row, int column) {
            Board board = game.getBoard();
            Tile tile = board.getTile(row, column);
            boolean isExit = row == board.getSize() - 1 && column == board.getSize() - 1;
            boolean hasPlayer = row == game.getPlayer().getRow() && column == game.getPlayer().getColumn();

            g.setColor(isExit ? new Color(80, 71, 43) : FLOOR);
            g.fillRect(x, y, size, size);
            if ((row * 7 + column * 11) % 4 == 0) {
                g.setColor(FLOOR_TEXTURE);
                int pebble = Math.max(2, size / 16);
                g.fillOval(x + size / 3, y + size / 3, pebble, pebble);
            }

            float wallWidth = Math.max(3, size / 7f);
            g.setStroke(new BasicStroke(wallWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
            g.setColor(WALL_SHADOW);
            if (tile.hasWall(Direction.UP)) g.drawLine(x, y + 2, x + size, y + 2);
            if (tile.hasWall(Direction.RIGHT)) g.drawLine(x + size - 2, y, x + size - 2, y + size);
            if (tile.hasWall(Direction.DOWN)) g.drawLine(x, y + size - 2, x + size, y + size - 2);
            if (tile.hasWall(Direction.LEFT)) g.drawLine(x + 2, y, x + 2, y + size);
            g.setColor(WALL);
            if (tile.hasWall(Direction.UP)) g.drawLine(x, y, x + size, y);
            if (tile.hasWall(Direction.RIGHT)) g.drawLine(x + size, y, x + size, y + size);
            if (tile.hasWall(Direction.DOWN)) g.drawLine(x, y + size, x + size, y + size);
            if (tile.hasWall(Direction.LEFT)) g.drawLine(x, y, x, y + size);

            if (hasPlayer) {
                int diameter = size / 2;
                g.setColor(ACCENT);
                g.fillOval(x + (size - diameter) / 2, y + (size - diameter) / 2, diameter, diameter);
            } else if (isExit) {
                g.setColor(ACCENT);
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(12, size / 3)));
                g.drawString("E", x + size / 2 - size / 9, y + size / 2 + size / 8);
            }
        }
    }
}