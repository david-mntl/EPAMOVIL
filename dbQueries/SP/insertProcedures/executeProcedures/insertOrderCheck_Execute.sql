USE [DBCore2]
GO

DECLARE @RC int
DECLARE @invoice_ID int
DECLARE @dateTime datetime
DECLARE @orderStatus integer
DECLARE @active int
DECLARE @customer_ID int
DECLARE @employee_ID int
DECLARE @BOffice int

SET @invoice_ID = 70
SET @dateTime = 17/11/2016
SET @orderStatus = 1
SET @active = 1
SET @customer_ID = 2121
SET @employee_ID = 2121
SET @BOffice = 4


-- TODO: Set parameter values here.

EXECUTE @RC = [dbo].[insertOrderCheck_Procedure] 
   @invoice_ID
  ,@dateTime
  ,@orderStatus
  ,@active
  ,@customer_ID
  ,@employee_ID
  ,@BOffice
GO


