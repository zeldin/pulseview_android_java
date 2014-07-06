package org.sigrok.androidutils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;

public final class LibWrangler
{
    protected static String stripSoVersion(String s)
    {
        int p = s.lastIndexOf(".so.");
        if (p >= 0)
            s = s.substring(0, p+3);
        return s;
    }

    protected static void makeLink(File from, File to)
        throws IOException
    {
        if (to.equals(from.getCanonicalFile())) {
            // Symlink already correct
            return;
        }
        from.delete();
        int rc;
        Process proc =
            Runtime.getRuntime().exec(new String[]{"ln", "-s",
                                                   to.getAbsolutePath(),
                                                   from.getAbsolutePath()});
        for(;;) {
            try {
                rc = proc.waitFor();
                break;
            } catch(InterruptedException ie) {
            }
        }
        if (rc != 0)
            throw new IOException("Failed to create symlink "+from);
    }

    public static void setupLibs(BufferedReader reader, File libDir1, File libDir2)
        throws IOException
    {
        if (!libDir2.exists())
            libDir2.mkdir();

        String libname;
        while ((libname = reader.readLine()) != null) {
            File shlib = new File(libDir2, libname);
            makeLink(shlib, new File(libDir1, stripSoVersion(libname)));
            System.load(shlib.getAbsolutePath());
        }
    }

    public static void setupLibs(InputStream is, File libDir1, File libDir2)
        throws IOException
    {
	setupLibs(new BufferedReader(new InputStreamReader(is, "US-ASCII")),
		  libDir1, libDir2);
    }
}
