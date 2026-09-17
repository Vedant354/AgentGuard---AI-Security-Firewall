@echo off
title AgentGuard Launcher

echo ==========================================
echo           AGENTGUARD STARTING
echo ==========================================
echo.

echo Starting Spring Boot Backend...
start "AgentGuard Backend" cmd /k "cd /d C:\Study\Projects\AgentGuard - AI Agent Firewall\backend\agentguard-backend && mvn spring-boot:run"

echo Starting React Frontend...
start "AgentGuard Frontend" cmd /k "cd /d C:\Study\Projects\AgentGuard - AI Agent Firewall\frontend\agentguard-dashboard && npm run dev"

echo.
echo ==========================================
echo Backend:  http://localhost:8080
echo Frontend: http://localhost:5173
echo ==========================================
echo.
echo Wait a few seconds for both servers to start.
pause