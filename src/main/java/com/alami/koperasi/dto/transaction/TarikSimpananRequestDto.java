package com.alami.koperasi.dto.transaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TarikSimpananRequestDto {
	private Double totalTarikSimpananMember;
	private String tanggalTarikSimpananMember;
	private String memberId;
}
