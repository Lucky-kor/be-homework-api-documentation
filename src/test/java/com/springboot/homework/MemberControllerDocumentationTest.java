package com.springboot.homework;

import com.springboot.exception.BusinessLogicException;
import com.springboot.member.controller.MemberController;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.dto.MemberResponseDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.google.gson.Gson;
import com.springboot.stamp.Stamp;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.springboot.util.ApiDocumentUtils.getResponsePreProcessor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
        // given
        long memberId = 1L;
        Member member = new Member(
                "fukuoka@gmail.com"
        );

        MemberDto.Response response = new MemberDto.Response(
                memberId,
                "fukuoka@gmail.com",
                "후쿠오카",
                "010-1111-2222",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        );
        given(memberService.findMember(memberId)).willReturn(new Member());
        given(mapper.memberToMemberResponse(Mockito.any(Member.class)))
                .willReturn(response);

        // when
        ResultActions actions = mockMvc.perform(
                get("/v11/members/{member-id}",memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(member.getEmail()))
                .andDo(document(
                        "get-member",
                        getResponsePreProcessor(),
                        pathParameters(
                                parameterWithName("member-id").description("회원 식별자(PK)")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별 키").ignored(),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("data.phone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.stamp").type(JsonFieldType.NUMBER).description("스탬프"),
                                        fieldWithPath("data.memberStatus").type(JsonFieldType.STRING).description("회원 상태")
                                )
                        )
                ));

        // then



    }

    @Test
    public void getMembersTest() throws Exception {
        // TODO 여기에 MemberController의 getMembers() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.

        // given

       List<MemberDto.Response> memberResponseDto = List.of(
               new MemberDto.Response(1L,"fukuoka@gmail.com","후쿠오카","010-1234-5678", Member.MemberStatus.MEMBER_ACTIVE,new Stamp()),
               new MemberDto.Response(2L,"tokyo@gmail.com","도쿄","010-9876-5432", Member.MemberStatus.MEMBER_ACTIVE,new Stamp())
       );
        Page<Member> memberPage = new PageImpl<>(List.of(new Member()));

        given(memberService.findMembers(Mockito.anyInt(), Mockito.anyInt())).willReturn(memberPage);
        given(mapper.membersToMemberResponses(Mockito.any())).willReturn(memberResponseDto);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page","1");
        params.add("size","10");

        // when
        ResultActions actions = mockMvc.perform(
                get("/v11/members")
                        .params(params)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
//
        );
        // then
        actions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data[0].email").value(memberResponseDto.get(0).getEmail()))
                .andExpect(jsonPath("$.data[0].name").value(memberResponseDto.get(0).getName()))
                .andExpect(jsonPath("$.data[0].phone").value(memberResponseDto.get(0).getPhone()))
                .andDo(document(
                        "get-members",
                        requestParameters(
                                parameterWithName("page").description("조회할 페이지 번호"),
                                parameterWithName("size").description("한 페이지에 조회할 회원 수")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data[].memberId").description("회원 ID"),
                                fieldWithPath("data[].name").description("회원 이름"),
                                fieldWithPath("data[].email").description("회원 이메일 주소"),
                                fieldWithPath("data[].phone").description("회원 전화번호"),
                                fieldWithPath("data[].memberStatus").description("회원 상태"),
                                fieldWithPath("data[].stamp").description("도장 개수")

                        )
                ));


    }

    @Test
    public void deleteMemberTest() throws Exception {
        // TODO 여기에 MemberController의 deleteMember() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
        // given
        long memberId = 1L;
        given(memberService.findMember(memberId)).willReturn(new Member());

        doNothing().when(memberService).deleteMember(memberId);

        // when
        ResultActions deleteActions = mockMvc.perform(
                delete("/v11/members/{memberId}",memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );
        // then
        deleteActions.andExpect(status().isNoContent())
                .andDo(document(
                        "delete-member",
                        pathParameters(
                                parameterWithName("memberId").description("회원 식별 키")
                        )
                ));

    }
}
