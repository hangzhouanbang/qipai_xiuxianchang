package com.anbang.qipai.xiuxianchang.plan.dao.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.GameServer;
import com.anbang.qipai.xiuxianchang.plan.dao.GameServerDao;

@Component
public class MongodbGameServerDao implements GameServerDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void save(GameServer gameServer) {
		mongoTemplate.insert(gameServer);
	}

	@Override
	public void remove(String[] ids) {
		Object[] tempIds = ids;
		Query query = new Query(Criteria.where("id").in(tempIds));
		mongoTemplate.remove(query, GameServer.class);
	}

	@Override
	public List<GameServer> findByGame(Game game) {
		Query query = new Query(Criteria.where("game").is(game));
		return mongoTemplate.find(query, GameServer.class);
	}

}
