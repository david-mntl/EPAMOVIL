USE [DBCore3]
GO

DECLARE @RC int
DECLARE @id_Product int
DECLARE @details varchar(100)
DECLARE @stock int
DECLARE @price int
DECLARE @taxFree bit
DECLARE @id_Supplier int
DECLARE @id_Category int
DECLARE @active bit
DECLARE @name varchar(100)
DECLARE @bOffice int

-- NOTA CUANDO SE QUIERE CREAR UN NUEVO PRODUCTO SE USA EL id_Product = 0
-- Ya que como es autoincremental y empieza en 1, el 0 nunca estara

Set @id_Product = 0
Set @details = 'nuevo Producto'
Set @stock = 30
Set @price = 2500
Set @taxFree = 1
Set @id_Supplier = 1111
Set @id_Category = 4
Set @active = 1
Set @name = 'desatornillador'
Set @bOffice = 3

-- TODO: Set parameter values here.

EXECUTE @RC = [dbo].[insertProduct_Procedure] 
   @id_Product
  ,@details
  ,@stock
  ,@price
  ,@taxFree
  ,@id_Supplier
  ,@id_Category
  ,@active
  ,@name
  ,@bOffice
GO


