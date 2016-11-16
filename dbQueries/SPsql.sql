Create Procedure createNewUser
	@Name varchar(50),
	@LastName1 varchar(50),
	@LastName2 varchar(50),
	@Residence varchar(50),
	@NickName varchar(50),
	@Password varchar(50),
	@BDate varchar(50),
	@Phone varchar(50),
	@Email varchar(50)

As
	Insert INTO CUSTOMER VALUES(Name,LastName1,LastName2,Residence,Nickname, 
	Password,BDate,Phoe,Email,PriorityLevel, Active) (@Name,@LastNmae1,@LastName2
	,@Residence,@NickName,@Password,@BDate,@phone, @Email,1,1);



	
	Insert INTO CUSTOMER VALUES(Name,LastName1,LastName2,Residence,Nickname, 
	Password,BDate,Phoe,Email,PriorityLevel, Active) (@Name,@LastNmae1,@LastName2
	,@Residence,@NickName,@Password,@BDate,@phone, @Email,1,1);
	
GO
