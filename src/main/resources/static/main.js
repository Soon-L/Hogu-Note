let currentMemoData = {}; // 모달이 열릴 때 현재 입력값을 저장할 변수

// 입장하기 (실시간 공유 중인 새메모에 합류)
async function doJoin() {
    const code = document.getElementById('shareCodeInput').value.trim();

    if (!code) {
        alert('코드를 입력해주세요.');
        return;
    }

    // /new_memo/{code} 로 이동 → MemoController가 세션에 code 저장
    window.location.href = `/new_memo/${code}`;
}
let dbPassword; // db에서 가져온 비밀번호
let currentPersonalCode;


const pwInput = document.getElementById('passwordInput');
const personalCode = document.getElementById('codeInput');





// 모달 열기
function openModal(name) {
    document.getElementById('modal-' + name).classList.add('active');	
}


// 모달창 닫기
function closeModal(name) {
    document.getElementById('modal-' + name).classList.remove('active');
}

function closeOnOverlay(e, name) {
    if (e.target === document.getElementById('modal-' + name)) closeModal(name);
}







// 코드로 메모 불러오기
async function doLoad(){
	
	const code = personalCode.value;
	currentPersonalCode = code;
	
	// 데이터 최신화
	await currentData(currentPersonalCode, null);
	
	
	
	if(!code){
		
		alert('코드를 입력해주세요.');
		return;
	}
	
	await getPassword(code);
	
	try{		
		const response = await fetch(`/api/memo/${code}/type`, {
			
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(currentMemoData.personalCode)
			
		});
		
		
		if(!response.ok){
			const errorData = await response.json();
			throw new Error(errorData.message || '코드 확인 실패');
		}
		
		//openModal('password');
		
		// 비밀메모
		if(dbPassword !== null){
			openModal('password');
			
		}
		// 공개메모
		else{
			window.location.href = `/memo/${currentMemoData.personalCode}`;	
		}
		
		

		
	}catch(error){
		console.log('Error checking code:', error);
		alert(error.message || '코드 확인 중 오류가 발생했습니다.')
		
	}
	
	
}

// DB에서 비번 가져오기
async function getPassword(pcode){

	await fetch(`/api/memo/${pcode}`)
	    .then(response => response.json()) // JSON 형태로 파싱
	    .then(data => {
	        console.log(data.message); // "성공"
			//console.log(data.dto.password);
			
			dbPassword = data.dto.password;
			
	    })
	    .catch(error => console.error('Error:', error));
}



// 비밀번호 확인
async function checkPassword(){
	
	const password = pwInput.value;
	
	if(!password){
		
		alert('비밀번호를 입력해주세요.');
		return;
	}
	
	// 데이터 최신화
	currentData(currentPersonalCode, password);
	
	
	
	try{
		
		const response = await fetch('/api/memo/load', {
			
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(currentMemoData)
			
		});


		if(!response.ok){
			const errorData = await response.json();
			throw new Error(errorData.message || '메모 불러오기 실패');
		}

		// 불러오기 성공
		window.location.href = `/memo/${currentMemoData.personalCode}`;	
		
	}catch(error){
		console.log('Error loading code:', error);
		alert(error.message || '메로를 불러오는 중 오류가 발생했습니다.')
	}
	
	
}





// 데이터 최신화
async function currentData(pcode, pw){
	
	currentMemoData = {
		personalCode: pcode,
		password: pw
	};
	
	
}








 