package com.absolute;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import redis.clients.jedis.Jedis;

//2 conventions, 1) store null as NULL
// 2) leave null off, dont store the field. 
public class LoadRedisCSV {
	private static List<String> headerList;
	private static Jedis jedis;
	static private String firstLine;

	public LoadRedisCSV(){
		jedis = new Jedis("localhost");
	}
	
	private static void init(){
		headerList = new ArrayList<String>();
	}
	
	
	private static void parseHeader(String header){
		firstLine = header;
		StringTokenizer st = new StringTokenizer(header, "^");
		while(st.hasMoreTokens()){
			headerList.add(st.nextToken());
		}
	}
	
	private static void loadFileasHM(String tableName, String file){
		try{
			init();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String fileLine = null;
			String header = br.readLine();
			parseHeader(header);
			
			int numMatch=0;
			int numNoMatch=0;
			int numRows=0;
			
			while((fileLine=br.readLine())!=null){
				StringTokenizer st = new StringTokenizer(fileLine,"^");
				List<String> dataList = new ArrayList<String>();			
				
				while(st.hasMoreTokens()){
					String next =  st.nextToken();
					dataList.add(next);
				}
								
				if(headerList.size()!=dataList.size()){
					numNoMatch++;
				}else{
					numMatch++;
					Map<String,String> map = new TreeMap<String,String>();
					int numCols=0;
					for(String h:headerList){
						map.put(h, dataList.get(numCols));
						numCols++;
					}
					jedis.hmset(tableName+numRows, map);
					numRows++;
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static void buildDataStructs(){
		Set<String> set = jedis.keys("vwDDDeviceByGroup*");
		ArrayList<String> temp = new ArrayList<String>();
		Map<String,List<String>> groupIDMap = new TreeMap<String,List<String>>();
		for(String st : set){
			Map<String,String> map = jedis.hgetAll(st);
			String groupID = map.get("GroupId");
			String ESN = map.get("ESN");
			if(groupIDMap.get(groupID)==null){
				ArrayList<String> al = new ArrayList<String>();
				al.add(ESN);
				groupIDMap.put(groupID,al);
			}else{
				List<String> al = groupIDMap.get(groupID);
				al.add(ESN);
			}
						
		}
		Set<String> groupIDKeys = groupIDMap.keySet();
		for(String groupIDs:groupIDKeys){
	//		System.out.println("groupID:"+groupIDs+" numvwDDDGroups:"+groupIDMap.get(groupIDs));
			List<String> groupIDList = groupIDMap.get(groupIDs);
			for(String str:groupIDList){
				if(groupIDs.equals("555")){
					System.out.println("555 ENTERING IN push"+groupIDs+"  str:"+str);
				}
				jedis.rpush(groupIDs,str);
			}
		}
		
		List<String> list = jedis.lrange("555", 0, jedis.llen("555"));
		for(String inList:list){
			System.out.println("groupID 555 :"+inList);
		}
		System.out.println("num keys:"+set.size());
		System.out.println("num distinct groupIds:"+groupIDMap.size());
		

	}
	
	public static void main(String []args){
		new LoadRedisCSV();
		jedis.select(0);
		jedis.flushDB();
		loadFileasHM("vwDDDeviceByGroup","/home/dc/storm/vwDDDeviceByGroup.csv");
		loadFileasHM("vwDDDevice","/home/dc/storm/vwDDDevice.csv");
		//run in redis-cli
		//hmget "vwDDDevice1" ESN
		//hmget "vwDDDeviceByGroup1" ESN
		System.out.println("db size:"+jedis.dbSize());
		buildDataStructs();
	}
}