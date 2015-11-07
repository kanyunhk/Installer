package net.qwertysam.resource;

public enum EnumOS
{
    LINUX("LINUX"),
    SOLARIS("SOLARIS"),
    WINDOWS("WINDOWS"),
    OSX("OSX"),
    UNKNOWN("UNKNOWN");

	private String name;
	
    private EnumOS(String osName) 
    {
    	name = osName;
    }
    
    public String getOsName()
    {
    	return name;
    }
}
