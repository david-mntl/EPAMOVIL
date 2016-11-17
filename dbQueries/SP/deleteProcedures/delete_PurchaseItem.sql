
CREATE PROCEDURE deletePurchaseItem_Procedure
	@purchasedItem_ID integer
	
AS
BEGIN

    SET NOCOUNT ON;
	declare @msg as VARCHAR(1000)

    Begin Tran deletePurchaseItem_Procedure

    Begin Try
		
		declare @idToVerify as integer;

		set @idToVerify = (SELECT PurchasedItem_ID FROM PURCHASE_ITEM where @purchasedItem_ID = PurchasedItem_ID);

		IF @purchasedItem_ID = @idToVerify 

			Begin
				Delete FROM PURCHASE_ITEM WHERE PurchasedItem_ID = @purchasedItem_ID

				SET @msg = 'The purchased item was succesful deleted '
				print @msg
				COMMIT TRAN deletePurchaseItem_Procedure;
			END

		ELSE
			Begin 

				SET @msg = 'The purchased item does not exist'
				print @msg
				COMMIT TRAN deletePurchaseItem_Procedure;
			END
    End try
    Begin Catch

        SET @msg = 'Error: ' + ERROR_MESSAGE() + ' on line ' + CONVERT(NVARCHAR(255), ERROR_LINE() ) + '.'
		print @msg
        Rollback TRAN deletePurchaseItem_Procedure

    End Catch

END
