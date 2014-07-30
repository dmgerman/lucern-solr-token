begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.pulsing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|pulsing
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
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormatSymbols
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|MockAnalyzer
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
name|codecs
operator|.
name|Codec
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
name|Document
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
name|FieldType
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
name|TextField
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
name|DocsEnum
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
name|FieldInfo
operator|.
name|IndexOptions
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
name|MultiFields
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
name|RandomIndexWriter
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
name|TermsEnum
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
name|DocIdSetIterator
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
name|BaseDirectoryWrapper
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
begin_comment
comment|/**  * Pulses 10k terms/docs,   * originally designed to find JRE bugs (https://issues.apache.org/jira/browse/LUCENE-3335)  *   * @lucene.experimental  */
end_comment
begin_class
annotation|@
name|LuceneTestCase
operator|.
name|Nightly
DECL|class|Test10KPulsings
specifier|public
class|class
name|Test10KPulsings
extends|extends
name|LuceneTestCase
block|{
DECL|method|test10kPulsed
specifier|public
name|void
name|test10kPulsed
parameter_list|()
throws|throws
name|Exception
block|{
comment|// we always run this test with pulsing codec.
name|Codec
name|cp
init|=
name|TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|Pulsing41PostingsFormat
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|f
init|=
name|createTempDir
argument_list|(
literal|"10kpulsed"
argument_list|)
decl_stmt|;
name|BaseDirectoryWrapper
name|dir
init|=
name|newFSDirectory
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|dir
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// we do this ourselves explicitly
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setCodec
argument_list|(
name|cp
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
expr_stmt|;
break|break;
default|default:
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
break|break;
block|}
name|Field
name|field
init|=
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|""
argument_list|,
name|ft
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|NumberFormat
name|df
init|=
operator|new
name|DecimalFormat
argument_list|(
literal|"00000"
argument_list|,
operator|new
name|DecimalFormatSymbols
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
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
literal|10050
condition|;
name|i
operator|++
control|)
block|{
name|field
operator|.
name|setStringValue
argument_list|(
name|df
operator|.
name|format
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|TermsEnum
name|te
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|ir
argument_list|,
literal|"field"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|DocsEnum
name|de
init|=
literal|null
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
literal|10050
condition|;
name|i
operator|++
control|)
block|{
name|String
name|expected
init|=
name|df
operator|.
name|format
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|te
operator|.
name|next
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|de
operator|=
name|TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|te
argument_list|,
literal|null
argument_list|,
name|de
argument_list|,
name|DocsEnum
operator|.
name|FLAG_NONE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|de
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|de
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** a variant, that uses pulsing, but uses a high TF to force pass thru to the underlying codec    */
DECL|method|test10kNotPulsed
specifier|public
name|void
name|test10kNotPulsed
parameter_list|()
throws|throws
name|Exception
block|{
comment|// we always run this test with pulsing codec.
name|int
name|freqCutoff
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|Codec
name|cp
init|=
name|TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|Pulsing41PostingsFormat
argument_list|(
name|freqCutoff
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|f
init|=
name|createTempDir
argument_list|(
literal|"10knotpulsed"
argument_list|)
decl_stmt|;
name|BaseDirectoryWrapper
name|dir
init|=
name|newFSDirectory
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|dir
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// we do this ourselves explicitly
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setCodec
argument_list|(
name|cp
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
expr_stmt|;
break|break;
default|default:
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
break|break;
block|}
name|Field
name|field
init|=
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|""
argument_list|,
name|ft
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|NumberFormat
name|df
init|=
operator|new
name|DecimalFormat
argument_list|(
literal|"00000"
argument_list|,
operator|new
name|DecimalFormatSymbols
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|freq
init|=
name|freqCutoff
operator|+
literal|1
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
literal|10050
condition|;
name|i
operator|++
control|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|freq
condition|;
name|j
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|df
operator|.
name|format
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
comment|// whitespace
block|}
name|field
operator|.
name|setStringValue
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|TermsEnum
name|te
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|ir
argument_list|,
literal|"field"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|DocsEnum
name|de
init|=
literal|null
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
literal|10050
condition|;
name|i
operator|++
control|)
block|{
name|String
name|expected
init|=
name|df
operator|.
name|format
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|te
operator|.
name|next
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|de
operator|=
name|TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|te
argument_list|,
literal|null
argument_list|,
name|de
argument_list|,
name|DocsEnum
operator|.
name|FLAG_NONE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|de
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|de
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
