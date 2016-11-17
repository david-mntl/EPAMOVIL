
CREATE PROCEDURE deleteOrderCheck_Procedure
	@invoice_ID integer
	
AS
BEGIN

    SET NOCOUNT ON;
	declare @msg as VARCHAR(1000)

    Begin Tran deleteOrderCheck_Procedure

    Begin Try
		
		declare @idToVerify as integer;

		set @idToVerify = (SELECT Invoice_ID  FROM ORDER_CHECK where @invoice_ID = Invoice_ID);

		IF @invoice_ID = @idToVerify 

			Begin
				Delete FROM ORDER_CHECK WHERE Invoice_ID = @invoice_ID

				SET @msg = 'The Order was succesful deleted '
				print @msg
				COMMIT TRAN deleteOrderCheck_Procedure;
			END

		ELSE
			Begin 

				SET @msg = 'The Order does not exist'
				print @msg
				COMMIT TRAN deleteOrderCheck_Procedure;
			END
    End try
    Begin Catch

        SET @msg = 'Error: ' + ERROR_MESSAGE() + ' on line ' + CONVERT(NVARCHAR(255), ERROR_LINE() ) + '.'
		print @msg
        Rollback TRAN deleteOrderCheck_Procedure

    End Catch

END
