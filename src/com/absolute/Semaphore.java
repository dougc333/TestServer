package com.absolute;

import redis.clients.jedis.Jedis;



public class Semaphore {

	public void setGroupID(Jedis jedis, String groupID){
		jedis.set("groupID", "555");
		jedis.set("groupID1","61");
	}
}
