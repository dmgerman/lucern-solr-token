begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|BaseTokenStreamTestCase
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
name|analysis
operator|.
name|CannedTokenStream
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
name|analysis
operator|.
name|LegacyNumericTokenStream
operator|.
name|LegacyNumericTermAttribute
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
name|analysis
operator|.
name|LegacyNumericTokenStream
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
name|analysis
operator|.
name|Token
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
name|analysis
operator|.
name|TokenStream
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|LegacyIntField
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
name|document
operator|.
name|StringField
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
name|store
operator|.
name|Directory
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
name|LegacyNumericUtils
import|;
end_import
begin_comment
comment|/** test tokenstream reuse by DefaultIndexingChain */
end_comment
begin_class
DECL|class|TestFieldReuse
specifier|public
class|class
name|TestFieldReuse
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testStringField
specifier|public
name|void
name|testStringField
parameter_list|()
throws|throws
name|IOException
block|{
name|StringField
name|stringField
init|=
operator|new
name|StringField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
comment|// passing null
name|TokenStream
name|ts
init|=
name|stringField
operator|.
name|tokenStream
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"bar"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|}
argument_list|)
expr_stmt|;
comment|// now reuse previous stream
name|stringField
operator|=
operator|new
name|StringField
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
name|TokenStream
name|ts2
init|=
name|stringField
operator|.
name|tokenStream
argument_list|(
literal|null
argument_list|,
name|ts
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|ts
argument_list|,
name|ts
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"baz"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|}
argument_list|)
expr_stmt|;
comment|// pass a bogus stream and ensure it's still ok
name|stringField
operator|=
operator|new
name|StringField
argument_list|(
literal|"foo"
argument_list|,
literal|"beer"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
name|TokenStream
name|bogus
init|=
operator|new
name|LegacyNumericTokenStream
argument_list|()
decl_stmt|;
name|ts
operator|=
name|stringField
operator|.
name|tokenStream
argument_list|(
literal|null
argument_list|,
name|bogus
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|ts
argument_list|,
name|bogus
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"beer"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testNumericReuse
specifier|public
name|void
name|testNumericReuse
parameter_list|()
throws|throws
name|IOException
block|{
name|LegacyIntField
name|legacyIntField
init|=
operator|new
name|LegacyIntField
argument_list|(
literal|"foo"
argument_list|,
literal|5
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
comment|// passing null
name|TokenStream
name|ts
init|=
name|legacyIntField
operator|.
name|tokenStream
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ts
operator|instanceof
name|LegacyNumericTokenStream
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LegacyNumericUtils
operator|.
name|PRECISION_STEP_DEFAULT_32
argument_list|,
operator|(
operator|(
name|LegacyNumericTokenStream
operator|)
name|ts
operator|)
operator|.
name|getPrecisionStep
argument_list|()
argument_list|)
expr_stmt|;
name|assertNumericContents
argument_list|(
literal|5
argument_list|,
name|ts
argument_list|)
expr_stmt|;
comment|// now reuse previous stream
name|legacyIntField
operator|=
operator|new
name|LegacyIntField
argument_list|(
literal|"foo"
argument_list|,
literal|20
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
name|TokenStream
name|ts2
init|=
name|legacyIntField
operator|.
name|tokenStream
argument_list|(
literal|null
argument_list|,
name|ts
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|ts
argument_list|,
name|ts2
argument_list|)
expr_stmt|;
name|assertNumericContents
argument_list|(
literal|20
argument_list|,
name|ts
argument_list|)
expr_stmt|;
comment|// pass a bogus stream and ensure it's still ok
name|legacyIntField
operator|=
operator|new
name|LegacyIntField
argument_list|(
literal|"foo"
argument_list|,
literal|2343
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
name|TokenStream
name|bogus
init|=
operator|new
name|CannedTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|"bogus"
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
argument_list|)
decl_stmt|;
name|ts
operator|=
name|legacyIntField
operator|.
name|tokenStream
argument_list|(
literal|null
argument_list|,
name|bogus
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|bogus
argument_list|,
name|ts
argument_list|)
expr_stmt|;
name|assertNumericContents
argument_list|(
literal|2343
argument_list|,
name|ts
argument_list|)
expr_stmt|;
comment|// pass another bogus stream (numeric, but different precision step!)
name|legacyIntField
operator|=
operator|new
name|LegacyIntField
argument_list|(
literal|"foo"
argument_list|,
literal|42
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
assert|assert
literal|3
operator|!=
name|LegacyNumericUtils
operator|.
name|PRECISION_STEP_DEFAULT
assert|;
name|bogus
operator|=
operator|new
name|LegacyNumericTokenStream
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|ts
operator|=
name|legacyIntField
operator|.
name|tokenStream
argument_list|(
literal|null
argument_list|,
name|bogus
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|bogus
argument_list|,
name|ts
argument_list|)
expr_stmt|;
name|assertNumericContents
argument_list|(
literal|42
argument_list|,
name|ts
argument_list|)
expr_stmt|;
block|}
DECL|class|MyField
specifier|static
class|class
name|MyField
implements|implements
name|IndexableField
block|{
DECL|field|lastSeen
name|TokenStream
name|lastSeen
decl_stmt|;
DECL|field|lastReturned
name|TokenStream
name|lastReturned
decl_stmt|;
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"foo"
return|;
block|}
annotation|@
name|Override
DECL|method|fieldType
specifier|public
name|IndexableFieldType
name|fieldType
parameter_list|()
block|{
return|return
name|StringField
operator|.
name|TYPE_NOT_STORED
return|;
block|}
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|TokenStream
name|reuse
parameter_list|)
block|{
name|lastSeen
operator|=
name|reuse
expr_stmt|;
return|return
name|lastReturned
operator|=
operator|new
name|CannedTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|"unimportant"
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|boost
specifier|public
name|float
name|boost
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|binaryValue
specifier|public
name|BytesRef
name|binaryValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|numericValue
specifier|public
name|Number
name|numericValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|testIndexWriterActuallyReuses
specifier|public
name|void
name|testIndexWriterActuallyReuses
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
specifier|final
name|MyField
name|field1
init|=
operator|new
name|MyField
argument_list|()
decl_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|field1
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|previous
init|=
name|field1
operator|.
name|lastReturned
decl_stmt|;
name|assertNotNull
argument_list|(
name|previous
argument_list|)
expr_stmt|;
specifier|final
name|MyField
name|field2
init|=
operator|new
name|MyField
argument_list|()
decl_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|field2
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|previous
argument_list|,
name|field2
operator|.
name|lastSeen
argument_list|)
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertNumericContents
specifier|private
name|void
name|assertNumericContents
parameter_list|(
name|int
name|value
parameter_list|,
name|TokenStream
name|ts
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|ts
operator|instanceof
name|LegacyNumericTokenStream
argument_list|)
expr_stmt|;
name|LegacyNumericTermAttribute
name|numericAtt
init|=
name|ts
operator|.
name|getAttribute
argument_list|(
name|LegacyNumericTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
name|boolean
name|seen
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|numericAtt
operator|.
name|getShift
argument_list|()
operator|==
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|value
argument_list|,
name|numericAtt
operator|.
name|getRawValue
argument_list|()
argument_list|)
expr_stmt|;
name|seen
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|seen
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
