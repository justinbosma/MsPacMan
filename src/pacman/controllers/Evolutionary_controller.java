package pacman.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;



public class Evolutionary_controller extends Controller<MOVE>{

	public static StarterGhosts ghosts = new StarterGhosts();
	 
	@Override
	public MOVE getMove(Game game, long timeDue) {
		//Depth is how many times to evolve
		int depth = 20;
		//Used for random stuff
		int r;
		Random rnd = new Random();
		//How many members in pool of parents
		int poolSize = 8;
		//How many future moves calculate
		int numMoves = 8;

		//Get all moves
		MOVE[] allMoves = MOVE.values();
		
		//The move to output
		MOVE m;
		
		//Comparator uses score
		Comparator<PacManNode> comp = new NodeComparator();
		
		//Priority queue that uses score for comparison
		//Is the pool for the moves lists. Associate with node for advancing
		PriorityQueue<PacManNode> pool = new PriorityQueue<PacManNode>(comp);
		
		//Used for passing into evolve function
		Game copy = game.copy();
		Game currentGameATM = copy;
		
		//Randomly generate initial population
		for(int i = 0; i < poolSize; i++) {
			System.out.println(i);
			ArrayList<MOVE> moveList = new ArrayList<MOVE>();
			//Randomly inserts moves into each list
			for(int j=0; j < numMoves; j++) {
				
				r = rnd.nextInt(4);
				System.out.println("j = " + j);
				System.out.println("adding " + allMoves[r]);
				moveList.add(allMoves[r]);
			}
			
			Game gameCopy = game.copy();
			Game gameATM = gameCopy;
			PacManNode pNode = new PacManNode(gameATM, 1, null);
			pNode.moveList = moveList;
			//Advances node before putting on priority queue so score is obtained
			advancePacManNode(pNode, timeDue);
			pool.add(pNode);
		}
		
		 m = evolve(pool, depth, allMoves, currentGameATM, timeDue);
		 System.out.println("Move " + m);
		return m;
	}
	
	public MOVE evolve(PriorityQueue<PacManNode> pool, int depth, MOVE[] allMoves, Game gameATM, long timeDue) {
		//Keep evolving until depth is reached. Depth is the evolution cutoff
		for(int i=0; i < depth; i++) {
			//Removes the week nodes, creates new based on strong, and invokes the random moveList method
			removeTheWeak(pool, depth, allMoves, gameATM, timeDue);
		}
		PacManNode goodNode = pool.remove();
		MOVE move = goodNode.moveList.get(0);
		return move;

	}
	
	//Removes and murders half the nodes, the ones at the end of the priority queue
	//Then adds new stock to pool using randomMoveAdd method
	public void removeTheWeak(PriorityQueue<PacManNode> pool, int depth, MOVE[] allMoves, Game gameATM, long timeDue) {
		PacManNode node;
		//Holder for good nodes
		ArrayList<ArrayList<MOVE>> helper = new ArrayList<ArrayList<MOVE>>();
		//How many nodes we gonna murder
		int murderSize = Math.round(pool.size()/2);
		
		for(int i=0; i < murderSize; i++) {
			node = pool.remove();
			helper.add(node.moveList);
		}
		//Kill em all
		pool.clear();
		
		//Creates two new nodes. The second is a copy of the first but with the randomly changed move list
		for(int k=0; k < helper.size(); k++) {
			//Create new node
			PacManNode pNode1 = new PacManNode(gameATM, 1 , null);
			//Copy the old Move list
			pNode1.moveList = helper.get(k);
			//Create new node that is copy of other
			PacManNode pNode2 = pNode1.copy();
			//Randomize the moveList of node2
			randomMoveAdd(pNode2.moveList, depth, allMoves, timeDue);
			
			//Advance the nodes to get score
			advancePacManNode(pNode1, timeDue);
			advancePacManNode(pNode2, timeDue);
			
			//Put them in the pool
			pool.add(pNode1);
			pool.add(pNode2);
		}
	}
	

	
	//Takes in the move list of a pacMan node and randomly chooses to change one of the values in the list
	public void randomMoveAdd(ArrayList<MOVE> moveList, int depth, MOVE[] allMoves, long timeDue) {
		
		int r1, r2;
		Random rnd = new Random();
		//Randomly inserts moves into each list
		for(int j=0; j < moveList.size(); j++) {
			r1 = rnd.nextInt(4);
			//1/4 chance to swap value in jth place
			if(r1 == 0) {
				//Randomly selects move from list of moves
				r2 = rnd.nextInt(allMoves.length);
				moveList.set(j, allMoves[r2]);
			}
		}
	}
	
	//Advances PacManNode in the direction of the associated move list
	public void advancePacManNode(PacManNode pNode, long timeDue) {
		Game nodeGameAtM = pNode.gameState;
		ArrayList<MOVE> moveList = pNode.moveList;
		for(int i=0; i < moveList.size(); i++) {
			nodeGameAtM.advanceGame(moveList.get(i), ghosts.getMove(nodeGameAtM, timeDue));
			
		}
	}

}
