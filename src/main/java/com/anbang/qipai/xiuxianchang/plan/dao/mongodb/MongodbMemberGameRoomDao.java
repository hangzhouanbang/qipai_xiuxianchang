package com.anbang.qipai.xiuxianchang.plan.dao.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.MemberGameRoom;
import com.anbang.qipai.xiuxianchang.plan.dao.MemberGameRoomDao;

@Component
public class MongodbMemberGameRoomDao implements MemberGameRoomDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void save(MemberGameRoom memberGameRoom) {
		mongoTemplate.insert(memberGameRoom);
	}

	@Override
	public void remove(Game game, String serverGameId, String memberId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("memberId").is(memberId));
		query.addCriteria(Criteria.where("gameRoom.game").is(game));
		query.addCriteria(Criteria.where("gameRoom.serverGame.gameId").is(serverGameId));
		mongoTemplate.remove(query, MemberGameRoom.class);
	}

	@Override
	public void remove(Game game, String serverGameId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("gameRoom.game").is(game));
		query.addCriteria(Criteria.where("gameRoom.serverGame.gameId").is(serverGameId));
		mongoTemplate.remove(query, MemberGameRoom.class);
	}

	@Override
	public MemberGameRoom findByGameAndMemberId(Game game, String gameId, String memberId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("memberId").is(memberId));
		query.addCriteria(Criteria.where("gameRoom.game").is(game));
		query.addCriteria(Criteria.where("gameRoom.serverGame.gameId").is(gameId));
		return mongoTemplate.findOne(query, MemberGameRoom.class);
	}

	@Override
	public List<MemberGameRoom> findByGame(Game game, String gameId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("gameRoom.game").is(game));
		query.addCriteria(Criteria.where("gameRoom.serverGame.gameId").is(gameId));
		return mongoTemplate.find(query, MemberGameRoom.class);
	}

	@Override
	public List<MemberGameRoom> find(int page, int size) {
		Query query = new Query();
		query.skip((page - 1) * size);
		query.limit(size);
		return mongoTemplate.find(query, MemberGameRoom.class);
	}

}
