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
comment|/**  *  *  **/
end_comment
begin_class
DECL|class|ExtendedFieldCacheImpl
class|class
name|ExtendedFieldCacheImpl
extends|extends
name|FieldCacheImpl
implements|implements
name|ExtendedFieldCache
block|{
DECL|field|LONG_PARSER
specifier|private
specifier|static
specifier|final
name|LongParser
name|LONG_PARSER
init|=
operator|new
name|LongParser
argument_list|()
block|{
specifier|public
name|long
name|parseLong
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|DOUBLE_PARSER
specifier|private
specifier|static
specifier|final
name|DoubleParser
name|DOUBLE_PARSER
init|=
operator|new
name|DoubleParser
argument_list|()
block|{
specifier|public
name|double
name|parseDouble
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|getLongs
specifier|public
name|long
index|[]
name|getLongs
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getLongs
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|LONG_PARSER
argument_list|)
return|;
block|}
comment|// inherit javadocs
DECL|method|getLongs
specifier|public
name|long
index|[]
name|getLongs
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|LongParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|long
index|[]
operator|)
name|longsCache
operator|.
name|get
argument_list|(
name|reader
argument_list|,
operator|new
name|Entry
argument_list|(
name|field
argument_list|,
name|parser
argument_list|)
argument_list|)
return|;
block|}
DECL|field|longsCache
name|Cache
name|longsCache
init|=
operator|new
name|Cache
argument_list|()
block|{
specifier|protected
name|Object
name|createValue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Object
name|entryKey
parameter_list|)
throws|throws
name|IOException
block|{
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|entryKey
decl_stmt|;
name|String
name|field
init|=
name|entry
operator|.
name|field
decl_stmt|;
name|LongParser
name|parser
init|=
operator|(
name|LongParser
operator|)
name|entry
operator|.
name|custom
decl_stmt|;
specifier|final
name|long
index|[]
name|retArray
init|=
operator|new
name|long
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
decl_stmt|;
name|TermDocs
name|termDocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
name|TermEnum
name|termEnum
init|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
do|do
block|{
name|Term
name|term
init|=
name|termEnum
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
operator|||
name|term
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
break|break;
name|long
name|termval
init|=
name|parser
operator|.
name|parseLong
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
name|termEnum
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
name|termEnum
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
catch|catch
parameter_list|(
name|StopFillCacheException
name|stop
parameter_list|)
block|{       }
finally|finally
block|{
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|retArray
return|;
block|}
block|}
decl_stmt|;
comment|// inherit javadocs
DECL|method|getDoubles
specifier|public
name|double
index|[]
name|getDoubles
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getDoubles
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|DOUBLE_PARSER
argument_list|)
return|;
block|}
comment|// inherit javadocs
DECL|method|getDoubles
specifier|public
name|double
index|[]
name|getDoubles
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|DoubleParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|double
index|[]
operator|)
name|doublesCache
operator|.
name|get
argument_list|(
name|reader
argument_list|,
operator|new
name|Entry
argument_list|(
name|field
argument_list|,
name|parser
argument_list|)
argument_list|)
return|;
block|}
DECL|field|doublesCache
name|Cache
name|doublesCache
init|=
operator|new
name|Cache
argument_list|()
block|{
specifier|protected
name|Object
name|createValue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Object
name|entryKey
parameter_list|)
throws|throws
name|IOException
block|{
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|entryKey
decl_stmt|;
name|String
name|field
init|=
name|entry
operator|.
name|field
decl_stmt|;
name|DoubleParser
name|parser
init|=
operator|(
name|DoubleParser
operator|)
name|entry
operator|.
name|custom
decl_stmt|;
specifier|final
name|double
index|[]
name|retArray
init|=
operator|new
name|double
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
decl_stmt|;
name|TermDocs
name|termDocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
name|TermEnum
name|termEnum
init|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
do|do
block|{
name|Term
name|term
init|=
name|termEnum
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
operator|||
name|term
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
break|break;
name|double
name|termval
init|=
name|parser
operator|.
name|parseDouble
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
name|termEnum
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
name|termEnum
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
catch|catch
parameter_list|(
name|StopFillCacheException
name|stop
parameter_list|)
block|{       }
finally|finally
block|{
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|retArray
return|;
block|}
block|}
decl_stmt|;
comment|// inherit javadocs
DECL|method|getAuto
specifier|public
name|Object
name|getAuto
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|autoCache
operator|.
name|get
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
return|;
block|}
DECL|field|autoCache
name|Cache
name|autoCache
init|=
operator|new
name|Cache
argument_list|()
block|{
specifier|protected
name|Object
name|createValue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Object
name|fieldKey
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|field
init|=
operator|(
operator|(
name|String
operator|)
name|fieldKey
operator|)
operator|.
name|intern
argument_list|()
decl_stmt|;
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
name|field
argument_list|)
argument_list|)
decl_stmt|;
try|try
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
operator|+
literal|" - cannot determine sort type"
argument_list|)
throw|;
block|}
name|Object
name|ret
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|term
operator|.
name|field
argument_list|()
operator|==
name|field
condition|)
block|{
name|String
name|termtext
init|=
name|term
operator|.
name|text
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
comment|/**            * Java 1.4 level code:             if (pIntegers.matcher(termtext).matches())            return IntegerSortedHitQueue.comparator (reader, enumerator, field);             else if (pFloats.matcher(termtext).matches())            return FloatSortedHitQueue.comparator (reader, enumerator, field);            */
comment|// Java 1.3 level code:
try|try
block|{
name|Integer
operator|.
name|parseInt
argument_list|(
name|termtext
argument_list|)
expr_stmt|;
name|ret
operator|=
name|getInts
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe1
parameter_list|)
block|{
try|try
block|{
name|Long
operator|.
name|parseLong
argument_list|(
name|termtext
argument_list|)
expr_stmt|;
name|ret
operator|=
name|getLongs
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe2
parameter_list|)
block|{
try|try
block|{
name|Float
operator|.
name|parseFloat
argument_list|(
name|termtext
argument_list|)
expr_stmt|;
name|ret
operator|=
name|getFloats
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe3
parameter_list|)
block|{
name|ret
operator|=
name|getStringIndex
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"field \""
operator|+
name|field
operator|+
literal|"\" does not appear to be indexed"
argument_list|)
throw|;
block|}
return|return
name|ret
return|;
block|}
finally|finally
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
block|}
end_class
end_unit
