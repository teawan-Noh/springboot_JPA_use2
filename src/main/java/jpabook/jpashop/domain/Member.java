package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

//    @JsonIgnore
    @OneToMany(mappedBy = "member") // 연관관계의 주인이 아니다 - 맵핑하는게 아니라 맵핑되는 거울이다 - 읽기 전용이다
    private List<Order> orders = new ArrayList<>();
}
