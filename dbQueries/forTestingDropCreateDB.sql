/*************** DELETE ALL DATABASE *********************/


ALTER TABLE [EMPLOYEE] DROP [FK_EMPLOYEE_BOFFICE_ID_BOffice];

ALTER TABLE [ORDER_CHECK] DROP [FK_ORDER_CHECK_BOFFICE_BOffice_ID];

ALTER TABLE [ORDER_CHECK] DROP [FK_ORDER_CHECK_CUSTOMER_ID_Card];

ALTER TABLE [PRODUCT] DROP [FK_PRODUCT_BOFFICE_BOffice];

ALTER TABLE [PRODUCT] DROP [FK_PRODUCT_CATEGORY_ID_Category];

ALTER TABLE [PRODUCT] DROP [FK_PRODUCT_SUPPLIER_ID_Supplier];

ALTER TABLE [PURCHASE_ITEM] DROP [FK_PURCHASE_ITEM_ORDER_CHECK_Invoice_ID];

ALTER TABLE [PURCHASE_ITEM] DROP [FK_PURCHASE_ITEM_PRODUCT_ID_Product];

ALTER TABLE [EMPLOYEE] DROP [FK_EMPLOYEE_ROLE_ID_Role];

ALTER TABLE [PURCHASE_ITEM] DROP [FK_ORDER_CHECK_EMPLOYEE_ID_Employee];



DROP TABLE [BOFFICE];

DROP TABLE [CATEGORY];
DROP TABLE [CUSTOMER];

DROP TABLE [EMPLOYEE];

DROP TABLE [ORDER_CHECK];
DROP TABLE [PRODUCT];

DROP TABLE [PURCHASE_ITEM];
DROP TABLE [SUPPLIER];

DROP TABLE [ROLE];



/******************Create DATABASE ************************/





	CREATE DATABASE DBCore2;

/****** Table [dbo].[BOFFICE]    ******/

CREATE TABLE [dbo].[BOFFICE](
	[BOffice_ID] [int] PRIMARY KEY IDENTITY(1,1) NOT NULL,
	[Name] [varchar](100) NOT NULL,
)
GO



/****** Table [dbo].[CATEGORY]   ******/

CREATE TABLE [dbo].[CATEGORY](
	[ID_Category] [int] PRIMARY KEY IDENTITY(1,1) NOT NULL,
	[Details] [varchar](250) NULL,
)
GO




/****** Table [dbo].[CUSTOMER]  ******/

CREATE TABLE [dbo].[CUSTOMER](
	[ID_Card] [int] PRIMARY KEY NOT NULL,
	[Name] [varchar](100) NOT NULL,
	[LastName1] [varchar](100) NOT NULL,
	[LastName2] [varchar](100) NOT NULL,
	[Residence] [varchar](200) NULL,
	[Nickname] [varchar](100) UNIQUE NOT NULL,
	[Secure_Pass] [varchar](100) NOT NULL,
	[BDate] [datetime] NOT NULL,
	[Phone] [varchar](100) NOT NULL,
	[Email] [varchar](100) UNIQUE NOT NULL,
	[PriorityLevel] [int] NOT NULL,
	[Active] [bit] NOT NULL,
)
GO


/******  Table [dbo].[EMPLOYEE] ******/

CREATE TABLE [dbo].[EMPLOYEE](
	[ID_Employee] [int] PRIMARY KEY NOT NULL,
	[Name] [varchar](100) NOT NULL,
	[LastName1] [varchar](100) NOT NULL,
	[LastName2] [varchar](100) NOT NULL,
	[Residence] [varchar](200) NULL,
	[Nickname] [varchar](100) UNIQUE NOT NULL,
	[Secure_Pass] [varchar](100) NOT NULL,
	[BDate] [datetime] NULL,
	[Phone] [varchar](100) NULL,
	[Email] [varchar](100) UNIQUE NOT NULL,
	[Position] [varchar](100) NOT NULL,
	[Active] [bit] NOT NULL,
	[BOffice_ID] [int] NOT NULL,
	[Role] [int] NOT NULL,
)

GO

/****** Table [dbo].[ORDER_CHECK] ******/

CREATE TABLE [dbo].[ORDER_CHECK](
	[Invoice_ID] [int] PRIMARY KEY IDENTITY(1,1) NOT NULL,
	[Date_Time] [datetime] NOT NULL,
	[Order_Status] [varchar](100) NOT NULL,
	[Active] [int] NOT NULL,
	[Customer_ID] [int] NOT NULL,
	[Employee_ID][int] NOT NULL,
	[BOffice] [int] NOT NULL,
)

GO


/******  Table [dbo].[PRODUCT]   ******/

CREATE TABLE [dbo].[PRODUCT](
	[ID_Product] [int] PRIMARY KEY IDENTITY(1,1) NOT NULL,
	[Details] [varchar](200) NULL,
	[Stock] [int] NOT NULL,
	[Price] [int] NOT NULL,
	[TaxFree] [bit] NOT NULL,
	[ID_Supplier] [int] NOT NULL,
	[ID_Category] [int] NOT NULL,
	[Active] [bit] NOT NULL,
	[Name] [varchar](50) NULL,
	[BOffice] [int] NOT NULL,
)
GO


/****** Table [dbo].[PURCHASE_ITEM]  ******/


CREATE TABLE [dbo].[PURCHASE_ITEM](
	[PurchasedItem_ID] [int] PRIMARY KEY IDENTITY(1,1) NOT NULL,
	[Invoice_ID] [int] NULL,
	[Quantity] [int] NULL,
	[Price] [int] NULL,
	[Product_ID] [int] NULL
)

GO

/******  Table [dbo].[SUPPLIER]   ******/

CREATE TABLE [dbo].[SUPPLIER](
	[ID_Supplier] [int] PRIMARY KEY NOT NULL,
	[Active] [bit] NOT NULL,
	[Name] [varchar](50) NOT NULL,
	[Country] [varchar](50) NULL,
	[Phone] [varchar](50) NULL,
)
GO

/******  Table [dbo].[ROLE]   ******/

CREATE TABLE [dbo].[ROLE](
	[ID_Role] [int] PRIMARY KEY IDENTITY(1,1) NOT NULL,	
	[Name] [varchar](50) NOT NULL,		
)
GO
