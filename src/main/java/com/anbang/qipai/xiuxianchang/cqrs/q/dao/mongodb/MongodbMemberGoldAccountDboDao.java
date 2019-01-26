package com.anbang.qipai.xiuxianchang.cqrs.q.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.anbang.qipai.xiuxianchang.cqrs.q.dao.MemberGoldAccountDboDao;
import com.anbang.qipai.xiuxianchang.cqrs.q.dbo.MemberGoldAccountDbo;

@Component
public class MongodbMemberGoldAccountDboDao implements MemberGoldAccountDboDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void save(MemberGoldAccountDbo accountDbo) {
		mongoTemplate.insert(accountDbo);
	}

	@Override
	public void update(String id, int balance) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		Update update = new Update();
		update.set("balance", balance);
		mongoTemplate.updateFirst(query, update, MemberGoldAccountDbo.class);
	}

	@Override
	public MemberGoldAccountDbo findByMemberId(String memberId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("memberId").is(memberId));
		return mongoTemplate.findOne(query, MemberGoldAccountDbo.class);
	}

}
