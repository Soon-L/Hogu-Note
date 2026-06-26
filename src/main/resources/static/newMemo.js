// currentData 변수
const originalMemoInput = document.getElementById('originalMemo');
const pwInput = document.getElementById('savePassword'); // 현재 메모 저장할때 비번
const personalCode = document.getElementById('personalCode'); // 현재 메모의 코드

let currentMemoData = {}; // 모달이 열릴 때 현재 입력값을 저장할 변수



// 불러오기 변수
const loadCode = document.getElementById('loadCode'); // 불러올 메모의 코드
const loadPassword = document.getElementById('loadPassword'); // 입력한 불러올 메모의 비번

let currentPersonalCode = ''; // 코드 저장 변수(비밀메모 모달 사용시 필요)
let getOriginalMemo; // 현재 메모의 변경 전 메모
let getPassword; //DB에 저장된 불러올 메모의 비번


// 변경사항 변수
let unSavedChanges;



// 모달 열기
async function openModal(name) {

    document.getElementById('modal-' + name).classList.add('active');
}


// 모달창 닫기
function closeModal(name) {
    document.getElementById('modal-' + name).classList.remove('active');
}


// 모달창 오버레이 닫기
function closeOnOverlay(e, name) {
    if (e.target === document.getElementById('modal-' + name)) closeModal(name);
}


// 현재 데이터
async function currentData(pcode, pw){
	
	currentMemoData = {
	    originalMemo: originalMemoInput.value,
		summaryMemo : null,
		personalCode: pcode,
	    password: pw
	};
}







// 저장하기
async function doSave(event) {

	// 현재 데이터
	currentData(personalCode.textContent.trim(), pwInput.value.trim());
	
	
    try {
        const response = await fetch('/api/memo', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(currentMemoData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || '메모 저장 실패');
        }
        // 폼 초기화
        //currentMemoData = {}; // 저장 후 데이터 초기화

    } catch (error) {
        console.error('Error saving memo:', error);
    }
	
	closeModal('save');
	
	// 모달창 닫힌 후 저장됐다고 뜨는 알림창 필요할듯
}


// 시크릿메모로 저장 활성화
function secretmode(){
	
	// 저장 모달에서 시크릿모드 활성화
	const checkbox = document.getElementById('secretmodeBtn');
	const is_checked = checkbox.checked;
	const elementSecret = document.querySelectorAll('.secretmode')


	// 체크시 비밀번호 입력창 보임
	if(is_checked){	
		elementSecret.forEach(element => {
			element.classList.add('active');
		})		
	}
	else{	
		elementSecret.forEach(element => {
			element.classList.remove('active');
		})
	}
}







// 코드로 메모 불러오기
async function doLoad(){
	
	// 입력한 코드
	const code = loadCode.value.trim();
	
		
	if(!code){
		
		alert('코드를 입력해주세요.');
		return;
	}
	
	
	// 불러오기 전 변경사항 확인
	unSavedChanges = await hasUnsavedChanges();
	if(unSavedChanges){
		
		// 저장할건지 안할건지 묻기
		await openModal('saveAndLoad');
		return;
		
	}
	
	
	try{
		const response = await fetch(`/api/memo/${code}/type`, {
			
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({personalCode: code})
			
		});
		
		
		
		if(!response.ok){
			const errorData = await response.json();
			throw new Error(errorData.message || '코드 확인 실패');
		}
		

		
		currentPersonalCode = code;
		
		// 불러올 메모 비번 확인
		await getMemoPassword(code);
		
		// 비밀메모 체크
		await checkSecret();

		
	}catch(error){
		console.log('Error checking code:', error);
		alert(error.message || '코드 확인 중 오류가 발생했습니다.')
		
	}
	
	
}


// 불러오기 -> 비번입력창 열리기
async function checkSecret(){
	// 비밀메모
	if(getPassword !== null){
		openModal('password');
	}
	// 공개메모
	else{
		window.location.href = `/memo/${currentPersonalCode}`;	
	}
}



// DB에서 비밀번호 가져오기
async function getMemoPassword(pcode){
	await fetch(`/api/memo/${pcode}`)
	    .then(response => response.json()) // JSON 형태로 파싱
	    .then(data => {
	        console.log(data.message); // "성공"
			console.log(data.dto);

			getPassword = data.dto.password;			
	    })
	    .catch(error => console.error('Error:', error));
	
}



// 가져온 비번 확인하기
async function checkPassword(){
	
	const password = loadPassword.value.trim();
	
	// 미입력
	if(!password){
		
		alert('비밀번호를 입력해주세요.');
		return;
	}
	
	
	currentData(loadCode.value.trim(), password);
	
	
	
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


// 변경사항 확인
async function hasUnsavedChanges(){
	return originalMemoInput.value !== "";
}


// 저장x 불러오기
async function doLoadNow(){
	
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




// 저장o 불러오기
async function doSaveAndLoad(){
	
	closeModal('saveAndLoad');
	closeModal('load');
	openModal('save');
	
	// 미완) 저장 -> 이동
	
}








// 코드 복사
function doCopy() {
    // TODO: 실제 코드 복사 로직
    const code = document.getElementById('shareCode').textContent;
    alert('복사됨: ' + code + ' (구현 예정)');
}

// 공유하기
function doKakaoShare() {
    // TODO: 카카오톡 공유 SDK 연동
    alert('카카오톡 공유 (구현 예정)');
}





// 나가기
async function doExit() {
	
	unSavedChanges = await hasUnsavedChanges();
	
	// 변경사항 저장 여부
	if(unSavedChanges){
		
		openModal('saveAndExit');
		return;
	}
	
    window.location.href = `/`;
}


// 저장x 나가기
async function doExitNow(){
	window.location.href = `/`;
}


// 저장o 나가기
async function doSaveAndExit(){
	await doSave(event);
	await doExitNow();
}

