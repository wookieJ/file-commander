package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

	public String getSize()
	{
		return size.get();
	}

	public String getLastModified()
	{
		return lastModified;
	}

	public String getFileType()
	{
		return fileType.get();
	}

	public SystemFile(File file)
	{
		try
		{
			if (file.exists())
			{
				this.file = file;
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
				this.lastModified = sdf.format(new Date(file.lastModified()));
				if (file.isFile())
					this.size = new SimpleStringProperty(String.valueOf(Files.size(file.toPath())) + "b");
				else
					this.size = new SimpleStringProperty("");
				this.fileName = new SimpleStringProperty(file.getName());

				// ImageIcon icon = (ImageIcon)
				// FileSystemView.getFileSystemView().getSystemIcon(file);
				// this.image = new ImageView(icon.getImage());

				this.image = new ImageView(new Image("resource/logo.png"));
				image.setFitHeight(20);
				image.setFitWidth(20);

				if (file.isFile())
					this.fileType = new SimpleStringProperty(
							"Plik " + file.getName().substring(file.getName().lastIndexOf(".") + 1));
				else
					this.fileType = new SimpleStringProperty("Folder plików");
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
	public ArrayList<SystemFile> listSystemFiles()
	{
		ArrayList<SystemFile> systemFiles = new ArrayList<>();
		for (File f : getFile().listFiles())
		{
			systemFiles.add(new SystemFile(f));
		}

		return systemFiles;
	}
}
