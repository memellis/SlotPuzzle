package com.ellzone.utils;

import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

public class JavaArchive {
	
	private String inputDirectory = "inputDirectory";
	private String outputJar = "outputJar";
	private int numJarElementsExracted;
	
	public JavaArchive() {
	}
	
	public JavaArchive(String inputDirectory, String outputJar) {
		this.inputDirectory = inputDirectory;
		this.outputJar = outputJar;
	}
	
	public void createJar() throws IOException
	{
		Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		JarOutputStream target = new JarOutputStream(new FileOutputStream(outputJar), manifest);
		add(new File(inputDirectory), target);
		target.close();
	}

	private void add(File source, JarOutputStream target) throws IOException
	{
		BufferedInputStream in = null;
		try
		{
			if (source.isDirectory())
			{
				String name = source.getPath().replace("\\", "/");
				if (!name.isEmpty())
				{
					if (!name.endsWith("/"))
						name += "/";
					JarEntry entry = new JarEntry(name);
					entry.setTime(source.lastModified());
					target.putNextEntry(entry);
					target.closeEntry();
				}
				for (File nestedFile: source.listFiles())
					add(nestedFile, target);
				return;
			}

			JarEntry entry = new JarEntry(source.getPath().replace("\\", "/"));
			entry.setTime(source.lastModified());
			target.putNextEntry(entry);
			in = new BufferedInputStream(new FileInputStream(source));

			byte[] buffer = new byte[1024];
			while (true)
			{
				int count = in.read(buffer);
				if (count == -1)
					break;
				target.write(buffer, 0, count);
			}
			target.closeEntry();
		}
		finally
		{
			if (in != null)
				in.close();
		}
	}
	
	public void extractJar(String jarPathName, String destDir) throws IOException {
		JarFile jarfile = new JarFile(new File(jarPathName));
		java.util.Enumeration<JarEntry> enu = jarfile.entries();
		numJarElementsExracted = 0;
		while(enu.hasMoreElements()) {
			
			JarEntry je = enu.nextElement();

			numJarElementsExracted++;

			File fl = new File(destDir, je.getName());
			if(!fl.exists())
			{
				fl.getParentFile().mkdirs();
				fl = new java.io.File(destDir, je.getName());
			}
			if(je.isDirectory()) {
					continue;
			}
			InputStream is = jarfile.getInputStream(je);
			FileOutputStream fo = new FileOutputStream(fl);
			while(is.available() > 0) {
				fo.write(is.read());
			}
			fo.close();
			is.close();
		}
	}
	
	public int getNumJarElementsExtracted() {
		return numJarElementsExracted;
	}
}
