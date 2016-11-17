USE [DBCore3]
GO

DECLARE @RC int
DECLARE @id_Supplier int
DECLARE @active bit
DECLARE @name varchar(100)
DECLARE @country varchar(100)
DECLARE @phone varchar(200)


Set @id_Supplier = 2121
Set @active = 1
Set @name = 'new sup'
Set @country = 'CR'
Set @phone = '999999'


-- TODO: Set parameter values here.

EXECUTE @RC = [dbo].[insertSupplier_Procedure] 
   @id_Supplier
  ,@active
  ,@name
  ,@country
  ,@phone
GO


