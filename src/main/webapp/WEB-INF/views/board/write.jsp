<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="../common/head.jsp" %>
<style type="text/css">
.placeholder{background-color: transparent; font-size: 16px}
</style>
</head>
<body>
<%@ include file="../common/header.jsp" %>
	<div class="container">
        <main>
        <h2 class="text-center my-5">기부 글 작성</h2>
        <form class="my-4" id="writeForm" method="post">
          <div class="form-control d-flex gap-2 mb-2">
            <div>
              <select class="form-select" style="min-width: 200px;" name="cno">
                <option value="3">청소년</option>
                <option value="4">어르신</option>
                <option value="5">동물</option>
                <option value="6">지구</option>
                <option value="7">환경</option>
                <option value="8">장애인</option>
                <option value="9">사회</option>
              </select>
            </div>
            <input type="text" class="form-control" placeholder="제목" name="title" required>
          </div>
          <div id="editor"></div>
          <div class="d-flex border rounded-1 list-group p-3 gap-3 flex-row mt-2">
            <div class="form-floating flex-grow-1">
              <input type="number" class="form-control" placeholder="모금 목표 금액(최대 1억)" name="goalamount" id="goalamount" min="1000000" max="100000000" value="1000000" required>
              <label for="goalamount">모금 목표 금액(최대 1억)</label>
            </div>
            
            <div class="form-floating flex-grow-1">
              <input type="text" class="form-control" placeholder="마감일" name="voiddate" id="voiddate" required>
              <label for="voiddate">마감일</label>
            </div>
          </div>
          <div class="d-flex border rounded-1 list-group p-3 gap-3 flex-row mt-2">
            <div class="form-floating rounded-2 overflow-hidden">
              <img src="https://placehold.co/250x250?text=No+img" alt="썸네일" id="thumbnailImg" style="height: 58px; object-fit: cover;">
            </div>
            <div class="form-floating d-flex flex-grow-1 position-relative">
			  <input type="text" class="form-control pe-5" id="thumbnailName" placeholder="썸네일" readonly>
			  <label for="thumbnailName" style="z-index: 10;">썸네일</label>
			  <input type="file" id="thumbnailFile" class="d-none" accept=".jpg, .jpeg, .png, .bmp, .gif, .webp">
			  <button type="button" class="btn btn-outline-primary position-absolute top-50 end-0 translate-middle-y me-2" id="uploadThumbnailBtn">썸네일 등록</button>
			</div>
          </div>
          
          <!-- 히든으로 값 전달 -->
          <input type="hidden" name="content" id="content">
          <input type="hidden" name="mno" value="${member.mno }">
          <input type="hidden" name="status" value="READY">
          <input type="hidden" name="uuid" id="uuid">
		  <input type="hidden" name="path" id="path">
		  <input type="hidden" name="origin" id="origin">
		  <input type="hidden" name="image" value="true">
          
          
          <div class="float-end mt-4">
            <a href="${cp }/board/list" class="btn btn-outline-primary me-2">취소</a>
            <button class="btn btn-primary me-2">승인요청</button>
          </div>
        	
        </form>
      </main>

    </div>

