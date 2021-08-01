package com.alami.koperasi.controller.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alami.koperasi.dto.controller.response.Response;
import com.alami.koperasi.dto.transaction.BayarPinjamanMemberRequestDto;
import com.alami.koperasi.dto.transaction.BayarPinjamanMemberResponseDto;
import com.alami.koperasi.dto.transaction.HistoryTransactionDateRequestDto;
import com.alami.koperasi.dto.transaction.HistoryTransactionMemberDto;
import com.alami.koperasi.dto.transaction.PinjamanMemberRequestDto;
import com.alami.koperasi.dto.transaction.PinjamanMemberResponseDto;
import com.alami.koperasi.dto.transaction.SimpananMemberRequestDto;
import com.alami.koperasi.dto.transaction.SimpananMemberResponseDto;
import com.alami.koperasi.dto.transaction.TarikSimpananRequestDto;
import com.alami.koperasi.dto.transaction.TarikSimpananResponseDto;
import com.alami.koperasi.model.member.MasterMember;
import com.alami.koperasi.model.transaction.BayarPinjamanMember;
import com.alami.koperasi.model.transaction.PinjamanMember;
import com.alami.koperasi.model.transaction.SimpananMember;
import com.alami.koperasi.model.transaction.TarikSimpananMember;
import com.alami.koperasi.repository.member.MasterMemberRepository;
import com.alami.koperasi.repository.transaction.BayarPinjamanMemberRepository;
import com.alami.koperasi.repository.transaction.PinjamanMemberRepository;
import com.alami.koperasi.repository.transaction.SimpananMemberRepository;
import com.alami.koperasi.repository.transaction.TarikSimpananMemberRepository;
import com.alami.koperasi.util.DateUtil;
import com.alami.koperasi.util.UniqueID;
import com.alami.koperasi.util.constant.transaction.TransactionTypeConstant;

@RestController
@RequestMapping("/rest/transaction")
public class TransactionController {
	@Autowired
	private PinjamanMemberRepository pinjamanMemberRepository;
	@Autowired
	private SimpananMemberRepository simpananMemberRepository;
	@Autowired
	private MasterMemberRepository masterMemberRepository;
	@Autowired
	private BayarPinjamanMemberRepository bayarPinjamanMemberRepository;
	@Autowired
	private TarikSimpananMemberRepository tarikSimpananMemberRepository;
	
