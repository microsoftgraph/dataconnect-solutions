/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.model.userdetails.db

// Class meant to be used to convert a Spark DataSet into a database table.
// Must match the exact schema in the database. This is the reason we do not follow the scala field naming convention
// The "id" column in the database is not defined since its values are autogenerated by the DB and not by the Spark job
case class School(
                   employee_profile_id: String,
                   employee_profile_version: String,
                   school: String
                 )
