package VirtualMemoryEmulator;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PhysicalMemory {

	private byte[][] memory = new byte[256][256]; // 256 frames of 256 bytes each (frame num, offset num)
	private int nextAvailableFrameNum = 0; // index of next available frame in physical memory
	
	public PhysicalMemory() {
		// initialize the starting bytes of each frame to -1 indicating that the frame is empty
		for (int i = 0; i < 256; i++) {
			memory[i][0] = -1;
			memory[i][1] = -1;
		}
	}
	
	/**
	 * Loads a desired page from disk and stores it in the next available frame in memory.
	 * @param pageNumber the page to retrieve from file
	 * @return the frame in which the page was stored, otherwise, -1 if there was an error
	 */
	public int loadFrameFromDiskAndStore(int pageNumber) {
		File backingStoreFile = new File("BACKING_STORE.bin");
		if (backingStoreFile.exists() == false) {
			System.out.println("BACKING_STORE.bin file not found in application directory!");
			return -1;
		}
		RandomAccessFile randomAccess = null;
		
		try {
			randomAccess = new RandomAccessFile(backingStoreFile, "r"); // new random access file for reading only
			randomAccess.seek(pageNumber*256); // skip the read head to the specified page in the file
			
			// read 256 bytes into the desired frame from the block in storage
			for (int i = 0; i < 256; i++) {
				memory[nextAvailableFrameNum][i] = randomAccess.readByte();
			}
			
			nextAvailableFrameNum++; // increment next available frame number
			
			randomAccess.close();
			
			return (nextAvailableFrameNum-1); // return the frame in which the page was stored
		} catch (IOException e) {
			e.printStackTrace();
			// make sure to close the file in case of error
			try {
				randomAccess.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return -1; // an error occurred during loading and writing to memory
	}
	
	/**
	 * Returns the physical address in memory via its' frame num and offset
	 * @param frameNumber the frame number (0-255)
	 * @param offset the offset (0-255)
	 * @return the physical address int
	 */
	public int getPhysicalAddress(int frameNumber, int offset) {
		return frameNumber*256+offset;
	}
	
	/**
	 * Return the byte located in the specified frame and offset
	 * @param frameNumber the frame number to lookup
	 * @param offset the offset within the frame to lookup
	 * @return the byte located in that memory address, or -1 if an error occurred.
	 * ie. if the frame or offset is out of bounds
	 */
	public byte getByteFromMemory(int frameNumber, int offset) {
		if (frameNumber < 0 || frameNumber > 255
				|| offset < 0 || offset > 255) {
			return -1; // error code (out of bounds)
		}
		else if (isFrameEmpty(frameNumber) == true) {
			return -1; // error code (frame is empty/not loaded into memory)
		}
		return memory[frameNumber][offset];
	}
	
	/**
	 * Determines if the frame with a given number is empty or not
	 * @param frameNumber number of frame to check
	 * @return true if the frame is empty, otherwise, false
	 */
	public boolean isFrameEmpty(int frameNumber) {
		if (frameNumber < 0 || frameNumber > 255) {
			return false; // out of memory bounds
		}
		if (memory[frameNumber][0] == -1
				&& memory[frameNumber][1] == -1) {
			return true;
		}
		return false;
	}
}