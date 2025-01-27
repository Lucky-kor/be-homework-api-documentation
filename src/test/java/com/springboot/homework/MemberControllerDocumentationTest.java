package com.springboot.homework;

import com.jayway.jsonpath.JsonPath;
import com.springboot.member.controller.MemberController;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.google.gson.Gson;
import com.springboot.stamp.Stamp;
import org.assertj.core.error.array2d.Array2dElementShouldBeDeepEqual;
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

import java.util.ArrayList;
import java.util.List;

import static com.springboot.util.ApiDocumentUtils.getRequestPreProcessor;
import static com.springboot.util.ApiDocumentUtils.getResponsePreProcessor;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
        long memberId = 1L;
        // TODO 여기에 MemberController의 getMember() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
//        MemberDto.Post post = new MemberDto.Post(
//                "honggildong@gmail.com",
//                "홍길동이",
//                "010-1111-0000"
//        );

        MemberDto.Response response = new MemberDto.Response(
                memberId,
                "gildon2@naver.com",
                "고길동",
                "010-8819-2761",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        );

        given(mapper.memberToMemberResponse(Mockito.any())).willReturn(response);

        ResultActions actions = mockMvc.perform(
                get("/v11/members/" + memberId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.member-id").value(response.getMemberId()))
                .andExpect(jsonPath("$.data.email").value(response.getEmail()))
                .andExpect(jsonPath("$.data.name").value(response.getName()))
                .andExpect(jsonPath("$.data.phone").value(response.getPhone()))
                .andExpect(jsonPath("$.data.memberStatus").value(response.getMemberStatus()))
//                 여기서부터 문서화 시작
                .andDo(document(
                        "get-member",
                        // 이쁘게 꾸미기 ~
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        pathParameters(
                                parameterWithName("member-id").description("회원 식별자")
                        ),
                        // 요청 필드
                        requestFields(
                                List.of(
                                        fieldWithPath("member-id").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("phone").type(JsonFieldType.STRING).description("핸드폰 번호"),
                                        fieldWithPath("memberStatus").type(JsonFieldType.STRING).description("회원 상태 : MEMBER_ACTIVE / MEMBER_SLEEP / MEMBER_QUIT")
                                )
                        ), responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 데이터"),
                                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("data.phone").type(JsonFieldType.STRING).description("핸드폰 번호"),
                                        fieldWithPath("data.memberStatus").type(JsonFieldType.STRING).description("회원 상태 : MEMBER_ACTIVE / MEMBER_SLEEP / MEMBER_QUIT"),
                                        fieldWithPath("data.stamp").type(JsonFieldType.OBJECT).description("스탬프")
                                )
                        )
                ));
    }

    @Test
    public void getMembersTest() throws Exception {
        // TODO 여기에 MemberController의 getMembers() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
    List<MemberDto.Response> memberList = List.of(
            new MemberDto.Response(1L, "memberOne@gmail.com", "1번회원", "010-1101-0010", Member.MemberStatus.MEMBER_ACTIVE, new Stamp()),
            new MemberDto.Response(2L, "memberTwo@gmail.com", "2번회원", "010-0001-0000", Member.MemberStatus.MEMBER_ACTIVE, new Stamp())
    );
        Page<Member> page = new PageImpl<>(List.of(new Member(), new Member()));

        // any가 안들어가네
        given(memberService.findMembers(Mockito.anyInt(), Mockito.anyInt())).willReturn(page);
        given(mapper.membersToMemberResponses(Mockito.any())).willReturn(memberList);

        ResultActions actions = mockMvc.perform(
                get("/v11/members")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("page", "1")
                        .param("size", "10")
        );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(memberList.size()))
                // 문서화 시작
                .andDo(document(
                        "get-members",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.ARRAY).description("결과값"),
                                fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("data[].email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("data[].phone").type(JsonFieldType.STRING).description("핸드폰 번호"),
                                fieldWithPath("data[].memberStatus").type(JsonFieldType.STRING).description("회원 상태 : MEMBER_ACTIVE / MEMBER_SLEEP / MEMBER_QUIT"),
                                fieldWithPath("data[].stamp").type(JsonFieldType.NUMBER).description("스탬프"),
                                fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지정보"),
                                fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("현재페이지"),
                                fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지에 담을 수 있는 데이터 갯수"),
                                fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("총 데이터"),
                                fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("총 페이지")
                        )
                ));


    }

    @Test
    public void deleteMemberTest() throws Exception {
        long memberId = 1L;

        doNothing().when(memberService).deleteMember(memberId);

        ResultActions actions = mockMvc.perform(
                delete("/v11/members/" + memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions
                .andExpect(status().isNoContent())
                // 문서화
                .andDo(document(
                        "delete-member",
                        getRequestPreProcessor(),
                        getResponsePreProcessor()
                ));
    }
}
