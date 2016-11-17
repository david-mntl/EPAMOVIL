
CREATE PROCEDURE deleteEmployee_Procedure
	@id_Employee integer
	
AS
BEGIN

    SET NOCOUNT ON;
	declare @msg as VARCHAR(1000)

    Begin Tran deleteEmployee_Procedure

    Begin Try
		
		declare @idToVerify as integer;

		set @idToVerify = (SELECT ID_Employee FROM EMPLOYEE where @id_Employee = ID_Employee);

		IF @id_Employee = @idToVerify 

			Begin
				Delete FROM EMPLOYEE WHERE ID_Employee = @id_Employee

				SET @msg = 'The customer was succesful deleted '
				print @msg
				COMMIT TRAN deleteEmployee_Procedure;
			END

		ELSE
			Begin 

				SET @msg = 'The customer does not exist'
				print @msg
				COMMIT TRAN deleteEmployee_Procedure;
			END
    End try
    Begin Catch

        SET @msg = 'Error: ' + ERROR_MESSAGE() + ' on line ' + CONVERT(NVARCHAR(255), ERROR_LINE() ) + '.'
		print @msg
        Rollback TRAN deleteEmployee_Procedure

    End Catch

END
