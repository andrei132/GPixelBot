package HaliteParty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GameMap{

    private final Site[][] contents;
    private final Location[][] locations;
    private final ArrayList<Location> iteratorLocations;
    public final int width, height;

    public GameMap(int width, int height, int[][] productions) {

        this.width = width;
        this.height = height;
        this.contents = new Site[width][height];
        this.locations = new Location[width][height];

        for (int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                final Site site = new Site(productions[x][y]);
                contents[x][y] = site;
                locations[x][y] = new Location(x, y, site);
            }
        }
        this.iteratorLocations = this.iteratorLocations();

    }

    public boolean inBounds(Location loc) {
        return loc.x < width && loc.x >= 0 && loc.y < height && loc.y >= 0;
    }

    public double getDistance(Location loc1, Location loc2) {
        int dx = Math.abs(loc1.x - loc2.x);
        int dy = Math.abs(loc1.y - loc2.y);

        if(dx > width / 2.0) dx = width - dx;
        if(dy > height / 2.0) dy = height - dy;

        return dx + dy;
    }

    public double getAngle(Location loc1, Location loc2) {
        int dx = loc1.x - loc2.x;

        // Flip order because 0,0 is top left
        // and want atan2 to look as it would on the unit circle
        int dy = loc2.y - loc1.y;

        if(dx > width - dx) dx -= width;
        if(-dx > width + dx) dx += width;

        if(dy > height - dy) dy -= height;
        if(-dy > height + dy) dy += height;

        return Math.atan2(dy, dx);
    }

    public Location getLocation(Location location, Direction direction) {
        switch (direction) {
            case STILL:
                return location;
            case NORTH:
                return locations[location.getX()][(location.getY() == 0 ? height : location.getY()) -1];
            case EAST:
                return locations[location.getX() == width - 1 ? 0 : location.getX() + 1][location.getY()];
            case SOUTH:
                return locations[location.getX()][location.getY() == height - 1 ? 0 : location.getY() + 1];
            case WEST:
                return locations[(location.getX() == 0 ? width : location.getX()) - 1][location.getY()];
            default:
                throw new IllegalArgumentException(String.format("Unknown direction %s encountered", direction));
        }
    }

    public Site getSite(Location loc, Direction dir) {
        return getLocation(loc, dir).getSite();
    }

    public Site getSite(Location loc) {
        return loc.getSite();
    }

    public Location getLocation(int x, int y) {
        return locations[x][y];
    }

    void reset() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final Site site = contents[x][y];
                site.owner = 0;
                site.strength = 0;
            }
        }
    }

    // Additional methods

    /**
     * Location[][] -> ArrayList<Location>
     * @return new ArrayList with all locations
     */
    private ArrayList<Location> iteratorLocations () {
        ArrayList<Location> Ilocations = new ArrayList<>();
        for (int y = 0; y < this.height; y++)
            for (int x = 0; x < this.width; x++)
                Ilocations.add(this.locations[x][y]);
        return Ilocations;
    }

    /**
     * Get for iterable locations
     * @return ArrayList with all locations
     */
    public ArrayList<Location> getIteratorLocations () {
        return iteratorLocations;
    }

    /**
     * Get all neighbors
     * @param location Location
     * @param includeSelf If in neighbors to include self location
     * @return ArrayList with all neighbors
     */
    public ArrayList<Location> getNeighbors(Location location, boolean includeSelf){
        ArrayList<Location> locations = new ArrayList<>();
        for (Direction direction : Direction.DIRECTIONS) {
            if(direction.equals(Direction.STILL)) {
                if (includeSelf) locations.add(location);
                else continue;
            }
            locations.add(this.getLocation(location,direction));
        }

        return locations;
    }

    /**
     * Return Manhattan distances between two locations
     * @param loc1 First Location
     * @param loc2 Second Location
     * @return Distance
     */
    public int getDistanceBetween2Location(Location loc1, Location loc2){
        int distanceX = Math.min(Math.abs(loc1.x - loc2.x), Math.min(loc1.x + width - loc2.x, loc2.x + width + loc1.x));
        int distanceY = Math.min(Math.abs(loc1.y - loc2.y), Math.min(loc1.y + width - loc2.y, loc2.y + width + loc1.y));;
        return distanceX + distanceY;
    }

    /**
     * Return Manhattan distances between two locations and path
     * @param loc1 First Location
     * @param loc2 Second Location
     * @return DistanceX, DistanceY and distance
     */
    public int[] getDistanceBetween2LocationWithPath(Location loc1, Location loc2){
        int distanceX = Math.min(Math.abs(loc1.x - loc2.x), Math.min(loc1.x + width - loc2.x, loc2.x + width + loc1.x));
        int distanceY = Math.min(Math.abs(loc1.y - loc2.y), Math.min(loc1.y + width - loc2.y, loc2.y + width + loc1.y));
        return new int[]{distanceX, distanceY, distanceX + distanceY};
    }

    /**
     * Find number of neighbors near location with ID
     * @param location Location to search
     * @param ID ID of neighbors
     * @return Number of neighbors
     */
    public int getNeighborsNumberWithID(Location location, int ID){
        ArrayList<Location> neighbors = this.getNeighbors(location,true);
        int count = 0;
        for (Location neighbor : neighbors) {
            if(neighbor.getSite().owner == ID) count++;
        }
        return count;
    }

    /**
     * Find direction from loc1 to loc2
     * @param loc1 Start location
     * @param loc2 End location
     * @return Direction
     */
    public Direction getDirection(Location loc1, Location loc2){

        if(this.getLocation(loc1,Direction.NORTH).equals(loc2)) return Direction.NORTH;
        if(this.getLocation(loc1,Direction.SOUTH).equals(loc2)) return Direction.SOUTH;
        if(this.getLocation(loc1,Direction.WEST).equals(loc2)) return Direction.WEST;
        if(this.getLocation(loc1,Direction.EAST).equals(loc2)) return Direction.EAST;

        return Direction.STILL;
    }

    /**
     * Return direction from location1 to location2 give priority to X direction
     * @param location First location
     * @param location1 Second location
     * @return Direction where to go
     */
    public Direction findPathToLocationPriorityX (Location location, Location location1){
        if(location.x > location1.x) return Direction.WEST;
        if(location.x < location1.x) return Direction.EAST;
        if(location.y > location1.y) return Direction.NORTH;
        if(location.y < location1.y) return Direction.SOUTH;
        return Direction.STILL;
    }

    /**
     * Return direction from location1 to location2 give priority to Y direction
     * @param location First location
     * @param location1 Second location
     * @return Direction where to go
     */
    public Direction findPathToLocationPriorityY (Location location, Location location1){
        if(location.y > location1.y) return Direction.NORTH;
        if(location.y < location1.y) return Direction.SOUTH;
        if(location.x > location1.x) return Direction.WEST;
        if(location.x < location1.x) return Direction.EAST;
        return Direction.STILL;
    }

    /**
     * return direction for currentLocation to targetLocation
     * @param currentLocation location of my site
     * @param targetLocation location where my site must be
     * @param moveMap unused
     * @return Direction where to go
     */
    public Direction tryFindOptimalDirection(Location currentLocation, Location targetLocation,
                                             HashMap<Location, Direction> moveMap){
        return findPathToLocationPriorityX(currentLocation,targetLocation);
    }

    /**
     * Find best target location
     * @param location Current location
     * @param targets Must be sorted ArrayList<Location> with targets
     * @return Direction where to go
     */
    public Direction getNearestProductionDirectionAtBorder(Location location, ArrayList<Location> targets,
                                                           HashMap<Location, Direction> moveMap){
        int min = width*height + 1;
        int maxDistanceAdmit = width/2 + height/2;
        Location location1 = null;

        for (Location target : targets) {
            int curr = getDistanceBetween2Location(location,target);
            if(curr < min){
                min = curr;
                location1 = target;
            }
        }

        if( min < maxDistanceAdmit){
            assert location1 != null;
            return tryFindOptimalDirection(location, location1, moveMap);
        }

        return Direction.STILL;
    }

    /**
     * Check if all neighbor are owned be ID
     * @param location Location to be checked
     * @param ID ID that must be
     * @return true if all neighbor are with ID
     */
    public boolean onlyMyLocation(Location location, int ID){
        for (Location neighbor : this.getNeighbors(location, true)) {
            if(neighbor.getSite().owner != ID) return false;
        }

        return true;
    }

    /**
     * Return how many percentage have bot with ID
     * @param ID Bot ID
     * @return Percentage
     */
    public int getPercentage(int ID){
        int count = 0;
        for (Location iteratorLocation : this.iteratorLocations) {
            if(iteratorLocation.getSite().owner == ID) count++;
        }
        return (int) ((((double)count)/(width*height))*100);
    }

    /**
     * If still and a fat cell come to you, change position with fat cell
     * @param moves Arraylist with all moves
     */
    public void tryToAvoidCollision(HashMap<Location, Direction> moves, int ID, int timeToLive){

        if (timeToLive <= 0) return;
        for (Location location : moves.keySet()) {
            if(moves.get(location).equals(Direction.STILL) && location.getSite().strength > 0){
                for (Direction cardinal : Direction.CARDINALS) {
                    Location neighbor = this.getLocation(location,cardinal);
                    if(neighbor.getSite().owner == ID)
                        if(moves.get(neighbor).equals(Direction.opposite(cardinal))
                                && neighbor.getSite().strength > 255 - 5 * location.getSite().production){
                            moves.put(location,cardinal);
                            break;
                        }
                }
            }

            if(location.getSite().strength > 0){
                Location futureLocation = this.getLocation(location,moves.get(location));
                if(futureLocation.getSite().owner != ID) continue;
                for (Location neighbor : this.getNeighbors(futureLocation, false)) {
                    if(neighbor.equals(location)) continue;
                    if(neighbor.getSite().owner != ID) continue;
                    if(moves.get(neighbor).equals(Direction.opposite(this.getDirection(futureLocation,neighbor)))
                            && neighbor.getSite().strength > 265 - location.getSite().strength)
                        moves.put(neighbor,Direction.STILL);
                }

            }
        }

        tryToAvoidCollision(moves, ID, timeToLive - 1);
    }

    /** Danu
     * Compute the amount of overkill damage dealt by my cell if moves to a direction
     * @param adjacentLocation the location after a move in a direction
     * @param myStrength the strength of my cell
     * @param ID my bot's ID
     * @return the amount of overkill damage
     */
    public int calculateDamage(Location adjacentLocation, int myStrength, int ID) {
        int dealtDamage = 0;
        for (Direction cardinal : Direction.CARDINALS) {
            if (this.getSite(adjacentLocation, cardinal).owner != ID
                    && this.getSite(adjacentLocation, cardinal).owner != 0) {
                dealtDamage += myStrength;
            }
        }

        return dealtDamage;
    }

    /**
     * Change direction of border cell if overkill is higher in another direction
     * @param moves table with all current moves
     * @param atBorder table with all border cells
     * @param ID my bot's ID
     */
    public void getMaxOverkill(HashMap<Location, Direction> moves, HashSet<Location> atBorder, int ID) {
        for (Location location : atBorder) {
            Direction currentAttackDirection = moves.get(location);
            // Calculate current damage;
            int maxOverkillDamage = calculateDamage(this.getLocation(location,currentAttackDirection),
                    location.getSite().strength, ID);

            Direction changedDirection = currentAttackDirection;

            for (Direction cardinal : Direction.DIRECTIONS) {
                if (cardinal != currentAttackDirection) {
                    int newOverkill = calculateDamage(this.getLocation(location, cardinal),
                            location.getSite().strength, ID);

                    if (newOverkill > maxOverkillDamage) {
                        maxOverkillDamage = newOverkill;
                        changedDirection = cardinal;
                    }
                }
            }
            moves.put(location, changedDirection);
        }
    }

    /** Danu
     * Determine if a cell is at border
     * @param location the checked cell
     * @param ID our bot's ID
     * @return true if at border / false if in inner area
     */
    public boolean atBorder (Location location, int ID) {
        for (Direction cardinal : Direction.CARDINALS) {
            if (this.getSite(location, cardinal).owner != ID)
                return true;
        }
        return false;
    }

}
