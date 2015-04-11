package com.myproject.analysis.java;

import java.util.HashMap;
import java.util.Map;

public class HiveCache {
	Map<String,Integer> cache;
	int[][] cache_matrix;
	int cacheSize;
	int current_index;
	public HiveCache() {
		cache=new HashMap<String,Integer>();
		cacheSize=50;
//		cache_matrix=new int[50][50];
		current_index=0;
//		for(int i=0;i<50;i++)
//			for(int j=0;j<50;j++)
//				cache_matrix[i][j]=0;
	}
	
	public void setCacheSize(int size) {
		current_index=0;
//		int[][] temp=new int[cacheSize][cacheSize];
//		for(int i=0;i<cacheSize;i++) {
//			for(int j=0;j<cacheSize;j++)
//				temp[i][j]=cache_matrix[i][j];
//		}
//		cache_matrix=new int[size][size];
//		for(int i=0;i<size;i++) {
//			for(int j=0;j<size;j++) {
//				if(i<cacheSize||j<cacheSize)
//					cache_matrix[i][j]=temp[i][j];
//				else
//					cache_matrix[i][j]=0;
//			}
//		}
		cacheSize=size;
	}
	
    public boolean find(String HQL) {
    	return cache.containsKey(HQL);
	}
    
    public int put(String HQL) {
    	int index=check();
    	cache.put(HQL, index);
		return index;
    }
    public int get(String HQL) {
    	return cache.get(HQL);
    }
    
    public int check() {
    	current_index++;
    	if(current_index>=50)
    		current_index=current_index-50;
    	return current_index;
    }
    
    private boolean compare(int a, int b) {
    	for(int i=0;i<cacheSize;i++) {
    		if(cache_matrix[a][i]<cache_matrix[b][i])
    			return false;
    		else
    			return true;
    	}
    	return true;
    }
}
