package com.myproject.analysis.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HiveJdbcDriver {

	private String className;
	private String connection;
	private String tableName;
	private ResultSet res;
	private Connection conn;
	protected Statement stmt;
	private Map<String, Integer> cache;
	private Map<Integer, String> cache_index;
	private int current_index;
	protected HiveCache hCache;

	public HiveJdbcDriver() {
		this.className = "org.apache.hadoop.hive.jdbc.HiveDriver";
		this.connection = "jdbc:hive://localhost:5001/default";
		this.tableName = "logtesttable";
		cache = new HashMap<String, Integer>();
		cache_index = new HashMap<Integer, String>();
		current_index = 0;
	}

	public void connect() throws ClassNotFoundException, SQLException {
		Class.forName(this.className);
		conn = DriverManager.getConnection(
				"jdbc:hive://localhost:5001/default", "", "");
		stmt = conn.createStatement();
		hCache=new HiveCache();
//		String hql = "add jar /Users/fengyunping/Documents/workspace/Jars/dissertion/hiveudf.jar";
//		stmt.execute(hql);
//		hql = "create temporary function getBrowser as 'hiveudf.UserAgentBrowser'";
//		stmt.execute(hql);
//		hql = "create temporary function getOS as 'hiveudf.UserAgentOS'";
//		stmt.execute(hql);
//		hql = "create temporary function getDevice as 'hiveudf.UserAgentDevice'";
//		stmt.execute(hql);
//		hql = "add jar /Users/fengyunping/Documents/workspace/Jars/dissertion/UserAgentUtils-1.6.jar";
//		stmt.execute(hql);
//		hql = "create temporary function getDate as 'hiveudf.getDate'";
//		stmt.execute(hql);
//		hql = "create temporary function getIp as 'hiveudf.ipSplit'";
//		stmt.execute(hql);
//		hql = "create temporary function getRequest as 'hiveudf.getRequest'";
//		stmt.execute(hql);
//		hql = "create database IF NOT EXISTS cache";
//		stmt.execute(hql);
//		hql = "add jar /Users/fengyunping/Downloads/hadoop/hive/hive-0.11.0/lib/hive-contrib-0.11.0.jar";
//		stmt.execute(hql);
		String hql = "create database IF NOT EXISTS cache";
		stmt.execute(hql);
	}

	public JSONObject getAllColumns() throws SQLException, JSONException {
		JSONObject result = new JSONObject();
		JSONArray arrayC = new JSONArray();
		String[] val = new String[9];
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("name", "host");
		jsonObj.put("ctype", "text");
		val[0] = "host";
		arrayC.put(jsonObj);
		jsonObj = new JSONObject();
		jsonObj.put("name", "time");
		jsonObj.put("ctype", "date");
		val[1] = "time";
		arrayC.put(jsonObj);
		jsonObj = new JSONObject();
		jsonObj.put("name", "request");
		jsonObj.put("ctype", "text");
		// jsonObj.put("request", "text");
		val[2] = "request";
		arrayC.put(jsonObj);
		jsonObj = new JSONObject();
		jsonObj.put("name", "status");
		jsonObj.put("ctype", "text");
		// jsonObj.put("status", "text");
		val[3] = "status";
		arrayC.put(jsonObj);
		jsonObj = new JSONObject();
		jsonObj.put("name", "size");
		jsonObj.put("ctype", "number");
		// jsonObj.put("size", "number");
		val[4] = "size";
		arrayC.put(jsonObj);
		jsonObj = new JSONObject();
		jsonObj.put("name", "browser");
		jsonObj.put("ctype", "text");
		// jsonObj.put("browser", "text");
		val[5] = "browser";
		arrayC.put(jsonObj);
		jsonObj = new JSONObject();
		jsonObj.put("name", "os");
		jsonObj.put("ctype", "text");
		// jsonObj.put("os", "text");
		val[6] = "os";
		arrayC.put(jsonObj);
		jsonObj = new JSONObject();
		jsonObj.put("name", "device");
		jsonObj.put("ctype", "text");
		// jsonObj.put("device", "text");
		val[7] = "device";
		arrayC.put(jsonObj);
		jsonObj = new JSONObject();
		jsonObj.put("name", "domain");
		jsonObj.put("ctype", "text");
		// jsonObj.put("domain", "text");
		val[8] = "domain";
		arrayC.put(jsonObj);
		result.put("columns", val);
		result.put("types", arrayC);
		return result;
	}

	private String[] parseString(String[] input) {
		String[] result = new String[input.length];
		for (int i = 0; i < input.length; i++) {
			// if(input[i].equals("request"))
			// result[i]="getRequest(request)";
			// else if(input[i].equals("browser"))
			// result[i]="getBrowser(referer)";
			// else if(input[i].equals("os"))
			// result[i]="getOS(referer)";
			// else if(input[i].equals("device"))
			// result[i]="getDevice(referer)";
			// else
			if (input[i].equals("domain"))
				result[i] = "parse_url(agent,'HOST')";
			else
				result[i] = input[i];
		}

		return result;
	}

	public JSONObject getSort(String[] fields, String[] sort_fields, int start,
			int end) throws SQLException, JSONException {
		JSONObject result = new JSONObject();
		String sql = "select ";
		String select = new String();
		String[] temp = parseString(fields);
		for (int i = 0; i < fields.length; i++) {
			select = select + temp[i] + " " + fields[i] + ", ";
		}
		sql = sql + select
				+ "(row_number() over () )r from logtesttable sort by ";
		for (int i = 0; i < sort_fields.length - 1; i++) {
			sql = sql + sort_fields[i] + ", ";
		}
		sql = sql + sort_fields[sort_fields.length - 1];
		select = "";
		for (int i = 0; i < fields.length - 1; i++)
			select = select + "tmp." + fields[i] + ", ";
		sql = "select " + select + " tmp." + fields[fields.length - 1]
				+ " from (" + sql + ") tmp where tmp.r>" + start
				+ " and tmp.r<" + end;
		ResultSet res = stmt.executeQuery(sql);
		int index = 0;
		JSONArray array = new JSONArray();
		while (res.next()) {
			JSONObject jsonObj = new JSONObject();
			for (int i = 1; i < fields.length + 1; i++) {
				jsonObj.put(fields[i - 1], res.getString(i));
			}
			array.put(jsonObj);

		}
		result.put("result", array);

		return result;

	}

	private String getDate(String date) {
		String result = new String();
		if (date.equals("year")) {
			result = "getDate(time,1)";
		} else if (date.equals("month")) {
			result = "getDate(time,2)";
		} else if (date.equals("day")) {
			result = "getDate(time,3)";
		} else if (date.equals("hour")) {
			result = "getDate(time,4)";
		} else if (date.equals("minute")) {
			result = "getDate(time,5)";
		} else if (date.equals("second")) {
			result = "getDate(time,6)";
		}
		return result;
	}

	private String realFields(String fields, int ip, String date) {
		String result = new String();
		if (fields.equals("domain"))
			result = "parse_url(agent,'HOST')";
		else if (fields.equals("time") && date != "none") {
			result = getDate(date);
		} else if (fields.equals("host") && ip != 0) {
			result = "getIp(host," + ip + ")";
		} else
			result = fields;
		return result;
	}

	private String[] parseRealFields(String[] fields, String[] func, int ip,
			String date) {
		String[] result = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			if (func[i].equals("none")) {
				result[i] = realFields(fields[i], ip, date);
			} else {
				result[i] = fields[i].replace(func[i], "");
				result[i] = result[i].replace("(", "");
				result[i] = result[i].replace(")", "");
				result[i] = result[i].trim();
				result[i] = realFields(result[i], ip, date);
			}
		}
		for (int i = 0; i < func.length; i++) {
			if (!func[i].equals("none")) {
				result[i] = func[i] + "(" + result[i] + ")";
			}
		}

		return result;
	}

	private String[] getReferer(String[] fields) {
		String[] result = new String[fields.length];

		for (int i = 0; i < fields.length; i++) {
			result[i] = fields[i].replace("(", "");
			result[i] = result[i].replace(")", "");
			result[i] = result[i].trim();
			result[i] = "r" + result[i];
		}

		return result;
	}

	private String getR(String input) {
		String result = "";
		result = input.replace("(", "");
		result = result.replace(")", "");
		result = result.trim();
		return result;
	}

	public JSONObject getData(String[] fields, String[] func, String[] group,
			String[] sort, int start, int end, int ip, String date,
			String chartType, String[] types) throws SQLException,
			JSONException {
		JSONObject result = new JSONObject();
		if (start > end)
			return result;
		String[] realFields = new String[fields.length];
		String[] refer = new String[fields.length];
		int[] groupby = new int[group.length];
		int[] sortby = new int[sort.length];
		for (int i = 0; i < fields.length; i++) {
			for (int j = 0; j < group.length; j++) {
				if (group[j].equals(fields[i])) {
					groupby[j] = i;
					continue;
				}
			}
		}
		for (int i = 0; i < fields.length; i++) {
			for (int j = 0; j < sort.length; j++) {
				if (sort[j].equals(fields[i])) {
					sortby[j] = i;
					continue;
				}
			}
		}
		String[] groupContent = new String[fields.length];
		String[] groupRefer = new String[fields.length];

		realFields = parseRealFields(fields, func, ip, date);
		refer = getReferer(fields);

		for (int i = 0; i < fields.length; i++) {
			int index = i;
			if (!func[i].equals("none")) {
				groupContent[index] = fields[index].replace(func[index], "");
				groupContent[index] = groupContent[index].replace("(", "");
				groupContent[index] = groupContent[index].replace(")", "");
				groupContent[index] = groupContent[index].trim();
				groupRefer[index] = "r" + groupContent[index];
				groupContent[index] = realFields(groupContent[index], ip, date);
				realFields[i] = func[i] + "(tmp." + groupRefer[i] + ")";
			}
		}

		String select = "(select ";
		for (int i = 0; i < realFields.length - 1; i++) {
			if (func[i].equals("none"))
				select += realFields[i] + " " + refer[i] + ", ";
		}
		for (int i = 0; i < groupContent.length; i++) {
			if (!func[i].equals("none"))
				select += groupContent[i] + " " + groupRefer[i] + ", ";
		}
		if (func[fields.length - 1].equals("none")) {
			select += realFields[fields.length - 1] + " "
					+ refer[fields.length - 1] + " from log) tmp ";
		} else {
			select += groupContent[fields.length - 1] + " "
					+ groupRefer[fields.length - 1] + " from log) tmp ";
		}
		String sql = "select ";
		for (int i = 0; i < realFields.length - 1; i++) {
			if (func[i].equals("none"))
				sql += "tmp." + refer[i] + ", ";
			else
				sql += realFields[i] + " " + refer[i] + ", ";
		}
		if (func[fields.length - 1].equals("none"))
			sql += "tmp." + refer[fields.length - 1] + " ";
		else
			sql += realFields[fields.length - 1] + " "
					+ refer[fields.length - 1] + " ";
		sql = sql + "from " + select;
		sql = sql + " where ";
		for (int i = 0; i < realFields.length - 1; i++) {
			if (!func[i].equals("none"))
				continue;
			sql += "tmp." + refer[i] + " is not NULL and ";
		}
		sql += "tmp." + refer[refer.length - 1] + " is not NULL ";
		if (group.length > 0) {
			sql = sql + " group by ";
			for (int i = 0; i < group.length - 1; i++) {
				sql = sql + "tmp.r" + getR(group[i]) + ", ";
			}
			sql = sql + "tmp.r" + getR(group[group.length - 1]);
		}
		if (sort.length > 0) {
			sql = sql + " sort by ";
			for (int i = 0; i < sort.length - 1; i++) {
				sql = sql + "r" + getR(sort[i]) + ", ";
			}
			sql = sql + "r" + getR(sort[sort.length - 1]) + " ";
		}
		ResultSet res;
		end++;
		System.out.println("SQL: " + sql);
		if (!cache.containsKey(sql)) {
			stmt.execute("drop table temp");
			stmt.execute("create table temp as " + sql);
			cache.put(sql, current_index);
			current_index++;
			if (current_index >= 50)
				current_index -= 50;
			stmt.execute("drop table cache.tmp" + (current_index - 1));
			stmt.execute("create table cache.tmp" + (current_index - 1)
					+ " as select * from temp");
			sql = "select * from temp " + "limit " + end;
			res = stmt.executeQuery(sql);
		} else {
			int cacheI = cache.get(sql);
			res = stmt.executeQuery("select * from cache.tmp" + cacheI
					+ " limit " + end);
		}

		int index = 0;
		if (chartType.equals("")) {
			JSONArray array = new JSONArray();
			while (res.next()) {
				boolean isNull = false;
				if (index >= start && index <= end) {
					JSONObject jsonObj = new JSONObject();
					for (int i = 1; i < fields.length + 1; i++) {
						if (types[i - 1].equals("number"))
							jsonObj.put(fields[i - 1],
									Integer.parseInt(res.getString(i)));
						else {
							String tt = res.getString(i);
							if (tt == null)
								isNull = true;
							jsonObj.put(fields[i - 1], res.getString(i));
						}
					}
					if (isNull)
						continue;
					array.put(jsonObj);
				}
				index++;
				if (index > end)
					break;
			}

			result.put("result", array);
		}

		if (chartType.equals("BasicLine") || chartType.equals("BasicColumn")) {
			index = 0;
			JSONArray table = new JSONArray();
			JSONObject chart = new JSONObject();
			JSONArray series = new JSONArray();
			String[][] val = new String[fields.length][];
			for (int i = 0; i < fields.length; i++) {
				val[i] = new String[end - start];
			}
			while (res.next()) {
				if (index >= start && index <= end) {
					JSONObject jsonObj = new JSONObject();
					for (int i = 1; i < fields.length + 1; i++) {
						if (types[i - 1].equals("number"))
							jsonObj.put(fields[i - 1],
									Integer.parseInt(res.getString(i)));
						else
							jsonObj.put(fields[i - 1], res.getString(i));
						val[i - 1][index - start] = res.getString(i);
					}
					table.put(jsonObj);
				}
				if (index > end) {
					break;
				}
				index++;
			}
			JSONObject seriesObj;
			index--;
			boolean find = false;
			for (int i = 0; i < fields.length; i++) {
				seriesObj = new JSONObject();
				for (int j = 0; j < groupby.length; j++) {
					int t = groupby[j];
					if (t == i) {
						find = true;
						break;
					}
				}
				if (find)
					continue;
				if (types[i].equals( "number")) {
					System.out.println(fields[i]);
					int[] cont = new int[index+1];//changed

					for (int l = 0; l < index-start; l++) {//changed
						System.out.println(val[i][l]);
						cont[l] = Integer.parseInt(val[i][l]);
					}

					seriesObj.put("name", fields[i]);
					seriesObj.put("data", cont);
					series.put(seriesObj);
				} else {
					String[] cont = new String[index];

					for (int l = 0; l < index; l++)
						cont[l] = val[i][l];
					seriesObj.put("name", fields[i]);
					seriesObj.put("data", cont);
					series.put(seriesObj);
				}
			}
			String[] categories = new String[index +1];//changed
			for (int i = 0; i < index+1; i++) {//changed
				categories[i] = "";
				for (int j = 0; j < groupby.length - 1; j++)
					categories[i] += val[groupby[j]][i] + " ";
				if (groupby.length > 0)
					categories[i] += val[groupby[groupby.length - 1]][i];
			}
			chart.put("categories", categories);
			chart.put("series", series);

			result.put("tableData", table);
			result.put("chartData", chart);
		}

		return result;
	}

	public JSONObject getDataTemp(String[] fields, String[] func,
			String[] group, String[] sort, int start, int end, int ip,
			String date, String chartType, String[] types) throws SQLException, JSONException {
		JSONObject result = new JSONObject();
		if (start > end)
			return result;
		String[] realFields = new String[fields.length];
		String[] refer = new String[fields.length];
		int[] groupby = new int[group.length];
		int[] sortby = new int[sort.length];
		for (int i = 0; i < fields.length; i++) {
			for (int j = 0; j < group.length; j++) {
				if (group[j].equals(fields[i])) {
					groupby[j] = i;
					continue;
				}
			}
		}
		for (int i = 0; i < fields.length; i++) {
			for (int j = 0; j < sort.length; j++) {
				if (sort[j].equals(fields[i])) {
					sortby[j] = i;
					continue;
				}
			}
		}
		String[] groupContent = new String[fields.length];
		String[] groupRefer = new String[fields.length];

		for (int i = 0; i < fields.length; i++) {
			if (func[i].equals("none"))
				realFields[i] = fields[i];
			else {
				realFields[i] = fields[i].replaceFirst(func[i], "");
			}
			realFields[i] = realFields[i].trim();
			realFields[i] = "r" + getR(realFields[i]);
		}

		String sql = "select ";
		for (int i = 0; i < fields.length - 1; i++) {
			if (func[i].equals("none"))
				sql = sql + realFields[i] + ", ";
			else
				sql = sql + func[i] + "(" + realFields[i] + ") "+"r" + getR(fields[i])+", ";
		}
		if (func[fields.length - 1].equals("none"))
			sql = sql + realFields[fields.length - 1] + " ";
		else
			sql = sql + func[fields.length - 1] + "("
					+ realFields[fields.length - 1] + ") "+"r" + getR(fields[fields.length-1])+" ";
		sql = sql + "from temp";
		if (group.length > 0) {
			sql = sql + " group by ";
			for (int i = 0; i < group.length - 1; i++) {
				sql = sql + "r" + getR(group[i]) + ", ";
			}
			sql = sql + "r" + getR(group[group.length - 1]);
		}
		if (sort.length > 0) {
			sql = sql + " sort by ";
			for (int i = 0; i < sort.length - 1; i++) {
				sql = sql + "r" + getR(sort[i]) + ", ";
			}
			sql = sql + "r" + getR(sort[sort.length - 1]) + " ";
		}

		
		sql = sql + " " + "limit " + end;
		System.out.println(sql);
		res = stmt.executeQuery(sql);
		
		
		int index = 0;
		if (chartType.equals("")) {
			JSONArray array = new JSONArray();
			while (res.next()) {
				boolean isNull=false;
				if (index >= start && index <= end) {
					JSONObject jsonObj = new JSONObject();
					for (int i = 1; i < fields.length + 1; i++) {
						if (types[i - 1].equals("number"))
							jsonObj.put(fields[i - 1],
									Integer.parseInt(res.getString(i)));
						else {
							String tt=res.getString(i);
							if(tt == null)
								isNull=true;
							jsonObj.put(fields[i - 1], res.getString(i));
						}
					}
					if(isNull)
						continue;
					array.put(jsonObj);
				}
				index++;
				if(index>end)
					break;
			}
			result.put("result", array);
		}

		if (chartType.equals("BasicLine") || chartType.equals("BasicColumn")) {
			index = 0;
			JSONArray table = new JSONArray();
			JSONObject chart = new JSONObject();
			JSONArray series = new JSONArray();
			String[][] val = new String[fields.length][];
			for (int i = 0; i < fields.length; i++) {
				val[i] = new String[end-start];
			}
			while (res.next()) {
				if (index >= start && index <= end) {
					JSONObject jsonObj = new JSONObject();
					for (int i = 1; i < fields.length + 1; i++) {
						if (types[i - 1].equals("number"))
							jsonObj.put(fields[i - 1],
									Integer.parseInt(res.getString(i)));
						else
							jsonObj.put(fields[i - 1], res.getString(i));
						val[i - 1][index - start] = res.getString(i);
					}
					table.put(jsonObj);
				}
				index++;
				if (index >= end) {
					break;
				}
			}
			JSONObject seriesObj;
			index--;
			boolean find = false;
			for (int i = 0; i < fields.length; i++) {
				seriesObj = new JSONObject();
				for (int j = 0; j < groupby.length; j++) {
					int t = groupby[j];
					if (t == i) {
						find = true;
						break;
					}
				}
				if (find)
					continue;
				if (types[i].equals("number")) {
					int[] cont = new int[index+1];//changed

					for (int l = 0; l < index+1; l++)//changed
						cont[l] = Integer.parseInt(val[i][l]);
					seriesObj.put("name", fields[i]);
					seriesObj.put("data", cont);
					series.put(seriesObj);
				} else {
					String[] cont = new String[index];

					for (int l = 0; l < index; l++)
						cont[l] = val[i][l];
					seriesObj.put("name", fields[i]);
					seriesObj.put("data", cont);
					series.put(seriesObj);
				}
			}
			String[] categories = new String[index +1];//changed
			for (int i = 0; i < index+1; i++) {//changed
				categories[i] = "";
				for (int j = 0; j < groupby.length - 1; j++)
					categories[i] += val[groupby[j]][i] + " ";
				if (groupby.length > 0)
				categories[i] += val[groupby[groupby.length - 1]][i];
			}
			chart.put("categories", categories);
			chart.put("series", series);

			result.put("tableData", table);
			result.put("chartData", chart);
		}

		return result;
	}

	public void cleanTrashWeb() throws SQLException {
		String sql = "drop table trash";
		stmt.execute(sql);
		sql = "create table trash as select tmp2.url from (select (row_number() over () +1) r , parse_url(agent,'HOST') url, host,parse_url(request'HOST') req  from log  sort by host ) tmp1 left join (select (row_number() over () ) r, parse_url(agent,'HOST') url, host from log sort by host) tmp2 on tmp1.r=tmp2.r and tmp1.host=tmp2.host where tmp2.host is not NULL and tmp2.url is not NULL";
		stmt.execute(sql);
		sql = "create table trashWebTemp as select log.* from log left outer join trash on parse_url(log.agent,'HOST') = trash.url where trash.url is NULL";
		stmt.execute(sql);
		sql = "drop table log";
		stmt.execute(sql);
		sql = "create table log as select * from trashWebTemp";
		stmt.execute(sql);
		sql = "drop table trashWebTemp";
		stmt.execute(sql);
	}

	public void initialTable() throws SQLException {
		String sql;
		sql = "drop table logtesttable";
		stmt.execute(sql);
		sql = "create table logtesttable ( host STRING,"
				+ "identity STRING,"
				+ "user STRING,"
				+ "time STRING,"
				+ "request STRING,"
				+ "status STRING,"
				+ "size STRING,"
				+ "cookie STRING,"
				+ "referer STRING,"
				+ "agent STRING)\n"
				+ "ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.RegexSerDe'\n"
				+ "WITH SERDEPROPERTIES (\n"
				+ "\"input.regex\"=\"([^ ]*) ([^ ]*) ([^ ]*) (?:-|\\\\[([^\\\\]]*)\\\\]) ([^ \\\"]*|\\\"[^\\\"]*\\\") (-|[0-9]*) (-|[0-9]*) ([^ \\\"]*|\\\"[^\\\"]*\\\") ((?:[^\\\"]|\\\\\\\")+)\\\"\\\\s\\\"((?:[^\\\"]|\\\\\\\")+)\\\"$\",\n"
				+ "\"output.format.string\" = \"%1$s %2$s %3$s %4$s %5$s %6$s %7$s %8$s %9$s %10$s\""
				+ ") ";
		stmt.executeQuery(sql);
		sql = "drop table log";
		stmt.execute(sql);
		sql = "create table log (host STRING, time STRING, request STRING,status STRING,size STRING,browser STRING,os STRING,device STRING,agent STRING) ";
		stmt.execute(sql);
	}

	public void loadFile(String path) throws SQLException {
		String sql;
		sql = "drop table logtesttemp";
		stmt.execute(sql);
		sql = "create table logtesttemp ( host STRING,"
				+ "identity STRING,"
				+ "user STRING,"
				+ "time STRING,"
				+ "request STRING,"
				+ "status STRING,"
				+ "size STRING,"
				+ "cookie STRING,"
				+ "referer STRING,"
				+ "agent STRING)\n"
				+ "ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.RegexSerDe'\n"
				+ "WITH SERDEPROPERTIES (\n"
				+ "\"input.regex\"=\"([^ ]*) ([^ ]*) ([^ ]*) (?:-|\\\\[([^\\\\]]*)\\\\]) ([^ \\\"]*|\\\"[^\\\"]*\\\") (-|[0-9]*) (-|[0-9]*) ([^ \\\"]*|\\\"[^\\\"]*\\\") ((?:[^\\\"]|\\\\\\\")+)\\\"\\\\s\\\"((?:[^\\\"]|\\\\\\\")+)\\\"$\",\n"
				+ "\"output.format.string\" = \"%1$s %2$s %3$s %4$s %5$s %6$s %7$s %8$s %9$s %10$s\""
				+ ") ";
		stmt.executeQuery(sql);
		sql = "load data local inpath '" + path + "' into table logtesttemp";
		stmt.execute(sql);
		sql = "insert overwrite table log select host,time,getRequest(request) request, status,size,getBrowser(referer) browser,getOS(referer) os, "
				+ "getDevice(referer) device,agent from logtesttemp";
		stmt.execute(sql);
		sql = "insert overwrite table logtesttable select * from logtesttemp";
		stmt.execute(sql);
	}
	
	

	public static void main(String[] args) throws Exception {

		HiveJdbcDriver driver = new HiveJdbcDriver();
		driver.connect();
		// driver.initialTable();
		// driver.loadFile("file:///home/cd/Downloads/access.log.stat.www.baidu.com.word.txt");
		String[] fields = { "count(host)", "os" };
		String[] func = { "count", "none" };
		String[] sort_fields = { "os" };
		String[] group = { "os" };
		String[] types = { "number", "text" };
		JSONObject result = driver.getData(fields, func, group, sort_fields, 0,
				1000, 0, "none", "BasicColumn", types);

		String[] fields2 = { "max(count(host))"};
		String[] func2 = {"max"};
		String[] sort_fields2 = {"max(count(host))"};
		String[] group2 = {};
		String[] types2 = { "number"};
		JSONObject result2 = driver.getDataTemp(fields2, func2, group2,
				sort_fields2, 0, 1, 0, "none", "",types2);

		
		//System.out.println(result.toString());
		System.out.println(result.toString());
		System.out.println(result2.toString());
		// System.out.println(URLEncoder.encode("中国","utf-8"));//转码
		// System.out.println(URLDecoder.decode("4D%E8%A7%A6%E6%84%9F%E8%A7%86%E9%A2%91","utf-8"));

	}

}
