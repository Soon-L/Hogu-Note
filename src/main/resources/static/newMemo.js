// currentData 변수
const originalMemoInput = document.getElementById('originalMemo');
const pwInput = document.getElementById('savePassword'); // 현재 메모 저장할때 비번
const personalCode = document.getElementById('personalCode'); // 현재 메모의 코드
const memoId = document.getElementById('memoId'); // 현재 메모id

let currentMemoData = {}; // 모달이 열릴 때 현재 입력값을 저장할 변수



// 불러오기 변수
const loadCode = document.getElementById('loadCode'); // 불러올 메모의 코드
const loadPassword = document.getElementById('loadPassword'); // 입력한 불러올 메모의 비번

let currentPersonalCode = ''; // 코드 저장 변수(비밀메모 모달 사용시 필요)
let getOriginalMemo; // 현재 메모의 변경 전 메모
let getPassword; //DB에 저장된 불러올 메모의 비번
let getMemoData;


// 상황 구분 변수
let loadAfterSave = false; // 일반 저장, 불러오기 저장 구분
let exitAfterSave = false; // 저장 후 나가기, 나가기 구분
let loadAfterUpdate = false; // 수정 구분용
let exitAfterUpdate = false; // 수정 후 나가기 구분용

const body = document.body;
const checkExist = body.dataset.checkExist === "true"; // 새메모, 불러온 메모 구분


// textarea의 readonly 여부로 VIEWER 판별 (서버에서 Thymeleaf로 설정됨)
const isViewer = document.getElementById('originalMemo').readOnly;

// 변경사항 변수
let unSavedChanges;

// ── 새로고침/탭닫기 경고 (WRITER 전용) ──
let isSaved = false; // 저장 완료 후 true → 경고 해제

window.addEventListener('beforeunload', (e) => {
    if (isViewer) return;                          // VIEWER는 경고 없음
    if (isSaved) return;                           // 저장 완료 상태면 경고 없음
    if (!originalMemoInput.value.trim()) return;   // 내용 없으면 경고 없음

    // 새로고침은 저장 유도만 (CLOSE 발행 안 함 - 취소 시 오발행 방지)
    e.preventDefault();
    e.returnValue = '';
});









console.log("checkExist: "+ checkExist);




// 모달 열기
async function openModal(name) {
	console.log(name+" 모달 열림");

    document.getElementById('modal-' + name).classList.add('active');
}


// 모달창 닫기
function closeModal(name) {
	console.log(name+" 모달 닫힘");
    document.getElementById('modal-' + name).classList.remove('active');
}


// 모달창 오버레이 닫기
function closeOnOverlay(e, name) {
    if (e.target === document.getElementById('modal-' + name)) closeModal(name);
}


// 현재 데이터
async function currentData(pcode, pw){
	
	console.log("데이터 최신화");
	
	// 기존메모
	if(checkExist){
		currentMemoData = {
			memoId : memoId.textContent,
		    originalMemo: originalMemoInput.value,
			summaryMemo : null,
			personalCode: pcode,
		    password: pw
		};
		
		console.log("현재 메모: "+currentMemoData.originalMemo);
	}
	// 새메모
	else{
		currentMemoData = {
		    originalMemo: originalMemoInput.value,
			summaryMemo : null,
			personalCode: pcode,
		    password: pw
		};
	}
	

}







// 저장하기
async function doSave() {
	
	console.log("doSave 시작");

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
        isSaved = true;                  // beforeunload 경고 해제
        currentMemoData = {};

    } catch (error) {
        console.error('Error saving memo:', error);
    }
	
	closeModal('save');
	
	console.log("doSave 끝");
	
	// 모달창 닫힌 후 저장됐다고 뜨는 알림창 필요할듯
}





// 수정하기
async function doUpdate() {
	//새로고침 방지
	// event.preventDefault();
	
	// 현재 데이터
	currentData(personalCode.textContent.trim());
	console.log(currentMemoData);

    try {
        const response = await fetch('/api/memo/update', {
            method: 'PUT',
			credentials: "same-origin",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(currentMemoData)
        });
		

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || '메모 저장 실패');
        }

        isSaved = true;                  // beforeunload 경고 해제
        currentMemoData = {};

    } catch (error) {
        console.error('Error saving memo:', error);
    }
	
	closeModal('update');
	
	// 모달창 닫힌 후 저장됐다고 뜨는 알림창 필요할듯
}



// 시크릿메모로 저장 활성화
function secretmode(){
	
	console.log("secretmode 시작");
	
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
	
	console.log("secretmode end");
}




// 새메모 -> 저장 모달에서 저장하기 클릭
async function doSaveClick() {
	
	// 저장하기
	await doSave();
	
	// 불러올 메모 데이터 확인
	if(loadAfterSave){
		await getMemo(loadCode.value.trim());

		// 불러오기, 나가기 변경사항 확인
		await checkChange();
	}
	
	// 저장 -> 나가기
	if(exitAfterSave){
		await doExitNow();
	}

	

}




