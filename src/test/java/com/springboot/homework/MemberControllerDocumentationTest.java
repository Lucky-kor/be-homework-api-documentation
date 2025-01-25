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
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.springboot.util.ApiDocumentUtils.getRequestPreProcessor;
import static com.springboot.util.ApiDocumentUtils.getResponsePreProcessor;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

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
        //given
        long memberId = 1L;
        MemberDto.Response response = new MemberDto.Response(
                memberId, "men@naver.com", "까치", "010-3048-3854",
                Member.MemberStatus.MEMBER_ACTIVE, new Stamp());

        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());
        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        //when
        ResultActions actions = mockMvc.perform(
                get("/v11/members/{memberId}", memberId) //member-id 일때는 안되고, memberId로 변경
                        .accept(MediaType.APPLICATION_JSON)
        );
            actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(response.getName()))
                .andDo(document(
                        "get-member",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        pathParameters(
                                parameterWithName("memberId").description("회원식별자")
                ),
               responseFields(
                       List.of(
                               fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                               fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                               fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                               fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름"),
                               fieldWithPath("data.phone").type(JsonFieldType.STRING).description("연락처"),
                               fieldWithPath("data.memberStatus").type(JsonFieldType.STRING).description("회원상태 : MEMBER_ACTIVE / MEMBER_SLEEP / MEMBER_QUIT"),
                               fieldWithPath("data.stamp").type(JsonFieldType.NUMBER).description("스탬프")
                       )
               )
        ));
    }

    @Test
    public void getMembersTest() throws Exception {
        // TODO 여기에 MemberController의 getMembers() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
       //given
        Page<Member> pageMember = new PageImpl<>(List.of(new Member(), new Member()));
        List<MemberDto.Response> responses = List.of(
                new MemberDto.Response(1L, "qkq@naver.com","김밥말아", "010-3848-4432", Member.MemberStatus.MEMBER_ACTIVE,new Stamp()),
                new MemberDto.Response(2L, "aajrrhrk@google.com","참치김밥", "010-3947-3443", Member.MemberStatus.MEMBER_SLEEP, new Stamp())
        );

        given(memberService.findMembers(Mockito.anyInt(), Mockito.anyInt())).willReturn(pageMember);
        given(mapper.membersToMemberResponses(Mockito.any())).willReturn(responses);

        //when
        ResultActions actions = mockMvc.perform(
                get("/v11/members")
                        .param("page", "1")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(responses.size()))
                .andDo(document(
                                "get-mambers",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                responseFields(
                        List.of(
                                fieldWithPath("data").type(JsonFieldType.ARRAY).description("데이터"),
                                fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("data[].email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("data[].phone").type(JsonFieldType.STRING).description("연락처"),
                                fieldWithPath("data[].memberStatus").type(JsonFieldType.STRING).description("회원상태 : MEMBER_ACTIVE / MEMBER_SLEEP / MEMBER_QUIT"),
                                fieldWithPath("data[].stamp").type(JsonFieldType.NUMBER).description("스탬프"),
                                fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지인포"),
                                fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("페이지"),
                                fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("사이즈"),
                                fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("총요소"),
                                fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("총페이지")
                        )

                ))
            );


    }

    @Test
    public void deleteMemberTest() throws Exception {
        // TODO 여기에 MemberController의 deleteMember() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
        //given
        long memberId = 1L;

        doNothing().when(memberService).deleteMember(memberId);

        //when
        ResultActions actions = mockMvc.perform(
                delete("/v11/members/{member-id}", memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions.andExpect(status().isNoContent())
                .andDo(document(
                        "delete-member",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        pathParameters(
                               parameterWithName("member-id").description("회원 식별자")
                        )

                ));

    }
}
