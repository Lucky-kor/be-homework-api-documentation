package com.springboot.restdocs.member;

import com.springboot.member.controller.MemberController;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.springboot.stamp.Stamp;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.springboot.util.ApiDocumentUtils.getRequestPreProcessor;
import static com.springboot.util.ApiDocumentUtils.getResponsePreProcessor;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class MemberControllerRestDocsTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    // 비어있음 / 계층분리하기위해
    @MockBean
    private MemberMapper mapper;

    @Autowired
    private Gson gson;

    @Test
    public void postMemberTest() throws Exception {
        // given
        // post 객체를 포스트할 데이터를 담아서 생성했음.
        MemberDto.Post post = new MemberDto.Post("hgd@gmail.com", "홍길동", "010-1234-5678");
        // post 객채를 json 타입으로 변환해줌
        String content = gson.toJson(post);
        // 응답 하려고 responseDto 객체를 post 한객체에 데이터와 똑같이 생성.
        MemberDto.Response responseDto =
                new MemberDto.Response(1L,
                        "hgd@gmail.com",
                        "홍길동",
                        "010-1234-5678",
                        Member.MemberStatus.MEMBER_ACTIVE,
                        new Stamp());



        // willReturn()이 최소한 null은 아니어야 한다.
        // given을 사용하여 빈 목객체로 mapper를 통해 변환되는지 확인만 하는것. new Member() 반환하는것도 안쓸꺼기 때문.
        // 실제로 디버깅 해보면 데이터도 아무것도 안들어감.
        given(mapper.memberPostToMember(Mockito.any(MemberDto.Post.class))).willReturn(new Member());

        Member mockResultMember = new Member();
        mockResultMember.setMemberId(1L);
        given(memberService.createMember(Mockito.any(Member.class))).willReturn(mockResultMember);

        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(responseDto);

        // when 검사를 할 부분
        ResultActions actions =
                mockMvc.perform(
                        post("/v11/members")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        // then 뭘 확인할서냐
        actions
                .andExpect(status().isCreated()) // HTTP 상태코드가 isCreated 여야함
                .andExpect(header().string("Location", is(startsWith("/v11/members/")))) // 경로가 /v11/members/ 로 시작해야함
                .andDo(document("post-member", // API 문서화 작업 시작 지점. post-member 가 문서의 식별자
                        getRequestPreProcessor(), // 요청 전처리기(Response Preprocessor)를 설정, 이를 통해서 요청 본문을 보기 좋게 정리
                        // 정렬: JSON 응답을 보기 좋게 정렬.
                        // 마스킹: 민감한 데이터를 마스킹하거나 제거.
                        // 필터링: 필요 없는 필드를 제거.
                        getResponsePreProcessor(), // 응답 전처리기를 설정, 이를 통해 응답 본문을 보기 좋게 처리
                        requestFields( // 요청 필드들을 문서화하는 작업
                                List.of(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"), // 이메일 필드의 타입과 설명을 지정.
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("phone").type(JsonFieldType.STRING).description("휴대폰 번호")
                                )
                        ),
                        responseHeaders( // 응답 헤더를 문서화하는 부분
                                headerWithName(HttpHeaders.LOCATION).description("Location header. 등록된 리소스의 URI")
                        )
                ));
    }

    @Test
    public void patchMemberTest() throws Exception {
        // given
        long memberId = 1L;
        MemberDto.Patch patch = new MemberDto.Patch(memberId, "홍길동", "010-1111-1111", Member.MemberStatus.MEMBER_ACTIVE);
        String content = gson.toJson(patch);

        MemberDto.Response responseDto =
                new MemberDto.Response(1L,
                        "hgd@gmail.com",
                        "홍길동",
                        "010-1111-1111",
                        Member.MemberStatus.MEMBER_ACTIVE,
                        new Stamp());


        // willReturn()이 최소한 null은 아니어야 한다.
        // 통신만 되는지 확인.
        // new Member() 를 봔환하는 이유: 안쓸꺼기 때문 어차피 빈객체임.
        // given: Mockito 를 사용하여 목 객체의 동작을 설정
        // 아래의 코드는 mapper 객체의 memberPatchToMember 메서드가 호출될때 어떤 타입의 객체가 전달되어도 '특정' new Member() 반환하도록 정하는것.
        // 테스트시 mapper 객체의 실제 동작을 대체하여 예측 가능한 결과를 반환하도록 한다.
        // Mock 사용이유: 의존성 격리 테스트 대상 객체가 외부 의존성(예: 데이터베이스, 웹 서비스)에 영향을 받지 않도록 격리
        // 검증 방법: Mockito를 사용한 테스트에서는 실제 동작을 하여, 타입변환을 하고, 서비스계층에 접근하지 않지만. 실제로 메서드의 동작이나 통신이 제대로 이루어지는지만 확인한다.
        // 1. 특정 메서드가 호출되었는지 검증
        // 2. Mockito의 given().willReturn()을 사용하여 예상된 값을 반환하도록 설정한 후, 그 반환 값을 검증
        // 3. 메서드 호출 시 전달된 인자를 검증합니다.
        given(mapper.memberPatchToMember(Mockito.any(MemberDto.Patch.class))).willReturn(new Member());

        given(memberService.updateMember(Mockito.any(Member.class))).willReturn(new Member());

        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(responseDto);

        // when
        ResultActions actions =
                mockMvc.perform(
                            patch("/v11/members/{member-id}", memberId)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(patch.getMemberId()))
                .andExpect(jsonPath("$.data.name").value(patch.getName()))
                .andExpect(jsonPath("$.data.phone").value(patch.getPhone()))
                .andExpect(jsonPath("$.data.memberStatus").value(patch.getMemberStatus().getStatus()))
                .andDo(document("patch-member",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        pathParameters(
                                parameterWithName("member-id").description("회원 식별자")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자").ignored(),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름").optional(),
                                        fieldWithPath("phone").type(JsonFieldType.STRING).description("휴대폰 번호").optional(),
                                        fieldWithPath("memberStatus").type(JsonFieldType.STRING).description("회원 상태: MEMBER_ACTIVE / MEMBER_SLEEP / MEMBER_QUIT").optional()
                                )
                        ),
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
}
