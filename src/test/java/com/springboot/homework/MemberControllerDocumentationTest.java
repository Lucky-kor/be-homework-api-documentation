package com.springboot.homework;

import com.jayway.jsonpath.JsonPath;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

import static com.springboot.util.ApiDocumentUtils.getRequestPreProcessor;
import static com.springboot.util.ApiDocumentUtils.getResponsePreProcessor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest 애노테이션은 Spring MVC 컨트롤러를 테스트하는 데 사용됩니다.
// 여기서는 MemberController를 테스트 대상으로 지정합니다.
// 이 애노테이션은 컨트롤러와 관련된 웹 계층만 로드하며, 서비스, 리포지토리 등 다른 빈은 로드하지 않습니다.
@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class MemberControllerDocumentationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;
    // 테스트 대상이 의존하는 클래스를 작성

    @MockBean
    private MemberMapper mapper;

    @Autowired
    private Gson gson;

    @Test
    public void getMemberTest() throws Exception { // get은 응답데이터에 바디에 못담음.
        // TODO 여기에 MemberController의 getMember() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
        // given
        // 테스트에 사용할 스텁 데이터.
        long memberId = 1L;
        MemberDto.Response responseDto = new MemberDto.Response(
                memberId,
                "munchi@cat.com",
                "만두",
                "010-3030-2020",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        );

        String content = gson.toJson(responseDto);

        // given 이건 테스트할 컨트롤러의 핸들러를 봐보면 memberService, mapper 를 두개 사용했음
        // Mock 객체를 이용한 Stubbing(given을 통한 메서드 지정)
        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());
        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(responseDto);

        // when
        // 이걸 할때에
        ResultActions actions = mockMvc.perform(
                RestDocumentationRequestBuilders
                        .get("/v11/members/{memberId}", memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );
        // then
        // 기대값은..
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(responseDto.getName()))
                .andExpect(jsonPath("$.data.memberId").value(responseDto.getMemberId()))
                .andDo(document( // 문서화 시작
                        "get-member",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        pathParameters( // get 할때는 1명의 회원 정보조회를 하는거니까. memberId를 받음.
                                parameterWithName("memberId").description("회원 식별자")
                                // 여기까지 요청 하는 방법.
                        ),
                        responseFields( List.of(
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("정보"),
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("회원 이메일"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("data.phone").type(JsonFieldType.STRING).description("전화번호"),
                                fieldWithPath("data.memberStatus").type(JsonFieldType.STRING).description("회원 상태:"),
                                fieldWithPath("data.stamp").type(JsonFieldType.NUMBER).description("스탬프 정보")
                                )
                                // 여기 까지가 응답 데이터와 설명. 어떡해 응답되는지, 무슨 데이터가 응답되는건지.
                        ))
                );
    }

    @Test
    public void getMembersTest() throws Exception {
        // TODO 여기에 MemberController의 getMembers() 핸들러 메서드 API 스펙 정보를
        // 포함하는 테스트 케이스를 작성 하세요.
        // given
        Member member1 = new Member("mandu@gmail.com", "만두", "010-2020-3030");
        member1.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);
        member1.setStamp(new Stamp());

        Member member2 = new Member("ari@dog.com", "아리", "010-2020-2020");
        member2.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);
        member2.setStamp(new Stamp());

        // 객체 두 개(member1, member2)를 포함하는 리스트를 생성. 이 리스트는 페이징된 데이터로 사용
        Page<Member> memberPage = new PageImpl<>(List.of(member1, member2), PageRequest.of(0, 10,Sort.by("memberId").descending()),2);

        List<MemberDto.Response> response = List.of(new MemberDto.Response (
                1L,
                "mandu@gmail.com",
                "만두",
                "010-2020-3030",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        ), new MemberDto.Response(
                2L,
                "ari@dog.com",
                "아리",
                "010-2020-2020",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
                )
        );
        String page = "1";
        String size = "10";
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page" , page);
        queryParams.add("size", size);

        given(memberService.findMembers(Mockito.anyInt(),Mockito.anyInt())).willReturn(memberPage);
        given(mapper.membersToMemberResponses(Mockito.anyList())).willReturn(response);

        // when
         ResultActions actions = mockMvc.perform(
                 get("/v11/members")
                         .params(queryParams)
                         .accept(MediaType.APPLICATION_JSON)
         );
         // then
        MvcResult result =
                actions.andExpect(status().isOk()) // 검증
                        .andExpect(jsonPath("$.data").isArray()) // 배열인지 검증
                        .andDo(document(
                                "get-members", // 이 경로로 요청을 넣을때
                                getRequestPreProcessor(),
                                getResponsePreProcessor(),
                                requestParameters( // 요청할때는 이런식으로 요청을 하라고 방법을 알려줌. 페이지와 사이즈를 입력해야함.
                                        parameterWithName("page").description("페이지"),
                                        parameterWithName("size").description("페이지 사이즈")
                                ),
                                responseFields(// 요청을 했을때, 응답으로 이러한 데이터들이 응답될것이다.
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("전체 데이터"),
                                        fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("data[].email").type(JsonFieldType.STRING).description("회원 이메일"),
                                        fieldWithPath("data[].name").type(JsonFieldType.STRING).description("회원 이름"),
                                        fieldWithPath("data[].phone").type(JsonFieldType.STRING).description("전화번호"),
                                        fieldWithPath("data[].memberStatus").type(JsonFieldType.STRING).description("회원 상태:"),
                                        fieldWithPath("data[].stamp").type(JsonFieldType.NUMBER).description("스탬프 정보"),
                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("전체 페이지 정보"),
                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("페이지"),
                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 데이터 갯수"),
                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 갯수")
                                )
                        )).andReturn();
        List list = JsonPath.parse(result.getResponse().getContentAsString()).read("$.data"); //

        assertThat(list.size(),is(2)); // 응답으로 나간 리스트의 사이즈가 2인지를 검증.

    }

    @Test
    public void deleteMemberTest() throws Exception {
        // TODO 여기에 MemberController의 deleteMember() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
        // given
        Member member1 = new Member("mandu@gmail.com", "만두", "010-2020-3030");
        member1.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);
        member1.setStamp(new Stamp());
        member1.setMemberId(1L);

        doNothing().when(memberService).deleteMember(Mockito.anyLong());
        // void라서 given() 말고 doNoting().when()
        // Mockito 에 있는 메서드.

        // when
        ResultActions actions = mockMvc.perform(
                delete("/v11/members/{memberId}", member1.getMemberId())
        );

                // then
        actions.andExpect(status().isNoContent())
                .andDo(document(
                        "delete-member",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        pathParameters(
                                List.of(
                                parameterWithName("memberId").description("회원 식별자")
                        )
                        )
                ));
    }
}
