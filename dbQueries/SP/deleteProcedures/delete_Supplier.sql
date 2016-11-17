
CREATE PROCEDURE deleteSupplier_Procedure
	@id_Supplier integer
	
AS
BEGIN

    SET NOCOUNT ON;
	declare @msg as VARCHAR(1000)

    Begin Tran deleteSupplier_Procedure

    Begin Try
		
		declare @idToVerify as integer;

		set @idToVerify = (SELECT ID_Supplier FROM SUPPLIER where @id_Supplier = ID_Supplier);

		IF @id_Supplier = @idToVerify 

			Begin
				Delete FROM SUPPLIER WHERE ID_Supplier = @id_Supplier

				SET @msg = 'The supplier was succesful deleted '
				print @msg
				COMMIT TRAN deleteSupplier_Procedure;
			END

		ELSE
			Begin 

				SET @msg = 'The supplier does not exist'
				print @msg
				COMMIT TRAN deleteSupplier_Procedure;
			END
    End try
    Begin Catch

        SET @msg = 'Error: ' + ERROR_MESSAGE() + ' on line ' + CONVERT(NVARCHAR(255), ERROR_LINE() ) + '.'
		print @msg
        Rollback TRAN deleteSupplier_Procedure

    End Catch

END
