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
name|AtomicReader
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
name|search
operator|.
name|Explanation
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
DECL|class|IfFunction
specifier|public
class|class
name|IfFunction
extends|extends
name|BoolFunction
block|{
DECL|field|ifSource
specifier|private
name|ValueSource
name|ifSource
decl_stmt|;
DECL|field|trueSource
specifier|private
name|ValueSource
name|trueSource
decl_stmt|;
DECL|field|falseSource
specifier|private
name|ValueSource
name|falseSource
decl_stmt|;
DECL|method|IfFunction
specifier|public
name|IfFunction
parameter_list|(
name|ValueSource
name|ifSource
parameter_list|,
name|ValueSource
name|trueSource
parameter_list|,
name|ValueSource
name|falseSource
parameter_list|)
block|{
name|this
operator|.
name|ifSource
operator|=
name|ifSource
expr_stmt|;
name|this
operator|.
name|trueSource
operator|=
name|trueSource
expr_stmt|;
name|this
operator|.
name|falseSource
operator|=
name|falseSource
expr_stmt|;
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
name|ifVals
init|=
name|ifSource
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
name|trueVals
init|=
name|trueSource
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
name|falseVals
init|=
name|falseSource
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
name|FunctionValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|byte
name|byteVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|ifVals
operator|.
name|boolVal
argument_list|(
name|doc
argument_list|)
condition|?
name|trueVals
operator|.
name|byteVal
argument_list|(
name|doc
argument_list|)
else|:
name|falseVals
operator|.
name|byteVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|short
name|shortVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|ifVals
operator|.
name|boolVal
argument_list|(
name|doc
argument_list|)
condition|?
name|trueVals
operator|.
name|shortVal
argument_list|(
name|doc
argument_list|)
else|:
name|falseVals
operator|.
name|shortVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
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
name|ifVals
operator|.
name|boolVal
argument_list|(
name|doc
argument_list|)
condition|?
name|trueVals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
else|:
name|falseVals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|ifVals
operator|.
name|boolVal
argument_list|(
name|doc
argument_list|)
condition|?
name|trueVals
operator|.
name|intVal
argument_list|(
name|doc
argument_list|)
else|:
name|falseVals
operator|.
name|intVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|ifVals
operator|.
name|boolVal
argument_list|(
name|doc
argument_list|)
condition|?
name|trueVals
operator|.
name|longVal
argument_list|(
name|doc
argument_list|)
else|:
name|falseVals
operator|.
name|longVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
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
name|ifVals
operator|.
name|boolVal
argument_list|(
name|doc
argument_list|)
condition|?
name|trueVals
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
else|:
name|falseVals
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|ifVals
operator|.
name|boolVal
argument_list|(
name|doc
argument_list|)
condition|?
name|trueVals
operator|.
name|strVal
argument_list|(
name|doc
argument_list|)
else|:
name|falseVals
operator|.
name|strVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|boolVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|ifVals
operator|.
name|boolVal
argument_list|(
name|doc
argument_list|)
condition|?
name|trueVals
operator|.
name|boolVal
argument_list|(
name|doc
argument_list|)
else|:
name|falseVals
operator|.
name|boolVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|bytesVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|BytesRef
name|target
parameter_list|)
block|{
return|return
name|ifVals
operator|.
name|boolVal
argument_list|(
name|doc
argument_list|)
condition|?
name|trueVals
operator|.
name|bytesVal
argument_list|(
name|doc
argument_list|,
name|target
argument_list|)
else|:
name|falseVals
operator|.
name|bytesVal
argument_list|(
name|doc
argument_list|,
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|objectVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|ifVals
operator|.
name|boolVal
argument_list|(
name|doc
argument_list|)
condition|?
name|trueVals
operator|.
name|objectVal
argument_list|(
name|doc
argument_list|)
else|:
name|falseVals
operator|.
name|objectVal
argument_list|(
name|doc
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
literal|true
return|;
comment|// TODO: flow through to any sub-sources?
block|}
annotation|@
name|Override
specifier|public
name|ValueFiller
name|getValueFiller
parameter_list|()
block|{
comment|// TODO: we need types of trueSource / falseSource to handle this
comment|// for now, use float.
return|return
name|super
operator|.
name|getValueFiller
argument_list|()
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
literal|"if("
operator|+
name|ifVals
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
operator|+
literal|','
operator|+
name|trueVals
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
operator|+
literal|','
operator|+
name|falseVals
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
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"if("
operator|+
name|ifSource
operator|.
name|description
argument_list|()
operator|+
literal|','
operator|+
name|trueSource
operator|.
name|description
argument_list|()
operator|+
literal|','
operator|+
name|falseSource
operator|+
literal|')'
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
name|ifSource
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|=
name|h
operator|*
literal|31
operator|+
name|trueSource
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|=
name|h
operator|*
literal|31
operator|+
name|falseSource
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
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
operator|!
operator|(
name|o
operator|instanceof
name|IfFunction
operator|)
condition|)
return|return
literal|false
return|;
name|IfFunction
name|other
init|=
operator|(
name|IfFunction
operator|)
name|o
decl_stmt|;
return|return
name|ifSource
operator|.
name|equals
argument_list|(
name|other
operator|.
name|ifSource
argument_list|)
operator|&&
name|trueSource
operator|.
name|equals
argument_list|(
name|other
operator|.
name|trueSource
argument_list|)
operator|&&
name|falseSource
operator|.
name|equals
argument_list|(
name|other
operator|.
name|falseSource
argument_list|)
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
name|ifSource
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|trueSource
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|falseSource
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
