begin_unit
begin_package
DECL|package|org.apache.lucene.expressions
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|HashMap
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
name|LeafReaderContext
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
name|SortField
import|;
end_import
begin_comment
comment|/**  * A {@link ValueSource} which evaluates a {@link Expression} given the context of an {@link Bindings}.  */
end_comment
begin_class
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
DECL|class|ExpressionValueSource
specifier|final
class|class
name|ExpressionValueSource
extends|extends
name|ValueSource
block|{
DECL|field|variables
specifier|final
name|ValueSource
name|variables
index|[]
decl_stmt|;
DECL|field|expression
specifier|final
name|Expression
name|expression
decl_stmt|;
DECL|field|needsScores
specifier|final
name|boolean
name|needsScores
decl_stmt|;
DECL|method|ExpressionValueSource
name|ExpressionValueSource
parameter_list|(
name|Bindings
name|bindings
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
if|if
condition|(
name|bindings
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
if|if
condition|(
name|expression
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
name|this
operator|.
name|expression
operator|=
name|expression
expr_stmt|;
name|variables
operator|=
operator|new
name|ValueSource
index|[
name|expression
operator|.
name|variables
operator|.
name|length
index|]
expr_stmt|;
name|boolean
name|needsScores
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|variables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ValueSource
name|source
init|=
name|bindings
operator|.
name|getValueSource
argument_list|(
name|expression
operator|.
name|variables
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|instanceof
name|ScoreValueSource
condition|)
block|{
name|needsScores
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|source
operator|instanceof
name|ExpressionValueSource
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|ExpressionValueSource
operator|)
name|source
operator|)
operator|.
name|needsScores
argument_list|()
condition|)
block|{
name|needsScores
operator|=
literal|true
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Internal error. Variable ("
operator|+
name|expression
operator|.
name|variables
index|[
name|i
index|]
operator|+
literal|") does not exist."
argument_list|)
throw|;
block|}
name|variables
index|[
name|i
index|]
operator|=
name|source
expr_stmt|;
block|}
name|this
operator|.
name|needsScores
operator|=
name|needsScores
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
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|FunctionValues
argument_list|>
name|valuesCache
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|FunctionValues
argument_list|>
operator|)
name|context
operator|.
name|get
argument_list|(
literal|"valuesCache"
argument_list|)
decl_stmt|;
if|if
condition|(
name|valuesCache
operator|==
literal|null
condition|)
block|{
name|valuesCache
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|context
operator|=
operator|new
name|HashMap
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"valuesCache"
argument_list|,
name|valuesCache
argument_list|)
expr_stmt|;
block|}
name|FunctionValues
index|[]
name|externalValues
init|=
operator|new
name|FunctionValues
index|[
name|expression
operator|.
name|variables
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|variables
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|String
name|externalName
init|=
name|expression
operator|.
name|variables
index|[
name|i
index|]
decl_stmt|;
name|FunctionValues
name|values
init|=
name|valuesCache
operator|.
name|get
argument_list|(
name|externalName
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|values
operator|=
name|variables
index|[
name|i
index|]
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Internal error. External ("
operator|+
name|externalName
operator|+
literal|") does not exist."
argument_list|)
throw|;
block|}
name|valuesCache
operator|.
name|put
argument_list|(
name|externalName
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
name|externalValues
index|[
name|i
index|]
operator|=
name|values
expr_stmt|;
block|}
return|return
operator|new
name|ExpressionFunctionValues
argument_list|(
name|this
argument_list|,
name|expression
argument_list|,
name|externalValues
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|boolean
name|reverse
parameter_list|)
block|{
return|return
operator|new
name|ExpressionSortField
argument_list|(
name|expression
operator|.
name|sourceText
argument_list|,
name|this
argument_list|,
name|reverse
argument_list|)
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
literal|"expr("
operator|+
name|expression
operator|.
name|sourceText
operator|+
literal|")"
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
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|expression
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|expression
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|needsScores
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|variables
argument_list|)
expr_stmt|;
return|return
name|result
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
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ExpressionValueSource
name|other
init|=
operator|(
name|ExpressionValueSource
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|expression
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|expression
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|expression
operator|.
name|equals
argument_list|(
name|other
operator|.
name|expression
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|needsScores
operator|!=
name|other
operator|.
name|needsScores
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|variables
argument_list|,
name|other
operator|.
name|variables
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|needsScores
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
name|needsScores
return|;
block|}
block|}
end_class
end_unit
