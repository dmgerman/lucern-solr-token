begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|search
operator|.
name|*
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
name|search
operator|.
name|function
operator|.
name|DocValues
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
name|Serializable
import|;
end_import
begin_comment
comment|/**  * Instantiates {@link org.apache.solr.search.function.DocValues} for a particular reader.  *<br>  * Often used when creating a {@link FunctionQuery}.  *  * @version $Id$  */
end_comment
begin_class
DECL|class|ValueSource
specifier|public
specifier|abstract
class|class
name|ValueSource
implements|implements
name|Serializable
block|{
DECL|method|getValues
specifier|public
specifier|abstract
name|DocValues
name|getValues
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|equals
specifier|public
specifier|abstract
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
function_decl|;
DECL|method|hashCode
specifier|public
specifier|abstract
name|int
name|hashCode
parameter_list|()
function_decl|;
comment|/** description of field, used in explain() */
DECL|method|description
specifier|public
specifier|abstract
name|String
name|description
parameter_list|()
function_decl|;
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|description
argument_list|()
return|;
block|}
block|}
end_class
begin_class
DECL|class|ValueSourceScorer
class|class
name|ValueSourceScorer
extends|extends
name|Scorer
block|{
DECL|field|reader
specifier|protected
name|IndexReader
name|reader
decl_stmt|;
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|maxDoc
specifier|protected
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|values
specifier|protected
specifier|final
name|DocValues
name|values
decl_stmt|;
DECL|field|checkDeletes
specifier|protected
name|boolean
name|checkDeletes
decl_stmt|;
DECL|method|ValueSourceScorer
specifier|protected
name|ValueSourceScorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|DocValues
name|values
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|setCheckDeletes
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|getReader
specifier|public
name|IndexReader
name|getReader
parameter_list|()
block|{
return|return
name|reader
return|;
block|}
DECL|method|setCheckDeletes
specifier|public
name|void
name|setCheckDeletes
parameter_list|(
name|boolean
name|checkDeletes
parameter_list|)
block|{
name|this
operator|.
name|checkDeletes
operator|=
name|checkDeletes
operator|&&
name|reader
operator|.
name|hasDeletions
argument_list|()
expr_stmt|;
block|}
DECL|method|matches
specifier|public
name|boolean
name|matches
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
operator|!
name|checkDeletes
operator|||
operator|!
name|reader
operator|.
name|isDeleted
argument_list|(
name|maxDoc
argument_list|)
operator|)
operator|&&
name|matchesValue
argument_list|(
name|doc
argument_list|)
return|;
block|}
DECL|method|matchesValue
specifier|public
name|boolean
name|matchesValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|doc
operator|++
expr_stmt|;
if|if
condition|(
name|doc
operator|>=
name|maxDoc
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|matches
argument_list|(
name|doc
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
block|}
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
block|{
name|doc
operator|=
name|target
operator|-
literal|1
expr_stmt|;
return|return
name|next
argument_list|()
return|;
block|}
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|values
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|values
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
end_class
end_unit
