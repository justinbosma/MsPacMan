package pacman.controllers;


import java.util.ArrayList;
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

    
    public PacManNode(Game game, int depth)
    {
        this.gameState = game;
        this.depth = depth;
    }
}
