@echo off

msiexec /a temp\%1.msi /qn TARGETDIR="%~dp0software\%2" /L* temp\%1_log.txt