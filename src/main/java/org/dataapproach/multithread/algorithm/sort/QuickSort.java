package org.dataapproach.multithread.algorithm.sort;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * QuickSort is a divide-and-conquer algorithm.
 * 
 * The average time complexity of QuickSort is Nlog2(N) and worst time complexity of QuickSort is N^2.
 * 
 * This uses inplace sorting.
 * 
 * It is better to switch to insertion sort from merge sort,
 *  if number of elements is less than 12
 *  
 * 
 * @author sarath
 *
 */
public class QuickSort {
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

//			 System.out.println("Array("+LENGTH+") elements before sort: ");
//			 // Print first 10 elements
//			 printIntArray(array);

			// run the algorithm and time how long it takes
			long startTime1 = System.currentTimeMillis();
			QuickSort.quicksort(array);
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
    /**
     * Main method used for sorting from clients. Input is sorted in place using multiple threads.
     *
     * @param input The array to sort.
     * @param <T>   The type of the objects being sorted, must extend Comparable.
     */
    public static <T extends Comparable<T>> void quicksort(T[] input) {
        final AtomicInteger count = new AtomicInteger(1);
        pool.execute(new QuicksortRunnable<T>(input, 0, input.length - 1, count));
        try {
            synchronized (count) {
                count.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sorts a section of an array using quicksort. The method used is not technically recursive as it just creates new
     * runnables and hands them off to the ThreadPoolExecutor.
     *
     * @param <T> The type of the objects being sorted, must extend Comparable.
     */
    private static class QuicksortRunnable<T extends Comparable<T>> implements Runnable {
        /**
         * The array being sorted.
         */
        private final T[] values;
        /**
         * The starting index of the section of the array to be sorted.
         */
        private final int left;
        /**
         * The ending index of the section of the array to be sorted.
         */
        private final int right;
        /**
         * The number of threads currently executing.
         */
        private final AtomicInteger count;

        /**
         * Default constructor. Sets up the runnable object for execution.
         *
         * @param values The array to sort.
         * @param left   The starting index of the section of the array to be sorted.
         * @param right  The ending index of the section of the array to be sorted.
         * @param count  The number of currently executing threads.
         */
        public QuicksortRunnable(T[] values, int left, int right, AtomicInteger count) {
            this.values = values;
            this.left = left;
            this.right = right;
            this.count = count;
        }

        /**
         * The thread's run logic. When this thread is done doing its stuff it checks to see if all other threads are as
         * well. If so, then we notify the count object so Sorter.quicksort stops blocking.
         */
        public void run() {
            quicksort(left, right);
            synchronized (count) {
                // AtomicInteger.getAndDecrement() returns the old value. If the old value is 1, then we know that the actual value is 0.
                if (count.getAndDecrement() == 1)
                    count.notify();
            }
        }

        /**
         * Method which actually does the sorting. Falls back on recursion if there are a certain number of queued /
         * running tasks.
         *
         * @param pLeft  The left index of the sub array to sort.
         * @param pRight The right index of the sub array to sort.
         */
        private void quicksort(int pLeft, int pRight) {
            if (pLeft < pRight) {
                int storeIndex = partition(pLeft, pRight);
                if (count.get() >= FALLBACK * N_THREADS) {
                    quicksort(pLeft, storeIndex - 1);
                    quicksort(storeIndex + 1, pRight);
                } else {
                    count.getAndAdd(2);
                    pool.execute(new QuicksortRunnable<T>(values, pLeft, storeIndex - 1, count));
                    pool.execute(new QuicksortRunnable<T>(values, storeIndex + 1, pRight, count));
                }
            }
        }

        /**
         * Partitions the portion of the array between indexes left and right, inclusively, by moving all elements less
         * than values[pivotIndex] before the pivot, and the equal or greater elements after it.
         *
         * @param pLeft
         * @param pRight
         * @return The final index of the pivot value.
         */
        private int partition(int pLeft, int pRight) {
            T pivotValue = values[pRight];
            int storeIndex = pLeft;
            for (int i = pLeft; i < pRight; i++) {
                if (values[i].compareTo(pivotValue) < 0) {
                    swap(i, storeIndex);
                    storeIndex++;
                }
            }
            swap(storeIndex, pRight);
            return storeIndex;
        }

        /**
         * Simple swap method.
         *
         * @param left  The index of the first value to swap with the second value.
         * @param right The index of the second value to swap with the first value.
         */
        private void swap(int left, int right) {
            T temp = values[left];
            values[left] = values[right];
            values[right] = temp;
        }
    }
}