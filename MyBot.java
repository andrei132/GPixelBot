import GPixelBot.GPixelBot;
import HaliteParty.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyBot {
    public static void main(String[] args){

        final InitPackage iPackage = Networking.getInit();
        final int myID = iPackage.myID;
        final GameMap gameMap = iPackage.map;

        GPixelBot gPixelBot = new GPixelBot(myID, gameMap);
        gPixelBot.getUtilsMap().createCostMap();
        Networking.sendInit("GPixelBot");

        while(true) {
            List<Move> moves = new ArrayList<Move>();
            HashMap<Location, Direction> movesHashTable = new HashMap<>();

            Networking.updateFrame(gameMap);
            gPixelBot.getUtilsMap().createCostMap();
            ArrayList<Location> targetLocations = gPixelBot.getTargetProds();

            gPixelBot.getUtilsMap().updateBorderCells(gPixelBot.getID());

            if (gPixelBot.isAtPiece()) gPixelBot.getUtilsMap().sortExpansionTarget(targetLocations);
            else gPixelBot.getUtilsMap().sortAttackTargets(targetLocations);

            gPixelBot.getUtilsMap().createMoveMap(gPixelBot.getID());
            gPixelBot.getUtilsMap().createEnemyInfluenceMap(gPixelBot.getID());


            for (int y = 0; y < gameMap.height; y++) {
                for (int x = 0; x < gameMap.width; x++) {

                    final Location location = gameMap.getLocation(x, y);
                    final Site site = location.getSite();
                    if(site.owner == myID) {
                        movesHashTable.put(location,gPixelBot.getMoveExpansion(location, targetLocations));
                    }
                }
            }

            gameMap.getMaxOverkill(movesHashTable, gPixelBot.getUtilsMap().getBorderCells(), gPixelBot.getID());
            gPixelBot.borderDefence();
            gameMap.tryToAvoidCollision(movesHashTable, gPixelBot.getID(), 6);

            for (Location location : movesHashTable.keySet()) {
                moves.add(new Move(location,movesHashTable.get(location)));
            }

            Networking.sendFrame(moves);
        }
    }
}
