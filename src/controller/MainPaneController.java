package controller;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.plaf.basic.BasicSplitPaneUI.KeyboardEndHandler;

import org.apache.commons.io.FileUtils;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.SystemFile;
import model.TypeOfFile;

public class MainPaneController implements Initializable
{
	private static final String INITIAL_PATH = "C:/Users/£ukasz/Desktop/AL test folder";

	private Properties properties;
	private String leftPath;
	private String rightPath;

	private Stage primaryStage;
	private Path sourceDir;
	private Path targetDir;
	private Task<Void> copyTask;
	private int copiedFilesCount;
	private int copiedDirsCount;
	private static Logger logger;

	@FXML
	private TableView<SystemFile> rightTableView;

	@FXML
	private Button deleteButton;

	@FXML
	private MenuItem aboutMenuItem;

	@FXML
	private Button newFolderButton;

	@FXML
	private Label rightLabel;

	@FXML
	private TextField rightPathTextField;

	@FXML
	private Label leftLabel;

	@FXML
	private Label rightPathLabel;

	@FXML
	private MenuItem englishMenuItem;

	@FXML
	private Label copyLabel;

	@FXML
	private ProgressBar bottomProgressBar;

	@FXML
	private Label leftPathLabel;

	@FXML
	private Button moveToLeftButton;

	@FXML
	private Button rightPathButton;

	@FXML
	private Menu languageMenu;

	@FXML
	private Button moveToRightButton;

	@FXML
	private Button copyToLeftButton;

	@FXML
	private Menu fileMenu;

	@FXML
	private TableView<SystemFile> leftTableView;

	@FXML
	private Menu helpMenu;

	@FXML
	private TextField leftPathTextField;

	@FXML
	private Menu editMenu;

	@FXML
	private Button editButton;

	@FXML
	private MenuItem closeMenuItem;

	@FXML
	private Label moveLabel;

	@FXML
	private MenuItem polishMenuItem;

	@FXML
	private Button bottomCancelButton;

	@FXML
	private Button leftPathButton;

	@FXML
	private Button copyToRightButton;

	@FXML
	private Button newFileButton;

	private Label mainLabel;
	private Button cancelButton;
	private TextArea statusArea;
	private ProgressBar progressBar;
	private Button yesButton;
	private Button noButton;
	private Button closeButton;

	private Boolean leftOrRight;

	public MainPaneController()
	{
		properties = new Properties();
		setLanguageProperties("properties/default.properties");

		leftPath = INITIAL_PATH;
		rightPath = INITIAL_PATH;

		leftOrRight = null;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		initTablesLabels();
		initMenuLabels();
		initToolBarLabels();
		initBottom();

		loadFiles();

		langugeMenuItemListener();
		tablesListener();
		textPathSearchInit();
		copyFilesInit();
		toolbarInit();

		// TODO - Sortowanie - nie braæ pod uwagê powracj¹cego elelementu
		// TODO - About (o aplikacji)
		// TODO - zamykanie na przycisk File->close
		// TODO - uwzglêdnianie w wygl¹dzie aplikacji (kolory) preferencji
		// u¿ytkownika zdefiniowanych na poziomie systemu operacyjnego
		// TODO - implementacja asynchronicznego mechanizmu powiadamiaj¹cego o
		// zmianach w systemie plików i odœwie¿aj¹cego listy wyœwietlanych
		// plików
	}

