package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // 기본적으로 트랜잭션이 끝나면 롤백함 // @Rollback(false) 처리하면 롤백 안함 (insert 쿼리 날라감).
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
//    @Rollback(false)
    public void joinMember() throws Exception {
        //given
        Member member = new Member();
        member.setName("Kim");

        //when
        Long savedId = memberService.join(member); // 영속성 컨텍스트에 올라감 // db에 커밋하기 전에는 insert쿼리 안날림. em.flush 하면 날림

        //then
        assertEquals(member, memberRepository.findOne(savedId)); // @Transactional이 선언 되어있기에 가능
    }

    @Test
    public void duplicateMember() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("Kim1");

        Member member2 = new Member();
        member2.setName("Kim1");

        //when
        memberService.join(member1);

        //then // 아이디가 중복인 경우 IllegalStateException 날림
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertEquals("이미 존재하는 회원입니다.", thrown.getMessage());
    }
}