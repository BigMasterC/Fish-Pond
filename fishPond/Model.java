package fishPond;

import java.util.*;
import cmsc131Utilities.Random131;

/**
 * Model for the Fish Pond Simulation.   The model consists of a List of Fish,   
 * a List of Plants, and a two dimensional array of boolean values representing 
 * the pond (each element in the array is either ROCK, or WATER.)               
 * <p>                                                               
 * Each time the simulation is re-started a new Model object is created.        
 * <p>                                                                             
 * STUDENTS MAY NOT ADD ANY FIELDS.  ALSO, STUDENTS MAY NOT ADD ANY PUBLIC      
 * METHODS.  (PRIVATE METHODS OF YOUR OWN ARE OKAY.)                            
 *                                                                           
 * @author Fawzi Emad,Chibundu Onwuegbule            
 */
public class Model {

	/* State of this Model.  STUDENTS MAY NOT ADD ANY FIELDS! */
	private ArrayList<Fish> fish; 
	//ArrayList of type Fish (elements) named "fish"
	private ArrayList<Plant> plants; 
	// ArrayList of type Plant (elements) named "plants"
	private boolean[][] landscape; //2D array called "landscape"

	/** Value stored in landscape array to represent water */
	public static final boolean WATER = false;

	/** Value stored in landscape array to represent rock */
	public static final boolean ROCK = true;

	/* Minimum pond dimensions */
	private static final int MIN_POND_ROWS = 5;
	private static final int MIN_POND_COLS = 5;

