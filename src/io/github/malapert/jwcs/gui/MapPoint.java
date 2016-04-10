/**
 * Apache License 2
 */
package io.github.malapert.jwcs.gui;

public final class MapPoint {

    public MapPoint() {
    }

    public MapPoint (double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        MapPoint p = (MapPoint)obj;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }

    public double x;
    public double y;
}
