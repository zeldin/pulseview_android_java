/*
 * This file is part of the PulseView project.
 *
 * Copyright (C) 2014 Marcus Comstedt <marcus@mc.pp.se>
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

package org.sigrok.androidutils;

public final class Environment
{
    static private String[] environment;

    public static String[] getEnvironment()
    {
	return environment;
    }

    public static void initEnvironment(String apkFile)
    {
	environment = new String[]
	{
	    "PYTHONHOME", ".",
	    "PYTHONPATH", apkFile+"/assets/python3.3",
	    "SIGROKDECODE_DIR", apkFile+"/assets/libsigrokdecode/decoders",
	};
    }
}
