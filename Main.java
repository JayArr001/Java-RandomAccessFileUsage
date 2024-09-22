import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

//this is a single-file program intended to demonstrate usage of RandomAccessFile's capability
//simulating accessing a potentially large database of employees plus their data, requiring the use of an index
//first, the program will read the index located at position 0 of the file and get the number of records
//it will store the location of the employee's data as a long in a LinkedHashMap for later access
//the program will print all IDs found in the .dat file and prompt the user to select an employee's ID
//after an ID is chosen, their salary is increased +10,000. This change is written to the file.
//The file is explicitly re-read and printed to show that it was modified via RandomAccessFile
public class Main
{
	private static Path employeeDataPath = Path.of("employeeData.dat");
	private static int recordsInFile = 0;
	private static Map<Integer, Long> indexedIDs = new LinkedHashMap<>();
	public static void main(String[] args)
	{
		//first check if the file is there. if not, print to console and exit
		if(!Files.exists(employeeDataPath))
		{
			System.out.println("Data file not found. Path checked:");
			System.out.println(employeeDataPath.toAbsolutePath());
			return;
		}
		File employeeDataFile = employeeDataPath.toFile();

		try(RandomAccessFile raf = new RandomAccessFile(employeeDataFile, "rw"))
		{
			//reads the index at position 0 and builds the indexedIDs LinkedHashMap
			loadIndex(raf, 0);
			if(recordsInFile < 1)
			{
				System.out.println("Zero records found in file. Terminating");
				return;
			}
			System.out.println("Printing all employee IDs:");
			for(int key : indexedIDs.keySet())
			{
				System.out.println(key);
			}

			Scanner scanner = new Scanner(System.in);
			System.out.println("Enter an employee ID to modify that employee's salary, or 0 to quit");
			while(scanner.hasNext())
			{
				String nextData = scanner.nextLine();
				try
				{
					int employeeID = -1;
					try
					{
						//this is basic input sanitation. we can do more to ensure better input
						//and sanitize inputs, but that is not the focus of this project
						employeeID = Integer.parseInt(nextData);
					}
					catch (Exception e)
					{
						System.out.println("invalid input given \"" + nextData + "\"");
						System.out.println(e);
					}
					if(employeeID == 0)
					{
						//providing a clean exit for the user
						System.out.println("0 entered or invalid ID. terminating");
						break;
					}
					if(employeeID > 0)
					{
						//get the requested employee's information as a pojo
						employeeData getEmployee = readEmployee(raf, employeeID);

						System.out.println("Employee data before modification:");
						System.out.printf("ID: %d, Salary: %.2f, FirstName: %s, LastName: %s\n",
								getEmployee.ID(), getEmployee.salary(),
								getEmployee.firstName(), getEmployee.lastName());

						//next block will increment salary by 10k
						//note that we could make this fancier and take another input from user to increment
						//by a specified amount, but that is extraneous and outside the scope of
						//what this program is intended to demonstrate
						double newSalary = getEmployee.salary() + 10000.00;
						raf.seek(indexedIDs.get(employeeID));
						//this time we will use skipBytes()
						raf.skipBytes(4); //4 bytes in an int, so we need to shift pointer over 4 bytes
						raf.writeDouble(newSalary);
						System.out.println("New Salary: " + newSalary);

						//now we will explicitly read the file again to prove that it was modified
						System.out.println("-----------------");
						System.out.println("Reading employee data file for verification");

						employeeData verifyEmployee = readEmployee(raf, employeeID);
						System.out.printf("ID: %d, Salary: %.2f, FirstName: %s, LastName: %s\n",
								verifyEmployee.ID(), verifyEmployee.salary(),
								verifyEmployee.firstName(), verifyEmployee.lastName());

						System.out.println("Enter another ID or 0 to quit");
					}
				}
				catch(Exception e)
				{
					System.out.println("error parsing employee ID");
					e.printStackTrace();
				}
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	//called at the start to load the file's index for use in RandomAccessFile operations
	private static void loadIndex(RandomAccessFile ra, int indexPosition)
	{
		try
		{
			ra.seek(indexPosition); //in this case it's always 0 but using a method arg is more flexible
			recordsInFile = ra.readInt(); //get the number of records in our file
			System.out.println("recordsInFile: " + recordsInFile);
			for(int i = 0; i < recordsInFile; i++)
			{
				indexedIDs.put(ra.readInt(), ra.readLong());
			}
		}
		catch(IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
	}

	//returning the data as pojo/record for readability and modularity
	private static employeeData readEmployee(RandomAccessFile raf, int getID) throws IOException
	{
		raf.seek(indexedIDs.get(getID)); //get index from ID, then move pointer to that spot
		int employeeIDFromRecord = raf.readInt(); //get the ID from record. also moves pointer
		//we can use skipBytes() to skip this read as well
		double employeeSalary = raf.readDouble(); //get salary, moves pointer
		String employeeFirstName = raf.readUTF(); //read names from the record
		String employeeLastName = raf.readUTF();

		return new employeeData(employeeIDFromRecord, employeeSalary, employeeFirstName, employeeLastName);
	}
}

//POJO used to deliver id, salary and employee name contained neatly in a single object
//the record is placed in the same file as the main class since it's only used here
//for POJOs that are expected to be used by many classes or sources, better for that record to be its own file
record employeeData(int ID, double salary, String firstName, String lastName)
{}
