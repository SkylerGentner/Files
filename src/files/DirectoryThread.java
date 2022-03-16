package files;

import java.io.File;
import java.util.ArrayList;

public class DirectoryThread extends Thread {
	Folder dir;

	DirectoryThread(Folder dir) {
		this.dir = dir;
	}

	public void run() {
		File file = new File(this.dir.getPath());
		this.dir.setFiles(file.listFiles());
		this.dir.setInFolders(getFoldersFiles(this.dir));
	}
	
	private static Folder[] getFoldersFiles(Folder path) {
		try {
			File file = new File(path.getPath());
			File[] files = file.listFiles();
			ArrayList<Folder> folders = new ArrayList<Folder>();
			ArrayList<File> actualFiles = new ArrayList<File>();
			for(File f : files) {
				if(f.isFile())
					actualFiles.add(f);
				else if(f.isDirectory())
					folders.add(new Folder(f.getAbsolutePath()));
			}
			
			for(Folder f : folders) {
				//System.out.println(f.getPath());
				f.setInFolders(getFoldersFiles(f));
			}
			
			Folder[] fold = new Folder[folders.size()];
			for(int i = 0; i < folders.size(); i++)
				fold[i] = folders.get(i);
			return fold;
		} catch(NullPointerException e) {
			return null;
		} 
	}
	
	public Folder getDir() {
		return dir;
	}
	
	public void setDir(Folder dir) {
		this.dir = dir;
	}
}
