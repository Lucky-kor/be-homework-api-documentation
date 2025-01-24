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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.springboot.util.ApiDocumentUtils.getRequestPreProcessor;
import static com.springboot.util.ApiDocumentUtils.getResponsePreProcessor;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
        long memberId = 1L;

        MemberDto.Response response = new MemberDto.Response(memberId,
                "test@test.com",
                "test",
                "010-1111-1111",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp());

        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());
        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        ResultActions getActions = mockMvc.perform(
                get("/v11/members/{member-id}", memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        getActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("test"))
                .andExpect(jsonPath("$.data.email").value("test@test.com"))
                .andDo(document(
                        "get-member",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        pathParameters(
                                parameterWithName("member-id").description("회원 아이디")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 데이터"),
                                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("멤버 아이디"),
                                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("data.phone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                                        fieldWithPath("data.memberStatus").type(JsonFieldType.STRING).description("멤버 상태"),
                                        fieldWithPath("data.stamp").type(JsonFieldType.NUMBER).description("스탭프 갯수")
                                )
                        )
                ));
    }

    @Test
    public void getMembersTest() throws Exception {
        // TODO 여기에 MemberController의 getMembers() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
        Page<Member> pageMembers = new PageImpl<>(List.of(new Member(), new Member()));

        List<MemberDto.Response> responses = List.of(
                new MemberDto.Response(
                    1L,
                    "test1@test.com",
                    "test1",
                    "010-1111-1111",
                    Member.MemberStatus.MEMBER_ACTIVE,
                    new Stamp()
                ),
        new MemberDto.Response(
                2L,
                "test2@test.com",
                "test2",
                "010-2222-2222",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
                )
        );

        given(memberService.findMembers(Mockito.anyInt(), Mockito.anyInt())).willReturn(pageMembers);
        given(mapper.membersToMemberResponses(Mockito.anyList())).willReturn(responses);

        //path 설정
        MultiValueMap<String,String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("page", "1");
        multiValueMap.add("size", "10");

        //ResultActions
        ResultActions getActions = mockMvc.perform(
                get("/v11/members")
                        .params(multiValueMap)
                        .accept(MediaType.APPLICATION_JSON)
        );

        getActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andDo(document(
                        "get-members",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        responseFields(
                                List.of(
                                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("결과 데이터"),
                                        fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER).description("회원 아이디"),
                                        fieldWithPath("data[].email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data[].name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("data[].phone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                                        fieldWithPath("data[].memberStatus").type(JsonFieldType.STRING).description("멤버 상태"),
                                        fieldWithPath("data[].stamp").type(JsonFieldType.NUMBER).description("스탭프 갯수"),
                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지 결과"),
                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("페이지"),
                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("사이즈"),
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
        doNothing().when(memberService).deleteMember(Mockito.anyLong());

        ResultActions actions = mockMvc.perform(
                delete("/v11/members/{member-id}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isNoContent())
                .andDo(document(
                        "delete-member",
                        pathParameters(
                                parameterWithName("member-id").description("회원아이디")
                        )
                ));
    }
}
