package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SystemFile
{
	private File file;
	private ImageView image;
	private SimpleStringProperty fileName;
	private SimpleStringProperty size;
	private SimpleStringProperty fileType;
	private String lastModified;
	private Date lastModifiedDate;
	

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public ImageView getImage()
	{
		return image;
	}

	public void setImage(ImageView image)
	{
		this.image = image;
	}

	public String getFileName()
	{
		return fileName.get();
	}

	public void setFileName(String fileName)
	{
		this.fileName = new SimpleStringProperty(fileName);
	}

	public String getSize()
	{
		return size.get();
	}

	public void setSize(String size)
	{
		this.size = new SimpleStringProperty(size);
	}

	public String getFileType()
	{
		return fileType.get();
	}

	public void setFileType(String fileType)
	{
		this.fileType = new SimpleStringProperty(fileType);
	}

	public String getLastModified()
	{
		return lastModified;
	}

	public void setLastModified(String lastModified)
	{
		this.lastModified = lastModified;
	}
	
	public Date getLastModifiedDate()
	{
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate)
	{
		this.lastModifiedDate = lastModifiedDate;
	}

	public SystemFile(File file, Properties properties)
	{
		try
		{
			if (file.exists())
			{
				this.file = file;
				
				// data format
				SimpleDateFormat sdf = new SimpleDateFormat(properties.getProperty("date-formater"));
				this.lastModifiedDate = new Date(file.lastModified());
				if(file.lastModified() != 0)
					this.lastModified = sdf.format(lastModifiedDate);
				else
					this.lastModified = "";
				
				// setting size of file
				if (file.isFile())
				{
					long size = Files.size(file.toPath());
					if (size < 1024)
						this.size = new SimpleStringProperty(String.valueOf(size) + "B");
					else if (size < 1_024_000)
						this.size = new SimpleStringProperty((String.format("%.1f", (double)size / 1_024)) + "kB");
					else if (size < 1_024_000_000)
						this.size = new SimpleStringProperty((String.format("%.1f", (double)size / 1_024_000)) + "MB");
					else
						this.size = new SimpleStringProperty((String.format("%.1f", (double)size / 1_024_000_000)) + "GB");
				} else
					this.size = new SimpleStringProperty("");
				
				// setting name of file
				this.fileName = new SimpleStringProperty(file.getName());

				
				// setting image from icon
				// ImageIcon icon = (ImageIcon)
				// FileSystemView.getFileSystemView().getSystemIcon(file);
				// this.image = new ImageView(icon.getImage());

				// TODO - Convert file icon into imageView
				if (file.isDirectory())
					this.image = new ImageView(new Image("resource/logo.png"));
				else
					this.image = new ImageView(new Image("resource/newFile.png"));
				image.setFitHeight(20);
				image.setFitWidth(20);

				// setting type of file based of extension
				if (file.isFile())
					this.fileType = new SimpleStringProperty(
							properties.getProperty("file") + " " + file.getName().substring(file.getName().lastIndexOf(".") + 1));
				else
					this.fileType = new SimpleStringProperty(properties.getProperty("folder"));
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public String toString()
	{
		return "SystemFile [file=" + file.getPath() + ", size=" + size + ", lastModified=" + lastModified
				+ ", fileType=" + fileType + "]";
	}

	/**
	 * Checking if file exists.
	 * 
	 * @return true if exists, false otherwise
	 */
	public boolean exists()
	{
		return getFile().exists();
	}

	/**
	 * Checking if file is a directory.
	 * 
	 * @return true if file is a directory
	 */
	public boolean isDirectory()
	{
		return getFile().isDirectory();
	}

	/**
	 * Getting list of SystemFiles
	 * 
	 * @return
	 */
	public ArrayList<SystemFile> listSystemFiles(Properties properties)
	{
		ArrayList<SystemFile> systemFiles = new ArrayList<>();
		for (File f : getFile().listFiles())
		{
			systemFiles.add(new SystemFile(f, properties));
		}

		return systemFiles;
	}
}
