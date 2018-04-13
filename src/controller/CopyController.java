package controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;

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

	private static Logger logger;

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
		logger = Logger.getLogger("copy_app_logger");
		// TextAreaLogHandler handler = new TextAreaLogHandler(statusArea);
		// logger.addHandler(handler);
		// fileFiltersDialog = new FileFilterDialog();
	}

	public CopyController()
	{
	}

	public void create(Path sourceDir, Path targetDir) throws IOException
	{
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

		Stage primaryStage = new Stage();
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
			
			textInit();
			buttonsInit();
		}
		else if(copyController == null)
			throw new IOException("copyController is null!");
		else if(sourceDir == null)
			throw new IOException("sourceDir is null!");
		else if(targetDir == null)
			throw new IOException("targetDir is null!");
	}

	private void buttonsInit()
	{
		
	}

	public void textInit()
	{
		mainLabel.setText(properties.getProperty("copy-label", "Kopiowanie"));
		yesButton.setText(properties.getProperty("yes-window", "Tak"));
		noButton.setText(properties.getProperty("no-window", "Nie"));
		cancelButton.setText(properties.getProperty("cancel-window", "Anuluj"));
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{

	}
}
