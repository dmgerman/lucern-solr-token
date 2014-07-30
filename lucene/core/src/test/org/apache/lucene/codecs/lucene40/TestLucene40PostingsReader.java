begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_class
DECL|class|TestLucene40PostingsReader
specifier|public
class|class
name|TestLucene40PostingsReader
extends|extends
name|LuceneTestCase
block|{
DECL|field|terms
specifier|static
specifier|final
name|String
name|terms
index|[]
init|=
operator|new
name|String
index|[
literal|100
index|]
decl_stmt|;
static|static
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|terms
index|[
name|i
index|]
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
block|{
name|OLD_FORMAT_IMPERSONATION_IS_ACTIVE
operator|=
literal|true
expr_stmt|;
comment|// explicitly instantiates ancient codec
block|}
comment|/** tests terms with different probabilities of being in the document.    *  depends heavily on term vectors cross-check at checkIndex    */
DECL|method|testPostings
specifier|public
name|void
name|testPostings
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"postings"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setCodec
argument_list|(
name|Codec
operator|.
name|forName
argument_list|(
literal|"Lucene40"
argument_list|)
argument_list|)
expr_stmt|;
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
comment|// id field
name|FieldType
name|idType
init|=
operator|new
name|FieldType
argument_list|(
name|StringField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|idType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Field
name|idField
init|=
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|idType
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
comment|// title field: short text field
name|FieldType
name|titleType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|titleType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|titleType
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|titleType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|titleType
operator|.
name|setIndexOptions
argument_list|(
name|indexOptions
argument_list|()
argument_list|)
expr_stmt|;
name|Field
name|titleField
init|=
operator|new
name|Field
argument_list|(
literal|"title"
argument_list|,
literal|""
argument_list|,
name|titleType
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|titleField
argument_list|)
expr_stmt|;
comment|// body field: long text field
name|FieldType
name|bodyType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|bodyType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bodyType
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bodyType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bodyType
operator|.
name|setIndexOptions
argument_list|(
name|indexOptions
argument_list|()
argument_list|)
expr_stmt|;
name|Field
name|bodyField
init|=
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|""
argument_list|,
name|bodyType
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|bodyField
argument_list|)
expr_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|1000
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|idField
operator|.
name|setStringValue
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|titleField
operator|.
name|setStringValue
argument_list|(
name|fieldValue
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|bodyField
operator|.
name|setStringValue
argument_list|(
name|fieldValue
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|==
literal|0
condition|)
block|{
name|iw
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// delete 1-100% of docs
name|iw
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"title"
argument_list|,
name|terms
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|terms
operator|.
name|length
argument_list|)
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// checkindex
block|}
DECL|method|indexOptions
name|IndexOptions
name|indexOptions
parameter_list|()
block|{
switch|switch
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
return|return
name|IndexOptions
operator|.
name|DOCS_ONLY
return|;
case|case
literal|1
case|:
return|return
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
return|;
case|case
literal|2
case|:
return|return
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
return|;
default|default:
return|return
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
return|;
block|}
block|}
DECL|method|fieldValue
name|String
name|fieldValue
parameter_list|(
name|int
name|maxTF
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|shuffled
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
name|int
name|i
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|terms
operator|.
name|length
argument_list|)
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|terms
operator|.
name|length
condition|)
block|{
name|int
name|tf
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
name|maxTF
argument_list|)
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
name|tf
condition|;
name|j
operator|++
control|)
block|{
name|shuffled
operator|.
name|add
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|shuffled
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|term
range|:
name|shuffled
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
