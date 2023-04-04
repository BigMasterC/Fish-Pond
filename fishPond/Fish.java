package fishPond;

import cmsc131Utilities.Random131;

/**
 * The state of a fish consists of its position in the pond (row and         
 * column), it's size, and the direction in which it is moving (UP, DOWN,    
 * LEFT, or RIGHT).                                                          
 * <p>                                                                 
 * A fish moves, eats other fish, eats plants, and shrinks over time.        
 * <p>                                                                          
 * STUDENTS MAY NOT ADD ANY FIELDS OR PUBLIC METHODS!  (You may add private  
 * methods of your own, if you wish.)                                        
 *                                                                           
 * @author Put Your Name Here          
 */

public class Fish {

	/** Initial size of each fish when simulation begins */
	public static final int FISH_STARTING_SIZE = 100;
	
	/** Maximum size for a fish */
	public static final int MAX_FISH_SIZE = 1000;
	
	/** Code for "Left" fish direction */
	public static final int LEFT = 10;
	
	/** Code for "Right" fish direction */
	public static final int RIGHT = 11;
	
	/** Code for "Up" fish direction */
	public static final int UP = 12;
	
	/** Code for "Down" fish direction */
	public static final int DOWN = 13;
	
	/*State of this fish.  YOU MAY NOT ADD ANY FIELDS! */
	private int row, col, size, direction;
	
			//  [END OF LISTING OF VARIABLES]   //
	
	/** Simply initializes the state of the fish with these parameters */
	public Fish(int row, int col, int size, int direction) {		
		this.row= row;
		this.col= col;
		this.size = size;
		this.direction = direction;
	}
	
	/** Standard copy constructor -- just copies the fields */
	public Fish(Fish x) {
		row = x.row;
		col = x.col;
		size = x.size;
		direction = x.direction;
	}
	
	/** Fish size increased by nutritionalValue. */
	public void eat(int nutritionalValue) {
		size += nutritionalValue;
	}
	
	/** Returns true if size is greater than zero, false otherwise */
	public boolean isAlive() {
		if (size > 0) {
			return true;
		}else {
			return false;
		}
	}
	
	/** Size is decreased by TWO.  */
	public void shrink() { 
		size -= 2;
	}
	
	/* This fish eats the other fish.  I.e. This fish's size is increased by
	 * the size of the fish "other".  The size of "other" is set to 0. */
	private void eat(Fish other) {
		size += other.size;
		other.size =0;
	}
	
	/** Implement this however you want 
	 * -- it's for your own purposes while debugging */
	public String toString() {
		return "Row: "+ row + "Col: " + col + "Size: " + size + 
				"Direction of fish: " + direction ;
	}
	
	/** The current object battles the parameter (other).  
	 * Whichever one is larger
	 * eats the other by calling the private "eat" method.  
	 * In cases where the sizes
	 * of the two fish are exactly equal, have the current object win. */
	public void fight(Fish other) {
		if (size > other.size) {
			this.eat(other); //the current object fish is eating the other fish
		}else if (size < other.size) {
			other.eat(this);
		} else if (size == other.size) {
			this.eat(other);
		}
	}
	
	/**The fish's location (row or col) is adjusted by ONE unit,
	 * depending on the fish's current direction (it is facing).
	 * For example, if the current direction is "UP", then
	 * the fish's row should be decremented.
	 * <p>
	 * If the fish's current direction is not equal to one of the static 
	 * constants UP,DOWN, LEFT, or RIGHT, then this method will throw an 
	 * IllegalFishDirectionException, 
	 * passing the fish's direction to the constructor. 
	 */
	public void move() {
		if (this.getDirection()== UP) {
		//if the current object fish is facing upwards, then it moves up a row  
			this.row-=1;
		}else if(this.getDirection() == DOWN){
			this.row +=1;			
		}else if (this.getDirection() == LEFT) {
			this.col -=1;
		}else if (this.getDirection() == RIGHT) {
			this.col += 1;
		}else {
			throw new IllegalFishDirectionException(this.getDirection()); 
			//the fish's direction
		}
	}
	
	/**The fish's direction is randomly determined (UP, DOWN, LEFT or RIGHT).  
	 * Sometimes the resulting direction will be the same as the original one.
	 * <p>
	 * YOU MUST FOLLOW THE INSTRUCTIONS BELOW OR YOU WILL NOT PASS OUR TESTS!
	 * <p>
	 * Call Random131.getRandomInteger(4). 
	 * If you open open the OG Declaration of the method, 
	 * "4" is the max number the random number can be
	 * <p>
	 * If the value is 0, set the direction to UP.  
	 * If 1, set to DOWN.  If 2, set to 
	 * LEFT.  
	 * If 3, set to RIGHT.  IMPORTANT:  DO NOT SET THE DIRECTION TO THE VALUES 
	 * 0, 1, 2, OR 3 
	 * -- directions must be set using the static constants (UP, DOWN, 
	 * LEFT, RIGHT). */
	public void setRandomDirection() {
		/*only call Random131.getRandomInteger(4) 
		 * ONCE because you're getting on random number*/
		int randomNum = Random131.getRandomInteger(4);
		
		if (randomNum == 0) {
			direction = UP;
		}else if (randomNum == 1) {
			direction = DOWN;
		}else if (randomNum == 2) {
			direction = LEFT;
		}else if (randomNum == 3) {
			direction = RIGHT;
		}
	}
	
	/** Returns size */
	public int getSize() {
		return size;
	}
	
	/** Returns row */
	public int getRow() {
		return row;
	}
	
	/** Returns column */
	public int getCol() {
		return col;
	}
	
	/** Returns direction (UP, DOWN, LEFT, or RIGHT) */
	public int getDirection() {
		return direction;
	}
}
