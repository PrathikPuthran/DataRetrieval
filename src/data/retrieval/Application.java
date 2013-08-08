package data.retrieval;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

import data.retrieval.data.Data;
import data.retrieval.data.Data.ProductData;
import data.retrieval.data.DataArray;
import data.retrieval.data.DataMap;



public class Application {
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		String pFile;
		String qFile;
		String className = null;
		if(args.length == 1) {
			className = args[0];
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter the path to the product data JSON file");
		pFile = reader.readLine();
		System.out.println("Enter the path to the query data JSON file");
		qFile = reader.readLine();
		File qF = new File(qFile);
		File pF = new File(pFile); 
		if(!qF.exists() || !pF.exists()) {
			throw new IllegalArgumentException("One of the file does not exist");
		}
		Data data;
		if(className != null) {
			System.out.println("Using class name "+ className);
			data = (Data)Class.forName(className).newInstance();
			data.productFile = pF;
			data.queryDataFile = qF;
		} else {
			data = new DataArray(qF, pF);
		}
		System.out.println("Loading the data to memory. Please wait");
		data.loadData();
		System.out.println("Data load completed");
		while(true) {
			showMessage();
			String opt = reader.readLine();
			if("1".equals(opt)) {
				System.out.println("Enter the query string");
				String query = reader.readLine();
				List<ProductData> products = data.getProducts(query);
				printProducts(products);
			} else if("2".equals(opt)) {
				System.out.println("Enter the artist name");
				String artist = reader.readLine();
				Set<String> queries = data.getQueryStrings(artist);
				printQueries(queries);
			} else if("3".equals(opt)) {
				break;
			} else {
				System.out.println("Invalid input");
			}
		}
	}
	
	public static void showMessage() {
		System.out.println("\n1=>Find products by query, 2=>Find queries by artist, 3=>Exit");
		System.out.println("Please provide your option");
	}
	
	public static void printProducts(List<ProductData> products) {
		if(products.size() == 0) {
			System.out.println("No results found");
			return;
		}
		System.out.println("Products are :\n");
		for(ProductData pd : products) {
			if(pd!=null) {
				System.out.println(pd.toString());
			}
		}
	}
	
	public static void printQueries(Set<String> queries) {
		if(queries == null || queries.size() == 0) {
			System.out.println("No results found");
			return;
		}
		System.out.println("Queries corresponding to the artist:\n");
		for(String q : queries) {
			if(q!=null) {
				System.out.println(q);
			}
		}
	}
}
