//Template is created by Zhuoli Xiao, on Sept. 19st, 2016.
//Only used for Lab 226, 2016 Fall. 
//All Rights Reserved. 

// Template modified by Rich Little on May 18, 2017

//pickCleverPivot() and getMedianIndex() written by Gurjyot Grewal

import java.util.Scanner;
import java.util.Vector;
import java.util.Arrays;
import java.io.File;
import java.util.Random;
import java.lang.*;


public class LinearSelect {
	//Function to invoke linearSelect
	public static int LS(int[] S, int k){
        if (S.length==1)
        	return 0;
       
        return linearSelect(0,S.length-1,S,k);


	}
    
    //do linearSelect in a recursive way 
    private static int linearSelect(int left,int right, int[] array, int k){
    	//if there is only one element now, just record.
    	if (left>=right){
    		return left;
    	}
    	//do the partition 
    	int p=pickCleverPivot(left,right, array);
    	// System.out.println(p);
    	int eIndex=partition(left,right,array,p);
    	//after the partition, do following ops
    	if (k<=eIndex){
    		return linearSelect(left,eIndex-1,array,k);
    	}else if(k==eIndex+1){
    		return eIndex;
    	}else{
    		return linearSelect(eIndex+1,right,array,k);
    	}

    }

    private static int pickCleverPivot(int left, int right, int[] array){

    	if((right-left)<5){
    		return getMedianIndex(left, right, array);
    	}

    	int front= left;
    	int i;

    	for (i=left;i<=right ; i+=5) {
    		
    		//Fifth element from i, used in case n%5!=0    	
    		int partitionRight = i+4;

    		if(partitionRight>right){
    			partitionRight = right;
    		}
   
    		int index = getMedianIndex(i, partitionRight, array);
    		
    		swap(array, index, front); //stack medians at front of subarray
    		front++;
    	}
    	/* Instead of calling linearSelect like done in pseudocode, call pickCleverPivot again, starting at the beginning
    	 * of subarray and ending at the end of the medians. Therefore doing the same thing and returning to linearSelect only once */
		return pickCleverPivot(left, front, array);

	}

	private static int getMedianIndex(int from, int to, int[] array){
		//int n = to-from;

		for (int i = from; i <= to; i++){
			int k = i;
			while (k > from && array[k-1] > array[k]) {
				swap(array, k, k-1);
				k--;
			}//end while
		}//end for

		//get median index by finding medianIndex in subarray and adding it to the from or left pointer
		int medIndex = 0;

		if((to-from)%2==0){
			medIndex = ((to-from)/2) - 1;
		}
		else{
			medIndex = ((to-from)/2);
		}

		return from+medIndex;

	}

    //do Partition with a pivot
    private static int partition(int left, int right, int[] array, int pIndex){
    	//move pivot to last index of the array
    	swap(array,pIndex,right);

    	int p=array[right];
    	int l=left;
    	int r=right-1;
  
    	while(l<=r){
    		while(l<=r && array[l]<=p){
    			l++;
    		}
    		while(l<=r && array[r]>=p){
    			r--;
    		}
    		if (l<r){
    			swap(array,l,r);
    		}
    	}

        swap(array,l,right);
    	return l;
    }

    //Pick a random pivot to do the linearSelect
	private static int pickRandomPivot(int left, int right){
		int index=0;
		Random rand= new Random();
		index = left+rand.nextInt(right-left+1);
		return index;  
	}

	

	//swap two elements in the array
	private static void swap(int[]array, int a, int b){
 		int tmp = array[a];
		array[a] = array[b];
		array[b] = tmp;
	}


	/* main()
	   Contains code to test the QuickSelect. Nothing in this function
	   will be marked. You are free to change the provided code to test your
	   implementation, but only the contents of the QuickSelect class above
	   will be considered during marking.
	*/
	public static void main(String[] args){

		Scanner s;
		if (args.length > 0){
			try{
				s = new Scanner(new File(args[0]));
			} catch(java.io.FileNotFoundException e){
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}else{
			s = new Scanner(System.in);
			System.out.printf("Enter a list of non-negative integers. Enter a negative value to end the list.\n");
		}
		Vector<Integer> inputVector = new Vector<Integer>();

		int v;
		while(s.hasNextInt() && (v = s.nextInt()) >= 0)
			inputVector.add(v);
		
		int k = inputVector.get(0);

		int[] array = new int[inputVector.size()-1];

		for (int i = 0; i < array.length; i++)
			array[i] = inputVector.get(i+1);

		System.out.printf("Read %d values.\n",array.length);


		long startTime = System.nanoTime();

		int kthsmallestindex = LS(array,k);
		
		int kthsmallest = array[kthsmallestindex];

		long endTime = System.nanoTime();

		long totalTime = (endTime-startTime);

		System.out.printf("The %d-th smallest element in the input list of size %d is %d.\n",k,array.length,kthsmallest);
		System.out.printf("Total Time (nanoseconds): %d\n",totalTime);
	}
}