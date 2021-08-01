package com.alami.koperasi.model.transaction;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import com.alami.koperasi.model.BaseModel;
import com.alami.koperasi.model.member.MasterMember;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "BAYAR_PINJAMAN_MEMBER")
@JsonView(DataTablesOutput.View.class)
public class BayarPinjamanMember extends BaseModel{
	@Id
	private String bayarPinjamanMemberId;
	
	private Double totalBayarPinjamanMember;
	@Temporal(TemporalType.DATE)
	private Date tanggalBayarPinjamanMember;
	@ManyToOne
	@JoinColumn(name="member_id")
	private MasterMember masterMember;

}
