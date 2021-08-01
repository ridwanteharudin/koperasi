package com.alami.koperasi.dto.transaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BayarPinjamanMemberRequestDto {
	private Double totalBayarPinjamanMember;
	private String tanggalBayarPinjamanMember;
	private String memberId;
}
