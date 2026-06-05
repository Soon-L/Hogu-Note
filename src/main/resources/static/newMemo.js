const originalMemo = document.getElementsById('originalMemo') // 메모 원본

export let currentMemoData = {}; // 모달이 열릴 때 현재 입력값을 저장할 변수






function openModal(name) {
	
	currentMemoData = {
	originalMemo : originalMemo.value
	};
	
  document.getElementById('modal-' + name).classList.add('active');
}
 
export function closeModal(name) {
  document.getElementById('modal-' + name).classList.remove('active');
}
 
function closeOnOverlay(e, name) {
  if (e.target === document.getElementById('modal-' + name)) closeModal(name);
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
 
function doSaveAndExit() {
  // TODO: 저장 후 이동
  alert('저장 후 나가기 (구현 예정)');
  window.location.href = 'main.html';
}
 
function doCopy() {
  // TODO: 실제 코드 복사 로직
  const code = document.getElementById('shareCode').textContent;
  alert('복사됨: ' + code + ' (구현 예정)');
}
 
function doKakaoShare() {
  // TODO: 카카오 공유 SDK 연동
  alert('카카오톡 공유 (구현 예정)');
}