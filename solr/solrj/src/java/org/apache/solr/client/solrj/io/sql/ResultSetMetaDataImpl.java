begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.io.sql
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|sql
package|;
end_package
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSetMetaData
import|;
end_import
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Types
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|Tuple
import|;
end_import
begin_class
DECL|class|ResultSetMetaDataImpl
class|class
name|ResultSetMetaDataImpl
implements|implements
name|ResultSetMetaData
block|{
DECL|field|resultSet
specifier|private
specifier|final
name|ResultSetImpl
name|resultSet
decl_stmt|;
DECL|field|metadataTuple
specifier|private
specifier|final
name|Tuple
name|metadataTuple
decl_stmt|;
DECL|field|firstTuple
specifier|private
specifier|final
name|Tuple
name|firstTuple
decl_stmt|;
DECL|method|ResultSetMetaDataImpl
name|ResultSetMetaDataImpl
parameter_list|(
name|ResultSetImpl
name|resultSet
parameter_list|)
block|{
name|this
operator|.
name|resultSet
operator|=
name|resultSet
expr_stmt|;
name|this
operator|.
name|metadataTuple
operator|=
name|this
operator|.
name|resultSet
operator|.
name|getMetadataTuple
argument_list|()
expr_stmt|;
name|this
operator|.
name|firstTuple
operator|=
name|this
operator|.
name|resultSet
operator|.
name|getFirstTuple
argument_list|()
expr_stmt|;
block|}
DECL|method|getColumnClass
specifier|private
name|Class
name|getColumnClass
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
name|Object
name|o
init|=
name|this
operator|.
name|firstTuple
operator|.
name|get
argument_list|(
name|this
operator|.
name|getColumnLabel
argument_list|(
name|column
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
name|String
operator|.
name|class
return|;
comment|//Nulls will only be present with Strings.
block|}
else|else
block|{
return|return
name|o
operator|.
name|getClass
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getColumnCount
specifier|public
name|int
name|getColumnCount
parameter_list|()
throws|throws
name|SQLException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|metadataTuple
operator|.
name|getStrings
argument_list|(
literal|"fields"
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Unable to determine fields for column count"
argument_list|)
throw|;
block|}
return|return
name|fields
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isAutoIncrement
specifier|public
name|boolean
name|isAutoIncrement
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|isCaseSensitive
specifier|public
name|boolean
name|isCaseSensitive
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|isSearchable
specifier|public
name|boolean
name|isSearchable
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|isCurrency
specifier|public
name|boolean
name|isCurrency
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|isNullable
specifier|public
name|int
name|isNullable
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|isSigned
specifier|public
name|boolean
name|isSigned
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getColumnDisplaySize
specifier|public
name|int
name|getColumnDisplaySize
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|this
operator|.
name|getColumnLabel
argument_list|(
name|column
argument_list|)
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getColumnLabel
specifier|public
name|String
name|getColumnLabel
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|aliases
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|metadataTuple
operator|.
name|get
argument_list|(
literal|"aliases"
argument_list|)
decl_stmt|;
return|return
name|aliases
operator|.
name|get
argument_list|(
name|this
operator|.
name|getColumnName
argument_list|(
name|column
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getColumnName
specifier|public
name|String
name|getColumnName
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|columns
init|=
name|metadataTuple
operator|.
name|getStrings
argument_list|(
literal|"fields"
argument_list|)
decl_stmt|;
if|if
condition|(
name|column
argument_list|<
literal|1
operator|||
name|column
argument_list|>
name|columns
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Column index "
operator|+
name|column
operator|+
literal|" is not valid"
argument_list|)
throw|;
block|}
return|return
name|columns
operator|.
name|get
argument_list|(
name|column
operator|-
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSchemaName
specifier|public
name|String
name|getSchemaName
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getPrecision
specifier|public
name|int
name|getPrecision
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getScale
specifier|public
name|int
name|getScale
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getTableName
specifier|public
name|String
name|getTableName
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getCatalogName
specifier|public
name|String
name|getCatalogName
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getColumnType
specifier|public
name|int
name|getColumnType
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
switch|switch
condition|(
name|getColumnTypeName
argument_list|(
name|column
argument_list|)
condition|)
block|{
case|case
literal|"String"
case|:
return|return
name|Types
operator|.
name|VARCHAR
return|;
case|case
literal|"Integer"
case|:
return|return
name|Types
operator|.
name|INTEGER
return|;
case|case
literal|"Long"
case|:
return|return
name|Types
operator|.
name|DOUBLE
return|;
case|case
literal|"Double"
case|:
return|return
name|Types
operator|.
name|DOUBLE
return|;
default|default:
return|return
name|Types
operator|.
name|JAVA_OBJECT
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getColumnTypeName
specifier|public
name|String
name|getColumnTypeName
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|this
operator|.
name|getColumnClass
argument_list|(
name|column
argument_list|)
operator|.
name|getSimpleName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isReadOnly
specifier|public
name|boolean
name|isReadOnly
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|isWritable
specifier|public
name|boolean
name|isWritable
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|isDefinitelyWritable
specifier|public
name|boolean
name|isDefinitelyWritable
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getColumnClassName
specifier|public
name|String
name|getColumnClassName
parameter_list|(
name|int
name|column
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|this
operator|.
name|getColumnClass
argument_list|(
name|column
argument_list|)
operator|.
name|getTypeName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|unwrap
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|iface
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|isWrapperFor
specifier|public
name|boolean
name|isWrapperFor
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|iface
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|false
return|;
block|}
block|}
end_class
end_unit
