package com.anbang.qipai.xiuxianchang.cqrs.q.dao.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.anbang.qipai.xiuxianchang.cqrs.q.dao.MemberGoldRecordDboDao;
import com.anbang.qipai.xiuxianchang.cqrs.q.dbo.MemberGoldRecordDbo;

@Component
public class MongodbMemberGoldRecordDboDao implements MemberGoldRecordDboDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void save(MemberGoldRecordDbo dbo) {
		mongoTemplate.insert(dbo);
	}

	@Override
	public long getCountByMemberId(String memberId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("memberId").is(memberId));
		return mongoTemplate.count(query, MemberGoldRecordDbo.class);
	}

	@Override
	public List<MemberGoldRecordDbo> findMemberGoldRecordByMemberId(String memberId, int page, int size) {
		Query query = new Query();
		query.addCriteria(Criteria.where("memberId").is(memberId));
		query.skip((page - 1) * size);
		query.limit(size);
		query.with(new Sort(new Order(Direction.DESC, "accountingTime")));
		return mongoTemplate.find(query, MemberGoldRecordDbo.class);
	}

	@Override
	public long countByMemberIdAndSummaryAndTime(String memberId, String summary, long startTime, long endTime) {
		Query query = new Query();
		query.addCriteria(Criteria.where("memberId").is(memberId));
		query.addCriteria(Criteria.where("summary.text").is(summary));
		query.addCriteria(Criteria.where("accountingTime").gt(endTime).lt(startTime));
		return mongoTemplate.count(query, MemberGoldRecordDbo.class);
	}

}
