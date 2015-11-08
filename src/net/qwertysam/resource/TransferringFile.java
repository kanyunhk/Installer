package net.qwertysam.resource;

import java.io.InputStream;

public class TransferringFile
{
	public InputStream stream;
	public String pathInJar;
	public String fileName;

	public TransferringFile(InputStream stream, String pathInJar, String fileName)
	{
		this.stream = stream;
		this.pathInJar = pathInJar;
		this.fileName = fileName;
	}
}
