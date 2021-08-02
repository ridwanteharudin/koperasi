package com.alami.koperasi.controller.transaction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
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
import com.alami.koperasi.dto.transaction.HistoryTransactionDateResponseDto;
import com.alami.koperasi.dto.transaction.HistoryTransactionMemberDto;
import com.alami.koperasi.dto.transaction.PinjamanMemberRequestDto;
import com.alami.koperasi.dto.transaction.PinjamanMemberResponseDto;
import com.alami.koperasi.dto.transaction.SimpananMemberRequestDto;
import com.alami.koperasi.dto.transaction.SimpananMemberResponseDto;
import com.alami.koperasi.dto.transaction.TarikSimpananRequestDto;
import com.alami.koperasi.dto.transaction.TarikSimpananResponseDto;
import com.alami.koperasi.dto.transaction.TransactionMemberKoperasiRequestDto;
import com.alami.koperasi.model.member.MasterMember;
import com.alami.koperasi.model.transaction.BayarPinjamanMember;
import com.alami.koperasi.model.transaction.PinjamanMember;
import com.alami.koperasi.model.transaction.PortofolioMember;
import com.alami.koperasi.model.transaction.SimpananMember;
import com.alami.koperasi.model.transaction.TarikSimpananMember;
import com.alami.koperasi.repository.member.MasterMemberRepository;
import com.alami.koperasi.repository.transaction.BayarPinjamanMemberRepository;
import com.alami.koperasi.repository.transaction.PinjamanMemberRepository;
import com.alami.koperasi.repository.transaction.PortofolioMemberRepository;
import com.alami.koperasi.repository.transaction.SimpananMemberRepository;
import com.alami.koperasi.repository.transaction.TarikSimpananMemberRepository;
import com.alami.koperasi.util.DateUtil;
import com.alami.koperasi.util.UniqueID;
import com.alami.koperasi.util.constant.transaction.TransactionTypeConstant;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	@Autowired
	private PortofolioMemberRepository portofolioMemberRepository;
	
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@Autowired
	TransactionController(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	@Value("${kafka.topic:test}")
    private String topic;
	
	private void sendMessage(String message, String topicName) {
		kafkaTemplate.send(topicName, message);
	}

	@RequestMapping(path = "/listmembertransaction", method = RequestMethod.GET)
	public Response<List<HistoryTransactionMemberDto>> listTransactionMember(@RequestParam(value="memberId", required = true)String memberId) {
		
		List<HistoryTransactionMemberDto> listHistoryTransactionDto = new ArrayList<HistoryTransactionMemberDto>();
		DecimalFormat df = new DecimalFormat("#");
		
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
			historyTransactionDto.setTotal(df.format(data.getTotalSimpananMember()));
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
			historyTransactionDto.setTotal(df.format(data.getTotalTarikSimpananMember()));
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
			historyTransactionDto.setTotal(df.format(data.getTotalPinjamanMember()));
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
			historyTransactionDto.setTotal(df.format(data.getTotalBayarPinjamanMember()));
			historyTransactionDto.setTanggal(data.getTanggalBayarPinjamanMember());
			
			listHistoryTransactionDto.add(historyTransactionDto);
		}
		
		Response<List<HistoryTransactionMemberDto>> response = new Response<List<HistoryTransactionMemberDto>>(listHistoryTransactionDto);
		return response;
	}
	
	@RequestMapping(path = "/listtransactiondate", method = RequestMethod.POST)
	public Response<List<HistoryTransactionDateResponseDto>> listTransactionByDate(@RequestBody HistoryTransactionDateRequestDto dto) {
		
		DecimalFormat df = new DecimalFormat("#");
		
		if(!StringUtils.hasText(dto.getStartDate())) {
			Response<List<HistoryTransactionDateResponseDto>> response = new Response<List<HistoryTransactionDateResponseDto>>();
			response.setError("START_DATE_EMPTY", "Tanggal mulai tidak boleh kosong");
			return response;
		}
		if(!StringUtils.hasText(dto.getEndDate())) {
			Response<List<HistoryTransactionDateResponseDto>> response = new Response<List<HistoryTransactionDateResponseDto>>();
			response.setError("END_DATE_EMPTY", "Tanggal akhir tidak boleh kosong");
			return response;
		}
		
		List<HistoryTransactionDateResponseDto> listHistoryTransactionDto = new ArrayList<HistoryTransactionDateResponseDto>();
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
			HistoryTransactionDateResponseDto historyTransactionDto = new HistoryTransactionDateResponseDto();
			historyTransactionDto.setJenisTransaksi(TransactionTypeConstant.SIMPANAN);
			historyTransactionDto.setTotal(df.format(data.getTotalSimpananMember()));
			historyTransactionDto.setTanggal(data.getTanggalSimpananMember());
			historyTransactionDto.setMemberId(data.getMasterMember().getMemberId());
			historyTransactionDto.setMemberName(data.getMasterMember().getMemberName());
			
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
			HistoryTransactionDateResponseDto historyTransactionDto = new HistoryTransactionDateResponseDto();
			historyTransactionDto.setJenisTransaksi(TransactionTypeConstant.TARIK);
			historyTransactionDto.setTotal(df.format(data.getTotalTarikSimpananMember()));
			historyTransactionDto.setTanggal(data.getTanggalTarikSimpananMember());
			historyTransactionDto.setMemberId(data.getMasterMember().getMemberId());
			historyTransactionDto.setMemberName(data.getMasterMember().getMemberName());
			
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
			HistoryTransactionDateResponseDto historyTransactionDto = new HistoryTransactionDateResponseDto();
			historyTransactionDto.setJenisTransaksi(TransactionTypeConstant.PINJAMAN);
			historyTransactionDto.setTotal(df.format(data.getTotalPinjamanMember()));
			historyTransactionDto.setTanggal(data.getTanggalPinjamanMember());
			historyTransactionDto.setMemberId(data.getMasterMember().getMemberId());
			historyTransactionDto.setMemberName(data.getMasterMember().getMemberName());
			
			listHistoryTransactionDto.add(historyTransactionDto);
		}
		
		List<BayarPinjamanMember> listBayarPinjamanMember = bayarPinjamanMemberRepository.findAll(new Specification<BayarPinjamanMember>() {
			@Override
			public Predicate toPredicate(Root<BayarPinjamanMember> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				// TODO Auto-generated method stub
				return criteriaBuilder.and(
						criteriaBuilder.isFalse(root.get("deleted")),
						criteriaBuilder.lessThanOrEqualTo(root.get("tanggalBayarPinjamanMember"), DateUtil.convertDate(dto.getEndDate())),
						criteriaBuilder.greaterThanOrEqualTo(root.get("tanggalBayarPinjamanMember"), DateUtil.convertDate(dto.getStartDate()))
				);
			}
		});
		
		for(BayarPinjamanMember data : listBayarPinjamanMember) {
			HistoryTransactionDateResponseDto historyTransactionDto = new HistoryTransactionDateResponseDto();
			historyTransactionDto.setJenisTransaksi(TransactionTypeConstant.BAYAR);
			historyTransactionDto.setTotal(df.format(data.getTotalBayarPinjamanMember()));
			historyTransactionDto.setTanggal(data.getTanggalBayarPinjamanMember());
			historyTransactionDto.setMemberId(data.getMasterMember().getMemberId());
			historyTransactionDto.setMemberName(data.getMasterMember().getMemberName());
			
			listHistoryTransactionDto.add(historyTransactionDto);
		}
		
		Response<List<HistoryTransactionDateResponseDto>> response = new Response<List<HistoryTransactionDateResponseDto>>(listHistoryTransactionDto);
		return response;
	}
	
	@RequestMapping(path = "/simpan", method = RequestMethod.POST)
	public Response<SimpananMemberResponseDto> simpan(@RequestBody SimpananMemberRequestDto dto){
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			SimpananMember simpananMember = new SimpananMember();
			PortofolioMember portoMember = new PortofolioMember();
		
			if(!StringUtils.hasText(dto.getMemberId())) {
				Response<SimpananMemberResponseDto> response = new Response<SimpananMemberResponseDto>();
				response.setError("MEMBER_ID_EMPTY", "Member Id tidak boleh kosong");
				return response;
			}
			if(dto.getTotalSimpananMember() == 0) {
				Response<SimpananMemberResponseDto> response = new Response<SimpananMemberResponseDto>();
				response.setError("TOTAL_SIMPANAN_ZERO", "Total simpanan tidak boleh 0 ");
				return response;
			}
			if(dto.getTotalSimpananMember() < 0) {
				Response<SimpananMemberResponseDto> response = new Response<SimpananMemberResponseDto>();
				response.setError("TOTAL_SIMPANAN_MINUS", "Total simpanan tidak boleh lebih kecil dari 0 ");
				return response;
			}
			if(!StringUtils.hasText(dto.getTanggalSimpananMember())) {
				Response<SimpananMemberResponseDto> response = new Response<SimpananMemberResponseDto>();
				response.setError("TANGGAL_IS_EMPTY", "Tanggal tidak boleh kosong");
				return response;
			}
			if(DateUtil.getDateNow().before(DateUtil.convertDate(dto.getTanggalSimpananMember()))) {
				Response<SimpananMemberResponseDto> response = new Response<SimpananMemberResponseDto>();
				response.setError("TANGGAL_GTE_TODAY", "Tanggal tidak boleh lebih besar dari hari ini");
				return response;
			}
			
			Optional<MasterMember> optionalMember = masterMemberRepository.findById(dto.getMemberId());
			if(!optionalMember.isPresent()) {
				Response<SimpananMemberResponseDto> response = new Response<SimpananMemberResponseDto>();
				response.setError("MEMBER_NOT_FOUND","member tidak di temukan","memberId",null);
				return response;
			}
			
			//check portofolio
			Optional<PortofolioMember> optionalPortoMember = portofolioMemberRepository.findOne(new Specification<PortofolioMember>() {
				@Override
				public Predicate toPredicate(Root<PortofolioMember> root, CriteriaQuery<?> query,
						CriteriaBuilder criteriaBuilder) {
					// TODO Auto-generated method stub
					return criteriaBuilder.and(
							criteriaBuilder.isFalse(root.get("deleted")),
						    criteriaBuilder.equal(root.join("masterMember",JoinType.LEFT).get("memberId"),dto.getMemberId())
					);
				}
			});
			if(!optionalPortoMember.isPresent()) {
				portoMember.setPortofolioMemberId(UniqueID.getUUID());
				portoMember.setTotalSimpananMember(dto.getTotalSimpananMember());
				portoMember.setMasterMember(optionalMember.get());
				portoMember.setTotalPinjamanMember(0.0);
			}else {
				portoMember = optionalPortoMember.get();
				portoMember.setTotalSimpananMember(portoMember.getTotalSimpananMember() + dto.getTotalSimpananMember());
			}
			portofolioMemberRepository.save(portoMember);
				
			simpananMember.setSimpananMemberId(UniqueID.getUUID());
			simpananMember.setMasterMember(optionalMember.get());
			simpananMember.setTanggalSimpananMember(DateUtil.convertDate(dto.getTanggalSimpananMember()));
			simpananMember.setTotalSimpananMember(dto.getTotalSimpananMember());
			
			simpananMemberRepository.save(simpananMember);
			
			//send message to kafka
			TransactionMemberKoperasiRequestDto kafkaDto = new TransactionMemberKoperasiRequestDto();
			kafkaDto.setMemberId(dto.getMemberId());
			kafkaDto.setMemberName(optionalMember.get().getMemberName());
			kafkaDto.setTotalTransactionMemberKoperasi(dto.getTotalSimpananMember());
			kafkaDto.setTransactionDateMemberKoperasi(dto.getTanggalSimpananMember());
			kafkaDto.setTransactionTypeMemberKoperasi(TransactionTypeConstant.SIMPANAN);
			
			String kafka = objectMapper.writeValueAsString(kafkaDto);
			this.sendMessage(kafka, topic);
			
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
			ObjectMapper objectMapper = new ObjectMapper();
			PinjamanMember pinjamanMember = new PinjamanMember();
			PortofolioMember portoMember = new PortofolioMember();
			
			if(!StringUtils.hasText(dto.getMemberId())) {
				Response<PinjamanMemberResponseDto> response = new Response<PinjamanMemberResponseDto>();
				response.setError("MEMBER_ID_EMPTY", "Member Id tidak boleh kosong");
				return response;
			}
			if(dto.getTotalPinjamanMember() == 0) {
				Response<PinjamanMemberResponseDto> response = new Response<PinjamanMemberResponseDto>();
				response.setError("TOTAL_PINJAMAN_ZERO", "Total Pinjaman tidak boleh 0 ");
				return response;
			}
			if(dto.getTotalPinjamanMember() < 0) {
				Response<PinjamanMemberResponseDto> response = new Response<PinjamanMemberResponseDto>();
				response.setError("TOTAL_PINJAMAN_MINUS", "Total Pinjaman tidak boleh lebih kecil dari 0");
				return response;
			}
			if(!StringUtils.hasText(dto.getTanggalPinjamanMember())) {
				Response<PinjamanMemberResponseDto> response = new Response<PinjamanMemberResponseDto>();
				response.setError("TANGGAL_IS_EMPTY", "Tanggal tidak boleh kosong");
				return response;
			}
			if(DateUtil.getDateNow().before(DateUtil.convertDate(dto.getTanggalPinjamanMember()))) {
				Response<PinjamanMemberResponseDto> response = new Response<PinjamanMemberResponseDto>();
				response.setError("TANGGAL_GTE_TODAY", "Tanggal tidak boleh lebih besar dari hari ini");
				return response;
			}
			
			Optional<MasterMember> optionalMember = masterMemberRepository.findById(dto.getMemberId());
			if(!optionalMember.isPresent()) {
				Response<PinjamanMemberResponseDto> response = new Response<PinjamanMemberResponseDto>();
				response.setError("MEMBER_NOT_FOUND","member tidak ditemukan","memberId",null);
				return response;
			}
				
			//check portofolio
			Optional<PortofolioMember> optionalPortoMember = portofolioMemberRepository.findOne(new Specification<PortofolioMember>() {
				@Override
				public Predicate toPredicate(Root<PortofolioMember> root, CriteriaQuery<?> query,
						CriteriaBuilder criteriaBuilder) {
					// TODO Auto-generated method stub
					return criteriaBuilder.and(
							criteriaBuilder.isFalse(root.get("deleted")),
						    criteriaBuilder.equal(root.join("masterMember",JoinType.LEFT).get("memberId"),dto.getMemberId())
					);
				}
			});
			if(!optionalPortoMember.isPresent()) {
				portoMember.setPortofolioMemberId(UniqueID.getUUID());
				portoMember.setTotalPinjamanMember(dto.getTotalPinjamanMember());
				portoMember.setMasterMember(optionalMember.get());
				portoMember.setTotalSimpananMember(0.0);
			}else {
				portoMember = optionalPortoMember.get();
				portoMember.setTotalPinjamanMember(portoMember.getTotalPinjamanMember() + dto.getTotalPinjamanMember());
			}
			portofolioMemberRepository.save(portoMember);
			
			pinjamanMember.setPinjamanMemberId(UniqueID.getUUID());
			pinjamanMember.setMasterMember(optionalMember.get());
			pinjamanMember.setTanggalPinjamanMember(DateUtil.convertDate(dto.getTanggalPinjamanMember()));
			pinjamanMember.setTotalPinjamanMember(dto.getTotalPinjamanMember());
			
			pinjamanMemberRepository.save(pinjamanMember);
			
			//send message to kafka
			TransactionMemberKoperasiRequestDto kafkaDto = new TransactionMemberKoperasiRequestDto();
			kafkaDto.setMemberId(dto.getMemberId());
			kafkaDto.setMemberName(optionalMember.get().getMemberName());
			kafkaDto.setTotalTransactionMemberKoperasi(dto.getTotalPinjamanMember());
			kafkaDto.setTransactionDateMemberKoperasi(dto.getTanggalPinjamanMember());
			kafkaDto.setTransactionTypeMemberKoperasi(TransactionTypeConstant.PINJAMAN);
			
			String kafka = objectMapper.writeValueAsString(kafkaDto);
			this.sendMessage(kafka, topic);
			
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
			ObjectMapper objectMapper = new ObjectMapper();
			BayarPinjamanMember bayarPinjamanMember = new BayarPinjamanMember();
			PortofolioMember portoMember = new PortofolioMember();
			
			if(!StringUtils.hasText(dto.getMemberId())) {
				Response<BayarPinjamanMemberResponseDto> response = new Response<BayarPinjamanMemberResponseDto>();
				response.setError("MEMBER_ID_EMPTY", "Member Id tidak boleh kosong");
				return response;
			}
			if(dto.getTotalBayarPinjamanMember() == 0) {
				Response<BayarPinjamanMemberResponseDto> response = new Response<BayarPinjamanMemberResponseDto>();
				response.setError("TOTAL_BAYAR_PINJAMAN_ZERO", "Total Bayar Pinjaman tidak boleh 0 ");
				return response;
			}
			if(!StringUtils.hasText(dto.getTanggalBayarPinjamanMember())) {
				Response<BayarPinjamanMemberResponseDto> response = new Response<BayarPinjamanMemberResponseDto>();
				response.setError("TANGGAL_IS_EMPTY", "Tanggal tidak boleh kosong");
				return response;
			}
			if(DateUtil.getDateNow().before(DateUtil.convertDate(dto.getTanggalBayarPinjamanMember()))) {
				Response<BayarPinjamanMemberResponseDto> response = new Response<BayarPinjamanMemberResponseDto>();
				response.setError("TANGGAL_GTE_TODAY", "Tanggal tidak boleh lebih besar dari hari ini");
				return response;
			}
			
			Optional<MasterMember> optionalMember = masterMemberRepository.findById(dto.getMemberId());
			if(!optionalMember.isPresent()) {
				Response<BayarPinjamanMemberResponseDto> response = new Response<BayarPinjamanMemberResponseDto>();
				response.setError("MEMBER_NOT_FOUND","member tidak di temukan","memberId",null);
				return response;
			}
			
			//check portofolio
			Optional<PortofolioMember> optionalPortoMember = portofolioMemberRepository.findOne(new Specification<PortofolioMember>() {
				@Override
				public Predicate toPredicate(Root<PortofolioMember> root, CriteriaQuery<?> query,
						CriteriaBuilder criteriaBuilder) {
					// TODO Auto-generated method stub
					return criteriaBuilder.and(
							criteriaBuilder.isFalse(root.get("deleted")),
						    criteriaBuilder.equal(root.join("masterMember",JoinType.LEFT).get("memberId"),dto.getMemberId())
					);
				}
			});
			if(!optionalPortoMember.isPresent()) {
				Response<BayarPinjamanMemberResponseDto> response = new Response<BayarPinjamanMemberResponseDto>();
				response.setError("PORTO_NOT_FOUND","Member harus melakukan Pinjaman terlebih dahulu","",null);
				return response;
			}else {
				portoMember = optionalPortoMember.get();
				if(portoMember.getTotalPinjamanMember() < dto.getTotalBayarPinjamanMember()) {
					Response<BayarPinjamanMemberResponseDto> response = new Response<BayarPinjamanMemberResponseDto>();
					response.setError("TOTAL_PINJAMAN_LESS_THAN_BAYAR","Total pinjaman tidak boleh lebih kecil dari bayar","",null);
					return response;
				}
				portoMember.setTotalPinjamanMember(portoMember.getTotalPinjamanMember() - dto.getTotalBayarPinjamanMember());
			}
			portofolioMemberRepository.save(portoMember);
				
			bayarPinjamanMember.setBayarPinjamanMemberId(UniqueID.getUUID());
			bayarPinjamanMember.setMasterMember(optionalMember.get());
			bayarPinjamanMember.setTanggalBayarPinjamanMember(DateUtil.convertDate(dto.getTanggalBayarPinjamanMember()));
			bayarPinjamanMember.setTotalBayarPinjamanMember(dto.getTotalBayarPinjamanMember());
			
			bayarPinjamanMemberRepository.save(bayarPinjamanMember);
			
			//send message to kafka
			TransactionMemberKoperasiRequestDto kafkaDto = new TransactionMemberKoperasiRequestDto();
			kafkaDto.setMemberId(dto.getMemberId());
			kafkaDto.setMemberName(optionalMember.get().getMemberName());
			kafkaDto.setTotalTransactionMemberKoperasi(dto.getTotalBayarPinjamanMember());
			kafkaDto.setTransactionDateMemberKoperasi(dto.getTanggalBayarPinjamanMember());
			kafkaDto.setTransactionTypeMemberKoperasi(TransactionTypeConstant.BAYAR);
			
			String kafka = objectMapper.writeValueAsString(kafkaDto);
			this.sendMessage(kafka, topic);
			
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
			ObjectMapper objectMapper = new ObjectMapper();
			TarikSimpananMember tarikSimpananMember = new TarikSimpananMember();
			PortofolioMember portoMember = new PortofolioMember();
			
			if(!StringUtils.hasText(dto.getMemberId())) {
				Response<TarikSimpananResponseDto> response = new Response<TarikSimpananResponseDto>();
				response.setError("MEMBER_ID_EMPTY", "Member Id tidak boleh kosong");
				return response;
			}
			if(dto.getTotalTarikSimpananMember() == 0) {
				Response<TarikSimpananResponseDto> response = new Response<TarikSimpananResponseDto>();
				response.setError("TOTAL_TARIK_SIMPANAN_ZERO", "Total Tarik Simpanan tidak boleh 0 ");
				return response;
			}
			if(!StringUtils.hasText(dto.getTanggalTarikSimpananMember())) {
				Response<TarikSimpananResponseDto> response = new Response<TarikSimpananResponseDto>();
				response.setError("TANGGAL_IS_EMPTY", "Tanggal tidak boleh kosong");
				return response;
			}
			if(DateUtil.getDateNow().before(DateUtil.convertDate(dto.getTanggalTarikSimpananMember()))) {
				Response<TarikSimpananResponseDto> response = new Response<TarikSimpananResponseDto>();
				response.setError("TANGGAL_GTE_TODAY", "Tanggal tidak boleh lebih besar dari hari ini");
				return response;
			}
			
			Optional<MasterMember> optionalMember = masterMemberRepository.findById(dto.getMemberId());
			if(!optionalMember.isPresent()) {
				Response<TarikSimpananResponseDto> response = new Response<TarikSimpananResponseDto>();
				response.setError("MEMBER_NOT_FOUND","member tidak ditemukan","memberId",null);
				return response;
			}
				
			//check portofolio
			Optional<PortofolioMember> optionalPortoMember = portofolioMemberRepository.findOne(new Specification<PortofolioMember>() {
				@Override
				public Predicate toPredicate(Root<PortofolioMember> root, CriteriaQuery<?> query,
						CriteriaBuilder criteriaBuilder) {
					// TODO Auto-generated method stub
					return criteriaBuilder.and(
							criteriaBuilder.isFalse(root.get("deleted")),
						    criteriaBuilder.equal(root.join("masterMember",JoinType.LEFT).get("memberId"),dto.getMemberId())
					);
				}
			});
			if(!optionalPortoMember.isPresent()) {
				Response<TarikSimpananResponseDto> response = new Response<TarikSimpananResponseDto>();
				response.setError("PORTO_NOT_FOUND","Member harus melakukan simpanan terlebih dahulu","",null);
				return response;
			}else {
				portoMember = optionalPortoMember.get();
				if(portoMember.getTotalSimpananMember() < dto.getTotalTarikSimpananMember()) {
					Response<TarikSimpananResponseDto> response = new Response<TarikSimpananResponseDto>();
					response.setError("TOTAL_SIMPANAN_LESS_THAN_TARIK","Total simpanan tidak boleh lebih kecil dari total tarik","",null);
					return response;
				}
				portoMember.setTotalSimpananMember(portoMember.getTotalSimpananMember() - dto.getTotalTarikSimpananMember());
			}
			portofolioMemberRepository.save(portoMember);
			
			tarikSimpananMember.setTarikSimpananMemberId(UniqueID.getUUID());
			tarikSimpananMember.setMasterMember(optionalMember.get());
			tarikSimpananMember.setTanggalTarikSimpananMember(DateUtil.convertDate(dto.getTanggalTarikSimpananMember()));
			tarikSimpananMember.setTotalTarikSimpananMember(dto.getTotalTarikSimpananMember());
			
			tarikSimpananMemberRepository.save(tarikSimpananMember);
			
			//send message to kafka
			TransactionMemberKoperasiRequestDto kafkaDto = new TransactionMemberKoperasiRequestDto();
			kafkaDto.setMemberId(dto.getMemberId());
			kafkaDto.setMemberName(optionalMember.get().getMemberName());
			kafkaDto.setTotalTransactionMemberKoperasi(dto.getTotalTarikSimpananMember());
			kafkaDto.setTransactionDateMemberKoperasi(dto.getTanggalTarikSimpananMember());
			kafkaDto.setTransactionTypeMemberKoperasi(TransactionTypeConstant.TARIK);
			
			String kafka = objectMapper.writeValueAsString(kafkaDto);
			this.sendMessage(kafka, topic);
			
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
