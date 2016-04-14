package pacman.controllers;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

//Code taken from Amy Hoover
//Is used to create nodes for advancing the game
//Gamestate is the state of the game at the moment
//depth is the limit to how far the algorithm goes
public class PacManNode 
{
    Game gameState;
    int depth;
    MOVE prevMove;
    int score;
    PacManNode parent;
    PacManNode[] children; 
    ArrayList<MOVE> moveList;

    
    public PacManNode(Game game, int depth, PacManNode parent)
    {
        this.gameState = game;
        this.depth = depth;
        this.score = game.getScore();
        this.parent = parent;
    }
    
    public PacManNode(Game game)
    {
        this.gameState = game;
        this.score = game.getScore();
    }
    
    public PacManNode copy() {
    	PacManNode node = new PacManNode(this.gameState);
    	node.depth = this.depth;
    	node.prevMove = this.prevMove;
    	node.score = this.score;
    	node.parent = this.parent;
    	node.children = this.children;
    	node.moveList = this.moveList;
    	return node;
    }


	
}

class NodeComparator implements Comparator<PacManNode> {
	
	
	@Override
	public int compare(PacManNode o1, PacManNode o2) {
		float diff;
		int out;
		diff = (o1.gameState.getScore()/o1.depth) - (o2.gameState.getScore()/o2.depth);
		out = Math.round(diff);
		return out;
	}
}
