package it.smartcommunitylab.cartella.asl.model.ext;

import java.io.File;

public class OdtFile {
	private File file;
	private String filename;
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
}
