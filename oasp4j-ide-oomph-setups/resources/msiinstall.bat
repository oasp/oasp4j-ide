@echo off
rem %1 name of the msi package in the temp folder without extension
rem %2 subfolder to be used in software as TARGETDIR. can be empty

msiexec /a temp\%1.msi /qn TARGETDIR="%~dp0software\%2" /L* temp\%1_log.txt