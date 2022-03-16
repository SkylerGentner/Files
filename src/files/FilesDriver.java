package files;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class FilesDriver {

	static ArrayList<DirectoryThread> dirs = new ArrayList<DirectoryThread>();
	public static void main(String[] args) {
		boolean isGUI = true;
		if(args.length > 0)
			isGUI = args[0].contains("nogui") ? false : true;
		
		File[] roots = File.listRoots();
		for(int i = 0; i < roots.length; i++)
				dirs.add(new DirectoryThread(new Folder(roots[i].getAbsolutePath())));
		if(!isGUI)
			System.out.println("Getting files...");
		long start = System.currentTimeMillis()/1000;
		for(DirectoryThread dir : dirs) {
			dir.start();
		}
		
		
		if(isGUI)
		{
			try {
				GUIThread guiThread = new GUIThread();
				guiThread.start();
				for(DirectoryThread dir : dirs) {
					try {
						dir.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				guiThread.updateGetFiles("Got Files");

				guiThread.join();
			} catch (Exception e1) {
				e1.printStackTrace();
				System.exit(3);
			}
		}
		else
		{
			Scanner in = new Scanner(System.in);
			System.out.print("What file are you looking for? ");
			String fileName = in.nextLine();
			
			Pattern fileNameRegex = null;
			fileNameRegex = getFileRegex(fileName);
			
			if(fileNameRegex == null)
			{
				System.out.println("Bad File");
				in.close();
				System.exit(0);
			}
			
			for(DirectoryThread dir : dirs) {
				try {
					dir.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Got files in " + ((System.currentTimeMillis()/1000) - start) + "s");
			
			ArrayList<String> filePaths = new ArrayList<String>();
			System.out.println("Searching...");
			start = System.currentTimeMillis()/1000;
			for(DirectoryThread dir : dirs)
			{
				ArrayList<String> filePath = searchFoldersFiles(dir.getDir(), fileNameRegex);
				if(!filePath.isEmpty())
					filePaths.addAll(filePath);
			}
			System.out.println("Search took " + ((System.currentTimeMillis()/1000) - start) + "s");
			if(!filePaths.isEmpty())
			{
				int counter = 1;
				for(String path : filePaths)
				{
					System.out.println(counter + ") " + path);
					counter++;
				}
				System.out.println(counter + ") End Program");
			}
			else
			{
				System.out.println("That file doesn't exist");
				in.close();
				System.exit(1);
			}
			
			String prompt = "Which location would you like to open? ";
			int choice = 1;
			while(choice > 0 || choice < filePaths.size() + 1)
			{
				System.out.print(prompt);
				choice = in.nextInt();
				if(choice == filePaths.size() + 1)
				{
					in.close();
					System.exit(2);
				}
				else if(choice < 1 || choice > filePaths.size() + 1)
				{
					System.out.println("Selection not valid, Try Again");
					System.out.println(prompt);
				}
				else
				{
					try {
						File file = new File(filePaths.get(choice-1));
						if(file.getParentFile() != null)
						{
							Desktop.getDesktop().open(file.getParentFile());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}


	public static Pattern getFileRegex(String fileName) {
		Pattern fileNameRegex;
		if(fileName.contains("."))
		{
			fileNameRegex = Pattern.compile(fileName);
		}
		else
		{
			String fileNamePattern = "";
			for(char c : fileName.toCharArray())
			{
				fileNamePattern += "[" + Character.toLowerCase(c) + Character.toUpperCase(c) + "]";
			}
			fileNameRegex = Pattern.compile(fileNamePattern + "[.][A-z]{1,4}");
		}
		return fileNameRegex;
	}
	
	
	//TODO MAKE ARRAYLIST INSIDE THAT ADDS ALL FOUND FILES
	public static ArrayList<String> searchFoldersFiles(Folder path, Pattern fileName) {
		ArrayList<String> foundFiles = new ArrayList<String>();
		try {
			File file = new File(path.getPath());
			for(File f : file.listFiles()) {
				try {
					if(fileName.matcher(f.getName()).matches())
					{
						foundFiles.add(f.getAbsolutePath());
					}
						
				} catch(StringIndexOutOfBoundsException e) { }
			}
		} catch(NullPointerException e) { }
		ArrayList<String> fileFound = null;
		if(path.getInFolders() != null)
		{
			for(int i = 0; i < path.getInFolders().length; i++) {
				Folder newFolder = path.getInFolders()[i];
				if(newFolder != null)
				{
					fileFound = searchFoldersFiles(newFolder, fileName);
					if(fileFound != null)
					{
						foundFiles.addAll(fileFound);
					}
				}
			}
		}
		return foundFiles;
	}


	public static void quit() {
		System.exit(-1);
	}
}
