

docker build -t blueprint.azurecr.io/bpcs/mssql-server:db_jwcv1 .  



docker run --name db_jwcv1 -p 1533:1433 -d blueprint.azurecr.io/bpcs/mssql-server:db_jwcv1  


docker commit db_jwcv1  
docker push blueprint.azurecr.io/bpcs/mssql-server:db_jwcv1  

