begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|HTMLStripTransformer
operator|.
name|TRUE
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Clob
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
begin_comment
comment|/**  * {@link Transformer} instance which converts a {@link Clob} to a {@link String}.  *<p>  * Refer to<a href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *<p>  *<b>This API is experimental and subject to change</b>  *  * @since solr 1.4  */
end_comment
begin_class
DECL|class|ClobTransformer
specifier|public
class|class
name|ClobTransformer
extends|extends
name|Transformer
block|{
annotation|@
name|Override
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|aRow
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
range|:
name|context
operator|.
name|getAllEntityFields
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|TRUE
operator|.
name|equals
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|CLOB
argument_list|)
argument_list|)
condition|)
continue|continue;
name|String
name|column
init|=
name|map
operator|.
name|get
argument_list|(
name|DataImporter
operator|.
name|COLUMN
argument_list|)
decl_stmt|;
name|String
name|srcCol
init|=
name|map
operator|.
name|get
argument_list|(
name|RegexTransformer
operator|.
name|SRC_COL_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcCol
operator|==
literal|null
condition|)
name|srcCol
operator|=
name|column
expr_stmt|;
name|Object
name|o
init|=
name|aRow
operator|.
name|get
argument_list|(
name|srcCol
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|List
condition|)
block|{
name|List
argument_list|<
name|Clob
argument_list|>
name|inputs
init|=
operator|(
name|List
argument_list|<
name|Clob
argument_list|>
operator|)
name|o
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|input
range|:
name|inputs
control|)
block|{
if|if
condition|(
name|input
operator|instanceof
name|Clob
condition|)
block|{
name|Clob
name|clob
init|=
operator|(
name|Clob
operator|)
name|input
decl_stmt|;
name|results
operator|.
name|add
argument_list|(
name|readFromClob
argument_list|(
name|clob
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|aRow
operator|.
name|put
argument_list|(
name|column
argument_list|,
name|results
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|o
operator|instanceof
name|Clob
condition|)
block|{
name|Clob
name|clob
init|=
operator|(
name|Clob
operator|)
name|o
decl_stmt|;
name|aRow
operator|.
name|put
argument_list|(
name|column
argument_list|,
name|readFromClob
argument_list|(
name|clob
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|aRow
return|;
block|}
DECL|method|readFromClob
specifier|private
name|String
name|readFromClob
parameter_list|(
name|Clob
name|clob
parameter_list|)
block|{
name|Reader
name|reader
init|=
name|FieldReaderDataSource
operator|.
name|readCharStream
argument_list|(
name|clob
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|len
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|DataImportHandlerException
operator|.
name|wrapAndThrow
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|field|CLOB
specifier|public
specifier|static
specifier|final
name|String
name|CLOB
init|=
literal|"clob"
decl_stmt|;
block|}
end_class
end_unit
