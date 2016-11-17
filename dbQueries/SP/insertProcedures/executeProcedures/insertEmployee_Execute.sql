USE [DBCore3]
GO

DECLARE @RC int
DECLARE @ID_Employee int
DECLARE @name varchar(100)
DECLARE @lastName1 varchar(100)
DECLARE @lastName2 varchar(100)
DECLARE @residence varchar(200)
DECLARE @nickName varchar(100)
DECLARE @Secure_Pass varchar(100)
DECLARE @BDate datetime
DECLARE @phone varchar(100)
DECLARE @email varchar(100)
DECLARE @position varchar(100)
DECLARE @Active bit
DECLARE @BOffice_ID int
DECLARE @Role int

Set @ID_Employee = 2121
Set @name = 'David'
Set @lastName1 = 'Monestel'
Set @lastName2 = 'Aguilar'
Set @residence = 'CostaRica'
Set @nickName = 'mntl'
Set @Secure_Pass = '123'
Set @BDate = 01/06/1996
Set @phone = '89731119'
Set @email = 'david@hotmail.com'
Set @position = 'Ingeniero'
Set @Active = 1
Set @BOffice_ID = 3
Set @Role = 1


-- TODO: Set parameter values here.

EXECUTE @RC = [dbo].[insertEmployee_Procedure] 
   @ID_Employee
  ,@name
  ,@lastName1
  ,@lastName2
  ,@residence
  ,@nickName
  ,@Secure_Pass
  ,@BDate
  ,@phone
  ,@email
  ,@position
  ,@Active
  ,@BOffice_ID
  ,@Role
GO