	private void toolbarInit()
	{
		deleteButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				SystemFile selectedLeftFile = leftTableView.getSelectionModel().getSelectedItem();
				SystemFile selectedRightFile = rightTableView.getSelectionModel().getSelectedItem();

				if (leftOrRight != null && leftOrRight == false && selectedLeftFile != null)
				{
					try
					{
						create(selectedLeftFile.toPath(), new File(rightPath).toPath(), 2);
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				} else if (leftOrRight != null && leftOrRight == true && selectedRightFile != null)
				{
					try
					{
						create(selectedRightFile.toPath(), new File(leftPath).toPath(), 2);
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		});

		newFolderButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				if (leftOrRight != null)
				{
					TextInputDialog input = new TextInputDialog();
					input.setTitle(properties.getProperty("new-folder-title", "Nowy folder"));
					input.setHeaderText(properties.getProperty("new-folder-title", "Nowy folder"));
					input.setGraphic(new ImageView("/resource/newFolderWindow.png"));

					// Remove the default buttons and then add your custom ones.
					input.getDialogPane().getButtonTypes().clear();
					input.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonData.OK_DONE));
					input.getDialogPane().getButtonTypes().add(
							new ButtonType(properties.getProperty("cancel-window", "Anuluj"), ButtonData.CANCEL_CLOSE));

					input.setContentText(
							properties.getProperty("new-folder-content", "Podaj nazwê nowego folderu") + ":");

					Stage stage = (Stage) input.getDialogPane().getScene().getWindow();
					stage.getIcons().add(new Image("/resource/logo.png"));

					Optional<String> newFolderName = input.showAndWait();
					if (newFolderName.isPresent())
					{
						File newFolder = null;
						if (leftOrRight != null && leftOrRight == false)
						{
							newFolder = new File(leftPath + "/" + newFolderName.get());
						} else if (leftOrRight != null && leftOrRight == true)
						{
							newFolder = new File(rightPath + "/" + newFolderName.get());
						}
						if (newFolder != null && !newFolder.exists())
						{
							newFolder.mkdir();
							rightLabel.setText(properties.getProperty("succeeded-label", "Sukces"));
							loadFiles();
						} else
						{
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle(properties.getProperty("folder-exists-title", "Folder istnieje"));
							alert.setHeaderText(properties.getProperty("folder-exists-title", "Folder istnieje"));
							alert.setContentText(properties.getProperty("folder-exists-content", "Folder istnieje"));
							Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
							alertStage.getIcons().add(new Image("/resource/logo.png"));

							rightLabel.setText(properties.getProperty("error-label", "B³¹d"));

							alert.showAndWait();
						}
					}
				}
			}
		});

		newFileButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				if (leftOrRight != null)
				{
					TextInputDialog input = new TextInputDialog();
					input.setTitle(properties.getProperty("new-file-title", "Nowy plik"));
					input.setContentText(properties.getProperty("new-file-content", "Podaj nazwê nowego pliku"));
					input.setHeaderText(properties.getProperty("new-file-title", "Nowy plik"));
					input.setGraphic(new ImageView("/resource/newFileWindow.png"));

					// Remove the default buttons and then add your custom ones.
					input.getDialogPane().getButtonTypes().clear();
					input.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonData.OK_DONE));
					input.getDialogPane().getButtonTypes().add(
							new ButtonType(properties.getProperty("cancel-window", "Anuluj"), ButtonData.CANCEL_CLOSE));

					Stage stage = (Stage) input.getDialogPane().getScene().getWindow();
					stage.getIcons().add(new Image("/resource/logo.png")); // To
																			// add
																			// an
																			// icon

					Optional<String> newFileName = input.showAndWait();
					if (newFileName.isPresent())
					{
						File newFolder = null;
						if (leftOrRight != null && leftOrRight == false)
						{
							newFolder = new File(leftPath + "/" + newFileName.get());
						} else if (leftOrRight != null && leftOrRight == true)
						{
							newFolder = new File(rightPath + "/" + newFileName.get());
						}
						if (newFolder != null && !newFolder.exists())
						{
							try
							{
								newFolder.createNewFile();
								rightLabel.setText(properties.getProperty("succeeded-label", "Sukces"));
								loadFiles();
							} catch (IOException e)
							{
								e.printStackTrace();
							}
						} else
						{
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle(properties.getProperty("file-exists-title", "Plik istnieje"));
							alert.setHeaderText(properties.getProperty("file-exists-title", "Plik istnieje"));
							alert.setContentText(
									properties.getProperty("file-exists-content", "Podaj inn¹ nazwê pliku"));
							Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
							alertStage.getIcons().add(new Image("/resource/logo.png")); // To
																						// add
																						// an
																						// icon

							rightLabel.setText(properties.getProperty("error-label", "B³¹d"));

							alert.showAndWait();
						}
					}
				}
			}
		});

		editButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				SystemFile selectedLeftFile = leftTableView.getSelectionModel().getSelectedItem();
				SystemFile selectedRightFile = rightTableView.getSelectionModel().getSelectedItem();
				SystemFile fileToEdit = null;

				if (leftOrRight != null && leftOrRight == false && selectedLeftFile != null)
				{
					fileToEdit = selectedLeftFile;
				} else if (leftOrRight != null && leftOrRight == true && selectedRightFile != null)
				{
					fileToEdit = selectedRightFile;
				}

				if (fileToEdit != null)
				{

					TextInputDialog input = new TextInputDialog();
					input.setTitle(properties.getProperty("rename-title", "Zmieñ nazwê"));
					input.setHeaderText(
							properties.getProperty("rename-title", "Zmieñ nazwê") + " - " + fileToEdit.getFileName());

					input.setContentText(properties.getProperty("raname-content", "Podaj now¹ nazwê") + ":");

					Optional<String> newFileName = input.showAndWait();
					if (newFileName.isPresent())
					{
						File newFile = fileToEdit.getFile();
						newFile.renameTo(new File(fileToEdit.getFile().getAbsolutePath() + "/../" + newFileName.get()));
						loadFiles();
					}
				}
			}
		});
	}

	private void initBottom()
	{
		bottomCancelButton.setText(properties.getProperty("cancel-window", "Anuluj"));
		bottomCancelButton.setDisable(true);
		rightLabel.setText("");
		leftLabel.setText("");
	}

	private void textPathSearchInit()
	{
		leftPathButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				File searchedFile = new File(leftPathTextField.getText());
				if (searchedFile != null && searchedFile.exists())
				{
					SystemFile searchedSystemFile = new SystemFile(searchedFile, properties);
					if (searchedSystemFile != null && searchedSystemFile.exists())
					{
						leftPath = searchedSystemFile.toPath().toString();
						loadFiles();
					}
				}

				leftPathTextField.setText(leftPath);
			}
		});

		leftPathTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				if (event.getCode() == KeyCode.ENTER)
				{
					File searchedFile = new File(leftPathTextField.getText());
					if (searchedFile != null && searchedFile.exists())
					{
						SystemFile searchedSystemFile = new SystemFile(searchedFile, properties);
						if (searchedSystemFile != null && searchedSystemFile.exists())
						{
							leftPath = searchedSystemFile.toPath().toString();
							loadFiles();
						}
					}

					leftPathTextField.setText(leftPath);
				}
			}
		});

		rightPathButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				File searchedFile = new File(rightPathTextField.getText());
				if (searchedFile != null && searchedFile.exists())
				{
					SystemFile searchedSystemFile = new SystemFile(searchedFile, properties);
					if (searchedSystemFile != null && searchedSystemFile.exists())
					{
						rightPath = searchedSystemFile.toPath().toString();
						loadFiles();
					}
				}

				rightPathTextField.setText(rightPath);
			}
		});

		rightPathTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				System.out.println(event.getCode());
				if (event.getCode() == KeyCode.ENTER)
				{
					File searchedFile = new File(rightPathTextField.getText());
					if (searchedFile != null && searchedFile.exists())
					{
						SystemFile searchedSystemFile = new SystemFile(searchedFile, properties);
						if (searchedSystemFile != null && searchedSystemFile.exists())
						{
							rightPath = searchedSystemFile.toPath().toString();
							loadFiles();
						}
					}

					rightPathTextField.setText(rightPath);
				}
			}

		});
	}

	private void copyFilesInit()
	{
		copyToRightButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				// Getting selected file
				SystemFile selectedFile = leftTableView.getSelectionModel().getSelectedItem();
				if (selectedFile != null && selectedFile.getTypeOfFile() != TypeOfFile.ROOT)
				{
					try
					{
						create(selectedFile.toPath(), new File(rightPath).toPath(), 1);
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		});

		copyToLeftButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				// Getting selected file
				SystemFile selectedFile = rightTableView.getSelectionModel().getSelectedItem();
				if (selectedFile != null && selectedFile.getTypeOfFile() != TypeOfFile.ROOT)
				{
					try
					{
						create(selectedFile.toPath(), new File(leftPath).toPath(), 1);
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		});

		moveToRightButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				// Getting selected file
				SystemFile selectedFile = leftTableView.getSelectionModel().getSelectedItem();
				if (selectedFile != null && selectedFile.getTypeOfFile() != TypeOfFile.ROOT)
				{
					try
					{
						create(selectedFile.toPath(), new File(rightPath).toPath(), 0);
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		});

		moveToLeftButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				// Getting selected file
				SystemFile selectedFile = rightTableView.getSelectionModel().getSelectedItem();
				if (selectedFile != null && selectedFile.getTypeOfFile() != TypeOfFile.ROOT)
				{
					try
					{
						create(selectedFile.toPath(), new File(leftPath).toPath(), 0);
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * Copying, moving or deleting file in new thread
	 * 
	 * @param sourceDir
	 *            source path
	 * @param targetDir
	 *            destiny path
	 * @param moveOrCopy
	 *            [0] - moving, [1] - copying, [2] - deleting
	 * @throws IOException
	 */
	public void create(Path sourceDir, Path targetDir, int moveOrCopy) throws IOException
	{
		String newTargetString = targetDir.toString() + "\\" + sourceDir.getFileName();
		String actualFilePath = sourceDir.toString();

		// checking if not trying to copy to the same directory path
		if (newTargetString.equals(actualFilePath) && moveOrCopy != 2)
		{
			return;
		}

		File rightTableRootFile = new File(targetDir.toString());
		File selectedFile = new File(sourceDir.toString());

		this.sourceDir = sourceDir;
		this.targetDir = targetDir;

		// overwriting existing file(s)
		if (Arrays.stream(rightTableRootFile.listFiles()).filter(f -> f.getName().equals(selectedFile.getName()))
				.count() > 0 || moveOrCopy == 2)
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

			this.primaryStage = new Stage();
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image("/resource/logo.png"));
			if (moveOrCopy == 1)
				primaryStage.setTitle("JCommander - " + properties.getProperty("copy-label", "Kopiowanie"));
			else if (moveOrCopy == 0)
				primaryStage.setTitle("JCommander - " + properties.getProperty("move-label", "Przenoszenie"));
			else
				primaryStage.setTitle("JCommander - " + properties.getProperty("delete-label", "Usuwanie"));
			primaryStage.show();

			CopyController copyController = (CopyController) loader.getController();

			if (copyController != null && sourceDir != null && targetDir != null)
			{

				this.mainLabel = copyController.getMainLabel();
				this.cancelButton = copyController.getCancelButton();
				this.statusArea = copyController.getStatusArea();
				this.progressBar = copyController.getProgressBar();
				this.yesButton = copyController.getYesButton();
				this.noButton = copyController.getNoButton();
				this.closeButton = copyController.getCloseButton();
				this.progressBar.setPrefWidth(primaryStage.getWidth() - 35);

				buttonsInit(moveOrCopy);
				copyControllerTextInit(moveOrCopy);
				loggerInit();

			} else if (copyController == null)
				throw new IOException("copyController is null!");
			else if (sourceDir == null)
				throw new IOException("sourceDir is null!");
			else if (targetDir == null)
				throw new IOException("targetDir is null!");
		}
		// copy new file(s)
		else
		{
			bottomCancelButton.setDisable(false);
			bottomCancelButton.setOnAction(e ->
			{
				if (copyTask != null)
				{
					copyTask.cancel();
				}
			});
			copyRoutine(false, moveOrCopy);
		}
	}

	private void loggerInit()
	{
		logger = Logger.getLogger("copy_app_logger");
		TextAreaLogHandler handler = new TextAreaLogHandler(statusArea);
		logger.addHandler(handler);
	}

	private void buttonsInit(int moveOrCopy)
	{
		cancelButton.setOnAction(e ->
		{
			if (copyTask != null)
			{
				copyTask.cancel();
			}
		});
		closeButton.setOnAction(e ->
		{
			if (primaryStage != null)
			{
				primaryStage.close();
			}
		});
		cancelButton.setDisable(true);
		closeButton.setDisable(true);
		yesButton.setOnAction(e -> copyRoutine(true, moveOrCopy));
		noButton.setOnAction(e ->
		{
			if (primaryStage != null)
			{
				primaryStage.close();
			}
		});
	}

	private void copyRoutine(boolean overwriting, int moveOrCopy)
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
					if (overwriting)
					{
						yesButton.setDisable(true);
						noButton.setDisable(true);
						closeButton.setDisable(true);
						cancelButton.setDisable(false);
					} else
					{
						bottomCancelButton.setDisable(false);
						rightLabel.setText("");
					}
				});

				if (fromFile.isDirectory())
				{
					dirsCount = (int) Arrays.stream(fromFile.listFiles()).filter(f -> f.isDirectory()).count();
					filesCount = fromFile.listFiles().length - dirsCount;
				}

				if (!newFile.exists() && moveOrCopy != 2)
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
							if (moveOrCopy == 1)
							{
								Files.copy(dir, target);
								if (overwriting)
								{
									logger.info(
											"Folder " + dir + " " + properties.getProperty("copied-to", "skopiowano do")
													+ " " + target + "\n");
								}
							} else if (moveOrCopy == 0)
							{
								Files.move(dir, target);
								if (overwriting)
								{
									logger.info("Folder " + dir + " "
											+ properties.getProperty("moved-to", "przeniesiono do") + " " + target
											+ "\n");
								}
							} else
							{
								if (overwriting)
								{
									logger.info(properties.getProperty("file-label", "Plik") + " " + dir + " "
											+ properties.getProperty("was-deleted", "zosta³ usuniêty") + "\n");
								}
							}
							copiedDirsCount++;
							updateProgress(++currentCounter, dirsCount + filesCount);

						} catch (FileAlreadyExistsException e)
						{
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

						Path target = targetDir.resolve(sourceDir.relativize(file));
						if (moveOrCopy == 1)
						{
							Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
							if (overwriting)
							{
								logger.info(properties.getProperty("file", "Plik") + " " + file + " "
										+ properties.getProperty("copied-to", "skopiowano do") + " " + targetDir
										+ "\n");
							}
						} else if (moveOrCopy == 0)
						{
							Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);// ,
																							// StandardCopyOption.REPLACE_EXISTING);
							if (overwriting)
							{
								logger.info(properties.getProperty("file", "Plik") + " " + file + " "
										+ properties.getProperty("moved-to", "przeniesiono do") + " " + targetDir
										+ "\n");
							}
						} else
						{
							if (overwriting)
							{
								logger.info(properties.getProperty("file-label", "Plik") + " " + file + " "
										+ properties.getProperty("was-deleted", "zosta³ usuniêty") + "\n");
							}
						}
						copiedFilesCount++;
						updateProgress(++currentCounter, dirsCount + filesCount);
						try
						{
							Thread.sleep(20);
						} catch (InterruptedException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} // pause for n milliseconds
						return FileVisitResult.CONTINUE;
					}
				});

				if (moveOrCopy == 2)
				{
					FileUtils.deleteDirectory(fromFile);
				}
				return null;
			}

		};
		if (overwriting)
			progressBar.progressProperty().bind(copyTask.progressProperty());
		else
			bottomProgressBar.progressProperty().bind(copyTask.progressProperty());
		new Thread(copyTask).start(); // Run the copy task

		copyTask.setOnFailed(e ->
		{
			Throwable t = copyTask.getException();
			String message = (t != null) ? t.toString() : properties.getProperty("error-label", "B³¹d!") + "\n";
			String operation;
			if (moveOrCopy == 1)
				operation = properties.getProperty("copying", "kopiowania");
			else if (moveOrCopy == 0)
				operation = properties.getProperty("moving", "przenoszenia");
			else
				operation = properties.getProperty("deleting", "usuwania");
			String textMessage = properties.getProperty("error-window", "Wyst¹pi³ b³¹d podczas " + operation + ".")
					+ ":\n";
			if (overwriting)
			{
				logger.info(textMessage);
				logger.info(message);
			} else
			{
				System.err.println(textMessage + message);
			}
			rightLabel.setText(properties.getProperty("error-label", "B³¹d!"));
			doTaskEventCloseRoutine(overwriting, moveOrCopy, fromFile);
			t.printStackTrace();
		});

		copyTask.setOnCancelled(e ->
		{
			if (moveOrCopy == 1)
			{
				if (overwriting)
				{
					logger.info(properties.getProperty("cancelled-window", "Kopiowanie anulowane przez u¿ytkownika!"));
					mainLabel.setText(
							properties.getProperty("cancelled-window", "Kopiowanie anulowane przez u¿ytkownika!"));
				} else
				{
					rightLabel.setText(properties.getProperty("cancelled-label", "Anulowano!"));
				}
			} else if (moveOrCopy == 0)
			{
				if (overwriting)
				{
					logger.info(properties.getProperty("cancelled-moving-window",
							"Przenoszenie anulowane przez u¿ytkownika!"));
					mainLabel.setText(properties.getProperty("cancelled-moving-window",
							"Przenoszenie anulowane przez u¿ytkownika!"));
				} else
				{
					rightLabel.setText(properties.getProperty("cancelled-label", "Anulowano!"));
				}
			} else
			{
				if (overwriting)
				{
					logger.info(properties.getProperty("cancelled-deleting-window",
							"Usuwanie anulowane przez u¿ytkownika!"));
					mainLabel.setText(properties.getProperty("cancelled-deleting-window",
							"Usuwanie anulowane przez u¿ytkownika!"));
				}
				rightLabel.setText(properties.getProperty("cancelled-label", "Anulowano!"));
			}
			doTaskEventCloseRoutine(overwriting, moveOrCopy, fromFile);
		});

		copyTask.setOnSucceeded(e ->
		{
			if (moveOrCopy == 1)
			{
				if (overwriting)
					logger.info(properties.getProperty("copy-completed", "Kopiowanie ukoñczone") + ". "
							+ properties.getProperty("directories-copied", "Skopiowane foldery") + " ["
							+ ((copiedDirsCount < 1) ? 0 : copiedDirsCount) + "], "
							+ properties.getProperty("files-copied", "Skopiowane pliki") + " [" + copiedFilesCount
							+ "]\n");
			} else if (moveOrCopy == 0)
			{
				if (overwriting)
					logger.info(properties.getProperty("move-completed", "Przenoszenie ukoñczone") + ". "
							+ properties.getProperty("directories-moved", "Przeniesione foldery") + " ["
							+ ((copiedDirsCount < 1) ? 0 : copiedDirsCount) + "], "
							+ properties.getProperty("files-moved", "Przeniesione pliki") + " [" + copiedFilesCount
							+ "]\n");
			} else
			{
				if (overwriting)
					logger.info(properties.getProperty("delete-completed", "Usuwanie ukoñczone") + ". "
							+ properties.getProperty("directories-deleted", "Usuniête foldery") + " ["
							+ ((copiedDirsCount < 1) ? 0 : copiedDirsCount) + "], "
							+ properties.getProperty("files-deleted", "Usuniête pliki") + " [" + copiedFilesCount
							+ "]\n");
			}
			// setting progress bar to 100% after succeeded
			if (overwriting)
			{
				progressBar.progressProperty().bind(new SimpleDoubleProperty(1));
				if (moveOrCopy == 1)
					mainLabel.setText(properties.getProperty("succeeded-window", "Kopiowanie zakoñczone pomyœlnie"));
				else if (moveOrCopy == 0)
					mainLabel.setText(
							properties.getProperty("succeeded-moving-window", "Przenoszenie zakoñczone pomyœlnie"));
				else
					mainLabel.setText(
							properties.getProperty("succeeded-deleting-window", "Usuwanie zakoñczone pomyœlnie"));
			} else
			{
				bottomProgressBar.progressProperty().bind(new SimpleDoubleProperty(1));
			}
			rightLabel.setText(properties.getProperty("succeeded-label", "Sukces"));

			doTaskEventCloseRoutine(overwriting, moveOrCopy, fromFile);
		});
	}

	private void doTaskEventCloseRoutine(boolean overwriting, int moveOrCopy, File fromFile)
	{
		bottomCancelButton.setDisable(true);
		if (overwriting)
		{
			closeButton.setDisable(false);
			cancelButton.setDisable(true);
		}
		if (moveOrCopy == 0)
			fromFile.delete();
		loadFiles();
	}

	public void copyControllerTextInit(int moveOrCopy)
	{
		String operation;
		if (moveOrCopy == 1)
			operation = properties.getProperty("copy-label", "Kopiowanie");
		else if (moveOrCopy == 0)
			operation = properties.getProperty("move-label", "Przenoszenie");
		else
			operation = properties.getProperty("delete-label", "Usuwnie");
		if (moveOrCopy != 2)
			mainLabel.setText(
					operation + " - " + properties.getProperty("copy-exist-question", "Czy chcesz nadpisaæ pliki?"));
		else
			mainLabel.setText(
					operation + " - " + properties.getProperty("deleting-question", "Czy na pewno chcesz usun¹æ"));
		yesButton.setText(properties.getProperty("yes-window", "Tak"));
		noButton.setText(properties.getProperty("no-window", "Nie"));
		cancelButton.setText(properties.getProperty("cancel-window", "Anuluj"));
		closeButton.setText(properties.getProperty("close-window", "Zamknij"));
	}

	private void tablesListener()
	{
		leftTableView.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				if (event.getClickCount() == 2)
				{
					SystemFile selectedFile = leftTableView.getSelectionModel().getSelectedItem();

					if (selectedFile != null)
					{
						// if file is directory, it turns into next root file
						if (selectedFile.isDirectory())
						{
							leftPath = selectedFile.getFile().getAbsolutePath();
							loadFiles();
						}
						// otherwise open it in default desktop program
						else
						{
							try
							{
								Desktop.getDesktop().open(selectedFile.getFile());
							} catch (IOException e)
							{
								e.printStackTrace();
							}
						}
					}
				}

				leftOrRight = false;
			}
		});

		leftTableView.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.RIGHT)
				{
					SystemFile selectedFile = leftTableView.getSelectionModel().getSelectedItem();

					// if file is directory, it turns into next root file
					if (selectedFile != null)
					{
						if (selectedFile.isDirectory())
						{
							leftPath = selectedFile.getFile().getAbsolutePath();
							loadFiles();
						}
						// otherwise open it in default desktop program
						else
						{
							try
							{
								Desktop.getDesktop().open(selectedFile.getFile());
							} catch (IOException e)
							{
								e.printStackTrace();
							}
						}

						leftOrRight = false;
					}
				} else if (event.getCode() == KeyCode.LEFT)
				{
					leftPath = leftPath + "/..";
					loadFiles();
				}
			}
		});

		rightTableView.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				if (event.getClickCount() == 2)
				{
					SystemFile selectedFile = rightTableView.getSelectionModel().getSelectedItem();

					// if file is directory, it turns into next root file
					if (selectedFile.isDirectory())
					{
						rightPath = selectedFile.getFile().getAbsolutePath();
						loadFiles();
					}
					// otherwise open it in default desktop program
					else
					{
						try
						{
							Desktop.getDesktop().open(selectedFile.getFile());
						} catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}

				leftOrRight = true;
			}
		});

		rightTableView.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.RIGHT)
				{
					SystemFile selectedFile = rightTableView.getSelectionModel().getSelectedItem();

					// if file is directory, it turns into next root file
					if (selectedFile != null)
					{
						if (selectedFile.isDirectory())
						{
							rightPath = selectedFile.getFile().getAbsolutePath();
							loadFiles();
						}
						// otherwise open it in default desktop program
						else
						{
							try
							{
								Desktop.getDesktop().open(selectedFile.getFile());
							} catch (IOException e)
							{
								e.printStackTrace();
							}
						}

						leftOrRight = true;
					}
				} else if (event.getCode() == KeyCode.LEFT)
				{
					rightPath = rightPath + "/..";
					loadFiles();
				}
			}
		});
	}

	private void langugeMenuItemListener()
	{
		polishMenuItem.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				setLanguageProperties("properties/polish.properties");

				initTablesLabels();
				initMenuLabels();
				initToolBarLabels();
				initBottom();

				loadFiles();
			}
		});

		englishMenuItem.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				setLanguageProperties("properties/english.properties");

				initTablesLabels();
				initMenuLabels();
				initToolBarLabels();
				initBottom();

				loadFiles();
			}
		});
	}

	public void loadFiles()
	{
		SystemFile leftRootFile, rightRootFile;
		List<SystemFile> leftSystemFileList = new ArrayList<>();
		List<SystemFile> rightSystemFileList = new ArrayList<>();

		// creating new SystemFiles
		leftRootFile = new SystemFile(new File(leftPath), properties);
		rightRootFile = new SystemFile(new File(rightPath), properties);
		if (leftRootFile.exists() && rightRootFile.exists() && leftRootFile.isDirectory()
				&& rightRootFile.isDirectory())
		{
			leftSystemFileList = leftRootFile.listSystemFiles(properties);
			rightSystemFileList = rightRootFile.listSystemFiles(properties);
		}

		// adding files to observable list
		ObservableList<SystemFile> leftObservableList = FXCollections.observableArrayList(leftSystemFileList);
		ObservableList<SystemFile> rightObservableList = FXCollections.observableArrayList(rightSystemFileList);

		// setting root file
		String leftRootParentName = leftRootFile.getFile().getParent();
		String rightRootParentName = rightRootFile.getFile().getParent();
		SystemFile leftParentFile, rightParentFile;
		if (leftRootParentName != null && rightRootParentName != null)
		{
			leftParentFile = new SystemFile(new File(leftRootParentName), properties);
			leftParentFile.setFileName(leftRootParentName);

			rightParentFile = new SystemFile(new File(rightRootParentName), properties);
			rightParentFile.setFileName(rightRootParentName);
		} else
		{
			leftParentFile = leftRootFile;
			leftParentFile.setFileName("");

			rightParentFile = rightRootFile;
			rightParentFile.setFileName("");
		}
		leftParentFile.setLastModified("");
		leftParentFile.setFileType("");
		leftParentFile.setTypeOfFile(TypeOfFile.ROOT);
		leftParentFile.setSize("");
		ImageView leftImageView = new ImageView(new Image("resource/folderBack.png"));
		leftImageView.setFitHeight(20);
		leftImageView.setFitWidth(20);
		leftParentFile.setImage(leftImageView);

		rightParentFile.setLastModified("");
		rightParentFile.setFileType("");
		rightParentFile.setTypeOfFile(TypeOfFile.ROOT);
		leftParentFile.setSize("");
		ImageView rightImageView = new ImageView(new Image("resource/folderBack.png"));
		rightImageView.setFitHeight(20);
		rightImageView.setFitWidth(20);
		rightParentFile.setImage(rightImageView);

		// adding root file and other files to tables
		leftObservableList.add(0, leftParentFile);
		rightObservableList.add(0, rightParentFile);
		leftTableView.setItems(leftObservableList);
		rightTableView.setItems(rightObservableList);

		// adding path to text box
		try
		{
			leftPathTextField.setText(new File(leftPath.toString()).getCanonicalPath().toString());
			rightPathTextField.setText(new File(rightPath.toString()).getCanonicalPath().toString());
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private void initMenuLabels()
	{
		try
		{
			fileMenu.setText(properties.getProperty("file-menu", "Plik"));
			editMenu.setText(properties.getProperty("edit-menu", "Edycja"));
			languageMenu.setText(properties.getProperty("language-menu", "Jêzyk"));
			helpMenu.setText(properties.getProperty("help-menu", "Pomoc"));
			aboutMenuItem.setText(properties.getProperty("about-menuItem", "O aplikacji"));
			closeMenuItem.setText(properties.getProperty("close-menuItem", "Zamknij"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void initToolBarLabels()
	{
		try
		{
			moveLabel.setText(properties.getProperty("move-label", "Przenieœ"));
			copyLabel.setText(properties.getProperty("copy-label", "Kopiuj"));
			leftPathLabel.setText(properties.getProperty("path-label", "Œcie¿ka:"));
			rightPathLabel.setText(properties.getProperty("path-label", "Œcie¿ka:"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void initTablesLabels()
	{
		try
		{
			TableColumn<SystemFile, ImageView> iconColumn = new TableColumn<SystemFile, ImageView>();
			iconColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
			iconColumn.setMinWidth(30);
			iconColumn.setMaxWidth(30);
			iconColumn.setResizable(false);

			TableColumn<SystemFile, String> nameColumn = new TableColumn<SystemFile, String>(
					properties.getProperty("name-column", "Nazwa"));
			nameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
			nameColumn.prefWidthProperty().bind(leftTableView.widthProperty().divide(3));

			TableColumn<SystemFile, String> sizeColumn = new TableColumn<SystemFile, String>(
					properties.getProperty("size-column", "Rozmiar"));
			sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
			sizeColumn.prefWidthProperty().bind(leftTableView.widthProperty().divide(6));

			TableColumn<SystemFile, Object> typeColumn = new TableColumn<SystemFile, Object>(
					properties.getProperty("type-column", "Typ"));
			typeColumn.setCellValueFactory(new PropertyValueFactory<>("fileType"));
			typeColumn.prefWidthProperty().bind(leftTableView.widthProperty().divide(7));

			TableColumn<SystemFile, String> modifiedColumn = new TableColumn<SystemFile, String>(
					properties.getProperty("modified-column", "Zmodyfikowany"));
			modifiedColumn.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
			modifiedColumn.prefWidthProperty().bind(leftTableView.widthProperty().divide(5));

			TableColumn<SystemFile, ImageView> iconColumnR = new TableColumn<SystemFile, ImageView>();
			iconColumnR.setCellValueFactory(new PropertyValueFactory<>("image"));
			iconColumnR.setMinWidth(30);
			iconColumnR.setMaxWidth(30);
			iconColumnR.setResizable(false);

			TableColumn<SystemFile, Object> nameColumnR = new TableColumn<SystemFile, Object>(
					properties.getProperty("name-column", "Nazwa"));
			nameColumnR.setCellValueFactory(new PropertyValueFactory<>("fileName"));
			nameColumnR.prefWidthProperty().bind(leftTableView.widthProperty().divide(3));

			TableColumn<SystemFile, Object> sizeColumnR = new TableColumn<SystemFile, Object>(
					properties.getProperty("size-column", "Rozmiar"));
			sizeColumnR.setCellValueFactory(new PropertyValueFactory<>("size"));
			sizeColumnR.prefWidthProperty().bind(leftTableView.widthProperty().divide(6));

			TableColumn<SystemFile, Object> typeColumnR = new TableColumn<SystemFile, Object>(
					properties.getProperty("type-column", "Typ"));
			typeColumnR.setCellValueFactory(new PropertyValueFactory<>("fileType"));
			typeColumnR.prefWidthProperty().bind(leftTableView.widthProperty().divide(7));

			TableColumn<SystemFile, String> modifiedColumnR = new TableColumn<SystemFile, String>(
					properties.getProperty("modified-column", "Zmodyfikowany"));
			modifiedColumnR.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
			modifiedColumnR.prefWidthProperty().bind(leftTableView.widthProperty().divide(5));

			if (leftTableView.getColumns().size() > 0)
				leftTableView.getColumns().clear();
			if (rightTableView.getColumns().size() > 0)
				rightTableView.getColumns().clear();

			leftTableView.getColumns().add(iconColumn);
			leftTableView.getColumns().add(nameColumn);
			leftTableView.getColumns().add(sizeColumn);
			leftTableView.getColumns().add(typeColumn);
			leftTableView.getColumns().add(modifiedColumn);

			rightTableView.getColumns().add(iconColumnR);
			rightTableView.getColumns().add(nameColumnR);
			rightTableView.getColumns().add(sizeColumnR);
			rightTableView.getColumns().add(typeColumnR);
			rightTableView.getColumns().add(modifiedColumnR);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setLanguageProperties(String filePath)
	{
		InputStream input = null;
		try
		{
			input = new FileInputStream(filePath);
			this.properties.load(input);
		} catch (IOException ex)
		{
			ex.printStackTrace();
		} finally
		{
			if (input != null)
			{
				try
				{
					input.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
