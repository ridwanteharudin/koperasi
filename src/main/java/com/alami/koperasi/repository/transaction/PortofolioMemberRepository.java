package com.alami.koperasi.repository.transaction;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import com.alami.koperasi.model.transaction.PortofolioMember;

public interface PortofolioMemberRepository extends DataTablesRepository<PortofolioMember, String>{
	PortofolioMember findByMasterMemberMemberId(String memberId);
}
