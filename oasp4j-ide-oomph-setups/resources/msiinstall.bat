@echo off

msiexec /a temp\%1.msi /qn TARGETDIR="%~dp0software" /L* temp\%1_log.txt