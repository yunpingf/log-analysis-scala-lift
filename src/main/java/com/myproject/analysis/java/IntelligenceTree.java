package com.myproject.analysis.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IntelligenceTree extends HiveJdbcDriver {
	private int inter_video=3;
	public JSONObject getVideoRank(int rank)
			throws SQLException, JSONException {
		System.out.println("Get Property");
		Properties prop=new Properties();
		FileInputStream propIn;
		try {
			propIn = new FileInputStream("app.properties");
			try {
				prop.load(propIn);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("error load properties");
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("error load file");
			e.printStackTrace();
		}
		String tableName;
		tableName=prop.getProperty("info.tableName");
		System.out.println("Get Video Rank");
		String sql = "select count, video_id from rankTable " 
				+ " limit " + rank;
		int cacheI=0;
		if (!hCache.find(sql)) {
			cacheI=hCache.put(sql);
			stmt.execute("drop table cache.tmp" + cacheI);
			stmt.execute("create table cache.tmp" + cacheI 
					+ " as "+sql);
		} else {
			cacheI = hCache.get(sql);
		}
//		ResultSet res = stmt.executeQuery(sql);
		ResultSet 	res = stmt.executeQuery("select * from cache.tmp" +cacheI);
		JSONObject result = new JSONObject();
		JSONArray series = new JSONArray();
		JSONObject tableData = new JSONObject();
		JSONArray table = new JSONArray();
		List count = new ArrayList();
		List device = new ArrayList();
		while (res.next()) {
			count.add(res.getString(1));
			device.add(res.getString(2));
		}
		JSONObject temp;
		int[] data = new int[count.size()];
		String[] categories = new String[count.size()];
		for (int i = 0; i < count.size(); i++) {
			data[i] = Integer.parseInt(count.get(i).toString());
			categories[i] = device.get(i).toString();
			temp = new JSONObject();
			temp.put("count", Integer.parseInt(count.get(i).toString()));
			temp.put("video_id", device.get(i));
			table.put(temp);
		}
		JSONObject seriesObj = new JSONObject();
		seriesObj.put("data", data);
		seriesObj.put("name", "count");
		series.put(seriesObj);
		JSONObject chartData = new JSONObject();
		chartData.put("series", series);
		chartData.put("categories", categories);
		result.put("tableData", table);
		result.put("chartData", chartData);
		return result;
	}

	public JSONObject getUserVideoRank(String uid)
			throws SQLException, JSONException, IOException {
		Properties prop=new Properties();
		FileInputStream propIn=new FileInputStream("app.properties");
		prop.load(propIn);
		String tableName;
		tableName=prop.getProperty("info.tableName");
		String sql = "select count(*) c, video_id from " + tableName
				+ " where user_id='" + uid
				+ "' group by video_id sort by c desc";
		int cacheI=0;
		if (!hCache.find(sql)) {
			cacheI=hCache.put(sql);
			stmt.execute("drop table cache.tmp" + cacheI);
			stmt.execute("create table cache.tmp" + cacheI 
					+ " as "+sql);
		} else {
			cacheI = hCache.get(sql);
		}
//		ResultSet res = stmt.executeQuery(sql);
		ResultSet 	res = stmt.executeQuery("select * from cache.tmp" +cacheI);
		JSONObject result = new JSONObject();
		JSONArray series = new JSONArray();
		JSONObject tableData = new JSONObject();
		JSONArray table = new JSONArray();
		List count = new ArrayList();
		List device = new ArrayList();
		while (res.next()) {
			count.add(res.getString(1));
			device.add(res.getString(2));
		}
		JSONObject temp;
		int[] data = new int[count.size()];
		String[] categories = new String[count.size()];
		for (int i = 0; i < count.size(); i++) {
			data[i] = Integer.parseInt(count.get(i).toString());
			categories[i] = device.get(i).toString();
			temp = new JSONObject();
			temp.put("count", Integer.parseInt(count.get(i).toString()));
			temp.put("video_id", device.get(i));
			table.put(temp);
		}
		JSONObject seriesObj = new JSONObject();
		seriesObj.put("data", data);
		seriesObj.put("name", "count");
		series.put(seriesObj);
		JSONObject chartData = new JSONObject();
		chartData.put("series", series);
		chartData.put("categories", categories);
		result.put("tableData", table);
		result.put("chartData", chartData);
		return result;
	}
	
	public void importHomeworkData(String path,String course_name) throws SQLException {
		hCache=new HiveCache();
		String sql;
		sql = "create table if not exists homework_"+course_name+" ( "
				+ "userId String," + "userName String, " + "homeworkName String,"
				+ "homeworkId String," + "startTime String," + "deadline String,"
				+ "actualStart String, actualEnd String, relateVideo String) "
				+ "ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' ";
		stmt.executeQuery(sql);
		sql = "load data local inpath '" + path
				+ "' into table homework_"+course_name;
		stmt.execute(sql);
		sql="create table if not exists homeworkdetail( homeworkId String, startTime String, deadline String, "+
		"courseName String, relateVideo String)";
		stmt.execute(sql);
		sql="insert into table homeworkdetail select distinct homeworkId, startTime, deadline,'"+course_name+"', relateVideo from homework_"+course_name;
		stmt.execute(sql);
	}

	public void importVideoData(String path) throws SQLException, IOException {
		hCache=new HiveCache();
		Properties prop=new Properties();
		FileInputStream propIn=new FileInputStream("app.properties");
		prop.load(propIn);
		String tableName1,tableName2;
		tableName1=prop.getProperty("info.tableName");
		tableName2=prop.getProperty("action.tableName");
		String sql;
		System.out.println("start loading");
		sql = "create table if not exists " + tableName1 + " ( "
				+ "randomID String," + "time String, " + "video_id String,"
				+ "user_id String," + "user_ip String," + "session_id String,"
				+ "agent String)"
				+ "ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' ";
		stmt.executeQuery(sql);
		sql = "create table if not exists " + tableName2 + " ( "
				+ "randomID String," + " video_id String, video_time String, action String, action_info String)"
				+ "ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' ";
		stmt.executeQuery(sql);
		sql="drop table videoActionTemp";
		stmt.execute(sql);
		sql = "create table if not exists videoActionTemp ( "
				+ "randomID String," + " video_id String, video_time String, action String, action_info String)"
				+ "ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' ";
		stmt.executeQuery(sql);
		sql = "create table if not exists pauseTable ( "
				+ "tid String," + " video_id String, video_time String, action String, time int)"
				+ "ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' ";
		stmt.executeQuery(sql);
		sql = "create table if not exists moveTable ( "
				+ "time String," + " video_id String, video_time String, action String, start_time int, end_time int) "
				+ "ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' ";
		stmt.executeQuery(sql);
		sql = "create table if not exists rankTable ( "
				+ "video_id String, count String)"
				+ "ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' ";
		stmt.executeQuery(sql);
		sql="create database if not exists videoinfo";
		stmt.execute(sql);
		File newDir = new File(path);
		File[] files = newDir.listFiles();
		int index = 0;
		String str = null;
		File info = new File(
				"/tmp/info.txt");
		File action = new File(
				"/tmp/action.txt");
		File pause = new File(
				"/tmp/pause.txt");
		if(info.exists())
		info.delete();
		if(action.exists())
		action.delete();
		if(pause.exists())
		pause.delete();
			action.createNewFile();
			info.createNewFile();
			pause.createNewFile();
		System.out.println(info.getPath());
		FileWriter os_info = new FileWriter(
				"/tmp/info.txt");
		FileWriter os_action = new FileWriter(
				"/tmp/action.txt");
		FileWriter os_pause = new FileWriter(
				"/tmp/pause.txt");
		int count = 0;
		List video_id=new ArrayList();
		
		for (int i = 0; i < files.length; i++) {
			String vid = null;
			String tid = null;
			if(files[i].getName().equals("log.pid")) {
				System.out.println("log.pid");
				continue;
			}
			FileReader reader = new FileReader(files[i]);
			BufferedReader br = new BufferedReader(reader);
			str = br.readLine();
			index = str.indexOf('+');
			if (index == 20) {
				String temp = str.substring(0, 7);
				os_info.write(temp);
				os_info.write("\t");
				str = str.substring(7);
				for (int n = 0; n < 4; n++) {
					index = str.indexOf('+');
					if (index <= 0) {
						os_info.write("-" + "\t");
						continue;
					}
					temp = str.substring(0, index);
					if (n == 1)
						vid = temp;
					if (n == 0)
						tid = temp;
					os_info.write(temp + "\t");
					str = str.substring(index + 1);
				}
				os_info.write(str + "\t");
				str = br.readLine();
				if (str != null)
					os_info.write(str);
				else
					os_info.write("-");
			} else {
				os_info.write(str + "\t");
				str = br.readLine();
				String temp;
				if (str == null) {
					str = "-+-+-+-+-";
					os_info.write("-\t");
				} else {
					temp = str.substring(0, 7);
					os_info.write(temp);
					os_info.write("\t");
					str = str.substring(7);
				}
				for (int n = 0; n < 4; n++) {
					index = str.indexOf('+');
					if (index <= 0) {
						os_info.write("-" + "\t");
						continue;
					}
					temp = str.substring(0, index);
					if (n == 1)
						vid = temp;
					if (n == 0)
						tid = temp;
					os_info.write(temp + "\t");
					str = str.substring(index + 1);
				}
				if (str != null)
					os_info.write(str);
				else
					os_info.write("-");
			}
			os_info.write("\n");
			if(!video_id.contains(vid)) {
				video_id.add(vid);
				System.out.println(vid);
			}
			boolean find_pause=false;
			while ((str = br.readLine()) != null) {
				index = str.indexOf('|');
				while (index >= 0) {
					os_action.write(tid + "\t");
					os_action.write(vid + "\t");
					String temp=new String();
					if(str.charAt(7)>'9') {
						temp=str.substring(0,6);
						String time_tmp=temp;
						os_action.write(temp+"\t");
						temp=str.substring(7,8);
						os_action.write(temp+"\t");
						if(temp.equals("h"))
							find_pause=true;
						String act=temp;
						temp=str.substring(8,index);
						if(act.equals("n")&&find_pause) {
							System.out.println("pause");
							find_pause=false;
							os_pause.write(tid + "\t");
							os_pause.write(vid + "\t");
							os_pause.write(time_tmp + "\t");
							os_pause.write(act + "\t");
							os_pause.write(temp + "\n");
						}
					}
					else if(str.charAt(8)>'9') {
						temp=str.substring(0,7);
						String time_tmp=temp;
						os_action.write(temp+"\t");
						temp=str.substring(8,9);
						os_action.write(temp+"\t");
						if(temp.equals("h"))
							find_pause=true;
						String act=temp;
						temp=str.substring(9,index);
						if(act.equals("n")&&find_pause) {
							System.out.println("pause");
							find_pause=false;
							os_pause.write(tid + "\t");
							os_pause.write(vid + "\t");
							os_pause.write(time_tmp + "\t");
							os_pause.write(act + "\t");
							os_pause.write(temp + "\n");
						}
					}
					else if(str.charAt(9)>'9') {
						temp=str.substring(0,8);
						String time_tmp=temp;
						os_action.write(temp+"\t");
						temp=str.substring(9,10);
						os_action.write(temp+"\t");
						if(temp.equals("h"))
							find_pause=true;
						String act=temp;
						temp=str.substring(10,index);
						if(act.equals("n")&&find_pause) {
							System.out.println("pause");
							find_pause=false;
							os_pause.write(tid + "\t");
							os_pause.write(vid + "\t");
							os_pause.write(time_tmp + "\t");
							os_pause.write(act + "\t");
							os_pause.write(temp + "\n");
						}
					}
					os_action.write(temp + "\n");
					str = str.substring(index + 1);
					index = str.indexOf('|');
				}
				os_action.write(tid + "\t");
				os_action.write(vid + "\t");
				String temp = str;
				if(str.charAt(7)>'9') {
					temp=str.substring(0,6);
					os_action.write(temp+"\t");
					String time_tmp=temp;
					temp=str.substring(7,8);
					os_action.write(temp+"\t");
					if(temp.equals("h"))
						find_pause=true;
					String act=temp;
					temp=str.substring(8);
					if(act.equals("n")&&find_pause) {
						System.out.println("8"+temp);
						find_pause=false;
						os_pause.write(tid + "\t");
						os_pause.write(vid + "\t");
						os_pause.write(time_tmp + "\t");
						os_pause.write(act + "\t");
						os_pause.write(temp + "\n");
					}
				}
				else if(str.charAt(8)>'9') {
					temp=str.substring(0,7);
					os_action.write(temp+"\t");
					String time_tmp=temp;
					temp=str.substring(8,9);
					os_action.write(temp+"\t");
					if(temp.equals("h"))
						find_pause=true;
					String act=temp;
					temp=str.substring(9);
					if(act.equals("n")&&find_pause) {
						System.out.println("9"+temp);
						find_pause=false;
						os_pause.write(tid + "\t");
						os_pause.write(vid + "\t");
						os_pause.write(time_tmp + "\t");
						os_pause.write(act + "\t");
						os_pause.write(temp + "\n");
					}
				}
				else if(str.charAt(9)>'9') {
					temp=str.substring(0,8);
					os_action.write(temp+"\t");
					String time_tmp=temp;
					temp=str.substring(9,10);
					os_action.write(temp+"\t");
					if(temp.equals("h"))
						find_pause=true;
					String act=temp;
					temp=str.substring(10);
					if(act.equals("n")&&find_pause) {
						System.out.println("10"+temp);
						find_pause=false;
						os_pause.write(tid + "\t");
						os_pause.write(vid + "\t");
						os_pause.write(time_tmp + "\t");
						os_pause.write(act + "\t");
						os_pause.write(temp + "\n");
					}
				}
				os_action.write(temp + "\n");
			}
			files[i].delete();
			count++;
			if (count == 100) {
				count = 0;
				os_action.close();
				sql = "load data local inpath '" + action.getPath()
						+ "' into table " + tableName2;
				System.out.println(sql);
				stmt.execute(sql);
				sql = "load data local inpath '" + action.getPath()
						+ "' into table videoActionTemp";
				stmt.execute(sql);
				os_action = new FileWriter("action.txt");
			}
		}
		if (count != 100) {
			count = 0;
			os_action.close();
			sql = "load data local inpath '" + action.getPath()
					+ "' into table " + tableName2;
			stmt.execute(sql);
			sql = "load data local inpath '" + action.getPath()
					+ "' into table videoActionTemp";
			stmt.execute(sql);
			os_action = new FileWriter("action.txt");
		}
		os_info.close();
		sql = "load data local inpath '" + info.getPath() + "' into table "
				+ tableName1;
		System.out.println(sql);
		stmt.execute(sql);
		os_pause.close();
		sql = "load data local inpath '" + pause.getPath() + "' into table pauseTable";
		System.out.println(sql);
		stmt.execute(sql);
		sql="insert into table moveTable select randomID, video_id, video_time, action, cast(split(action_info,'[+]')[0] as INT),cast(split(action_info,'[+]')[1] as INT) "+
		"from videoActionTemp where action='m'";
		stmt.execute(sql);
		sql="insert overwrite table rankTable select video_id, count(*) c "+
		"from videoinfo group by video_id sort by c desc";
		System.out.println(sql);
		stmt.execute(sql);
		List pause_res;
		List forward_res;
		List back_res;
		ResultSet res;
		for(int i=0;i<video_id.size();i++) {
			String vid=video_id.get(i).toString();
			pause_res=new ArrayList();
			forward_res=new ArrayList();
			back_res=new ArrayList();
			sql = "select count(*), cast(time/"+inter_video+" as INT) vtime from pauseTable "+
					" where video_id='"+vid+"' and time is not NULL group by cast(time/"+inter_video+" as INT) sort by vtime";
			res=stmt.executeQuery(sql);
			int real_size=0;
			while (res.next()) {
				int tmp=Integer.parseInt(res.getString(2));
				while(real_size<tmp) {
					pause_res.add(0);
					real_size++;
				}
				pause_res.add(res.getString(1));
				real_size++;
				
			}
			sql = "select count(*),cast(start_time/"+inter_video+" as INT) s from moveTable "+
					" where video_id='"+vid+"' and start_time is not NULL and start_time<end_time group by cast(start_time/"+inter_video+" as INT) sort by s";
			res=stmt.executeQuery(sql);
			real_size=0;
			while (res.next()) {
				int tmp=Integer.parseInt(res.getString(2));
				while(real_size<tmp) {
					forward_res.add(0);
					real_size++;
				}
				forward_res.add(res.getString(1));
				real_size++;
				
			}
			sql = "select count(*),cast(start_time/"+inter_video+" as INT) s from moveTable "+
					" where video_id='"+vid+"' and start_time is not NULL and start_time>end_time group by cast(start_time/"+inter_video+" as INT) sort by s";
			res=stmt.executeQuery(sql);
			real_size=0;
			while (res.next()) {
				int tmp=Integer.parseInt(res.getString(2));
				while(real_size<tmp) {
					back_res.add(0);
					real_size++;
				}
				back_res.add(res.getString(1));
				real_size++;				
			}
			File video = new File(
					"/tmp/video.txt");
			if(video.exists())
			video.delete();
			int size=0;
			FileWriter os_video = new FileWriter(
					"/tmp/video.txt");
			if(pause_res.size()>forward_res.size()) {
				if(pause_res.size()>back_res.size())
					size=pause_res.size();
				else 
					size=back_res.size();
			}
			else if(forward_res.size()>back_res.size())
				size=forward_res.size();
			else 
				size=back_res.size();
			for(int j=0;j<size;j++) {
				os_video.write(vid+"\t");
				if(j>(pause_res.size()-1))
					os_video.write(0+"\t");
				else
					os_video.write(pause_res.get(j).toString()+"\t");
				if(j>(forward_res.size()-1))
					os_video.write(0+"\t");
				else
					os_video.write(forward_res.get(j).toString()+"\t");
				if(j>(back_res.size()-1))
					os_video.write(0+"\t");
				else
					os_video.write(back_res.get(j).toString()+"\t");
				os_video.write(j*inter_video+"\n");
			}
			os_video.close();
			sql="drop table videoinfo.video"+vid;
			stmt.execute(sql);
			sql = "create table videoinfo.video"+vid+" ( "
					+ "video_id String, count_pause String, count_forward String, count_back String, time String)"
					+ "ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' ";
			System.out.println(sql);
			stmt.execute(sql);
			sql = "load data local inpath '" + video.getPath() + "' into table videoinfo.video"+vid;
			stmt.execute(sql);
		}
		
	}
	
	public JSONObject getVideoPause(int videoId,int inter)
			throws SQLException, JSONException {
		System.out.println("Get Property");
		Properties prop=new Properties();
		FileInputStream propIn;
		try {
			propIn = new FileInputStream("app.properties");
			try {
				prop.load(propIn);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("error load properties");
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("error load file");
			e.printStackTrace();
		}
		String tableName;
		tableName=prop.getProperty("action.tableName");
		String sql = "select count(*), cast(time/"+inter+" as INT) vtime from pauseTable "+
				" where video_id='"+videoId+"' and time is not NULL group by cast(time/"+inter+" as INT) sort by vtime";
		System.out.println("Before executation");
		System.out.println(sql);
		int cacheI=0;
		if (!hCache.find(sql)) {
			cacheI=hCache.put(sql);
			stmt.execute("drop table cache.tmp" + cacheI);
			stmt.execute("create table cache.tmp" + cacheI 
					+ " as "+sql);
		} else {
			cacheI = hCache.get(sql);
		}
//		ResultSet res = stmt.executeQuery(sql);
		ResultSet 	res = stmt.executeQuery("select * from cache.tmp" +cacheI);
		System.out.println("After executation");
		JSONObject result = new JSONObject();
		JSONArray series = new JSONArray();
		JSONObject tableData = new JSONObject();
		JSONArray table = new JSONArray();
		List count = new ArrayList();
		List device = new ArrayList();
		int real_size=0;
		while (res.next()) {
			int tmp=Integer.parseInt(res.getString(2));
			while(real_size<tmp) {
				count.add(0);
				device.add(real_size*inter);
				real_size++;
			}
			count.add(res.getString(1));
			device.add(Integer.parseInt(res.getString(2))*inter);
			real_size++;
			
		}
		JSONObject temp;
		int[] data = new int[count.size()];
		String[] categories = new String[count.size()];
		for (int i = 0; i < count.size(); i++) {
			data[i] = Integer.parseInt(count.get(i).toString());
			categories[i] = device.get(i).toString();
			temp = new JSONObject();
			temp.put("count", Integer.parseInt(count.get(i).toString()));
			temp.put("time", device.get(i));
			table.put(temp);
		}
		JSONObject seriesObj = new JSONObject();
		seriesObj.put("data", data);
		seriesObj.put("name", "count");
		series.put(seriesObj);
		JSONObject chartData = new JSONObject();
		chartData.put("series", series);
		chartData.put("categories", categories);
		result.put("tableData", table);
		result.put("chartData", chartData);
		return result;
	}
	
	public JSONObject getVideoMove(int videoId, int inter)
			throws SQLException, JSONException {
		System.out.println("Get Property");
		Properties prop=new Properties();
		FileInputStream propIn;
		try {
			propIn = new FileInputStream("app.properties");
			try {
				prop.load(propIn);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("error load properties");
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("error load file");
			e.printStackTrace();
		}
		String tableName;
		tableName=prop.getProperty("action.tableName");
		String sql = "select count(*),cast(start_time/"+inter+" as INT) s, cast(end_time/"+inter+" as INT) e from moveTable "+
				" where video_id='"+videoId+"' and end_time is not NULL and start_time is not NULL group by cast(end_time/"+inter+" as INT), cast(start_time/"+inter
				+" as INT) sort by e,s";
		System.out.println(sql);
		int cacheI=0;
		if (!hCache.find(sql)) {
			cacheI=hCache.put(sql);
			stmt.execute("drop table cache.tmp" + cacheI);
			stmt.execute("create table cache.tmp" + cacheI 
					+ " as "+sql);
		} else {
			cacheI = hCache.get(sql);
		}
//		ResultSet res = stmt.executeQuery(sql);
		ResultSet 	res = stmt.executeQuery("select * from cache.tmp" +cacheI);
		System.out.println("After executation");
		JSONObject result = new JSONObject();
		JSONArray series = new JSONArray();
		JSONObject tableData = new JSONObject();
		JSONArray table = new JSONArray();
		List count_tmp = new ArrayList();
		List start_tmp = new ArrayList();
		List end_tmp=new ArrayList();
		List count = new ArrayList();
		int max_start=0;
		int max_end=0;
		while (res.next()) {
			count_tmp.add(res.getString(1));
			start_tmp.add(res.getString(2));
			end_tmp.add(res.getString(3));
			if(max_start<Integer.parseInt(res.getString(2)))
				max_start=Integer.parseInt(res.getString(2));
			if(max_end<Integer.parseInt(res.getString(3)))
				max_end=Integer.parseInt(res.getString(3));
		}
	
		int current_end=0;
		int current_start=0;
		int[] end=new int[max_start+1];
		for(int i=0;i<count_tmp.size();i++) {
			while(current_start<=max_start&&current_end<Integer.parseInt(end_tmp.get(i).toString())) {
				end[current_start]=0;
				current_start++;
			}
			if(current_start>max_start) {
				current_start=0;
				count.add(end);
				end=new int[max_start+1];
				current_end++;
			}
			while(current_start==0&&(Integer.parseInt(end_tmp.get(i).toString())>current_end)) {
				for(int j=0;j<max_start;j++)
					end[j]=0;
				count.add(end);
				current_end++;
			}
			while((Integer.parseInt(start_tmp.get(i).toString())>current_start)) {
				end[current_start]=0;
				current_start++;
			}
			end[current_start]=Integer.parseInt(count_tmp.get(i).toString());
			current_start++;
		}
		
		JSONArray jarray=new JSONArray();
		JSONObject jobj=new JSONObject();
		JSONArray cat=new JSONArray();
		for(int i=0;i<count.size();i++) {
			jobj.put("name",i*inter);
			jobj.put("dSata", count.get(i));
			jarray.put(jobj);
			jobj=new JSONObject();
		}
		for(int i=0;i<max_start+1;i++) {
			cat.put(i*inter);
		}
		result.put("series", jarray);
		result.put("categories",cat);
		return result;
	}
	
	public JSONObject getHomeworkByName (String cname) throws SQLException, JSONException {
		String sql;
		sql="select count(*), homeworkId, relateVideo from homework_"+cname
				+" where not relateVideo=0 group by homeworkId, relateVideo";
		System.out.println(sql);
		int cacheI=0;
		if (!hCache.find(sql)) {
			cacheI=hCache.put(sql);
			stmt.execute("drop table cache.tmp" + cacheI);
			stmt.execute("create table cache.tmp" + cacheI 
					+ " as "+sql);
		} else {
			cacheI = hCache.get(sql);
		}
//		ResultSet res = stmt.executeQuery(sql);
		ResultSet 	homework_res = stmt.executeQuery("select * from cache.tmp" +cacheI);
		List video=new ArrayList();
		List homework_count=new ArrayList();
		List homework_id=new ArrayList();
		while(homework_res.next()) {
			homework_count.add(homework_res.getString(1));
			homework_id.add(homework_res.getString(2));
			video.add(homework_res.getString(3));
		}
		sql="select count, video_id from rankTable where video_id='"+video.get(0).toString()+"'";
		for(int i=1;i<video.size();i++) {
			sql=sql+" or video_id='"+video.get(i).toString()+"'";
		}
		cacheI=0;
		System.out.println(sql);
		if (!hCache.find(sql)) {
			cacheI=hCache.put(sql);
			stmt.execute("drop table cache.tmp" + cacheI);
			stmt.execute("create table cache.tmp" + cacheI 
					+ " as "+sql);
		} else {
			cacheI = hCache.get(sql);
		}
//		ResultSet res = stmt.executeQuery(sql);
		ResultSet 	video_res = stmt.executeQuery("select * from cache.tmp" +cacheI);
		Map video_rank=new HashMap();
		while(video_res.next()) {
			video_rank.put(video_res.getString(2),video_res.getString(1));
		}
		System.out.println(1);
		int[] hw=new int[video.size()];
		int[] vi=new int[video.size()];
		for(int i=0;i<video.size();i++) {
			hw[i]=Integer.parseInt(homework_count.get(i).toString());
			Object vtemp=video_rank.get(video.get(i));
			if(vtemp==null)
				vi[i]=0;
			else
				vi[i]=Integer.parseInt(vtemp.toString());
		}
		JSONObject result = new JSONObject();
		JSONArray series = new JSONArray();
		JSONObject jobj=new JSONObject();
		JSONArray cat=new JSONArray();
		jobj.put("name","homework");
		jobj.put("data", hw);
		series.put(jobj);
		jobj=new JSONObject();
		jobj.put("name","video");
		jobj.put("data", vi);
		series.put(jobj);
		for(int i=0;i<homework_id.size();i++) {
			cat.put(homework_id.get(i));
		}
		result.put("series", series);
		result.put("categories",cat);
		return result;
	}
	
	public JSONObject getVideoByHomework(String hid,int inter,String cname) throws SQLException, NumberFormatException, JSONException {
		JSONObject result=new JSONObject();
		String sql="select from_unixtime(unix_timestamp(deadline,'yyyy/MM/dd HH:mm'),'yyyy-MM-dd'),relateVideo from homeworkdetail where homeworkId='"+hid+"'";
		int cacheI=0;
		if (!hCache.find(sql)) {
			cacheI=hCache.put(sql);
			stmt.execute("drop table cache.tmp" + cacheI);
			stmt.execute("create table cache.tmp" + cacheI 
					+ " as "+sql);
		} else {
			cacheI = hCache.get(sql);
		}
//		ResultSet res = stmt.executeQuery(sql);
		ResultSet 	res = stmt.executeQuery("select * from cache.tmp" +cacheI);
		String deadline;
		String videoId;
		if(res.next()){
			deadline=res.getString(1);
			videoId=res.getString(2);
		}
		else
			return result;
		sql="select count(*),cast(datediff('"+deadline+"',from_unixtime(cast(time/1000 as int),'yyyy-MM-dd'))/"+inter+" as int) from videoInfo where video_id='"+videoId+"' and datediff('"+deadline+
				"',from_unixtime(cast(time/1000 as int),'yyyy-mm-dd'))<=0 group by cast( datediff('"+deadline+
				"',from_unixtime(cast(time/1000 as int),'yyyy-MM-dd'))/"+inter+" as int) sort by t desc";
		cacheI=0;
		System.out.println(sql);
		if (!hCache.find(sql)) {
			cacheI=hCache.put(sql);
			stmt.execute("drop table cache.tmp" + cacheI);
			stmt.execute("create table cache.tmp" + cacheI 
					+ " as "+sql);
		} else {
			cacheI = hCache.get(sql);
		}
//		ResultSet res = stmt.executeQuery(sql);
		res = stmt.executeQuery("select * from cache.tmp" +cacheI);
		JSONArray series = new JSONArray();
		JSONObject tableData = new JSONObject();
		JSONArray table = new JSONArray();
		List count = new ArrayList();
		List day = new ArrayList();
		while (res.next()) {
			count.add(res.getString(1));
			day.add(res.getString(2));
		}
		JSONObject temp;
		int[] data = new int[count.size()];
		int[] categories = new int[count.size()];
		for (int i = 0; i < count.size(); i++) {
			data[i] = Integer.parseInt(count.get(i).toString());
			categories[i] = Integer.parseInt(day.get(i).toString())*(-1)*inter;
			temp = new JSONObject();
			temp.put("count", Integer.parseInt(count.get(i).toString()));
			temp.put("day", Integer.parseInt(day.get(i).toString())*(-1)*inter);
			table.put(temp);
		}
		JSONObject seriesObj = new JSONObject();
		seriesObj.put("data", data);
		seriesObj.put("name", "count");
		series.put(seriesObj);
		JSONObject chartData = new JSONObject();
		chartData.put("series", series);
		chartData.put("categories", categories);
		result.put("tableData", table);
		result.put("chartData", chartData);
		sql="select count(*) from homework_"+cname+" where homeworkId='"+hid+"' group by homeworkId";
		res=stmt.executeQuery(sql);
		JSONObject homework=new JSONObject();
		if(res.next()) {
			homework.put("count", Integer.parseInt(res.getString(1)));
			homework.put("homeworkId", hid);
			homework.put("videoId", videoId);
			result.put("Content", homework);
		}
		return result;
	}
	
	public JSONObject getVideoInfo(int videoId)
			throws SQLException, JSONException {
		System.out.println("Get Property");
		Properties prop=new Properties();
		FileInputStream propIn;
		try {
			propIn = new FileInputStream("app.properties");
			try {
				prop.load(propIn);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("error load properties");
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("error load file");
			e.printStackTrace();
		}
		String tableName;
		tableName=prop.getProperty("action.tableName");
		String sql = "select * from videoinfo.video"+videoId;
		System.out.println(sql);
		int cacheI=0;
		if (!hCache.find(sql)) {
			cacheI=hCache.put(sql);
			stmt.execute("drop table cache.tmp" + cacheI);
			stmt.execute("create table cache.tmp" + cacheI 
					+ " as "+sql);
		} else {
			cacheI = hCache.get(sql);
		}
//		ResultSet res = stmt.executeQuery(sql);
		ResultSet 	res = stmt.executeQuery("select * from cache.tmp" +cacheI);
		System.out.println("After executation");
		JSONObject result = new JSONObject();
		JSONArray series = new JSONArray();
		List pause = new ArrayList();
		List forward = new ArrayList();
		List back=new ArrayList();
		while (res.next()) {
			pause.add(res.getString(2));
			forward.add(res.getString(3));
			back.add(res.getString(4));
		}
		int[] pause_res=new int[pause.size()];
		int[] forward_res=new int[pause.size()];
		int[] back_res=new int[pause.size()];
		for(int i=0;i<pause.size();i++) {
			pause_res[i]=Integer.parseInt(pause.get(i).toString());
			forward_res[i]=Integer.parseInt(forward.get(i).toString());
			back_res[i]=Integer.parseInt(back.get(i).toString());
		}
		JSONObject jobj=new JSONObject();
		JSONArray cat=new JSONArray();
		jobj.put("name","pause");
		jobj.put("data", pause_res);
		series.put(jobj);
		jobj=new JSONObject();
		jobj.put("name","forward");
		jobj.put("data", forward_res);
		series.put(jobj);
		jobj=new JSONObject();
		jobj.put("name","back");
		jobj.put("data", back_res);
		series.put(jobj);
		for(int i=0;i<pause.size();i++) {
			cat.put(i*inter_video);
		}
		result.put("series", series);
		result.put("categories",cat);
		return result;
	}
	
	
	
	
	

	public static void main(String[] args) throws IOException, JSONException {
		// TODO Auto-generated method stub
		IntelligenceTree testhost = new IntelligenceTree();

		try {
			testhost.connect();
//			testhost.importVideoData("/home/cd/20141024");
//			testhost.importHomeworkData("/home/cd/test.csv","sixiuke");
//			JSONObject result=testhost.getVideoRank(3);
//			result=testhost.getVideoRank(3);
			
			Long time_start=System.currentTimeMillis();
//			JSONObject result1=testhost.getVideoInfo(76830);
			JSONObject result1=testhost.getHomeworkByName("sixiuke");
//			JSONObject result1=testhost.getVideoByHomework("3900", 10, "sixiuke");
			Long time_end=System.currentTimeMillis();
			System.out.println(time_end-time_start);
////			JSONObject result2=testhost.getVideoPause(88257,300);
////			JSONObject result2=testhost.getUserVideoRank("159195370");
////			System.out.println(result);
////			System.out.println(result2);
//			time_start=System.currentTimeMillis();
//			result1=testhost.getVideoInfo(3);
//			time_end=System.currentTimeMillis();
//			System.out.println(time_end-time_start);
			System.out.println(result1);
			
			/*testhost.importVideoData(
					"/Users/fengyunping/Documents/workspace/intelligencetree/log",
					"videoInfo", "videoAction");*/
//			JSONObject result = testhost.getVideoRank("videoInfo", 1);
			//JSONObject result = testhost.getUserVideoRank("videoInfo", "88515");
//			System.out.println(result);
		} catch (SQLException e) { // TODO Auto-generated catch
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		/*
		 * String[] fields = { "count(remoteUser)", "host" }; String[] func = {
		 * "count", "none" }; String[] sort_fields = {}; String[] group = {
		 * "host" }; String[] types = { "number", "text" }; long start =getV
		 * System.currentTimeMillis(); try { testhost.connect(); JSONObject
		 * result = testhost.getAllColumns(); System.out.println(result); }
		 * catch (SQLException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (JSONException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch
		 * (ClassNotFoundException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

	}
}
