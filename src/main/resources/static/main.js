function loadMemo() {
  const code = document.getElementById('codeInput').value.trim();
  if (!code) {
    alert('코드를 입력해주세요.');
    return;
  }
  window.location.href = 'newMemo.html?code=' + encodeURIComponent(code);
}
 
document.addEventListener('DOMContentLoaded', function () {
  document.getElementById('codeInput').addEventListener('keydown', function (e) {
    if (e.key === 'Enter') loadMemo();
  });
});
 