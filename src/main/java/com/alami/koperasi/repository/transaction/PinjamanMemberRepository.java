package com.alami.koperasi.repository.transaction;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import com.alami.koperasi.model.transaction.PinjamanMember;

public interface PinjamanMemberRepository extends DataTablesRepository<PinjamanMember, String>{

}
