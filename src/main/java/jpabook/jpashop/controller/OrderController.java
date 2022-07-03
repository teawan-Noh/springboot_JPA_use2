package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model){

        // 선택할 수 있는 리스트를 제공하기 위해 list를 전부 불러와서 전달해줌
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    //주문 폼 이동
    //메인 화면에서 상품 주문을 선택하면 /order 를 GET 방식으로 호출
    //OrderController 의 createForm() 메서드
    //주문 화면에는 주문할 고객정보와 상품 정보가 필요하므로 model 객체에 담아서 뷰에 넘겨줌

    //@RequestParam: 화면의 select의 name이 파람과 일치하는 곳의 value를 가져온다
    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count){

        orderService.order(memberId, itemId, count);
        return "redirect:/orders";
    }

    //주문 실행
    //주문할 회원과 상품 그리고 수량을 선택해서 Submit 버튼을 누르면 /order URL을 POST 방식으로 호출
    //컨트롤러의 order() 메서드를 실행
    //이 메서드는 고객 식별자( memberId ), 주문할 상품 식별자( itemId ), 수량( count ) 정보를 받아서 주문 서비스에 주문을 요청
    //주문이 끝나면 상품 주문 내역이 있는 /orders URL로 리다이렉트


    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model){
    // 단순하게 화면에서 조회하는 기능만 있을 경우 컨트롤러에서 레파지도토리로 바로 불러도 괜찮다
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);

        return "order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId){
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
