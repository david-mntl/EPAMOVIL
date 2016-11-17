USE [DBCore2]
GO

DECLARE @RC int
DECLARE @purchasedItem_ID int
DECLARE @invoice_ID int
DECLARE @quantity int
DECLARE @price int
DECLARE @product_ID int

SET @purchasedItem_ID = 0
SET @invoice_ID = 1
SET @quantity = 20
SET @price = 2000
SET @product_ID = 1

-- TODO: Set parameter values here.

EXECUTE @RC = [dbo].[insertPurchaseItem_Procedure] 
   @purchasedItem_ID
  ,@invoice_ID
  ,@quantity
  ,@price
  ,@product_ID
GO


