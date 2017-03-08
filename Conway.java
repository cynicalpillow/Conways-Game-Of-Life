import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class Conway{

	/* 
	 * Conway's game of life
	 * By Rui Li
	 * /////////////////////
	 * Key bindings:
	 *
	 * E -- changes pen from erase to draw (blue is drawing, red is erasing)
	 * X -- clears board
	 * Spacebar -- starts/stops simulation
	 * R -- rotates presets (only for glider currently)
	 *
	 * 1 -- is default drawing
	 * 2 -- Glider preset
	 * 3 -- Glider gun preset
	 * 4 -- Pulsar preset
	 */

	static JFrame f;
	static DrawPanel p;
	static int width = 200;
	static int height = 100;
	static int size = 6;
	static boolean[][] board;
	static boolean run = false;
	static int tick = 30;
	static ArrayList<Point> currMousePos = new ArrayList<>();
	static int selectPresets = 1;
	static Point presetPreview;
	static int rotation = 0;
	static boolean erase = false;

	//Initialization
	/*Swing initialization*/
	public static void init(){
		f = new JFrame();
		p = new DrawPanel();
		initBoard();
		
		p.addKeyListener(p);
		p.addMouseListener(new MouseEventListener());
		p.addMouseMotionListener(new MotionListener());
		f.setSize(width*size, height*size);
		p.setFocusable(true);
		f.add(p);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	/*Board initialization*/
	public static void initBoard(){
		board = new boolean[height][width];
	}
	//Preset generation patterns
	public static void generateGliderGun(int startx, int starty){
		if(startx + 36 < width && starty + 9 < height){
			//Generate first square
			board[starty+5][startx+1] = true;
			board[starty+5][startx+2] = true;
			board[starty+6][startx+1] = true;
			board[starty+6][startx+2] = true;
			//Generate second square
			board[starty+3][startx+35] = true;
			board[starty+3][startx+36] = true;
			board[starty+4][startx+35] = true;
			board[starty+4][startx+36] = true;
			//Gun
			board[starty+1][startx+25] = true;
			board[starty+2][startx+25] = true;

			board[starty+6][startx+25] = true;
			board[starty+7][startx+25] = true;

			board[starty+2][startx+23] = true;
			board[starty+6][startx+23] = true;

			board[starty+3][startx+22] = true;
			board[starty+4][startx+22] = true;
			board[starty+5][startx+22] = true;

			board[starty+3][startx+21] = true;
			board[starty+4][startx+21] = true;
			board[starty+5][startx+21] = true;

			//Other gun
			board[starty+6][startx+18] = true;

			board[starty+5][startx+17] = true;
			board[starty+6][startx+17] = true;
			board[starty+7][startx+17] = true;

			board[starty+4][startx+16] = true;
			board[starty+8][startx+16] = true;

			board[starty+6][startx+15] = true;

			board[starty+3][startx+14] = true;
			board[starty+9][startx+14] = true;
			board[starty+3][startx+13] = true;
			board[starty+9][startx+13] = true;
			board[starty+4][startx+12] = true;
			board[starty+8][startx+12] = true;
			board[starty+5][startx+11] = true;
			board[starty+6][startx+11] = true;
			board[starty+7][startx+11] = true;
		}
	}
	public static void generateGlider(int startx, int starty){
		if(startx + 2 < width && starty + 2 < height){
			if(rotation == 0){
				board[starty][startx+2] = true;
				board[starty+1][startx+2] = true;
				board[starty+2][startx+2] = true;
				board[starty+2][startx+1] = true;
				board[starty+1][startx] = true;
			} else if(rotation == 1){
				board[starty][startx+1] = true;
				board[starty+1][startx] = true;
				board[starty+2][startx] = true;
				board[starty+2][startx+1] = true;
				board[starty+2][startx+2] = true;
			} else if(rotation == 2){
				board[starty][startx] = true;
				board[starty+1][startx] = true;
				board[starty+2][startx] = true;
				board[starty][startx+1] = true;
				board[starty+1][startx+2] = true;
			} else if(rotation == 3){
				board[starty][startx] = true;
				board[starty][startx+1] = true;
				board[starty][startx+2] = true;
				board[starty+1][startx+2] = true;
				board[starty+2][startx+1] = true;
			}
		}
	}
	public static void generatePulsar(int startx, int starty){
		//Left top block
		board[starty][startx+2] = true;
		board[starty][startx+3] = true;
		board[starty][startx+4] = true;
		board[starty+2][startx] = true;
		board[starty+3][startx] = true;
		board[starty+4][startx] = true;
		board[starty+5][startx+2] = true;
		board[starty+5][startx+3] = true;
		board[starty+5][startx+4] = true;
		board[starty+4][startx+5] = true;
		board[starty+3][startx+5] = true;
		board[starty+2][startx+5] = true;
		//Right top block
		board[starty][startx+8] = true;
		board[starty][startx+9] = true;
		board[starty][startx+10] = true;
		board[starty+2][startx+7] = true;
		board[starty+3][startx+7] = true;
		board[starty+4][startx+7] = true;
		board[starty+5][startx+8] = true;
		board[starty+5][startx+9] = true;
		board[starty+5][startx+10] = true;
		board[starty+4][startx+12] = true;
		board[starty+3][startx+12] = true;
		board[starty+2][startx+12] = true;
		//Left bottom block
		board[starty+7][startx+2] = true;
		board[starty+7][startx+3] = true;
		board[starty+7][startx+4] = true;
		board[starty+8][startx+5] = true;
		board[starty+9][startx+5] = true;
		board[starty+10][startx+5] = true;
		board[starty+8][startx] = true;
		board[starty+9][startx] = true;
		board[starty+10][startx] = true;
		board[starty+12][startx+2] = true;
		board[starty+12][startx+3] = true;
		board[starty+12][startx+4] = true;
		//Right bottom block
		board[starty+7][startx+8] = true;
		board[starty+7][startx+9] = true;
		board[starty+7][startx+10] = true;
		board[starty+8][startx+7] = true;
		board[starty+9][startx+7] = true;
		board[starty+10][startx+7] = true;
		board[starty+8][startx+12] = true;
		board[starty+9][startx+12] = true;
		board[starty+10][startx+12] = true;
		board[starty+12][startx+8] = true;
		board[starty+12][startx+9] = true;
		board[starty+12][startx+10] = true;
	}
	//Update methods
	public static void update(){
		boolean[][] result = new boolean[height][width];
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				int count = 0;
				if(i-1 >= 0 && board[i-1][j])count++;
				if(i+1 < height && board[i+1][j])count++;
				if(j-1 >= 0 && board[i][j-1])count++;
				if(j+1 < width && board[i][j+1])count++;
				//check diagonals
				if(i-1 >= 0 && j-1 >= 0 && board[i-1][j-1])count++;
				if(i-1 >= 0 && j+1 < width && board[i-1][j+1])count++;
				if(i+1 < height && j-1 >= 0 && board[i+1][j-1])count++;
				if(i+1 < height && j+1 < width && board[i+1][j+1])count++;
				//create new
				if(board[i][j] && count >= 2 && count <= 3)result[i][j] = true;
				if(!board[i][j] && count == 3)result[i][j] = true;
			}
		}
		board = result;
		repaintPanel();
		try{
			Thread.sleep(tick);
		} catch (Exception e){}
	}
	public static void updateBlock(){
		for(Point z : currMousePos){
			boolean found = false;
			for(int i = 0; i < height && !found; i++){
				for(int j = 0; j < width && !found; j++){
					if(new Rectangle(j*size, i*size, size, size).contains(z)){
						if(!erase)board[i][j] = true;
						else board[i][j] = false;
						repaintPanel();
						found = true;
					}
				}
			}
		}
		currMousePos.clear();
	}
	//Find corresponding block
	public static int[] findBlock(Point p){
		int[] coord = new int[2];
		coord[0] = -1;
		coord[1] = -1;
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				if(new Rectangle(j*size, i*size, size, size).contains(p)){
					coord[1] = i;
					coord[0] = j;
					return coord;
				}
			}
		}
		return coord;
	}
	//Swing components
	static class DrawPanel extends JPanel implements KeyListener{
		public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.darkGray);
            for(int i = 0; i < height; i++){
				for(int j = 0; j < width; j++){
					if(board[i][j])g.fillRect(j*size, i*size, size, size);
				}
			}
			if(presetPreview != null){
				//Preset previewing
				if(selectPresets == 2){
					Color selection = new Color(242, 121, 0, 127);
					g.setColor(selection);
					int[] coord = findBlock(presetPreview);
					for(int i = coord[1]; i <= coord[1]+2 && i < height; i++){
						for(int j = coord[0]; j <= coord[0]+2 && j < width; j++){
							g.fillRect(j*size, i*size, size, size);
						}
					}
				} else if(selectPresets == 3){
					Color selection = new Color(242, 121, 0, 127);
					g.setColor(selection);
					int[] coord = findBlock(presetPreview);
					for(int i = coord[1]; i <= coord[1]+9 && i < height; i++){
						for(int j = coord[0]; j <= coord[0]+36 && j < width; j++){
							g.fillRect(j*size, i*size, size, size);
						}
					}
				} else if(selectPresets == 4){
					Color selection = new Color(242, 121, 0, 127);
					g.setColor(selection);
					int[] coord = findBlock(presetPreview);
					for(int i = coord[1]; i <= coord[1]+12 && i < height; i++){
						for(int j = coord[0]; j <= coord[0]+12 && j < width; j++){
							g.fillRect(j*size, i*size, size, size);
						}
					}
				} else {
					//Erase
					if(erase){
						g.setColor(Color.red);
						int[] coord = findBlock(presetPreview);
						g.fillRect(coord[0]*size, coord[1]*size, size, size);
					} else {
						g.setColor(Color.blue);
						int[] coord = findBlock(presetPreview);
						g.fillRect(coord[0]*size, coord[1]*size, size, size);
					}
				}
			}
        }
      	public void keyPressed(KeyEvent e){
      		if(e.getKeyCode() == KeyEvent.VK_SPACE){
      			if(run)run = false;
      			else run = true;
      		} else if(e.getKeyCode() == KeyEvent.VK_R){
      			rotation++;
      			rotation%=4;
      		} else if(e.getKeyCode() == KeyEvent.VK_E){
      			if(erase)erase = false;
      			else erase = true;
      			repaintPanel();
      		} else if(e.getKeyCode() == KeyEvent.VK_X){
      			initBoard();
      			repaintPanel();
      		} else if(e.getKeyCode() == KeyEvent.VK_1){
      			selectPresets = 1;
      			repaintPanel();
      		} else if(e.getKeyCode() == KeyEvent.VK_2){
      			selectPresets = 2;
      			repaintPanel();
      		} else if(e.getKeyCode() == KeyEvent.VK_3){
      			selectPresets = 3;
      			repaintPanel();
      		} else if(e.getKeyCode() == KeyEvent.VK_4){
      			selectPresets = 4;
      			repaintPanel();
      		}
      	}
	    public void keyReleased(KeyEvent e){}
	    public void keyTyped(KeyEvent e){}
	}
	static class MouseEventListener implements MouseListener {
		public void mousePressed(MouseEvent e) {
			if(selectPresets == 1){
				currMousePos.add(e.getPoint());
				updateBlock();
			} else if(selectPresets == 2){
				int[] coord = findBlock(e.getPoint());
				if(coord[0] != -1 && coord[1] != -1){
					generateGlider(coord[0], coord[1]);
					repaintPanel();
				}
			} else if(selectPresets == 3){
				int[] coord = findBlock(e.getPoint());
				if(coord[0] != -1 && coord[1] != -1){
					generateGliderGun(coord[0], coord[1]);
					repaintPanel();
				}
			} else if(selectPresets == 4){
				int[] coord = findBlock(e.getPoint());
				if(coord[0] != -1 && coord[1] != -1){
					generatePulsar(coord[0], coord[1]);
					repaintPanel();
				}
			}
	    }
	    public void mouseReleased(MouseEvent e) {
	    }
	    public void mouseEntered(MouseEvent e) {
	    }
	    public void mouseExited(MouseEvent e) {
	    }
	    public void mouseClicked(MouseEvent e) {
	    }
	}
	static class MotionListener implements MouseMotionListener {
		public void mouseDragged(MouseEvent e){
			if(selectPresets == 1){
				presetPreview = e.getPoint();
				if(selectPresets > 1)repaintPanel();
				else {
					currMousePos.add(e.getPoint());
					updateBlock();
				}
			}
		}
		public void mouseMoved(MouseEvent e){
			presetPreview = e.getPoint();
			repaintPanel();
		}
	}
	public static void repaintPanel(){
		f.getToolkit().sync();
		p.repaint();
	}
	public static void main(String args[]){
		init();
		while(true){
			while(run)update();
			try{
				Thread.sleep(1);
			} catch (Exception e){}
		}
	}
}