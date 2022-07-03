package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("M") // 기본으로 두면 클레스명으로 들어갈꺼다
@Getter
@Setter
public class Movie extends Item{

    private String director;
    private String actor;
}
