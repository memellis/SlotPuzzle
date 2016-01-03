package com.ellzone.utils;

import java.io.IOException;
import com.badlogic.gdx.files.FileHandle;

public class FileUtils {
	public static void copyFile(FileHandle from, FileHandle to) throws IOException {	
		from.copyTo(to);
	}
}
