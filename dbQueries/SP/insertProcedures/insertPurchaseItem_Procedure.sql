
CREATE PROCEDURE insertPurchaseItem_Procedure
	@purchasedItem_ID integer,
	@invoice_ID integer,
	@quantity integer,
	@price integer,
	@product_ID integer	


AS
BEGIN

    SET NOCOUNT ON;
	declare @msg as VARCHAR(1000)

    Begin Tran insertPurchaseItem_Procedure

    Begin Try
		
		declare @idToVerify as integer;

		set @idToVerify = (SELECT PurchasedItem_ID FROM PURCHASE_ITEM where @purchasedItem_ID = PurchasedItem_ID);

		IF @purchasedItem_ID = @idToVerify 
			Begin
				UPDATE PURCHASE_ITEM
					SET  Invoice_ID = @invoice_ID, Quantity=@quantity, Price = @price,Product_ID = @product_ID
					WHERE PurchasedItem_ID= @purchasedItem_ID;

				SET @msg = 'The purchased item already exist, so it was successful updated'
				print @msg
				COMMIT TRAN insertPurchaseItem_Procedure;
			END

		ELSE
			BEGIN
				IF @purchasedItem_ID =0
					Begin 					

						insert into PURCHASE_ITEM(Invoice_ID,Quantity,Price,Product_ID)
						values(@invoice_ID,@quantity, @price, @product_ID) 						

						SET @msg = 'The purchased item has been created'
						print @msg
						COMMIT TRAN insertPurchaseItem_Procedure;
					END
			
			
				ELSE
					Begin 
						SET IDENTITY_INSERT PURCHASE_ITEM on;

						insert into PURCHASE_ITEM(PurchasedItem_ID,Invoice_ID,Quantity,Price,Product_ID)
						values(@purchasedItem_ID,@invoice_ID,@quantity, @price, @product_ID) 

						SET IDENTITY_INSERT PURCHASE_ITEM off;

						SET @msg = 'The purchased item has been created'
						print @msg
						COMMIT TRAN insertPurchaseItem_Procedure;
					END
			END
    End try
    Begin Catch

        SET @msg = 'Error: ' + ERROR_MESSAGE() + ' on line ' + CONVERT(NVARCHAR(255), ERROR_LINE() ) + '.'
		print @msg
        Rollback TRAN insertPurchaseItem_Procedure

    End Catch

END
