package com.alami.koperasi.repository.member;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import com.alami.koperasi.model.member.MasterMember;

public interface MasterMemberRepository extends DataTablesRepository<MasterMember, String>{

}
