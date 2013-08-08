package data.retrieval.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;



//This implementation holds the data using HashMap. 
public class DataMap extends Data {
	Map<Integer, ProductData> productMap = new HashMap<Integer, ProductData>();
	Map<String, Set<Integer>> queryMap = new HashMap<String, Set<Integer>>();
	Map<String, Set<String>> artistToQueryMap = new HashMap<String, Set<String>>();
	
	public DataMap(File qFile, File pFile) {
		super(qFile, pFile);
	}
	public DataMap() {
		
	}
	@Override
	public void loadData() throws FileNotFoundException, IOException {
		loadProductData();
		loadQueryData();
	}

	private void loadProductData() throws IOException {		
		Gson gson = new Gson();
		BufferedReader reader = new BufferedReader(new FileReader(productFile));
		Product[] products = gson.fromJson(reader, Product[].class);
		reader.close();
		for(int i=0; i<products.length; i++) {
			Product prd = products[i];
			if(prd!=null) {
				productMap.put(prd.productId, new ProductData(prd.productName, prd.genre, prd.artist));
			}
		}
	}

	private void loadQueryData() throws IOException {
		Gson gson = new Gson();
		BufferedReader reader = new BufferedReader(new FileReader(queryDataFile));
		QueryData[] qData = gson.fromJson(reader, QueryData[].class);
		reader.close();
		for(int i=0; i<qData.length; i++) {
			 QueryData data = qData[i];
			 if(data == null || data.query == null) {
				 continue;
			 }
			 String normalizedQueryData = normalizeData(data.query);
			 if(!queryMap.containsKey(normalizedQueryData)) {
				 Set<Integer> pids = new HashSet<Integer>();
				 pids.add(data.productId);
				 queryMap.put(normalizedQueryData, pids);
			 } else {
				 Set<Integer> pids = queryMap.get(normalizedQueryData);
				 pids.add(data.productId);
				 queryMap.put(normalizedQueryData, pids);
			 }
			 ProductData pData = productMap.get(data.productId);
			 if(pData != null && normalizedQueryData != null) {
				 if(!artistToQueryMap.containsKey(pData.artist)) {
					 Set<String> queryVals = new HashSet<String>();
					 queryVals.add(normalizedQueryData);
					 artistToQueryMap.put(pData.artist, queryVals);
				 } else {
					 Set<String> queryVals = artistToQueryMap.get(pData.artist);
					 queryVals.add(normalizedQueryData);
					 artistToQueryMap.put(pData.artist, queryVals);
				 }
			 }
		}
	}

	@Override
	public List<ProductData> getProducts(String queryString) {
		if(queryString == null) {
			return null;
		}
		queryString = normalizeData(queryString);
		Set<Integer> pids = queryMap.get(queryString);
		List<ProductData> pData = new ArrayList<Data.ProductData>();
		for(Integer pid : pids) {
			pData.add(productMap.get(pid));
		}
		return pData;
	}

	@Override
	public Set<String> getQueryStrings(String artist) {
		return artistToQueryMap.get(artist);
	}
	
	public static class Product {
		public String productName;
		public String genre;
		public String artist;
		public int productId;
		
		public Product() {
			
		}
	}
	
	public static class QueryData {
		String query;
		String timestamp;
		int productId;
		public QueryData() {
			
		} 
	}
}
