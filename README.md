# TaxSms
About the API

An API designed to assist government entities in overseeing corporate tax affairs is required. This involves establishing endpoints that enable the government to interact with the tax management system, including submitting tax data,create message schedule, retrieving records, updating information, and generating reports. Secure authentication and authorization protocols are also implemented to ensure that only authorized personnel can access and manage tax data. It is built with Java, Spring Boot, and Spring Framework. The API main URL /home.

Features

This API provides HTTP endpoint's and tools for the following:

Login: POST/login

User Management: POST/user/save, POST/user/saveChange, POST/user/blockUser, POST/user/changePassword, POST/user/findByName,... 

Role Management: POST/user/save, POST/user/saveChange, POST/user/blockUser, POST/user/changePassword, POST/user/findByName... 

Menu Management: POST/menu/save, POST/menu/delete, GET/menu/findMenuUserTree, GET/menu/findMenuTree,... 

Message Management: POST/message/save, POST/message/download, POST/message/findTotalMessFromLoadDetailReport, POST/message/findTotalMessByDate, POST/message/findTop5Scheduler, POST/message/reportDownload, POST/message/approve, POST/message/approve, POST/message/loadReport, POST/message/downloadExcel... 

Log Management: POST/log/save, POST/log/findPage.

Message Template Management: POST/smstemp/add, POST/smstemp/update, POST/smstemp/findByProgramId... 

...

Details

To test all the functions of the API, you can sign in with Admin account which has the admin role to access every features of the project. Provide parmeters in the body as in the picture below:

![429919733_771359138296625_3429921232609647446_n](https://github.com/duckhoa123/TaxSms/assets/101631798/a26360ec-f10a-44cb-9337-742d3359bc95)

And then you can copy the token generated in the returned object and paste it in the authorization header to use any end-point:

![429627685_1138495547578069_6471140224274014646_n](https://github.com/duckhoa123/TaxSms/assets/101631798/4fe4ca0a-e98f-4095-a0c8-700354c56c9d)

For any end-point that send file to computer such as: generating report, pdf files,... You provide parameters required in the body and click send and download. For example:

![429810080_955466185979719_1043603622534313457_n](https://github.com/duckhoa123/TaxSms/assets/101631798/b147bfb8-9454-4fa6-ab82-e4b8478903fd)

Then you will file as below: 

![429130063_3766021070310181_6286286107425634522_n](https://github.com/duckhoa123/TaxSms/assets/101631798/a5e9f10a-d3ac-49d7-8c37-65e137b17cad)

Technologies used

This project was developed with:

Java 11 (Java Development Kit - JDK: 11.0.9)

Spring Boot 2.2.2

Google Oauth 1.30.6

Google API Client 1.30.9

Log4j 1.2.17

Google Guava 28.1-jre

Apache Common 1.4

Github Pagehelper 1.2.13

Microsoft SQL Server 

Apache Tika 1.23

Apache Poi 4.1.2

Maven

Execution

You need to have Microsoft SQL Server installed on your machine to run the API. After installed, you can use the .bak file attached in Database folder to restore the database and then type in your username and password for SQL server:


<img width="717" alt="423472153_2395130190878117_8661648126697593368_n" src="https://github.com/duckhoa123/TaxSms/assets/101631798/fe4f6392-0f7d-419c-b3e1-6d7b6f417705">

You have to change the paths in the picture below, where the File folder is located in your machine

![423541930_364861796431252_7196994490574629606_n](https://github.com/duckhoa123/TaxSms/assets/101631798/42a15a0c-9deb-4d8b-af55-af6439910a31)

By default, the API will be available at http://localhost:6005/home

Thank you !!!






 
