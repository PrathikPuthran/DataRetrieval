package data.retrieval.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public abstract class Data {
	
	public File queryDataFile;
	public File productFile;
	
	public Data(File qdFile, File pFile) {
		this.queryDataFile = qdFile;
		this.productFile = pFile;
	}
	public Data() {
		
	}
	public abstract void loadData() throws FileNotFoundException, IOException;
	public abstract List<ProductData> getProducts(String queryString);
	public abstract Set<String> getQueryStrings(String artist);
	
	public String normalizeData(String query) {
		if(query == null) {
			return null;
		}
		String res = truncate(query);
		res = removePunctuations(res);
		res = removeExtraSpaces(res);
		return res;
	}
	
	public String truncate(String res) {
		return res.replaceAll("^\\s+|\\s+$", "");
	}
	
	public String removePunctuations(String res) {
		return res.replaceAll("[^a-zA-Z0-9 ]", "");
	}
	
	public String removeExtraSpaces(String query) {
		return query.replaceAll("\\s\\s+", "\\s");
	}
	
	public static class ProductData {
		String productName;
		String genre;
		String artist;
		public ProductData(String pName, String genre, String artist) {
			this.productName = pName;
			this.genre = genre;
			this.artist = artist;
		}
		
		public String toString() {
			return "{productName : "+productName+", genre:" +genre +", artist:"+artist +"}";
		}
	}
}
