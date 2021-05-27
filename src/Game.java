package src;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import game2D.*;

// By 2717790

// Game demonstrates how we can override the GameCore class
// to create our own 'game'. We usually need to implement at
// least 'draw' and 'update' (not including any local event handling)
// to begin the process. You should also add code to the 'init'
// method that will initialise event handlers etc. By default GameCore
// will handle the 'Escape' key to quit the game but you should
// override this with your own event handler.

/**
 * @author David Cairns
 *
 */
@SuppressWarnings("serial")

public class Game extends GameCore {
	// Useful game constants
	static int screenWidth = 512;
	static int screenHeight = 384;

	// TODO music loop
	// TODO fix bounceback code
	// TODO make sprites not bounce when flipped - leave till last
	// TODO fix sprite moving right when attacking and facing left - leave till last
	// TODO Background parallax - make not scroll when wall + add layers
	// TODO Somehow make the game screen bigger - making sprites bigger too - last
	// TODO Refactor

	float lift = 0.005f;
	float gravity = 0.0005f;

	// Game resources
	boolean cheat = false;
	boolean musicPlaying = false;
	boolean paused = false;

	// X and Y positions for enemies to spawn in on map 1 and map 2.
	float[] positionsX1 = { 356, 1127, 1669 };
	float[] positionsY1 = { 287, 319, 319 };

	float[] positionsX2 = { 356, 1128, 1537 };
	float[] positionsY2 = { 287, 318, 319 };

	// All animations used.
	Animation pIdle;
	Animation pWalk;
	Animation pAttack;
	Animation pDead;
	Animation eIdle;
	Animation eRun;
	Animation portalA;
	Animation pButton;
	Animation parallaxMountain;

	// All sprites used.
	GameCharacter player = null;
	Sprite bgMountain;
	Sprite portal;
	Sprite play;
	ArrayList<GameCharacter> enemies = new ArrayList<GameCharacter>();

	// setting the maps, 3 = main menu, 1 is map 1, 2 is map 2.
	int map = 3;

	TileMap tmap = new TileMap(); // Our tile map, note that we load it in init()

	// background image
	Image background;

	int offSetY = 0;
	int offSetX = 0;

	/**
	 * The obligatory main method that creates an instance of our class and starts
	 * it running
	 * 
	 * @param args The list of parameters this program might use (ignored)
	 */
	public static void main(String[] args) {

		Game gct = new Game();
		gct.setTitle("Skeleton");
		gct.init();
		// Start in windowed mode with the given screen height and width
		gct.setSize(screenWidth, screenHeight);
		gct.run(false, screenWidth, screenHeight);
	}

	/**
	 * Initialise the class, e.g. set up variables, load images, create animations,
	 * register event handlers
	 */
	public void init() {
		GameCharacter s; // Temporary reference to a sprite

		// Load the tile map and print it out so we can check it is valid
		// Load the tile map based on what level it is, load map 1 as a backup.
		if (map == 1) {
			tmap.loadMap("src/maps", "map.txt");
		} else if (map == 2) {
			tmap.loadMap("src/maps", "map2.txt");
		} else {

			tmap.loadMap("src/maps", "map.txt");

		}

		// Stops the music from playing over itself every time the game is re
		// initalised.
		// Comment out this entire if to hear the swing sound filter because the music
		// is really loud.
		if (!musicPlaying) {
			Sound bgm = new Sound("src/sounds/music.wav");
			bgm.start();
			musicPlaying = true;
		}

		// Clear the enemies before creating them to avoid duplication.
		enemies.clear();

		setSize(tmap.getPixelWidth() / 4, tmap.getPixelHeight());
		setVisible(true);

		// Create the background and parallax sprites.
		background = loadImage("src/images/parallax-mountain-bg.png");
		parallaxMountain = new Animation();
		parallaxMountain.addFrame(loadImage("src/images/parallax-mountain-mountains.png"), 100);
		bgMountain = new Sprite(parallaxMountain);
		bgMountain.setX(0);
		bgMountain.setY(150);
		bgMountain.show();

		// Load all animations onto sprites.
		pButton = new Animation();
		pButton.addFrame(loadImage("src/images/Play Button.png"), 100);
		play = new Sprite(pButton);

		pIdle = new Animation();
		pIdle.loadAnimationFromSheet("src/images/pIdle.png", 11, 1, 60);

		pWalk = new Animation();
		pWalk.loadAnimationFromSheet("src/images/pWalk.png", 13, 1, 60);

		pAttack = new Animation();
		pAttack.loadAnimationFromSheet("src/images/pAttack.png", 18, 1, 60);

		pDead = new Animation();
		pDead.loadAnimationFromSheet("src/images/pDead.png", 15, 1, 60);

		eIdle = new Animation();
		eIdle.loadAnimationFromSheet("src/images/eIdle.png", 4, 1, 60);

		eRun = new Animation();
		eRun.loadAnimationFromSheet("src/images/eRun.png", 8, 1, 100);

		portalA = new Animation();
		portalA.loadAnimationFromSheet("src/images/portal.png", 8, 1, 60);

		// Create the enemies.
		for (int c = 0; c < 3; c++) {
			s = new GameCharacter(eIdle);
			enemies.add(s);
		}

		initialiseGame();

		System.out.println(tmap);
	}

