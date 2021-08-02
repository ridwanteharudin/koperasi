package com.alami.koperasi.controller.member;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alami.koperasi.dto.controller.response.Response;
import com.alami.koperasi.dto.member.MasterMemberDto;
import com.alami.koperasi.dto.member.MasterMemberRequestDto;
import com.alami.koperasi.dto.member.MasterMemberResponseDto;
import com.alami.koperasi.model.member.MasterMember;
import com.alami.koperasi.model.transaction.PortofolioMember;
import com.alami.koperasi.repository.member.MasterMemberRepository;
import com.alami.koperasi.repository.transaction.PortofolioMemberRepository;
import com.alami.koperasi.util.DateUtil;
import com.alami.koperasi.util.UniqueID;

@RestController
@RequestMapping("/rest/member")
public class MemberController {
	@Autowired
	private MasterMemberRepository masterMemberRepository;
	@Autowired
	private PortofolioMemberRepository portofolioMemberRepository;
	
	@RequestMapping(path = "/list", method = RequestMethod.GET)
	public Response<List<MasterMemberDto>> list() {
		DecimalFormat df = new DecimalFormat("#");
		List<MasterMemberDto> listMemberDto = new ArrayList<MasterMemberDto>();
		List<MasterMember> listMasterMember = masterMemberRepository.findAll(new Specification<MasterMember>() {
			@Override
			public Predicate toPredicate(Root<MasterMember> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				// TODO Auto-generated method stub
				return criteriaBuilder.isFalse(root.get("deleted"));
			}
		});
		
		for(MasterMember data : listMasterMember) {
			MasterMemberDto memberDto = new MasterMemberDto();
			memberDto.setMemberId(data.getMemberId());
			memberDto.setMemberAddress(data.getMemberAddress());
			memberDto.setMemberBirthDate(data.getMemberBirthDate());
			memberDto.setMemberName(data.getMemberName());
			memberDto.setMemberMobilePhone(data.getMemberMobilePhone());
			
			PortofolioMember portoMember = portofolioMemberRepository.findByMasterMemberMemberId(data.getMemberId());
			if(portoMember != null) {
				memberDto.setTotalPinjaman(df.format(portoMember.getTotalPinjamanMember()));
				memberDto.setTotalSimpanan(df.format(portoMember.getTotalSimpananMember()));
			}else {
				memberDto.setTotalPinjaman("0");
				memberDto.setTotalSimpanan("0");
			}
			
			listMemberDto.add(memberDto);
		}
		
		Response<List<MasterMemberDto>> response = new Response<List<MasterMemberDto>>(listMemberDto);
		return response;
	}
	
	@RequestMapping(path = "/save", method = RequestMethod.POST)
	public Response<MasterMemberResponseDto> saveproduct(@RequestBody MasterMemberRequestDto dto){
		try {
			MasterMember member = new MasterMember();
		
			if(!StringUtils.hasText(dto.getMemberName())) {
				Response<MasterMemberResponseDto> response = new Response<MasterMemberResponseDto>();
				response.setError("MEMBER_NAME_EMPTY", "Nama tidak boleh kosong");
				return response;
			}
			if(dto.getMemberName().length() < 3) {
				Response<MasterMemberResponseDto> response = new Response<MasterMemberResponseDto>();
				response.setError("MEMBER_NAME_ERROR_LENGTH", "Nama minimal 3 huruf");
				return response;
			}
			if(!StringUtils.hasText(dto.getMemberMobilePhone())) {
				Response<MasterMemberResponseDto> response = new Response<MasterMemberResponseDto>();
				response.setError("MOBILE_PHONE_EMPTY", "Nomor HP tidak boleh kosong");
				return response;
			}
			
			//Update Member
			if(StringUtils.hasText(dto.getMemberId())) {
				Optional<MasterMember> optionalMember = masterMemberRepository.findById(dto.getMemberId());
				if(!optionalMember.isPresent()) {
					Response<MasterMemberResponseDto> response = new Response<MasterMemberResponseDto>();
					response.setError("MEMBER_NOT_FOUND","Anggota tidak ditemukan","memberId",null);
					return response;
				}
				member = optionalMember.get();
			}else {
				member.setMemberId(UniqueID.getUUID());
			}
			
			member.setMemberName(dto.getMemberName());
			member.setMemberMobilePhone(dto.getMemberMobilePhone());
			member.setMemberAddress(dto.getMemberAddress());
			member.setMemberBirthDate(DateUtil.convertDate(dto.getMemberBirthDate()));
			
			masterMemberRepository.save(member);
			
			MasterMemberResponseDto responseDto = new MasterMemberResponseDto();
			responseDto.setResponse("success");
			Response<MasterMemberResponseDto> response = new Response<MasterMemberResponseDto>(responseDto);
			return response;
			
		}catch(Exception e) {
			e.printStackTrace();
			Response<MasterMemberResponseDto> response = new Response<MasterMemberResponseDto>();
			response.setError("ERR_SAVE_MEMBER", e.getMessage());
			return response;
		}
	}
}
