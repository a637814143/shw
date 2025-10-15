# 简单的API测试

Write-Host "测试管理员登录..." -ForegroundColor Yellow

$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/login" -Method POST -Body $loginBody -ContentType "application/json"
    Write-Host "登录成功!" -ForegroundColor Green
    Write-Host "响应数据: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan
} catch {
    Write-Host "登录失败: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "响应内容: $($_.Exception.Response)" -ForegroundColor Yellow
}
