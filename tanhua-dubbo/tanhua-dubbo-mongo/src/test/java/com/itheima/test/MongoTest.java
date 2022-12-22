package com.itheima.test;


import com.itheima.test.domain.Person;
import com.tanhua.mongo.MongoApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MongoApplication.class)
public class MongoTest {

    @Resource
    private MongoTemplate mongoTemplate;

    @Test
    public void testInsert() {
        // 插入一条数据
        Person person = new Person();
        person.setAddress("上海");
        person.setAge(23);
        person.setName("Robert");
        this.mongoTemplate.save(person);
    }

    @Test
    public void testFindAll() {
        // 查询所有数据
        List<Person> all = this.mongoTemplate.findAll(Person.class);
        all.forEach(System.out::println);
    }

    @Test
    public void testFindByCondition() {
        // 根据条件查询
        Criteria criteria = Criteria.where("age")
                .is(23).and("myname").is("Robert");
        Query query = new Query(criteria);
        List<Person> people = this.mongoTemplate.find(query, Person.class);
        people.forEach(System.out::println);
    }

    @Test
    public void testFindPage() {
        int page = 2;
        int size = 5;
        // 条件分页
        Criteria criteria = Criteria.where("age").lt(55);
        Query query = new Query(criteria);
        query.skip((page - 1) * size).limit(size)
                .with(Sort.by(Sort.Order.desc("age")));
        List<Person> people = this.mongoTemplate.find(query, Person.class);
        people.forEach(System.out::println);
    }

    @Test
    public void testUpdate() {
        Query query = new Query(Criteria.where("id").is("63a431e998e6c17444113fc9"));
        // updateFirst是更新满足条件的第一个记录
        // 设置要更新的内容
        Update update = new Update();
        update.set("myname", "Jack");
        this.mongoTemplate.updateFirst(query, update, Person.class);
    }

    @Test
    public void testDelete() {
        // 根据条件删除
        Query query = new Query(Criteria.where("myname").is("Jack"));
        this.mongoTemplate.remove(query, Person.class);
    }
}
