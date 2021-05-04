CREATE TABLE watermarktable
(
TableName varchar(50),
WatermarkValue datetime,
);

INSERT INTO watermarktable
VALUES ('Message','1/1/2010 12:00:00 AM')

CREATE PROCEDURE sp_write_watermark @LastModifiedtime datetime, @TableName varchar(50)
AS

BEGIN

    UPDATE watermarktable
    SET [WatermarkValue] = @LastModifiedtime 
WHERE [TableName] = @TableName

END

CREATE PROCEDURE sp_read_watermark @TableName varchar(50)
AS

BEGIN

    SELECT CONVERT(VARCHAR, CAST(WATERMARKVALUE AS DATETIMEOFFSET), 127) as Watermark
    FROM watermarktable 
    where TableName = @TableName

END
