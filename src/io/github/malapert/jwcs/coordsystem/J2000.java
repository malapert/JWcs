/*
 * Copyright (C) 2014-2016 malapert
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.malapert.jwcs.coordsystem;

/**
 *
 * @author malapert
 */
public class J2000 implements ReferenceSystemInterface {

    /**
     * The name of this reference frame.
     */
    private final static ReferenceSystemInterface.Type REF_SYSTEM = ReferenceSystemInterface.Type.J2000;

    /**
     * The default value of the equinox.
     */
    private final static float DEFAULT_EQUINOX = 2000.0f;

    /**
     * The epoch of the equinox.
     */
    private float equinox;

    /**
     * Creates J2000 frame.
     */
    public J2000() {
        init(DEFAULT_EQUINOX);
    }

    /**
     * initialization.
     *
     * @param equinox the equinox
     */
    private void init(float equinox) {
        this.equinox = equinox;
    }

    @Override
    public Type getReferenceSystemType() {
        return REF_SYSTEM;
    }

    @Override
    public Float getEpochObs() {
        return null;
    }

    @Override
    public float getEquinox() {
        return this.equinox;
    }

    @Override
    public String toString() {
        return "J2000";
    }        

}
