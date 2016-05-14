/**
 * Apache License 2
 */
package io.github.malapert.jwcs.proj.gui;

/**
 *
 * @author malapert
 */
public final class MapPoint {

    /**
     * Constructor.
     */
    public MapPoint() {
    }

    /**
     * Constructor.
     * @param x coordinate along X axis
     * @param y coordinate along Y axis
     */
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

    /**
     * X coordinate along X axis.
     */
    public double x;
    
    /**
     * Y coordinate along Y axis.
     */
    public double y;
}
