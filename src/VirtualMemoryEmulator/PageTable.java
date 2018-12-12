package VirtualMemoryEmulator;

public class PageTable {

	private int[] pageTable = new int[256]; // 256 frames
	
	public PageTable() {
		// setup the page table to point every page to -1 frame (page-fault frame)
		for (int i = 0; i < 256; i++) {
			pageTable[i] = -1;
		}
	}
	
	/**
	 * Returns whether or not a frame number is currently associated with a given page number
	 * @param pageNumber the page number to lookup
	 * @return true if a frame is found, otherwise, false
	 */
	public boolean doesFrameExist(int pageNumber) {
		if (pageNumber < 0 || pageNumber > 255) {
			return false; // out of bounds
		}
		if (pageTable[pageNumber] == -1) {
			return false; // no frame is currently associated with this page number
		}
		return true;
	}
	
	/**
	 * Returns the frame number (physical frame number) via a page number.
	 * Returns -1000000 if no page number exists in the page table.
	 * @param pageNumber the page number to lookup
	 * @return the frame number the particular page number points to, 
	 * otherwise, -1000000 if it doesn't exist in the table (page fault)
	 */
	public int getFrameNumber(int pageNumber) {
		if (pageNumber < 0 || pageNumber > 255) {
			return -1000000; // out of bounds request
		}
		if (pageTable[pageNumber] == -1) {
			return -1000000; // page fault
		}
		return pageTable[pageNumber];
	}
	
	/**
	 * Sets the frame number associated with the given page number.
	 * @param pageNumber the page number
	 * @param frameNumber the frame number
	 */
	public void setFrameNumber(int pageNumber, int frameNumber) {
		if (pageNumber < 0 || pageNumber > 255) {
			return; // out of bounds page number
		}
		pageTable[pageNumber] = frameNumber;
	}
}
