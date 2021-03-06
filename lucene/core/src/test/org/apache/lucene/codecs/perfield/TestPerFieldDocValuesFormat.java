begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.codecs.perfield
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|perfield
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
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|codecs
operator|.
name|DocValuesFormat
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
name|asserting
operator|.
name|AssertingCodec
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
name|BinaryDocValuesField
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
name|NumericDocValuesField
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
name|BaseDocValuesFormatTestCase
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
name|BinaryDocValues
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
name|DirectoryReader
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
name|IndexWriter
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
name|IndexWriterConfig
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
name|RandomCodec
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
name|search
operator|.
name|Query
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
name|TermQuery
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
name|TopDocs
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
name|TestUtil
import|;
end_import
begin_comment
comment|/**  * Basic tests of PerFieldDocValuesFormat  */
end_comment
begin_class
DECL|class|TestPerFieldDocValuesFormat
specifier|public
class|class
name|TestPerFieldDocValuesFormat
extends|extends
name|BaseDocValuesFormatTestCase
block|{
DECL|field|codec
specifier|private
name|Codec
name|codec
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|codec
operator|=
operator|new
name|RandomCodec
argument_list|(
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCodec
specifier|protected
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|codec
return|;
block|}
annotation|@
name|Override
DECL|method|codecAcceptsHugeBinaryValues
specifier|protected
name|boolean
name|codecAcceptsHugeBinaryValues
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|TestUtil
operator|.
name|fieldSupportsHugeBinaryDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|// just a simple trivial test
comment|// TODO: we should come up with a test that somehow checks that segment suffix
comment|// is respected by all codec apis (not just docvalues and postings)
DECL|method|testTwoFieldsTwoFormats
specifier|public
name|void
name|testTwoFieldsTwoFormats
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// we don't use RandomIndexWriter because it might add more docvalues than we expect !!!!1
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
decl_stmt|;
specifier|final
name|DocValuesFormat
name|fast
init|=
name|TestUtil
operator|.
name|getDefaultDocValuesFormat
argument_list|()
decl_stmt|;
specifier|final
name|DocValuesFormat
name|slow
init|=
name|DocValuesFormat
operator|.
name|forName
argument_list|(
literal|"SimpleText"
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setCodec
argument_list|(
operator|new
name|AssertingCodec
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocValuesFormat
name|getDocValuesFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
if|if
condition|(
literal|"dv1"
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
return|return
name|fast
return|;
block|}
else|else
block|{
return|return
name|slow
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|IndexWriter
name|iwriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|longTerm
init|=
literal|"longtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongterm"
decl_stmt|;
name|String
name|text
init|=
literal|"This is the text to be indexed. "
operator|+
name|longTerm
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"fieldname"
argument_list|,
name|text
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"dv1"
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"dv2"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"hello world"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now search the index:
name|IndexReader
name|ireader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
comment|// read-only=true
name|IndexSearcher
name|isearcher
init|=
name|newSearcher
argument_list|(
name|ireader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|isearcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"fieldname"
argument_list|,
name|longTerm
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"fieldname"
argument_list|,
literal|"text"
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocs
name|hits
init|=
name|isearcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// Iterate through the results:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|hitDoc
init|=
name|isearcher
operator|.
name|doc
argument_list|(
name|hits
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|text
argument_list|,
name|hitDoc
operator|.
name|get
argument_list|(
literal|"fieldname"
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
name|ireader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
assert|;
name|NumericDocValues
name|dv
init|=
name|ireader
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getNumericDocValues
argument_list|(
literal|"dv1"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|dv
operator|.
name|get
argument_list|(
name|hits
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|BinaryDocValues
name|dv2
init|=
name|ireader
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getBinaryDocValues
argument_list|(
literal|"dv2"
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|term
init|=
name|dv2
operator|.
name|get
argument_list|(
name|hits
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"hello world"
argument_list|)
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
name|ireader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
