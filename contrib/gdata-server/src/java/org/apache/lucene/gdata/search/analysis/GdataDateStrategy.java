begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.search.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|analysis
package|;
end_package
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|data
operator|.
name|ServerBaseEntry
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|config
operator|.
name|IndexSchemaField
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|index
operator|.
name|GdataIndexerException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|DateTime
import|;
end_import
begin_comment
comment|/**  * This content strategy retrieves a so called GData Date from a RFC 3339  * timestamp format. The format will be parsed and indexed as a timestamp value.  *   * @author Simon Willnauer  *   */
end_comment
begin_class
DECL|class|GdataDateStrategy
specifier|public
class|class
name|GdataDateStrategy
extends|extends
name|ContentStrategy
block|{
DECL|method|GdataDateStrategy
specifier|protected
name|GdataDateStrategy
parameter_list|(
name|IndexSchemaField
name|fieldConfiguration
parameter_list|)
block|{
name|super
argument_list|(
name|fieldConfiguration
argument_list|)
expr_stmt|;
block|}
comment|/**      * @throws NotIndexableException      * @see org.apache.lucene.gdata.search.analysis.ContentStrategy#processIndexable(org.apache.lucene.gdata.search.analysis.Indexable)      */
annotation|@
name|Override
DECL|method|processIndexable
specifier|public
name|void
name|processIndexable
parameter_list|(
name|Indexable
argument_list|<
name|?
extends|extends
name|Node
argument_list|,
name|?
extends|extends
name|ServerBaseEntry
argument_list|>
name|indexable
parameter_list|)
throws|throws
name|NotIndexableException
block|{
name|String
name|path
init|=
name|this
operator|.
name|config
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|Node
name|node
decl_stmt|;
try|try
block|{
name|node
operator|=
name|indexable
operator|.
name|applyPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathExpressionException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|NotIndexableException
argument_list|(
literal|"Can not apply path -- "
operator|+
name|path
operator|+
literal|" FieldConfig: "
operator|+
name|this
operator|.
name|config
argument_list|)
throw|;
block|}
if|if
condition|(
name|node
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NotIndexableException
argument_list|(
literal|"Could not retrieve content for schema field: "
operator|+
name|this
operator|.
name|config
argument_list|)
throw|;
name|String
name|textContent
init|=
name|node
operator|.
name|getTextContent
argument_list|()
decl_stmt|;
try|try
block|{
name|this
operator|.
name|content
operator|=
name|getTimeStamp
argument_list|(
name|textContent
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NotIndexableException
argument_list|(
literal|"Can not parse GData date -- "
operator|+
name|textContent
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see org.apache.lucene.gdata.search.analysis.ContentStrategy#createLuceneField()      */
annotation|@
name|Override
DECL|method|createLuceneField
specifier|public
name|Field
index|[]
name|createLuceneField
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|fieldName
operator|==
literal|null
condition|)
throw|throw
operator|new
name|GdataIndexerException
argument_list|(
literal|"Required field 'name' is null -- "
operator|+
name|this
operator|.
name|config
argument_list|)
throw|;
if|if
condition|(
name|this
operator|.
name|content
operator|==
literal|null
condition|)
throw|throw
operator|new
name|GdataIndexerException
argument_list|(
literal|"Required field 'content' is null -- "
operator|+
name|this
operator|.
name|config
argument_list|)
throw|;
if|if
condition|(
name|this
operator|.
name|content
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return
operator|new
name|Field
index|[
literal|0
index|]
return|;
name|Field
name|retValue
init|=
operator|new
name|Field
argument_list|(
name|this
operator|.
name|fieldName
argument_list|,
name|this
operator|.
name|content
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
decl_stmt|;
name|float
name|boost
init|=
name|this
operator|.
name|config
operator|.
name|getBoost
argument_list|()
decl_stmt|;
if|if
condition|(
name|boost
operator|!=
literal|1.0f
condition|)
name|retValue
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
operator|new
name|Field
index|[]
block|{
name|retValue
block|}
return|;
block|}
DECL|method|getTimeStamp
specifier|private
specifier|static
name|String
name|getTimeStamp
parameter_list|(
name|String
name|dateString
parameter_list|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|DateTime
operator|.
name|parseDateTimeChoice
argument_list|(
name|dateString
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class
end_unit