	@RequestMapping(path = "/listmembertransaction", method = RequestMethod.GET)
	public Response<List<HistoryTransactionMemberDto>> listTransactionMember(@RequestParam(value="memberId", required = true)String memberId) {
		List<HistoryTransactionMemberDto> listHistoryTransactionDto = new ArrayList<HistoryTransactionMemberDto>();
		
		List<SimpananMember> listSimpananMember = simpananMemberRepository.findAll(new Specification<SimpananMember>() {
			@Override
			public Predicate toPredicate(Root<SimpananMember> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				// TODO Auto-generated method stub
				return criteriaBuilder.and(
						criteriaBuilder.isFalse(root.get("deleted")),
					    criteriaBuilder.equal(root.join("masterMember",JoinType.LEFT).get("memberId"),memberId)
				);
			}
		});
		
		for(SimpananMember data : listSimpananMember) {
			HistoryTransactionMemberDto historyTransactionDto = new HistoryTransactionMemberDto();
			historyTransactionDto.setJenisTransaksi(TransactionTypeConstant.SIMPANAN);
			historyTransactionDto.setTotal(data.getTotalSimpananMember());
			historyTransactionDto.setTanggal(data.getTanggalSimpananMember());
			
			listHistoryTransactionDto.add(historyTransactionDto);
		}
		
		List<TarikSimpananMember> listTarikSimpananMember = tarikSimpananMemberRepository.findAll(new Specification<TarikSimpananMember>() {
			@Override
			public Predicate toPredicate(Root<TarikSimpananMember> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				// TODO Auto-generated method stub
				return criteriaBuilder.and(
						criteriaBuilder.isFalse(root.get("deleted")),
					    criteriaBuilder.equal(root.join("masterMember",JoinType.LEFT).get("memberId"),memberId)
				);
			}
		});
		
		for(TarikSimpananMember data : listTarikSimpananMember) {
			HistoryTransactionMemberDto historyTransactionDto = new HistoryTransactionMemberDto();
			historyTransactionDto.setJenisTransaksi(TransactionTypeConstant.TARIK);
			historyTransactionDto.setTotal(data.getTotalTarikSimpananMember());
			historyTransactionDto.setTanggal(data.getTanggalTarikSimpananMember());
			
			listHistoryTransactionDto.add(historyTransactionDto);
		}
		
		List<PinjamanMember> listPinjamanMember = pinjamanMemberRepository.findAll(new Specification<PinjamanMember>() {
			@Override
			public Predicate toPredicate(Root<PinjamanMember> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				// TODO Auto-generated method stub
				return criteriaBuilder.and(
						criteriaBuilder.isFalse(root.get("deleted")),
					    criteriaBuilder.equal(root.join("masterMember",JoinType.LEFT).get("memberId"),memberId)
				);
			}
		});
		
		for(PinjamanMember data : listPinjamanMember) {
			HistoryTransactionMemberDto historyTransactionDto = new HistoryTransactionMemberDto();
			historyTransactionDto.setJenisTransaksi(TransactionTypeConstant.PINJAMAN);
			historyTransactionDto.setTotal(data.getTotalPinjamanMember());
			historyTransactionDto.setTanggal(data.getTanggalPinjamanMember());
			
			listHistoryTransactionDto.add(historyTransactionDto);
		}
		
		List<BayarPinjamanMember> listBayarPinjamanMember = bayarPinjamanMemberRepository.findAll(new Specification<BayarPinjamanMember>() {
			@Override
			public Predicate toPredicate(Root<BayarPinjamanMember> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				// TODO Auto-generated method stub
				return criteriaBuilder.and(
						criteriaBuilder.isFalse(root.get("deleted")),
					    criteriaBuilder.equal(root.join("masterMember",JoinType.LEFT).get("memberId"),memberId)
				);
			}
		});
		
		for(BayarPinjamanMember data : listBayarPinjamanMember) {
			HistoryTransactionMemberDto historyTransactionDto = new HistoryTransactionMemberDto();
			historyTransactionDto.setJenisTransaksi(TransactionTypeConstant.BAYAR);
			historyTransactionDto.setTotal(data.getTotalBayarPinjamanMember());
			historyTransactionDto.setTanggal(data.getTanggalBayarPinjamanMember());
			
			listHistoryTransactionDto.add(historyTransactionDto);
		}
		
		Response<List<HistoryTransactionMemberDto>> response = new Response<List<HistoryTransactionMemberDto>>(listHistoryTransactionDto);
		return response;
	}
	
