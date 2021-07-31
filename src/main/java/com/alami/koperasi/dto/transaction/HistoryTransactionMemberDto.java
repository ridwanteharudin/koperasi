package com.alami.koperasi.dto.transaction;

import java.util.Date;

import com.alami.koperasi.serializer.JsonDateDeserializer;
import com.alami.koperasi.serializer.JsonDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryTransactionMemberDto {
	private Double total;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date tanggal;
	private String jenisTransaksi;
}
