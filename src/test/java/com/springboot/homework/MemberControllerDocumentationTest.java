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

import javax.xml.transform.Result;

import java.util.List;

import static com.springboot.util.ApiDocumentUtils.getRequestPreProcessor;
import static com.springboot.util.ApiDocumentUtils.getResponsePreProcessor;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
        // TODO 여기에 MemberController의 getMember() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
        // given
        long memberId = 1L;
        MemberDto.Response response = new MemberDto.Response(1L, "taek@gmail.com", "택", "010-2401-4188", Member.MemberStatus.MEMBER_ACTIVE, new Stamp());

        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());
        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        // when
        ResultActions actions = mockMvc.perform(
                get("/v11/members/{memberId}", memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(response.getEmail()))
                .andDo(document("get-member",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        pathParameters(
                                parameterWithName("memberId").description("회원 식별자")
                        ),
                        responseFields(
                                List.of(fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 데이터"),
                                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("data.phone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                                        fieldWithPath("data.memberStatus").type(JsonFieldType.STRING).description("회원 상태 : 활동중 / 휴면 / 탈퇴"),
                                        fieldWithPath("data.stamp").type(JsonFieldType.NUMBER).description("스탬프"))
                        )
                ));



    }

    @Test
    public void getMembersTest() throws Exception {
        // TODO 여기에 MemberController의 getMembers() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
        List<MemberDto.Response> members = List.of(
                new MemberDto.Response(1L, "taek@gmail.com", "택", "010-2401-4188", Member.MemberStatus.MEMBER_ACTIVE, new Stamp()),
                new MemberDto.Response(2L, "taek2@gmail.com", "택2", "010-2401-4182", Member.MemberStatus.MEMBER_ACTIVE, new Stamp())
        );
        Page<Member> memberPage = new PageImpl<>(List.of(new Member(), new Member()));

        given(memberService.findMembers(Mockito.anyInt(), Mockito.anyInt())).willReturn(memberPage);
        given(mapper.membersToMemberResponses(Mockito.anyList())).willReturn(members);

        // when
        ResultActions actions = mockMvc.perform(
                get("/v11/members")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("page", "1")
                        .param("size", "2")
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(members.size()))
                .andDo(document("get-members",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        requestParameters(parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지당 개수")),
                        responseFields(
                                List.of(fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("결과 데이터"),
                                        fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("data[].email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data[].name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("data[].phone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                                        fieldWithPath("data[].memberStatus").type(JsonFieldType.STRING).description("회원 상태 : 활동중 / 휴면 / 탈퇴"),
                                        fieldWithPath("data[].stamp").type(JsonFieldType.NUMBER).description("스탬프 갯수"),
                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수")
                                ))));



    }

    @Test
    public void deleteMemberTest() throws Exception {
        // TODO 여기에 MemberController의 deleteMember() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
        // given
        long memberId = 1L;

        // void 일때는 doNothing
        doNothing().when(memberService).deleteMember(Mockito.anyLong());

        // when
        ResultActions deleteActions = mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/v11/members/{memberId}", memberId)
        );

        // then
        deleteActions.andExpect(status().isNoContent())
                .andDo(document("delete-member",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        pathParameters(parameterWithName("memberId").description("삭제할 회원의 식별자"))
                ));
    }
}
