
CREATE PROCEDURE deleteProduct_Procedure
	@id_Product integer
	
AS
BEGIN

    SET NOCOUNT ON;
	declare @msg as VARCHAR(1000)

    Begin Tran deleteProduct_Procedure

    Begin Try
		
		declare @idToVerify as integer;

		set @idToVerify = (SELECT ID_Product  FROM PRODUCT where @id_Product = ID_Product);

		IF @id_Product = @idToVerify 

			Begin
				Delete FROM PRODUCT WHERE ID_Product = @id_Product

				SET @msg = 'The product was succesful deleted '
				print @msg
				COMMIT TRAN deleteProduct_Procedure;
			END

		ELSE
			Begin 

				SET @msg = 'The product does not exist'
				print @msg
				COMMIT TRAN deleteProduct_Procedure;
			END
    End try
    Begin Catch

        SET @msg = 'Error: ' + ERROR_MESSAGE() + ' on line ' + CONVERT(NVARCHAR(255), ERROR_LINE() ) + '.'
		print @msg
        Rollback TRAN deleteProduct_Procedure

    End Catch

END
