/*// 입력 요소들
const originalMemoInput = document.getElementById('originalMemo');
//const pwInput = document.getElementById('savePassword');
const loadPassword = document.getElementById('loadPassword'); // 불러올 메모의 비번
const personalCode = document.getElementById('personalCode');
const loadCode = document.getElementById('loadCode'); // 불러올 메모의 코드

let currentMemoData = {}; // 모달이 열릴 때 현재 입력값을 저장할 변수
let currentPersonalCode = ''; // 코드 저장 변수(비밀메모 모달 사용시 필요)
let getOriginalMemo; // 현재 메모의 변경 전 메모
let getPassword; // 메모의 비밀번호



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


	
// DB에서 메모 비밀번호 가져오기
function getMemoPassword(pcode){
	fetch(`/api/memo/${pcode}`)
	    .then(response => response.json()) // JSON 형태로 파싱
	    .then(data => {
	        console.log(data.message); // "성공"
			console.log(data.dto);

			getPassword = data.dto.password;
			
	    })
	    .catch(error => console.error('Error:', error));
	
}








// 모달 열기
function openModal(name) {

	// 현재 데이터
    currentMemoData = {
        originalMemo: originalMemoInput.value,
		summaryMemo : null,
		personalCode: personalCode.textContent,
        //password: pwInput.value.trim()
    };
	
	console.log("현재 메모: "+currentMemoData.originalMemo);


    document.getElementById('modal-' + name).classList.add('active');
	
	
}



// 모달창 닫기
function closeModal(name) {
    document.getElementById('modal-' + name).classList.remove('active');
}

function closeOnOverlay(e, name) {
    if (e.target === document.getElementById('modal-' + name)) closeModal(name);
}




// 변경사항 확인
function hasUnsavedChanges(){
	
	return getOriginalMemo !== originalMemoInput.value;
}









// 저장하기
async function doSave(event) {
	//새로고침 방지
	// event.preventDefault();

    try {
        const response = await fetch('/api/memo/update', {
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

        currentMemoData = {}; // 저장 후 데이터 초기화

    } catch (error) {
        console.error('Error saving memo:', error);
    }
	
	closeModal('save');
	
	// 모달창 닫힌 후 저장됐다고 뜨는 알림창 필요할듯
}



// 시크릿모드 활성화
function secretmode(){
	
	// 저장 모달에서 시크릿모드 활성화
	const checkbox = document.getElementById('secretmodeBtn');
	const is_checked = checkbox.checked;
	const elementSecret = document.querySelectorAll('.secretmode')


	if(is_checked){
		
		console.log(is_checked);
		
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
	
	// 변경사항 확인
	if(hasUnsavedChanges()){
		console.log("탔냐");
		openModal('check');
		
		return;
	}
	
	currentPersonalCode = loadCode.value.trim();
	
	if(!currentPersonalCode){
		
		alert('코드를 입력해주세요.');
		return;
	}
	
	// 불러올 메모 비밀번호 가져오기
	getMemoPassword(currentPersonalCode);
	
	try{
		
		const response = await fetch(`/api/memo/${currentPersonalCode}/type`, {
			
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({personalCode: currentPersonalCode})
			
		});
		
		
		if(!response.ok){
			const errorData = await response.json();
			throw new Error(errorData.message || '코드 확인 실패');
		}
		
		
		if(getPassword !== null){
			console.log("비밀메모 탐");
			console.log(getPassword);
			openModal('password');
		}
		else{
			console.log("공개메모 탐");
			// 불러오기 성공
			window.location.href = `/memo/${currentPersonalCode}`;	
		}
		
	}catch(error){
		console.log('Error checking code:', error);
		alert(error.message || '코드 확인 중 오류가 발생했습니다.')
		
	}
	
	
}



// 비밀번호 확인
async function checkPassword(){
		
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
		
		
		// 비밀번호 체크
		if(getPassword !== loadPassword){
			alert('비밀번호가 다릅니다.');
			return;
		}
		else{
			// 불러오기 성공
			window.location.href = `/memo/${currentMemoData.personalCode}`;		
		}

		
	}catch(error){
		console.log('Error loading code:', error);
		alert(error.message || '메로를 불러오는 중 오류가 발생했습니다.')
	}
	
	
}



// 변경사항 저장하고 불러오기
async function doSaveAndLoad(){
	
	
	// 저장하기
	try {
	    const response = await fetch('/api/memo/update', {
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


	} catch (error) {

	    console.error('Error saving memo:', error);
	}
	
	


	// 불러오기
	try{
		
		const response = await fetch(`/api/memo/${currentPersonalCode}/type`, {
			
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({personalCode: currentPersonalCode})
			
		});
		
		
		if(!response.ok){
			const errorData = await response.json();
			throw new Error(errorData.message || '코드 확인 실패');
		}
		
		
		// 비밀번호 체크
		if(getPassword !== null){
			openModal('password');
		}
		else{
			window.location.href = `/memo/${currentPersonalCode}`;	
		}

		
	}catch(error){
		console.log('Error checking code:', error);
		alert(error.message || '코드 확인 중 오류가 발생했습니다.')
		
	}
	
}
// 변경사항 저장 안하고 불러오기
async function doLoadNow(){
	
	console.log("탔냐")
	
	try{
		console.log("try 타는지");
		
		const response = await fetch(`/api/memo/${currentPersonalCode}/type`, {
			
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({personalCode: currentPersonalCode})
			
		});
		
		
		if(!response.ok){
			const errorData = await response.json();
			throw new Error(errorData.message || '코드 확인 실패');
		}

		// 비밀메모 여부 체크
		if(getPassword !== null){
			openModal('password');
		}
		else{
			window.location.href = `/memo/${currentPersonalCode}`;	
		}

		
	}catch(error){
		console.log('Error checking code:', error);
		alert(error.message || '코드 확인 중 오류가 발생했습니다.')
		
	}
	
	
}





// 나가기
function doExit() {
	
	// 변경사항 확인
	if(hasUnsavedChanges()){
		console.log("탔냐");
		openModal('check');
		
		return;
	}
	
    window.location.href = `/`;
}


// 저장 안하고 나가기
async function doExitNow(){
	window.location.href = `/`;
}

// 저장하고 나가기
async function doSaveAndExit() {
	// 저장하기
    await doSave();
    //alert('저장 후 나가기 (구현 예정)'); // This alert might fire before doSave() finishes if not awaited
	window.location.href = `/`;
	
}

function doCopy() {
    // TODO: 실제 코드 복사 로직
    const code = document.getElementById('shareCode').textContent;
    alert('복사됨: ' + code + ' (구현 예정)');
}

function doKakaoShare() {
    // TODO: 카카오톡 공유 SDK 연동
    alert('카카오톡 공유 (구현 예정)');
}

// Event listeners for modal buttons (assuming they exist in HTML)
document.addEventListener('DOMContentLoaded', () => {
    const openModalBtn = document.getElementById('openModalBtn'); // Button to open the save confirmation modal
    const confirmSaveBtn = document.getElementById('confirmSaveBtn'); // Button inside the modal to confirm save
    const cancelSaveBtn = document.getElementById('cancelSaveBtn'); // Button inside the modal to cancel save

    if (openModalBtn) {
        openModalBtn.addEventListener('click', () => openModal('saveConfirmModal'));
    }
    if (confirmSaveBtn) {
        confirmSaveBtn.addEventListener('click', async () => {
            closeModal('saveConfirmModal');
            await doSave();
        });
    }
    if (cancelSaveBtn) {
        cancelSaveBtn.addEventListener('click', () => closeModal('saveConfirmModal'));
    }
});*/



// 편집 중 세션 만료 방지
setInterval(async () => {
    await fetch("/api/session/keep-alive");
}, 5 * 60 * 1000); // 5분마다