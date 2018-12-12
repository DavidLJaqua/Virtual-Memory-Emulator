package VirtualMemoryEmulator;

public class TLB {
	
	/*
	 * TLB (translation look-aside buffer) lookup table, holds 16 entries
	 * [i][0] holds the page number and [i][1] holds the associated frame and [index][2] holds the counter
	 * for when it was inserted (lower the number = inserted earlier in time)
	 */
	private int[][] tlb = new int[16][3];
	private int counter = 0;
	
	public TLB() {
		// initialize all indexes in the tlb table to -1 (signifying not holding any page/frame)
		for (int i = 0; i < 16; i++) {
			tlb[i][0] = -1;
		}
	}
	
	/**
	 * Checks if the given page is currently in the tlb lookup table.
	 * @param pageNumber the page number to lookup
	 * @return true if the page and frame exists, otherwise, false
	 */
	public boolean doesPageExist(int pageNumber) {
		for (int i = 0; i < 16; i++) {
			if (tlb[i][0] == pageNumber) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Searches the tlb for the frame associated with the given page number.
	 * @param pageNumber the page number to lookup
	 * @return the frame number if found, otherwise, -1 if it doesn't exist in the tlb
	 */
	public int getFrameNumber(int pageNumber) {
		for (int i = 0; i < 16; i++) {
			if (tlb[i][0] == pageNumber) {
				return tlb[i][2];
			}
		}
		return -1; // frame associated with the given page number does not exist
	}
	
	/**
	 * Sets the page number and frame number association in the tlb table.
	 * Chooses the best next available location using the getNextAvailableIndex method
	 * which uses a FIFO algorithm
	 * @param pageNumber the page number
	 * @param frameNumber the frame number
	 */
	public void setFrameNumber(int pageNumber, int frameNumber) {
		if (doesPageExist(pageNumber) == true) {
			return; // this page and frame association already exists in the tlb
		}
		
		int index = getNextAvailableIndex();
		tlb[index][0] = pageNumber;
		tlb[index][1] = frameNumber;
		tlb[index][2] = counter;
		counter++;
	}
	
	/**
	 * Finds the next best available index. If any index is unassigned, it is returned first.
	 * If all indexes are assigned, then the one that was assigned the longest time ago is chosen (FIFO algorithm)
	 * @return the best available index to use
	 */
	public int getNextAvailableIndex() {
		int oldestAssignedIndex = 0, oldestAge = 1000000;
		for (int i = 0; i < 16; i++) {
			if (tlb[i][0] == -1) {
				return i; // this index is unassigned, use it
			} else {
				if (tlb[i][2] < oldestAge) {
					oldestAssignedIndex = i;
					oldestAge = tlb[i][2];
				}
			}
		}
		return oldestAssignedIndex;
	}
}
