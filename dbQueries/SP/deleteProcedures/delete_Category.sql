
CREATE PROCEDURE deleteCategory_Procedure
	@id_Category integer
	
AS
BEGIN

    SET NOCOUNT ON;
	declare @msg as VARCHAR(1000)

    Begin Tran deleteCategory_Procedure

    Begin Try
		
		declare @idToVerify as integer;

		set @idToVerify = (SELECT ID_Category FROM CATEGORY where @id_Category = ID_Category);

		IF @id_Category = @idToVerify 

			Begin
				Delete FROM CATEGORY WHERE ID_Category = @id_Category

				SET @msg = 'The category was succesful deleted '
				print @msg
				COMMIT TRAN deleteCategory_Procedure;
			END

		ELSE
			Begin 

				SET @msg = 'The category does not exist'
				print @msg
				COMMIT TRAN deleteCategory_Procedure;
			END
    End try
    Begin Catch

        SET @msg = 'Error: ' + ERROR_MESSAGE() + ' on line ' + CONVERT(NVARCHAR(255), ERROR_LINE() ) + '.'
		print @msg
        Rollback TRAN deleteCategory_Procedure

    End Catch

END
