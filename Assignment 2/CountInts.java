import java.util.*;
import java.io.*;


public class CountInts {
	
	public static void main(String[] args) {
		 Scanner s;
        // Some of the file reading code below has been taken from previous CSC225/226 templates
        if (args.length > 0){
            //open scanner from file 
            try{
                s = new Scanner(new File(args[0]));
            } catch(java.io.FileNotFoundException e){
                System.out.printf("Unable to open %s\n",args[0]);
                return;
            }

            int numInts = 0;
            int v;

            while(s.hasNextInt()){
            	numInts++;
            	v=s.nextInt();
            }

            System.out.println("There are " + numInts + " numbers");
        }
	}
}