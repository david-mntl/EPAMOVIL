
CREATE PROCEDURE inserCustomer_Procedure
	@ID_Card integer,
	@name varchar(100),
	@lastName1 varchar(100),
	@lastName2 varchar(100),
	@residence varchar(200),
	@nickName varchar(100),
	@Secure_Pass varchar(100),
	@BDate datetime,
	@phone varchar(100),
	@email varchar(100),
	@priorityLevel int,
	@Active bit

AS
BEGIN

    SET NOCOUNT ON;
	declare @msg as VARCHAR(1000)

    Begin Tran inserCustomer_Procedure

    Begin Try
		
		declare @idToVerify as integer;

		set @idToVerify = (SELECT ID_Card FROM CUSTOMER where @ID_Card = ID_Card);

		IF @ID_Card = @idToVerify 
			Begin
				UPDATE CUSTOMER
					SET ID_Card=@ID_Card, Name=@name, LastName1 = @lastName1 , LastName2 = @lastName2, Residence = @residence , Nickname = @nickName, Secure_Pass = @Secure_Pass , BDate = @BDate , Phone = @phone, Email = @email, PriorityLevel = @priorityLevel , Active = @Active
					WHERE ID_Card= @ID_Card;

				SET @msg = 'The client already existed, so it was successful updated'
				print @msg
				COMMIT TRAN inserCustomer_Procedure;
			END

		ELSE
			Begin 
				insert into CUSTOMER(ID_Card, Name, LastName1, LastName2, Residence, Nickname, Secure_Pass, BDate, Phone,Email,PriorityLevel,Active)
				values(@ID_Card, @name, @lastName1, @lastName2, @residence, @nickName, @Secure_Pass, @BDate, @phone,@email,@priorityLevel,@Active)

				SET @msg = 'The client has been created'
				print @msg
				COMMIT TRAN inserCustomer_Procedure;
			END
    End try
    Begin Catch

        SET @msg = 'Error: ' + ERROR_MESSAGE() + ' on line ' + CONVERT(NVARCHAR(255), ERROR_LINE() ) + '.'
		print @msg
        Rollback TRAN inserCustomer_Procedure

    End Catch

END
