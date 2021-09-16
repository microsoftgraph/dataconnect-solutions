

docker build -t contosohub.azurecr.io/microsoft-gdc/mssql-server:db_jwcv1 .  



docker run --name db_jwcv1 -p 1533:1433 -d contosohub.azurecr.io/microsoft-gdc/mssql-server:db_jwcv1  


docker commit db_jwcv1  
docker push contosohub.azurecr.io/microsoft-gdc/mssql-server:db_jwcv1  

