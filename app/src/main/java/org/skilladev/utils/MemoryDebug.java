package org.skilladev.utils;

/**
 * Created by administrador on 02/06/14.
 */
public class MemoryDebug {

	public static long getUsedMemorySize() {

		long freeSize = 0L;
		long totalSize = 0L;
		long usedSize = -1L;
		try {
			Runtime info = Runtime.getRuntime();
			freeSize = info.freeMemory();
			totalSize = info.totalMemory();
			usedSize = totalSize - freeSize;
			//Log.d("MemoryDebug", "Total: "+String.valueOf( totalSize )+" Used: "+String.valueOf( usedSize )+ " Free: "+String.valueOf( freeSize ));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return usedSize;
	}
}