// 불러온 메모 -> 저장 모달에서 저장하기 클릭
async function doUpdateClick() {
	
	// 수정하기
	await doUpdate();
	
	// 불러올 메모 데이터 확인
	if(loadAfterUpdate){
		await getMemo(loadCode.value.trim());

		// 불러오기, 나가기 변경사항 확인
		await checkChange();
	}
	
	// 수정 -> 나가기
	if(exitAfterUpdate){
		// 불러오기, 나가기 변경사항 확인
		await doExitNow();
	}

}






// 불러오기 -> 저장 모달열기
function saveAndLoad() {
	console.log("saveAndLoad 시작");
	
    loadAfterSave = true;
	
	closeModal('saveAndLoad');
	closeModal('load');
    openModal("save");
	
	console.log("saveAndLoad 끝");
}


// 불러오기 -> 수정 모달 열기
function updateAndLoad(){
	
	loadAfterUpdate = true;
	
	closeModal('updateAndLoad');
	closeModal('load');
	openModal('update');
	
}


// 일반 저장 모달열기
function openSave() {
    loadAfterSave = false;
    openModal("save");
}



// 저장 안하고 불러오기
async function doNotSaveLoad(){
	
	loadAfterSave = true;
	
	// 불러올 메모 데이터 확인
	await getMemo(loadCode.value.trim());

	// 불러오기, 나가기 변경사항 확인
	await checkChange();
}



// 수정 안하고 불러오기
async function doNotUpdateLoad(){
	
	loadAfterUpdate = true;
	
	// 불러올 메모 데이터 확인
	await getMemo(loadCode.value.trim());

	// 불러오기, 나가기 변경사항 확인
	await checkChange();
}







// 코드로 메모 불러오기
async function doLoad(){
	console.log("doLoad 시작");
	
	// 입력한 코드
	const code = loadCode.value.trim();
	
		
	if(!code){
		
		alert('코드를 입력해주세요.');
		return;
	}
	
	currentPersonalCode = code;
	
	console.log("메모는: "+ checkExist);

	// 불러올 메모 원본 데이터 확인
	if(checkExist){
		console.log("getmemo 탈거임");
		await getMemo(personalCode.textContent.trim());
	}

	
	
	// 불러오기 전 변경사항 확인
	unSavedChanges = await hasUnsavedChanges();
	if(unSavedChanges){
		
		console.log("변경사항 있음");
		console.log("메모는: "+ checkExist);
		
		if(checkExist){
			console.log("변경사항 감지 수정");

			// 저장할건지 안할건지 묻기
			await openModal('updateAndLoad');
			return;
		}
		else{
			console.log("변경사항 감지 저장");

			// 저장할건지 안할건지 묻기
			await openModal('saveAndLoad');
			return;
		}
		
	}
	
	
	try{
		const response = await fetch(`/api/memo/${code}/type`, {
			
			method: 'POST',
			credentials: "same-origin",
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({personalCode: code})
			
		});
		
		
		if(!response.ok){
			const errorData = await response.json();
			throw new Error(errorData.message || '코드 확인 실패');
		}
		
		// 불러올 메모 데이터 확인
		await getMemo(code);
		
		// 비밀메모 체크
		await checkSecret();

		
	}catch(error){
		console.log('Error checking code:', error);
		alert(error.message || '코드 확인 중 오류가 발생했습니다.')
		
	}
	
	console.log("doLoad 끝");
	
	
}


// 불러오기 -> 비번입력창 열리기
async function checkSecret(){
	
	console.log("비번 확인 탐");
	
	// 비밀메모
	if(getPassword !== null){
		openModal('password');
	}
	// 공개메모
	else{
		await doLoadNow();	
	}
}



// DB에서 데이터 가져오기
async function getMemo(pcode){
	await fetch(`/api/memo/${pcode}`)
	    .then(response => response.json()) // JSON 형태로 파싱
	    .then(data => {
	        console.log(data.message); // "성공"
			console.log(data.dto);

			getPassword = data.dto.password; // 비번
			getMemoData = data.dto; // 메모 통으로
					
	    })
	    .catch(error => console.error('Error:', error));
	
}


// DB에서 원본 메모 가져오기
async function getMemoOriginalMemo(pcode){
	await fetch(`/api/memo/${pcode}`)
	    .then(response => response.json()) // JSON 형태로 파싱
	    .then(data => {
	        console.log(data.message); // "성공"
			console.log(data.dto);

			getOriginalMemo = data.dto.originalMemo; // 원본 메모
					
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
	console.log(checkExist);
	// 기존 메모
	if(checkExist){
		console.log(getMemoData);
		console.log(getMemoData.originalMemo);
		return getMemoData.originalMemo !== currentMemoData.originalMemo;
	}
	else{
		console.log("새메모 경우");
		return currentMemoData.originalMemo !== "";
	}

}


// 코드로 이동하기
async function doLoadNow(){
	
	console.log("불러온 코드: "+ currentPersonalCode);
	
	await currentData(currentPersonalCode, loadPassword.value.trim());
	
	console.log("데이터 확인: "+currentMemoData.personalCode);
	console.log("데이터 확인: "+currentMemoData.originalMemo);
	console.log("데이터 확인: "+currentMemoData.password);
	
	
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
		
		window.location.href = `/memo/${currentMemoData.personalCode}`;


		
	}catch(error){
		console.log('Error loading code:', error);
		alert(error.message || '메로를 불러오는 중 오류가 발생했습니다.')
	}
	
	console.log("doLoadNow 끝");
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
	
	const code = personalCode.textContent.trim();
	console.log("코드 가져옴?"+code);
	
	if(isViewer){
		await doExitNow();
		return;
	}
	
	// 원본 데이터
	if(checkExist){
		await getMemo(code);
	}

	
	// 현재 데이터
	await currentData(code);
	
	unSavedChanges = await hasUnsavedChanges();
	
	// 변경사항 저장 여부
	if(unSavedChanges){
		console.log("변경사항 타긴 함")
		
		// 수정 후 나가기
		if(checkExist){
			openModal('updateAndExit');
			return;
		}
		// 저장 후 나가기
		else{
			openModal('saveAndExit');
			return;
		}
		

	}
	
    //await doExitNow();
}


// 저장x 나가기
async function doExitNow(){
    isSaved = true; // beforeunload 경고 없이 이동
    sendClose();    // 참여자에게 CLOSE 알림
	window.location.href = `/`;
}


// 저장o -> 나가기
function doSaveAndExit() {
	console.log("saveAndExit 시작");
	
    exitAfterSave = true;
	
	closeModal('saveAndExit');
    openModal("save");
	
	console.log("saveAndLoad 끝");
}


// 수정o -> 나가기
function doUpdateAndExit() {
	console.log("updateAndExit 시작");
	
    exitAfterUpdate = true;
	
	closeModal('updateAndExit');
    openModal("update");
	
	console.log("saveAndLoad 끝");
}




// 변경사항 확인
async function checkChange(){
	
	console.log("checkChange 시작");
	
	// 불러오기 -> 저장만 작동
	if (loadAfterSave) {
	    loadAfterSave = false;
		
		// 저장할지 유무 모달 닫기
		closeModal('saveAndLoad');
		
		// 비밀메모 비번 체크
		await checkSecret();
	
	}
	
	
/*	// 나가기 -> 저장만 작동
	if(exitAfterSave){
		exitAfterSave = false;
		await doExitNow();
	}*/
	
	
	// 수정하기
	if(loadAfterUpdate){
		
		loadAfterUpdate = false;
		
		// 저장할지 유무 모달 닫기
		closeModal('updateAndLoad');

		// 비밀메모 비번 체크
		await checkSecret();

	}
	
	
/*	// 나가기 -> 수정만 작동
	console.log("판별용: "+exitAfterSave);
	if(exitAfterSave){
		exitAfterSave = false;
		await doExitNow();
	}*/
}





// ===== WebSocket (STOMP) =====

let client; // sendMemo()에서 참조할 수 있도록 스코프 밖에 선언

window.addEventListener('load', () => {
    const code = personalCode.textContent.trim();

    client = new StompJs.Client({
        webSocketFactory: () => new SockJS('/ws/memo'),
        reconnectDelay: 3000,

        onConnect: () => {
            console.log('WebSocket 연결됨. role:', isViewer ? 'VIEWER' : 'WRITER', '/ code:', code);

            // WRITER, VIEWER 모두 구독 (수신은 둘 다)
            client.subscribe(`/topic/memo/${code}`, (frame) => {
                const msg = JSON.parse(frame.body);
                displayMemo(msg);
            });
        },

        onDisconnect: () => console.log('WebSocket 연결 해제'),

        onStompError: (frame) => console.error('STOMP 오류:', frame)
    });

    client.activate();
});


// 내가 입력할 때마다 발행 (textarea oninput에서 호출)
function sendMemo() {
    if (isViewer) return;                        // VIEWER는 발행 차단
    if (!client || !client.connected) return;

    const code = personalCode.textContent.trim();
    const content = originalMemoInput.value;

    client.publish({
        destination: `/app/memo/${code}`,
        body: JSON.stringify({
            code: code,
            content: content,
            type: 'UPDATE'
        })
    });
}


// WRITER 이탈 시 참여자에게 알림
function sendClose() {
    if (isViewer) return;
    if (!client || !client.connected) return;

    const code = personalCode.textContent.trim();

    client.publish({
        destination: `/app/memo/${code}`,
        body: JSON.stringify({
            code: code,
            type: 'CLOSE'
        })
    });
}


// 상대방 메모 수신 시 textarea 업데이트
function displayMemo(msg) {
    if (msg.type === 'UPDATE') {
        // VIEWER는 항상 반영, WRITER는 포커스 밖일 때만 반영 (입력 중 덮어쓰기 방지)
        if (isViewer || document.activeElement !== originalMemoInput) {
            originalMemoInput.value = msg.content;
        }
    }

    // WRITER가 나가면 VIEWER는 메인으로 이동
    if (msg.type === 'CLOSE' && isViewer) {
        alert('작성자가 메모장을 닫았습니다.');
        window.location.href = '/';
    }
}




