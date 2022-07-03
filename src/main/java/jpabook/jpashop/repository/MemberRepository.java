package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

//    @PersistenceContext // 스프링이 EntityManager 만들어서 주입해준다.  //@RequiredArgsConstructor 안쓸 때 사용
    private final EntityManager em;

    public void save(Member member){
        em.persist(member);  // persist : 영속성 컨텍스트에 member를 넣어두었다가 commit되는 시점에 디비로 insert 쿼리가 날라감
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    // createQuery(쿼리, 반환타입)
    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

}
