package com.example.cmtProject.entity.mes.wareHouse;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "WAREHOUSES")
@NoArgsConstructor
@AllArgsConstructor
public class Warehouses {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_WAREHOUSES_WHS_NO")
	@SequenceGenerator(name = "SEQ_WAREHOUSES_WHS_NO", sequenceName = "SEQ_WAREHOUSES_WHS_NO", allocationSize = 1)
	@Column(name = "WHS_NO",precision = 19, scale = 0, nullable = false)
	private Long whsNo; //창고 번호(PK)

    @Column(name = "WHS_CODE", nullable = false, length = 50, unique = true)
    private String whsCode; //창고 코드

    @Column(name = "WHS_NAME", nullable = false, length = 100)
    private String whsName; //창고 명칭

    @Column(name = "WHS_TYPE", nullable = false, length = 50)
    private String whsType; //창고 유형(공통코드 WH_TYPE 참조)

    @Column(name = "WHS_LOCATION", length = 255)
    private String whsLocation; //창고 위치

    @Column(name = "WHS_MANAGER", length = 50)
    private String whsManager; //창고 관리자

    @Column(name = "WHS_CAPACITY", precision = 10, scale = 2)
    private BigDecimal whsCapacity; //창고 용량

    @Column(name = "WHS_COMMENTS", length = 500)
    private String whsComments; //창고 설명(비고)

    @Column(name = "WHS_USED", length = 100)
    private String whsUsed; //창고 사용 정보

    @Column(name = "USE_YN", nullable = false, length = 1)
    private String useYn = "Y"; //사용 여부(Y/N)

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy; //생성자

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy; //수정자

    @Column(name = "CREATED_DATE")
    private LocalDate createdDate; //생성일시

    @Column(name = "UPDATED_DATE")
    private LocalDate updatedDate; //수정일시

//    @PrePersist
//    protected void onCreate() {
//        createdDate = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedDate = LocalDateTime.now();
//    }
}