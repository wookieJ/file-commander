package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

public class CopyController implements Initializable
{
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

	@FXML
	private Button closeButton;

	public TextArea getStatusArea()
	{
		return statusArea;
	}

	public void setStatusArea(TextArea statusArea)
	{
		this.statusArea = statusArea;
	}

	public Button getCancelButton()
	{
		return cancelButton;
	}

	public void setCancelButton(Button cancelButton)
	{
		this.cancelButton = cancelButton;
	}

	public ProgressBar getProgressBar()
	{
		return progressBar;
	}

	public void setProgressBar(ProgressBar progressBar)
	{
		this.progressBar = progressBar;
	}

	public Button getYesButton()
	{
		return yesButton;
	}

	public void setYesButton(Button yesButton)
	{
		this.yesButton = yesButton;
	}

	public Label getMainLabel()
	{
		return mainLabel;
	}

	public void setMainLabel(Label mainLabel)
	{
		this.mainLabel = mainLabel;
	}

	public Button getNoButton()
	{
		return noButton;
	}

	public void setNoButton(Button noButton)
	{
		this.noButton = noButton;
	}

	public CopyController()
	{
	}

	public Button getCloseButton()
	{
		return closeButton;
	}

	public void setCloseButton(Button closeButton)
	{
		this.closeButton = closeButton;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{

	}
}
