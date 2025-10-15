# 家政服务管理系统 API 测试脚本

Write-Host "=== 家政服务管理系统 API 测试 ===" -ForegroundColor Green

# 测试管理员登录
Write-Host "`n1. 测试管理员登录..." -ForegroundColor Yellow

$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/login" -Method POST -Body $loginBody -ContentType "application/json"
    Write-Host "登录成功!" -ForegroundColor Green
    Write-Host "Token: $($loginResponse.data.token.Substring(0, [Math]::Min(20, $loginResponse.data.token.Length)))..." -ForegroundColor Cyan
    
    $token = $loginResponse.data.token
    
    # 测试获取管理员信息
    Write-Host "`n2. 测试获取管理员信息..." -ForegroundColor Yellow
    
    $headers = @{
        "Authorization" = "Bearer $token"
    }
    
    $profileResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/profile" -Method GET -Headers $headers
    Write-Host "获取管理员信息成功!" -ForegroundColor Green
    Write-Host "用户名: $($profileResponse.data.username)" -ForegroundColor Cyan
    Write-Host "真实姓名: $($profileResponse.data.realName)" -ForegroundColor Cyan
    
    # 测试获取统计数据
    Write-Host "`n3. 测试获取统计数据..." -ForegroundColor Yellow
    
    $statsResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/statistics/basic" -Method GET -Headers $headers
    Write-Host "获取统计数据成功!" -ForegroundColor Green
    Write-Host "用户数量: $($statsResponse.data.userCount)" -ForegroundColor Cyan
    Write-Host "服务者数量: $($statsResponse.data.providerCount)" -ForegroundColor Cyan
    
    # 测试获取系统公告
    Write-Host "`n4. 测试获取系统公告..." -ForegroundColor Yellow
    
    $noticesResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/notices" -Method GET -Headers $headers
    Write-Host "获取系统公告成功!" -ForegroundColor Green
    Write-Host "公告数量: $($noticesResponse.data.totalElements)" -ForegroundColor Cyan
    
    # 测试获取服务分类
    Write-Host "`n5. 测试获取服务分类..." -ForegroundColor Yellow
    
    $categoriesResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/categories" -Method GET -Headers $headers
    Write-Host "获取服务分类成功!" -ForegroundColor Green
    Write-Host "分类数量: $($categoriesResponse.data.totalElements)" -ForegroundColor Cyan
    
    Write-Host "`n=== 所有API测试通过! ===" -ForegroundColor Green
    
} catch {
    Write-Host "API测试失败: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "请确保应用已启动并运行在 http://localhost:8080" -ForegroundColor Yellow
}
