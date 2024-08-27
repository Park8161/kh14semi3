package com.kh.kh14semi3.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.kh14semi3.configuration.CustomCertProperties;
import com.kh.kh14semi3.dao.CertDao;
import com.kh.kh14semi3.dao.MemberDao;
import com.kh.kh14semi3.dto.CertDto;
import com.kh.kh14semi3.dto.MemberDto;
import com.kh.kh14semi3.error.TargetNotFoundException;
import com.kh.kh14semi3.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller

@RequestMapping("/member")

public class MemberController {
	@Autowired
	private PasswordEncoder encoder; 
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private MemberDao memberDao;
	//로그인
	@GetMapping("/login")
	public String login() {
		return "/WEB-INF/views/member/login.jsp";
	}
	@PostMapping("/login")
	public String login(@RequestParam String memberId,
								@RequestParam String memberPw,
								@RequestParam(required = false) String remember,//아이디 저장하기 기능넣을시 활용용
								HttpSession session, HttpServletResponse response) {
		//1. 아이디에 해당하는 정보(MemberDto)를 불러옴
		//->없으면 실패
		//2. MemberDto와 비밀번호를 비교
		//->안맞으면 실패
		//3.차단회원 차단 되게 
		//4. 1,2,3 다 성공시 로그인 성공
		
		//1번
		MemberDto memberDto = memberDao.selectOne(memberId);
		if(memberDto == null) return "redirect:/member/login?error"; //redirect는 get으로 감
		
		//2번
//		boolean isValid = memberPw.equals(memberDto.getMemberPw()); //암호화변되면 equals 못씀 쓰면 false
//		boolean isValid = encoder.matches(memberDto.getMemberPw(), encrypt);
//		if(isValid == false) return "redirect:/member/login?error"; //
		
		
		String rawPw = memberDto.getMemberPw(); // 비밀번호 암호화안된것
		String encPw = encoder.encode(rawPw); // 암호화된 비밀번호

		boolean isValid = encoder.matches(memberPw,encPw);
		if (!isValid) {
		    return "redirect:/member/login?error";
		}
		
//		//3번 차단
//		BlockDto blockDto = blockDao.selectLastOne(memberId);
//		boolean isBlock = blockDto != null && blockDto.getBlockType().equals("차단");
//		if (isBlock)
//			return "redirect:/member/block";
		
		//4번
		session.setAttribute("createdUser", memberId);
		session.setAttribute("createdRank", memberDto.getMemberRank()); //? 관리자 메뉴추가할때 썼던 코드
//		memberDao.updateMemberLogin(memberId); 최종 로그인 시각이 반영되도록 할때 필요한 코드
		
		//쿠키를 사용한 아이디저장 기능 코드
		if(remember != null) {//아이디저장체크o
			Cookie ck = new Cookie("saveId", memberId);//쿠키생성
			ck.setPath("/");
			ck.setMaxAge(4 * 7 * 24 * 60 * 60); //기간4주
			response.addCookie(ck);
		}
		else {//아이디저장체크x
			Cookie ck = new Cookie("saveId", memberId);//쿠키생성
			ck.setMaxAge(0); //0초=삭제
			response.addCookie(ck);
		}

		
		return "redirect:/home/main"; //성공시 메인으로
}
	
	//로그아웃 필요없으면 주석 처리하세용
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("createdUser");
		session.removeAttribute("createdRank"); // ? 관리자 메뉴추가 할때 썻던 코드
		return "redirect:/";
	}
	
	//비밀번호 찾기(재설정 링크 방식)
	@GetMapping("/findPw")
	public String findPw() {
		return "/WEB-INF/views/member/findPw.jsp";
	}
	@PostMapping("/findPw")
	public String findPw(@RequestParam String memberId,
									@RequestParam String memberEmail) throws IOException, MessagingException {
		
		//아이디로 회원 정보 조회
		MemberDto memberDto = memberDao.selectOne(memberId);
		if(memberDto == null) {
			return"redirect:findPw?error";
		}
		//이메일비교
		if(!memberEmail.equals(memberDto.getMemberEmail())) {
			return "redirect:findPw?error";
		}
		//템플릿을 불러와 재설정메일발송
		emailService.sendResetPw(memberId,memberEmail);
		
		return "redirect:findPwFinish";
	}
	@RequestMapping("/findPwFinish")
	public String findPwFinish() {
		return "/WEB-INF/views/member/findPwFinish.jsp";
	}
	
	@Autowired
	private CertDao certDao;
	
	@Autowired
	private CustomCertProperties customCertProperties;
	
	//비밀번호 재설정 페이지
	@GetMapping("/resetPw") 
	public String resetPw(@ModelAttribute CertDto certDto,
									@RequestParam String memberId,
									Model model) {
		boolean isValid = certDao.check(certDto, customCertProperties.getExpire());
		if(isValid) {			
			model.addAttribute("certDto", certDto);
			model.addAttribute("memberId", memberId);
			return "/WEB-INF/views/member/resetPw.jsp";
		}
		else {
			throw new TargetNotFoundException("올바르지 않은 접근");
		}
	}
	@PostMapping("/resetPw")
	public String resetPw(@ModelAttribute CertDto certDto,
									@ModelAttribute MemberDto memberDto) {
		//인증정보확인
		boolean isValid = certDao.check(certDto, customCertProperties.getExpire());
		if(!isValid) {
			throw new TargetNotFoundException("올바르지 않은 접근");
		}
		//인증성공시 인증번호 삭제(1회접근페이지)
		certDao.delete(certDto.getCertEmail());
		
		//비밀번호변경
		memberDao.updateMemberPw(
				memberDto.getMemberId(), memberDto.getMemberPw());
		return "redirect:resetPwFinish";
	}
	@RequestMapping("/resetPwFinish")
	public String resetPwFinish() {
		return "/WEB-INF/views/member/resetPwFinish.jsp";
	}
}
