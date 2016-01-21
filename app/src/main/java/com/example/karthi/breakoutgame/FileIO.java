package com.example.karthi.breakoutgame;
/*
 * Author : Aishwarya & Vel
 * Date : 22/Nov/2015
 * Purpose : Function creates the directory and file at the path if they don't exist, and insert dummy data inside the file.
 *  Debugging Mechanism has been added, contains logging at all most all levels
*/

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/*
 * Author - Aishwarya, Vadivel
 * Date : 22/Nov/2015
 * Purpose : Communicate and store the file Info
 */
public class FileIO {
	private final String directoryName;
	private final String fileName;
	private final File storagePath;
	private File scoreFile;

	/*
     * Author - Aishwarya, Vadivel
     * Date : 22/Nov/2015
     * Purpose : Default Constructor to create File Score
     */
	public FileIO() {
		directoryName = "BreakoutGame";
		fileName = "Score.txt";
		storagePath = Environment.getExternalStorageDirectory();
	}

	/*
	 * Author - Aishu
	 * Date : 22/Nov/2015
	 * Purpose : Function creates the directory and file at the path if they don't exist, and insert dummy data inside the file.
	 */
	public boolean initIO() {
		try {
			File directory = new File(storagePath, directoryName);
			scoreFile = new File(directory, fileName);
			
			//Log.i("File", "Path of file : " + scoreFile.toString());
			
			/*
			For testing , deleting the file in each increment
			Log.i("Remove this line ", "Deleting file for testing first...");
			scoreFile.delete();
    		*/
			//scoreFile.delete();
			// Checks if directory doesn't exist, if it doesn't then calls the create dir. function
	    	if(! directory.exists()) {
	    		// Creates a dir. referenced by this file
	    		directory.mkdir();
	    	}

	    	// Checks if file doesn't exist, if it doesn't then calls the create file function
	    	if(! scoreFile.exists()) {
	    		// Creates a file
				scoreFile.createNewFile();
	    	}
	    	
	    	// if false that means file is empty, insert it with dummy data
	    	if(fileIsEmpty()) {
	    		Log.i("File", "Empty file detected");
	    		Log.i("File", "Saving dummy records to file");

				List<Score> scoreList = new ArrayList<>();
				scoreList.add(new Score(1, "Vadivel", 40, 15));
				scoreList.add(new Score(2, "Aishwarya", 30, 12));
				scoreList.add(new Score(3, "Ram", 20, 10));
				scoreList.add(new Score(4, "Thamarai", 10, 5));
				writeList(scoreList);
	    	}
	    	return true; // if everything successful, return true
		}
		catch(Exception e) {
			Log.e("File", "dir/file not created : " +  e.getMessage());
			return false;
		}
    }

	/*
	 * Author - Aishwarya
	 * Date - 22/Nov/2015
	 * Writes a given list of score objects into the file.
	 */

	public boolean writeList(List<Score> listToWrite) {
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		try {
			Log.i("File", "Writing " + listToWrite.size() + " items to disk");
			// Assume default encoding
			// We overwrite file each time with the new records.
			fileWriter = new FileWriter(scoreFile, false);
			//wrap FileWriter in BufferedWriter
			bufferedWriter= new BufferedWriter(fileWriter);
			Collections.sort(listToWrite, new Comparator<Score>() {
				@Override
				public int compare(Score lhs, Score rhs) {
					int lhsScore = lhs.getScore();
					int rhsScore = rhs.getScore();

					// If scores aren't equal, check for their respective scores.
					if (lhsScore != rhsScore) {
						if (lhsScore > rhsScore)
							return -1;
						else if (lhsScore < rhsScore)
							return 1;
					} else {
						// If scores are equal, check for their time.
						// Least time wins.
						if (lhs.getTime() < rhs.getTime())
							return -1;
						else if (lhs.getTime() > rhs.getTime())
							return 1;
					}

					return 0;
				}
			});
			int rank = 1;
			for(Score p : listToWrite) {
				bufferedWriter.write( rank + "|" + p.getUserName() + "|" + p.getScore() + "|" + p.getTime() + '|');
				bufferedWriter.newLine();
				rank++;
			}
			// close and flush connections
			fileWriter.flush();
			bufferedWriter.flush();
			bufferedWriter.close();
			fileWriter.close();
			return true;
		}
		catch (Exception e) {
			Log.e("File", "list not written : " + e.getMessage());
			return false;
		}
	}

	/*
	 * Author - Aishwarya
	 * Date : 22/Nov/2015
	 * Purpose : Function to check if file on disk is empty. Parameters are the file path. Returns true if file was empty
	 */
	public boolean fileIsEmpty()
	{
		Boolean flag = true; // flag which tells us if the file is completely empty or not. True assumes file is empty
		try {
			FileReader fileReader = new FileReader(scoreFile);
			BufferedReader reader = new BufferedReader(fileReader);
			String line;
			
			while( (line = reader.readLine()) != null) {
				String delimited[] = null;
				// Split each line into it's constituent properties, then the result is stored in a string array.
				delimited = line.split("\\|");
				// This will make sure to take only the correct data in the correct format
				if(delimited.length >= 1)
					flag = false;
			}
			// close and flush connections
			reader.close();
			fileReader.close();
			return flag;
		}
		catch(Exception e) {
			Log.e("File Error : ", e.getMessage());
			e.printStackTrace();
			return flag;
		}
	}

	/*
	 * Author - Vadivel
	 * Date : 22/Nov/2015
	 * Reads the whole file and generates a List<Score> corresponding to each row in the file.
	 */
	public List<Score> readFile() {
		List<Score> scoreList = new ArrayList<Score>();
		try {
			FileReader fileReader = new FileReader(scoreFile);
			BufferedReader reader = new BufferedReader(fileReader);
			String line;
			
			while((line = reader.readLine())!=null) {
				//System.out.print(line);
				String delimited[] = null;
				// Split each line into it's constituent properties, then the result is stored in a string array.
				delimited = line.split("\\|");
				// This will make sure to take only the correct data in the correct format
				//System.out.println("del len:" + delimited.length);
				if(delimited.length == 4) {
					//	String array is used to initialize a Score object.
					Score p1 = new Score(Integer.parseInt(delimited[0]), delimited[1], Integer.parseInt(delimited[2]), Integer.parseInt(delimited[3]));
					// Each Score object is stored in a List of Score
					scoreList.add(p1);
				}
			}
			// close and flush connections
			reader.close();
			fileReader.close();
			return scoreList;
		}
		catch(Exception e) {
			Log.e("File not Read : ", e.getMessage());
			return null;	
		}
	}
}// end of Class FileIO
