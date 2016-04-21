package pacman.controllers;

import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Final_Controller extends Controller<MOVE>{

	//initializes ghosts
	 public static StarterGhosts ghosts = new StarterGhosts();
	
	public MOVE getMove(Game game, long timeDue) {
		int limit = 10;
		int blinkySpot = game.getGhostCurrentNodeIndex(GHOST.BLINKY);
		int pinkySpot = game.getGhostCurrentNodeIndex(GHOST.PINKY);
		int inkySpot = game.getGhostCurrentNodeIndex(GHOST.INKY);
		int sueSpot = game.getGhostCurrentNodeIndex(GHOST.SUE);
		int pacPos = game.getPacmanCurrentNodeIndex();
		

		
		if((game.getManhattanDistance(pacPos, blinkySpot) < limit) && !(game.isGhostEdible(GHOST.BLINKY))) {
			return runFromGhost(game, GHOST.BLINKY);
		}
		else if((game.getManhattanDistance(pacPos, pinkySpot) < limit)  && !(game.isGhostEdible(GHOST.PINKY))) {
			return runFromGhost(game, GHOST.PINKY);
		}
		else if((game.getManhattanDistance(pacPos, inkySpot) < limit)  && !(game.isGhostEdible(GHOST.INKY))) {
			return runFromGhost(game, GHOST.INKY);
		}
		else if((game.getManhattanDistance(pacPos, sueSpot) < limit)  && !(game.isGhostEdible(GHOST.SUE))) {
			return runFromGhost(game, GHOST.SUE);
		}
		else if(nearPowerPillAndGhost(game)) {
			return getPowerPill(game);
		}
		else if(game.isGhostEdible(GHOST.BLINKY) || game.isGhostEdible(GHOST.PINKY) || 
				game.isGhostEdible(GHOST.INKY) || game.isGhostEdible(GHOST.SUE)) {
			return getEdibleGhost(game, findClosestEdibleGhost(game));
		}
		else {
			return moveTowardsPill(game);
		}
		
		
	}
	
	//Takes in Game and nearest ghost
	//returns the move that gives max distance away from ghost
	public MOVE runFromGhost(Game game, GHOST ghost) {
		//Get all possible moves
		MOVE[] moves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
		//Max distance from nearby ghosts
		int maxDist = 0;
		//Distance from ghost after move
		int moveDist = 0;
		//Best move for max distance from ghost. Initialized at UP
		MOVE maxMove = MOVE.UP;

		
		for(MOVE m: moves) {
			Game gameCopy = game.copy();
			Game gameATM = gameCopy;
			gameATM.advanceGame(m, ghosts.getMove(gameATM, 0));
			//Gets the new distance between ghost and Ms. Pacman after move m
			moveDist = game.getManhattanDistance(gameATM.getPacmanCurrentNodeIndex(), gameATM.getGhostCurrentNodeIndex(ghost));
					
			if(moveDist > maxDist) {
				maxDist = moveDist;
				maxMove = m;
			}
		}
		return maxMove;
	}
	

	//Method checks if edible ghost is near power pill
	//It outputs true if a ghost is near one and Ms. PacMan is within distance 'dist' from it
	public Boolean nearPowerPillAndGhost(Game game) {
		int[] powerPills = game.getActivePowerPillsIndices();
		int dist = 5;
		int distGhost = 10;
		int blinkySpot = game.getGhostCurrentNodeIndex(GHOST.BLINKY);
		int pinkySpot = game.getGhostCurrentNodeIndex(GHOST.PINKY);
		int inkySpot = game.getGhostCurrentNodeIndex(GHOST.INKY);
		int sueSpot = game.getGhostCurrentNodeIndex(GHOST.SUE);
		int pacPos = game.getPacmanCurrentNodeIndex();
		int pillPos = -1;
 
		for(int i = 0; i < powerPills.length; i++) {
			if(game.getManhattanDistance(pacPos, powerPills[i]) < dist) {
				pillPos = powerPills[i];
			}
		}
		
		if(!(pillPos == -1) && (game.getManhattanDistance(pillPos, blinkySpot) < distGhost || 
				game.getManhattanDistance(pillPos, pinkySpot) < distGhost || 
				game.getManhattanDistance(pillPos, inkySpot) < distGhost ||
				game.getManhattanDistance(pillPos, sueSpot) < distGhost)) {
			return true;
		}
		else {
			return false;
		}
		
		
	}
	//Method is used when a non-edible ghost is close to a power pill. 
	//Method returns the move that will get it to the power pill
	public MOVE getPowerPill(Game game) {
		
		int powerPills[] = game.getActivePillsIndices();
		int closestPill = 0;
		int currentDist;
		int minDist = 1000000000;
		int pacPosition = game.getPacmanCurrentNodeIndex();
		for(int i = 0; i < powerPills.length; i++) {
			currentDist = game.getManhattanDistance(pacPosition, powerPills[i]);
			if(currentDist < minDist) {
				currentDist = minDist;
				closestPill = powerPills[i];
			}
		}
		
		return game.getNextMoveTowardsTarget(pacPosition, closestPill, DM.MANHATTAN);
	}
	

	
	//Find closest edible ghost
	public int findClosestEdibleGhost(Game game) {
		int blinkySpot = game.getGhostCurrentNodeIndex(GHOST.BLINKY);
		int pinkySpot = game.getGhostCurrentNodeIndex(GHOST.PINKY);
		int inkySpot = game.getGhostCurrentNodeIndex(GHOST.INKY);
		int sueSpot = game.getGhostCurrentNodeIndex(GHOST.SUE);
		int pacPos = game.getPacmanCurrentNodeIndex();
		
		int blinkyDist = game.getManhattanDistance(pacPos, blinkySpot);
		int pinkyDist = game.getManhattanDistance(pacPos, pinkySpot);
		int inkyDist = game.getManhattanDistance(pacPos, inkySpot); 
		int sueDist = game.getManhattanDistance(pacPos, sueSpot);
		
		//Checks if ghost are edible. If they are not the distance is increased to make it undesirable to move there
		//really shitty trick, probably better ways to do this, but it works because distances are fairly small normally
		if(!(game.isGhostEdible(GHOST.BLINKY))) {
			blinkyDist = 999999;
		}
		if(!(game.isGhostEdible(GHOST.PINKY))) {
			pinkyDist = 999999;
		}	
		if(!(game.isGhostEdible(GHOST.INKY))) {
			inkyDist = 999999;
		}
		if(!(game.isGhostEdible(GHOST.SUE))) {
			sueDist = 9999999;
		}	
		
		
		if((blinkyDist < pinkyDist) && (blinkyDist < inkyDist) && (blinkyDist < sueDist)) {
			return blinkySpot;
		}
		else if((pinkyDist < blinkyDist) && (pinkyDist < inkyDist) && (pinkyDist < sueDist)) {
			return pinkySpot;
		}
		else if((inkyDist < blinkyDist) && (inkyDist < pinkyDist) && (inkyDist < sueDist)) {
			return inkySpot;
		}
		else {
			return sueSpot;
		}
		
		
	}
	
	//Move towards closest edible ghost
	public MOVE getEdibleGhost(Game game, int ghostPosition) {
		int pacPosition = game.getPacmanCurrentNodeIndex();
		
		return game.getNextMoveTowardsTarget(pacPosition, ghostPosition, DM.MANHATTAN);
	}
	
	//Eat the nearest pill
	public MOVE eatNearestPill(Game game) {
		int pills[] = game.getPillIndices();
		int pacPosition = game.getPacmanCurrentNodeIndex();
		int closestPill = 0;
		int minDist = 9999999;
		int currentDist;
		
		for(int i  = 0; i < pills.length; i++) {
			currentDist = game.getManhattanDistance(pacPosition, pills[i]);
			if(currentDist < minDist) {
				minDist = currentDist;
				closestPill = pills[i];
			}
		}
		
		return game.getNextMoveTowardsTarget(pacPosition, closestPill, DM.MANHATTAN);
	}
	
	//Gets active pills and moves towards closest
	public MOVE moveTowardsPill(Game game) {
		int[] pillIndices = game.getActivePillsIndices();
		int minimumDist = 999999;
		int minPill = pillIndices[0];
		int pacPos = game.getPacmanCurrentNodeIndex();
		int manDist;
		for(int i = 0; i < pillIndices.length; i++) {
			manDist = game.getManhattanDistance(pacPos, pillIndices[i]);
			if(manDist < minimumDist) {
				minimumDist = manDist;
				minPill = pillIndices[i];
			}
		}
		return game.getNextMoveTowardsTarget(pacPos, minPill, DM.MANHATTAN);
	} 
	
	

}
