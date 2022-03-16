package files;

import javafx.application.Platform;

public class GUIThread extends Thread{
	FilesGUI mUI = new FilesGUI();
	
	@Override
	public void run()
	{
		mUI.initialize();
	}
	
	public void updateGetFiles(String status)
	{
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				mUI.updateGetFilesStatus(status);
			}
		});
	}
}
