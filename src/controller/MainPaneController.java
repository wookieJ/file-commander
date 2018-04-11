package controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

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
import model.SystemFile;

public class MainPaneController implements Initializable
{
	private Properties properties;

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
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		initTables();
		initMenu();
		initToolBar();
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
			TableColumn<SystemFile, String> nameColumn = new TableColumn<SystemFile, String>(
					properties.getProperty("name-column"));
			// nameColumn.setCellValueFactory(new
			// PropertyValueFactory<>("name"));
			nameColumn.prefWidthProperty().bind(leftTableView.widthProperty().divide(4)); // w
																							// *
																							// 1/4

			TableColumn<SystemFile, String> sizeColumn = new TableColumn<SystemFile, String>(
					properties.getProperty("size-column"));
			// nameColumn.setCellValueFactory(new
			// PropertyValueFactory<>("size"));
			sizeColumn.prefWidthProperty().bind(leftTableView.widthProperty().divide(4)); // w
																							// *
																							// 1/4

			TableColumn<SystemFile, String> typeColumn = new TableColumn<SystemFile, String>(
					properties.getProperty("type-column"));
			// nameColumn.setCellValueFactory(new
			// PropertyValueFactory<>("type"));
			typeColumn.prefWidthProperty().bind(leftTableView.widthProperty().divide(4)); // w
																							// *
																							// 1/4

			TableColumn<SystemFile, String> modifiedColumn = new TableColumn<SystemFile, String>(
					properties.getProperty("modified-column"));
			// nameColumn.setCellValueFactory(new
			// PropertyValueFactory<>("modified"));
			modifiedColumn.prefWidthProperty().bind(leftTableView.widthProperty().divide(4)); // w
																								// *
																								// 1/4

			leftTableView.getColumns().add(nameColumn);
			leftTableView.getColumns().add(sizeColumn);
			leftTableView.getColumns().add(typeColumn);
			leftTableView.getColumns().add(modifiedColumn);

			rightTableView.getColumns().add(nameColumn);
			rightTableView.getColumns().add(sizeColumn);
			rightTableView.getColumns().add(typeColumn);
			rightTableView.getColumns().add(modifiedColumn);
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