	/**
	 * You will probably want to put code to restart a game in a separate method so
	 * that you can call it to restart the game.
	 */
	public void initialiseGame() {

		// Initialise the player with an animation

		player = new GameCharacter(pIdle);
		player.setHealth(3);

		player.setX(70);
		// Maybe set above 300 to "rise from grave".
		player.setY(300);
		player.setVelocityX(0);
		player.setVelocityY(0);
		player.show();

		// Initialise the portal and set it according to level.
		portal = new Sprite(portalA);
		if (map == 1) {
			portal.setX(1791);
			portal.setY(320);
		} else if (map == 2) {
			portal.setX(1144);
			portal.setY(159);
		}
		portal.show();

		// Spawns the enemies at their positions on the levels.
		int i = 0;
		if (map == 1) {
			for (GameCharacter s : enemies) {
				s.setX(positionsX1[i]);
				s.setY(positionsY1[i]);
				s.show();
				i++;
			}
		} else if (map == 2) {
			for (GameCharacter s : enemies) {
				s.setX(positionsX2[i]);
				s.setY(positionsY2[i]);
				s.show();
				i++;
			}
		}

		// Show play button.
		play.setX(100);
		play.setY(150);
		play.show();

	}

	/**
	 * Draw the current state of the game
	 */
	public void draw(Graphics2D g) {
		// Be careful about the order in which you draw objects - you
		// should draw the background first, then work your way 'forward'

		// First work out how much we need to shift the view
		// in order to see where the player is.
		int xo = offSetX;
		int yo = offSetY;

		// If relative, adjust the offset so that
		// it is relative to the player

		// ...?

		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.drawImage(background, 0, 0, screenWidth, screenHeight, null, null);
		bgMountain.setScale(1.5f);

		// only draw player, tile maps, portal, enemies and parallax mountains if a map
		// is loaded.
		if (map == 1 || map == 2) {
			// Apply offsets to sprites then draw them

			// Draw the parallax mountains scaled up.
			bgMountain.drawTransformed(g);

			// Flip the enemies if the turn the other way.
			for (GameCharacter s : enemies) {
				s.setOffsets(xo, yo);
				if (s.isFilpped()) {
					s.drawFlipped(g);
				} else {
					s.draw(g);
				}
			}

			// Apply offsets to player and draw, draw flipped if facing the other way.
			player.setOffsets(xo, yo);
			if (player.isFilpped()) {
				player.drawFlipped(g);
			} else {
				player.draw(g);
			}

			portal.setOffsets(xo, yo);
			portal.draw(g);

			// Apply offsets to tile map and draw it
			tmap.draw(g, xo, yo);

			// Show health
			String msg = String.format("Health: %d", player.getHealth());
			g.setColor(Color.WHITE);
			g.drawString(msg, getWidth() - 100, 70);
		} else {
			// Draw play button if map isn't 1 or 2.
			play.setScale(0.5f);
			play.drawTransformed(g);
		}

	}

