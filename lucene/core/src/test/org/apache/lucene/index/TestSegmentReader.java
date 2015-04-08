begin_unit
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
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|store
operator|.
name|IOContext
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
begin_class
DECL|class|TestSegmentReader
specifier|public
class|class
name|TestSegmentReader
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|testDoc
specifier|private
name|Document
name|testDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
DECL|field|reader
specifier|private
name|SegmentReader
name|reader
init|=
literal|null
decl_stmt|;
comment|//TODO: Setup the reader w/ multiple documents
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
name|SegmentCommitInfo
name|info
init|=
name|DocHelper
operator|.
name|writeDoc
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|testDoc
argument_list|)
decl_stmt|;
name|reader
operator|=
operator|new
name|SegmentReader
argument_list|(
name|info
argument_list|,
name|IOContext
operator|.
name|READ
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|dir
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|nameValues
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|numFields
argument_list|(
name|testDoc
argument_list|)
operator|==
name|DocHelper
operator|.
name|all
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDocument
specifier|public
name|void
name|testDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|reader
operator|.
name|numDocs
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
operator|>=
literal|1
argument_list|)
expr_stmt|;
name|StoredDocument
name|result
init|=
name|reader
operator|.
name|document
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//There are 2 unstored fields on the document that are not preserved across writing
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|numFields
argument_list|(
name|result
argument_list|)
operator|==
name|DocHelper
operator|.
name|numFields
argument_list|(
name|testDoc
argument_list|)
operator|-
name|DocHelper
operator|.
name|unstored
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|StorableField
argument_list|>
name|fields
init|=
name|result
operator|.
name|getFields
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|StorableField
name|field
range|:
name|fields
control|)
block|{
name|assertTrue
argument_list|(
name|field
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|nameValues
operator|.
name|containsKey
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testGetFieldNameVariations
specifier|public
name|void
name|testGetFieldNameVariations
parameter_list|()
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|allFieldNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|indexedFieldNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|notIndexedFieldNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|tvFieldNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|noTVFieldNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fieldInfo
range|:
name|reader
operator|.
name|getFieldInfos
argument_list|()
control|)
block|{
specifier|final
name|String
name|name
init|=
name|fieldInfo
operator|.
name|name
decl_stmt|;
name|allFieldNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
name|indexedFieldNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|notIndexedFieldNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldInfo
operator|.
name|hasVectors
argument_list|()
condition|)
block|{
name|tvFieldNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
name|noTVFieldNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|allFieldNames
operator|.
name|size
argument_list|()
operator|==
name|DocHelper
operator|.
name|all
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|allFieldNames
control|)
block|{
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|nameValues
operator|.
name|containsKey
argument_list|(
name|s
argument_list|)
operator|==
literal|true
operator|||
name|s
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|indexedFieldNames
operator|.
name|size
argument_list|()
operator|==
name|DocHelper
operator|.
name|indexed
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|indexedFieldNames
control|)
block|{
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|indexed
operator|.
name|containsKey
argument_list|(
name|s
argument_list|)
operator|==
literal|true
operator|||
name|s
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|notIndexedFieldNames
operator|.
name|size
argument_list|()
operator|==
name|DocHelper
operator|.
name|unindexed
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//Get all indexed fields that are storing term vectors
name|assertTrue
argument_list|(
name|tvFieldNames
operator|.
name|size
argument_list|()
operator|==
name|DocHelper
operator|.
name|termvector
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|noTVFieldNames
operator|.
name|size
argument_list|()
operator|==
name|DocHelper
operator|.
name|notermvector
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTerms
specifier|public
name|void
name|testTerms
parameter_list|()
throws|throws
name|IOException
block|{
name|Fields
name|fields
init|=
name|MultiFields
operator|.
name|getFields
argument_list|(
name|reader
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|term
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|term
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|String
name|fieldValue
init|=
operator|(
name|String
operator|)
name|DocHelper
operator|.
name|nameValues
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fieldValue
operator|.
name|indexOf
argument_list|(
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|PostingsEnum
name|termDocs
init|=
name|TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|reader
argument_list|,
name|DocHelper
operator|.
name|TEXT_FIELD_1_KEY
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"field"
argument_list|)
argument_list|,
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|reader
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|termDocs
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|termDocs
operator|=
name|TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|reader
argument_list|,
name|DocHelper
operator|.
name|NO_NORMS_KEY
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|DocHelper
operator|.
name|NO_NORMS_TEXT
argument_list|)
argument_list|,
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|reader
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|termDocs
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|PostingsEnum
name|positions
init|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|reader
argument_list|,
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|reader
argument_list|)
argument_list|,
name|DocHelper
operator|.
name|TEXT_FIELD_1_KEY
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"field"
argument_list|)
argument_list|)
decl_stmt|;
comment|// NOTE: prior rev of this test was failing to first
comment|// call next here:
name|assertTrue
argument_list|(
name|positions
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|positions
operator|.
name|docID
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|positions
operator|.
name|nextPosition
argument_list|()
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testNorms
specifier|public
name|void
name|testNorms
parameter_list|()
throws|throws
name|IOException
block|{
comment|//TODO: Not sure how these work/should be tested
comment|/*     try {       byte [] norms = reader.norms(DocHelper.TEXT_FIELD_1_KEY);       System.out.println("Norms: " + norms);       assertTrue(norms != null);     } catch (IOException e) {       e.printStackTrace();       assertTrue(false);     } */
name|checkNorms
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|checkNorms
specifier|public
specifier|static
name|void
name|checkNorms
parameter_list|(
name|LeafReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
comment|// test omit norms
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|DocHelper
operator|.
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IndexableField
name|f
init|=
name|DocHelper
operator|.
name|fields
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
name|assertEquals
argument_list|(
name|reader
operator|.
name|getNormValues
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
operator|!=
literal|null
argument_list|,
operator|!
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|reader
operator|.
name|getNormValues
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
operator|!=
literal|null
argument_list|,
operator|!
name|DocHelper
operator|.
name|noNorms
operator|.
name|containsKey
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|reader
operator|.
name|getNormValues
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// test for norms of null
name|NumericDocValues
name|norms
init|=
name|MultiDocValues
operator|.
name|getNormValues
argument_list|(
name|reader
argument_list|,
name|f
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|norms
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testTermVectors
specifier|public
name|void
name|testTermVectors
parameter_list|()
throws|throws
name|IOException
block|{
name|Terms
name|result
init|=
name|reader
operator|.
name|getTermVectors
argument_list|(
literal|0
argument_list|)
operator|.
name|terms
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_2_KEY
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|result
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|term
init|=
name|termsEnum
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|int
name|freq
init|=
operator|(
name|int
operator|)
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|FIELD_2_TEXT
operator|.
name|indexOf
argument_list|(
name|term
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|freq
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
name|Fields
name|results
init|=
name|reader
operator|.
name|getTermVectors
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|results
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"We do not have 3 term freq vectors"
argument_list|,
literal|3
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOutOfBoundsAccess
specifier|public
name|void
name|testOutOfBoundsAccess
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|numDocs
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
try|try
block|{
name|reader
operator|.
name|document
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|reader
operator|.
name|getTermVectors
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|reader
operator|.
name|document
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|reader
operator|.
name|getTermVectors
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|expected
parameter_list|)
block|{}
block|}
block|}
end_class
end_unit
