package pacman.controllers;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
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

//Code structure taken from Amy Hoovers BFS algorithm
public class DFS_Controller extends Controller<MOVE> {
	
	//initializes ghosts
	 public static StarterGhosts ghosts = new StarterGhosts();
	 
	 // get move method obtains available moves and advances ms Pacman in highest scoring direction
		public MOVE getMove(Game game,long timeDue)
		{
				//Gets possible moves. Eliminates moving into walls
	            MOVE[] allMoves=game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());	        
	            int highScore = -1;
	            MOVE highMove = null;
	            
	           
	            for(MOVE m: allMoves)
	            {
	                System.out.println("Trying Move: " + m);
	                
	                //Copies game state for DFS
	                Game gameCopy = game.copy();
	                Game gameAtM = gameCopy;
	                
	                //Advances copy of game
	                gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
	                int tempHighScore = this.dfsRecursive(new PacManNode(gameAtM, 0, null), 100);
	                
	                //Sets the highest move from running dfs
	                if(highScore < tempHighScore)
	                {
	                    highScore = tempHighScore;
	                    highMove = m;
	                }
	                
	                System.out.println("Trying Move: " + m + ", Score: " + tempHighScore);
	               
	            }
	            
	            System.out.println("High Score: " + highScore + ", High Move:" + highMove);
	              return highMove;
	                
		}
	        
	        public int dfsRecursive(PacManNode rootGameState, int maxDepth) {
	        	int highScore = -1;
	        	PacManNode pmNode = rootGameState;
	        	MOVE[] allMoves = pmNode.gameState.getPossibleMoves(pmNode.gameState.getPacmanCurrentNodeIndex(), pmNode.gameState.getPacmanLastMoveMade());
	        	
	        	if(rootGameState.depth >= maxDepth) {
	        		int score = pmNode.gameState.getScore();
                    
                    if (highScore < score) {
                             highScore = score;
                    }
                    return score;
	        		
	        	}
	        	
	        	else {
	        		for(MOVE m: allMoves) {

	        			
	        			Game gameCopy = pmNode.gameState.copy();
	        			gameCopy.advanceGame(m,  ghosts.getMove(gameCopy, 0));
	        			PacManNode node = new PacManNode(gameCopy, pmNode.depth+1, null);
	        			int score = dfsRecursive(node, maxDepth);

	        			System.out.println(m + " score: " + score + " depth: " + pmNode.depth);
	                       
                        if (highScore < score)
                                 highScore = score;
	        		}
	        	}
	        	return highScore;
	        }
	        
	    
	}