	/**
	 * Update any sprites and check for collisions
	 * 
	 * @param elapsed The elapsed time between this call and the previous call of
	 *                elapsed
	 */
	public void update(long elapsed) {

		if (paused)
			return;

		if (map == 1 || map == 2) {
			offSetX = -((int) player.getX() - 256);
			offSetY = -((int) player.getY() - 192);

			if (offSetY < 0) {
				offSetY = 0;
			}

			bgMountain.update(elapsed);
			bgMountain.setVelocityX((player.getVelocityX() / 10) * -1);

			// Make adjustments to the speed of the sprite due to gravity
			player.setVelocityY(player.getVelocityY() + (gravity * elapsed));

			for (GameCharacter s : enemies)
				s.setVelocityY(s.getVelocityY() + (gravity * elapsed));

			player.setAnimationSpeed(1.0f);

			// Make enemies move if they detect the player.
			for (GameCharacter s : enemies)
				s.detectPlayer(player);

			// Enemy movement.
			for (GameCharacter s : enemies) {
				if (s.isRight()) {
					s.setVelocityX(0.05f);
					s.setAnimation(eRun);

				} else if (s.isLeft()) {
					s.setVelocityX(-0.05f);
					s.setAnimation(eRun);

				} else {
					s.setVelocityX(0);
					s.setAnimation(eIdle);
				}
			}

			// Player controls events. TODO find a way to make this a method and separate
			// the setting animations because otherwise it will have to be repeated for
			// enemies
			if (player.isRight()) {
				player.setVelocityX(0.08f);
				player.setAnimation(pWalk);

			} else if (player.isLeft()) {
				player.setVelocityX(-0.08f);
				player.setAnimation(pWalk);

			} else {
				player.setVelocityX(0);
				player.setAnimation(pIdle);
			}

			if (player.isJump()) {
				player.setVelocityY(-0.25f);
				player.setJump(false);
				Sound jump = new Sound("src/sounds/jump.wav");
				jump.start();
			}

			if (player.isAttack() && player.isGrounded()) {
				player.setAnimation(pAttack);
				player.setVelocityX(0);
				if (pAttack.hasLooped()) {
					player.setAttack(false);
					player.setPosition(player.getX(), player.getY() + 8);
				}
			}

			if (player.getHealth() <= 0) {
				player.setAnimation(pDead);
				if (pDead.hasLooped()) {
					Sound gameOver = new Sound("src/sounds/gameOver.wav");
					gameOver.start();
					initialiseGame();
				}
			}

			// Update enemy sprites
			for (GameCharacter s : enemies)
				s.update(elapsed);

			// Now update the sprites animation and position
			player.update(elapsed);

			// Then check for any collisions that may have occurred
			handleScreenEdge(player, tmap, elapsed);
			checkTileCollision(player, tmap);
			// If player falls out bottom of map, they die.
			if (player.getY() > 325)
				player.setHealth(0);
			// Check collision for all enemies.
			for (GameCharacter s : enemies) {
				checkTileCollision(s, tmap);
				if (s.getY() > 325) {
					s.setX(-790);
				}
			}

			for (GameCharacter s : enemies) {

				// If player collides with sprite, hurt them, if player is attacking kill enemy.
				if ((boundingBoxCollision(s, player)) && player.isAttack()) {
					Sound ehurt = new Sound("src/sounds/ehurt.wav");
					ehurt.start();
					s.setX(-790);
				} else if ((boundingBoxCollision(s, player)) && !player.isAttack()) {

					Sound hurt = new Sound("src/sounds/hurt.wav");
					hurt.start();

					if (!cheat) {
						player.setHealth(player.getHealth() - 1);
					}
					player.setVelocityY(-0.15f);

					// Set player's velocity to bounce them backwards.
					if (player.isFilpped()) {
						s.setX(player.getX() - 100);
					} else {
						s.setX(player.getX() + 100);
					}

				}

			}

			portal.update(elapsed);

			// If player gets to portal increment map and re-initalise.
			if (boundingBoxCollision(portal, player)) {
				map++;
				init();
			}
		} else {
			// If no map is loaded only load the play button.
			play.update(elapsed);
		}

	}

	/**
	 * Checks and handles collisions with the edge of the screen
	 * 
	 * @param s       The Sprite to check collisions for
	 * @param tmap    The tile map to check
	 * @param elapsed How much time has gone by since the last call
	 */
	public void handleScreenEdge(Sprite s, TileMap tmap, long elapsed) {
		// This method just checks if the sprite has gone off the bottom screen.
		// Ideally you should use tile collision instead of this approach

		if (s.getY() + s.getHeight() > tmap.getPixelHeight()) {
			// Put the player back on the map 1 pixel above the bottom
			s.setY(tmap.getPixelHeight() - s.getHeight() - 1);

			// and make them bounce
			s.setVelocityY(-s.getVelocityY());
		}
	}

