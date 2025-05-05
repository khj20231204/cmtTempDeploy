
	function empDetail_view(){
		console.log("수정버튼 클릭댐");
		        
	    const id = $("input[name='empId']").val();
	    const form = document.getElementById("empDetail");
	    const formData = new FormData(form);
	    formData.append("empProfileFile", $("input[name='empProfileFile']")[0].files[0]);
            	        
        $.ajax({
            url: `/emp/empUpdate/${id}`,
            method: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
	        	Swal.fire({
			        title: '수정 하시겠습니까?',
			        icon: 'warning',
			        showCancelButton: true,   
			        confirmButtonColor: '#435ebe',   
			        cancelButtonColor: '#f27474',       
			        confirmButtonText: '네',         
			        cancelButtonText: '아니오'       
	       	         }).then((result) => {
	        	        if (result.isConfirmed) {
	        	            Swal.fire({
	        	                title: '수정이 완료되었습니다!',
	        	                icon: 'success'
	        	            }).then(() => {
	        	                location.reload();
	        	               //window.close();
	        	            });
	        	        }
	        	    });
		         	        	},
    	            error: function(error) {
    	                alert("정보 수정 실패ㅠㅠ");
    	            }
    	        });
    	        
    	        for (let pair of formData.entries()) {
    	            console.log(pair[0] + ": " + pair[1]);
   	        }	            	       
        }
            
     	//프로필사진 미리보기
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
		});


		//퇴사일 데이트박스 출력
		$(document).ready(function () {
		    toggleEmpEndDate();
		    $('#empStatus').on('change', function () {
		        toggleEmpEndDate();
		    });

		    function toggleEmpEndDate() {
		        const selected = $('#empStatus option:selected').text(); // 선택된 텍스트
		        if (selected.includes('퇴사')) { // 예: '퇴사' or 'RESIGNED'
		            $('#empEndDateWrapper').show();
		        } else {
		            $('#empEndDateWrapper').hide();
		            $('#empEndDate').val(''); // 값도 초기화
		        }
		    }
		});
		
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
		
		
		function search_address() {
			new daum.Postcode({
				oncomplete : function(data) {
					console.log(data);
					document.empDetail.empPostCode.value = data.zonecode;

					let address = data.address;
					if (data.buildingName != "") {
						address += " (" + data.buildingName + ")";
					}

					document.empDetail.empAddress1.value = address;
					document.empDetail.empAddress2.focus();
				}
			}).open();
		}