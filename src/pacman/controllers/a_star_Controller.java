package pacman.controllers;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.PriorityQueue;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;


public class a_star_Controller extends Controller<MOVE> {
	
	//initializes ghosts
	 public static StarterGhosts ghosts = new StarterGhosts();
	 

	@Override
	public MOVE getMove(Game game, long timeDue) {
		MOVE[] allMoves=game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
		
		Game gameCopy = game.copy();
		Game gameATM = gameCopy;
		PacManNode pNode = new PacManNode(gameATM, 0, null);
		ArrayList<MOVE> moveList = aStar(pNode, 12);
		System.out.println(moveList.get(0));
		return moveList.get(0);
	}
	
	public ArrayList<MOVE> aStar(PacManNode no, int maxDepth) {
		
		//Initializes Graph
		ArrayList<PacManNode> graph = new ArrayList<PacManNode>();
		graph.add(no);
		
		//Comparator uses score/depth 
		Comparator<PacManNode> comp = new NodeComparator();
		
		//Priority queue that uses score/depth for comparison
		PriorityQueue<PacManNode> OPEN = new PriorityQueue<PacManNode>(comp);
		ArrayList<PacManNode> CLOSED = new ArrayList<PacManNode>();
		
		//Add start node to OPEN
		OPEN.add(no);
		
		//List of moves to output
		ArrayList<MOVE> moveList = new ArrayList<MOVE>();

		
		//Makes sure open is never empty
		while(!OPEN.isEmpty()) {
			//Remove first node off open and add it to closed
			PacManNode n = OPEN.remove();
			CLOSED.add(n);
			
			//Max Depth is used as cutoff for how far to search, i.e. goal state is max depth with highest score
			//Or, 
			if(n.depth >= maxDepth) {
				MOVE lastMove = n.prevMove;
				moveList.add(0, lastMove);
				PacManNode current = n.parent;
				while(!(current.parent == null)) {
					moveList.add(0, current.prevMove);
					current = current.parent;
				}
				return moveList;
				
			}
			else {
				MOVE[] allMoves = n.gameState.getPossibleMoves(n.gameState.getPacmanCurrentNodeIndex());
				for(MOVE m: allMoves) {
					
					//Ignores backwards moves
					//if(m.opposite() == n.prevMove) {
						//System.out.println("Fart");
					//}
					
					//else {
						//Copies gameState
						Game gameCopy = n.gameState.copy();
						//Advances game
						gameCopy.advanceGame(m,  ghosts.getMove(gameCopy, 0));
						//Creates new node with depth + 1 and sets parent to n, the parent node
						PacManNode node = new PacManNode(gameCopy, n.depth + 1, n);
						//Sets prevMove for backtracking
						node.prevMove = m;

						OPEN.add(node);
					//}
				}
			}
		}
		throw new EmptyStackException();
	}	

	
}

