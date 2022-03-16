package files;

import java.io.File;

public class Folder{
	String path;
	Folder[] inFolders;
	File[] files;
	
	public Folder(String path) {
		this.path = path;
	}
	
	public void setInFolders(Folder[] inFolders) {
		this.inFolders = inFolders;
	}
	
	public void setFiles(File[] files) {
		this.files = files;
	}
	
	public String getPath() {
		return path;
	}
	
	public Folder[] getInFolders() {
		return this.inFolders;
	}
	
	public File[] getFiles() {
		return this.files;
	}
}
