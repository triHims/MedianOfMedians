import java.io.*;
import java.util.*;
import java.nio.file.Paths;
import java.nio.file.Files;
class medianofmedian2{
    public static void main(String[] args) {
	String input;
	String findNum;

	// This is the implementation of quickselect with median of median
	// note this takes a files that contains comma seperated numbers as inputs and a number that indicates the kth smallest number to search for
	/*
	example file
           3,2,1,4,5,6
           2
	
	*/

	try(BufferedReader br = new BufferedReader(new FileReader("inp.txt"))){
	    while(true){
		input = br.readLine();

		if(input == null){
		    break;
		}else if(input.length()==0){
		    continue;
		}

		findNum = br.readLine();

		// //System.out.println(input);
		// //System.out.println(findNum);


		int searchNum = Integer.parseInt(findNum.replaceAll("\\s+",""));

		String[] parts = input.replaceAll("\\s+","").split(",");

		int arr[] = new int[parts.length];

		int i=0;
		for(String s:parts){
		    arr[i++] = Integer.parseInt(s);
		}



		// //System.out.println("Parsed Array ");
		// for(int val:arr){
		//     //System.out.printf("%d ",val);
		// }

		// //System.out.println("");

		// //System.out.printf("findNum = %d %n",searchNum);


		// //System.out.println(input);

		runLogic(arr,searchNum);

	    }
	}catch( FileNotFoundException e){
	    e.printStackTrace();
	    throw new RuntimeException(e);
	}catch(IOException e){
	    e.printStackTrace();
	    throw new RuntimeException(e);
	}






    

    }

    public static void runLogic(int arr[],int tar){


	// int piv = select(arr,0,arr.length,arr.length/2,0,0);
	//int piv = medianofMedian(arr,0,arr.length,0,0);
	int piv = select(arr,0,arr.length, tar,0,0);

	// for(int a:arr){
	// 	//System.out.printf("%d ",a);
	// }
	// //System.out.println("");

	System.out.printf("Solution Pivot ind %d , val - %d %n%n",piv,arr[piv]);



    }

    public static void swap(int arr[],int i1,int i2){
	int temp = arr[i1];
	arr[i1] = arr[i2];
	arr[i2] = temp;
    }

    public static void insertionSort(int [] arr,int low,int high){
	for (int i=low+1;i<high;i++){
	    int key = arr[i];

	    int j=i-1;

	    while(j>=low && arr[j] > key){
		swap(arr,j,j+1);
		j--;
	    }

	    arr[j+1] = key;
	}
    }

    public static int medianofMedian(int arr[],int low,int high,int ispartition,int depth){


	int count = high-low;

	//System.out.printf("Value of low %d high %d depth %d %n",low,high,depth);
	


	if(count<6){
	    return select(arr,low,high,count/2,ispartition,depth+1);
	}


	int partitions = (int)Math.ceil(count/5.0);


 

	for(int i=0;i<partitions;i++){
	    //int pivot =  select(arr,low+i*5,Math.min(low+i*5+5,high),2,1,depth+1);
	    int medianPivot = medianofMedian(arr,low+i*5,Math.min(low+i*5+5,high),ispartition,depth+1);
	    swap(arr,low+i,medianPivot);
	}
	//System.out.println("printing median pivot");

	//for(int i=low;i< low+partitions;i++){
	    //System.out.printf("%d ",arr[i]);
	//}
	//System.out.println("");


	//int med = select(arr,low,low+partitions,partitions/2,1,depth+1);
	int med = select(arr,low,low+partitions,(partitions)/2 - 1,1,depth+1);
	//System.out.printf("med was  %d %n",med);

	return med;

    }


    public static int select(int[] arr,int low,int high, int k,int ispartition,int depth){ //Note: k should be passed as k-1 as in index of element
	if(high-low <=5){ // median of 5 condition
	    insertionSort(arr,low,high);
	    return low+k;
	}


	int approxMed = medianofMedian(arr,low,high,ispartition,depth+1);



	int start = dnfTriage(arr,low,high,arr[approxMed]);

	//System.out.printf("value at start - %d data at start - %d %n",start,arr[start]);




	if(start==low+k){
	    return start;
	}else if(low+k < start){
	    return select(arr,low,start,k,ispartition,depth+1);
	}else{
	    return select(arr,start+1,high,(k+low)-(start+1),ispartition,depth+1);
	    // why k is passed as (k+low) - (start+1)
	    /* consider range (0,14) k = 11
	    (0,7) (8,14) -> lets choose right so new k = (11-0) - 8 = 3
	    (8,10) (11,14) -> in this subrange if we simply subtract low ie 11 from K
	    it will give us -7 , there fore we restore original index by 3+8 then subtract new low ie 11 -> 11-11 = 0


             */
	}



    }


    /// We will try to write a triage function

    static int dnfTriage(int arr[],int low,int high,int pivot){
	int start = low;
	int end = high-1;
	int mid = start;
	/*
	Logic of this is simple,
	the array needs to be divided into 3 parts
	[elements less than pivot] [elements equal to pivot ][elements greater than pivot]
	we will take a start , mid, and end;

        end goal is
	start will point where [elements equal to pivt start]
	mid will point where [elements greater than pivot start]
	 */
	while(mid<=end){
	    if(arr[mid] < pivot){
		swap(arr,start,mid);
		start++;
		mid++;
	    }else if(arr[mid] == pivot ){
		mid++;
	    }else {
		swap(arr,mid,end);
		end--;
	    }
	    // //System.out.printf("After iteration - start - %d , mid - %d , end - %d %n",start,mid,end);
	    // for(int a:arr){
	    // 	//System.out.printf("%d ",a);
	    // }
	    // //System.out.println("");
	}

	return start;
    }


}
