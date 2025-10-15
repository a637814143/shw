# 测试认证API

Write-Host "=== 测试认证API ===" -ForegroundColor Green

# 测试登录
Write-Host "`n1. 测试登录..." -ForegroundColor Yellow

$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/test-login" -Method POST -Body $loginBody -ContentType "application/json"
    Write-Host "登录成功!" -ForegroundColor Green
    Write-Host "响应: $($loginResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan
    
    if ($loginResponse.code -eq 200) {
        $token = $loginResponse.data.token
        Write-Host "Token: $($token.Substring(0, [Math]::Min(20, $token.Length)))..." -ForegroundColor Cyan
        
        # 测试Token验证
        Write-Host "`n2. 测试Token验证..." -ForegroundColor Yellow
        
        $headers = @{
            "Authorization" = "Bearer $token"
        }
        
        $verifyResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/test-verify" -Method GET -Headers $headers
        Write-Host "Token验证成功!" -ForegroundColor Green
        Write-Host "验证响应: $($verifyResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan
    }
    
} catch {
    Write-Host "测试失败: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "响应状态: $($_.Exception.Response.StatusCode)" -ForegroundColor Yellow
    }
}