	/** THIS METHOD HAS BEEN WRITTEN FOR YOU!
	 * <p>
	 * If numRows is smaller than MIN_POND_ROWS, or if numCols is smaller than 
	 * MIN_POND_COLS, then this method will throw an IllegalPondSizeException.
	 * <p>
	 * The fields "rows" and "cols" are initilized with the values of 
	 * parameters numRows and numCols.
	 * <p>
	 * The field "landscape" is initialized as a 2-dimensional array of booleans  
	 * The size is determined by rows and cols. Every entry in the landscape 
	 * array is filled with WATER. The border around the perimeter of the 
	 * landscape array (top, bottom, left, right) is then overwritten with ROCK.
	 * <p>
	 * Random rocks are placed in the pond until the number of rocks 
	 * (in addition to those in the border) reaches numRocks.
	 * <p>
	 * The "plants" ArrayList is instantiated.
	 * Randomly placed Plant objects are put into the List. 
	 * Their positions are chosen so that they are never above rocks or
	 * in the same position as another plant. 
	 * Plants are generated in this way until the list reaches size numPlants.
	 * <p>
	 * The "fish" ArrayList is instantiated.  
	 * Now randomly placed Fish objects are put into the List.  
	 * Their directions are also randomly selected.  The positions are 
	 * chosen so that they are never above rocks, plants, or other fish.
	 * Fish are 
	 * generated in this way until the list reaches size numFish.
	 * 
	 * @param numRows number of rows for pond
	 * @param numCols number of columns for pond
	 * @param numRocks number of rocks to be drawn in addition to rocks around 
	 * border of pond
	 * @param numFish number of fish to start with
	 * @param numPlants number of plants to start with
	 */
	public Model(int numRows, int numCols, int numRocks, int numFish, 
			int numPlants) {

		if (numRows < MIN_POND_ROWS || numCols < MIN_POND_COLS)
			throw new IllegalPondSizeException(numRows, numCols);

		landscape = new boolean[numRows][numCols];
		plants = new ArrayList<Plant>();
		fish = new ArrayList<Fish>();

	/* Fill landscape with water and a border of rocks around the perimeter */
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++)
				landscape[i][j] = WATER;
			landscape[i][0] = ROCK;
			landscape[i][numCols - 1] = ROCK;
		}
		for (int j = 1; j < numCols - 1; j++) {
			landscape[0][j] = ROCK;
			landscape[numRows - 1][j] = ROCK;
		}

		/* Place random rocks */
		int rocksPlaced = 0;
		while (rocksPlaced < numRocks) {

			int row = Random131.getRandomInteger(numRows);
			int col = Random131.getRandomInteger(numCols);
			if (landscape[row][col] == WATER) {
				landscape[row][col] = ROCK;
				rocksPlaced++;
			}
		}

		/* Place random plants */
		for (int i = 0; i < numPlants; i++) {
			int row = Random131.getRandomInteger(numRows);
			int col = Random131.getRandomInteger(numCols);
			try{
				addPlant(new Plant(row, col, Plant.PLANT_STARTING_SIZE));
			}
			catch(IllegalPlantPositionException e) {
				i--;
			}
		}

		/* Place random fish */
		for (int i = 0; i < numFish; i++) {
			int row = Random131.getRandomInteger(numRows);
			int col = Random131.getRandomInteger(numCols);
			int r = Random131.getRandomInteger(4);
			int dir;
			if (r == 0)                 
				dir = Fish.UP;
			else if (r == 1)
				dir = Fish.DOWN;
			else if (r == 2)
				dir = Fish.LEFT;
			else
				dir = Fish.RIGHT;

			Fish f = new Fish(row, col, Fish.FISH_STARTING_SIZE, dir);
			try {	
				addFish(f);
			}
			catch(IllegalFishPositionException e) {
				i--;
			}
		}
	}

	/** THIS METHOD HAS BEEN WRITTEN FOR YOU.
	 * <p>
	 * When a plant gets bigger than Plant.MAX_PLANT_SIZE, it will explode into
	 * 2 to 9 smaller plants, whose sizes add up to the size 
	 * of the original plant. The smaller plants will be placed in the 9 regions
	 * of the landscape array that surround the original plant.
	 *  If there are rocks, fish, or other plants already occupying these 
	 * adjacent regions, then fewer than 9 plants are created. If there are no 
	 * available regions nearby, the plant will not explode.  */
	public void plantExplosions() {

		Iterator<Plant> i = plants.iterator();
		while(i.hasNext()) {
			Plant curr = i.next();
			if (curr.getSize() > Plant.MAX_PLANT_SIZE) {
				int count = 0;    // number of available slots for little plants
				boolean[] freeSpace = new boolean[9]; 
				// true if just water in that region

			/* locations of 8 little plants are determined by these offsets to
				* the coordinates of the plant that is exploding. */
				int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
				int[] dy = {1, 1, 0, -1, -1, -1, 0, 1};

				int r = curr.getRow();
				int c = curr.getCol();

				/* Look to see if space is available in all eight directions */
				for (int j = 0; j < 8; j++) {
					freeSpace[j] = isSpaceAvailable(r + dy[j], c + dx[j]);
					if (freeSpace[j])
						count++;
				}

				/* We'll split only if 1 or more spaces are available */
				if (count > 0) {
					i.remove();    // kill off original plant
					int newSize = curr.getSize() / (count + 1);   // truncates!

				/* Add little plants to the list -- iterator is now broken! */
					for (int j = 0; j < 8; j++)
						if (freeSpace[j])
							plants.add(new Plant(r + dy[j], c + dx[j], 
									newSize));

					plants.add(new Plant(r, c, newSize));  // replace original

				/* Since we've modified the List, the original iterator
				* is no longer useful.  Start iterating from the beginning. */
					i = plants.iterator();
				}	
			}
		}
	}

	/** THIS METHOD HAS BEEN WRITTEN FOR YOU!
	 * <p>
	 * When a fish gets bigger than Fish.MAX_FISH_SIZE, it will explode into
	 * 4 to 8 smaller fish, whose sizes add up to the size of the original fish.
	 The smaller fish will be placed in the eight regions of the landscape array
	 * surrounding the original fish. The little fish will be begin moving in
	 * directions that point away from the original location.
	 * (Note that no little fish is placed into the original location of the
	 * landscape array where the exploding fish was 
	 * -- just in the surrounding squares.) 
	 * If there are rocks, fish, or plants already occupying these adjacent 
	 * squares, then fewer than eight little fish are created. If there are not 
	 * at least four available surrounding squares,
	 * then the fish will not explode.*/
	public void fishExplosions() {

		Iterator<Fish> i = fish.iterator();
		while(i.hasNext()) {
			Fish curr = i.next();
			if (curr.getSize() > Fish.MAX_FISH_SIZE) {
				int count = 0;  // number of available squares for little fish
				boolean[] freeSpace = new boolean[8];  
				// true if just water in that region

			/* locations of the 8 little fish are determined by these offsets
				* to the coordinates of the original fish that is exploding */
				int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
				int[] dy = {-1, -1, 0, 1, 1, 1, 0, -1};

				/* directions for the 8 little fish */
				int[] newDir = {Fish.UP, Fish.UP, Fish.RIGHT, Fish.RIGHT, 
						Fish.DOWN, Fish.DOWN, Fish.LEFT, Fish.LEFT};

				int r = curr.getRow();
				int c = curr.getCol();

				/* Look to see if space is available in all directions */
				for (int j = 0; j < 8; j++) {
					freeSpace[j] = isSpaceAvailable(r + dy[j], c + dx[j]);
					if (freeSpace[j])
						count++;
				}

				/* We'll split only if 4 or more spaces are available */
				if (count > 3) {
					i.remove();  // remove original fish from List
					int newSize = curr.getSize() / count;

					/* Add little fish to the list -- iterator is now broken! */
					for (int j = 0; j < 8; j++)
						if (freeSpace[j])
							fish.add(new Fish(r + dy[j], c + dx[j], newSize, 
									newDir[j]));

					/* Since we have modified the List, the original Iterator
					 * is no longer valid.  We'll start iterating again from the
					 * beginning. */
					i = fish.iterator();
				}	
			}
		}
	}
	//[ WHERE I STARTED CODING ]
	/* Checks the specified location to see if it has a rock, 
	 * fish, or plant in it. If so, returns false; 
	 * if it is just water, returns true. */
	public boolean isSpaceAvailable(int r, int c) { 
		// "r" --> "row" & "c" --> "column"
		if (landscape[r][c]== Model.ROCK) {
			return false;
		}else {
			for (Fish fishy : fish) {
				if (fishy.getRow()== r && fishy.getCol()== c) { 
					//how to get position of a fish in the pond
					return false;
				}
			}
			for (Plant plant : plants) {
				if (plant.getRow()== r && plant.getCol()== c) {
					return false;
				}
			}
			return true;
		}
	}

	/** Copy Constructor.
	 * <p>
	 * Since landscape is immutable, it is be copied with just a shallow copy. 
	 * //[shallow copy of a 2d array]
	 * Fish and Plants are mutable, so they must be copied with a DEEP copy! 
	 * //[DEEP copy of ArrayLists] 
	 * can't use ".clone()" because it creates a shallow copy
	 * (WARNING:  Each fish and each plant must be copied.)
	 */
	public Model(Model other) {//DONE
		fish = new ArrayList<>(); 
		/*initializing current object because it was not 
		 * initialized and was only declared. 
		 * DON'T HAVE TO INITILIZE WITH A SIZE*/
		plants = new ArrayList <>();

		this.landscape = new boolean [other.landscape.length][];
		for (int i = 0; i < other.landscape.length; i++) {
			landscape[i] = new boolean[other.landscape[i].length];
			//creating column
		}
		for (int row = 0; row < other.landscape.length; row++) {
			for (int col = 0; col < other.landscape[row].length; col++) {
				this.landscape[row][col]= other.landscape[row][col];
				//what makes this a shallow copy
			}
		}
		/*"For each fishy in the ArrayList fish, copy over the elements 
		in the other.fish ArrayList into the current object ArrayList"*/
		for (Fish fishy : other.fish) { //deep copy
			fish.add(new Fish(fishy));
		}
		for (Plant plant : other.plants) { //Deep copy
			plants.add(new Plant(plant));
		}
	}

	/** Fish f eats a portion of plant p.  The amount eaten is either 
	 * Plant.PLANT_BITE_SIZE or the current size of the plant, 
	 * whichever is smaller. The fish's size is increased by this amount and the
	 *  plant's size is decreased by this amount. */
	public static void fishEatPlant(Fish f, Plant p) {
		if (Plant.PLANT_BITE_SIZE < p.getSize()) {
			f.eat(Plant.PLANT_BITE_SIZE); 
			//size of the fish is increase when calling "eat" method
			p.removeBite(Plant.PLANT_BITE_SIZE); 
			//size is decreased when the method is called
		}else if (Plant.PLANT_BITE_SIZE > p.getSize()) {
			f.eat(p.getSize());
			p.removeBite(p.getSize());
		}
	}

	/** returns number of rows in landscape array */
	public int getRows() {
		return landscape.length; 
		//returns the number of rows in the array (CHECK)
	}

	/** returns number of columns in landscape array */
	public int getCols() {
		return landscape[0].length;
	}

	/** Iterates through fish list.  
	 * For each fish that isAlive, shrinks the fish by
	 * invoking it's "shrink" method. */
	public void shrinkFish() { //DOUBLE CHECK
		for (Fish fishy: fish) {
			if (fishy.isAlive()) {
				fishy.shrink();
			}
		}
	}

	/** Iterates through the plants list, 
	 * growing each plant by invoking it's "grow"
	 * only if they're alive
	 * method. */
	public void growPlants() {
		for (Plant plant : plants) {
			if(plant.isAlive()) {
				plant.grow();
			}
		}
	}

	/** Iterates through the list of Fish. 
	 * Any fish that is no longer alive is removed
	 *  from the list. */
	public void removeDeadFish() {
		//can't modify the ArrayList that we're looping through
		ArrayList<Fish> newFishList = new ArrayList<>();

		newFishList = new ArrayList<Fish>(fish); //shallow copy of a fish array
		for (Fish fishes : newFishList) {
			if (!(fishes.isAlive())) {
				fish.remove(fishes);
			}
		}
	}

	/** Iterates through the list of Plants. 
	 * Any plant that is no longer alive is removed
	 * from the list. */
	public void removeDeadPlants() {
		ArrayList<Plant> newPlantList = new ArrayList<>();

		newPlantList = new ArrayList<Plant>(plants);
		for (Plant plant : newPlantList) {
			if (!(plant.isAlive())) {
				plants.remove(plant);
			}
		}
	}

	/**Checks if the fish f is surrounded ON FOUR SIDES
	 * (above, below, left, right)
	 * by rocks.  If so, return true. 
	 * If there is at least one side without a rock,
	 * then return false. */
	private boolean fishIsSurroundedByRocks(Fish f) {
		//check if there's a rock in all four position surrounding the fish
		int row = f.getRow();
		int col = f.getCol();
		if ((landscape[row+1][col]== Model.ROCK) && 
				(landscape[row-1][col]== Model.ROCK)  && 
				(landscape[row][col-1]== Model.ROCK) && 
				(landscape[row][col+1]== Model.ROCK)) {//DOWN , UP, LEFT, RIGHT
			return Model.ROCK; //"true"
		}else {
			return Model.WATER; //"false"
		}
	}

	/**
	 * Iterate through list of Fish. 
	 * FOR EACH FISH THAT IS ALIVE, do the following:
	 * <p>
	 * 1. If this fishIsSurroundedByRocks, DO NOTHING, 
	 * and move on to the next fish. (This fish will not turn.)
	 * <p>
	 * 2. If this fish's direction is not equal to one of the 
	 * codes UP, DOWN, LEFT, or RIGHT, then throw an 
	 * IllegalFishDirectionException, passing this fish's direction to the 
	 * constructor of the IllegalFishDirectionException.
	 * <p>    
	 * 3. 
	 * Check whether or not this fish is about to hit a rock if it moves in it's 
	 * current direction.  If it is about to hit a rock, call the fish's 
	 * setRandomDirection method.  Repeat this step until the fish is no longer
	 * about to hit a rock.  Do not make any EXTRA calls to setRandomDirection
	 * or you will fail our tests! [ONLY ONE CALL]
	 */
	public void turnFish() {
		boolean ableToMove = false;

		for(Fish fishy: fish) {
			int row = fishy.getRow();
			int col = fishy.getCol();

			if(fishy.isAlive()) {

				if (fishIsSurroundedByRocks(fishy)) { 
					/*because this is an ArrayList, calling the .get() 
					 * method allows for this to work*/
					continue;
				}else if(fishy.getDirection()!= Fish.UP && 
						fishy.getDirection()!= Fish.DOWN &&
						fishy.getDirection()!= Fish.LEFT && 
						fishy.getDirection()!= Fish.RIGHT) {
					throw new IllegalFishDirectionException
					(fishy.getDirection());
				}
				do {
					if ((landscape[row+1][col]== Model.ROCK && 
							fishy.getDirection()== Fish.DOWN) || 
							// 2nd part is asking where the fish is facing
							(landscape[row-1][col]== Model.ROCK && 
							fishy.getDirection()== Fish.UP) || 
							(landscape[row][col+1]== Model.ROCK && 
							fishy.getDirection()== Fish.RIGHT)||
							(landscape[row][col-1]== Model.ROCK && 
							fishy.getDirection()== Fish.LEFT)) { 
						// check if UP/DOWN/LEFT/RIGHT
						fishy.setRandomDirection(); 
						/*the fish then turns to another direction and the fact 
						 * that the fish was about to hit 
						 * a rock is set to true*/
						ableToMove=false;
						//this block checks if I'm not able to move
					}else {
						ableToMove = true; //reassigning "ableToMove" here
					}
				} while (ableToMove == false); // checking if I'm able to move
			}
		}
	}

	/**
	 * Note:  This method assumes that each live fish that is not surrounded by
	 * rocks is already facing a direction where there is no rock!
	 * (Typically the call to this method should immediately follow a call to 
	 * "turnFish", which ensures that these conditions are satisfied.)
	 * <p>
	 * This method iterates through the list of fish.
	 * FOR EACH FISH THAT IS ALIVE, do the following:
	 * <p>
	 * 1.  
	 * Check to see if this fishIsSurroundedByRocks. 
	 * If so, DO NOTHING and move along to the next fish in the list.
	 * (This fish does not move, does not eat, does not fight.)
	 * <p>        
	 * 2.  Move this fish by calling it's "move" method.
	 * <p>
	 * 3.  
	 * Check if there is a plant that isAlive and 
	 * is located in the same position as this fish.
	 * If so, have the fish eat part of the plant by calling
	 * fishEatPlant.
	 * <p>    
	 * 4.  
	 * Check if there is another fish (distinct from this fish) 
	 * that is in the same location as this fish.  
	 * If so, have the two fish fight each other by calling
	 * the fight method.  IMPORTANT -- the fight method is not symmetrical. You 
	 * must use THIS fish as the current object, and pass the OTHER fish as the 
	 * parameter (otherwise you will not pass our tests.)
	 */
	public void moveFish() {//CHECK
		for (Fish fishy : fish) {
			if (fishy.isAlive()) { //#1
				if (fishIsSurroundedByRocks(fishy)) { 
                /*because this is an ArrayList, calling the .get()
                 *  method allows for this to work*/
					continue;
				}
				fishy.move(); //#2
				for (Plant plant: plants){
					if (plant.isAlive()&& 
							(fishy.getRow()==plant.getRow() && 
							fishy.getCol()== plant.getCol())) { 
						//has the same position
						fishEatPlant(fishy, plant);
					}
				}
				for (Fish otherFishy: fish){
					if (fishy!= otherFishy) {
						if (otherFishy.isAlive()&& 
								((fishy.getRow()==otherFishy.getRow()) && 
										(fishy.getCol() == 
										otherFishy.getCol()))) { 
							//has the same position
							fishy.fight(otherFishy);
						}
					}
				}
			}
		}
	}

	/** Attempts to add the plant p to plant list, if possible.
	 * <p>
	 * First checks if the landscape in the plant's location is equal to ROCK. 
	 * If it is, then does not add the plant to the list.  Instead throws an 
	 * IllegalPlantPositionException, passing 
	 * IllegalPlantPositionException.PLANT_OVER_ROCK to the constructor.
	 * <p>
	 * Now checks for another plant (distinct from the parameter) that is in the 
	 * same location as the parameter.
	 * If one is found, then does not add the plant to the list.  
	 * Instead throws an IllegalPlantPositionException,
	 * passing IllegalPlantPositionException.TWO_PLANTS_IN_ONE_PLACE to the 
	 * constructor.
	 * <p>
	 * Otherwise, adds the plant to the list "plants".
	 */
	public void addPlant(Plant p) {
		int rowOfPlant = p.getRow();
		int colOfPlant = p.getCol();
		if (landscape[rowOfPlant][colOfPlant]== Model.ROCK) { 
			//has the same position
			throw new IllegalPlantPositionException
			(IllegalPlantPositionException.PLANT_OVER_ROCK);
		}
		for (Plant newPlant: plants){ 
			//how to make sure this new plant is distinct
			if ((newPlant.getRow()==p.getRow()) && 
					(newPlant.getCol() == p.getCol())) { 
				//has the same position
				throw new IllegalPlantPositionException
				(IllegalPlantPositionException.TWO_PLANTS_IN_ONE_PLACE);
			}
		}
		plants.add(p);

	}

	/**Adds the fish f to the fish list, if possible.
	 * <p>
	 * First checks if the landscape in the fish's location is equal to ROCK.  
	 * If it is, then the fish is not added to the list.  Instead, throws an 
	 * IllegalFishPositionException, passing 
	 * IllegalFishPositionException.FISH_OVER_ROCK to the constructor.
	 * <p>
	 * Next checks for another fish (distinct from the parameter) that is in the 
	 * same location as the parameter.  If one is found, then the fish is not
	 * added to the list.  Instead throws an IllegalFishPositionException,
	 * passing IllegalFishPositionException.
	 * TWO_FISH_IN_ONE_PLACE to the constructor.
	 * <p>
	 * Otherwise, adds the parameter to the "fish" list.
	 */
	public void addFish(Fish f){
		int rowOfFish = f.getRow();
		int colOfFish = f.getCol();
		if (landscape[rowOfFish][colOfFish]== Model.ROCK) { 
			//has the same position
			throw new IllegalFishPositionException
			(IllegalFishPositionException.FISH_OVER_ROCK);
		}
		for (Fish fishy: fish){
			if ((fishy.getRow()==f.getRow()) && (fishy.getCol() == f.getCol()))
			{ 
				//has the same position
				throw new IllegalFishPositionException
				(IllegalFishPositionException.TWO_FISH_IN_ONE_PLACE);
			}
		}
		fish.add(f);
	}

	/** Returns a COPY of the fish list.  Hint:  Use the ArrayList<Fish> copy 
	 * constructor, otherwise you will fail our tests! */
	public ArrayList<Fish> getFish() {
		return new ArrayList<Fish>(fish); 
		/*call the copy constructor of the ArrayList class and 
		 * passing in the Collection I want to copy as an argument*/ 
	}

	/** Returns a COPY of the plants list.  Hint:  Use the ArrayList<Plant> 
	 * copy constructor, otherwise you will fail our tests! */ 
	public ArrayList<Plant> getPlants() {
		return new ArrayList<Plant>(plants);
	}

	/** Returns the specified entry of the landscape array */
	public boolean getShape(int row, int col) { 
		//returns the shape of the landscape
		return landscape[row][col];
	}
}
