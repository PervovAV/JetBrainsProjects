package tictactoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TicTacToe extends JFrame {
    private GameState gameState;
    private GameAreaPanel gameAreaPanel;
    private SupportPanel supportPanel;
    private SettingsPanel settingsPanel;
    private GameSettings gameSettings = new GameSettings();
    private final int side = 3;

    public TicTacToe() {
        super("Tic Tac Toe");
        gameState = new GameState(side);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 360);
        setLocationRelativeTo(null);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        gameAreaPanel = new GameAreaPanel();
        supportPanel = new SupportPanel();
        settingsPanel = new SettingsPanel();
        add(settingsPanel);
        add(gameAreaPanel);
        add(supportPanel);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menuGame = new JMenu("Game");
        menuGame.setName("MenuGame");
        menuGame.setMnemonic(KeyEvent.VK_G);
        menuBar.add(menuGame);

        JMenuItem menuItemHumanHuman = new JMenuItem("Human vs Human");
        menuItemHumanHuman.setName("MenuHumanHuman");
        menuItemHumanHuman.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameSettings = new GameSettings();
                gameSettings.setXPlayer("Human");
                gameSettings.setYPlayer("Human");
                prepareNewGame();
            }
        });

        JMenuItem menuItemHumanRobot = new JMenuItem("Human vs Robot");
        menuItemHumanRobot.setName("MenuHumanRobot");
        menuItemHumanRobot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameSettings = new GameSettings();
                gameSettings.setXPlayer("Human");
                gameSettings.setYPlayer("Robot");
                prepareNewGame();
            }
        });

        JMenuItem menuItemRobotHuman = new JMenuItem("Robot vs Human");
        menuItemRobotHuman.setName("MenuRobotHuman");
        menuItemRobotHuman.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameSettings = new GameSettings();
                gameSettings.setXPlayer("Robot");
                gameSettings.setYPlayer("Human");
                prepareNewGame();
            }
        });

        JMenuItem menuItemRobotRobot = new JMenuItem("Robot vs Robot");
        menuItemRobotRobot.setName("MenuRobotRobot");
        menuItemRobotRobot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameSettings = new GameSettings();
                gameSettings.setXPlayer("Robot");
                gameSettings.setYPlayer("Robot");
                prepareNewGame();
            }
        });

        JMenuItem menuItemExit = new JMenuItem("Exit");
        menuItemExit.setMnemonic(KeyEvent.VK_X);
        menuItemExit.setName("MenuExit");
        menuItemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menuGame.add(menuItemHumanHuman);
        menuGame.add(menuItemHumanRobot);
        menuGame.add(menuItemRobotHuman);
        menuGame.add(menuItemRobotRobot);
        menuGame.addSeparator();
        menuGame.add(menuItemExit);

        setVisible(true);
    }

    public boolean makeMoveRobot() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Optional<Coordinate> move = Robot.getMove(gameState.getField(), gameState.getTurn());
        if (move.isEmpty()) {
            return false;
        }
        String buttonName = getButtonNameByCoor(move.get());
        gameAreaPanel.updateButtonMoveText(buttonName, gameState.getTurn());
        var played = gameState.makeMove(move.get());
        return true;
    }

    public void prepareNewGame() {
        String player1 = gameSettings.getXPlayer() == PlayerType.HUMAN ? "Human" : "Robot";
        String player2 = gameSettings.getYPlayer() == PlayerType.HUMAN ? "Human" : "Robot";

        settingsPanel.setPlayer1(player1);
        settingsPanel.setPlayer2(player2);
        settingsPanel.setButtonStartReset("Reset");

        TicTacToe.this.gameState = new GameState(side);
        supportPanel.setStatus(generateStatusString());
        clearBoard();
        settingsPanel.startFromMenu(player1, player2);
    }

    private String getButtonNameByCoor(Coordinate coor) {
        String curCoor = coor.getX() + " " + coor.getY();

        switch (curCoor) {
            case "0 0" : return "ButtonA3";
            case "0 1" : return "ButtonB3";
            case "0 2" : return "ButtonC3";
            case "1 0" : return "ButtonA2";
            case "1 1" : return "ButtonB2";
            case "1 2" : return "ButtonC2";
            case "2 0" : return "ButtonA1";
            case "2 1" : return "ButtonB1";
            case "2 2" : return "ButtonC1";
            default: return "unknown";
        }
    }

    private String generateStatusString() {
        var player = gameState.getTurn();
        String playerXO = player == GameState.Player.X ? "X" : "O";
        var playerWhoType = "X".equals(playerXO) ?
                gameSettings.getXPlayer() : gameSettings.getYPlayer();
        String playerWho = playerWhoType == PlayerType.HUMAN ? "Human" : "Robot";

        switch (gameState.getStatus()) {
            case BEFORE: return "Game is not started";
            case IN_PROGRESS: return "The turn of " + playerWho + " Player " + "(" + playerXO + ")";
            case FINISHED:
                var winner = gameState.getWinner();
                playerWhoType = "X".equals(playerXO) ?
                        gameSettings.getYPlayer() : gameSettings.getXPlayer();
                playerWho = playerWhoType == PlayerType.HUMAN ? "Human" : "Robot";
                return winner.isEmpty() ? "Draw" :
                        (winner.get() == GameState.Player.X ?
                              "The " + playerWho + " Player (X) wins" : "The " + playerWho + " Player (O) wins");
        }
        return "unknown";
    }

    private String toPlayerString(GameState.Player player) {
        return player == GameState.Player.X ? "X" : "O";
    }

    private void clearBoard() {
        gameAreaPanel.clear();
    }

    private void updateStatus() {
        supportPanel.setStatus(generateStatusString());
    }


    class GameAreaPanel extends JPanel {
        private ArrayList<FieldButton> buttons;

        public GameAreaPanel() {
            var dims = new Dimension(300, 300);
            setSize(dims);
            setPreferredSize(dims);
            setLayout(new GridLayout(3, 3));

            buttons = new ArrayList<FieldButton>();
            buttons.add(new FieldButton("ButtonA3",  new Coordinate(0, 0)));
            buttons.add(new FieldButton("ButtonB3",  new Coordinate(0, 1)));
            buttons.add(new FieldButton("ButtonC3",  new Coordinate(0, 2)));
            buttons.add(new FieldButton("ButtonA2",  new Coordinate(1, 0)));
            buttons.add(new FieldButton("ButtonB2",  new Coordinate(1, 1)));
            buttons.add(new FieldButton("ButtonC2",  new Coordinate(1, 2)));
            buttons.add(new FieldButton("ButtonA1",  new Coordinate(2, 0)));
            buttons.add(new FieldButton("ButtonB1",  new Coordinate(2, 1)));
            buttons.add(new FieldButton("ButtonC1",  new Coordinate(2, 2)));
            setDisabledButtons();

            for (FieldButton button : buttons) {
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        var gameState = TicTacToe.this.gameState;
                        if (gameState.isGameOver()) {
                            return;
                        }
                        var played = gameState.makeMove(button.getCoordinate());
                        if (played.isPresent()) {
                            button.setText(toPlayerString(played.get()));
                            TicTacToe.this.updateStatus();
                            if (gameState.isGameOver()) {
                                setDisabledButtons();
                                settingsPanel.setResetToButtonStartReset();
                                return;
                            }
                        }
                        GameState.Player turn = TicTacToe.this.gameState.getTurn();
                        PlayerType playerType =
                                turn == turn.X ? gameSettings.getXPlayer() : gameSettings.getYPlayer();
                        if (playerType == PlayerType.ROBOT) {
                            makeMoveRobot();
                            updateStatus();
                            if (TicTacToe.this.gameState.isGameOver()) {
                                gameAreaPanel.setDisabledButtons();
                                settingsPanel.setResetToButtonStartReset();
                            }
                        }
                    }
                });
                add(button);
            }
        }

        public void updateButtonMoveText(String buttonName, GameState.Player player) {
            for (var el : buttons) {
                if (buttonName.equals(el.getName())) {
                    el.setText(toPlayerString(player));
                }
            }
        }

        public void setEnabledButtons() {
            for (FieldButton button : buttons) {
                button.setEnabled(true);
            }
        }

        public void setDisabledButtons() {
            for (FieldButton button : buttons) {
                button.setEnabled(false);
            }
        }

        public void clear() {
            for (FieldButton button : buttons) {
                button.setText(" ");
                setDisabledButtons();
            }
        }
    }


    class SettingsPanel extends JPanel {

        private JButton buttonPlayer1;
        private JButton buttonPlayer2;
        private JButton buttonStartReset;

        public SettingsPanel() {
            var dims = new Dimension(300, 30);
            setSize(dims);
            setPreferredSize(dims);
            setLayout(new GridLayout(1, 3));

            buttonPlayer1 = new JButton("Human");
            buttonPlayer1.setName("ButtonPlayer1");
            buttonPlayer1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    buttonPlayer1.setText("Human".equals(buttonPlayer1.getText()) ? "Robot" : "Human");
                }
            });

            buttonPlayer2 = new JButton("Human");
            buttonPlayer2.setName("ButtonPlayer2");
            buttonPlayer2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    buttonPlayer2.setText("Human".equals(buttonPlayer2.getText()) ? "Robot" : "Human");
                }
            });

            buttonStartReset = new JButton("Start");
            buttonStartReset.setName("ButtonStartReset");
            buttonStartReset.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    var gameSate = TicTacToe.this.gameState;
                    if ("Reset".equals(buttonStartReset.getText())) {
                        buttonStartReset.setText("Start");
                        buttonPlayer1.setEnabled(true);
                        buttonPlayer1.setText("Human");
                        buttonPlayer2.setEnabled(true);
                        buttonPlayer2.setText("Human");
                        TicTacToe.this.gameState = new GameState(side);
                        supportPanel.setStatus(generateStatusString());
                        TicTacToe.this.clearBoard();
                    }   else if ("Start".equals(buttonStartReset.getText()) && gameSate.isGameBefore()) {
                        buttonPlayer1.setEnabled(false);
                        buttonPlayer2.setEnabled(false);
                        gameAreaPanel.setEnabledButtons();
                        gameSettings = new GameSettings();
                        gameSettings.setXPlayer(buttonPlayer1.getText());
                        gameSettings.setYPlayer(buttonPlayer2.getText());
                        TicTacToe.this.gameState.setGameSettings(gameSettings);
                        buttonStartReset.setText("Reset");
                        gameSate.setStatus(GameState.Status.IN_PROGRESS);
                        supportPanel.setStatus(generateStatusString());

                        if (gameSettings.getXPlayer() == PlayerType.ROBOT
                                        && gameSettings.getYPlayer() == PlayerType.ROBOT) {
                            while (!gameState.isGameOver()) {
                                boolean flag = makeMoveRobot();
                                updateStatus();
                                if(!flag) {
                                    return;
                                }
                            }
                            gameAreaPanel.setDisabledButtons();
                            settingsPanel.setResetToButtonStartReset();
                        } else if (gameSettings.getXPlayer() == PlayerType.ROBOT) {
                            makeMoveRobot();
                            supportPanel.setStatus(generateStatusString());
                        }
                    }
                }
            });

            add(buttonPlayer1);
            add(buttonStartReset);
            add(buttonPlayer2);
        }

        public void startFromMenu(String player1, String player2) {
            buttonPlayer1.setText(player1);
            buttonPlayer2.setText(player2);
            buttonStartReset.setText("Reset");

            buttonPlayer1.setEnabled(false);
            buttonPlayer2.setEnabled(false);
            gameAreaPanel.setEnabledButtons();
            gameSettings = new GameSettings();
            gameSettings.setXPlayer(buttonPlayer1.getText());
            gameSettings.setYPlayer(buttonPlayer2.getText());

            var gameSate = TicTacToe.this.gameState;
            gameSate.setStatus(GameState.Status.IN_PROGRESS);
            supportPanel.setStatus(generateStatusString());

            if (gameSettings.getXPlayer() == PlayerType.ROBOT
                    && gameSettings.getYPlayer() == PlayerType.ROBOT) {
                while (!gameState.isGameOver()) {
                    boolean flag = makeMoveRobot();
                    updateStatus();
                    if(!flag) {
                        return;
                    }
                }
                gameAreaPanel.setDisabledButtons();
                settingsPanel.setResetToButtonStartReset();
            } else if (gameSettings.getXPlayer() == PlayerType.ROBOT) {
                makeMoveRobot();
                supportPanel.setStatus(generateStatusString());
            }
        }

        public void setPlayer1(String player) {
            buttonPlayer1.setText(player);
        }

        public void setPlayer2(String player) {
            buttonPlayer2.setText(player);
        }

        public void setButtonStartReset(String text) {
            buttonStartReset.setText(text);
        }

        public void setResetToButtonStartReset() {
            buttonStartReset.setText("Reset");
        }
    }


    class FieldButton extends JButton {
        private Coordinate coord;

        public FieldButton(String name, Coordinate coord) {
            super(" ");
            setName(name);
            setFont(new Font("Arial", Font.BOLD, 40));
            this.coord = coord;
        }

        public Coordinate getCoordinate() { return coord; }
    }


    class SupportPanel extends JPanel {
        private JLabel statusLabel;

        public SupportPanel() {
            var dims = new Dimension(300, 30);
            setSize(dims);
            setPreferredSize(dims);
            statusLabel = new JLabel(TicTacToe.this.generateStatusString());
            statusLabel.setHorizontalTextPosition(JLabel.LEFT);
            var resetButton = new JButton("Reset");
            add(statusLabel);
            resetButton.setName("ButtonReset");
            statusLabel.setName("LabelStatus");

            resetButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    gameAreaPanel.setEnabledButtons();
                }
            });
        }

        public void setStatus(String status) {
            statusLabel.setText(status);
        }
    }
}