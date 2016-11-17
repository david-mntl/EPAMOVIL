
CREATE PROCEDURE insertEmployee_Procedure
	@ID_Employee integer,
	@name varchar(100),
	@lastName1 varchar(100),
	@lastName2 varchar(100),
	@residence varchar(200),
	@nickName varchar(100),
	@Secure_Pass varchar(100),
	@BDate datetime,
	@phone varchar(100),
	@email varchar(100),
	@position varchar(100),
	@Active bit,
	@BOffice_ID integer,
	@Role integer

AS
BEGIN

    SET NOCOUNT ON;
	declare @msg as VARCHAR(1000)

    Begin Tran insertEmployee_Procedure

    Begin Try
		
		declare @idToVerify as integer;

		set @idToVerify = (SELECT ID_Employee FROM EMPLOYEE where @ID_Employee = ID_Employee);

		IF @ID_Employee = @idToVerify 
			Begin
				UPDATE EMPLOYEE
					SET ID_Employee = @ID_Employee, Name=@name, LastName1 = @lastName1 , LastName2 = @lastName2, Residence = @residence , Nickname = @nickName, Secure_Pass = @Secure_Pass , BDate = @BDate , Phone = @phone, Email = @email, Position = @position, Active = @Active, BOffice_ID = @BOffice_ID, Role = @Role
					WHERE ID_Employee= @ID_Employee;

				SET @msg = 'The Employee already existed, so it was successful updated'
				print @msg
				COMMIT TRAN insertEmployee_Procedure;
			END

		ELSE
			Begin 
				insert into EMPLOYEE(ID_Employee, Name, LastName1, LastName2, Residence, Nickname, Secure_Pass, BDate, Phone,Email,Position,Active,BOffice_ID,Role)
				values(@ID_Employee, @name, @lastName1, @lastName2, @residence, @nickName, @Secure_Pass, @BDate, @phone,@email,@position,@Active,@BOffice_ID,@Role)

				SET @msg = 'The Employee has been created'
				print @msg
				COMMIT TRAN insertEmployee_Procedure;
			END
    End try
    Begin Catch

        SET @msg = 'Error: ' + ERROR_MESSAGE() + ' on line ' + CONVERT(NVARCHAR(255), ERROR_LINE() ) + '.'
		print @msg
        Rollback TRAN insertEmployee_Procedure

    End Catch

END
