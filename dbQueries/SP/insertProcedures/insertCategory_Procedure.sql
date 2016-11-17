
CREATE PROCEDURE insertCategory_Procedure
	@id_Category integer,
	@details varchar(250)

AS
BEGIN

    SET NOCOUNT ON;
	declare @msg as VARCHAR(1000)

    Begin Tran insertCategory_Procedure

    Begin Try
		
		declare @idToVerify as integer;

		set @idToVerify = (SELECT ID_Category FROM CATEGORY where @id_Category = ID_Category);

		IF @id_Category = @idToVerify 
			Begin
				UPDATE CATEGORY
					SET Details = @details
					WHERE ID_Category= @id_Category;

				SET @msg = 'The Category already exist, so it was successful updated'
				print @msg
				COMMIT TRAN insertCategory_Procedure;
			END

		ELSE
			Begin 
				insert into CATEGORY(Details)
				values(@details) 

				SET @msg = 'The Category has been created'
				print @msg
				COMMIT TRAN insertCategory_Procedure;
			END
    End try
    Begin Catch

        SET @msg = 'Error: ' + ERROR_MESSAGE() + ' on line ' + CONVERT(NVARCHAR(255), ERROR_LINE() ) + '.'
		print @msg
        Rollback TRAN insertCategory_Procedure

    End Catch

END
