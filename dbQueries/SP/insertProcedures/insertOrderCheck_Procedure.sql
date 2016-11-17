
CREATE PROCEDURE insertOrderCheck_Procedure
	@invoice_ID integer,
	@dateTime datetime,
	@orderStatus varchar(100),
	@active integer,
	@customer_ID integer,
	@employee_ID integer,
	@BOffice integer
	

AS
BEGIN

    SET NOCOUNT ON;
	declare @msg as VARCHAR(1000)

    Begin Tran insertOrderCheck_Procedure

    Begin Try
		
		declare @idToVerify as integer;

		set @idToVerify = (SELECT Invoice_ID FROM ORDER_CHECK where @invoice_ID = Invoice_ID);

		IF @invoice_ID = @idToVerify 
			Begin
				UPDATE ORDER_CHECK
					SET  Date_Time = @dateTime, Order_Status=@orderStatus, Active = @active,Customer_ID = @customer_ID , Employee_ID= @employee_ID , BOffice= @BOffice
					WHERE Invoice_ID= @invoice_ID;

				SET @msg = 'The Order already exist, so it was successful updated'
				print @msg
				COMMIT TRAN insertOrderCheck_Procedure;
			END

		ELSE
			Begin 
				insert into ORDER_CHECK(Date_Time, Order_Status, Active, Customer_ID,Employee_ID, BOffice)
				values(@dateTime,@orderStatus, @active, @customer_ID, @employee_ID,@BOffice) 

				SET @msg = 'The supplier has been created'
				print @msg
				COMMIT TRAN insertOrderCheck_Procedure;
			END
    End try
    Begin Catch

        SET @msg = 'Error: ' + ERROR_MESSAGE() + ' on line ' + CONVERT(NVARCHAR(255), ERROR_LINE() ) + '.'
		print @msg
        Rollback TRAN insertOrderCheck_Procedure

    End Catch

END
