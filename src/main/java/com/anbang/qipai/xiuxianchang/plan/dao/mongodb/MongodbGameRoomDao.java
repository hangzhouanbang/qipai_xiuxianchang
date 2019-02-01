package com.anbang.qipai.xiuxianchang.plan.dao.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.GameRoom;
import com.anbang.qipai.xiuxianchang.plan.dao.GameRoomDao;

@Component
public class MongodbGameRoomDao implements GameRoomDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void save(GameRoom gameRoom) {
		mongoTemplate.insert(gameRoom);
	}

	@Override
	public void updateGameRoomFinished(List<String> ids, boolean finished) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").in(ids));
		Update update = new Update();
		update.set("finished", finished);
		mongoTemplate.updateMulti(query, update, GameRoom.class);
	}

	@Override
	public void updateGameRoomFinishedByGame(Game game, String serverGameId, boolean finished) {
		Query query = new Query();
		query.addCriteria(Criteria.where("game").is(game));
		query.addCriteria(Criteria.where("serverGame.gameId").is(serverGameId));
		Update update = new Update();
		update.set("finished", finished);
		mongoTemplate.updateFirst(query, update, GameRoom.class);
	}

	@Override
	public void updateGameRoomPlayersCountAndFull(String id, int playersCount, boolean full) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		Update update = new Update();
		update.set("playersCount", playersCount);
		update.set("full", full);
		mongoTemplate.updateFirst(query, update, GameRoom.class);
	}

	@Override
	public List<GameRoom> findNotFullGameRoom(Game game) {
		Query query = new Query();
		query.addCriteria(Criteria.where("game").is(game));
		query.addCriteria(Criteria.where("full").is(false));
		query.addCriteria(Criteria.where("finished").is(false));
		return mongoTemplate.find(query, GameRoom.class);
	}

	@Override
	public List<GameRoom> findExpireGameRoom(long deadlineTime, boolean finished) {
		Query query = new Query();
		query.addCriteria(Criteria.where("finished").is(finished));
		query.addCriteria(Criteria.where("deadlineTime").lt(deadlineTime));
		return mongoTemplate.find(query, GameRoom.class);
	}

	@Override
	public GameRoom findGameRoomByGame(Game game, String gameId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("game").is(game));
		query.addCriteria(Criteria.where("serverGame.gameId").is(gameId));
		return mongoTemplate.findOne(query, GameRoom.class);
	}

	@Override
	public void updateGameRoomServerGameId(String id, String serverGameId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		Update update = new Update();
		update.set("serverGame.gameId", serverGameId);
		mongoTemplate.updateFirst(query, update, GameRoom.class);
	}

}
