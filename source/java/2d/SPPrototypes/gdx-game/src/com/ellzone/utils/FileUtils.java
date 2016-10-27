package com.ellzone.utils;

import java.util.Map;
import java.util.Set;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Gdx;

public class FileUtils {
	FileHandle[] files;
	Map<String, FileHandle> fileHandles;
	
	public static final String LOG_TAG = "FileUtils";
	
	public FileUtils() {
	}
	
	public void gatherJavaFiles (FileHandle dir, Set<String> names, Map<String, FileHandle> fileHandles, boolean recursive) {
		this.fileHandles = fileHandles;
		files = dir.list();
		for (FileHandle file : files) {
			if (file.isDirectory() && recursive) {
				Gdx.app.log(LOG_TAG, "Found directory " + file.name());
				gatherJavaFiles(file, names, fileHandles, recursive);
			} else {
				if ((file.extension().equals("java") |
				    (file.extension().equals("class")))) {
					Gdx.app.log(LOG_TAG, "Found java file " + file.name());
					if (names.contains(file.name())) {
						Gdx.app.log(LOG_TAG, file.name() + " duplicate!");
					}
					names.add(file.name());
					fileHandles.put(file.name(), file);
				}
			}
		}
	}
	
	public int getNumJavaFilesFound() {
		if (this.fileHandles == null) {
			return 0;
		}
		return this.fileHandles.size();
	}
}

