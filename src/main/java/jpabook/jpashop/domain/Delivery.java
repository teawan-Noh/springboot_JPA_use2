package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {
    
    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;
    
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY) // 거울
    private Order order;
    
    @Embedded
    private Address address;
    
    @Enumerated(EnumType.STRING) // EnumType.ORDINAL은 숫자로 들어감 사용하지 말 것. 
    private DeliveryStatus status; // READY, COMP
}
