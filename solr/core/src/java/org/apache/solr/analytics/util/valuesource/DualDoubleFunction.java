begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analytics.util.valuesource
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|util
operator|.
name|valuesource
package|;
end_package
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
name|FunctionValues
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
name|DoubleDocValues
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
begin_comment
comment|/**  * Abstract {@link ValueSource} implementation which wraps two ValueSources  * and applies an extendible double function to their values.  **/
end_comment
begin_class
DECL|class|DualDoubleFunction
specifier|public
specifier|abstract
class|class
name|DualDoubleFunction
extends|extends
name|ValueSource
block|{
DECL|field|a
specifier|protected
specifier|final
name|ValueSource
name|a
decl_stmt|;
DECL|field|b
specifier|protected
specifier|final
name|ValueSource
name|b
decl_stmt|;
DECL|method|DualDoubleFunction
specifier|public
name|DualDoubleFunction
parameter_list|(
name|ValueSource
name|a
parameter_list|,
name|ValueSource
name|b
parameter_list|)
block|{
name|this
operator|.
name|a
operator|=
name|a
expr_stmt|;
name|this
operator|.
name|b
operator|=
name|b
expr_stmt|;
block|}
DECL|method|name
specifier|protected
specifier|abstract
name|String
name|name
parameter_list|()
function_decl|;
DECL|method|func
specifier|protected
specifier|abstract
name|double
name|func
parameter_list|(
name|int
name|doc
parameter_list|,
name|FunctionValues
name|aVals
parameter_list|,
name|FunctionValues
name|bVals
parameter_list|)
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
name|name
argument_list|()
operator|+
literal|"("
operator|+
name|a
operator|.
name|description
argument_list|()
operator|+
literal|","
operator|+
name|b
operator|.
name|description
argument_list|()
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|FunctionValues
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
specifier|final
name|FunctionValues
name|aVals
init|=
name|a
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
specifier|final
name|FunctionValues
name|bVals
init|=
name|b
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
return|return
operator|new
name|DoubleDocValues
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|func
argument_list|(
name|doc
argument_list|,
name|aVals
argument_list|,
name|bVals
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|aVals
operator|.
name|exists
argument_list|(
name|doc
argument_list|)
operator|&
name|bVals
operator|.
name|exists
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|name
argument_list|()
operator|+
literal|'('
operator|+
name|aVals
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
operator|+
literal|','
operator|+
name|bVals
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
operator|+
literal|')'
return|;
block|}
block|}
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
name|a
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|b
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
name|DualDoubleFunction
name|other
init|=
operator|(
name|DualDoubleFunction
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|a
operator|.
name|equals
argument_list|(
name|other
operator|.
name|a
argument_list|)
operator|&&
name|this
operator|.
name|b
operator|.
name|equals
argument_list|(
name|other
operator|.
name|b
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
name|int
name|h
init|=
name|a
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|^=
operator|(
name|h
operator|<<
literal|13
operator|)
operator||
operator|(
name|h
operator|>>>
literal|20
operator|)
expr_stmt|;
name|h
operator|+=
name|b
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|^=
operator|(
name|h
operator|<<
literal|23
operator|)
operator||
operator|(
name|h
operator|>>>
literal|10
operator|)
expr_stmt|;
name|h
operator|+=
name|name
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class
end_unit
