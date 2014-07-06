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
