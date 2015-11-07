package net.qwertysam.util;

import java.io.File;

public class MinecraftDirUtil
{
	private static final String USERNAME = System.getProperty("user.name");
	private static final String SEP = File.separator;
	private static final String WINDOWS_7_PATH = "C:" + SEP + "Users" + SEP + USERNAME + SEP + "AppData" + SEP + "Roaming" + SEP + ".minecraft";
	private static final String WINDOWS_XP_PATH = "C:" + SEP + "Users" + SEP + USERNAME + SEP + "AppData" + SEP + "Roaming" + SEP + ".minecraft"; // TODO find out what it is
	private static final String LINUX_PATH = "~" + SEP + USERNAME + "home" + SEP + ".minecraft"; // TODO find out what it is
	private static final String MAC_PATH = "~" + SEP + USERNAME + "home" + SEP + ".minecraft"; // TODO find out what it is
	
	public static String getDefaultMinecraftPath()
	{
		switch(OsUtil.getOSType())
		{
			case WINDOWS:
				if (new File(WINDOWS_7_PATH).exists()) return WINDOWS_7_PATH;
				else if (new File(WINDOWS_XP_PATH).exists()) return WINDOWS_XP_PATH;
				break;
			case OSX:
				if (new File(MAC_PATH).exists()) return MAC_PATH;
				break;
			case LINUX:
				if (new File(LINUX_PATH).exists()) return LINUX_PATH;
				break;
			default:
				break;
		}
		return null;
	}
}
