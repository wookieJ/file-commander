package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class CopyController implements Initializable
{
	private Properties properties;
	private Stage primaryStage;

	@FXML
	private TextArea statusArea;

	@FXML
	private Button cancelButton;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private Button yesButton;

	@FXML
	private Label mainLabel;

	@FXML
	private Button noButton;

	private Path sourceDir;
	private Path targetDir;
	private Task<Void> copyTask;
	private int copiedFilesCount;
	private int copiedDirsCount;

	public Path getSourceDir()
	{
		return sourceDir;
	}

	public void setSourceDir(Path sourceDir)
	{
		this.sourceDir = sourceDir;
	}

	public Path getTargetDir()
	{
		return targetDir;
	}

	public void setTargetDir(Path targetDir)
	{
		this.targetDir = targetDir;
	}

	public Properties getProperties()
	{
		return properties;
	}

	public void setProperties(Properties properties)
	{
		this.properties = properties;
	}

	public CopyController(Properties properties)
	{
		this.properties = properties;
	}

	public CopyController()
	{
	}

	public void create(Path sourceDir, Path targetDir) throws IOException
	{
		String newTargetString = targetDir.toString() + "\\" + sourceDir.getFileName();
		String actualFilePath = sourceDir.toString();

		// checking if not trying to copy to the same directory path
		if (newTargetString.equals(actualFilePath))
		{
			return;
		}

		// Load root layout from fxml file.
		FXMLLoader loader = new FXMLLoader();
		BorderPane rootLayout = null;
		try
		{
			rootLayout = loader.load(getClass().getResource("/view/CopyPane.fxml").openStream());
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		this.primaryStage = new Stage();
		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image("/resource/logo.png"));
		primaryStage.setTitle("JCommander - " + properties.getProperty("copy-label"));
		primaryStage.show();

		CopyController copyController = (CopyController) loader.getController();

		if (copyController != null && sourceDir != null && targetDir != null)
		{
			this.mainLabel = copyController.mainLabel;
			this.cancelButton = copyController.cancelButton;
			this.statusArea = copyController.statusArea;
			this.progressBar = copyController.progressBar;
			this.yesButton = copyController.yesButton;
			this.noButton = copyController.noButton;
			this.sourceDir = sourceDir;
			this.targetDir = targetDir;

			this.progressBar.setPrefWidth(primaryStage.getWidth() - 35);

			File rightTableRootFile = new File(targetDir.toString());
			File selectedFile = new File(sourceDir.toString());
			// checing if right table contains file we want to copy
			if (Arrays.stream(rightTableRootFile.listFiles()).filter(f -> f.getName().equals(selectedFile.getName()))
					.count() <= 0)
			{
				noButton.setDisable(true);
				cancelButton.setOnAction(e ->
				{
					if (copyTask != null)
					{
						copyTask.cancel();
					}
				});
				cancelButton.setDisable(false);
				copyRoutine(false);
			} else
			{
				buttonsInit();
			}

			textInit();

		} else if (copyController == null)
			throw new IOException("copyController is null!");
		else if (sourceDir == null)
			throw new IOException("sourceDir is null!");
		else if (targetDir == null)
			throw new IOException("targetDir is null!");
	}

	private void buttonsInit()
	{
		cancelButton.setOnAction(e ->
		{
			if (copyTask != null)
			{
				copyTask.cancel();
			}
		});
		cancelButton.setDisable(true);
		yesButton.setOnAction(e -> copyRoutine(true));
	}

	private void copyRoutine(boolean overwriting)
	{
		File fromFile = new File(sourceDir.toString());
		File newFile = new File(targetDir.toString() + "/" + sourceDir.getFileName());
		this.copiedFilesCount = 0;
		this.copiedDirsCount = 0;

		copyTask = new Task<Void>()
		{
			int currentCounter;
			int dirsCount = 0;
			int filesCount = 0;

			@Override
			protected Void call() throws Exception
			{

				Platform.runLater(() ->
				{
					yesButton.setDisable(true);
					noButton.setDisable(true);
					cancelButton.setDisable(false);
				});

				if (fromFile.isDirectory())
				{
					dirsCount = (int) Arrays.stream(fromFile.listFiles()).filter(f -> f.isDirectory()).count();
					filesCount = fromFile.listFiles().length - dirsCount;
				}

				Thread.sleep(100); // pause for n milliseconds

				if (!newFile.exists())
				{
					if (fromFile.isDirectory())
					{
						newFile.mkdir();
					} else
						newFile.createNewFile();
				}

				targetDir = newFile.toPath();

				Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>()
				{
					/*
					 * Copy the directories.
					 */
					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
					{
						if (isCancelled())
						{

							System.out.println("Cancelled");
							return FileVisitResult.TERMINATE;
						}

						Path target = targetDir.resolve(sourceDir.relativize(dir));

						try
						{
							Files.copy(dir, target);
							statusArea.appendText("Directory " + dir + " copied to " + target + "\n");
							copiedDirsCount++;
							updateProgress(++currentCounter, dirsCount + filesCount);
						} catch (FileAlreadyExistsException e)
						{
							System.out.println("File exist");
							if (!Files.isDirectory(target))
							{
								throw e;
							}
						}
						return FileVisitResult.CONTINUE;
					}

					/*
					 * Copy the files.
					 */
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
					{
						if (isCancelled())
						{
							System.out.println("Cancelled");
							return FileVisitResult.TERMINATE;
						}

						Files.copy(file, targetDir.resolve(sourceDir.relativize(file)),
								StandardCopyOption.REPLACE_EXISTING);
						statusArea.appendText("File " + file + " copied to " + targetDir + "\n");
						copiedFilesCount++;
						updateProgress(++currentCounter, dirsCount + filesCount);
						return FileVisitResult.CONTINUE;
					}
				});
				return null;
			}
		};

		progressBar.progressProperty().bind(copyTask.progressProperty());
		new Thread(copyTask).start(); // Run the copy task

		copyTask.setOnFailed(e ->
		{
			Throwable t = copyTask.getException();
			String message = (t != null) ? t.toString() : "Unknown Exception!\n";
			statusArea.appendText("There was an error during the copy process:\n");
			statusArea.appendText(message);
			doTaskEventCloseRoutine(copyTask, overwriting);
			t.printStackTrace();
		});

		copyTask.setOnCancelled(e ->
		{
			statusArea.appendText("Copy is cancelled by user.\n");
			doTaskEventCloseRoutine(copyTask, overwriting);
		});

		copyTask.setOnSucceeded(e ->
		{
			statusArea.appendText(
					"Copy completed. " + "Directories copied [" + ((copiedDirsCount < 1) ? 0 : copiedDirsCount) + "], "
							+ "Files copied [" + copiedFilesCount + "]\n");

			doTaskEventCloseRoutine(copyTask, overwriting);
		});
	}

	private void doTaskEventCloseRoutine(Task<Void> copyTask2, boolean overwriting)
	{
		// closing copying window
		if (!overwriting)
			primaryStage.close();
	}

	public void textInit()
	{
		mainLabel.setText(properties.getProperty("copy-label", "Kopiowanie") + " - "
				+ properties.getProperty("copy-exist-question", "Czy chcesz nadpisaæ pliki?"));
		yesButton.setText(properties.getProperty("yes-window", "Tak"));
		noButton.setText(properties.getProperty("no-window", "Nie"));
		cancelButton.setText(properties.getProperty("cancel-window", "Anuluj"));
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{

	}
}
