begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|index
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
name|index
operator|.
name|NumericDocValues
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
name|SortedNumericDocValues
import|;
end_import
begin_comment
comment|/**   * SortField for {@link SortedNumericDocValues}.  *<p>  * A SortedNumericDocValues contains multiple values for a field, so sorting with  * this technique "selects" a value as the representative sort value for the document.  *<p>  * By default, the minimum value in the list is selected as the sort value, but  * this can be customized.  *<p>  * Like sorting by string, this also supports sorting missing values as first or last,  * via {@link #setMissingValue(Object)}.  * @see SortedNumericSelector  */
end_comment
begin_class
DECL|class|SortedNumericSortField
specifier|public
class|class
name|SortedNumericSortField
extends|extends
name|SortField
block|{
DECL|field|selector
specifier|private
specifier|final
name|SortedNumericSelector
operator|.
name|Type
name|selector
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|SortField
operator|.
name|Type
name|type
decl_stmt|;
comment|/**    * Creates a sort, by the minimum value in the set     * for the document.    * @param field Name of field to sort by.  Must not be null.    * @param type Type of values    */
DECL|method|SortedNumericSortField
specifier|public
name|SortedNumericSortField
parameter_list|(
name|String
name|field
parameter_list|,
name|SortField
operator|.
name|Type
name|type
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|type
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a sort, possibly in reverse, by the minimum value in the set     * for the document.    * @param field Name of field to sort by.  Must not be null.    * @param type Type of values    * @param reverse True if natural order should be reversed.    */
DECL|method|SortedNumericSortField
specifier|public
name|SortedNumericSortField
parameter_list|(
name|String
name|field
parameter_list|,
name|SortField
operator|.
name|Type
name|type
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|type
argument_list|,
name|reverse
argument_list|,
name|SortedNumericSelector
operator|.
name|Type
operator|.
name|MIN
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a sort, possibly in reverse, specifying how the sort value from     * the document's set is selected.    * @param field Name of field to sort by.  Must not be null.    * @param type Type of values    * @param reverse True if natural order should be reversed.    * @param selector custom selector type for choosing the sort value from the set.    */
DECL|method|SortedNumericSortField
specifier|public
name|SortedNumericSortField
parameter_list|(
name|String
name|field
parameter_list|,
name|SortField
operator|.
name|Type
name|type
parameter_list|,
name|boolean
name|reverse
parameter_list|,
name|SortedNumericSelector
operator|.
name|Type
name|selector
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|CUSTOM
argument_list|,
name|reverse
argument_list|)
expr_stmt|;
if|if
condition|(
name|selector
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/** Returns the selector in use for this sort */
DECL|method|getSelector
specifier|public
name|SortedNumericSelector
operator|.
name|Type
name|getSelector
parameter_list|()
block|{
return|return
name|selector
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
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|selector
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|type
operator|.
name|hashCode
argument_list|()
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
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
return|return
literal|false
return|;
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
return|return
literal|false
return|;
name|SortedNumericSortField
name|other
init|=
operator|(
name|SortedNumericSortField
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|selector
operator|!=
name|other
operator|.
name|selector
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|type
operator|!=
name|other
operator|.
name|type
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"<sortednumeric"
operator|+
literal|": \""
argument_list|)
operator|.
name|append
argument_list|(
name|getField
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
if|if
condition|(
name|getReverse
argument_list|()
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|'!'
argument_list|)
expr_stmt|;
if|if
condition|(
name|missingValue
operator|!=
literal|null
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" missingValue="
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|missingValue
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|" selector="
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|selector
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" type="
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|type
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setMissingValue
specifier|public
name|void
name|setMissingValue
parameter_list|(
name|Object
name|missingValue
parameter_list|)
block|{
name|this
operator|.
name|missingValue
operator|=
name|missingValue
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|getComparator
parameter_list|(
name|int
name|numHits
parameter_list|,
name|int
name|sortPos
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|INT
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|IntComparator
argument_list|(
name|numHits
argument_list|,
name|getField
argument_list|()
argument_list|,
operator|(
name|Integer
operator|)
name|missingValue
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|SortedNumericSelector
operator|.
name|wrap
argument_list|(
name|DocValues
operator|.
name|getSortedNumeric
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
argument_list|,
name|selector
argument_list|,
name|type
argument_list|)
return|;
block|}
block|}
return|;
case|case
name|FLOAT
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|FloatComparator
argument_list|(
name|numHits
argument_list|,
name|getField
argument_list|()
argument_list|,
operator|(
name|Float
operator|)
name|missingValue
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|SortedNumericSelector
operator|.
name|wrap
argument_list|(
name|DocValues
operator|.
name|getSortedNumeric
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
argument_list|,
name|selector
argument_list|,
name|type
argument_list|)
return|;
block|}
block|}
return|;
case|case
name|LONG
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|LongComparator
argument_list|(
name|numHits
argument_list|,
name|getField
argument_list|()
argument_list|,
operator|(
name|Long
operator|)
name|missingValue
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|SortedNumericSelector
operator|.
name|wrap
argument_list|(
name|DocValues
operator|.
name|getSortedNumeric
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
argument_list|,
name|selector
argument_list|,
name|type
argument_list|)
return|;
block|}
block|}
return|;
case|case
name|DOUBLE
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|DoubleComparator
argument_list|(
name|numHits
argument_list|,
name|getField
argument_list|()
argument_list|,
operator|(
name|Double
operator|)
name|missingValue
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|SortedNumericSelector
operator|.
name|wrap
argument_list|(
name|DocValues
operator|.
name|getSortedNumeric
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
argument_list|,
name|selector
argument_list|,
name|type
argument_list|)
return|;
block|}
block|}
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
block|}
end_class
end_unit