<%@ include file="../common/footer.jsp" %>
	<script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
  <script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>
  <script>
    $(_ => {
      const cp = '${pageContext.request.contextPath}';
      const editor = new toastui.Editor({
        el: document.querySelector('#editor'), // 에디터를 표시할 요소
        height: '500px',
        initialEditType: 'markdown',  // 'wysiwyg' 또는 'markdown'
        previewStyle: 'vertical',     // 'vertical' 또는 'tab'
        placeholder: '이미지는 가로 너비를 기준으로 꽉 채워 표시되며, 세로 길이는 비율에 따라 자동 조정됩니다. 너무 세로로 긴 이미지는 가독성과 디자인을 해칠 수 있으므로 사용을 지양해주세요.',
        initialValue: '',
        hooks: {
        	  addImageBlobHook: (blob, callback) => {
        	    const formData = new FormData();
        	    formData.append('uploadFile', blob);

        	    fetch(cp + '/upload', {
        	      method: 'POST',
        	      body: formData
        	    })
        	    .then(res => res.json())
        	    .then(data => {
        	      if (Array.isArray(data) && data.length > 0) {
        	        const file = data[0];
        	        console.log(file);
        	        const imageUrl = 'https://happygivers-bucket.s3.ap-northeast-2.amazonaws.com/upload/' + file.path + '/' +  file.uuid;
        	        callback(imageUrl, file.origin);
        	      } else {
        	        alert("이미지 업로드 실패");
        	      }
        	    })
        	    .catch(err => {
        	      console.error(err);
        	      alert("이미지 업로드 오류 발생");
        	    });
        	  }
        	}
      });

      flatpickr("#voiddate", {
        dateFormat: "Y-m-d",   // Y-m-d
        locale: "ko",          // 한국어
        minDate: new Date().fp_incr(15), // 오늘로부터 15일 이후 부터만 선택 가능
        maxDate: new Date().fp_incr(180), // 최대 6개월 뒤까지 선택 가능
        disableMobile: true, // 모바일 기본 날짜 선택기 비활성화
        animate: "true",
        allowInput: false
      });
      
      
      $('#writeForm').on('submit', function () {
    	  event.preventDefault();
    	  if($("#voiddate").val().trim() === ""){
    		  alert("마감일을 선택해주세요.");
    		  return;
    	  }
    	  const markdown = editor.getMarkdown(); // 또는 editor.getHTML();
    	  console.log(markdown);
    	  $('#content').val(markdown);
    	  
    	  this.submit();
    	});
      
      $("#goalamount").on("keyup", function(e){
        let value = parseInt(e.target.value, 10);

        // 1억보다 크면 1억으로 고정
        if (value > 100000000) {
          e.target.value = 100000000;
        }

        // 음수 방지
        if (value < 1000000) {
          e.target.value = 1000000;
        }
      })
    })
    
     $('#uploadThumbnailBtn').on('click', function () {
	    $('#thumbnailFile').click();
	  });
	
	 $('#thumbnailFile').on('change', function () {
	    const fileName = $(this)[0].files[0]?.name || '';
	    $('#thumbnailName').val(fileName);
	  });
  </script>
  
  <script type="text/javascript">
  const cp = '${pageContext.request.contextPath}';
  // 썸네일 파일 변경시 사이즈, 확장자 체크
  $('#thumbnailFile').on('change', function (e) {
	  event.preventDefault();
	  const file = this.files[0];
	  if (!file) return;

	  const MAX_FILE_SIZE = 1 * 1024 * 1024;
	  const ONLY_EXT = ['jpg', 'jpeg', 'png', 'bmp', 'gif', 'webp'];
	  const ext = file.name.split(".").pop().toLowerCase();

	  if (!ONLY_EXT.includes(ext) || file.size > MAX_FILE_SIZE) {
	    alert("썸네일은 이미지 파일(jpg, jpeg, png, bmp, gif, webp)만 등록 가능하며 최대 1MB입니다.");
	    $(this).val("");
	    $("#thumbnailName").val("");
	    return;
	  }
		
		const formData = new FormData();
		formData.append("uploadFile", file);
		
			
		$.ajax({
			url : '${cp}/upload',
			method : 'POST',
			data : formData,
			processData : false, // data를 queryString으로 쓰지 않겠다.
			contentType : false, // multipart/form-data; 이후에 나오게될 브라우저 정보도 포함시킨다, 즉 기본 브라우저 설정을 따르는 옵션.
			success : function(data) {
				if(Array.isArray(data) && data.length > 0){
					const a = data[0];
					const imageUrl = 'https://happygivers-bucket.s3.ap-northeast-2.amazonaws.com/upload/' + a.path + '/' +  a.uuid;
					if(a.image){
						$('#thumbnailName').val(a.origin);
						$('#uuid').val(a.uuid);
				        $('#path').val(a.path);
				        $('#origin').val(a.origin);
				        console.log($('#thumbnailImg'));
				        $('#thumbnailImg').attr("src", imageUrl);
				     
					}
				}
				else {
					alert('썸네일이 없습니다.');
				}
			},
			error: function () {
				alert('썸네일 업로드 실패');
			}
		});
	});

    
	

</script>
</body>
</html>