/*
 * Copyright (C) 2018 Heinrich-Heine-Universitaet Duesseldorf, Institute of Computer Science,
 * Department Operating Systems
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package de.hhu.bsinfo.neutrino.api.util.service.resolve;

import de.hhu.bsinfo.neutrino.api.util.FormattedRuntimeException;

public class CircularDependencyException extends FormattedRuntimeException {

    public CircularDependencyException(String format, Object... args) {
        super(format, args);
    }

    public CircularDependencyException(String format, Throwable cause, Object... args) {
        super(format, cause, args);
    }

    public CircularDependencyException(String format, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object... args) {
        super(format, cause, enableSuppression, writableStackTrace, args);
    }
}
