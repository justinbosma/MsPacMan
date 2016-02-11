package pacman.controllers;


import java.util.ArrayList;
import java.util.List;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;


public class PacManNode 
{
    Game gameState;
    int depth;
    //int score;
    
    public PacManNode(Game game, int depth)
    {
        this.gameState = game;
        this.depth = depth;
        //this.score = score;
    }
}
