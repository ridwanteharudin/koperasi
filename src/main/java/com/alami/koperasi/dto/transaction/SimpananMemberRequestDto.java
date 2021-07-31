package com.alami.koperasi.dto.transaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpananMemberRequestDto {
	private Double totalSimpananMember;
	private String tanggalSimpananMember;
	private String memberId;
}
