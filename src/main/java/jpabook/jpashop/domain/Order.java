package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // 테이블명 설정
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 다른곳에서 생성자 못쓰도록 막아둠
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // FK //연관관계 주인
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // CascadeType.ALL : delete 할때도 같이 처리해 줌
    private List<OrderItem> orderItems = new ArrayList<>();

    // casecade 선언 안했을 때 : 엔티티당 각각 persist를 호출해야 한다.
    // persist(orderItemA)
    // persist(orderItemB)
    // persist(orderItemC)
    // persist(order)

    // casecade 선언 했을 때 : cascade 는 persist를 전파한다. orderItemA,B,C 가 컬랙션에 담겨있으므로 모두 persist 해준다ㅏ.
    // persist(order)

    // cascade 선언 : delivery에 값만 설정해 놓고 order만 persist하면 delivery까지 같이 persist 해준다.
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY) // 하나의 배송정보는 하나의 주문정보를 가진다
    @JoinColumn(name = "delivery_id") // FK // 연관관계 주인
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문시간

    @Enumerated(EnumType.STRING) // EnumType.ORDINAL은 숫자로 들어감 사용하지 말 것.
    private OrderStatus status; // 주문상태 [ORDER, CANCEL]

    //==연관관계 편의 메서드==//
    // 컨트롤 하는쪽이 들고있는게 좋다.
    // 양방향일 때 사용하면 좋다 - 양쪽에 세팅해야하는 것을 원자적으로 한번에 세팅한다.
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    // 연관관계 메서드를 사용 안할 경우 - 비지니스 로직에서 아래처럼 처리해줘야 한다. -> 연관관계 메서드로 처리 -> 누락 실수 등을 방지
    // Member member = new Member();
    // Order order = new Order();
    //
    // member.getOrders().add(order);
    // order.setMember(member);

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==// 여러개의 연관관계로 복잡한 생성은 별도의 생성 메서드가 있으면 좋다.
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }
//    생성 메서드( createOrder() ): 주문 엔티티를 생성할 때 사용한다.
//    주문 회원, 배송정보, 주문상품의 정보를 받아서 실제 주문 엔티티를 생성한다.
    
    //==비지니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능합니다.");
        }
        
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) { // 각각 아이템에 캔슬 날려준다
            orderItem.cancel();
        }
    }
//    주문 취소( cancel() ): 주문 취소시 사용한다. 주문 상태를 취소로 변경하고 주문상품에 주문 취소를 알린다.
//    만약 이미 배송을 완료한 상품이면 주문을 취소하지 못하도록 예외를 발생시킨다.

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice(){
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
        // 스트림이나 람다를 사용하면 깔끔하게 사용 가능
//        int totalPrice = orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
//        return totalPrice;
    }

//    전체 주문 가격 조회: 주문 시 사용한 전체 주문 가격을 조회한다.
//    전체 주문 가격을 알려면 각각의 주문상품가격을 알아야 한다.
//    로직을 보면 연관된 주문상품들의 가격을 조회해서 더한 값을 반환한다.(실무에서는주로 주문에 전체 주문 가격 필드를 두고 역정규화 한다.)
}
