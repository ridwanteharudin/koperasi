package com.alami.koperasi.dto.transaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PinjamanMemberRequestDto {
	private Double totalPinjamanMember;
	private String tanggalPinjamanMember;
	private String memberId;
}
