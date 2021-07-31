package com.alami.koperasi.dto.member;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MasterMemberRequestDto {
	private String memberId;
	private String memberName;
	private String memberBirthDate;
	private String memberMobilePhone;
	private String memberAddress;
}
