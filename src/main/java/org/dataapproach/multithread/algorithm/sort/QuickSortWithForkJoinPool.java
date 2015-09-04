package org.dataapproach.multithread.algorithm.sort;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * 
 * QuickSort is a divide-and-conquer algorithm.
 * 
 * The average time complexity of QuickSort is Nlog2(N) and worst time complexity of QuickSort is N^2.
 * 
 * This uses inplace sorting.
 * 
 * It is better to switch to insertion sort from merge sort,
 *  if number of elements is less than 12
 *  
 * @author sarath
 *
 */
public class QuickSortWithForkJoinPool {

	/**
     * Number of threads to use for sorting.
     */
    private static final int N_THREADS = Runtime.getRuntime().availableProcessors();

    /**
     * Multiple to use when determining when to fall back.
     */
    private static final int FALLBACK = 1;

    /**
     * Thread pool used for executing sorting Runnables.
     */
    private static Executor pool = Executors.newFixedThreadPool(N_THREADS);

    private static final Random RAND = new Random(42); // random number



	public static void main(String[] args) throws Throwable {

		int LENGTH = 1000; // initial length of array to sort
		int RUNS = 16; // how many times to grow by 2?

		System.out.println("N_THREADS(cores): " + N_THREADS);
		System.out.println("");
		
		for (int i = 1; i <= RUNS; i++) {
			Integer[] array = createRandomArray(LENGTH);
			List<Integer> arrayList = (List<Integer>)Arrays.asList(array);
//			 System.out.println("Array("+LENGTH+") elements before sort: ");
//			 // Print first 10 elements
//			 printIntArray(array);

			// run the algorithm and time how long it takes
			long startTime1 = System.currentTimeMillis();
			
			QuickSorter<Integer> quickSort = new QuickSorter<Integer>(arrayList);
			 
	        ForkJoinPool pool = new ForkJoinPool();
	        pool.invoke(quickSort);
			long endTime1 = System.currentTimeMillis();

//			 System.out.println("Array("+LENGTH+") elements after sort: ");
//			// Print first 10 elements
//			 printIntArray(array);

			if (!isSorted(array)) {
				throw new RuntimeException("not sorted afterward: "
						+ Arrays.toString(array));
			}

			System.out.printf("Time taken for %10d elements  =>  %6d ms \n",
					LENGTH, endTime1 - startTime1);
			System.out.println("");
			LENGTH *= 2; // double size of array for next time
		}

	}

	// Returns true if the given array is in sorted ascending order.
	public static boolean isSorted(Integer[] a) {
		for (int i = 0; i < a.length - 1; i++) {
			if (a[i] > a[i + 1]) {
				return false;
			}
		}
		return true;
	}

	// Creates an array of the given length, fills it with random
	// non-negative integers, and returns it.
	public static Integer[] createRandomArray(int length) {
		Integer[] a = new Integer[length];
		for (int i = 0; i < a.length; i++) {
			a[i] = RAND.nextInt(1000000);
			// a[i] = RAND.nextInt(40);
		}
		return a;
	}

	// Beginning of method void printIntArray(int[])

	public static void printIntArray(int[] array) {

		System.out.print("{");
		// for (int i = 0; i < array.length - 1; i++)
		for (int i = 0; i < 10 - 1; i++)
			System.out.print(array[i] + ", ");
		System.out.println(array[array.length - 1] + "}");

	} // E
	
	
	
}

class QuickSorter<T extends Comparable> extends RecursiveAction {
	 
    /**
	 * 
	 */
	private static final long serialVersionUID = 6684854258264948267L;
	private List<T> data;
    private int left;
    private int right;
 
    public QuickSorter(List<T> data){
        this.data=data;
        this.left = 0;
        this.right = data.size() - 1;
    }
 
    public QuickSorter(List<T> data, int left, int right){
        this.data = data;
        this.left = left;
        this.right = right;
    }
 
    @Override
    protected void compute() {
        if (left < right){
            int pivotIndex = left + ((right - left)/2);
 
            pivotIndex = partition(pivotIndex);
 
            invokeAll(new QuickSorter(data, left, pivotIndex-1),
                      new QuickSorter(data, pivotIndex+1, right));
        }
 
    }
 
    private int partition(int pivotIndex){
        T pivotValue = data.get(pivotIndex);
 
        swap(pivotIndex, right);
 
        int storeIndex = left;
        for (int i=left; i<right; i++){
            if (data.get(i).compareTo(pivotValue) < 0){
                swap(i, storeIndex);
                storeIndex++;
            }
        }
 
        swap(storeIndex, right);
 
        return storeIndex;
    }
 
    private void swap(int i, int j){
        if (i != j){
            T iValue = data.get(i);
 
            data.set(i, data.get(j));
            data.set(j, iValue);
        }
    }
}
