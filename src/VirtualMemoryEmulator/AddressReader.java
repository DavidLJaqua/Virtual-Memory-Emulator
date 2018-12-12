package VirtualMemoryEmulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class AddressReader {

	private BufferedReader logicalAddressReader;
	private ArrayList<Integer> logicalAddresses = new ArrayList<>();
	private int nextAddress = 0;
	
	public AddressReader() {
		setup();
	}
	
	/**
	 * Sets up the logical address reader and reads all 1000 lines of addresses from the addresses.txt file
	 * and saves them in an array list for fast accessing via getNextLogicalAddress() method.
	 * This way we don't have extra overhead of retrieving the address via an i/o operation each time we need
	 * the next address.
	 */
	public void setup() {
		File addressFile = new File("addresses.txt");
		if (addressFile.exists() == false) {
			System.out.println("Addresses.txt file not found in application directory!");
			return;
		}
		try {
			logicalAddressReader = new BufferedReader(new FileReader(addressFile));
			String line;
			while((line = logicalAddressReader.readLine()) != null) {
				if (line.equals("") == false) {
					logicalAddresses.add(new Integer(Integer.valueOf(line)));
				}
			}
			logicalAddressReader.close();
		} catch (IOException e) {
			e.printStackTrace();
			
			// make sure the file is correctly closed if an error does occur
			try {
				logicalAddressReader.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns the next address from the addresses.txt file
	 * @return the next address or -1000000 if there are no more addresses
	 */
	public int getNextAddress() {
		if (nextAddress < logicalAddresses.size()) {
			int address = logicalAddresses.get(nextAddress).intValue();
			nextAddress++; // increment pointer to next location
			return address;
		} else {
			return -1000000; // error code, no more addresses left
		}
		
	}	
}