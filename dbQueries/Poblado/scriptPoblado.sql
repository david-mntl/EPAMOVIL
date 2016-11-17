USE [DBCore3]
GO

SELECT *
  FROM [dbo].CATEGORY,BOFFICE,SUPPLIER
GO

SELECT * FROM CATEGORY



INSERT INTO ROLE (Name)VALUES ('Ingeniero')
INSERT INTO ROLE (Name)VALUES ('Arquitecto')
INSERT INTO ROLE (Name)VALUES ('Administrador')
INSERT INTO ROLE (Name)VALUES ('Empleado')
INSERT INTO ROLE (Name)VALUES ('Distribuidor')

INSERT INTO BOFFICE(Name)VALUES ('Cartago')
INSERT INTO BOFFICE(Name)VALUES ('San Jose')
INSERT INTO BOFFICE(Name)VALUES ('Heredia')
INSERT INTO BOFFICE(Name)VALUES ('Guanacaste')
INSERT INTO BOFFICE(Name)VALUES ('Puntarenas')
INSERT INTO BOFFICE(Name)VALUES ('Limon')

INSERT INTO CATEGORY(Details)VALUES ('Maderas')
INSERT INTO CATEGORY(Details)VALUES ('Metal')
INSERT INTO CATEGORY(Details)VALUES ('Herramientas')
INSERT INTO CATEGORY(Details)VALUES ('Concreto')
INSERT INTO CATEGORY(Details)VALUES ('Vidrios')
INSERT INTO CATEGORY(Details)VALUES ('Mecanico')
INSERT INTO CATEGORY(Details)VALUES ('Electrico')

INSERT INTO SUPPLIER(ID_Supplier,Active,Name,Country,Phone)
	VALUES (1111,1,'Ostar','Costa Rica','26462632')
INSERT INTO SUPPLIER(ID_Supplier,Active,Name,Country,Phone)
	VALUES (2222,1,'Phillips','Costa Rica','874516')
INSERT INTO SUPPLIER(ID_Supplier,Active,Name,Country,Phone)
	VALUES (3333,1,'LG','Costa Rica','6688512')
INSERT INTO SUPPLIER(ID_Supplier,Active,Name,Country,Phone)
	VALUES (4444,1,'Alcatel','Costa Rica','32165498')
INSERT INTO SUPPLIER(ID_Supplier,Active,Name,Country,Phone)
	VALUES (5555,1,'Del Mueble','Costa Rica','4662255')

INSERT INTO [dbo].[PRODUCT] ([Details],[Stock],[Price],[TaxFree],[ID_Supplier],[ID_Category],[Active],[Name],[BOffice])
     VALUES('buen Vidrio Reforzado',30,2500,1,1111,6,1,'Vidrio Puerta Delantera',1)
INSERT INTO [dbo].[PRODUCT] ([Details],[Stock],[Price],[TaxFree],[ID_Supplier],[ID_Category],[Active],[Name],[BOffice])
     VALUES('buen techo Reforzado',30,30000,1,2222,5,1,'Techo',2)
INSERT INTO [dbo].[PRODUCT] ([Details],[Stock],[Price],[TaxFree],[ID_Supplier],[ID_Category],[Active],[Name],[BOffice])
     VALUES('buena conductividad',80,5000,1,3333,7,1,'Cable electrico',3)
INSERT INTO [dbo].[PRODUCT] ([Details],[Stock],[Price],[TaxFree],[ID_Supplier],[ID_Category],[Active],[Name],[BOffice])
     VALUES('Madera de Roble',60,40000,1,4444,2,1,'Puerta Delantera',3)
INSERT INTO [dbo].[PRODUCT] ([Details],[Stock],[Price],[TaxFree],[ID_Supplier],[ID_Category],[Active],[Name],[BOffice])
     VALUES('Tubos calidad',50,70000,1,2222,6,1,'Lavatorios baños',3)
INSERT INTO [dbo].[PRODUCT] ([Details],[Stock],[Price],[TaxFree],[ID_Supplier],[ID_Category],[Active],[Name],[BOffice])
     VALUES('Ceramica Reforzada',30,46500,1,5555,5,1,'Ceramica Suelo',2)
INSERT INTO [dbo].[PRODUCT] ([Details],[Stock],[Price],[TaxFree],[ID_Supplier],[ID_Category],[Active],[Name],[BOffice])
     VALUES('Clava lo que sea',40,6000,1,1111,4,1,'Martillo',6)

