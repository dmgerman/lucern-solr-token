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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Term
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
name|TermDocs
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
name|TermEnum
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
begin_comment
comment|/**  * Expert: A sorted hit queue for fields that contain strictly floating point values.  * Hits are sorted into the queue by the values in the field and then by document number.  *  *<p>Created: Feb 2, 2004 9:23:03 AM  *  * @author  Tim Jones (Nacimiento Software)  * @since   lucene 1.4  * @version $Id$  */
end_comment
begin_class
DECL|class|FloatSortedHitQueue
class|class
name|FloatSortedHitQueue
extends|extends
name|FieldSortedHitQueue
block|{
comment|/** 	 * Creates a hit queue sorted over the given field containing float values. 	 * @param reader Index to use. 	 * @param float_field Field containing float sort information 	 * @param size Number of hits to collect. 	 * @throws IOException If an error occurs reading the index. 	 */
DECL|method|FloatSortedHitQueue
name|FloatSortedHitQueue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|float_field
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|reader
argument_list|,
name|float_field
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Returns a comparator for sorting hits according to a field containing floats. 	 * Just calls<code>comparator(IndexReader,String)</code>. 	 * @param reader  Index to use. 	 * @param field  Field containg float values. 	 * @return  Comparator for sorting hits. 	 * @throws IOException If an error occurs reading the index. 	 */
DECL|method|createComparator
specifier|protected
name|ScoreDocLookupComparator
name|createComparator
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|comparator
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
return|;
block|}
comment|/** 	 * Returns a comparator for sorting hits according to a field containing floats. 	 * @param reader  Index to use. 	 * @param fieldname  Field containg float values. 	 * @return  Comparator for sorting hits. 	 * @throws IOException If an error occurs reading the index. 	 */
DECL|method|comparator
specifier|static
name|ScoreDocLookupComparator
name|comparator
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|)
throws|throws
name|IOException
block|{
name|TermEnum
name|enumerator
init|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldname
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|comparator
argument_list|(
name|reader
argument_list|,
name|enumerator
argument_list|,
name|fieldname
argument_list|)
return|;
block|}
comment|/** 	 * Returns a comparator for sorting hits according to a field containing floats using the given enumerator 	 * to collect term values. 	 * @param reader  Index to use. 	 * @param fieldname  Field containg float values. 	 * @return  Comparator for sorting hits. 	 * @throws IOException If an error occurs reading the index. 	 */
DECL|method|comparator
specifier|static
name|ScoreDocLookupComparator
name|comparator
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|TermEnum
name|enumerator
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|field
init|=
name|fieldname
operator|.
name|intern
argument_list|()
decl_stmt|;
return|return
operator|new
name|ScoreDocLookupComparator
argument_list|()
block|{
specifier|protected
specifier|final
name|float
index|[]
name|fieldOrder
init|=
name|generateSortIndex
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|float
index|[]
name|generateSortIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|float
index|[]
name|retArray
init|=
operator|new
name|float
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
decl_stmt|;
if|if
condition|(
name|retArray
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|TermDocs
name|termDocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|enumerator
operator|.
name|term
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no terms in field "
operator|+
name|field
argument_list|)
throw|;
block|}
do|do
block|{
name|Term
name|term
init|=
name|enumerator
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
break|break;
name|float
name|termval
init|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
name|termDocs
operator|.
name|seek
argument_list|(
name|enumerator
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
block|{
name|retArray
index|[
name|termDocs
operator|.
name|doc
argument_list|()
index|]
operator|=
name|termval
expr_stmt|;
block|}
block|}
do|while
condition|(
name|enumerator
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|retArray
return|;
block|}
specifier|public
specifier|final
name|int
name|compare
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|,
specifier|final
name|ScoreDoc
name|j
parameter_list|)
block|{
specifier|final
name|float
name|fi
init|=
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
decl_stmt|;
specifier|final
name|float
name|fj
init|=
name|fieldOrder
index|[
name|j
operator|.
name|doc
index|]
decl_stmt|;
if|if
condition|(
name|fi
operator|<
name|fj
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|fi
operator|>
name|fj
condition|)
return|return
literal|1
return|;
return|return
literal|0
return|;
block|}
specifier|public
specifier|final
name|int
name|compareReverse
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|,
specifier|final
name|ScoreDoc
name|j
parameter_list|)
block|{
specifier|final
name|float
name|fi
init|=
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
decl_stmt|;
specifier|final
name|float
name|fj
init|=
name|fieldOrder
index|[
name|j
operator|.
name|doc
index|]
decl_stmt|;
if|if
condition|(
name|fi
operator|>
name|fj
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|fi
operator|<
name|fj
condition|)
return|return
literal|1
return|;
return|return
literal|0
return|;
block|}
specifier|public
specifier|final
name|boolean
name|sizeMatches
parameter_list|(
specifier|final
name|int
name|n
parameter_list|)
block|{
return|return
name|fieldOrder
operator|.
name|length
operator|==
name|n
return|;
block|}
specifier|public
name|Object
name|sortValue
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|)
block|{
return|return
operator|new
name|Float
argument_list|(
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
argument_list|)
return|;
block|}
specifier|public
name|int
name|sortType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|FLOAT
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
