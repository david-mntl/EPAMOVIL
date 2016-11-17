
CREATE PROCEDURE insertSupplier_Procedure
	@id_Supplier integer,
	@active bit,
	@name varchar(100),
	@country varchar(100),
	@phone varchar(200)
	

AS
BEGIN

    SET NOCOUNT ON;
	declare @msg as VARCHAR(1000)

    Begin Tran insertSupplier_Procedure

    Begin Try
		
		declare @idToVerify as integer;

		set @idToVerify = (SELECT ID_Supplier FROM SUPPLIER where @id_Supplier = ID_Supplier);

		IF @id_Supplier = @idToVerify 
			Begin
				UPDATE SUPPLIER
					SET ID_Supplier=@id_Supplier, Active = @active, Name=@name, Country = @country,Phone = @phone 
					WHERE ID_Supplier= @id_Supplier;

				SET @msg = 'The Supplier already exist, so it was successful updated'
				print @msg
				COMMIT TRAN insertSupplier_Procedure;
			END

		ELSE
			Begin 
				insert into SUPPLIER(ID_Supplier, Active, Name, Country,Phone)
				values(@id_Supplier,@active, @name, @country, @phone) 

				SET @msg = 'The supplier has been created'
				print @msg
				COMMIT TRAN insertSupplier_Procedure;
			END
    End try
    Begin Catch

        SET @msg = 'Error: ' + ERROR_MESSAGE() + ' on line ' + CONVERT(NVARCHAR(255), ERROR_LINE() ) + '.'
		print @msg
        Rollback TRAN insertSupplier_Procedure

    End Catch

END
