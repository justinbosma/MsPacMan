package pacman.controllers;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class Q_Controller extends Controller<MOVE>{
	
	public static StarterGhosts ghosts = new StarterGhosts();
	ArrayList<int[]> states = new ArrayList<int[]>();
	ArrayList<ArrayList<Integer>> Q = new ArrayList<ArrayList<Integer>>();
	boolean QBuilt = false;
	int[] stateRep;
	int maxQValue;
	int maxQIndex;
	int stateRepIndex;
	boolean r = true;
	
	
	
	public MOVE getMove(Game game, long timeDue) {
		maxQValue = 0;
		maxQIndex = 0;
		while(QBuilt == false) {

			createVectors();
			createQ();		
			buildQ(game, 1200, timeDue);
			QBuilt = true;	
		}
		 
		stateRep = getStateRep(game);
		stateRepIndex = getStateIndex(stateRep);
		
		for(int i = 0; i < Q.size(); i ++) {
			if(Q.get(stateRepIndex).get(i) > maxQValue) {
				maxQIndex = i;
			}
		}


		/*for(int k = 0; k < 9; k++) {	
			System.out.print(stateRep[k]);	
		}
		System.out.println();*/

		return getQMove(game, states.get(maxQIndex));

	}

	//Runs Each Episode Updating Q matrix
	public  void buildQ(Game game, int episodes, long timeDue) {
		MOVE[] allMoves;
		MOVE highMove = MOVE.UP;
		int[] oldStateRep;
		int[] newStateRep;
		int oldStateIndex;
		int newStateIndex;
		int oldScore = 0;
		int newScore = 0;
        Game gameCopy;
        Game gameATM;



		for(int i = 0; i < episodes; i++) {
			
			gameCopy = game.copy();
			gameATM = gameCopy;
			
			//While the game is not over keep getting highest reward for move then make addtions for Q
			while(!gameATM.gameOver()) {
				
				
				oldStateRep = getStateRep(gameCopy);
				oldStateIndex = getStateIndex(oldStateRep);
				oldScore = gameCopy.getScore();
				
				//grabs all possible moves, ignoring backtracking, then checks them for the highest score
				allMoves=game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
				
				for(MOVE m: allMoves) {

	                gameATM = gameCopy;
	                gameATM.advanceGame(m, ghosts.getMove(gameATM, 0));

	                
	                if(gameATM.getScore() >= oldScore) {
	                	highMove = m;
	                	newScore = gameATM.getScore();
	                	System.out.println(newScore + " " + i);
	                }
				}
				
				gameCopy.advanceGame(highMove, ghosts.getMove(gameCopy, timeDue));

				newStateRep = getStateRep(gameCopy);
				newStateIndex = getStateIndex(newStateRep);

				Q.get(oldStateIndex).set(newStateIndex, valueQ(gameCopy, newStateIndex, oldScore, newScore, timeDue));
				//System.out.println(oldStateIndex + " " + newStateIndex);
				//System.out.println(Q.get(oldStateIndex).get(newStateIndex));
			}
		}
	}
	
	//Calculates the values for Q
	public int valueQ(Game game, int stateIndex, int oldScore, int newScore, long timeDue) {
		Game gameCopy = game.copy();
		Game gameATM = gameCopy;
		MOVE highMove = MOVE.UP;

		int newStateIndex;
		int totalValue;
		int score = -1;

		
		MOVE[] moves = gameATM.getPossibleMoves(game.getPacmanCurrentNodeIndex());
		
		//Finds the maximum value of the next Q states
		for(MOVE m: moves) {
			gameCopy = game.copy();
			gameATM = gameCopy;
		
            gameATM.advanceGame(m, ghosts.getMove(gameATM, timeDue));
			if(gameATM.getScore() > score) {
				score = gameATM.getScore();
				highMove = m;
			}
		}
		//Fresh copy
		gameCopy = game.copy();
		gameATM = gameCopy;
		//Advances game in direction of highMove
		gameATM.advanceGame(highMove, ghosts.getMove(gameATM, timeDue));
		//Gets the state index for the highest move
		newStateIndex = getStateIndex(getStateRep(gameATM));
		//This is the Q value which is the reward + the max state transition
		totalValue = calculateReward(game) + Q.get(stateIndex).get(newStateIndex);
		
		return totalValue;
		
	}
	
	//Calculate Reward
	public int calculateReward(Game game) {
		int reward = 0;
		if(game.wasPillEaten()) {
			reward = reward + 5;
		}
		if(game.wasGhostEaten(GHOST.BLINKY) || game.wasGhostEaten(GHOST.PINKY) 
				|| game.wasGhostEaten(GHOST.INKY) || game.wasGhostEaten(GHOST.SUE)) {
			reward = reward + 200;
		}
		if(game.wasPowerPillEaten()) {
			reward = reward + 20;
		}
		if(game.wasPacManEaten()) {
			reward = reward - 50;
		}
		
		return reward;
	}
		

	
	//Creates vectors for state representations
	//V1 - v4 having a value 1 represents the presence of a wall. The order is v1 = left, v2 = right, v3 = up, v4 = down
	//v5 represents the direction of desired item. This takes on a value from 0 - 3 representing up, down, left, right respectively 
	//v6 - v9 having a value of 1 represents the direction of ghosts within a specific distance v6 = up, v7 = down, v8 = left, v9 = right
	public void createVectors() {
		for(int v1 = 0; v1 < 2; v1 ++) {
			for(int v2 = 0; v2 < 2; v2 ++) {
				for(int v3 = 0; v3 < 2; v3 ++) {
					for(int v4 = 0; v4 < 2; v4 ++) {
						for(int v5 = 0; v5 < 4; v5 ++) {
							for(int v6 = 0; v6 < 2; v6 ++) {
								for(int v7 = 0; v7 < 2; v7 ++) {
									for(int v8 = 0; v8 < 2; v8 ++) {
										for(int v9 = 0; v9 < 2; v9 ++) {
											int[] pi = {v1, v2, v3, v4, v5, v6, v7, v8, v9};
											states.add(pi);
										}
									}
								}
							}
						}
					}
				}
			}			
		}
	}
	
	//Creates a nXn array with all zeroes for initial Q matrix
	public void createQ() {
		ArrayList<Integer> col = new ArrayList<Integer>();
		for(int i = 0; i < states.size(); i++) {
			col.add(Integer.valueOf(0));
		}
		for(int k = 0; k < states.size(); k++) {
			Q.add(col);			
		}
	}
	//Gets state representation for current game state
	public int[] getStateRep(Game game) {
		int limit = 10;
		int[] state = {1,1,1,1,0,0,0,0,0};
		MOVE[] posMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
		
		//First Block of IF statements gets the wall setup of the current position by checking possible moves
		//if there is a possible move, then there is no wall
		if(Arrays.asList(posMoves).contains(MOVE.UP)){
			state[0] = 0;
		}
		if(Arrays.asList(posMoves).contains(MOVE.DOWN)){
			state[1] = 0;
		}
		if(Arrays.asList(posMoves).contains(MOVE.LEFT)){
			state[2] = 0;
		}
        if(Arrays.asList(posMoves).contains(MOVE.RIGHT)){
			state[3] = 0;
		}
		
		//Second block of statements finds the direction of desired item (with highest value) and sets vector value to that.
		//range from 0 - 3 represents up, down, left ,right respectively
		
        state[4] = findItem(game);
        
		//Third block calculates distances of ghosts. A "1" is put in place for each direction with ghost < 5 distance
		int blinkySpot = game.getGhostCurrentNodeIndex(GHOST.BLINKY);
		int pinkySpot = game.getGhostCurrentNodeIndex(GHOST.PINKY);
		int inkySpot = game.getGhostCurrentNodeIndex(GHOST.INKY);
		int sueSpot = game.getGhostCurrentNodeIndex(GHOST.SUE);
		int up = game.getNeighbour(game.getPacmanCurrentNodeIndex(), MOVE.UP);
		int down = game.getNeighbour(game.getPacmanCurrentNodeIndex(), MOVE.DOWN);
		int left = game.getNeighbour(game.getPacmanCurrentNodeIndex(), MOVE.LEFT);
		int right = game.getNeighbour(game.getPacmanCurrentNodeIndex(), MOVE.RIGHT);
		
		if(up > 0 && (game.getManhattanDistance(up, blinkySpot) < limit || game.getManhattanDistance(up, pinkySpot) < limit
				|| game.getManhattanDistance(up, inkySpot) < limit || game.getManhattanDistance(up, sueSpot) < limit)) {

			state[5] = 1;
			
		}
		if(down > 0 && (game.getManhattanDistance(down, blinkySpot) < limit || game.getManhattanDistance(down, pinkySpot) < limit
				|| game.getManhattanDistance(down, inkySpot) < limit || game.getManhattanDistance(down, sueSpot) < limit)) {
			state[6] = 1;
		}
		if(left > 0 && (game.getManhattanDistance(left, blinkySpot) < limit || game.getManhattanDistance(left, pinkySpot) < limit
				|| game.getManhattanDistance(left, inkySpot) < limit || game.getManhattanDistance(left, sueSpot) < limit)) {
			state[7] = 1;
		}
		if(right > 0 && (game.getManhattanDistance(right, blinkySpot) < limit || game.getManhattanDistance(right, pinkySpot) < limit
				|| game.getManhattanDistance(right, inkySpot) < limit || game.getManhattanDistance(right, sueSpot) < limit)) {
			state[8] = 1;
		}
		
		return state;
	}
	
	//Finds desired item to seek. Looks at distance of power pills vs. Ghost distance vs. regular pill
	//Output is int in range 0-4  representing the directions up, down, left, right respectively
	public int findItem(Game game) {
		MOVE[] moves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
		int blinkySpot = game.getGhostCurrentNodeIndex(GHOST.BLINKY);
		int pinkySpot = game.getGhostCurrentNodeIndex(GHOST.PINKY);
		int inkySpot = game.getGhostCurrentNodeIndex(GHOST.INKY);
		int sueSpot = game.getGhostCurrentNodeIndex(GHOST.SUE);
		int up = game.getNeighbour(game.getPacmanCurrentNodeIndex(), MOVE.UP);
		int down = game.getNeighbour(game.getPacmanCurrentNodeIndex(), MOVE.DOWN);
		int left = game.getNeighbour(game.getPacmanCurrentNodeIndex(), MOVE.LEFT);
		int right = game.getNeighbour(game.getPacmanCurrentNodeIndex(), MOVE.RIGHT);
		
		//This block finds if an edible ghost is less than 5 squares away and tells the state to seek it
		if(Arrays.asList(moves).contains(MOVE.UP) &&(edibleInRange(game, GHOST.BLINKY, blinkySpot, up) || 
				edibleInRange(game, GHOST.PINKY, pinkySpot, up) || edibleInRange(game, GHOST.SUE, sueSpot, up) || 
				edibleInRange(game, GHOST.INKY, inkySpot, up))) {
			return 0;
		}
		else if(Arrays.asList(moves).contains(MOVE.DOWN) && (edibleInRange(game, GHOST.BLINKY, blinkySpot, down) || 
				edibleInRange(game, GHOST.PINKY, pinkySpot, down) || edibleInRange(game, GHOST.SUE, sueSpot, down) || 
				edibleInRange(game, GHOST.INKY, inkySpot, down))) {
			return 1;
		}
		else if(Arrays.asList(moves).contains(MOVE.LEFT) && (edibleInRange(game, GHOST.BLINKY, blinkySpot, left) || 
				edibleInRange(game, GHOST.PINKY, pinkySpot, left) || edibleInRange(game, GHOST.SUE, sueSpot, left) || 
				edibleInRange(game, GHOST.INKY, inkySpot, left))) {
			return 2;
		}
		else if(Arrays.asList(moves).contains(MOVE.RIGHT) && (edibleInRange(game, GHOST.BLINKY, blinkySpot, right) || 
				edibleInRange(game, GHOST.PINKY, pinkySpot, right) || edibleInRange(game, GHOST.SUE, sueSpot, right) || 
				edibleInRange(game, GHOST.INKY, inkySpot, right))) {
			return 3;
		}
		
		//This block looks for active power pills in range of 5
		if(Arrays.asList(moves).contains(MOVE.UP) && powerInRange(game, up)) {
			return 0;
		}
		else if(Arrays.asList(moves).contains(MOVE.DOWN) && powerInRange(game, down)) {
			return 1;
		}
		else if(Arrays.asList(moves).contains(MOVE.LEFT) && powerInRange(game, left)) {
			return 2;
		}
		else if(Arrays.asList(moves).contains(MOVE.RIGHT) && powerInRange(game, right)) {
			return 3;
		}
		
		//otherwise go after nearest pill
		return pillDir(game);
	}
	
	//checks if an edible ghost is within range 5
	public boolean edibleInRange(Game game, GHOST ghost, int ghostSpot, int nodeIndex) {
		return (game.isGhostEdible(ghost) && game.getManhattanDistance(nodeIndex, ghostSpot) < 5);
	}
	
	//checks if a power pill is within range 5 by iterating through the list of power pill indices 
	public boolean powerInRange(Game game, int nodeIndex) {
		int[] indices = game.getActivePowerPillsIndices();
		boolean inRange = false;

		
		for(int i = 0; i < indices.length; i++) {
			if(game.getManhattanDistance(nodeIndex, indices[i]) < 5) {
				inRange = true;
			}
		}
		return inRange;
	}
	
	//gets the direction of the nearest active pill
	public int pillDir(Game game) {
		int[] indices = game.getActivePillsIndices();
		int here = game.getPacmanCurrentNodeIndex();
		int min = indices[0];
		int go = 0;
		
		for(int i = 1; i < indices.length; i++) {
			if(game.getManhattanDistance(here, min) > game.getManhattanDistance(here, indices[i])) {
				min = indices[i];
			}
		}
		
		MOVE move = game.getNextMoveTowardsTarget(here, min, DM.PATH);
		
		if(move == MOVE.UP) {
			go = 0;
		}
		else if(move == MOVE.DOWN) {
			go = 1;
		}
		else if(move == MOVE.LEFT) {
			go = 2;
		}
		else if(move == MOVE.RIGHT) {
			go = 3;
		}
		
		return go;
	}
	
	//Returns index of state in Q matrix
	public int getStateIndex(int[] state) {
		int index = -1;
		for(int i = 0; i < states.size(); i++) {
			if(sameState(states.get(i), state)) {
				index = i;
			}
		}
		return index;
	}
	
	//Checks if same state
	public boolean sameState(int[] s1, int[] s2) {
		boolean thing = true;
		for(int i = 0; i < s1.length; i++) {
			if(!(s1[i] == s2[i])) {
				thing = false;
			}
		}

		return thing;
	}
	
	//Gets the move corresponding to the state transition
	public MOVE getQMove(Game game, int[] s2) {
		MOVE[] moves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
		MOVE theMove = MOVE.UP;

		for(MOVE m: moves) {
			Game gameCopy = game.copy();
			Game gameATM = gameCopy;
			gameATM.advanceGame(m, ghosts.getMove(gameATM, 0));
			if(sameState(s2, getStateRep(gameATM))) {
				theMove = m;
				//System.out.println("Match!!!");
			}
		}
		return theMove;
	}
	
}


