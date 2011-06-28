begin_unit
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
operator|.
name|AtomicReaderContext
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
name|queries
operator|.
name|function
operator|.
name|DocValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|IndexSearcher
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
name|util
operator|.
name|BytesRef
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
name|util
operator|.
name|Arrays
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
begin_class
DECL|class|MultiFunction
specifier|public
specifier|abstract
class|class
name|MultiFunction
extends|extends
name|ValueSource
block|{
DECL|field|sources
specifier|protected
specifier|final
name|List
argument_list|<
name|ValueSource
argument_list|>
name|sources
decl_stmt|;
DECL|method|MultiFunction
specifier|public
name|MultiFunction
parameter_list|(
name|List
argument_list|<
name|ValueSource
argument_list|>
name|sources
parameter_list|)
block|{
name|this
operator|.
name|sources
operator|=
name|sources
expr_stmt|;
block|}
DECL|method|name
specifier|abstract
specifier|protected
name|String
name|name
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|description
argument_list|(
name|name
argument_list|()
argument_list|,
name|sources
argument_list|)
return|;
block|}
DECL|method|description
specifier|public
specifier|static
name|String
name|description
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|ValueSource
argument_list|>
name|sources
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|boolean
name|firstTime
init|=
literal|true
decl_stmt|;
for|for
control|(
name|ValueSource
name|source
range|:
name|sources
control|)
block|{
if|if
condition|(
name|firstTime
condition|)
block|{
name|firstTime
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|valsArr
specifier|public
specifier|static
name|DocValues
index|[]
name|valsArr
parameter_list|(
name|List
argument_list|<
name|ValueSource
argument_list|>
name|sources
parameter_list|,
name|Map
name|fcontext
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocValues
index|[]
name|valsArr
init|=
operator|new
name|DocValues
index|[
name|sources
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ValueSource
name|source
range|:
name|sources
control|)
block|{
name|valsArr
index|[
name|i
operator|++
index|]
operator|=
name|source
operator|.
name|getValues
argument_list|(
name|fcontext
argument_list|,
name|readerContext
argument_list|)
expr_stmt|;
block|}
return|return
name|valsArr
return|;
block|}
DECL|class|Values
specifier|public
class|class
name|Values
extends|extends
name|DocValues
block|{
DECL|field|valsArr
specifier|final
name|DocValues
index|[]
name|valsArr
decl_stmt|;
DECL|method|Values
specifier|public
name|Values
parameter_list|(
name|DocValues
index|[]
name|valsArr
parameter_list|)
block|{
name|this
operator|.
name|valsArr
operator|=
name|valsArr
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|MultiFunction
operator|.
name|toString
argument_list|(
name|name
argument_list|()
argument_list|,
name|valsArr
argument_list|,
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueFiller
specifier|public
name|ValueFiller
name|getValueFiller
parameter_list|()
block|{
comment|// TODO: need ValueSource.type() to determine correct type
return|return
name|super
operator|.
name|getValueFiller
argument_list|()
return|;
block|}
block|}
DECL|method|toString
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|String
name|name
parameter_list|,
name|DocValues
index|[]
name|valsArr
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|boolean
name|firstTime
init|=
literal|true
decl_stmt|;
for|for
control|(
name|DocValues
name|vals
range|:
name|valsArr
control|)
block|{
if|if
condition|(
name|firstTime
condition|)
block|{
name|firstTime
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|vals
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|void
name|createWeight
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|ValueSource
name|source
range|:
name|sources
control|)
name|source
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|sources
operator|.
name|hashCode
argument_list|()
operator|+
name|name
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|MultiFunction
name|other
init|=
operator|(
name|MultiFunction
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|sources
operator|.
name|equals
argument_list|(
name|other
operator|.
name|sources
argument_list|)
return|;
block|}
block|}
end_class
end_unit
