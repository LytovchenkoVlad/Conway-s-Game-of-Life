
/*
 * @Author: Litovchenko Vladislav
 * go040596@gmail.com
 * Conway's Game of Life
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameOfLife {
	// ******** CONSTANT's
	final String NAME_OF_GAME = "Conway's Game of Life";
	final int START_LOCATION = 200;
	final int LIFE_SIZE = 50;
	final int POINT_RADIUS = 10;
	final int FIELD_SIZE = (LIFE_SIZE * 1) * POINT_RADIUS - 3;
	// ******** CONSTANT's
	boolean[][] lifeGeneration = new boolean[LIFE_SIZE][LIFE_SIZE];
	boolean[][] nextGeneration = new boolean[LIFE_SIZE][LIFE_SIZE];
	volatile boolean goNextGeneration = false; // fixed the problem in 64-bit
												// JVM
	int showDelay = 200;
	Random random = new Random();
	JFrame frame; // variable declaration - обьявление переменной JFrame
	Canvas canvasPanel;

	public static void main(String[] args) {
		new GameOfLife().go();
	}

	void go() {
		/*
		 * CREATION WINDOW IN JFRAME
		 */
		frame = new JFrame(NAME_OF_GAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // button "EXIT"
		frame.setSize(500, 500); // it's size wiwdow
		frame.setLocation(START_LOCATION, START_LOCATION); // position left-up
															// ugol window
		frame.setResizable(false); // don't may resize window

		canvasPanel = new Canvas();
		canvasPanel.setBackground(Color.white);

		JButton fillButton = new JButton("Fill");
		fillButton.addActionListener(new FillButtonListener()); // оброботчик
																// класса выше

		JButton stepButton = new JButton("Step");
		stepButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				processOfLife();
				canvasPanel.repaint();
			}
		});

		final JButton goButton = new JButton("Play");
		goButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goNextGeneration = !goNextGeneration;
				goButton.setText(goNextGeneration ? "Stop" : "Play");
			}
		});

		JPanel btnPanel = new JPanel();
		btnPanel.add(fillButton);
		btnPanel.add(stepButton);
		btnPanel.add(goButton);

		frame.getContentPane().add(BorderLayout.CENTER, canvasPanel);
		frame.getContentPane().add(BorderLayout.SOUTH, btnPanel);
		frame.setVisible(true);

		// endless loop of life
		while (true) {
			if (goNextGeneration) {
				processOfLife();
				canvasPanel.repaint();
				try {
					Thread.sleep(showDelay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// randomly fill cells
	public class FillButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			for (int x = 0; x < LIFE_SIZE; x++) {
				for (int y = 0; y < LIFE_SIZE; y++) {
					lifeGeneration[x][y] = random.nextBoolean();
				}
			}
			canvasPanel.repaint();
		}
	}

	// count the number of neighbors
	int countNeighbors(int x, int y) {
		int count = 0;
		for (int dx = -1; dx < 2; dx++) {
			for (int dy = -1; dy < 2; dy++) {
				int nX = x + dx;
				int nY = y + dy;
				nX = (nX < 0) ? LIFE_SIZE - 1 : nX;
				nY = (nY < 0) ? LIFE_SIZE - 1 : nY;
				nX = (nX > LIFE_SIZE - 1) ? 0 : nX;
				nY = (nY > LIFE_SIZE - 1) ? 0 : nY;
				count += (lifeGeneration[nX][nY]) ? 1 : 0;
			}
		}
		if (lifeGeneration[x][y]) {
			count--;
		}
		return count;
	}

	// the main process of life
	void processOfLife() {
		for (int x = 0; x < LIFE_SIZE; x++) {
			for (int y = 0; y < LIFE_SIZE; y++) {
				int count = countNeighbors(x, y);
				nextGeneration[x][y] = lifeGeneration[x][y];
				// if are 3 live neighbors around empty cells - the cell becomes
				// alive
				nextGeneration[x][y] = (count == 3) ? true : nextGeneration[x][y];
				// if cell has less than 2 or greater than 3 neighbors - it will
				// be die
				nextGeneration[x][y] = ((count < 2) || (count > 3)) ? false : nextGeneration[x][y];
			}
		}
		for (int x = 0; x < LIFE_SIZE; x++) {
			System.arraycopy(nextGeneration[x], 0, lifeGeneration[x], 0, LIFE_SIZE);
		}
	}

	// Painter all
	public class Canvas extends JPanel {
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			for (int x = 0; x < LIFE_SIZE; x++) {
				for (int y = 0; y < LIFE_SIZE; y++) {
					if (lifeGeneration[x][y]) {
						g.fillOval(x * POINT_RADIUS, y * POINT_RADIUS, POINT_RADIUS, POINT_RADIUS);
					}
				}
			}

		}
	}

}
