package files;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FilesGUI extends Application {
	static Label getFilesStatus;
	@Override
	public void start(Stage primaryStage) throws Exception {
		ListView<String> foundFiles = new ListView<String>();
		VBox mainVBox = new VBox();
		
		getFilesStatus = new Label("Getting Files");
		
		Label searchStatus = new Label("Waiting for files");
		
		TextField searchFileNameTxt = new TextField();
		searchFileNameTxt.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.ENTER)
			{
				if(!searchFileNameTxt.getText().equalsIgnoreCase("") && getFilesStatus.getText().equalsIgnoreCase("Got Files"))
					searchForFiles(searchFileNameTxt.getText(), foundFiles, searchStatus);
			}
		});
		Button searchFileNameBtn = new Button("Search");
		searchFileNameBtn.setOnMouseClicked(e-> {
			if(!searchFileNameTxt.getText().equalsIgnoreCase("") && getFilesStatus.getText().equalsIgnoreCase("Got Files"))
				searchForFiles(searchFileNameTxt.getText(), foundFiles, searchStatus);
		});
		
		Button openFileExplorer = new Button("Open File Location");
		openFileExplorer.setOnMouseClicked(e->{
			String selectedFilePath = foundFiles.getSelectionModel().getSelectedItem();
			if(selectedFilePath != null)
			{
				try {
					File file = new File(selectedFilePath);
					if(file.getParentFile() != null)
					{
						Desktop.getDesktop().open(file.getParentFile());
					}
				} catch (IOException a) {
					a.printStackTrace();
				}
			}
		});
		
		
		HBox fileName = new HBox();
		fileName.getChildren().addAll(searchFileNameTxt, searchFileNameBtn);
		HBox bottomRow = new HBox();
		bottomRow.getChildren().addAll(getFilesStatus, openFileExplorer, searchStatus);
		mainVBox.getChildren().addAll(fileName, foundFiles, bottomRow);
		primaryStage.setScene(new Scene(mainVBox, 600, 400));
		primaryStage.setResizable(false);
		primaryStage.setTitle("File Search");
		primaryStage.getIcons().add(new Image(new File("resources/icon.png").toURI().toString()));
		primaryStage.show();
	}
	
	@Override
	public void stop() {
		FilesDriver.quit();
	}
	
	public void initialize() {
		launch();
	}
	
	public void updateGetFilesStatus(String status)
	{
		getFilesStatus.setText(status);
	}
	
	private void searchForFiles(String fileName, ListView<String> foundFiles, Label searchStatus)
	{
		foundFiles.getItems().clear();
		searchStatus.setText("Searching");
		Pattern regex = FilesDriver.getFileRegex(fileName);
		ArrayList<String> filePaths = new ArrayList<String>();
		for(DirectoryThread dir : FilesDriver.dirs)
		{
			ArrayList<String> filePath = FilesDriver.searchFoldersFiles(dir.getDir(), regex);
			if(!filePath.isEmpty())
				filePaths.addAll(filePath);
		}
		if(!filePaths.isEmpty())
		{
			foundFiles.getItems().addAll(filePaths);
			foundFiles.refresh();

			searchStatus.setText("Search Done");
		}
		else
		{
			searchStatus.setText("That file doesn't exist");
		}
	}
}
