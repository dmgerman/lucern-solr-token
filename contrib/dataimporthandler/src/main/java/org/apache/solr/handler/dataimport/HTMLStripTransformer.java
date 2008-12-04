begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
operator|.
name|HTMLStripReader
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
name|StringReader
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
comment|/**  * A Transformer implementation which strip off HTML tags using org.apache.solr.analysis.HTMLStripReader This is useful  * in case you don't need this HTML anyway.  *  * @version $Id$  * @see org.apache.solr.analysis.HTMLStripReader  * @since solr 1.4  */
end_comment
begin_class
DECL|class|HTMLStripTransformer
specifier|public
class|class
name|HTMLStripTransformer
extends|extends
name|Transformer
block|{
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|row
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|fields
init|=
name|context
operator|.
name|getAllEntityFields
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|field
range|:
name|fields
control|)
block|{
name|String
name|col
init|=
name|field
operator|.
name|get
argument_list|(
name|DataImporter
operator|.
name|COLUMN
argument_list|)
decl_stmt|;
name|String
name|splitHTML
init|=
name|field
operator|.
name|get
argument_list|(
name|STRIP_HTML
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|TRUE
operator|.
name|equals
argument_list|(
name|splitHTML
argument_list|)
condition|)
continue|continue;
name|Object
name|tmpVal
init|=
name|row
operator|.
name|get
argument_list|(
name|col
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmpVal
operator|==
literal|null
condition|)
continue|continue;
if|if
condition|(
name|tmpVal
operator|instanceof
name|List
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|inputs
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|tmpVal
decl_stmt|;
name|List
name|results
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|input
range|:
name|inputs
control|)
block|{
name|Object
name|o
init|=
name|stripHTML
argument_list|(
name|input
argument_list|,
name|col
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
name|results
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
name|row
operator|.
name|put
argument_list|(
name|col
argument_list|,
name|results
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|value
init|=
name|tmpVal
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Object
name|o
init|=
name|stripHTML
argument_list|(
name|value
argument_list|,
name|col
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
name|row
operator|.
name|put
argument_list|(
name|col
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|row
return|;
block|}
DECL|method|stripHTML
specifier|private
name|Object
name|stripHTML
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|column
parameter_list|)
block|{
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|StringReader
name|strReader
init|=
operator|new
name|StringReader
argument_list|(
name|value
argument_list|)
decl_stmt|;
try|try
block|{
name|HTMLStripReader
name|html
init|=
operator|new
name|HTMLStripReader
argument_list|(
name|strReader
argument_list|)
decl_stmt|;
name|char
index|[]
name|cbuf
init|=
operator|new
name|char
index|[
literal|1024
operator|*
literal|10
index|]
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|count
init|=
name|html
operator|.
name|read
argument_list|(
name|cbuf
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
operator|-
literal|1
condition|)
break|break;
comment|// end of stream mark is -1
if|if
condition|(
name|count
operator|>
literal|0
condition|)
name|out
operator|.
name|append
argument_list|(
name|cbuf
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
name|html
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Failed stripping HTML for column: "
operator|+
name|column
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|field|STRIP_HTML
specifier|public
specifier|static
specifier|final
name|String
name|STRIP_HTML
init|=
literal|"stripHTML"
decl_stmt|;
DECL|field|TRUE
specifier|public
specifier|static
specifier|final
name|String
name|TRUE
init|=
literal|"true"
decl_stmt|;
block|}
end_class
end_unit
