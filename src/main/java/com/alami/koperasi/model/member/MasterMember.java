package com.alami.koperasi.model.member;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import com.alami.koperasi.model.BaseModel;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "MASTER_MEMBER")
@JsonView(DataTablesOutput.View.class)
public class MasterMember extends BaseModel{
	@Id
	private String memberId;
	private String memberName;
	@Temporal(TemporalType.DATE)
	private Date memberBirthDate;
	
	private String memberMobilePhone;
	private String memberAddress;
}
