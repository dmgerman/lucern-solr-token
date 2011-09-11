begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.queries.function.valuesource
package|package
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
name|valuesource
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
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|FloatDocValues
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
name|search
operator|.
name|similarities
operator|.
name|Similarity
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
name|similarities
operator|.
name|TFIDFSimilarity
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
name|Map
import|;
end_import
begin_class
DECL|class|NormValueSource
specifier|public
class|class
name|NormValueSource
extends|extends
name|ValueSource
block|{
DECL|field|field
specifier|protected
name|String
name|field
decl_stmt|;
DECL|method|NormValueSource
specifier|public
name|NormValueSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"norm"
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|name
argument_list|()
operator|+
literal|'('
operator|+
name|field
operator|+
literal|')'
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
name|context
operator|.
name|put
argument_list|(
literal|"searcher"
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexSearcher
name|searcher
init|=
operator|(
name|IndexSearcher
operator|)
name|context
operator|.
name|get
argument_list|(
literal|"searcher"
argument_list|)
decl_stmt|;
name|Similarity
name|sim
init|=
name|searcher
operator|.
name|getSimilarityProvider
argument_list|()
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|sim
operator|instanceof
name|TFIDFSimilarity
operator|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"requires a TFIDFSimilarity (such as DefaultSimilarity)"
argument_list|)
throw|;
block|}
specifier|final
name|TFIDFSimilarity
name|similarity
init|=
operator|(
name|TFIDFSimilarity
operator|)
name|sim
decl_stmt|;
specifier|final
name|byte
index|[]
name|norms
init|=
name|readerContext
operator|.
name|reader
operator|.
name|norms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|norms
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|ConstDoubleDocValues
argument_list|(
literal|0.0
argument_list|,
name|this
argument_list|)
return|;
block|}
return|return
operator|new
name|FloatDocValues
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|similarity
operator|.
name|decodeNormValue
argument_list|(
name|norms
index|[
name|doc
index|]
argument_list|)
return|;
block|}
block|}
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
return|return
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|NormValueSource
operator|)
name|o
operator|)
operator|.
name|field
argument_list|)
return|;
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
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|+
name|field
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit
