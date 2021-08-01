package com.alami.koperasi.model.transaction;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import com.alami.koperasi.model.BaseModel;
import com.alami.koperasi.model.member.MasterMember;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "PORTOFOLIO_MEMBER")
@JsonView(DataTablesOutput.View.class)
public class PortofolioMember extends BaseModel{
	@Id
	private String portofolioMemberId;
	
	private Double totalSimpananMember;
	private Double totalPinjamanMember;
	@ManyToOne
	@JoinColumn(name="member_id")
	private MasterMember masterMember;
}
