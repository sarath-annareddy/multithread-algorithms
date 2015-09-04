package org.dataapproach.multithread.algorithm.sort;

import java.util.*;

/**
 * @author sarath
 *
 */
public class MergeSort {
	private static final Random RAND = new Random(42); // random number
														// generator

	private static final int NUM_THREADS = Runtime.getRuntime()
			.availableProcessors();

	public static void main(String[] args) throws Throwable {
		int LENGTH = 1000; // initial length of array to sort
		int RUNS = 16; // how many times to grow by 2?

		System.out.println("NUM_THREADS(cores): " + NUM_THREADS);

		for (int i = 1; i <= RUNS; i++) {
			int[] a = createRandomArray(LENGTH);

			// run the algorithm and time how long it takes
			long startTime1 = System.currentTimeMillis();
			parallelMergeSort(a, NUM_THREADS);
			long endTime1 = System.currentTimeMillis();

			if (!isSorted(a)) {
				throw new RuntimeException("not sorted afterward: "
						+ Arrays.toString(a));
			}

			System.out.printf("%10d elements  =>  %6d ms \n", LENGTH, endTime1
					- startTime1);
			LENGTH *= 2; // double size of array for next time
		}
	}

	public static void parallelMergeSort(int[] a, int NUM_THREADS) {
		if (NUM_THREADS <= 1) {
			mergeSort(a);
			return;
		}

		int mid = a.length / 2;

		int[] left = Arrays.copyOfRange(a, 0, mid);
		int[] right = Arrays.copyOfRange(a, mid, a.length);

		Thread leftSorter = mergeSortThread(left, NUM_THREADS);
		Thread rightSorter = mergeSortThread(right, NUM_THREADS);

		leftSorter.start();
		rightSorter.start();

		try {
			leftSorter.join();
			rightSorter.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		merge(left, right, a);
	}

	private static Thread mergeSortThread(final int[] a, final int NUM_THREADS) {
		return new Thread() {
			@Override
			public void run() {
				parallelMergeSort(a, NUM_THREADS / 2);
			}
		};
	}

	/**
	 * Original MergeSort
	 * 
	 * @param a
	 */
	public static void mergeSort(int[] a) {
		if (a.length <= 1)
			return;

		int mid = a.length / 2;

		int[] left = Arrays.copyOfRange(a, 0, mid);
		int[] right = Arrays.copyOfRange(a, mid, a.length);

		mergeSort(left);
		mergeSort(right);

		merge(left, right, a);
	}

	private static void merge(int[] a, int[] b, int[] r) {
		int i = 0, j = 0, k = 0;
		while (i < a.length && j < b.length) {
			if (a[i] < b[j])
				r[k++] = a[i++];
			else
				r[k++] = b[j++];
		}

		while (i < a.length)
			r[k++] = a[i++];

		while (j < b.length)
			r[k++] = b[j++];
	}

	// Swaps the values at the two given indexes in the given array.
	public static final void swap(int[] a, int i, int j) {
		if (i != j) {
			int temp = a[i];
			a[i] = a[j];
			a[j] = temp;
		}
	}

	// Randomly rearranges the elements of the given array.
	public static void shuffle(int[] a) {
		for (int i = 0; i < a.length; i++) {
			// move element i to a random index in [i .. length-1]
			int randomIndex = (int) (Math.random() * a.length - i);
			swap(a, i, i + randomIndex);
		}
	}

	// Returns true if the given array is in sorted ascending order.
	public static boolean isSorted(int[] a) {
		for (int i = 0; i < a.length - 1; i++) {
			if (a[i] > a[i + 1]) {
				return false;
			}
		}
		return true;
	}

	// Creates an array of the given length, fills it with random
	// non-negative integers, and returns it.
	public static int[] createRandomArray(int length) {
		int[] a = new int[length];
		for (int i = 0; i < a.length; i++) {
			a[i] = RAND.nextInt(1000000);
			// a[i] = RAND.nextInt(40);
		}
		return a;
	}
}
