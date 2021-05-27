package game2D;

//By 2717790

//New class to differentiate characters in the game from regular sprites.
//mostly just flags to create sprite states.
public class GameCharacter extends Sprite {

	// Character sprite flags.
	boolean right = false;
	boolean left = false;
	boolean jump = false;
	boolean attack = false;
	boolean grounded = false;
	boolean filpped = false;
	int health = 1;

	public GameCharacter(Animation anim) {
		super(anim);
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isJump() {
		return jump;
	}

	public void setJump(boolean jump) {
		this.jump = jump;
	}

	public boolean isAttack() {
		return attack;
	}

	public void setAttack(boolean attack) {
		this.attack = attack;
	}

	public boolean isGrounded() {
		return grounded;
	}

	public void setGrounded(boolean grounded) {
		this.grounded = grounded;
	}

	public boolean isFilpped() {
		return filpped;
	}

	public void setFilpped(boolean facingLeft) {
		this.filpped = facingLeft;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public void detectPlayer(GameCharacter p) {

		// TODO add Y check
		if ((p.getX() < this.getX()) && (p.getX() > (this.getX() - 250))) {

			this.setLeft(true);
			this.setFilpped(false);

		} else {

			this.setLeft(false);

		}

		if (((p.getX() > this.getX()) && (p.getX() < (this.getX() + 250)))) {

			this.setRight(true);
			this.setFilpped(true);

		} else {

			this.setRight(false);

		}

	}

}
