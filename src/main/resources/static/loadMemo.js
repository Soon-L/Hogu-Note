// 편집 중 세션 만료 방지
setInterval(async () => {
    await fetch("/api/session/keep-alive");
}, 5 * 60 * 1000); // 5분마다