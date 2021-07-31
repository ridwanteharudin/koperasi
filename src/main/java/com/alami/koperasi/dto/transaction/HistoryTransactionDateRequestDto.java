package com.alami.koperasi.dto.transaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryTransactionDateRequestDto {
	private String startDate;
	private String endDate;
}
