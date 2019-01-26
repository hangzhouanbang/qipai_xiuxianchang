package com.anbang.qipai.xiuxianchang.plan.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.anbang.qipai.xiuxianchang.plan.bean.LawsMutexGroup;
import com.anbang.qipai.xiuxianchang.plan.dao.LawsMutexGroupDao;

@Component
public class MongodbLawsMutexGroupDao implements LawsMutexGroupDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void save(LawsMutexGroup lawsMutexGroup) {
		mongoTemplate.insert(lawsMutexGroup);
	}

	@Override
	public void remove(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		mongoTemplate.remove(query, LawsMutexGroup.class);
	}

}
