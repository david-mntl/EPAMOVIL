USE [DBCore3]
GO

DECLARE @RC int
DECLARE @id_Category int
DECLARE @details varchar(250)

Set @id_Category = 9
Set @details = 'new Cat'

-- TODO: Set parameter values here.

EXECUTE @RC = [dbo].[insertCategory_Procedure] 
   @id_Category
  ,@details
GO


