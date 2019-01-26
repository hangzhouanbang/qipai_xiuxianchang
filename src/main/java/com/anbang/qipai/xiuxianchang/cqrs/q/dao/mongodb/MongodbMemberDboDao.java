package com.anbang.qipai.xiuxianchang.cqrs.q.dao.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.anbang.qipai.xiuxianchang.cqrs.q.dao.MemberDboDao;
import com.anbang.qipai.xiuxianchang.cqrs.q.dbo.MemberDbo;

@Component
public class MongodbMemberDboDao implements MemberDboDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void insert(MemberDbo dbo) {
		mongoTemplate.insert(dbo);
	}

	@Override
	public MemberDbo findByMemberId(String memberId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(memberId));
		return mongoTemplate.findOne(query, MemberDbo.class);
	}

	@Override
	public List<MemberDbo> findAllMembers() {
		Query query = new Query();
		Update update1 = new Update();
		update1.set("_class", "com.anbang.qipai.xiuxianchang.plan.bean.games.GameLaw");
		mongoTemplate.updateMulti(query, update1, "gameLaw");

		Update update2 = new Update();
		update2.set("_class", "com.anbang.qipai.xiuxianchang.plan.bean.games.LawsMutexGroup");
		mongoTemplate.updateMulti(query, update2, "lawsMutexGroup");

		Update update3 = new Update();
		update3.set("_class", "com.anbang.qipai.xiuxianchang.plan.bean.games.GameServer");
		mongoTemplate.updateMulti(query, update3, "gameServer");

		Update update4 = new Update();
		update4.set("_class", "com.anbang.qipai.xiuxianchang.cqrs.q.dbo.MemberDbo");
		mongoTemplate.updateMulti(query, update4, "memberDbo");
		return mongoTemplate.find(query, MemberDbo.class);
	}

	@Override
	public void updateMemberBaseInfo(String memberId, String nickname, String headimgurl, String gender) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(memberId));
		Update update = new Update();
		update.set("nickname", nickname);
		update.set("headimgurl", headimgurl);
		update.set("gender", gender);
		mongoTemplate.updateFirst(query, update, MemberDbo.class);
	}

}
