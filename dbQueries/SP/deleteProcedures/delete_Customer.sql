
CREATE PROCEDURE deleteCustomer_Procedure
	@id_Card integer
	
AS
BEGIN

    SET NOCOUNT ON;
	declare @msg as VARCHAR(1000)

    Begin Tran deleteCustomer_Procedure

    Begin Try
		
		declare @idToVerify as integer;

		set @idToVerify = (SELECT ID_Card FROM CUSTOMER where @id_Card = ID_Card);

		IF @id_Card = @idToVerify 

			Begin
				Delete FROM CUSTOMER WHERE ID_Card = @id_Card

				SET @msg = 'The customer was succesful deleted '
				print @msg
				COMMIT TRAN deleteCustomer_Procedure;
			END

		ELSE
			Begin 

				SET @msg = 'The customer does not exist'
				print @msg
				COMMIT TRAN deleteCustomer_Procedure;
			END
    End try
    Begin Catch

        SET @msg = 'Error: ' + ERROR_MESSAGE() + ' on line ' + CONVERT(NVARCHAR(255), ERROR_LINE() ) + '.'
		print @msg
        Rollback TRAN deleteCustomer_Procedure

    End Catch

END
