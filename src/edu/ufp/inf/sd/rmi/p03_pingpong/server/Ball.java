package edu.ufp.inf.sd.rmi.p03_pingpong.server;

import java.io.Serializable;

public class Ball implements Serializable {
    private final int playerID;

    // Constructor with basic ball setup
    public Ball(int playerID) {
        this.playerID = playerID;
    }

    // Getters and setters for ball's position and velocity
    public int getPlayerID() {
        return playerID;
    }

    @Override
    public String toString() {
        return "Ball [playerID=" + playerID + "]";
    }
}