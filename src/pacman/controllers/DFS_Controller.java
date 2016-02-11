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

public class DFS_Controller extends Controller<MOVE> {

	 public static StarterGhosts ghosts = new StarterGhosts();
	 
	 // get move method obtains available moves and advances ms Pacman in highest scoring direction
		public MOVE getMove(Game game,long timeDue)
		{
				//Gets possible moves. Eliminates moving into walls
	            MOVE[] allMoves=game.getPossibleMoves(game.getPacmanCurrentNodeIndex());	        
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
	                int tempHighScore = this.dfs(new PacManNode(gameAtM, 0), 10);
	                
	                
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
	        // Travels graph until depth is equal to max depth. 
			//Selects the highest scoring node to go to
	        public int dfs(PacManNode rootGameState, int maxdepth)
		{

	            int highScore = -1;
			
	            Deque<PacManNode> stack = new ArrayDeque<PacManNode>();
	            stack.push(rootGameState);

			//System.out.println("Adding Node at Depth: " + rootGameState.depth);
	                
	  			//While the stack is not empty and the depth does not equal max depth
	  			//keep adding the various states to the stack. The last state on is the first one traversed.
	            while(!stack.isEmpty())
	                {
	                    PacManNode pmnode = stack.pop();
	                    
	                    //get all possible moves
	                    MOVE[] allMoves = pmnode.gameState.getPossibleMoves(pmnode.gameState.getPacmanCurrentNodeIndex());
	                    
	                    System.out.println("Removing Node at Depth: " + pmnode.depth + " Score " + pmnode.gameState.getScore());
	                    
	                    if(pmnode.depth >= maxdepth)
	                    {
	                        int score = pmnode.gameState.getScore();
	                       
	                         if (highScore < score)
	                                  highScore = score;
	                    }
	                    
	                    else
	                    {

	                        //Add children to stack for each allowable move
	                        for(MOVE m: allMoves)
	                        {
	                        	int d = pmnode.depth +1;
	                        	System.out.println(m + " " + d);
	                        	
	                        	//Copies game state for advancing 
	                            Game gameCopy = pmnode.gameState.copy();
	                            
	                            //Advances game in direction m
	                            gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));
	                            
	                            //Sets new PacMan node with gameCopy and adds 1 to depth
	                            PacManNode node = new PacManNode(gameCopy, pmnode.depth+1);
	                            stack.push(node);
	                        }
	                    }

			}
	                
	                return highScore;
		}
	        
	    
	}


