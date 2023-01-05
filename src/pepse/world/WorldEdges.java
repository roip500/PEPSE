package pepse.world;

public class WorldEdges {

    private int worldsRightEdge;
    private int worldsLeftEdge;

    public WorldEdges(int worldsRightEdge, int worldsLeftEdge){
        this.worldsRightEdge = worldsRightEdge;
        this.worldsLeftEdge = worldsLeftEdge;
    }

    public void setWorldsRightEdge(int worldsRightEdge){
        this.worldsRightEdge = worldsRightEdge;
    }

    public void setWorldsLeftEdge(int worldsLeftEdge){
        this.worldsLeftEdge = worldsLeftEdge;
    }

    public int getWorldsRightEdge(){
        return worldsRightEdge;
    }

    public int getWorldsLeftEdge(){
        return worldsLeftEdge;
    }
}
