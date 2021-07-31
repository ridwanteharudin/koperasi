package com.alami.koperasi.model;

import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@JsonView(DataTablesOutput.View.class)
public class BaseModel {
	private String createdBy;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	private String modifiedBy;
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;
	private boolean deleted;
}
