package jpabook.jpashop;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/*
* userA
*** JPA1 BOOK
*** JPA2 BOOK
* userB
*** SPRING1 BOOK
*** SPRING2 BOOK
*/

@Component
@RequiredArgsConstructor
public class initDb {

  private final InitService initService;

  @PostConstruct
  public void init() {
    initService.dbInit1();
    initService.dbInit2();
  }

  @Component
  @Transactional
  @RequiredArgsConstructor
  static class InitService {

    private final EntityManager em;

    public void dbInit1() {
      Member member = createMember("userA", "서울", "테헤란로", "06236");
      em.persist(member);

      Book book1 = createBook("JPA1 Book", 10000, 10);
      em.persist(book1);

      Book book2 = createBook("JPA1 Book", 20000, 10);
      em.persist(book2);

      OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
      OrderItem orderItem2 = OrderItem.createOrderItem(book2, 10000, 2);

      Delivery delivery = createDelivery(member);

      Order.createOrder(member, delivery, orderItem1, orderItem2);

    }

    public void dbInit2() {
      Member member = createMember("userB", "부산", "해운대", "11223");
      em.persist(member);

      Book book1 = createBook("SPRING1 Book", 10000, 10);
      em.persist(book1);

      Book book2 = createBook("SPRING2 Book", 20000, 10);
      em.persist(book2);

      OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
      OrderItem orderItem2 = OrderItem.createOrderItem(book2, 10000, 2);

      Delivery delivery = createDelivery(member);

      Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);

      em.persist(order);
    }

    private Member createMember(String name, String city, String street, String zipcode) {
      Member member = new Member();
      member.setName(name);
      member.setAddress(new Address(city, street, zipcode));
      return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
      Book book = new Book();
      book.setName(name);
      book.setPrice(price);
      book.setStockQuantity(stockQuantity);
      return book;
    }

    private Delivery createDelivery(Member member) {
      Delivery delivery = new Delivery();
      delivery.setAddress(member.getAddress());
      return delivery;
    }
  }

}