	@RequestMapping(path = "/listtransactiondate", method = RequestMethod.POST)
	public Response<List<HistoryTransactionMemberDto>> listTransactionByDate(@RequestBody HistoryTransactionDateRequestDto dto) {
		
		if(!StringUtils.hasText(dto.getStartDate())) {
			Response<List<HistoryTransactionMemberDto>> response = new Response<List<HistoryTransactionMemberDto>>();
			response.setError("START_DATE_EMPTY", "Start Date Id is null or empty");
			return response;
		}
		if(!StringUtils.hasText(dto.getEndDate())) {
			Response<List<HistoryTransactionMemberDto>> response = new Response<List<HistoryTransactionMemberDto>>();
			response.setError("END_DATE_EMPTY", "End Date Id is null or empty");
			return response;
		}
		
		List<HistoryTransactionMemberDto> listHistoryTransactionDto = new ArrayList<HistoryTransactionMemberDto>();
		List<SimpananMember> listSimpananMember = simpananMemberRepository.findAll(new Specification<SimpananMember>() {
			@Override
			public Predicate toPredicate(Root<SimpananMember> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				// TODO Auto-generated method stub
				return criteriaBuilder.and(
						criteriaBuilder.isFalse(root.get("deleted")),
						criteriaBuilder.lessThanOrEqualTo(root.get("tanggalSimpananMember"), DateUtil.convertDate(dto.getEndDate())),
						criteriaBuilder.greaterThanOrEqualTo(root.get("tanggalSimpananMember"), DateUtil.convertDate(dto.getStartDate()))
				);
			}
		});
		
		for(SimpananMember data : listSimpananMember) {
			HistoryTransactionMemberDto historyTransactionDto = new HistoryTransactionMemberDto();
			historyTransactionDto.setJenisTransaksi(TransactionTypeConstant.SIMPANAN);
			historyTransactionDto.setTotal(data.getTotalSimpananMember());
			historyTransactionDto.setTanggal(data.getTanggalSimpananMember());
			
			listHistoryTransactionDto.add(historyTransactionDto);
		}
		
		List<TarikSimpananMember> listTarikSimpananMember = tarikSimpananMemberRepository.findAll(new Specification<TarikSimpananMember>() {
			@Override
			public Predicate toPredicate(Root<TarikSimpananMember> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				// TODO Auto-generated method stub
				return criteriaBuilder.and(
						criteriaBuilder.isFalse(root.get("deleted")),
						criteriaBuilder.lessThanOrEqualTo(root.get("tanggalTarikSimpananMember"), DateUtil.convertDate(dto.getEndDate())),
						criteriaBuilder.greaterThanOrEqualTo(root.get("tanggalTarikSimpananMember"), DateUtil.convertDate(dto.getStartDate()))
				);
			}
		});
		
		for(TarikSimpananMember data : listTarikSimpananMember) {
			HistoryTransactionMemberDto historyTransactionDto = new HistoryTransactionMemberDto();
			historyTransactionDto.setJenisTransaksi(TransactionTypeConstant.TARIK);
			historyTransactionDto.setTotal(data.getTotalTarikSimpananMember());
			historyTransactionDto.setTanggal(data.getTanggalTarikSimpananMember());
			
			listHistoryTransactionDto.add(historyTransactionDto);
		}
		
		List<PinjamanMember> listPinjamanMember = pinjamanMemberRepository.findAll(new Specification<PinjamanMember>() {
			@Override
			public Predicate toPredicate(Root<PinjamanMember> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				// TODO Auto-generated method stub
				return criteriaBuilder.and(
						criteriaBuilder.isFalse(root.get("deleted")),
						criteriaBuilder.lessThanOrEqualTo(root.get("tanggalPinjamanMember"), DateUtil.convertDate(dto.getEndDate())),
						criteriaBuilder.greaterThanOrEqualTo(root.get("tanggalPinjamanMember"), DateUtil.convertDate(dto.getStartDate()))
				);
			}
		});
		
		for(PinjamanMember data : listPinjamanMember) {
			HistoryTransactionMemberDto historyTransactionDto = new HistoryTransactionMemberDto();
			historyTransactionDto.setJenisTransaksi(TransactionTypeConstant.PINJAMAN);
			historyTransactionDto.setTotal(data.getTotalPinjamanMember());
			historyTransactionDto.setTanggal(data.getTanggalPinjamanMember());
			
			listHistoryTransactionDto.add(historyTransactionDto);
		}
		
		List<BayarPinjamanMember> listBayarPinjamanMember = bayarPinjamanMemberRepository.findAll(new Specification<BayarPinjamanMember>() {
			@Override
			public Predicate toPredicate(Root<BayarPinjamanMember> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				// TODO Auto-generated method stub
				return criteriaBuilder.and(
						criteriaBuilder.isFalse(root.get("deleted")),
						criteriaBuilder.lessThanOrEqualTo(root.get("totalBayarPinjamanMember"), DateUtil.convertDate(dto.getEndDate())),
						criteriaBuilder.greaterThanOrEqualTo(root.get("totalBayarPinjamanMember"), DateUtil.convertDate(dto.getStartDate()))
				);
			}
		});
		
		for(BayarPinjamanMember data : listBayarPinjamanMember) {
			HistoryTransactionMemberDto historyTransactionDto = new HistoryTransactionMemberDto();
			historyTransactionDto.setJenisTransaksi(TransactionTypeConstant.BAYAR);
			historyTransactionDto.setTotal(data.getTotalBayarPinjamanMember());
			historyTransactionDto.setTanggal(data.getTanggalBayarPinjamanMember());
			
			listHistoryTransactionDto.add(historyTransactionDto);
		}
		
		Response<List<HistoryTransactionMemberDto>> response = new Response<List<HistoryTransactionMemberDto>>(listHistoryTransactionDto);
		return response;
	}
	
