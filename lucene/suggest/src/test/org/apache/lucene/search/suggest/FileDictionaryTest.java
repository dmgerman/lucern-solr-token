begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractMap
operator|.
name|SimpleEntry
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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
name|TestUtil
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
name|TestUtil
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|FileDictionaryTest
specifier|public
class|class
name|FileDictionaryTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|generateFileEntry
specifier|private
name|Map
operator|.
name|Entry
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|String
argument_list|>
name|generateFileEntry
parameter_list|(
name|String
name|fieldDelimiter
parameter_list|,
name|boolean
name|hasWeight
parameter_list|,
name|boolean
name|hasPayload
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|entryValues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|term
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|300
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|entryValues
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasWeight
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|fieldDelimiter
argument_list|)
expr_stmt|;
name|long
name|weight
init|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|entryValues
operator|.
name|add
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|weight
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hasPayload
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|fieldDelimiter
argument_list|)
expr_stmt|;
name|String
name|payload
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|300
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|entryValues
operator|.
name|add
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
return|return
operator|new
name|SimpleEntry
argument_list|<>
argument_list|(
name|entryValues
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|generateFileInput
specifier|private
name|Map
operator|.
name|Entry
argument_list|<
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|,
name|String
argument_list|>
name|generateFileInput
parameter_list|(
name|int
name|count
parameter_list|,
name|String
name|fieldDelimiter
parameter_list|,
name|boolean
name|hasWeights
parameter_list|,
name|boolean
name|hasPayloads
parameter_list|)
block|{
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|hasPayload
init|=
name|hasPayloads
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
name|count
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|hasPayloads
condition|)
block|{
name|hasPayload
operator|=
operator|(
name|i
operator|==
literal|0
operator|)
condition|?
literal|true
else|:
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
block|}
name|Map
operator|.
name|Entry
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|String
argument_list|>
name|entrySet
init|=
name|generateFileEntry
argument_list|(
name|fieldDelimiter
argument_list|,
operator|(
operator|!
name|hasPayloads
operator|&&
name|hasWeights
operator|)
condition|?
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
else|:
name|hasWeights
argument_list|,
name|hasPayload
argument_list|)
decl_stmt|;
name|entries
operator|.
name|add
argument_list|(
name|entrySet
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|entrySet
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SimpleEntry
argument_list|<>
argument_list|(
name|entries
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testFileWithTerm
specifier|public
name|void
name|testFileWithTerm
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|,
name|String
argument_list|>
name|fileInput
init|=
name|generateFileInput
argument_list|(
name|atLeast
argument_list|(
literal|100
argument_list|)
argument_list|,
name|FileDictionary
operator|.
name|DEFAULT_FIELD_DELIMITER
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|InputStream
name|inputReader
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|fileInput
operator|.
name|getValue
argument_list|()
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|FileDictionary
name|dictionary
init|=
operator|new
name|FileDictionary
argument_list|(
name|inputReader
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|entries
init|=
name|fileInput
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|InputIterator
name|inputIter
init|=
name|dictionary
operator|.
name|getEntryIterator
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|inputIter
operator|.
name|hasPayloads
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|term
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|inputIter
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|entries
operator|.
name|size
argument_list|()
operator|>
name|count
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|entry
init|=
name|entries
operator|.
name|get
argument_list|(
name|count
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|size
argument_list|()
operator|>=
literal|1
argument_list|)
expr_stmt|;
comment|// at least a term
name|assertEquals
argument_list|(
name|entry
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|inputIter
operator|.
name|weight
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|inputIter
operator|.
name|payload
argument_list|()
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|count
argument_list|,
name|entries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileWithWeight
specifier|public
name|void
name|testFileWithWeight
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|,
name|String
argument_list|>
name|fileInput
init|=
name|generateFileInput
argument_list|(
name|atLeast
argument_list|(
literal|100
argument_list|)
argument_list|,
name|FileDictionary
operator|.
name|DEFAULT_FIELD_DELIMITER
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|InputStream
name|inputReader
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|fileInput
operator|.
name|getValue
argument_list|()
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|FileDictionary
name|dictionary
init|=
operator|new
name|FileDictionary
argument_list|(
name|inputReader
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|entries
init|=
name|fileInput
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|InputIterator
name|inputIter
init|=
name|dictionary
operator|.
name|getEntryIterator
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|inputIter
operator|.
name|hasPayloads
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|term
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|inputIter
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|entries
operator|.
name|size
argument_list|()
operator|>
name|count
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|entry
init|=
name|entries
operator|.
name|get
argument_list|(
name|count
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|size
argument_list|()
operator|>=
literal|1
argument_list|)
expr_stmt|;
comment|// at least a term
name|assertEquals
argument_list|(
name|entry
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|entry
operator|.
name|size
argument_list|()
operator|==
literal|2
operator|)
condition|?
name|Long
operator|.
name|parseLong
argument_list|(
name|entry
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
else|:
literal|1
argument_list|,
name|inputIter
operator|.
name|weight
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|inputIter
operator|.
name|payload
argument_list|()
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|count
argument_list|,
name|entries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileWithWeightAndPayload
specifier|public
name|void
name|testFileWithWeightAndPayload
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|,
name|String
argument_list|>
name|fileInput
init|=
name|generateFileInput
argument_list|(
name|atLeast
argument_list|(
literal|100
argument_list|)
argument_list|,
name|FileDictionary
operator|.
name|DEFAULT_FIELD_DELIMITER
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|InputStream
name|inputReader
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|fileInput
operator|.
name|getValue
argument_list|()
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|FileDictionary
name|dictionary
init|=
operator|new
name|FileDictionary
argument_list|(
name|inputReader
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|entries
init|=
name|fileInput
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|InputIterator
name|inputIter
init|=
name|dictionary
operator|.
name|getEntryIterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|inputIter
operator|.
name|hasPayloads
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|term
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|inputIter
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|entries
operator|.
name|size
argument_list|()
operator|>
name|count
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|entry
init|=
name|entries
operator|.
name|get
argument_list|(
name|count
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|size
argument_list|()
operator|>=
literal|2
argument_list|)
expr_stmt|;
comment|// at least term and weight
name|assertEquals
argument_list|(
name|entry
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|entry
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|inputIter
operator|.
name|weight
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|size
argument_list|()
operator|==
literal|3
condition|)
block|{
name|assertEquals
argument_list|(
name|entry
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|inputIter
operator|.
name|payload
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|inputIter
operator|.
name|payload
argument_list|()
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|count
argument_list|,
name|entries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileWithOneEntry
specifier|public
name|void
name|testFileWithOneEntry
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|,
name|String
argument_list|>
name|fileInput
init|=
name|generateFileInput
argument_list|(
literal|1
argument_list|,
name|FileDictionary
operator|.
name|DEFAULT_FIELD_DELIMITER
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|InputStream
name|inputReader
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|fileInput
operator|.
name|getValue
argument_list|()
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|FileDictionary
name|dictionary
init|=
operator|new
name|FileDictionary
argument_list|(
name|inputReader
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|entries
init|=
name|fileInput
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|InputIterator
name|inputIter
init|=
name|dictionary
operator|.
name|getEntryIterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|inputIter
operator|.
name|hasPayloads
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|term
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|inputIter
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|entries
operator|.
name|size
argument_list|()
operator|>
name|count
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|entry
init|=
name|entries
operator|.
name|get
argument_list|(
name|count
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|size
argument_list|()
operator|>=
literal|2
argument_list|)
expr_stmt|;
comment|// at least term and weight
name|assertEquals
argument_list|(
name|entry
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|entry
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|inputIter
operator|.
name|weight
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|size
argument_list|()
operator|==
literal|3
condition|)
block|{
name|assertEquals
argument_list|(
name|entry
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|inputIter
operator|.
name|payload
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|inputIter
operator|.
name|payload
argument_list|()
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|count
argument_list|,
name|entries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileWithDifferentDelimiter
specifier|public
name|void
name|testFileWithDifferentDelimiter
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|,
name|String
argument_list|>
name|fileInput
init|=
name|generateFileInput
argument_list|(
name|atLeast
argument_list|(
literal|100
argument_list|)
argument_list|,
literal|" , "
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|InputStream
name|inputReader
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|fileInput
operator|.
name|getValue
argument_list|()
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|FileDictionary
name|dictionary
init|=
operator|new
name|FileDictionary
argument_list|(
name|inputReader
argument_list|,
literal|" , "
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|entries
init|=
name|fileInput
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|InputIterator
name|inputIter
init|=
name|dictionary
operator|.
name|getEntryIterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|inputIter
operator|.
name|hasPayloads
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|term
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|inputIter
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|entries
operator|.
name|size
argument_list|()
operator|>
name|count
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|entry
init|=
name|entries
operator|.
name|get
argument_list|(
name|count
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|size
argument_list|()
operator|>=
literal|2
argument_list|)
expr_stmt|;
comment|// at least term and weight
name|assertEquals
argument_list|(
name|entry
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|entry
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|inputIter
operator|.
name|weight
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|size
argument_list|()
operator|==
literal|3
condition|)
block|{
name|assertEquals
argument_list|(
name|entry
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|inputIter
operator|.
name|payload
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|inputIter
operator|.
name|payload
argument_list|()
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|count
argument_list|,
name|entries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
