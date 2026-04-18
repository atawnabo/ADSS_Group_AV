package domain;
import java.util.Objects;

public class Location {
    private int shelfNum;
    private int aisleNum;

    public Location(int shelfNum, int aisleNum) {
        if (shelfNum < 0 || aisleNum < 0) {
            throw new IllegalArgumentException("Shelf and aisle numbers must be non-negative.");
        }

        this.shelfNum = shelfNum;
        this.aisleNum = aisleNum;
    }

    public int getShelfNum() {
        return shelfNum;
    }

    public void setShelfNum(int shelfNum) {
        if (shelfNum < 0) {
            throw new IllegalArgumentException("Shelf number must be non-negative.");
        }
        this.shelfNum = shelfNum;
    }

    public int getAisleNum() {
        return aisleNum;
    }

    public void setAisleNum(int aisleNum) {
        if (aisleNum < 0) {
            throw new IllegalArgumentException("Aisle number must be non-negative.");
        }
        this.aisleNum = aisleNum;
    }

    @Override
    public String toString() {
        return "Location{" +
                "shelfNum=" + shelfNum +
                ", aisleNum=" + aisleNum +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return shelfNum == location.shelfNum &&
               aisleNum == location.aisleNum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shelfNum, aisleNum);
    }
}