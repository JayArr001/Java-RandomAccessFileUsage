# Java-RandomAccessFileUsage
This is a single-file program intended to demonstrate usage of RandomAccessFile, simulating read/write to a potentially large database of employees plus their data, requiring the use of an index. The program will read the index located at position 0 of the file and get the number of records in the file. It stores the location of the employee's data as a long in a LinkedHashMap for later access. The program will print all IDs found in the .dat file and prompt the user to select an employee's ID. After an ID is chosen, their salary is increased +10,000. This change is written to the file. That part of the file is explicitly re-read and printed to show that it was modified and read via RandomAccessFile.</br>
</br>
All output from this program are to the console only. Running this program requires both Main.java and an accompanying employeeData.dat file. The program checks for the .dat file in the same working directory as it is executed from.</br>
</br>
This program is intended to showcase the author's skills and familiarity with the following (non-exhaustive) Java topics:</br>
Writing to files using RandomAccessFile</br>
Reading from files using an index via RandomAccessFile</br>
Exception handling</br>
String formatting</br>
</br>
Users can run this program by acquiring Main.java and employeeData.dat placed in the same working directory, then executing it by targeting Main.java in a command prompt using Java, or running Main.java inside an IDE.
