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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.springboot.util.ApiDocumentUtils.getRequestPreProcessor;
import static com.springboot.util.ApiDocumentUtils.getResponsePreProcessor;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
        long memberId = 1L;

        MemberDto.Response response = new MemberDto.Response(memberId,
                "tjsk1999@naver.com",
                "택",
                "010-6782-8932",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp());


        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());
        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        ResultActions getActions = mockMvc.perform(
                get("/v11/members/{memberId}", memberId)
                        .accept(MediaType.APPLICATION_JSON)

        );

        getActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(response.getEmail()))
                .andDo(document(
                        "get-member",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        pathParameters(parameterWithName("memberId").description("회원 식별자")),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 데이터"),
                                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("data.phone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                                        fieldWithPath("data.memberStatus").type(JsonFieldType.STRING).description("회원 상태: 활동중 / 휴면 상태 / 탈퇴 상태"),
                                        fieldWithPath("data.stamp").type(JsonFieldType.NUMBER).description("스탬프 갯수")
                                )
                        )
                ));
    }

    @Test
    public void getMembersTest() throws Exception {
        // TODO 여기에 MemberController의 getMembers() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
        long memberIdOne = 1L;

        long memberIdTwo = 2L;

        Page<Member> pageMembers = new PageImpl<>(List.of(new Member(), new Member()));

        List<MemberDto.Response> responses = List.of(
                new MemberDto.Response(
                        memberIdOne,
                        "tjsk1999@naver.com",
                        "택현",
                        "010-6782-8932",
                        Member.MemberStatus.MEMBER_ACTIVE,
                        new Stamp()
                ),
                new MemberDto.Response(
                        memberIdTwo,
                        "asd@naver.com",
                        "택",
                        "010-1111-2222",
                        Member.MemberStatus.MEMBER_ACTIVE,
                        new Stamp()
                )
        );

        given(memberService.findMembers(Mockito.anyInt(), Mockito.anyInt())).willReturn(pageMembers);
        given(mapper.membersToMemberResponses(Mockito.anyList())).willReturn(responses);

        ResultActions resultActions = mockMvc.perform(
                get("/v11/members")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("page", "1")
                        .param("size", "20")
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(responses.size()))
                .andDo(document(
                        "get-members",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.ARRAY).description("결과 데이터"),
                                fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("data[].email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("data[].phone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                                fieldWithPath("data[].memberStatus").type(JsonFieldType.STRING).description("회원 상태: 활동중 / 휴면 상태 / 탈퇴 상태"),
                                fieldWithPath("data[].stamp").type(JsonFieldType.NUMBER).description("스탬프 갯수"),
                                fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("현재 페이지 정보"),
                                fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("한 페이지 당 데이터 개수"),
                                fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 데이터 개수"),
                                fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 개수")
                        )
                ));
    }

    @Test
    public void deleteMemberTest() throws Exception {
        // TODO 여기에 MemberController의 deleteMember() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
        long memberId = 1L;

        doNothing().when(memberService).deleteMember(Mockito.anyLong());

        ResultActions deleteActions = mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/v11/members/{memberId}", memberId)
        );

        deleteActions.andExpect(status().isNoContent())
                .andDo(document(
                        "delete-member",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        pathParameters(
                                parameterWithName("memberId").description("회원 식별자")
                        )
                ));
    }
}