	@RequestMapping(path = "/simpan", method = RequestMethod.POST)
	public Response<SimpananMemberResponseDto> simpan(@RequestBody SimpananMemberRequestDto dto){
		try {
			SimpananMember simpananMember = new SimpananMember();
		
			if(!StringUtils.hasText(dto.getMemberId())) {
				Response<SimpananMemberResponseDto> response = new Response<SimpananMemberResponseDto>();
				response.setError("MEMBER_ID_EMPTY", "Member Id is null or empty");
				return response;
			}
			if(dto.getTotalSimpananMember() == 0) {
				Response<SimpananMemberResponseDto> response = new Response<SimpananMemberResponseDto>();
				response.setError("TOTAL_SIMPANAN_ZERO", "Total Simpanan is zero ");
				return response;
			}
			if(!StringUtils.hasText(dto.getTanggalSimpananMember())) {
				Response<SimpananMemberResponseDto> response = new Response<SimpananMemberResponseDto>();
				response.setError("TANGGAL_IS_EMPTY", "Tanggal is null or empty");
				return response;
			}
			
			Optional<MasterMember> optionalMember = masterMemberRepository.findById(dto.getMemberId());
			if(!optionalMember.isPresent()) {
				Response<SimpananMemberResponseDto> response = new Response<SimpananMemberResponseDto>();
				response.setError("MEMBER_NOT_FOUND","member doesn't exist","memberId",null);
				return response;
			}
				
			simpananMember.setSimpananMemberId(UniqueID.getUUID());
			simpananMember.setMasterMember(optionalMember.get());
			simpananMember.setTanggalSimpananMember(DateUtil.convertDate(dto.getTanggalSimpananMember()));
			simpananMember.setTotalSimpananMember(dto.getTotalSimpananMember());
			
			simpananMemberRepository.save(simpananMember);
			SimpananMemberResponseDto responseDto = new SimpananMemberResponseDto();
			responseDto.setResponse("Success");
			Response<SimpananMemberResponseDto> response = new Response<SimpananMemberResponseDto>(responseDto);
			return response;
			
		}catch(Exception e) {
			e.printStackTrace();
			Response<SimpananMemberResponseDto> response = new Response<SimpananMemberResponseDto>();
			response.setError("ERR_SAVE_SIMPANAN", e.getMessage());
			return response;
		}
	}
	
	@RequestMapping(path = "/pinjam", method = RequestMethod.POST)
	public Response<PinjamanMemberResponseDto> pinjam(@RequestBody PinjamanMemberRequestDto dto){
		try {
			PinjamanMember pinjamanMember = new PinjamanMember();
		
			if(!StringUtils.hasText(dto.getMemberId())) {
				Response<PinjamanMemberResponseDto> response = new Response<PinjamanMemberResponseDto>();
				response.setError("MEMBER_ID_EMPTY", "Member Id is null or empty");
				return response;
			}
			if(dto.getTotalPinjamanMember() == 0) {
				Response<PinjamanMemberResponseDto> response = new Response<PinjamanMemberResponseDto>();
				response.setError("TOTAL_PINJAMAN_ZERO", "Total Pinjaman is zero ");
				return response;
			}
			if(!StringUtils.hasText(dto.getTanggalPinjamanMember())) {
				Response<PinjamanMemberResponseDto> response = new Response<PinjamanMemberResponseDto>();
				response.setError("TANGGAL_IS_EMPTY", "Tanggal is null or empty");
				return response;
			}
			
			Optional<MasterMember> optionalMember = masterMemberRepository.findById(dto.getMemberId());
			if(!optionalMember.isPresent()) {
				Response<PinjamanMemberResponseDto> response = new Response<PinjamanMemberResponseDto>();
				response.setError("MEMBER_NOT_FOUND","member doesn't exist","memberId",null);
				return response;
			}
				
			pinjamanMember.setPinjamanMemberId(UniqueID.getUUID());
			pinjamanMember.setMasterMember(optionalMember.get());
			pinjamanMember.setTanggalPinjamanMember(DateUtil.convertDate(dto.getTanggalPinjamanMember()));
			pinjamanMember.setTotalPinjamanMember(dto.getTotalPinjamanMember());
			
			pinjamanMemberRepository.save(pinjamanMember);
			PinjamanMemberResponseDto responseDto = new PinjamanMemberResponseDto();
			responseDto.setResponse("Success");
			Response<PinjamanMemberResponseDto> response = new Response<PinjamanMemberResponseDto>(responseDto);
			return response;
			
		}catch(Exception e) {
			e.printStackTrace();
			Response<PinjamanMemberResponseDto> response = new Response<PinjamanMemberResponseDto>();
			response.setError("ERR_SAVE_PINJAMAN", e.getMessage());
			return response;
		}
	}
	