	/**
	 * Override of the keyPressed event defined in GameCore to catch our own events
	 * 
	 * @param e The event that has been generated
	 */
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		// Switch statement instead of lots of ifs...
		// Need to use break to prevent fall through.
		switch (key) {
		case KeyEvent.VK_ESCAPE:
			stop();
			break;
		case KeyEvent.VK_RIGHT:
			// If the player moves right set the sprite, NOT to flip.
			player.setFilpped(false);
			player.setRight(true);
			break;
		case KeyEvent.VK_LEFT:
			// If the player moves left flip the sprite.
			player.setFilpped(true);
			player.setLeft(true);
			break;
		case KeyEvent.VK_UP:
			// Player can only jump if they are "grounded" meaning touching the ground.
			if (player.isGrounded() && !player.isAttack()) {
				player.setJump(true);
				player.setGrounded(false);
			}
			break;
		case KeyEvent.VK_Z:
			// Player can only attack if they are grounded and not already attacking.
			if (player.isGrounded() && !player.isAttack()) {
				player.setAttack(true);
				pAttack.start();
				SoundFilter swing = new SoundFilter("src/sounds/swing.wav");
				swing.start();
			}
			break;
		case KeyEvent.VK_T:
			cheat = !cheat;
			System.out.println("cheat " + cheat);
			break;
		case KeyEvent.VK_P:
			paused = !paused;
			break;
		// Cheat to teleport to end of level.
		case KeyEvent.VK_Y:
			if (map == 1) {
				player.setX(1791);
				player.setY(320);
			} else if (map == 2) {
				player.setX(1144);
				player.setY(159);
			}
			break;
		default:
			break;
		}

	}

	// Check sprite collision.
	public boolean boundingBoxCollision(Sprite s1, Sprite s2) {
		return ((s1.getX() + s1.getImage().getWidth(null) > s2.getX())
				&& (s1.getX() < (s2.getX() + s2.getImage().getWidth(null)))
				&& ((s1.getY() + s1.getImage().getHeight(null) > s2.getY())
						&& (s1.getY() < s2.getY() + s2.getImage().getHeight(null))));
	}

	/**
	 * Check and handles collisions with a tile map for the given sprite 's'.
	 * Initial functionality is limited...
	 * 
	 * @param s    The Sprite to check collisions for
	 * @param tmap The tile map to check
	 */

	public void checkTileCollision(GameCharacter s, TileMap tmap) {
		// Take a note of a sprite's current position
		float sx = s.getX();
		float sy = s.getY();

		// Find out how wide and how tall a tile is
		float tileWidth = tmap.getTileWidth();
		float tileHeight = tmap.getTileHeight();

		// Divide the sprite’s x coordinate by the width of a tile, to get
		// the number of tiles across the x axis that the sprite is positioned at
		int xtile = (int) (sx / tileWidth);
		// The same applies to the y coordinate
		int ytile = (int) (sy / tileHeight);

		// What tile character is at the top left of the sprite s?
		char ch = tmap.getTileChar(xtile, ytile);

		// bottom middle
		xtile = (int) ((sx + (s.getWidth() / 2)) / tileWidth);
		ytile = (int) ((sy + s.getHeight()) / tileHeight);
		ch = tmap.getTileChar(xtile, ytile);

		// If it's not empty space
		if (ch != '.') {
//			System.out.println("mb");
			s.setVelocityY(0); // stop vertical movement
			s.setPosition(s.getX(), s.getY() - 1);
			// Grounded player can jump again.
			s.setGrounded(true);
		}

		// middle right
		xtile = (int) ((sx + s.getWidth()) / tileWidth);
		ytile = (int) ((sy + (s.getHeight() / 2)) / tileHeight);
		ch = tmap.getTileChar(xtile, ytile);

		// If it's not empty space
		if (ch != '.') {
//			System.out.println("mr");
			s.setPosition((s.getX() - 2), s.getY());
		}

		// middle left
		xtile = (int) (sx / tileWidth);
		ytile = (int) ((sy + (s.getHeight() / 2)) / tileHeight);
		ch = tmap.getTileChar(xtile, ytile);

		// If it's not empty space
		if (ch != '.') {
//			System.out.println("ml");
			s.setPosition((s.getX() + 2), s.getY());
		}

		// middle top
		xtile = (int) ((sx + (s.getWidth() / 2)) / tileWidth);
		ytile = (int) (sy / tileHeight);
		ch = tmap.getTileChar(xtile, ytile);

		// If it's not empty space
		if (ch != '.') {
//			System.out.println("mt");
			s.setVelocityY(0); // stop vertical movement
		}

	}

	public void keyReleased(KeyEvent e) {

		int key = e.getKeyCode();

		// Switch statement instead of lots of ifs...
		// Need to use break to prevent fall through.
		switch (key) {
		case KeyEvent.VK_ESCAPE:
			stop();
			break;
		case KeyEvent.VK_RIGHT:
			player.setRight(false);
			break;
		case KeyEvent.VK_LEFT:
			player.setLeft(false);
			break;
		case KeyEvent.VK_UP:
			player.setJump(false);
			break;
		default:
			break;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		// If the player clicks on the play button when map = 3 (menu) then set man=p to
		// 1 and start the game.
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (((e.getX() > 104 && e.getX() < 397) && (e.getY() > 153 && e.getY() < 248)) && map == 3) {
				map = 1;
				init();
				Sound start = new Sound("src/sounds/start.wav");
				start.start();
			}
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
