import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class Color7 extends JFrame implements ActionListener, HyperlinkListener, ItemListener
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private final int BOARD_SIZE	= 31;
	private final Color BOARD_BGCOLOR	= new Color(0xC0C0FF);
	private final int GRID_WIDTH	= 16;
	private final int BORDER_WIDTH	=  2;
	private final Color[] GRID_COLOR= new Color[] {
			new Color(0xFF0000),
			new Color(0xFFFF00),
			new Color(0x00FF00),
			new Color(0x00FFFF),
			new Color(0x0000FF),
			new Color(0xFF00FF),
			new Color(0x7F7F7F)
		};
	private final String[] COLOR_NAME	= new String[] {
			"Red    ",
			"Yellow ",
			"Green  ",
			"Cyan   ",
			"Blue   ",
			"Magenta",
			"Gray   "
		};
	private final int TURN_PLAYER	= 1;
	private final int TURN_COMPUTER	= 2;

	/**	AI Difficulty.	**/
	@SuppressWarnings("unused")
	private final int AI_DIFF_EASY		= 0;	// Easy
	private final int AI_DIFF_MEDIUM	= 1;	// Medium
	@SuppressWarnings("unused")
	private final int AI_DIFF_HARD		= 2;	// Hard
	private int AI_DEPTH	= AI_DIFF_MEDIUM;

	/**	Color Clustering Factor.	**/
	@SuppressWarnings("unused")
	private final int COLOR_CLUS_VLOW	= 0;	// Very Low
	@SuppressWarnings("unused")
	private final int COLOR_CLUS_LOW	= 1;	// Low
	private final int COLOR_CLUS_MEDIUM	= 2;	// Medium
	@SuppressWarnings("unused")
	private final int COLOR_CLUS_HIGH	= 3;	// High
	@SuppressWarnings("unused")
	private final int COLOR_CLUS_VHIGH	= 4;	// Very High
	private int COLOR_CLUSTER	= COLOR_CLUS_MEDIUM;

	private final String HELP_PAGE_TEXT	=
			"<HTML>" +
				"<BODY>" +
					"<H1><U>Color 7</U></H1>" +
					"<H2><U>Table of Contents</U></H2>" +
						"<BLOCKQUOTE>" +
						"<A HREF=\"#Intro\">Introduction</A><BR/>" +
						"<A HREF=\"#HowToPlay\">How to Play</A><BR/>" +
						"<A HREF=\"#Credits\">Credits</A><BR/>" +
						"</BLOCKQUOTE>" +
					"<H2><A NAME=\"Intro\"><U>Introduction</U></A></H2>" +
						"<BLOCKQUOTE>" +
						"<P>Color 7 is a puzzle game playing against an AI. In the game board, there are blocks with different colors. You start at the <B>top left block</B> while AI starts at the <B>bottom right block</B>. You and the AI take turn in expanding each own territory, which is represented by your current color. To expand your territory, you <B>change the current color</B> of your territory, for which adjacent blocks with same color will become your own territory. The one who first gets <B>over 50%</B> of territory wins the game.</P>" +
						"</BLOCKQUOTE>" +
					"<H2><A NAME=\"HowToPlay\"><U>How to Play</U></A></H2>" +
						"<H3>[Basic Game Play]</H3>" +
							"<OL><LI>Choose one of the available colors by clicking on one color grid at the <B>bottom of the screen</B>. You cannot choose your current color or AI's current color (marked with an \"X\").</LI>" +
							"<LI>AI will do the same an choose an available color.</LI>" +
							"<LI>Repeat above steps until one side wins the game.</LI>" +
						"</OL>" +
						"<H3>[Other Controls]</H3>" +
						"<P><B>NEW GAME</B>: Start a new game.</P>" +
						"<P><B>HELP</B>: This help page.</P>" +
						"<P><B>Color Clustering</B>: Factor affecting the chance of occurance of a patch of adjacent blocks with same color. It applies at the time when starting a new game.</P>" +
						"<P><B>Difficulty</B>: Factor affecting the intelligence of the AI. You may change the different in the middle of the game.</P>" +
						"<P><B>Current Statistics</B> (Bottom right corner): It shows the current number of blocks and corresponding percentage of your and AI's territories.</P>" +
					"<H2><A NAME=\"Credits\"><U>Credits</U></A></H2>" +
						"<P>Game idea comes from <A HREF=\"http://en.wikipedia.org/wiki/7_Colors\">http://en.wikipedia.org/wiki/7_Colors</A></P>" +
						"<P>I just reimplement it in Java for my own fun. The first time I played this game was from my old Pocket PC running Windows CE. It is a really addictive game. Hope you have fun too.</P>" +
						"<P>Basically no guarantee, no warranty, no support, just feel free to play. Comments are welcome, those new features may not be added." +
						"<P><I>(Copyleft) 2014 K (No much concern on the copyright matters)</I></P>" +
				"</BODY>" +
			"</HTML>";

	private Board board;
	private JLabel P1StatLabel, P2StatLabel, WinLoseLabel;
	private JComboBox difficultyCombo, clusterCombo;
	private JButton newButton;
	private JButton helpButton;
	private JEditorPane helpPane;
	private JScrollPane helpSPane;
	private JFrame helpFrame;
	private JButton[] colorButtons;
	private JPanel glassPane;
	private JLabel glassPaneLabel;

	private int totalBlockCount;
	private Block[][] BOARD_ARRAY;
	private int P1Color				= -1;
	private int P2Color				= -1;
	private int playerTurn			= TURN_PLAYER;


	public Color7()
	{
		super("Color 7");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		init();
		repaint();
		System.out.println("totalBlockCount = " + totalBlockCount);
		setVisible(true);
	}


	private void init()
	{
		setSize(new Dimension(BOARD_SIZE * GRID_WIDTH + 150, (BOARD_SIZE+4) * GRID_WIDTH + 150));
		setBackground(Color.black);
		BOARD_ARRAY	= new Block[BOARD_SIZE][BOARD_SIZE];

		board	= new Board();
System.out.println("Size 0 = " + (BOARD_SIZE*GRID_WIDTH));
		board.setPreferredSize(new Dimension(BOARD_SIZE*GRID_WIDTH, BOARD_SIZE*GRID_WIDTH));
		board.setSize(new Dimension(BOARD_SIZE*GRID_WIDTH, BOARD_SIZE*GRID_WIDTH));
		board.setBackground(BOARD_BGCOLOR);

		JPanel colorPanel	= new JPanel(new GridLayout(1, GRID_COLOR.length));
		colorPanel.setOpaque(false);
		colorButtons	= new JButton[GRID_COLOR.length];
		for (int i=0; i<GRID_COLOR.length; i++)
		{
			colorButtons[i]	= new JButton("");
			colorButtons[i].setBackground(GRID_COLOR[i]);
			colorButtons[i].setPreferredSize(new Dimension(GRID_WIDTH*2, GRID_WIDTH*2));
			colorButtons[i].setMargin(new Insets(0, 0, 0, 0));
			colorButtons[i].addActionListener(this);
			colorPanel.add(colorButtons[i]);
		}

		newButton	= new JButton("NEW GAME");
		newButton.addActionListener(this);
		helpButton	= new JButton("HELP");
		helpButton.addActionListener(this);
		WinLoseLabel	= new JLabel();
		WinLoseLabel.setForeground(Color.red);
		WinLoseLabel.setFont(new Font("Dialog", Font.BOLD, 15));
		P1StatLabel	= new JLabel();
		P1StatLabel.setHorizontalAlignment(JLabel.RIGHT);
		P2StatLabel	= new JLabel();
		P2StatLabel.setHorizontalAlignment(JLabel.RIGHT);

		difficultyCombo	= new JComboBox(new String[] {"Easy", "Medium", "Hard"});
		difficultyCombo.setSelectedIndex(AI_DEPTH);
		difficultyCombo.addItemListener(this);
		JPanel difficultyPanel	= new JPanel(new BorderLayout());
		difficultyPanel.setOpaque(false);
		difficultyPanel.add(new JLabel("Difficulty: ", JLabel.RIGHT), BorderLayout.WEST);
		difficultyPanel.add(difficultyCombo, BorderLayout.EAST);

		clusterCombo	= new JComboBox(new String[] {"Very Low", "Low", "Medium", "High", "Very High"});
		clusterCombo.setSelectedIndex(COLOR_CLUSTER);
		clusterCombo.addItemListener(this);
		JPanel clusterPanel	= new JPanel(new BorderLayout());
		clusterPanel.setOpaque(false);
		clusterPanel.add(new JLabel("Color Clustering: ", JLabel.RIGHT), BorderLayout.WEST);
		clusterPanel.add(clusterCombo, BorderLayout.EAST);

		JPanel statPanel	= new JPanel(new BorderLayout());
		statPanel.setOpaque(false);
		statPanel.add(P1StatLabel, BorderLayout.NORTH);
		statPanel.add(P2StatLabel, BorderLayout.SOUTH);

		JPanel topPanel	= new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		topPanel.setOpaque(false);
		topPanel.add(newButton);
		topPanel.add(clusterPanel);
		topPanel.add(difficultyPanel);
		topPanel.add(helpButton);

		JPanel bottomPanel	= new JPanel(new BorderLayout(5, 5));
		bottomPanel.setOpaque(false);
		bottomPanel.add(colorPanel, BorderLayout.WEST);
		bottomPanel.add(WinLoseLabel, BorderLayout.CENTER);
		bottomPanel.add(statPanel, BorderLayout.EAST);

		Container c	= getContentPane();
		c.setLayout(new BorderLayout(5, 5));
		c.add(topPanel, BorderLayout.NORTH);
		c.add(board, BorderLayout.CENTER);
		c.add(bottomPanel, BorderLayout.SOUTH);
		validate();

		glassPaneLabel	= new JLabel("AI Thinking...");
		glassPaneLabel.setHorizontalAlignment(JLabel.CENTER);
		glassPaneLabel.setHorizontalTextPosition(JLabel.CENTER);
		glassPaneLabel.setFont(new Font("Dialog", Font.BOLD, 48));
		glassPaneLabel.setForeground(Color.yellow);
		glassPane	= new JPanel(new BorderLayout());
		glassPane.add(glassPaneLabel, BorderLayout.CENTER);
		glassPane.addMouseListener(new MouseAdapter() {});		// absorb all mouse events
		setGlassPane(glassPane);

		generateNewBoard();
		debugPrintBoard(BOARD_ARRAY);
	}


	private void generateNewBoard()
	{
		WinLoseLabel.setText("");
		for (int i=0; i<colorButtons.length; i++)
		{
			colorButtons[i].setText("");
			colorButtons[i].setEnabled(true);
		}

		totalBlockCount	= 0;
		Random rand	= new Random();
		for (int i=0; i<BOARD_SIZE; i++)
		{
			int len	= i < BOARD_SIZE/2 ? i*2+1 : (BOARD_SIZE-i)*2-1;
			int y0	= (BOARD_SIZE-len) / 2;
			for (int j=y0; j<y0+len; j++)
			{
				boolean clusterColor	= rand.nextInt(10) <= COLOR_CLUSTER;
				BOARD_ARRAY[i][j]	= new Block(i, j, rand.nextInt(GRID_COLOR.length));
				BOARD_ARRAY[i][j].setBoard(BOARD_ARRAY);
				if (clusterColor)
				{
					Block block1	= BOARD_ARRAY[i][j].getBlockLeft();
					Block block2	= BOARD_ARRAY[i][j].getBlockRight();
					Block block3	= BOARD_ARRAY[i][j].getBlockUp();
					Block block4	= BOARD_ARRAY[i][j].getBlockDown();
					Vector<Integer> adjacentColor	= new Vector<Integer>();
					if (block1 != null && !adjacentColor.contains(block1.getColor()))
						adjacentColor.add(block1.getColor());
					if (block2 != null && !adjacentColor.contains(block2.getColor()))
						adjacentColor.add(block2.getColor());
					if (block3 != null && !adjacentColor.contains(block3.getColor()))
						adjacentColor.add(block3.getColor());
					if (block4 != null && !adjacentColor.contains(block4.getColor()))
						adjacentColor.add(block4.getColor());
					if (adjacentColor.size() > 0)
						BOARD_ARRAY[i][j].setColor((Integer) adjacentColor.elementAt(rand.nextInt(adjacentColor.size())));
				}
			}
			totalBlockCount	+= len;
		}
		int lastX	= BOARD_SIZE-1;
		int lastY	= (BOARD_SIZE-1)/2;
		while (BOARD_ARRAY[0][lastY].getColor() == BOARD_ARRAY[lastX][lastY].getColor())
		{
			BOARD_ARRAY[lastX][lastY].setColor(rand.nextInt(GRID_COLOR.length));
		}
		BOARD_ARRAY[0][lastY].setOwner(Block.P1);
		BOARD_ARRAY[lastX][lastY].setOwner(Block.P2);
		P1Color	= BOARD_ARRAY[0][lastY].getColor();
		P2Color	= BOARD_ARRAY[lastX][lastY].getColor();
		colorButtons[P1Color].setText("X");
		colorButtons[P2Color].setText("X");
		colorButtons[P1Color].setEnabled(false);
		colorButtons[P2Color].setEnabled(false);

//		for (int i=1; i<BOARD_SIZE/2; i++)
//			BOARD_ARRAY[i][lastY].setColor(BOARD_ARRAY[0][lastY].getColor());
//		for (int j=lastY; j<BOARD_SIZE; j++)
//			BOARD_ARRAY[BOARD_SIZE/2][j].setColor(BOARD_ARRAY[0][lastY].getColor());
		playerTurn	= TURN_PLAYER;
		recalcOwner(BOARD_ARRAY);
		isEndGame();
	//	int[] stat	= recalcStat(BOARD_ARRAY);
	//	float P1Percent	= ((int) (stat[0] * 10000.00f / totalBlockCount)) / 100.00f;
	//	float P2Percent	= ((int) (stat[1] * 10000.00f / totalBlockCount)) / 100.00f;
	//	P1StatLabel.setText("You: " + stat[0] + " (" + P1Percent + "%) ");
	//	P2StatLabel.setText("AI: " + stat[1] + " (" + P2Percent + "%) ");
		board.repaint();
	}


	private void recalcOwner(Block[][] boardArray)
	{
		adjacentToOwner(boardArray);

		Vector<Block> undeterminedVector	= new Vector<Block>();
		for (int i=0; i<boardArray.length; i++)
			for (int j=0; j<boardArray[i].length; j++)
			{
				Block block	= boardArray[i][j];
				if (block == null)
					continue;
				int owner	= block.getOwner();
				if (owner != Block.P1 && owner != Block.P2)
				{
					block.setOwner(Block.UNDETERMINED);
					undeterminedVector.addElement(block);
				}
			}

//System.out.println("undeterminedVector size = " + undeterminedVector.size());
		int index	= 0;
		while (index < undeterminedVector.size())
		{
			Block block	= (Block) undeterminedVector.elementAt(index);
			if (block.getOwner() == Block.UNDETERMINED)
			{
				Vector<Block> checkingVector	= new Vector<Block>();
				int owner	= pathToOwner(block, boardArray, undeterminedVector, checkingVector);
//System.out.println("[" + block.getX() + "," + block.getY() + "] owner2 = " + owner);
//System.out.println("checkingVector size = " + checkingVector.size());
				if (owner != Block.UNDETERMINED)
				{
					Enumeration<Block> e	= checkingVector.elements();
					while (e.hasMoreElements())
					{
						Block block2	= (Block) e.nextElement();
						if (block2.getOwner() == Block.UNDETERMINED)
						{
							block2.setOwner(owner);
							if (owner == Block.P1)
								block2.setColor(P1Color);
							else if (owner == Block.P2)
								block2.setColor(P2Color);
						}
					}
				}
			}
			index++;
		}
	}


	private int[] recalcStat(Block[][] boardArray)
	{
		int P1Count	= 0;
		int P2Count	= 0;
		for (int i=0; i<boardArray.length; i++)
			for (int j=0; j<boardArray[i].length; j++)
				if (boardArray[i][j] != null)
				{
					int owner	= boardArray[i][j].getOwner();
					if (owner == Block.P1)
						P1Count++;
					else if (owner == Block.P2)
						P2Count++;
				}
		return new int[] {P1Count, P2Count};
	}


	private void adjacentToOwner(Block[][] boardArray)
	{
		boolean finished	= false;
		while (!finished)
		{
			finished	= true;
			for (int i=0; i<boardArray.length; i++)
				for (int j=0; j<boardArray[i].length; j++)
				{
					Block block	= boardArray[i][j];
					if (block == null)
						continue;
					int owner	= block.getOwner();
					if (owner == Block.P1 || owner == Block.P2)
					{
						int color	= block.getColor();

						Block block1	= block.getBlockLeft();
						if (block1 != null && block1.getColor() == color && block1.getOwner() != owner)
						{
							block1.setOwner(owner);
							finished	= false;
						}
						Block block2	= block.getBlockRight();
						if (block2 != null && block2.getColor() == color && block2.getOwner() != owner)
						{
							block2.setOwner(owner);
							finished	= false;
						}
						Block block3	= block.getBlockUp();
						if (block3 != null && block3.getColor() == color && block3.getOwner() != owner)
						{
							block3.setOwner(owner);
							finished	= false;
						}
						Block block4	= block.getBlockDown();
						if (block4 != null && block4.getColor() == color && block4.getOwner() != owner)
						{
							block4.setOwner(owner);
							finished	= false;
						}
					}
				}
		}
	}


	private int pathToOwner(Block block, Block[][] boardArray, Vector<Block> undeterminedVector, Vector<Block> checkingVector)
	{
		int owner	= block.getOwner();
		if (owner != Block.UNDETERMINED)
			return owner;

		checkingVector.addElement(block);
		boolean pathToP1	= false;
		boolean pathToP2	= false;

		Block block1	= block.getBlockUp();
		int owner1	= Block.UNDETERMINED;
		if (block1 != null && !checkingVector.contains(block1))
		{
			owner1	= pathToOwner(block1, boardArray, undeterminedVector, checkingVector);
			if (owner1 == Block.NEUTRAL)
				return owner1;
			pathToP1	= pathToP1 || owner1 == Block.P1;
			pathToP2	= pathToP2 || owner1 == Block.P2;
		}

		Block block2	= block.getBlockDown();
		int owner2	= Block.UNDETERMINED;
		if (block2 != null && !checkingVector.contains(block2))
		{
			owner2	= pathToOwner(block2, boardArray, undeterminedVector, checkingVector);
			if (owner2 == Block.NEUTRAL)
				return owner2;
			pathToP1	= pathToP1 || owner2 == Block.P1;
			pathToP2	= pathToP2 || owner2 == Block.P2;
			if (pathToP1 && pathToP2)		// have paths to both owners
				return Block.NEUTRAL;
		}

		Block block3	= block.getBlockLeft();
		int owner3	= Block.UNDETERMINED;
		if (block3 != null && !checkingVector.contains(block3))
		{
			owner3	= pathToOwner(block3, boardArray, undeterminedVector, checkingVector);
			if (owner3 == Block.NEUTRAL)
				return owner3;
			pathToP1	= pathToP1 || owner3 == Block.P1;
			pathToP2	= pathToP2 || owner3 == Block.P2;
			if (pathToP1 && pathToP2)		// have paths to both owners
				return Block.NEUTRAL;
		}

		Block block4	= block.getBlockRight();
		int owner4	= Block.UNDETERMINED;
		if (block4 != null && !checkingVector.contains(block4))
		{
			owner4	= pathToOwner(block4, boardArray, undeterminedVector, checkingVector);
			if (owner4 == Block.NEUTRAL)
				return owner4;
			pathToP1	= pathToP1 || owner4 == Block.P1;
			pathToP2	= pathToP2 || owner4 == Block.P2;
			if (pathToP1 && pathToP2)		// have paths to both owners
				return Block.NEUTRAL;
		}

		if (pathToP1)
			owner	= Block.P1;
		else if (pathToP2)
			owner	= Block.P2;
		else
			owner	= Block.UNDETERMINED;
//System.out.println("pathToOwner(" + block + ") > " + owner);
		return owner;
	}


	private int[] AIChoose(Block[][] boardArray, int color1, int color2, int turn, int depth)
	{
		int bestColor	= -1;
		int bestCount	= 0;
		for (int c=0; c<GRID_COLOR.length; c++)
		{
			if (c != color1 && c != color2 && boardHasColor(boardArray, c))
			{
				Block[][] newBoard	= cloneBoard(boardArray);
				for (int i=0; i<newBoard.length; i++)
					for (int j=0; j<newBoard[i].length; j++)
						if (newBoard[i][j] != null && newBoard[i][j].getOwner() == turn)
							newBoard[i][j].setColor(c);
				recalcOwner(newBoard);

				int[] result2	= recalcStat(newBoard);
				if (depth >= AI_DEPTH || result2[0]+result2[1] >= totalBlockCount)
				{
					if (result2[1] > bestCount)
					{
						bestColor	= c;
						bestCount	= result2[1];
					}
				}
				else
				{
					int[] result	= AIChoose(newBoard, turn == TURN_PLAYER ? c : color1, turn == TURN_COMPUTER ? c : color2, turn == TURN_PLAYER ? TURN_COMPUTER : TURN_PLAYER, turn == TURN_PLAYER ? depth+1 : depth);
					if (result[1] > bestCount)
					{
						bestColor	= c;
						bestCount	= result[1];
					}
				}
			}
		}
//System.out.println("AIChoose(, " + (turn == TURN_PLAYER ? "P" : "C") + ", " + depth + ", " + COLOR_NAME[color1] + ", " + COLOR_NAME[color2] + ") = {" + COLOR_NAME[bestColor] + ", " + bestCount + "}");
		return new int[] {bestColor, bestCount};
	}


	private void AIThinkingEnd()
	{
		glassPane.setCursor(Cursor.getDefaultCursor());
		glassPane.setVisible(false);
		if (isEndGame())
			return;
		playerTurn	= TURN_PLAYER;
	}


	private void AIThinkingStart()
	{
		glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		glassPane.setVisible(true);

		BufferedImage bimg	= new BufferedImage(getContentPane().getWidth(), getContentPane().getHeight(), BufferedImage.TYPE_INT_RGB);
		getContentPane().paint(bimg.getGraphics());
		RescaleOp rescale	= new RescaleOp(0.8f, 0f, null);
		rescale.filter(bimg, bimg);
		glassPaneLabel.setIcon(new ImageIcon(bimg));
		new Thread(new AIThinkRunnable()).start();
	}


	private boolean boardHasColor(Block[][] boardArray, int color)
	{
		for (int i=0; i<boardArray.length; i++)
			for (int j=0; j<boardArray[i].length; j++)
				if (boardArray[i][j] != null && boardArray[i][j].getColor() == color)
					return true;
		return false;
	}


	private Block[][] cloneBoard(Block[][] boardArray)
	{
		Block[][] newBoard	= new Block[boardArray.length][];
		for (int i=0; i<boardArray.length; i++)
		{
			newBoard[i]	= new Block[boardArray[i].length];
			for (int j=0; j<boardArray[i].length; j++)
				if (boardArray[i][j] != null)
				{
					newBoard[i][j]	= (Block) boardArray[i][j].clone();
					newBoard[i][j].setBoard(newBoard);
				}
		}
		return newBoard;
	}


	private void chooseColor(int color, int turn)
	{
		int owner	= turn == TURN_PLAYER ? Block.P1 : Block.P2;
		if (turn == TURN_PLAYER)
		{
			colorButtons[P1Color].setText("");
			colorButtons[P1Color].setEnabled(true);
			P1Color	= color;
		}
		else
		{
			colorButtons[P2Color].setText("");
			colorButtons[P2Color].setEnabled(true);
			P2Color	= color;
		}
		colorButtons[color].setText("X");
		colorButtons[color].setEnabled(false);
		for (int i=0; i<BOARD_SIZE; i++)
			for (int j=0; j<BOARD_SIZE; j++)
				if (BOARD_ARRAY[i][j] != null && BOARD_ARRAY[i][j].getOwner() == owner)
				{
					BOARD_ARRAY[i][j].setColor(color);
				}

		recalcOwner(BOARD_ARRAY);
		board.repaint();
	}


	private boolean isEndGame()
	{
	//	recalcStat(BOARD_ARRAY);
		int[] stat	= recalcStat(BOARD_ARRAY);
		float P1Percent	= ((int) (stat[0] * 10000.00f / totalBlockCount)) / 100.00f;
		float P2Percent	= ((int) (stat[1] * 10000.00f / totalBlockCount)) / 100.00f;
		System.out.println("P1 = " + stat[0] + "/" + totalBlockCount + " = " + P1Percent + "; P2 = " + stat[1] + "/" + totalBlockCount + " = " + P2Percent);
		P1StatLabel.setText("P1: " + stat[0] + " (" + P1Percent + "%)");
		P2StatLabel.setText("P2: " + stat[1] + " (" + P2Percent + "%)");
		if (P1Percent > 50.0f || P2Percent > 50.0f || stat[0]+stat[1] >= totalBlockCount)
		{
			if (P1Percent > 50.0f)
			{
				WinLoseLabel.setText("You Win!");
				JOptionPane.showMessageDialog(this, "You Win!");
			}
			else if (P2Percent > 50.0f)
			{
				WinLoseLabel.setText("You Lose!");
				JOptionPane.showMessageDialog(this, "You Lose!");
			}
			else
			{
				WinLoseLabel.setText("A tie.");
				JOptionPane.showMessageDialog(this, "A tie.");
			}

			for (int i=0; i<colorButtons.length; i++)
				colorButtons[i].setEnabled(false);
			return true;
		}
		return false;
	}


	private void debugPrintBoard(Block[][] board)
	{
		for (int i=0; i<board.length; i++)
		{
			for (int j=0; j<board[i].length; j++)
			{
				if (board[i][j] == null)
					System.out.print(" N");
				else
					System.out.print(" "+board[i][j].getColor());
			}
			System.out.println();
		}
	}


	public void actionPerformed(ActionEvent aevt)
	{
		Object src	= aevt.getSource();
		if (src == newButton)
		{
			if (WinLoseLabel.getText().equals(""))
			{
				int ans	= JOptionPane.showConfirmDialog(this, new String[] {"This game is in progress.", "Do you want to start a new game?"}, "New Game", JOptionPane.YES_NO_OPTION);
				if (ans != JOptionPane.YES_OPTION)
					return;
			}
			generateNewBoard();
		}
		else if (src == helpButton)
		{
			if (helpFrame == null || !helpFrame.isShowing())
			{
				helpPane	= new JEditorPane();
				helpPane.setEditable(false);
				helpPane.setContentType("text/html");
				helpPane.setText(HELP_PAGE_TEXT);
				helpPane.addHyperlinkListener(this);

				helpSPane	= new JScrollPane(helpPane,
						JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				helpSPane.setPreferredSize(getSize());

				helpFrame	= new JFrame("Color 7: Help");
				helpFrame.getContentPane().add(helpSPane);
				helpFrame.pack();
				helpFrame.setLocationRelativeTo(this);
			}
			helpPane.setCaretPosition(0);
//			helpPane.scrollRectToVisible(new Rectangle(0, 0));
			helpFrame.setVisible(true);
		}
		else
		{
			for (int c=0; c<colorButtons.length; c++)
				if (src == colorButtons[c])
				{
					chooseColor(c, playerTurn);
					if (isEndGame())
						return;

					playerTurn	= playerTurn == TURN_PLAYER ? TURN_COMPUTER : TURN_PLAYER;
					if (playerTurn == TURN_COMPUTER)
					{
						AIThinkingStart();
					}
					return;
				}
		}
	}


	public void hyperlinkUpdate(HyperlinkEvent e)
	{
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
		{
			try
			{
				URL url	= e.getURL();
				System.out.println(url);
				if (url != null)
				{
					if (Desktop.isDesktopSupported())
					{
						Desktop.getDesktop().browse(url.toURI());
					}
					else
					{
						helpPane.setPage(url);
					}
				}
				else
				{
					String desc	= e.getDescription();
					if (desc != null && desc.startsWith("#"))
					{
						helpPane.scrollToReference(desc.substring(1));
					}
				}
			} catch (IOException ex)	{
			} catch (URISyntaxException ex)	{ }
		}
	}


	public void itemStateChanged(ItemEvent ievt)
	{
		Object src	= ievt.getSource();
		if (src == difficultyCombo && ievt.getStateChange() == ItemEvent.SELECTED)
		{
			AI_DEPTH	= difficultyCombo.getSelectedIndex();
		}
		else if (src == clusterCombo && ievt.getStateChange() == ItemEvent.SELECTED)
		{
			COLOR_CLUSTER	= clusterCombo.getSelectedIndex();
		}
	}


	public static void main(String[] args)
	{
		new Color7();
	}


//--------------------------------------------------------------------------//


	class Board extends JPanel
	{

		/**
		 * 
		 */
		private static final long	serialVersionUID	= 1L;


		public void paint(Graphics g)
		{
			Graphics2D g2d	= (Graphics2D) g;
			g2d.setColor(getBackground());
			double boardWidth	= getWidth();
			double boardHeight	= getHeight();
			g2d.fillRect(0, 0, (int) boardWidth, (int) boardHeight);
			g2d.setStroke(new BasicStroke(BORDER_WIDTH));

			final double SQRT2	= Math.sqrt(2);
			final double HALF_SQRT2	= SQRT2/2;
			double halfBoardSize	= (BOARD_SIZE+1) / 2;
			double blockWidth	= Math.min(boardWidth-20, boardHeight-20) / halfBoardSize / SQRT2;
			double x0	= (boardWidth-BOARD_SIZE*blockWidth*SQRT2)/2;
			double y0	= boardHeight/2;

			double x1	= x0;
			double y1	= y0;
			for (int i=0; i<BOARD_ARRAY.length; i++)
			{
				for (int j=0; j<BOARD_ARRAY[i].length; j++)
				{
					if (BOARD_ARRAY[i][j] != null)
					{
						x1	= x0 + (i+j)*HALF_SQRT2*blockWidth;
						y1	= y0 + (i-j)*HALF_SQRT2*blockWidth;
						Path2D.Double block	= new Path2D.Double();
						block.moveTo(x1-HALF_SQRT2*blockWidth+2, y1);
						block.lineTo(x1, y1-HALF_SQRT2*blockWidth+2);
						block.lineTo(x1+HALF_SQRT2*blockWidth-2, y1);
						block.lineTo(x1, y1+HALF_SQRT2*blockWidth-2);
						block.closePath();

						g2d.setColor(GRID_COLOR[BOARD_ARRAY[i][j].getColor()]);
						g2d.fill(new Area(block));
						g2d.setColor(Color.black);
						g2d.draw(block);
//						g2d.drawString(i+","+j+","+GRID_COLOR[BOARD_ARRAY[i][j].getColor()], (float) x1, (float) y1);
//						return;
					}
				}
			}
		}


		public void paint2(Graphics g)
		{
			Graphics2D g2d	= (Graphics2D) g;
			g2d.setColor(getBackground());
			double w0	= getWidth();
			double h0	= getHeight();
//System.out.println("Size 1 = " + w0 + "x" + h0);
			g2d.fillRect(0, 0, (int) w0, (int) h0);
			g2d.rotate(-Math.PI/4, w0/2, h0/2);

			double factor	= BOARD_SIZE;
			double w1	= Math.min(w0, h0) / factor;
			double x0	= w0 - w1*factor;
			double y0	= h0 - w1*factor;
//System.out.println("x0,y0,w1 = " + x0 + ", "+y0 + ", " + w1);
//System.out.println("w0,h0 = " + w0 + "x"+h0);
			double x1	= x0 + 0.5*w1;
			for (int i=0; i<BOARD_ARRAY.length; i++)
			{
//				double x1	= x0 + (i+0.5) * w1;
				double y1	= y0;
				for (int j=0; j<BOARD_ARRAY[i].length; j++)
				{
					if (BOARD_ARRAY[i][j] != null)
					{
						paintGrid(g2d, x1, y1, w1, GRID_COLOR[BOARD_ARRAY[i][j].getColor()], Integer.toString(BOARD_ARRAY[i][j].getOwner()));
					}
					y1	+= w1;
				}
				x1	+= w1;
			}
			g2d.rotate(Math.PI/4, w0/2, h0/2);
		}	// paint()


		private void paintGrid(Graphics2D g2d, double x, double y, double w, Color c, String text)
		{
			Shape s1	= new Rectangle2D.Double(x-w/2, y-w/2, w, w);
			Shape s2	= new Rectangle2D.Double(x-w/2+BORDER_WIDTH, y-w/2+BORDER_WIDTH, w-BORDER_WIDTH*2, w-BORDER_WIDTH*2);
			g2d.setColor(Color.black);
			g2d.fill(s1);
			g2d.setColor(c);
			g2d.fill(s2);
			g2d.setColor(Color.black);
		}

	}

//--------------------------------------------------------------------------//
	class AIThinkRunnable implements Runnable
	{
		public void run()
		{
			long startMs	= System.currentTimeMillis();
			int[] result	= AIChoose(cloneBoard(BOARD_ARRAY), P1Color, P2Color, playerTurn, 0);
			long endMs	= System.currentTimeMillis();
			System.out.println("AIChoose = " + COLOR_NAME[result[0]] + "/" + result[1] + " [Time = " + (endMs - startMs) + "ms]");
			chooseColor(result[0], playerTurn);
			AIThinkingEnd();
		}
	}

//--------------------------------------------------------------------------//

	class Block
	{
		public static final int UNDETERMINED= 0;
		public static final int P1			= 1;
		public static final int P2			= 2;
		public static final int NEUTRAL		= 3;

		private Block[][] boardArray;
		private int color;
		private int owner;
		private int x;
		private int y;

		public Block(int x, int y, int color)
		{
			this.x		= x;
			this.y		= y;
			this.color	= color;
			owner		= UNDETERMINED;
		}


		public Object clone()
		{
			Block block	= new Block(x, y, color);
			block.setOwner(owner);
			block.setBoard(boardArray);
			return block;
		}


		public Block getBlockDown()		{	return y < BOARD_SIZE-1 ? boardArray[x][y+1] : null;	}
		public Block getBlockLeft()		{	return x > 0 ? boardArray[x-1][y] : null;	}
		public Block getBlockRight()	{	return x < BOARD_SIZE-1 ? boardArray[x+1][y] : null;	}
		public Block getBlockUp()		{	return y > 0 ? boardArray[x][y-1] : null;	}
		public int getColor()	{	return color;	}
		public int getOwner()	{	return owner;	}
		public int getX()		{	return x;		}
		public int getY()		{	return y;		}


		public void setBoard(Block[][] boardArray)		{	this.boardArray	= boardArray;	}
		public void setColor(int color)	{	this.color	= color;	}
		public void setOwner(int owner)	{	this.owner	= owner;	}
		public void setX(int x)			{	this.x		= x;		}
		public void setY(int y)			{	this.y		= y;		}

		public String toString()
		{
			return "Block[x=" + x + ",y=" + y + ",owner=" + owner + ",color=" + color + "]";
		}
	}

}
