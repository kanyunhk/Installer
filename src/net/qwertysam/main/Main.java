package net.qwertysam.main;

import net.qwertysam.gui.Form1;
import net.qwertysam.util.MinecraftDirUtil;

public class Main
{
	public static void main(String[] args)
	{
		System.out.println(MinecraftDirUtil.getDefaultMinecraftPath());
		new Form1();
	}
}
