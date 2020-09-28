package jpabook.jpashop.api;

import java.util.List;
import java.util.PrimitiveIterator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

  private final MemberService memberService;

  @GetMapping(value = "/api/v1/members")
  public List<Member> membersV1() {
    return memberService.findMembers();
  }

  @GetMapping(value = "/api/v2/members")
  public Result membersV2() {
    List<Member> findMembers = memberService.findMembers();
    List<MemberDto> collect = findMembers.stream()
        .map(m -> new MemberDto(m.getName()))
        .collect(Collectors.toList());
    return new Result(collect);
  }

  @Data
  @AllArgsConstructor
  class Result<T> {
    private T data;
  }

  @Data
  @AllArgsConstructor
  class MemberDto {
    private String name;
  }

  @PostMapping(value = "/api/v1/members")
  public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
    Long id = memberService.join(member);
    return new CreateMemberResponse(id);
  }

  @PostMapping(value = "/api/v2/members")
  public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
    Member member = new Member();
    member.setName(request.getName());
    //member.setAddress(new Address(request.getCity(), request.getStreet(), request.getZipcode()));

    Long id = memberService.join(member);
    return new CreateMemberResponse(id);
  }

  @PutMapping(value = "/api/v2/members/{id}")
  public UpdateMemberResponse updateMemberV2(@PathVariable Long id, @RequestBody @Valid UpdateMemberRequest request){
    memberService.update(id, request.getName());
    Member findMember = memberService.findOne(id);
    return new UpdateMemberResponse(id, findMember.getName());
  }

  @Data
  static class CreateMemberRequest {
    private String name;
//    private String city;
//    private String street;
//    private String zipcode;
  }

  @Data
  class CreateMemberResponse {
    private Long id;

    public CreateMemberResponse(Long id) {
      this.id = id;
    }
  }

  @Data
  static class UpdateMemberRequest {
    private String name;
  }

  @Data
  @AllArgsConstructor
  class UpdateMemberResponse {
    private Long id;
    private String name;
  }
}
