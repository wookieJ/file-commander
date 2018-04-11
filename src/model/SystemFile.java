package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public class SystemFile
{
	private File file;
	private Long size;
	private Long lastModified;
	private String fileType;

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public Long getSize()
	{
		return size;
	}

	public void setSize(Long size)
	{
		this.size = size;
	}

	public Long getLastModified()
	{
		return lastModified;
	}

	public void setLastModified(Long lastModified)
	{
		this.lastModified = lastModified;
	}

	public String getFileType()
	{
		return fileType;
	}

	public void setFileType(String fileType)
	{
		this.fileType = fileType;
	}

	public SystemFile(File file)
	{
		try
		{
			if (file.exists())
			{
				this.file = file;
				this.lastModified = file.lastModified();
				this.size = Files.size(file.toPath());
				if (file.isFile())
					this.fileType = "Plik " + file.getName().substring(file.getName().lastIndexOf(".") + 1);
				else
					this.fileType = "Folder plików";
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Properties prop = new Properties(); InputStream input = null; try { input
	 * = new FileInputStream("properties/default.properties"); prop.load(input);
	 * System.out.println(prop.getProperty("hello")); } catch (IOException ex) {
	 * ex.printStackTrace(); } finally { if (input != null) { try {
	 * input.close(); } catch (IOException e) { e.printStackTrace(); } } }
	 */
}
