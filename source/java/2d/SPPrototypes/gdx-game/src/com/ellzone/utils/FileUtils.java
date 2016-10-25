package com.ellzone.utils;

import java.util.Map;
import java.util.Set;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Gdx;

public class FileUtils {
	public static final String LOG_TAG = "FileUtils";
	
	public FileUtils() {
	}
	
	public static void gatherJavaFiles (FileHandle dir, Set<String> names, Map<String, FileHandle> fileHandles, boolean recursive) {
		FileHandle[] files = dir.list();
		for (FileHandle file : files) {
			if (file.isDirectory() && recursive) {
				gatherJavaFiles(file, names, fileHandles, recursive);
			} else {
				if (file.extension().equals("java")) {
					Gdx.app.log(LOG_TAG, "Found java file " + file.name());
					if (names.contains(file.name())) {
						System.out.println(file.name() + " duplicate!");
					}
					names.add(file.name());
					fileHandles.put(file.name(), file);
				}
			}
		}
	}
}

