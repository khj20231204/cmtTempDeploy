package com.example.cmtProject.dto.mes.standardInfoMgt;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientsDTO {

	private Long cltNo;             // 거래처 번호
    private String cltCode;         // 거래처 코드
    private String cltName;         // 거래처명
    private String cltOwnerName;    // 대표자명
    private String cltManagerName;  // 담당자명
    private String cltPhone;        // 전화번호
    private String cltEmail;        // 이메일
    private String cltAddress;      // 주소
    private String cltAddress2;     // 주소2
    private String cltPostCode;     // 우편번호
    private String cltType;         // 수주/발주 구분
    private String cltPrcType;      // 거래처 형태
    private String cltComments;     // 메모
    private String clientType;      // 거래처 구분 (예: 납품처/구매처 등)
    private LocalDate regiDate;      // 등록일
}