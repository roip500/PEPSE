package pepse.world;

public class WorldEdges {

    private int worldsRightEdge;
    private int worldsLeftEdge;

    /**
     * this class contains the right and left edges of our world
     */
    public WorldEdges(int worldsLeftEdge, int worldsRightEdge){
        this.worldsRightEdge = worldsRightEdge;
        this.worldsLeftEdge = worldsLeftEdge;
    }

    /**
     * this function updates the value of the right edge of our world
     * @param worldsRightEdge the new right edge of the world
     */
    public void setWorldsRightEdge(int worldsRightEdge){
        this.worldsRightEdge = worldsRightEdge;
    }

    /**
     * this function updates the value of the left edge of our world
     * @param worldsLeftEdge the new left edge of the world
     */
    public void setWorldsLeftEdge(int worldsLeftEdge){
        this.worldsLeftEdge = worldsLeftEdge;
    }

    /**
     * @return the current right edge of the world
     */
    public int getWorldsRightEdge(){
        return worldsRightEdge;
    }

    /**
     * @return the current left edge of the world
     */
    public int getWorldsLeftEdge(){
        return worldsLeftEdge;
    }
}
