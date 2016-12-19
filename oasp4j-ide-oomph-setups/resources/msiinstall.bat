@echo off

msiexec /a "%~dp0test\%1.msi" /qn TARGETDIR="%~dp0software" /L* log.txt