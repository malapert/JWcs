/*
 * Copyright (C) 2014-2016 Jean-Christophe Malapert
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
package io.github.malapert.jwcs.proj.exception;

import io.github.malapert.jwcs.proj.Projection;
import java.util.logging.Level;

/**
 * Pixel Beyond Projection Exception
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 */
public class PixelBeyondProjectionException extends ProjectionException {
    private final static long serialVersionUID = -3719985099230583709L;

    /**
     * Creates a PixelBeyondProjectionException based on a message.
     *
     * @param projectionName Projection
     * @param message message
     */
    public PixelBeyondProjectionException(final Projection projectionName, final String message) {
        super(projectionName, message);
        getProjection().getLogger().log(Level.FINE, "{0} - Solution not defined for {1}", new Object[]{this.getProjection().getClass().getName(), getMessage()});
        
    }

    @Override
    public String toString() {
        return this.getProjection().getClass().getName() + " - Solution not defined for " + getMessage();
    }

}
