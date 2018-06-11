import java.util.Scanner;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class main {

	private static String WD; 
	final static File SD = new File("/EOS_DIGITAL/DCIM");
	private static String transfer_folder;
	private static int moved;
	private static int total_files;
	
	public static void main(String[] args) {
		// Path of folder where files are located
		File[] sd_subdirs = SD.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);

		//Get prepend String
		Scanner input = new Scanner(System.in);
		System.out.print("Destination Folder: ");
		transfer_folder = input.nextLine();
		WD = "/WD" + transfer_folder + "/";
		input.close();

		final Path wd_path = Paths.get(WD);
		if (Files.notExists(wd_path)) {
			try {
				Files.createDirectories(wd_path);
			} catch (IOException e) {
				System.out.println("Couldn't create folder " + transfer_folder);
				e.printStackTrace();
			}
		}

		// Iterate through subfolders, only if folder has CANON prepend String
		for (File subdir : sd_subdirs) {
			if (subdir.getName().contains("CANON")) {
				System.out.println("Moving .mov files from within " + subdir.getName() + "...\n");
				moveFiles(subdir);
				System.out.println("\n...Done. Moved " + moved + " out of " + total_files);
			}
		}

	}

	private static void moveFiles(File directory) {
		File folder = new File(directory.getAbsolutePath());
		File[] filesList = folder.listFiles();
		
		moved = 0;
		total_files = 0;
		
		for (File file: filesList) {
			String fileCurrentName = file.getName(); 

			// Only move .mov files
			if(fileCurrentName.lastIndexOf(".") != -1 && fileCurrentName.lastIndexOf(".") != 0 &&
					fileCurrentName.substring(fileCurrentName.lastIndexOf(".")+1).equalsIgnoreCase("mov")) {
				String folderName = StringUtils.substringBetween(fileCurrentName, " ", "MVI");
				Path destinationDir = Paths.get(WD + folderName.trim());

				if (Files.notExists(destinationDir)) {
					try {
						Files.createDirectories(destinationDir);
					} catch (IOException e) {
						System.out.println("Couldn't create folder " + transfer_folder);
						e.printStackTrace();
					}
				}

				try {
					total_files++;
					Files.copy(file.toPath(), Paths.get(destinationDir + "/" + file.getName()));
					moved++;
				} catch (FileAlreadyExistsException e) {
					System.out.println("File " + file.getName() + " already exists in " + destinationDir);
				} catch (IOException e) {
					System.out.println("Failed to move file " + file.getAbsolutePath());
					e.printStackTrace();
				} 
			}
		}
	}

}
