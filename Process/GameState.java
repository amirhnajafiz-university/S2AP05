package game.Process;

import game.Control.LocationController;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;

/**
 * This class holds the state of game and all of its elements.
 * This class also handles user inputs, which affect the game state.
 * 
 * @author Seyed Mohammad Ghaffarian
 */
public class GameState implements Serializable {

	private int mapRowsLimit, mapColsLimit; // This is the map limits
	
	public int locX, locY, diam;
	public boolean gameOver;
	public static int speed = 4;
	private int currentDirection; // This is the last rotation degree
	private long lastShotTime;
	private int turnPassed; // This is a simple counter we need for shots fired
	public boolean shotFired, waitForSecond; // These booleans are for the shots

	private boolean keyUP, keyDOWN, keyRIGHT, keyLEFT;
	private boolean mousePress;
	private int mouseX, mouseY;	
	private KeyHandler keyHandler;
	private MouseHandler mouseHandler;
	
	public GameState() {
		diam = 32;
		gameOver = false;
		currentDirection = 0;
		//
		keyUP = false;
		keyDOWN = false;
		keyRIGHT = false;
		keyLEFT = false;
		//
		mousePress = false;
		mouseX = 0;
		mouseY = 0;
		lastShotTime = 1000;
		//
		keyHandler = new KeyHandler();
		mouseHandler = new MouseHandler();
	}

	/**
	 * A setter method for setting the locations.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public void setLocation (int x, int y) {
		locX = x;
		locY = y;
	}

	/**
	 * This is a method for setting the borders
	 * limits.
	 *
	 * @param mapRows the rows limit
	 * @param mapCols the cols limit
	 */
	public void setLimits(int mapRows, int mapCols) {
		mapRowsLimit = mapRows;
		mapColsLimit = mapCols;
	}
	
	/**
	 * The method which updates the game state.
	 */
	public void update() {

		// The second bullet fire
		shotFired = false;
		if (turnPassed == 3 && waitForSecond) {
			shotFired = true;
			waitForSecond = false;
		}

		if (mousePress)
		{
			int oldY = locY; // Keeping the old coordinates
			int oldX = locX;
			int speedHolder = GameState.speed;

			if (Math.pow(Math.abs(locY - mouseY), 2) + (long) Math.pow(Math.abs(locX - mouseX), 2) > 2 * Math.pow(10, 4))
				GameState.speed *= 2; // The new speed based on the distance from mouse

			int bound = locY - mouseY;
			locY = (Math.abs(bound) > GameState.speed) ? (locY > mouseY) ? locY - GameState.speed : locY + GameState.speed : mouseY;
			if(!LocationController.check(locX, locY))
				locY = oldY;

			bound = locX - mouseX;
			locX = (Math.abs(bound) > GameState.speed) ? (locX > mouseX) ? locX - GameState.speed : locX + GameState.speed : mouseX;
			if(!LocationController.check(locX, locY))
				locX = oldX;

			GameState.speed = speedHolder; // Resetting the game speed
		}

		if (keyUP)
		{
			currentDirection -= 5;
		}
		if (keyDOWN)
		{
			currentDirection += 5;
		}

		TankLine.setTheta(currentDirection);

		if (keyLEFT)
		{
			TankLine.solveTheorem(-1);
		}
		if (keyRIGHT)
		{
			TankLine.solveTheorem(-1);
		}
		if (LocationController.check(locX + (int)TankLine.x, locY + (int)TankLine.y)) {
			locY = locY + (int)TankLine.y;
			locX = locX + (int)TankLine.x;
		}

		locX = Math.max(locX, GameFrame.DRAWING_START_X); // Setting the new locations based on the limits
		locX = Math.min(locX, mapColsLimit * GameMap.CHANGING_FACTOR - GameMap.CHANGING_FACTOR / 2 + GameFrame.DRAWING_START_X);
		locY = Math.max(locY, GameFrame.DRAWING_START_Y);
		locY = Math.min(locY, mapRowsLimit * GameMap.CHANGING_FACTOR - GameMap.CHANGING_FACTOR / 2 + GameFrame.DRAWING_START_Y);
		turnPassed++; // Need to increase counter
	}

	/**
	 * This method will check the control buttons
	 * and will send the rotation degree as int.
	 *
	 * @return the rotation
	 */
	public int direction () {
		return currentDirection; // If no move is made
	}

	/*
	 * this method is the same as the direction method
	 * but it has different parameters to compare the
	 * current place and the mouse place to chose the direction.
	 *
	 */
	private int mouseDirection () {
		if (mouseX < locX) {
			if (mouseY < locY) {
				currentDirection = 225;
				return 225;
			}
			if (mouseY > locY) {
				currentDirection = 135;
				return 135;
			}
			else  {
				currentDirection = 180;
				return 180;
			}
		} else if (mouseX > locX) {
			if (mouseY < locY) {
				currentDirection = 315;
				return 315;
			}
			if (mouseY > locY) {
				currentDirection = 45;
				return 45;
			} else {
				currentDirection = 0;
				return 0;
			}
		} else {
			if (mouseY < locY) {
				currentDirection = 270;
				return 270;
			}
			if (mouseY > locY) {
				currentDirection = 90;
				return 90;
			} else
			return currentDirection;
		}
	}

	public KeyListener getKeyListener() {
		return keyHandler;
	}
	public MouseListener getMouseListener() {
		return mouseHandler;
	}
	public MouseMotionListener getMouseMotionListener() {
		return mouseHandler;
	}

	/**
	 * The keyboard handler.
	 */
	class KeyHandler extends KeyAdapter implements Serializable {

		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode())
			{
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W:
					keyUP = true;
					break;
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S:
					keyDOWN = true;
					break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A:
					keyLEFT = true;
					break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D:
					keyRIGHT = true;
					break;
				case KeyEvent.VK_ESCAPE:
					gameOver = true;
					break;
				case KeyEvent.VK_SPACE:
					takeAShot();
					break;
			}
		}

		/*
			This method limits the times between each shot
			fired.
		 */
		private void takeAShot () {
			long time = System.currentTimeMillis();
			if ((time - lastShotTime) / 1000 > 1) {
				lastShotTime = time;
				shotFired = true;
				turnPassed = 0;
				waitForSecond = true;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode())
			{
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W:
					keyUP = false;
					break;
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S:
					keyDOWN = false;
					break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A:
					keyLEFT = false;
					break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D:
					keyRIGHT = false;
					break;
			}
		}

	}

	/**
	 * The mouse handler.
	 */
	class MouseHandler extends MouseAdapter implements Serializable {

		@Override
		public void mousePressed(MouseEvent e) {
			mouseX = e.getX() - GameMap.CHANGING_FACTOR / 4; // This is for placing the mouse
			mouseY = e.getY() - GameMap.CHANGING_FACTOR / 4; // at the center of the shape
			mousePress = true;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			mousePress = false;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			mouseX = e.getX() - GameMap.CHANGING_FACTOR / 4;
			mouseY = e.getY() - GameMap.CHANGING_FACTOR / 4;
		}
	}
}