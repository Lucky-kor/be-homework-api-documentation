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

import java.util.ArrayList;
import java.util.List;

import static com.springboot.util.ApiDocumentUtils.getRequestPreProcessor;
import static com.springboot.util.ApiDocumentUtils.getResponsePreProcessor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.hamcrest.Matchers.is;
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
//      given 준비
//      SingleResponseDto안에 들어가는 정보
//        SingleResponseDto가 없으면 이렇게 오는데

//        {
//         "memberId": 1,
//        "email": "agag@naver.com",
//        "name": "김영진",
//        "phone": "010-4525-2525",
//        "memberStatus": "활동중",
//        "stamp": 0
//    }

//        "data" 안에 있다고 명시해 주기위해 responseDto로 감싸줌
//        {
//    "data": {
//        "memberId": 1,
//        "email": "agag@naver.com",
//        "name": "김영진",
//        "phone": "010-4525-2525",
//        "memberStatus": "활동중",
//        "stamp": 0
//    }

        long memberId = 1;
        MemberDto.Response response = new MemberDto.Response(
                1,
                "skskal@naver.com",
                "라떼",
                "010-4525-2727",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        );

        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());

        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);
//      when 실행

        ResultActions getAction = mockMvc.perform(
//            컨트롤러 계층 getMember @PathVariable("member-id") url을 맞춰 줘야하기때문
                get("/v11/members/{member-id}", memberId)
//                    요청이 들어 왔을 때 탙입은 JSON
                        .accept(MediaType.APPLICATION_JSON)
//            get 요청은 따로 body에 컨텐츠를 담아서 보내지 않으므로
        );


//      then 검증
        getAction.andExpect(status().isOk())
//                1)get요청이 끝나서 http상태 코드의 결과
//                기댓값 jsonPath($.data.name).value(비교대상.getName())
                .andExpect(jsonPath("$.data.name").value(response.getName()))
                .andExpect(jsonPath("$.data.email").value(response.getEmail()))
                .andExpect(jsonPath("$.data.phone").value(response.getPhone()))

                //    "data": {
//        "memberId": 1,
//        "email": "agag@naver.com",
//        "name": "김영진",
//        "phone": "010-4525-2525",
//        "memberStatus": "활동중",
//        "stamp": 0
//    }
//                .and() 검증이 끝나고 무언가를 할것이다
//
                .andDo(
//                        document( "문서제목 , -> 문서화
//                        (필수) 전처리 작업
//                        getRequestPreProcessor(),
//                        getResponsePreProcessor(),
                        document("get-member",
                                getRequestPreProcessor(),
                                getResponsePreProcessor(),
//                                pathParameters 이름
//                                Parameter가 들어가니까
                                pathParameters(
                                        parameterWithName("member-id").description("회원 식별자")
                                ),
//                                요청이 끝나고 response 되는 정보들에 대한 fields 값
                                responseFields(
//                                        필드에 들어오는 경로에 값과 타입을 fieldWithPath("경로").type.("타입") 맞춰서 적어주고 .description(설명)
//
//                                        Json의 데이터 타입
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("회원 조회 정보"),
                                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("회원 이메일"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("회원 이름"),
                                        fieldWithPath("data.phone").type(JsonFieldType.STRING).description("회원 전화번호"),
                                        fieldWithPath("data.memberStatus").type(JsonFieldType.STRING).description("회원 상태 : 활동중 / 휴면 / 탈퇴"),
                                        fieldWithPath("data.stamp").type(JsonFieldType.NUMBER).description("스템프 개수")
                                )
                        ));

//                2) 1번의 상태가 맞다면 다음 검증 실행 현재 가져온 데이터가
        // TODO 여기에 MemberController의 getMember() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.

    }

    @Test
    public void getMembersTest() throws Exception {
//        given 준비
//        memberController getMembers에 필요한 정보들
//        page정보, member를 담은 List
//        mapper, service

//        응답올 body에 담길 List<responseDto>의 정보를 포함하여 만들어 줌
        Member member1 = new Member("ssss@ssss.com", "라떼", "010-1111-1111");
        member1.setMemberId(1L);
        member1.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);
        new Stamp();

        Member member2 = new Member("aaaa@aaaa.com", "미미", "010-2222-2222");
        member1.setMemberId(2L);
        member1.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);
        new Stamp();

//        {
//    "data": {
//        "memberId": 1,
//        "email": "agag@naver.com",
//        "name": "김영진",
//        "phone": "010-4525-2525",
//        "memberStatus": "활동중",
//        "stamp": 0
//    }
//}
//      MemberController getMembers 파라미터를 page, size 2개를 받고 있고 MultivalueMap<String,String> queryParam = new LinkedMultiValueMap 으로
//      localhost:8080/v11/members?page=1&size=5
//      getMembers()안에서 사용할 Page<Member>, List<Member>
//      Page내에 들어가야할 정보는 List<data> , pageRequest 가 있어야하고 new PageImpl<>(리스트,pageRequest)
        Page<Member> pageMember = new PageImpl<>(List.of(member1, member2), PageRequest.of(0, 5, Sort.by("memberId").descending()), 2);
