package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import model.SystemFile;

public class MainPaneController implements Initializable
{
	private static final String INITIAL_PATH = "C:/";

	private Properties properties;
	private String leftPath;
	private String rightPath;

	@FXML
	private TableView<SystemFile> rightTableView;

	@FXML
	private Button deleteButton;

	@FXML
	private TableView<SystemFile> leftTableView;

	@FXML
	private MenuItem aboutMenuItem;

	@FXML
	private Button newFolderButton;

	@FXML
	private Button copyButton;

	@FXML
	private Menu helpMenu;

	@FXML
	private Button copyButton1;

	@FXML
	private Label rightLabel;

	@FXML
	private TextField leftPathTextField;

	@FXML
	private TextField rightPathTextField;

	@FXML
	private Menu editMenu;

	@FXML
	private Label leftLabel;

	@FXML
	private Label copyLabel;

	@FXML
	private Button editButton;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private MenuItem closeMenuItem;

	@FXML
	private Button moveToLeftButton;

	@FXML
	private Label moveLabel;

	@FXML
	private Menu languageMenu;

	@FXML
	private Button moveToRightButton;

	@FXML
	private Button newFileButton;

	@FXML
	private Menu fileMenu;

	public MainPaneController()
	{
		properties = new Properties();
		setLanguage("properties/default.properties");

		leftPath = INITIAL_PATH;
		rightPath = INITIAL_PATH;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		initTables();
		initMenu();
		initToolBar();

		loadFiles(0, leftPath); // 0 for left
		loadFiles(1, rightPath); // 1 for right
	}

	private void loadFiles(int leftOrRight, String root)
	{
		// TODO - Path traversal
		SystemFile rootFile;
		List<SystemFile> systemFileList = new ArrayList<>();

		rootFile = new SystemFile(new File(root));
		if (rootFile.exists() && rootFile.isDirectory())
		{
			systemFileList = rootFile.listSystemFiles();
		}

		TableView<SystemFile> table;
		if (leftOrRight == 0)
			table = leftTableView;
		else
			table = rightTableView;

		// TODO - Add root folder
		ObservableList<SystemFile> observableList = FXCollections.observableArrayList(systemFileList);
//		observableList.stream().forEach(System.out::println);
		table.setItems(observableList);
	}

	private void initMenu()
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

	private void initToolBar()
	{
		try
		{
			moveLabel.setText(properties.getProperty("move-label"));
			copyLabel.setText(properties.getProperty("copy-label"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void initTables()
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

	public void setLanguage(String filePath)
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
