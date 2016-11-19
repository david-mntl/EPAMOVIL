
CREATE PROCEDURE insertProduct_Procedure
	@id_Product integer,
	@details varchar(100),
	@stock integer,
	@price integer,
	@taxFree bit,
	@id_Supplier integer,
	@id_Category integer,
	@active bit,
	@name varchar(100),
	@bOffice integer	

AS
BEGIN

    SET NOCOUNT ON;
	declare @msg as VARCHAR(1000)

    Begin Tran insertProduct_Procedure

    Begin Try
		
		declare @idToVerify as integer;

		set @idToVerify = (SELECT ID_Product FROM PRODUCT where @id_Product = ID_Product);

		IF @id_Product = @idToVerify 
			Begin
				UPDATE PRODUCT
					SET Details = @details, Stock = @stock , Price = @price , TaxFree = @taxFree, ID_Supplier = @id_Supplier, ID_Category = @id_Category, Active = @active , Name=@name,BOffice = @bOffice
					WHERE ID_Product= @id_Product;

				SET @msg = 'The Product already exist, so it was successful updated'
				print @msg
				COMMIT TRAN insertProduct_Procedure;
			END

		ELSE
			Begin
				IF @id_Product = 0
					BEGIN
					
						insert into PRODUCT(Details, Stock, Price, TaxFree, ID_Supplier, ID_Category, Active, Name, BOffice)
						values( @details, @stock, @price, @taxFree, @id_Supplier, @id_Category, @active,@name,@bOffice)

						SET @msg = 'The Product has been created'
						print @msg
						COMMIT TRAN insertProduct_Procedure;

					END
				ELSE
					BEGIN
					
						SET IDENTITY_INSERT [PRODUCT] on;
				 
						insert into PRODUCT(ID_Product, Details, Stock, Price, TaxFree, ID_Supplier, ID_Category, Active, Name, BOffice)
						values(@id_Product, @details, @stock, @price, @taxFree, @id_Supplier, @id_Category, @active,@name,@bOffice)

						SET IDENTITY_INSERT [PRODUCT] off;

						SET @msg = 'The Product has been created'
						print @msg
						COMMIT TRAN insertProduct_Procedure;
					END
			END
    End try
    Begin Catch

        SET @msg = 'Error: ' + ERROR_MESSAGE() + ' on line ' + CONVERT(NVARCHAR(255), ERROR_LINE() ) + '.'
		print @msg
        Rollback TRAN insertProduct_Procedure

    End Catch

END
