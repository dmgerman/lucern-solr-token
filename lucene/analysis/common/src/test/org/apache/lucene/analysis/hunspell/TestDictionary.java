begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.hunspell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell
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
name|ByteArrayInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilterInputStream
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
name|text
operator|.
name|ParseException
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
name|CharsRef
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
name|IOUtils
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
name|IntsRef
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
name|fst
operator|.
name|Builder
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
name|fst
operator|.
name|CharSequenceOutputs
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
name|fst
operator|.
name|FST
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
name|fst
operator|.
name|Outputs
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
name|fst
operator|.
name|Util
import|;
end_import
begin_class
DECL|class|TestDictionary
specifier|public
class|class
name|TestDictionary
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSimpleDictionary
specifier|public
name|void
name|testSimpleDictionary
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|affixStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"simple.aff"
argument_list|)
decl_stmt|;
name|InputStream
name|dictStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"simple.dic"
argument_list|)
decl_stmt|;
name|Dictionary
name|dictionary
init|=
operator|new
name|Dictionary
argument_list|(
name|affixStream
argument_list|,
name|dictStream
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dictionary
operator|.
name|lookupSuffix
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'e'
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dictionary
operator|.
name|lookupPrefix
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'s'
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|IntsRef
name|ordList
init|=
name|dictionary
operator|.
name|lookupWord
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'o'
block|,
literal|'l'
block|,
literal|'r'
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ordList
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ordList
operator|.
name|length
argument_list|)
expr_stmt|;
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|dictionary
operator|.
name|flagLookup
operator|.
name|get
argument_list|(
name|ordList
operator|.
name|ints
index|[
literal|0
index|]
argument_list|,
name|ref
argument_list|)
expr_stmt|;
name|char
name|flags
index|[]
init|=
name|Dictionary
operator|.
name|decodeFlags
argument_list|(
name|ref
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|flags
operator|.
name|length
argument_list|)
expr_stmt|;
name|ordList
operator|=
name|dictionary
operator|.
name|lookupWord
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'l'
block|,
literal|'u'
block|,
literal|'c'
block|,
literal|'e'
block|,
literal|'n'
block|}
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|ordList
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ordList
operator|.
name|length
argument_list|)
expr_stmt|;
name|dictionary
operator|.
name|flagLookup
operator|.
name|get
argument_list|(
name|ordList
operator|.
name|ints
index|[
literal|0
index|]
argument_list|,
name|ref
argument_list|)
expr_stmt|;
name|flags
operator|=
name|Dictionary
operator|.
name|decodeFlags
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|flags
operator|.
name|length
argument_list|)
expr_stmt|;
name|affixStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|dictStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCompressedDictionary
specifier|public
name|void
name|testCompressedDictionary
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|affixStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"compressed.aff"
argument_list|)
decl_stmt|;
name|InputStream
name|dictStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"compressed.dic"
argument_list|)
decl_stmt|;
name|Dictionary
name|dictionary
init|=
operator|new
name|Dictionary
argument_list|(
name|affixStream
argument_list|,
name|dictStream
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dictionary
operator|.
name|lookupSuffix
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'e'
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dictionary
operator|.
name|lookupPrefix
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'s'
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|IntsRef
name|ordList
init|=
name|dictionary
operator|.
name|lookupWord
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'o'
block|,
literal|'l'
block|,
literal|'r'
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|dictionary
operator|.
name|flagLookup
operator|.
name|get
argument_list|(
name|ordList
operator|.
name|ints
index|[
literal|0
index|]
argument_list|,
name|ref
argument_list|)
expr_stmt|;
name|char
name|flags
index|[]
init|=
name|Dictionary
operator|.
name|decodeFlags
argument_list|(
name|ref
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|flags
operator|.
name|length
argument_list|)
expr_stmt|;
name|affixStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|dictStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCompressedBeforeSetDictionary
specifier|public
name|void
name|testCompressedBeforeSetDictionary
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|affixStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"compressed-before-set.aff"
argument_list|)
decl_stmt|;
name|InputStream
name|dictStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"compressed.dic"
argument_list|)
decl_stmt|;
name|Dictionary
name|dictionary
init|=
operator|new
name|Dictionary
argument_list|(
name|affixStream
argument_list|,
name|dictStream
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dictionary
operator|.
name|lookupSuffix
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'e'
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dictionary
operator|.
name|lookupPrefix
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'s'
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|IntsRef
name|ordList
init|=
name|dictionary
operator|.
name|lookupWord
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'o'
block|,
literal|'l'
block|,
literal|'r'
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|dictionary
operator|.
name|flagLookup
operator|.
name|get
argument_list|(
name|ordList
operator|.
name|ints
index|[
literal|0
index|]
argument_list|,
name|ref
argument_list|)
expr_stmt|;
name|char
name|flags
index|[]
init|=
name|Dictionary
operator|.
name|decodeFlags
argument_list|(
name|ref
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|flags
operator|.
name|length
argument_list|)
expr_stmt|;
name|affixStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|dictStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCompressedEmptyAliasDictionary
specifier|public
name|void
name|testCompressedEmptyAliasDictionary
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|affixStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"compressed-empty-alias.aff"
argument_list|)
decl_stmt|;
name|InputStream
name|dictStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"compressed.dic"
argument_list|)
decl_stmt|;
name|Dictionary
name|dictionary
init|=
operator|new
name|Dictionary
argument_list|(
name|affixStream
argument_list|,
name|dictStream
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dictionary
operator|.
name|lookupSuffix
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'e'
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dictionary
operator|.
name|lookupPrefix
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'s'
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|IntsRef
name|ordList
init|=
name|dictionary
operator|.
name|lookupWord
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'o'
block|,
literal|'l'
block|,
literal|'r'
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|dictionary
operator|.
name|flagLookup
operator|.
name|get
argument_list|(
name|ordList
operator|.
name|ints
index|[
literal|0
index|]
argument_list|,
name|ref
argument_list|)
expr_stmt|;
name|char
name|flags
index|[]
init|=
name|Dictionary
operator|.
name|decodeFlags
argument_list|(
name|ref
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|flags
operator|.
name|length
argument_list|)
expr_stmt|;
name|affixStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|dictStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// malformed rule causes ParseException
DECL|method|testInvalidData
specifier|public
name|void
name|testInvalidData
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|affixStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"broken.aff"
argument_list|)
decl_stmt|;
name|InputStream
name|dictStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"simple.dic"
argument_list|)
decl_stmt|;
try|try
block|{
operator|new
name|Dictionary
argument_list|(
name|affixStream
argument_list|,
name|dictStream
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"The affix file contains a rule with less than four elements"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|24
argument_list|,
name|expected
operator|.
name|getErrorOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|affixStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|dictStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// malformed flags causes ParseException
DECL|method|testInvalidFlags
specifier|public
name|void
name|testInvalidFlags
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|affixStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"broken-flags.aff"
argument_list|)
decl_stmt|;
name|InputStream
name|dictStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"simple.dic"
argument_list|)
decl_stmt|;
try|try
block|{
operator|new
name|Dictionary
argument_list|(
name|affixStream
argument_list|,
name|dictStream
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"expected only one flag"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|affixStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|dictStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|CloseCheckInputStream
specifier|private
class|class
name|CloseCheckInputStream
extends|extends
name|FilterInputStream
block|{
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|method|CloseCheckInputStream
specifier|public
name|CloseCheckInputStream
parameter_list|(
name|InputStream
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|closed
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|isClosed
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|this
operator|.
name|closed
return|;
block|}
block|}
DECL|method|testResourceCleanup
specifier|public
name|void
name|testResourceCleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|CloseCheckInputStream
name|affixStream
init|=
operator|new
name|CloseCheckInputStream
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"compressed.aff"
argument_list|)
argument_list|)
decl_stmt|;
name|CloseCheckInputStream
name|dictStream
init|=
operator|new
name|CloseCheckInputStream
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"compressed.dic"
argument_list|)
argument_list|)
decl_stmt|;
operator|new
name|Dictionary
argument_list|(
name|affixStream
argument_list|,
name|dictStream
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|affixStream
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dictStream
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|affixStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|dictStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|affixStream
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dictStream
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testReplacements
specifier|public
name|void
name|testReplacements
parameter_list|()
throws|throws
name|Exception
block|{
name|Outputs
argument_list|<
name|CharsRef
argument_list|>
name|outputs
init|=
name|CharSequenceOutputs
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
name|Builder
argument_list|<
name|CharsRef
argument_list|>
name|builder
init|=
operator|new
name|Builder
argument_list|<>
argument_list|(
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE2
argument_list|,
name|outputs
argument_list|)
decl_stmt|;
name|IntsRef
name|scratchInts
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
comment|// a -> b
name|Util
operator|.
name|toUTF16
argument_list|(
literal|"a"
argument_list|,
name|scratchInts
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|scratchInts
argument_list|,
operator|new
name|CharsRef
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
comment|// ab -> c
name|Util
operator|.
name|toUTF16
argument_list|(
literal|"ab"
argument_list|,
name|scratchInts
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|scratchInts
argument_list|,
operator|new
name|CharsRef
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
comment|// c -> de
name|Util
operator|.
name|toUTF16
argument_list|(
literal|"c"
argument_list|,
name|scratchInts
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|scratchInts
argument_list|,
operator|new
name|CharsRef
argument_list|(
literal|"de"
argument_list|)
argument_list|)
expr_stmt|;
comment|// def -> gh
name|Util
operator|.
name|toUTF16
argument_list|(
literal|"def"
argument_list|,
name|scratchInts
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|scratchInts
argument_list|,
operator|new
name|CharsRef
argument_list|(
literal|"gh"
argument_list|)
argument_list|)
expr_stmt|;
name|FST
argument_list|<
name|CharsRef
argument_list|>
name|fst
init|=
name|builder
operator|.
name|finish
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"atestanother"
argument_list|)
decl_stmt|;
name|Dictionary
operator|.
name|applyMappings
argument_list|(
name|fst
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"btestbnother"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|(
literal|"abtestanother"
argument_list|)
expr_stmt|;
name|Dictionary
operator|.
name|applyMappings
argument_list|(
name|fst
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ctestbnother"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|(
literal|"atestabnother"
argument_list|)
expr_stmt|;
name|Dictionary
operator|.
name|applyMappings
argument_list|(
name|fst
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"btestcnother"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|(
literal|"abtestabnother"
argument_list|)
expr_stmt|;
name|Dictionary
operator|.
name|applyMappings
argument_list|(
name|fst
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ctestcnother"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|(
literal|"abtestabcnother"
argument_list|)
expr_stmt|;
name|Dictionary
operator|.
name|applyMappings
argument_list|(
name|fst
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ctestcdenother"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|(
literal|"defdefdefc"
argument_list|)
expr_stmt|;
name|Dictionary
operator|.
name|applyMappings
argument_list|(
name|fst
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ghghghde"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSetWithCrazyWhitespaceAndBOMs
specifier|public
name|void
name|testSetWithCrazyWhitespaceAndBOMs
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"UTF-8"
argument_list|,
name|Dictionary
operator|.
name|getDictionaryEncoding
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"SET\tUTF-8\n"
operator|.
name|getBytes
argument_list|(
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"UTF-8"
argument_list|,
name|Dictionary
operator|.
name|getDictionaryEncoding
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"SET\t UTF-8\n"
operator|.
name|getBytes
argument_list|(
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"UTF-8"
argument_list|,
name|Dictionary
operator|.
name|getDictionaryEncoding
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"\uFEFFSET\tUTF-8\n"
operator|.
name|getBytes
argument_list|(
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"UTF-8"
argument_list|,
name|Dictionary
operator|.
name|getDictionaryEncoding
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"\uFEFFSET\tUTF-8\r\n"
operator|.
name|getBytes
argument_list|(
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFlagWithCrazyWhitespace
specifier|public
name|void
name|testFlagWithCrazyWhitespace
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
name|Dictionary
operator|.
name|getFlagParsingStrategy
argument_list|(
literal|"FLAG\tUTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|Dictionary
operator|.
name|getFlagParsingStrategy
argument_list|(
literal|"FLAG    UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit