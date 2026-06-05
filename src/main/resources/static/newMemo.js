// 입력 요소들
const originalMemoInput = document.getElementById('originalMemo');
const modalOriginalMemo =document.getElementById('modalOriginalMemo');
const pwInput = document.getElementById('savePassword');
const personalCode = document.getElementById('personalCode');
const modalPersonalCode = document.getElementById('modalPersonalCode');

export let currentMemoData = {}; // 모달이 열릴 때 현재 입력값을 저장할 변수

// Function to open a modal and populate currentMemoData
function openModal(name) {

	// 현재 데이터
    currentMemoData = {
        originalMemo: originalMemoInput.value,
		personalCode: personalCode.textContent,
        pw: pwInput.value
    };

    // Populate modal content for confirmation (assuming modal elements exist)
/*    const modalTitleSpan = document.getElementById('modalTitle');
    const modalContentSpan = document.getElementById('modalContent');
    const modalPersonalCodeSpan = document.getElementById('modalPersonalCode');*/

	
	// 모달창에 hidden으로 숨겨둔 원본메모
    modalOriginalMemo.textContent = currentMemoData.originalMemo;
	modalPersonalCode.textCotent = currentMemoData.personalCode;
    document.getElementById('modal-' + name).classList.add('active');
}




// 모달창 닫기
export function closeModal(name) {
    document.getElementById('modal-' + name).classList.remove('active');
}

function closeOnOverlay(e, name) {
    if (e.target === document.getElementById('modal-' + name)) closeModal(name);
}




// 저장하기
async function doSave() {
    const responseDiv = document.getElementById('response'); // Assuming an element with id 'response' exists

    try {
        const response = await fetch('/api/memo', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(currentMemoData) // Use the data collected when modal was opened
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || '메모 저장 실패');
        }

        const savedMemo = await response.json();
        if (responseDiv) {
            responseDiv.className = 'success';
            responseDiv.innerHTML = `
                <p><strong>메모가 성공적으로 저장되었습니다!</strong></p>
                <p>ID: ${savedMemo.memoId}</p>
                <p>원본메모: ${savedMemo.originalMemo}</p>
                <p>비밀번호: ${savedMemo.pw}</p>
                <p>개인코드: ${savedMemo.personalCode}</p>
                <p>생성일: ${new Date(savedMemo.createdAt).toLocaleString()}</p>
                <p>수정일: ${new Date(savedMemo.updatedAt).toLocaleString()}</p>
            `;
        }

        // 폼 초기화
        const memoForm = document.getElementById('memoForm');
        if (memoForm) memoForm.reset();
        currentMemoData = {}; // 저장 후 데이터 초기화

    } catch (error) {
        if (responseDiv) {
            responseDiv.className = 'error';
            responseDiv.innerHTML = `<p>오류 발생: ${error.message}</p>`;
        }
        console.error('Error saving memo:', error);
    }
}




function doLoad() {
    const code = document.getElementById('loadCode').value.trim();
    if (!code) { alert('코드를 입력해주세요.'); return; }
    // TODO: 불러오기 로직
    alert('불러오기: ' + code);
    closeModal('load');
    document.getElementById('loadCode').value = '';
}

function doExit() {
    window.location.href = 'main.html';
}

async function doSaveAndExit() {
    // Save logic before redirecting
    await doSave(); // Wait for save to complete
    alert('저장 후 나가기 (구현 예정)'); // This alert might fire before doSave() finishes if not awaited
    window.location.href = 'main.html';
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
});