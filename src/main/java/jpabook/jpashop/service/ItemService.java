package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // 조회가 더 많은 경우 클레스에 readOnly 선언
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional // 클레스에 readOnly 선언해서 따로 설정 안해주면 저장 안됨.
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    public List<Item > findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }

    @Transactional
    public void updateItem(Long id, String name, int price) {
        Item item = itemRepository.findOne(id);
        item.setName(name);
        item.setPrice(price);
    }
}
