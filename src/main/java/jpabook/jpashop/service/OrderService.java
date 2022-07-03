package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
//    주문 서비스는 주문 엔티티와 주문 상품 엔티티의 비즈니스 로직을 활용해서 주문, 주문 취소, 주문 내역 검색 기능을 제공한다.
//    참고: 예제를 단순화하려고 한 번에 하나의 상품만 주문할 수 있다.
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     * controller 에서는 식별자만 넘기고 service에서 엔티티 조회 및 로직 처리
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count){

        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress()); //예제기 때문에 간단하게 처리 (배송 주소 = 주문자의 주소)

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);
        // casecade 옵션 덕분에 Delivery와 OrderItem을 따로 persist 안하고 자동으로 persist 됨 -> 트랜잭션이 커밋되는 시점에 flush가 일어나며 쿼리가 날라감
        // Delivery와 OrderItem을 Order에서만 참조해서 쓰기 때문에 casecade를 사용했지만
        // Delivery와 OrderItem을 여러군데에서 참조해서 사용할 경우 casecade 사용하지말고 별도의 repository를 생성해서 persist 해주는게 좋다

        return order.getId();
    }
//    주문( order() ): 주문하는 회원 식별자, 상품 식별자, 주문 수량 정보를 받아서 실제 주문 엔티티를 생성한 후 저장한다.

    /**
     * 취소
     */

    @Transactional
    public void cancelOrder(Long orderId){
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        // 주문 취소소
       order.cancel();

       // JPA의 장점 - 더티 체크를 통해 변동이 있는 부분에 대한 쿼리가 자동으로 날라간다.
    }

//    주문 취소( cancelOrder() ): 주문 식별자를 받아서 주문 엔티티를 조회한 후 주문 엔티티에 주문 취소를 요청한다.

    // 검색
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAllByString(orderSearch);
    }

//    주문 검색( findOrders() ): OrderSearch 라는 검색 조건을 가진 객체로 주문 엔티티를 검색한다.
//    자세한 내용은 다음에 나오는 주문 검색 기능에서 알아보자.


    // 현재 사용중인 모델 : 도메인 모델 패턴  // JPA/ ORM을 사용하면 이 패턴을 많이 사용한다. - 유지보수에 어떤 패턴이 더 효율적인지 판단하여 사용하면 된다.
    // 한 프로잭트 내에서 둘 다 사용할 수도있다. 현재 문맥에서 어떤걸 적용하는게 더 효율적인지 판단하여 사용하면 된다.
    //참고: 주문 서비스의 주문과 주문 취소 메서드를 보면 비즈니스 로직 대부분이 엔티티에 있다. 서비스 계층은 단순히 엔티티에 필요한 요청을 위임하는 역할을 한다.
    //이처럼 엔티티가 비즈니스 로직을 가지고 객체 지향의 특성을 적극 활용하는 것을
    //도메인 모델 패턴(http://martinfowler.com/eaaCatalog/domainModel.html)이라 한다.
    //반대로 엔티티에는 비즈니스 로직이 거의 없고 서비스 계층에서 대부분의 비즈니스 로직을 처리하는 것을
    // 트랜잭션 스크립트 패턴(http://martinfowler.com/eaaCatalog/transactionScript.html)이라 한다.
}
