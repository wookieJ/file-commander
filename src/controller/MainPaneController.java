package controller;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import com.sun.glass.ui.CommonDialogs.Type;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.SystemFile;
import model.TypeOfFile;

public class MainPaneController implements Initializable
{
	private static final String INITIAL_PATH = "C:/Users/£ukasz/Desktop/AL test folder";

	private Properties properties;
	private String leftPath;
	private String rightPath;

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
	private ProgressBar progressBar;

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
	private Button leftPathButton;

	@FXML
	private Button copyToRightButton;

	@FXML
	private Button newFileButton;

	private CopyController copyController;

	public MainPaneController()
	{
		properties = new Properties();
		setLanguageProperties("properties/default.properties");

		leftPath = INITIAL_PATH;
		rightPath = INITIAL_PATH;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		copyController = new CopyController(properties);
		
		initTablesLabels();
		initMenuLabels();
		initToolBarLabels();

		loadFiles(0, leftPath); // 0 for left
		loadFiles(1, rightPath); // 1 for right

		langugeMenuItemListener();
		tablesListener();
		textPathSearchInit();

		copyFilesInit();

		// TODO - Sortowanie - nie braæ pod uwagê powracj¹cego elelementu
		// TODO - Klawiatura - szukanie zatwierdzanie enterem
		// TODO - Przegl¹danie - zatwierdzanie elementów enterem
		// TODO - kopia w nowym w¹tku
		// TODO - dodaæ defaultowe wartoœci properties
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
						loadFiles(0, leftPath); // 0 for left
					}
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
						loadFiles(1, rightPath); // 1 for right
					}
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
					// creating new stage - copying box
					try
					{
						copyController.create(selectedFile.toPath(), new File(rightPath).toPath());
					} catch (IOException e)
					{
						e.printStackTrace();
						return;
					}
					 
					/*
					 * This method can be used with the walkFileTree method to
					 * copy a directory and all entries in the directory, or an
					 * entire file-tree where required.
					 */

					/*
					 * UnsupportedOperationException - if the array contains a
					 * copy option that is not supported
					 * FileAlreadyExistsException - if the target file exists
					 * but cannot be replaced because the REPLACE_EXISTING
					 * option is not specified (optional specific exception)
					 * DirectoryNotEmptyException - the REPLACE_EXISTING option
					 * is specified but the file cannot be replaced because it
					 * is a non-empty directory (optional specific exception)
					 * IOException - if an I/O error occursSecurityException -
					 * In the case of the default provider, and a security
					 * manager is installed, the checkRead method is invoked to
					 * check read access to the source file, the checkWrite is
					 * invoked to check write access to the target file. If a
					 * symbolic link is copied the security manager is invoked
					 * to check LinkPermission("symbolic").
					 */
				}
			}

		});
	}

	private void createNewFile(SystemFile selectedFile) throws IOException
	{
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

					// if file is directory, it turns into next root file
					if (selectedFile.isDirectory())
					{
						leftPath = selectedFile.getFile().getAbsolutePath();
						loadFiles(0, leftPath); // 0 for left
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
						loadFiles(1, rightPath); // 1 for right
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

				loadFiles(0, leftPath); // 0 for left
				loadFiles(1, rightPath); // 1 for right
				
				copyController = new CopyController(properties);
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

				loadFiles(0, leftPath); // 0 for left
				loadFiles(1, rightPath); // 1 for right
				
				copyController = new CopyController(properties);
			}
		});
	}

	private void loadFiles(int leftOrRight, String root)
	{
		SystemFile rootFile;
		List<SystemFile> systemFileList = new ArrayList<>();

		// creating new SystemFile
		rootFile = new SystemFile(new File(root), properties);
		if (rootFile.exists() && rootFile.isDirectory())
		{
			systemFileList = rootFile.listSystemFiles(properties);
		}

		// deciding to which table write
		TableView<SystemFile> table;
		if (leftOrRight == 0)
			table = leftTableView;
		else
			table = rightTableView;

		// adding files to observable list
		ObservableList<SystemFile> observableList = FXCollections.observableArrayList(systemFileList);

		// setting root file
		String rootParentName = rootFile.getFile().getParent();
		SystemFile parentFile;
		if (rootParentName != null)
		{
			parentFile = new SystemFile(new File(rootParentName), properties);
			parentFile.setFileName(rootParentName);
		} else
		{
			parentFile = rootFile;
			parentFile.setFileName("");
		}
		parentFile.setLastModified("");
		parentFile.setFileType("");
		parentFile.setTypeOfFile(TypeOfFile.ROOT);
		parentFile.setSize("");
		ImageView imageView = new ImageView(new Image("resource/folderBack.png"));
		imageView.setFitHeight(20);
		imageView.setFitWidth(20);
		parentFile.setImage(imageView);

		// adding root file and other files to tables
		observableList.add(0, parentFile);
		table.setItems(observableList);

		// adding path to text box
		TextField textField;
		if (leftOrRight == 0)
			textField = leftPathTextField;
		else
			textField = rightPathTextField;

		textField.setText(root);
	}

	private void initMenuLabels()
	{
		try
		{
			fileMenu.setText(properties.getProperty("file-menu"));
			editMenu.setText(properties.getProperty("edit-menu"));
			languageMenu.setText(properties.getProperty("language-menu"));
			helpMenu.setText(properties.getProperty("help-menu"));
			aboutMenuItem.setText(properties.getProperty("about-menuItem"));
			closeMenuItem.setText(properties.getProperty("close-menuItem"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void initToolBarLabels()
	{
		try
		{
			moveLabel.setText(properties.getProperty("move-label"));
			copyLabel.setText(properties.getProperty("copy-label"));
			leftPathLabel.setText(properties.getProperty("path-label"));
			rightPathLabel.setText(properties.getProperty("path-label"));
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
					properties.getProperty("name-column"));
			nameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
			nameColumn.prefWidthProperty().bind(leftTableView.widthProperty().divide(3));

			TableColumn<SystemFile, String> sizeColumn = new TableColumn<SystemFile, String>(
					properties.getProperty("size-column"));
			sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
			sizeColumn.prefWidthProperty().bind(leftTableView.widthProperty().divide(6));

			TableColumn<SystemFile, Object> typeColumn = new TableColumn<SystemFile, Object>(
					properties.getProperty("type-column"));
			typeColumn.setCellValueFactory(new PropertyValueFactory<>("fileType"));
			typeColumn.prefWidthProperty().bind(leftTableView.widthProperty().divide(7));

			TableColumn<SystemFile, String> modifiedColumn = new TableColumn<SystemFile, String>(
					properties.getProperty("modified-column"));
			modifiedColumn.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
			modifiedColumn.prefWidthProperty().bind(leftTableView.widthProperty().divide(5));

			TableColumn<SystemFile, ImageView> iconColumnR = new TableColumn<SystemFile, ImageView>();
			iconColumnR.setCellValueFactory(new PropertyValueFactory<>("image"));
			iconColumnR.setMinWidth(30);
			iconColumnR.setMaxWidth(30);
			iconColumnR.setResizable(false);

			TableColumn<SystemFile, Object> nameColumnR = new TableColumn<SystemFile, Object>(
					properties.getProperty("name-column"));
			nameColumnR.setCellValueFactory(new PropertyValueFactory<>("fileName"));
			nameColumnR.prefWidthProperty().bind(leftTableView.widthProperty().divide(3));

			TableColumn<SystemFile, Object> sizeColumnR = new TableColumn<SystemFile, Object>(
					properties.getProperty("size-column"));
			sizeColumnR.setCellValueFactory(new PropertyValueFactory<>("size"));
			sizeColumnR.prefWidthProperty().bind(leftTableView.widthProperty().divide(6));

			TableColumn<SystemFile, Object> typeColumnR = new TableColumn<SystemFile, Object>(
					properties.getProperty("type-column"));
			typeColumnR.setCellValueFactory(new PropertyValueFactory<>("fileType"));
			typeColumnR.prefWidthProperty().bind(leftTableView.widthProperty().divide(7));

			TableColumn<SystemFile, String> modifiedColumnR = new TableColumn<SystemFile, String>(
					properties.getProperty("modified-column"));
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
