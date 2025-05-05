	

	//프로필사진
	$(function () {
		$("#empProfileFile").on("change", function (e) {
	    let files = e.target.files;
	    let imageFiles = Array.from(files).filter(file => file.type.startsWith("image/"));
	
	    if (imageFiles.length > 1) {
	    	alert("프로필 사진은 1장만 업로드 가능합니다.");
	    	imageFiles = imageFiles.slice(0, 1);
	    }
	
	    $("#preview_profile").html("");
	
	    imageFiles.forEach(file => {
	      let reader = new FileReader();
	      reader.onload = function (event) {
	      	let img = $("<img>")
	          .attr("src", event.target.result)
	          .attr("height", "100px")
	          .addClass("profile-thumb");
	
	        $("#preview_profile").append(img);
	      };
	      reader.readAsDataURL(file);
	    });
	  });
		
		
		  // 사원번호 유효성 검사
		$("#empId").on("keyup", function() {
		    var empId = $(this).val();
		    $.ajax({
		        url : '/emp/checkId',
		        type : 'post',
		        data : { empId : empId },
		        success : function(response){
		        	console.log(response);
		            if(response == 1){
		                $('#idCheckMsg').text('[중복 아이디] 이미 사용중인 아이디 입니다.').css('color', 'black');
		            } else {
		                $('#idCheckMsg').text('사용 가능한 아이디 입니다.').css('color', 'green');
		            } 
		        },
		        error : function(){
		            console.log("아이디 중복체크 오류");
		        }
		    });
		});
	});
        //사원등록 얼럿창
    function empSubmit(){
    	Swal.fire({
            title: '사원등록을 저장하겠습니까?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: '사원등록',
            cancelButtonText: '아니오'
        }).then((result) => {
            if (result.isConfirmed) {
                document.getElementById('empRegi').submit();
            }
        });
    }
    
        
      //주차 등록버튼 
	$('#empParkingStatus').on('change', function(){
		toggleEmpParkingStatus();
	
	function toggleEmpParkingStatus(){
		const selected = $('#empParkingStatus option:selected').text();
		if(selected.includes('미등록')){
			$('#empCarNumber').hide();
			$('#empCarNumber').val('');
		} else {
			$('#empCarNumber').show();
		}
	}
	});
			
	//카카오 주소 api
	function search_address() {
		new daum.Postcode({
			oncomplete : function(data) {
				console.log(data);
				document.empRegi.empPostCode.value = data.zonecode;

				let address = data.address;
				if (data.buildingName != "") {
					address += " (" + data.buildingName + ")";
				}

				document.empRegi.empAddress1.value = address;
				document.empRegi.empAddress2.focus();
			}
		}).open();
	}
	
	//사원 pdf 출력
	function empPrint(){
		const checkedRows = grid.getCheckedRows();
		const empId = checkedRows[0].empId;
		console.log("클릭된 사원 아이디 >>" + empId);
		
		$.ajax({
		url : '/emp/empPrint/' + empId ,
		method : 'get',
		data : {empId : empId},
		success : function(response){
			//alert("PDF저장성공!");
			Swal.fire({
			  icon: "success",
			  title: "사원정보 PDF 저장 성공!",
			});
			window.open('/pdfs/empPdf.pdf', '_blank', 'width=800,height=1000');
		},
		error : function(error){
			console.log("오류발생");
		}
		
		});
      	  
  }
	
	