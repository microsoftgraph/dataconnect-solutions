#!/usr/bin/env bash
# Run Microsoft SQl Server and initialization script (at the same time)
/usr/bpcs/app/run-initialization.sh & /opt/mssql/bin/sqlservr