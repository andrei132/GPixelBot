package GPixelBot;

import HaliteParty.Direction;
import HaliteParty.GameMap;
import HaliteParty.Location;

import java.util.ArrayList;

public class GPixelBot {
    private final int ID;
    private final GameMap gameMap;
    private final UtilsMap utilsMap;
    private boolean atPiece;

    public GPixelBot (int ID, GameMap gameMap) {
        this.ID = ID;
        this.gameMap = gameMap;
        this.utilsMap = new UtilsMap(gameMap);
        this.atPiece = true;
    }

    public boolean isAtPiece () {
        return atPiece;
    }

    public void setAtPiece (boolean atPiece) {
        this.atPiece = atPiece;
    }

    public int getID () {
        return ID;
    }

    public GameMap getGameMap () {
        return gameMap;
    }

    public UtilsMap getUtilsMap () {
        return utilsMap;
    }

    /**
     * Get target at border
     * @return ArrayList<Location> with targets
     */
    public ArrayList<Location> getTargetProds(){

        // Get targets from neighborhood
        ArrayList<Location> targets = new ArrayList<>();
        for (Location iteratorLocation : gameMap.getIteratorLocations()) {

            if(iteratorLocation.getSite().owner == 0 && this.gameMap.getNeighborsNumberWithID(iteratorLocation,ID) != 0
            && iteratorLocation.getSite().production > 0) targets.add(iteratorLocation);

        }

        return targets;
    }

    /**
     * Get move for expansion
     * @param location My location
     * @param targetLocations Must be sorted Arraylist<Location> with all target at border
     * @return Direction where to go
     */
    public Direction getMoveExpansion (Location location, ArrayList<Location> targetLocations ){
        Direction d;
        for (Location targetLocation : targetLocations) {
            if (this.utilsMap.getEnemyInfluenceMap().get(targetLocation).value > 0)
                this.atPiece = false;

            d = this.gameMap.getDirection(location,targetLocation);
            if(!d.equals(Direction.STILL) && (location.getSite().strength > targetLocation.getSite().strength + 1))
                return d;
        }

        return goToHelp(location, targetLocations);
    }

    /**
     * Return direction if is not at border
     * @param location Current location
     * @param targets Must be sorted Arraylist<Location> with all target at border
     * @return Direction where to go
     */
    public Direction goToHelp(Location location, ArrayList<Location> targets){
        if(!this.gameMap.onlyMyLocation(location,this.ID)) return Direction.STILL;
        if(location.getSite().strength < location.getSite().production * 5) return Direction.STILL;
        Direction direction = gameMap.getNearestProductionDirectionAtBorder(location, targets, this.utilsMap.getMovesMap());

        return direction;
    }

    public void borderDefence(){
        for (Location borderCell : this.utilsMap.getBorderCells()) {
            if(!this.utilsMap.getMovesMap().get(borderCell).equals(Direction.STILL)){
                for (Location neighbor : this.gameMap.getNeighbors(borderCell,false)) {
                    if(neighbor.getSite().owner != ID) continue;
                    if(!this.gameMap.atBorder(neighbor,this.ID)
                            && this.utilsMap.getMovesMap().get(neighbor).equals(Direction.STILL))
                        this.utilsMap.getMovesMap().put(neighbor, this.gameMap.getDirection(neighbor,borderCell));

                }

            }
        }

    }
}
