# Virtual-Memory-Emulator
A virtual memory emulator written in Java. Uses TLB table, page table, and physical memory to lookup and store data from the backing store binary file.

This programming assignment 3 was written for Operating Systems class and utilizes knowledge learned about virtual memory and the methods in which operating systems utilize virtual memory to dynamically load data from storage into memory using on demand memory management.

This program simulates this functionality of virtual memory using a few components such as a TLB lookup table (for most recently accessed page and frame number associations), a page table (for longer term page and frame associations), physical memory for storing loaded frames from disk, and finally an address reader for reading in memory addresses from disk that will then utilized to load data on disk from the BACKING_STORE.bin file into physical memory.

Some important specifications of the program:
- AddressReader reads in 1000 logical addresses from the addresses.txt file
- PageTable holds space for 256 page number and frame number associations
- TLB table holds space for 16 page number and frame number last used associations for quicker lookup than the PageTable
- Physical Memory holds space for 256 frames of memory each of 256 bytes in size. This is the exact size of the data in
the BACKING_STORE.bin file so we don't need to worry about removing frames from memory to fit extra frames as they are requested.
