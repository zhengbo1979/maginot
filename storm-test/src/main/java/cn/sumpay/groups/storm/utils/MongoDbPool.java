package cn.sumpay.groups.storm.utils;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoDbPool {
	private static final String server_ip = "192.168.8.9";
	private static final Integer server_port = 27017;
	private static final String db_name = "storm"; // db名
	private static final String collection_name = "storm_test"; // collection名
	private static final MongoClient mongoClient;
	
	static {
		// 连接到 mongodb 服务
		mongoClient = new MongoClient(server_ip, server_port);
	}

	public final static MongoDatabase getDB() {
		return getDB(db_name);
	}
	
	public final static MongoDatabase getDB(String dbName) {
		return mongoClient.getDatabase(dbName);
	}

	public final static MongoCollection<Document> getCollection() {
		return getCollection(db_name,collection_name);
	}
	
	public final static MongoCollection<Document> getCollection(String dbName,
			String collectionName) {
		MongoDatabase db = mongoClient.getDatabase(dbName);
		return db.getCollection(collectionName);
	}

	public static void main(String args[]) {
		try {
			// 连接到数据库
			MongoCollection<Document> collection = getCollection();
			System.out.println("Connect to database successfully");

			// 单个插入
			Document doc = new Document("name", "MongoDB").append("type",
					"database");
			collection.insertOne(doc);
			// 可以把JSON 字符串直接解析成Document
			doc = Document.parse("{'name':'Java', 'type':'language'}");
			collection.insertOne(doc);

			// 插入多个文档
			List<Document> documents = new ArrayList<Document>();
			for (int i = 0; i < 10; i++) {
				documents.add(new Document("i", i));
			}
			collection.insertMany(documents);
			
			MongoCursor<Document> cursor = collection.find().iterator();

			while (cursor.hasNext()) {
		        System.out.println(cursor.next().toJson());
		    }
			
			mongoClient.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}