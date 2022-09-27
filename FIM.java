import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class FIM {

	//FINSIHED: Complete -> Option A) Collect new Baseline?
	//FINISHED: Complete -> Option B) Begin monitoring files with saved Baseline?
	//FINISHED: Clean Option 'A'
	//FINISHED: Clean Option 'B'
	//IN-PROGRESS: Clean up project (naming conventions, name of java file, formatting, etc.)
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InterruptedException {

		System.out.println("What would you like to do?");
		System.out.println("    A) Collect new Baseline?");
		System.out.println("    B) Begin monitoring files with saved Baseline?");
		System.out.println("");
		
		@SuppressWarnings("resource")
		Scanner response  = new Scanner(System.in);  // Create a Scanner object
	    System.out.println("Please enter 'A' or 'B'");
	    String optionSelected = response.nextLine();  // Read user input
	    
	    //Option A) Collect new Baseline
	    if (optionSelected.equalsIgnoreCase("a")) {
	    	System.out.println("'A' was entered");
    	    System.out.println("");
	        
	        //# Calculate Hash from the target files and store in baseline.txt
	        //# Collect all files in the target folder
	    	File dir = new File("C:\\Users\\David\\eclipse-workspace\\FIM\\Files");
	    	File[] directoryListing = dir.listFiles();
	    	
	    	int fileNum = 0;
	    	if (directoryListing.length != 0) {
	    		
	    		System.out.println("Number of files in " + dir.toPath() + " -> " + directoryListing.length);
		    	FileWriter myWriter = new FileWriter("baseline.txt");
	    		
		    	//# For each file, calculate the hash, and write to baseline.txt
		    	for (File child : directoryListing) {
	    		  
		    		//Use SHA-1 algorithm
		  	    	MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
		  	    	 
		  	    	//SHA-1 checksum 
		  	    	String shaChecksum = getFileChecksum(shaDigest, child);
		  	    	 
		  	    	//View the checksum
		  	    	//System.out.println(child + "|" + shaChecksum);
		  	    	fileNum++;
		  	    
		  	    	if (fileNum <= directoryListing.length - 1) { 
		  	    		myWriter.append(child + "|" + shaChecksum + System.getProperty("line.separator"));
		  	    	} else {
		  	    		myWriter.append(child + "|" + shaChecksum);
		  	    	}
	    	  }
	    	  System.out.println("New Baseline created");
	    	  myWriter.close();
	    	  
	    	} else {
	    		System.out.println("Number of files in " + dir.toPath() + " -> " + directoryListing.length);
		    	System.out.println("No new Baseline was created");
	    	}

		//Option B) Begin monitoring files with saved Baseline
	    } else if (optionSelected.equalsIgnoreCase("b")) {
	    	System.out.println("'B' was entered");
	    	
	    	//# Load file|hash from baseline.txt and store them in a dictionary
	    	Map<String, String> map = new HashMap<String, String>();
	    	
	    	BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(
						"C:\\Users\\David\\eclipse-workspace\\FIM\\baseline.txt"));
				String line = reader.readLine();
				while (line != null) {
					//System.out.println(line);
					
					String[] parts = line.split("\\|");
					String part1 = parts[0];
					String part2 = parts[1];
					map.put(part1, part2);
					
					// read next line
					line = reader.readLine();
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//# Begin (continuously) monitoring files with saved Baseline
			while (true) {
				TimeUnit.SECONDS.sleep(2);
				
				//# For each file, calculate the hash, and write to baseline.txt
				File dir = new File("C:\\Users\\David\\eclipse-workspace\\FIM\\Files");
		    	File[] directoryListing = dir.listFiles();
		    	if (directoryListing != null) {
			    	  for (File child : directoryListing) {
				    	  
			    		  if (map.get(child.toString()) == null) {
				  	    		//# A new file has been created!
				  	    		System.out.println(child.toString() + " has been created!");
				          }
			    		  else {
					    		//Use SHA-1 algorithm
					  	    	MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
					  	    	 
					  	    	//SHA-1 checksum 
					  	    	String shaChecksum = getFileChecksum(shaDigest, child);
					  	    	 
					  	    	//View checksum (hash of the file)
						    	//System.out.println("shaChecksum: " + shaChecksum);
					  	    	
						    	//If the hash of the file doesn't match with the hash in the dictionary, then it was changed
						    	if (map.get(child.toString()).equalsIgnoreCase(shaChecksum)) {
						    		//System.out.println("The file has not changed");
						    	} else {
						    		System.out.println(child.toString() + " has changed!!!");
						    	}
				            }
			    	  }
		    	}
		    	
		    	ArrayList<String> ar = new ArrayList<String>();
				
				for (int i = 0; i < directoryListing.length; i++) {
					ar.add(directoryListing[i].toString());
				}
				
				for (String key : map.keySet()) {
					//System.out.println(key);
					
					//check if key is inside ar
					if (!ar.contains(key)) {
						System.out.println(key + " has been deleted!");
					}
				}		
			}
	    }
	}
	 
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	 private static String getFileChecksum(MessageDigest digest, File file) throws IOException
	 {
	     //Get file input stream for reading the file content
	     FileInputStream fis = new FileInputStream(file);
	      
	     //Create byte array to read data in chunks
	     byte[] byteArray = new byte[1024];
	     int bytesCount = 0; 
	       
	     //Read file data and update in message digest
	     while ((bytesCount = fis.read(byteArray)) != -1) {
	         digest.update(byteArray, 0, bytesCount);
	     };
	      
	     //close the stream; We don't need it now.
	     fis.close();
	      
	     //Get the hash's bytes
	     byte[] bytes = digest.digest();
	      
	     //This bytes[] has bytes in decimal format;
	     //Convert it to hexadecimal format
	     StringBuilder sb = new StringBuilder();
	     for(int i=0; i< bytes.length ;i++)
	     {
	         sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	     }
	      
	     //return complete hash
	    return sb.toString();
	 }
	 
}


