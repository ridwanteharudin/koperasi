package com.alami.koperasi.dto.member;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MasterMemberDto {
	@Id
	private String memberId;
	private String memberName;
	@Temporal(TemporalType.DATE)
	private Date memberBirthDate;
	
	private String memberMobilePhone;
	private String memberAddress;
	
	private String totalPinjaman;
	private String totalSimpanan;
}