//       List<MemberDto.Response> responses = List.of(
//       new MemberDto.Response(), new MemberDto.Response())로 만들어 줌  membersToMemberResponses(ArrayList<>();) -> mapper 에서 사용할 객체를 만들어 줘야함
        List<MemberDto.Response> responses = List.of(
                new MemberDto.Response(1L, "ssss@ssss.com", "라떼", "010-1111-1111",
                        Member.MemberStatus.MEMBER_ACTIVE, new Stamp()),
                new MemberDto.Response(2L, "aaaa@aaaa.com", "미미", "010-2222-2222",
                        Member.MemberStatus.MEMBER_ACTIVE, new Stamp())
        );
//      findMembers안에 들어갈 매개변수 타입이 int 2개 이므로 가짜객체로 받아야 함
        given(memberService.findMembers(Mockito.anyInt(), Mockito.anyInt())).willReturn(pageMember);
        given(mapper.membersToMemberResponses(Mockito.anyList())).willReturn(responses);
//        url뒤에 mockMvc.queryParams() 메서드를 사용할 때 넣어줄 페이지에 대한 정보를 멀티벨류맵으로 제작
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        String page = "1";
        String size = "5";
        queryParams.add("page", "1");
        queryParams.add("size", "5");

//        when 실행
//        실행의 값을 담을 ResultActions 안에서 mockMvc로 실행
        ResultActions getMembersAction =
                mockMvc.perform(
                        get("/v11/members")
//                                .queryParams(MultivalueMap 이 들어간다..)
//                        localhost:8080/v11/members?page=1&size=5 요청을 실행할 url
                                .queryParams(queryParams)
                                .accept(MediaType.APPLICATION_JSON)
                );
//        현재 요청에 대한 응답이
//        {
//            "data": [
//            {
//                "memberId": 2,
//                    "email": "agaqg@naver.com",
//                    "name": "김영진",
//                    "phone": "010-4526-2525",
//                    "memberStatus": "활동중",
//                    "stamp": 0
//            },
//            {
//                "memberId": 1,
//                    "email": "agag@naver.com",
//                    "name": "김영진",
//                    "phone": "010-4525-2525",
//                    "memberStatus": "활동중",
//                    "stamp": 0
//            }
//    ],
//            "pageInfo": {
//            "page": 1,
//                    "size": 5,
//                    "totalElements": 2,
//                    "totalPages": 1
//        }
//        }
       MvcResult result =
               getMembersAction
                       .andExpect(status().isOk())
//                jsonPath - "data" 로들어오는 정보가 너무 많아서 우선 배열인지만 확인했다.
                .andExpect(jsonPath("data").isArray())
//                검증이 끝나고 실행할 것이다 document("이름",전처리 작업, 들어오는 parameter, 각 설명)
                .andDo(document("get-members",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
//                        요청올 때 매개변수에 대해 설명 해줄거야 requestParameters
                        requestParameters(
                                List.of(
//                                        매개변수에 대한 이름 및 설명 parameterWithName
                                        parameterWithName("page").description("페이지"),
                                        parameterWithName("size").description("사이즈")
                                )
                        ),
                        requestFields(
                                List.of(
//                                        필드 경로에 대한 이름 및 설명
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("전체 데이터"),
//                                        현재 배열로 들어오고 있기떄문에 명시해줌
//                                        필드 경로에 들어오는 경로에 대한 설명
                                        fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("data[].email").type(JsonFieldType.STRING).description("회원 이메일"),
                                        fieldWithPath("data[].name").type(JsonFieldType.STRING).description("회원 이름"),
                                        fieldWithPath("data[].phone").type(JsonFieldType.STRING).description("회원 휴대폰번호"),
                                        fieldWithPath("data[].memberStatus").type(JsonFieldType.STRING)
                                                .description("회원 상태 : 활동중 / 휴면 / 탈퇴"),
                                        fieldWithPath("data[].stamp").type(JsonFieldType.NUMBER).description("스탬프 개수"),
                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("전체 페이지 정보"),
                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("페이지의 수"),
                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지의 사이즈"),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("컨텐츠"),
                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 정보")
                                        )

                        )
                )).andReturn();

        List list = JsonPath.parse(result.getResponse().getContentAsString()).read("$.data");

        assertThat(list.size(),is(2));

    }

    @Test
    public void deleteMemberTest() throws Exception {
        // TODO 여기에 MemberController의 deleteMember() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
    }
}
