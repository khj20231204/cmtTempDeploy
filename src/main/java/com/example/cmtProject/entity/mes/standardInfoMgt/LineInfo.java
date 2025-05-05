package com.example.cmtProject.entity.mes.standardInfoMgt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "LINES")
@NoArgsConstructor
@AllArgsConstructor
public class LineInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PDT_LINE_NO")
    @SequenceGenerator(name = "SEQ_PDT_LINE_NO", sequenceName = "SEQ_PDT_LINE_NO", allocationSize = 1)
    @Column(name = "LINE_NO")
	private Long lineNo;  
	
	@Column(name = "LINE_CODE")
	private String lineCode; 
	
	@Column(name = "LINE_NAME")
	private String lineName; 
	
	@Column(name = "LINE_LOCATION")
	private String lineLocation; 
	
	@Column(name = "LINE_STATUS")
	private String lineStatus; 
	
	@Column(name = "LINE_COMMENTS")
	private String lineComments;
	
}