	@RequestMapping(path = "/bayar", method = RequestMethod.POST)
	public Response<BayarPinjamanMemberResponseDto> bayar(@RequestBody BayarPinjamanMemberRequestDto dto){
		try {
			BayarPinjamanMember bayarPinjamanMember = new BayarPinjamanMember();
		
			if(!StringUtils.hasText(dto.getMemberId())) {
				Response<BayarPinjamanMemberResponseDto> response = new Response<BayarPinjamanMemberResponseDto>();
				response.setError("MEMBER_ID_EMPTY", "Member Id is null or empty");
				return response;
			}
			if(dto.getTotalBayarPinjamanMember() == 0) {
				Response<BayarPinjamanMemberResponseDto> response = new Response<BayarPinjamanMemberResponseDto>();
				response.setError("TOTAL_BAYAR_PINJAMAN_ZERO", "Total Bayar Pinjaman is zero ");
				return response;
			}
			if(!StringUtils.hasText(dto.getTanggalBayarPinjamanMember())) {
				Response<BayarPinjamanMemberResponseDto> response = new Response<BayarPinjamanMemberResponseDto>();
				response.setError("TANGGAL_IS_EMPTY", "Tanggal is null or empty");
				return response;
			}
			
			Optional<MasterMember> optionalMember = masterMemberRepository.findById(dto.getMemberId());
			if(!optionalMember.isPresent()) {
				Response<BayarPinjamanMemberResponseDto> response = new Response<BayarPinjamanMemberResponseDto>();
				response.setError("MEMBER_NOT_FOUND","member doesn't exist","memberId",null);
				return response;
			}
				
			bayarPinjamanMember.setBayarPinjamanMemberId(UniqueID.getUUID());
			bayarPinjamanMember.setMasterMember(optionalMember.get());
			bayarPinjamanMember.setTanggalBayarPinjamanMember(DateUtil.convertDate(dto.getTanggalBayarPinjamanMember()));
			bayarPinjamanMember.setTotalBayarPinjamanMember(dto.getTotalBayarPinjamanMember());
			
			bayarPinjamanMemberRepository.save(bayarPinjamanMember);
			
			BayarPinjamanMemberResponseDto responseDto = new BayarPinjamanMemberResponseDto();
			responseDto.setResponse("Success");
			Response<BayarPinjamanMemberResponseDto> response = new Response<BayarPinjamanMemberResponseDto>(responseDto);
			return response;
			
		}catch(Exception e) {
			e.printStackTrace();
			Response<BayarPinjamanMemberResponseDto> response = new Response<BayarPinjamanMemberResponseDto>();
			response.setError("ERR_SAVE_BAYAR_PINJAMAN", e.getMessage());
			return response;
		}
	}
	
	@RequestMapping(path = "/tarik", method = RequestMethod.POST)
	public Response<TarikSimpananResponseDto> tarik(@RequestBody TarikSimpananRequestDto dto){
		try {
			TarikSimpananMember tarikSimpananMember = new TarikSimpananMember();
		
			if(!StringUtils.hasText(dto.getMemberId())) {
				Response<TarikSimpananResponseDto> response = new Response<TarikSimpananResponseDto>();
				response.setError("MEMBER_ID_EMPTY", "Member Id is null or empty");
				return response;
			}
			if(dto.getTotalTarikSimpananMember() == 0) {
				Response<TarikSimpananResponseDto> response = new Response<TarikSimpananResponseDto>();
				response.setError("TOTAL_TARIK_SIMPANAN_ZERO", "Total Tarik Simpanan is zero ");
				return response;
			}
			if(!StringUtils.hasText(dto.getTanggalTarikSimpananMember())) {
				Response<TarikSimpananResponseDto> response = new Response<TarikSimpananResponseDto>();
				response.setError("TANGGAL_IS_EMPTY", "Tanggal is null or empty");
				return response;
			}
			
			Optional<MasterMember> optionalMember = masterMemberRepository.findById(dto.getMemberId());
			if(!optionalMember.isPresent()) {
				Response<TarikSimpananResponseDto> response = new Response<TarikSimpananResponseDto>();
				response.setError("MEMBER_NOT_FOUND","member doesn't exist","memberId",null);
				return response;
			}
				
			tarikSimpananMember.setTarikSimpananMemberId(UniqueID.getUUID());
			tarikSimpananMember.setMasterMember(optionalMember.get());
			tarikSimpananMember.setTanggalTarikSimpananMember(DateUtil.convertDate(dto.getTanggalTarikSimpananMember()));
			tarikSimpananMember.setTotalTarikSimpananMember(dto.getTotalTarikSimpananMember());
			
			tarikSimpananMemberRepository.save(tarikSimpananMember);
			
			TarikSimpananResponseDto responseDto = new TarikSimpananResponseDto();
			responseDto.setResponse("Success");
			Response<TarikSimpananResponseDto> response = new Response<TarikSimpananResponseDto>(responseDto);
			return response;
			
		}catch(Exception e) {
			e.printStackTrace();
			Response<TarikSimpananResponseDto> response = new Response<TarikSimpananResponseDto>();
			response.setError("ERR_SAVE_BAYAR_PINJAMAN", e.getMessage());
			return response;
		}
	}
}
