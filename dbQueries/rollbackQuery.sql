Use DBCore;
GO


restore database DBCore 
from disk = 'C:\Program Files\Microsoft SQL Server\MSSQL13.SQLSERVER\MSSQL\Backup\DBCore.bak'
with replace,stats,recovery
GO

--This should be the full backup