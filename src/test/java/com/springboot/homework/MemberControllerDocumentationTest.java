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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import java.util.List;

import static com.springboot.util.ApiDocumentUtils.getRequestPreProcessor;
import static com.springboot.util.ApiDocumentUtils.getResponsePreProcessor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
                memberId,
                "munchkin@cute.cat",
                "먼치킨",
                "010-7777-7777",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        );
        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());

        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);
        //when / then
        mockMvc.perform(
                get("/v11/members/{memberId}",memberId)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(response.getName()))
                .andExpect(jsonPath("$.data.phone").value(response.getPhone()))
                .andDo(document(
                        "get-member",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        pathParameters(
                                parameterWithName("memberId").description("회원 식별자")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 데이터"),
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("회원 이메일"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("회원 이름 "),
                                fieldWithPath("data.phone").type(JsonFieldType.STRING).description("회원 핸드폰번호"),
                                fieldWithPath("data.memberStatus").type(JsonFieldType.STRING).description("회원 상태 : 활동중 / 휴면 상태 / 탈퇴 상태"),
                                fieldWithPath("data.stamp").type(JsonFieldType.NUMBER).description("회원 스탬프 개수")
                        )
                ));
    }
    @Test
    public void getMembersTest() throws Exception {
        // TODO 여기에 MemberController의 getMembers() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
        //given
        Member member1 = new Member("munchkin@cute.cat","먼치킨","010-7777-7777");
        member1.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);
        member1.setStamp(new Stamp());
        Member member2 = new Member("legdoll@cute.cat","렉돌","010-6666-6666");
        member2.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);
        member2.setStamp(new Stamp());

        Page<Member> pageMembers = new PageImpl<>(List.of(member1,member2), PageRequest.of(0,5, Sort.by("memberId").descending()),2);

        List<MemberDto.Response> response = List.of(
                new MemberDto.Response(1L,"munchkin@cute.cat","먼치킨","010-7777-7777", Member.MemberStatus.MEMBER_ACTIVE,new Stamp()),
                new MemberDto.Response(2L,"legdoll@cute.cat","렉돌","010-6666-6666", Member.MemberStatus.MEMBER_ACTIVE,new Stamp())
        );
        //when
        given(memberService.findMembers(Mockito.anyInt(),Mockito.anyInt())).willReturn(pageMembers);

        given(mapper.membersToMemberResponses(Mockito.anyList())).willReturn(response);

        String page = "1";
        String size = "5";

        MultiValueMap<String,String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page",page);
        queryParams.add("size",size);

        ResultActions actions = mockMvc.perform(
                get("/v11/members/")
                        .params(queryParams)
                        .accept(MediaType.APPLICATION_JSON)
        );
        //then
        MvcResult result = actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andDo(document(
                        "get-members",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        requestParameters(
                                parameterWithName("page").description("페이지"),
                                parameterWithName("size").description("페이지 사이즈")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.ARRAY).description("결과 데이터"),
                                fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("data[].email").type(JsonFieldType.STRING).description("회원 이메일"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("data[].phone").type(JsonFieldType.STRING).description("회원 휴대폰 번호"),
                                fieldWithPath("data[].memberStatus").type(JsonFieldType.STRING).description("회원 상태: 활동중 / 휴면 상태 / 탈퇴"),
                                fieldWithPath("data[].stamp").type(JsonFieldType.NUMBER).description("회원 스탬프 갯수"),
                                fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("전체 페이지 정보"),
                                fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("페이지 정보"),
                                fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("사이즈 정보"),
                                fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 정보의 갯수"),
                                fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 갯수")
                        )
                )).andReturn();

        List list = JsonPath.parse(result.getResponse().getContentAsString()).read("$.data");

        assertThat(list.size(),is(2));
    }

    @Test
    public void deleteMemberTest() throws Exception {
        // TODO 여기에 MemberController의 deleteMember() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
        //given
        long memberId = 1L;

        doNothing().when(memberService).deleteMember(memberId);
        //when / then
        mockMvc.perform(
                delete("/v11/members/{memberId}",memberId)
        )
                .andExpect(status().isNoContent())
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
