package com.springboot.homework;
import com.springboot.member.controller.MemberController;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.google.gson.Gson;
import com.springboot.stamp.Stamp;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class MemberControllerDocumentationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberMapper mapper;

    @Autowired
    private Gson gson;

    @Test
    public void getMemberTest() throws Exception {
        // TODO 여기에 MemberController의 getMember() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
        // - 주제: 복잡한 미로에서 어딘가에 있을 MEMBER 라는 주인공을 찾아라!
        //1. MemberController 클래스에 @GetMapping 애너테이션으로 public ResponseEntity getMember(){} 메서드를 참고한다.
        //2. Member member(객체) = memberService (객쳬).findMember(memberId) - 맴버 ID 별로 식별해서 조회를 해야합니다.
        //3. getMember 라는 메서드의 리턴문을 보니 리스폰스 엔티티를 singleResponseDto 의 생성자를 통해서 받고 있는 것을 알수 있다.
        //4. singleResponseDto 의 매개변수를 보겠다. -> MemberMapper 라는 인터페이스 뼈대인 memberToMemberRespose 에게 member로 매개 변수를 받고 있다.
        //5. MemberMapper 인터페이스를 따라 가보니 -> MemberDto.Response 라는 곳에 Member member 가 담겨 지는 것을 확인할 수 있다.
        //6. 오 드디어 Member member 객체를 찾았습니다~!!
        //7. Member 객체는 memberId, email, name, phone, memberStatus, stamp 라는 정보를 가지고 있습니다.
        //8. 오 그러면 지금 당장 그 Member 라는 객체의 정보를 가져오자!
        //9. 헉 근데 사장님. 저희는 DB 가 없어서 데이터가 별도로 저장이 안됩니다.
        //10. 흠 그럼 너가 만들어!  개발자야.. 개발자는 고생 길이다.

        //힌트를 주마.
        // Member 객체에 있는 memberId(long), email(String), name(String), phone(String), memberstatus(상태), Stamp(long) 를 찾는게 이번 과제의 목표다.

        // 아니 사장님 그거는 내가 말했잖아요! 사장: 내가 시켰으니 너는 무조건 해야해.
        //미안. 자금이 부족해서 우리는 데이터베이스가 없어. 너가 스스로 더미 데이터를 생성해서 테스트를 해봐야 해.
        //given, when. then 형식에 맞춰서 해.

        long memberId = 1L;
        //왜 MemberDto.Response 를 선언하는가?
        //client -> server 간의 데이터 전송을 할 때 유심히 바라보면 답이 나온다.
        //client 입장에서 정보를 줄때 Dto 라는 데이터를 전송을 합니다.
        //따라서, Dto 를 선언해야 하는 것이지요.
        MemberDto.Response response = new MemberDto.Response(memberId, "asd123@gmail.com","김오이","010-4444-5555", Member.MemberStatus.MEMBER_ACTIVE,new Stamp());

        //given -> mock 형태를 지정해서 만들어줘야 합니다.
        //memberService 와 mapper 에 가짜 mock 형태로 틀을 만들어 줘야 합니다.
        // MemberController 테스트를 위해서 memberService를 가짜(mock) 형태로 만들어줘서 테스트를 진행하면 리소스를 줄일 수 있기 떄문에 만들어 줍니다.
        //특히, mapper 에는 response 라는 객체를 willReturn 해줘야 한다.
        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());
        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        //when
        //URI url 혹은 ResultActions actions perform 으로 http 경로를 설정해줍니다.
        //사용자가 접속할 http 웹 사이트 주소가 필요합니다.
        // 결과를 표시할 url 를 명시해줘야 하며, get 매핑은 body 의 데이터를 담지 말아야 한다는 것에 주의해야 합니다.

        ResultActions actions = mockMvc.perform(
                get("/v11/members/"+memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then //검증
        //검증을 위한 상태코드를 표시 해줘야 합니다.
        //요청한 데이터가 알맞게 들어 왔는지 검증을 해줘야 하는 것입니다.
        // 데이터의 형식을 검증하기 위해 데이터에서 고유한 값이 email을 이용하면 됩니다.
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(memberId))
                .andExpect(jsonPath("$.data.email").value(response.getEmail()))
                .andDo(
                        document("get-member",
                                preprocessRequest(),
                                preprocessResponse(),
                                responseFields(
                                        List.of(
                                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터").optional(),
                                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("식별자"),
                                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름"),
                                                fieldWithPath("data.phone").type(JsonFieldType.STRING).description("번호"),
                                                fieldWithPath("data.memberStatus").type(JsonFieldType.STRING).description("회원 상태"),
                                                fieldWithPath("data.stamp").type(JsonFieldType.NUMBER).description("스탬프")
                                        )
                                )
                        ));

    }

    @Test
    public void getMembersTest() throws Exception {
        // TODO 여기에 MemberController의 getMembers() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
        //위에 있는 getMember 와 약간의 차이로는 많은 데이터가 들어 온다는 것입니다.
        // List<> 형태이며, page,size 를 가지고 있습니다.

        long memberId = 1L;

        Page<Member> page = new PageImpl<>(List.of(new Member(), new Member()));
        List<MemberDto.Response> responses = List.of(
                new MemberDto.Response(1L, "asd123@gmail.com", "김라뗴", "010-4444-5555", Member.MemberStatus.MEMBER_ACTIVE,new Stamp()),
                new MemberDto.Response(2L, "fff44@gmail.com", "김라뗴", "010-4444-5555", Member.MemberStatus.MEMBER_ACTIVE,new Stamp()),
                new MemberDto.Response(3L, "zxc878@gmail.com", "라라뗴", "010-6767-5555", Member.MemberStatus.MEMBER_ACTIVE,new Stamp()),
                new MemberDto.Response(4L, "koko7878@gmail.com", "오라뗴", "010-7878-5555", Member.MemberStatus.MEMBER_ACTIVE,new Stamp())
        );

        //given
        given(memberService.findMembers(Mockito.anyInt(),Mockito.anyInt())).willReturn(page);
        given(mapper.membersToMemberResponses(Mockito.anyList())).willReturn(responses);

        //when
        ResultActions actions = mockMvc.perform(
                get("/v11/members")
                        .param("page","1")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)

        );

        //then

        actions .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(responses.size()))
                .andDo(document("get-members",
                        preprocessRequest(),
                        preprocessResponse(),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("결과").optional(),
                                        fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("data[].email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data[].name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("data[].phone").type(JsonFieldType.STRING).description("번호"),
                                        fieldWithPath("data[].memberStatus").type(JsonFieldType.STRING).description("회원 상태"),
                                        fieldWithPath("data[].stamp").type(JsonFieldType.NUMBER).description("스탬프"),
                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수")
                                )
                        )

                ));


    }

    @Test
    public void deleteMemberTest() throws Exception {
        // TODO 여기에 MemberController의 deleteMember() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
        long memberId = 1L;
        MemberDto.Response response = new MemberDto.Response(memberId,"asd123@gmail.com", "김기리","010-4444-5555", Member.MemberStatus.MEMBER_ACTIVE, new Stamp());
        //service, mapper 단 으로 들어옵니다. 따라서 response entity에 대한 given 으로 목을 만들어 줘야 해요.
        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());
        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        //given -> delete 에서는 given 설정이 업습니다.

        //when
        doNothing().when(memberService).deleteMember(Mockito.anyLong());
        ResultActions actions = mockMvc.perform(
                delete("/v11/members/"+memberId)
                        .accept(MediaType.APPLICATION_JSON));
        //then
        actions.andExpect(status().isNoContent())
                .andDo(document(
                        "delete-member",
                        preprocessRequest(),
                        preprocessResponse()

                ));
    }
}
