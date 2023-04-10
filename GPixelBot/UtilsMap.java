package GPixelBot;

import HaliteParty.Direction;
import HaliteParty.GameMap;
import HaliteParty.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class UtilsMap {

    static class HelpClass {
        Integer value;
        boolean isPrimal;

        public HelpClass () {
            this.value = 0;
            this.isPrimal = false;
        }

        public Integer getValue () {
            return value;
        }

        public HelpClass setValue (Integer value) {
            this.value = value;
            return this;
        }

        public boolean isPrimal () {
            return isPrimal;
        }

        public HelpClass setPrimal (boolean primal) {
            isPrimal = primal;
            return this;
        }
    }

    private final GameMap gameMap;
    private final HashMap <Location, Integer> costMap;
    private final HashMap <Location, HelpClass> enemyInfluenceMap;
    private final HashMap<Location, Direction> movesMap;
    private final HashSet <Location> borderCells;



    public UtilsMap (GameMap gameMap) {
        this.gameMap = gameMap;
        this.costMap = new HashMap<>();
        this.enemyInfluenceMap = new HashMap<>();
        this.movesMap = new HashMap<>();
        this.borderCells = new HashSet<>();
    }

    public HashSet<Location> getBorderCells() {
        return borderCells;
    }

    public HashMap<Location, Integer> getCostMap () {
        return costMap;
    }

    public HashMap<Location, HelpClass> getEnemyInfluenceMap () {
        return enemyInfluenceMap;
    }

    /**
     * Create a cost map with heuristic productivity/strength * 1000
     */
    public void createCostMap(){
        for (Location iteratorLocation : this.gameMap.getIteratorLocations()) {
            ArrayList<Integer> ratio = new ArrayList<>();
            for (Location neighbor : gameMap.getNeighbors(iteratorLocation, true)) {
                if (neighbor.getSite().owner == 0)
                    ratio.add((int)(((double)(neighbor.getSite().production + 1))/(neighbor.getSite().strength + 1)*1000));
            }
            int intRatio = ratio.stream().reduce(0, Integer::sum);
            this.costMap.put(iteratorLocation,(intRatio + 1)/(ratio.size() + 1));
        }
    }

    private void setValueInEnemyInfluenceMap(Location location, int valueToSet, Direction direction){
        if (!this.enemyInfluenceMap.get(this.gameMap.getLocation(location, direction)).isPrimal)
            this.enemyInfluenceMap.put(this.gameMap.getLocation(location, direction),
                                       new HelpClass().setValue(valueToSet));
    }

    private void completeEnemyInfluenceMap(){
        for (Location location : gameMap.getIteratorLocations()) {
            if(this.enemyInfluenceMap.get(location).isPrimal){

                this.setValueInEnemyInfluenceMap(location,
                                                 2 + this.enemyInfluenceMap.get(this.gameMap.getLocation(location, Direction.NORTH)).value,
                                                 Direction.NORTH);

                this.setValueInEnemyInfluenceMap(location,
                                                 2 + this.enemyInfluenceMap.get(this.gameMap.getLocation(location, Direction.SOUTH)).value,
                                                 Direction.SOUTH);

                this.setValueInEnemyInfluenceMap(location,
                                                 2 + this.enemyInfluenceMap.get(this.gameMap.getLocation(location, Direction.WEST)).value,
                                                 Direction.WEST);

                this.setValueInEnemyInfluenceMap(location,
                                                 2 + this.enemyInfluenceMap.get(this.gameMap.getLocation(location, Direction.EAST)).value,
                                                 Direction.EAST);

                Location refLocation = this.gameMap.getLocation(location, Direction.NORTH);
                this.setValueInEnemyInfluenceMap(refLocation,
                                                 1 + this.enemyInfluenceMap.get(this.gameMap.getLocation(refLocation, Direction.NORTH)).value,
                                                 Direction.NORTH);

                this.setValueInEnemyInfluenceMap(refLocation,
                                                 1 + this.enemyInfluenceMap.get(this.gameMap.getLocation(refLocation, Direction.WEST)).value,
                                                 Direction.WEST);

                this.setValueInEnemyInfluenceMap(refLocation,
                                                 1 + this.enemyInfluenceMap.get(this.gameMap.getLocation(refLocation, Direction.EAST)).value,
                                                 Direction.EAST);

                refLocation = this.gameMap.getLocation(location,Direction.SOUTH);
                this.setValueInEnemyInfluenceMap(refLocation,
                                                 1 + this.enemyInfluenceMap.get(this.gameMap.getLocation(refLocation, Direction.SOUTH)).value,
                                                 Direction.SOUTH);

                this.setValueInEnemyInfluenceMap(refLocation,
                                                 1 + this.enemyInfluenceMap.get(this.gameMap.getLocation(refLocation, Direction.WEST)).value,
                                                 Direction.WEST);

                this.setValueInEnemyInfluenceMap(refLocation,
                                                 1 + this.enemyInfluenceMap.get(this.gameMap.getLocation(refLocation, Direction.EAST)).value,
                                                 Direction.EAST);

                refLocation = this.gameMap.getLocation(location,Direction.WEST);
                this.setValueInEnemyInfluenceMap(refLocation,
                                                 1 + this.enemyInfluenceMap.get(this.gameMap.getLocation(refLocation, Direction.SOUTH)).value,
                                                 Direction.SOUTH);

                this.setValueInEnemyInfluenceMap(refLocation,
                                                 1 + this.enemyInfluenceMap.get(this.gameMap.getLocation(refLocation, Direction.NORTH)).value,
                                                 Direction.NORTH);

                this.setValueInEnemyInfluenceMap(refLocation,
                                                 1 + this.enemyInfluenceMap.get(this.gameMap.getLocation(refLocation, Direction.WEST)).value,
                                                 Direction.WEST);

                refLocation = this.gameMap.getLocation(location,Direction.EAST);
                this.setValueInEnemyInfluenceMap(refLocation,
                                                 1 + this.enemyInfluenceMap.get(this.gameMap.getLocation(refLocation, Direction.SOUTH)).value,
                                                 Direction.SOUTH);

                this.setValueInEnemyInfluenceMap(refLocation,
                                                 1 + this.enemyInfluenceMap.get(this.gameMap.getLocation(refLocation, Direction.NORTH)).value,
                                                 Direction.NORTH);

                this.setValueInEnemyInfluenceMap(refLocation,
                                                 1 + this.enemyInfluenceMap.get(this.gameMap.getLocation(refLocation, Direction.EAST)).value,
                                                 Direction.EAST);
            }
        }

    }

    /**
     * Create an influence map for enemy, influence from enemy cell is 3 moves
     * @param ID my id
     */
    public void createEnemyInfluenceMap(int ID){
        for (Location location : this.gameMap.getIteratorLocations()) {
            if(location.getSite().owner != 0 && location.getSite().owner != ID && location.getSite().strength > 0){
                this.enemyInfluenceMap.put(location, new HelpClass().setPrimal(true).setValue(3));
            } else this.enemyInfluenceMap.put(location, new HelpClass().setPrimal(false).setValue(0));
        }
        this.completeEnemyInfluenceMap();
    }

    /**
     * Sort target give by Attack strategy
     * @param targetLocations ArrayList with all target location
     */
    public void sortAttackTargets (ArrayList<Location> targetLocations) {
        targetLocations.sort((a, b) -> this.enemyInfluenceMap.get(b).value.compareTo(
                this.enemyInfluenceMap.get(a).value));
    }

    /**
     * Sort target given by expansion strategy
     * @param targetLocations ArrayList with all target
     */
    public void  sortExpansionTarget (ArrayList<Location> targetLocations) {
        targetLocations.sort((a,b) -> this.costMap.get(b).compareTo(
                this.costMap.get(a)));
    }

    public HashMap<Location, Direction> getMovesMap () {
        return movesMap;
    }

    /**
     * Put at all location STILL moves in moveHashTable
     * @param ID my id
     */
    public void createMoveMap(int ID){
        for (Location iteratorLocation : gameMap.getIteratorLocations()) {
            if(iteratorLocation.getSite().owner == ID)
                this.movesMap.put(iteratorLocation, Direction.STILL);
        }

    }


    /** Danu
     * Updates the Set of borderCells before each turn
     * @param ID our bot's ID
     */
    public void updateBorderCells(int ID) {
        this.borderCells.clear();
        for (Location iteratorLocation : gameMap.getIteratorLocations()) {
            if (this.gameMap.getSite(iteratorLocation).owner == ID && this.gameMap.atBorder(iteratorLocation, ID) )
                this.borderCells.add(iteratorLocation);
        }
    }



}
