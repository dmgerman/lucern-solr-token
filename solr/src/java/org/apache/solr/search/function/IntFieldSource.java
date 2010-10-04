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
name|solr
operator|.
name|search
operator|.
name|MutableValueInt
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
name|MutableValue
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
name|FieldCache
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
name|cache
operator|.
name|FloatValuesCreator
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
name|cache
operator|.
name|IntValuesCreator
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
name|cache
operator|.
name|CachedArray
operator|.
name|DoubleValues
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
name|cache
operator|.
name|CachedArray
operator|.
name|FloatValues
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
name|cache
operator|.
name|CachedArray
operator|.
name|IntValues
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
begin_comment
comment|/**  * Obtains int field values from the {@link org.apache.lucene.search.FieldCache}  * using<code>getInts()</code>  * and makes those values available as other numeric types, casting as needed. *  * @version $Id$  */
end_comment
begin_class
DECL|class|IntFieldSource
specifier|public
class|class
name|IntFieldSource
extends|extends
name|NumericFieldCacheSource
argument_list|<
name|IntValues
argument_list|>
block|{
DECL|method|IntFieldSource
specifier|public
name|IntFieldSource
parameter_list|(
name|IntValuesCreator
name|creator
parameter_list|)
block|{
name|super
argument_list|(
name|creator
argument_list|)
expr_stmt|;
block|}
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"int("
operator|+
name|field
operator|+
literal|')'
return|;
block|}
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IntValues
name|vals
init|=
name|cache
operator|.
name|getInts
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|creator
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
name|arr
init|=
name|vals
operator|.
name|values
decl_stmt|;
return|return
operator|new
name|DocValues
argument_list|()
block|{
specifier|final
name|MutableValueInt
name|val
init|=
operator|new
name|MutableValueInt
argument_list|()
decl_stmt|;
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|arr
index|[
name|doc
index|]
return|;
block|}
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|arr
index|[
name|doc
index|]
return|;
block|}
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|arr
index|[
name|doc
index|]
return|;
block|}
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|arr
index|[
name|doc
index|]
return|;
block|}
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|Float
operator|.
name|toString
argument_list|(
name|arr
index|[
name|doc
index|]
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|description
argument_list|()
operator|+
literal|'='
operator|+
name|intVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ValueSourceScorer
name|getRangeScorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|lowerVal
parameter_list|,
name|String
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
name|int
name|lower
decl_stmt|,
name|upper
decl_stmt|;
comment|// instead of using separate comparison functions, adjust the endpoints.
if|if
condition|(
name|lowerVal
operator|==
literal|null
condition|)
block|{
name|lower
operator|=
name|Integer
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
else|else
block|{
name|lower
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|lowerVal
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|includeLower
operator|&&
name|lower
operator|<
name|Integer
operator|.
name|MAX_VALUE
condition|)
name|lower
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|upperVal
operator|==
literal|null
condition|)
block|{
name|upper
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
name|upper
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|upperVal
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|includeUpper
operator|&&
name|upper
operator|>
name|Integer
operator|.
name|MIN_VALUE
condition|)
name|upper
operator|--
expr_stmt|;
block|}
specifier|final
name|int
name|ll
init|=
name|lower
decl_stmt|;
specifier|final
name|int
name|uu
init|=
name|upper
decl_stmt|;
return|return
operator|new
name|ValueSourceScorer
argument_list|(
name|reader
argument_list|,
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matchesValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|int
name|val
init|=
name|arr
index|[
name|doc
index|]
decl_stmt|;
comment|// only check for deleted if it's the default value
comment|// if (val==0&& reader.isDeleted(doc)) return false;
return|return
name|val
operator|>=
name|ll
operator|&&
name|val
operator|<=
name|uu
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|ValueFiller
name|getValueFiller
parameter_list|()
block|{
return|return
operator|new
name|ValueFiller
argument_list|()
block|{
specifier|private
specifier|final
name|int
index|[]
name|intArr
init|=
name|arr
decl_stmt|;
specifier|private
specifier|final
name|MutableValueInt
name|mval
init|=
operator|new
name|MutableValueInt
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|MutableValue
name|getValue
parameter_list|()
block|{
return|return
name|mval
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|fillValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|mval
operator|.
name|value
operator|=
name|intArr
index|[
name|doc
index|]
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
