package com.alami.koperasi.dto.transaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionMemberKoperasiRequestDto {
	private Double totalTransactionMemberKoperasi;
	private String transactionDateMemberKoperasi;
	private String transactionTypeMemberKoperasi;
	private String memberId;
	private String memberName;
}
