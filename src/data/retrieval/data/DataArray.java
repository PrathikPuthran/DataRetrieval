package data.retrieval.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;

//This implementation is based on ordered data and binary search over this data
public class DataArray extends Data {
	Product[] products;
	QueryData[] queries;
	List<ArtistData> artistData = new ArrayList<DataArray.ArtistData>();
	public DataArray(File qdFile, File pFile) {
		super(qdFile, pFile);
	}

	public DataArray() {
		
	}
	@Override
	public void loadData() throws FileNotFoundException, IOException {
		loadProductData();
		loadQueryData();
	}
	
	private void loadProductData() throws IOException {
		Gson gson = new Gson();
		BufferedReader reader = new BufferedReader(new FileReader(productFile));
		products = gson.fromJson(reader, Product[].class);
		reader.close();
		Arrays.sort(products);
		Set<String> artists = new HashSet<String>();
		for(int i=0; i<products.length; i++) {
			if(products[i] != null && products[i].artist != null && !artists.contains(products[i].artist)) {
				artistData.add(new ArtistData(products[i].artist));
				artists.add(products[i].artist);
			}
		}
		Collections.sort(artistData);
	}
	
	private void loadQueryData() throws IOException {
		Gson gson = new Gson();
		BufferedReader reader = new BufferedReader(new FileReader(queryDataFile));
		queries = gson.fromJson(reader, QueryData[].class);
		reader.close();
		for(int i=0; i<queries.length; i++) {
			if(queries[i] == null) {
				queries[i] = new QueryData();
				queries[i].query = null;
			}
		}
		Arrays.sort(queries);
		for(int i=0; i<queries.length; i++) {
			if(queries[i]!=null && queries[i].query != null) {
				Product p1 = new Product();
				p1.productId = queries[i].productId;
				int index = Arrays.binarySearch(products, p1);
				if(index >= 0) {
					ArtistData data = new ArtistData(products[index].artist);
					int ind = Collections.binarySearch(artistData, data);
					if(ind >= 0) {
						artistData.get(ind).queryIndex.add(i);
					}
				}
			}
		}
	}
	
	@Override
	public List<ProductData> getProducts(String queryString) {
		List<ProductData> result = new ArrayList<Data.ProductData>();
		QueryData d = new QueryData();
		d.query = queryString;
		int ind = Arrays.binarySearch(queries, d);
		Set<Integer> seen = new HashSet<Integer>();
		if(ind >= 0 ) {
			int v2 = ind;
			while(ind<queries.length && queries[ind] != null && queries[ind].query != null && queries[ind].query.equals(queryString)) {
				int pId = queries[ind].productId;
				Product d1 = new Product();
				d1.productId = pId;
				int res = Arrays.binarySearch(products, d1);
				if(res>=0 && !seen.contains(products[res].productId)) {
					result.add(new ProductData(products[res].productName, products[res].genre, products[res].artist));
					seen.add(products[res].productId);
				}
				ind++;
			}
			ind = v2--;
			while(ind>=0 && queries[ind] != null && queries[ind].query != null && queries[ind].query.equals(queryString)) {
				int pId = queries[ind].productId;
				Product d1 = new Product();
				d1.productId = pId;
				int res = Arrays.binarySearch(products, d1);
				if(res>=0 && !seen.contains(products[res].productId)) {
					result.add(new ProductData(products[res].productName, products[res].genre, products[res].artist));
					seen.add(products[res].productId);
				}
				ind--;
			}			
		}
		return result;
	}

	@Override
	public Set<String> getQueryStrings(String artist) {
		Set<String> result = new HashSet<String>();
		ArtistData d1 = new ArtistData(artist);
		int ind = Collections.binarySearch(artistData, d1);
		if(ind>=0) {
			ArtistData aD = artistData.get(ind);
			for(Integer i : aD.queryIndex) {
				if(queries[i] != null && queries[i].query != null) {
					result.add(queries[i].query);
				}
			}
		}
		return result;
	}
	
	public static class Product implements Comparable<Product>{
		public String productName;
		public String genre;
		public String artist;
		public int productId;
		
		public Product() {
			
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + productId;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Product other = (Product) obj;
			if (productId != other.productId)
				return false;
			return true;
		}

		@Override
		public int compareTo(Product o) {
			if(o==null) {
				return -1;
			} else {
				return this.productId - o.productId;
			}
		}
		
	}
	
	public static class QueryData implements Comparable<QueryData> {
		String query;
		String timestamp;
		int productId;
		public QueryData() {
			
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((query == null) ? 0 : query.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			QueryData other = (QueryData) obj;
			if (query == null) {
				if (other.query != null)
					return false;
			} else if (!query.equals(other.query))
				return false;
			return true;
		}

		@Override
		public int compareTo(QueryData o) {
			if(o==null || o.query == null) {
				return -1;
			} else if(this.query == null) {
				return 1;
			} else {
				return this.query.compareTo(o.query);
			}
			
		} 
	}
	
	public static class ArtistData implements Comparable<ArtistData> {
		String artistName;
		List<Integer> queryIndex = new ArrayList<Integer>();
		public ArtistData(String aName) {
			artistName = aName;
		}
		@Override
		public int compareTo(ArtistData o) {
			if(o==null || o.artistName==null) {
				return -1;
			} else if(this.artistName == null) {
				return 1;
			} else {
				return this.artistName.compareTo(o.artistName);
			}
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((artistName == null) ? 0 : artistName.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ArtistData other = (ArtistData) obj;
			if (artistName == null) {
				if (other.artistName != null)
					return false;
			} else if (!artistName.equals(other.artistName))
				return false;
			return true;
		}
		
	}
}
