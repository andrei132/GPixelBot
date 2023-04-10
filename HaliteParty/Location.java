package HaliteParty;

import java.util.Objects;

public class Location {

    // Public for backward compability
    public final int x, y;
    private final Site site;


    public Location(int x, int y, Site site) {
        this.x = x;
        this.y = y;
        this.site = site;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Site getSite() {
        return site;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return x == location.x && y == location.y;
    }


}
