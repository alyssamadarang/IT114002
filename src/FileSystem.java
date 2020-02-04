import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileSystem {

    //create a file
    public void createFileAndGetDetails(String fileName) {

        try {
            File fileReference = new File(fileName);
            if (fileReference.createNewFile()) {
                System.out.println("Didn't exist, created new");
            }
            else {
                System.out.println("File already exists");
            }

            System.out.println(fileName + " is located at " + fileReference.getAbsolutePath());

            if (fileReference.canRead()) {
                System.out.println(fileName + " is readable");
            }
            else {
                System.out.println(fileName + " is not readable");
            }

            if (fileReference.canWrite()) {
                System.out.println(fileName + " is writable");
            }
            else {
                System.out.println(fileName + " is not writable");
            }

            if (fileReference.canExecute()) {
                System.out.println(fileName + " is executable");
            }
            else {
                System.out.println(fileName + " is not executable");
            }
        }

        catch(IOException ie) {
            ie.printStackTrace();
        }

    }

    // write to the file
    public void writeToFile(String fileName, String msg) {
        try(FileWriter fw = new FileWriter(fileName)) {
            fw.write(msg);
            System.out.println("Wrote " + msg + " to " + fileName);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    // read file
    public void readFromFile(String fileName) {
        File file = new File(fileName);
        try(Scanner reader = new Scanner(file)) {
            String fullText = "";
            while (reader.hasNextLine()) {
                String n1 = reader.nextLine();
                System.out.println("Next line: " + n1);
                fullText += n1;
            }

            if (reader.hasNextLine()) {
                fullText += System.lineSeparator();
            }

            System.out.println("Contents of " + fileName + ": ");
            System.out.println(fullText);
        }

        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // main method
    public static void main(String[] args) {
        String fileName = "test.txt";
        FileSystem fss = new FileSystem();
        fss.createFileAndGetDetails(fileName);
        fss.writeToFile(fileName, "Hello world! We're writing to files");

    }


}
