package VirtualMemoryEmulator;

/**
 * This programming assignment 3 was written for Operating Systems class and utilizes knowledge learned about virtual memory
 * and the methods in which operating systems utilize virtual memory to dynamically load data from storage into memory using
 * on demand memory management.
 * 
 * This program simulates this functionality of virtual memory using a few components such as a TLB lookup table (for most
 * recently accessed page and frame number associations), a page table (for longer term page and frame associations), 
 * physical memory for storing loaded frames from disk, and finally an address reader for reading in memory addresses
 * from disk that will then utilized to load data on disk from the BACKING_STORE.bin file into physical memory.
 * 
 * Some important specifications of the program:
 * AddressReader reads in 1000 logical addresses from the addresses.txt file
 * PageTable holds space for 256 page number and frame number associations
 * TLB table holds space for 16 page number and frame number last used associations for quicker lookup than the PageTable
 * Physical Memory holds space for 256 frames of memory each of 256 bytes in size. This is the exact size of the data in
 * the BACKING_STORE.bin file so we don't need to worry about removing frames from memory to fit extra frames as they are requested.
 * @author David Jaqua
 */
public class Main {
	static AddressReader addressReader;
	static PageTable pageTable;
	static TLB tlb;
	static PhysicalMemory physicalMemory;
	
	public static void main(String[] args) {
		addressReader = new AddressReader();
		pageTable = new PageTable();
		tlb = new TLB();
		physicalMemory = new PhysicalMemory();
		
		processAddresses();
	}
	
	/**
	 * Performs the virtual memory simulation by processing addresses as they are retrieved from the AddressReader.
	 * Looks up each memory to see if it exists in the TLB table as well as the Page table. If it does not exist in either,
	 * then the memory location, then the 256 bytes will be loaded into memory from the backing_store.bin file.
	 * 
	 * Prints out the virtual and physical memory addresses as well as the specific byte of memory the address points to.
	 * Finally, after the algorithm has finished it prints out the number of page faults and TLB table hits. A low TLB hit
	 * count is to be expected because the addresses were randomly generated.
	 */
	public static void processAddresses() {
		int logicalAddress, physicalAddress, pageNumber, offset, frameNumber;
		int[] pageNumberAndOffset;
		byte value;
		int numPageFaults = 0, numTLBHits = 0;
		
		while (true) {
			logicalAddress = addressReader.getNextAddress();
			if (logicalAddress == -1000000) {
				break; // reached end of addresses
			}
			pageNumberAndOffset = getPageNumberAndOffsetFromAddress(logicalAddress);
			pageNumber = pageNumberAndOffset[0];
			offset = pageNumberAndOffset[1];
			
			/*
			 * check if the page is currently in the tlb lookup table, if not, check the page table.
			 * if it isn't in the page table, then a page fault has occured, and it must be loaded from storage into memory
			 */
			if(tlb.doesPageExist(pageNumber) == true) {
				// this page already exists in the tlb lookup table, thus it is already loaded in physical memory (no page fault)
				frameNumber = tlb.getFrameNumber(pageNumber);
				physicalAddress = physicalMemory.getPhysicalAddress(frameNumber, offset);
				value = physicalMemory.getByteFromMemory(frameNumber, offset);
				numTLBHits++;
			}
			else if (pageTable.doesFrameExist(pageNumber) == true) {
				// this page already exists in the page table, thus it is already loaded in physical memory (no page fault)
				frameNumber = pageTable.getFrameNumber(pageNumber);
				tlb.setFrameNumber(pageNumber, frameNumber);
				physicalAddress = physicalMemory.getPhysicalAddress(frameNumber, offset);
				value = physicalMemory.getByteFromMemory(frameNumber, offset);
			} else {
				// this page is not in the page table or tlb lookup table, thus is is not loaded in memory (Page fault)
				// load it into memory and store the returned frame in the page table and tlb with the associated page number
				numPageFaults++;
				frameNumber = physicalMemory.loadFrameFromDiskAndStore(pageNumber);
				if (frameNumber == -1) {
					// error occurred because BACKING_STORE.bin file does not exist in the program's active directory
					return; // terminate execution
				}
				pageTable.setFrameNumber(pageNumber, frameNumber);
				tlb.setFrameNumber(pageNumber, frameNumber);
				physicalAddress = physicalMemory.getPhysicalAddress(frameNumber, offset);
				value = physicalMemory.getByteFromMemory(frameNumber, offset);
			}
			System.out.println("Virtual Address: " + logicalAddress + " | Physical Address: " + physicalAddress
					+ " | Value: " + value);
		}
		System.out.println("\nNumber of page faults: " + numPageFaults + "/1000 (" + round(numPageFaults/1000.0*100.0) + "%)");
		System.out.println("TLB hit rate: " + numTLBHits + "/1000 (" + round(numTLBHits/1000.0*100.0) + "%)");
	}
	
	/**
	 * Retrieves the 8 bit page number and 8 bit offset from a logical integer address
	 * @param address the logical integer address
	 * @return integer array of size 2 holding the [page number, offset] pair
	 */
	public static int[] getPageNumberAndOffsetFromAddress(int address) {
		int[] pageNumberAndOffset = new int[2];
		String binary = intToBinary(address);
		if (binary.length() < 16) {
			binary = padBinaryStringTo16Bits(binary); // pad binary to 16 bits
		}
		// determine the offset (last 8 bits)
		pageNumberAndOffset[1] = binaryToInt( binary.substring(binary.length()-8, binary.length()) );
		// determine the page number (8 bits before offset)
		pageNumberAndOffset[0] = binaryToInt( binary.substring(binary.length()-16, binary.length()-8) );
		return pageNumberAndOffset;
	}
	
	/**
	 * Pads a < 16 bit binary string to a 16 bit binary string
	 * @param binary string to pad
	 * @return the padded 16 bit binary string
	 */
	public static String padBinaryStringTo16Bits(String binary) {
		while(binary.length() < 16) {
			binary = "0" + binary;
		}
		return binary;
	}
	
	/**
	 * Converts an integer to it's binary string representation
	 * @param num the number to convert
	 * @return the binary representation of the number
	 */
	public static String intToBinary(int num) {
		return Integer.toBinaryString(num);
	}
	
	/**
	 * Converts a binary string to it's integer representation
	 * @param string binary string
	 * @return integer value of binary string
	 */
	public static int binaryToInt(String string) {
		int integer = 0;
		int multiplier = 1;
		for (int i = string.length()-1; i >= 0; i--) {
			if (string.charAt(i) == '1') {
				integer += multiplier;
			}
			multiplier = multiplier*2;
		}
		return integer;
	}
	
	/**
	 * Rounds a double to two or less decimal places
	 * @param num double decimal number to round
	 * @return the double rounded to two or less decimal places
	 */
	public static double round(double num) {
		return Math.round((num*100.0))/100.0;
	}
}
