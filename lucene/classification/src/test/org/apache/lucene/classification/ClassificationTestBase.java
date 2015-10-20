begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.classification
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
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
name|LeafReader
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
name|SlowCompositeReaderWrapper
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
name|After
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import
begin_comment
comment|/**  * Base class for testing {@link Classifier}s  */
end_comment
begin_class
DECL|class|ClassificationTestBase
specifier|public
specifier|abstract
class|class
name|ClassificationTestBase
parameter_list|<
name|T
parameter_list|>
extends|extends
name|LuceneTestCase
block|{
DECL|field|POLITICS_INPUT
specifier|protected
specifier|static
specifier|final
name|String
name|POLITICS_INPUT
init|=
literal|"Here are some interesting questions and answers about Mitt Romney.. "
operator|+
literal|"If you don't know the answer to the question about Mitt Romney, then simply click on the answer below the question section."
decl_stmt|;
DECL|field|POLITICS_RESULT
specifier|protected
specifier|static
specifier|final
name|BytesRef
name|POLITICS_RESULT
init|=
operator|new
name|BytesRef
argument_list|(
literal|"politics"
argument_list|)
decl_stmt|;
DECL|field|TECHNOLOGY_INPUT
specifier|protected
specifier|static
specifier|final
name|String
name|TECHNOLOGY_INPUT
init|=
literal|"Much is made of what the likes of Facebook, Google and Apple know about users."
operator|+
literal|" Truth is, Amazon may know more."
decl_stmt|;
DECL|field|STRONG_TECHNOLOGY_INPUT
specifier|protected
specifier|static
specifier|final
name|String
name|STRONG_TECHNOLOGY_INPUT
init|=
literal|"Much is made of what the likes of Facebook, Google and Apple know about users."
operator|+
literal|" Truth is, Amazon may know more. This technology observation is extracted from the internet."
decl_stmt|;
DECL|field|SUPER_STRONG_TECHNOLOGY_INPUT
specifier|protected
specifier|static
specifier|final
name|String
name|SUPER_STRONG_TECHNOLOGY_INPUT
init|=
literal|"More than 400 million people trust Google with their e-mail, and 50 million store files"
operator|+
literal|" in the cloud using the Dropbox service. People manage their bank accounts, pay bills, trade stocks and "
operator|+
literal|"generally transfer or store huge volumes of personal data online. traveling seeks raises some questions Republican presidential. "
decl_stmt|;
DECL|field|TECHNOLOGY_RESULT
specifier|protected
specifier|static
specifier|final
name|BytesRef
name|TECHNOLOGY_RESULT
init|=
operator|new
name|BytesRef
argument_list|(
literal|"technology"
argument_list|)
decl_stmt|;
DECL|field|indexWriter
specifier|protected
name|RandomIndexWriter
name|indexWriter
decl_stmt|;
DECL|field|dir
specifier|protected
name|Directory
name|dir
decl_stmt|;
DECL|field|ft
specifier|protected
name|FieldType
name|ft
decl_stmt|;
DECL|field|textFieldName
specifier|protected
name|String
name|textFieldName
decl_stmt|;
DECL|field|categoryFieldName
specifier|protected
name|String
name|categoryFieldName
decl_stmt|;
DECL|field|booleanFieldName
specifier|protected
name|String
name|booleanFieldName
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
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
name|indexWriter
operator|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|textFieldName
operator|=
literal|"text"
expr_stmt|;
name|categoryFieldName
operator|=
literal|"cat"
expr_stmt|;
name|booleanFieldName
operator|=
literal|"bool"
expr_stmt|;
name|ft
operator|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|indexWriter
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
DECL|method|checkCorrectClassification
specifier|protected
name|ClassificationResult
argument_list|<
name|T
argument_list|>
name|checkCorrectClassification
parameter_list|(
name|Classifier
argument_list|<
name|T
argument_list|>
name|classifier
parameter_list|,
name|String
name|inputDoc
parameter_list|,
name|T
name|expectedResult
parameter_list|)
throws|throws
name|Exception
block|{
name|ClassificationResult
argument_list|<
name|T
argument_list|>
name|classificationResult
init|=
name|classifier
operator|.
name|assignClass
argument_list|(
name|inputDoc
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|classificationResult
operator|.
name|getAssignedClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"got an assigned class of "
operator|+
name|classificationResult
operator|.
name|getAssignedClass
argument_list|()
argument_list|,
name|expectedResult
argument_list|,
name|classificationResult
operator|.
name|getAssignedClass
argument_list|()
argument_list|)
expr_stmt|;
name|double
name|score
init|=
name|classificationResult
operator|.
name|getScore
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"score should be between 0 and 1, got:"
operator|+
name|score
argument_list|,
name|score
operator|<=
literal|1
operator|&&
name|score
operator|>=
literal|0
argument_list|)
expr_stmt|;
return|return
name|classificationResult
return|;
block|}
DECL|method|checkOnlineClassification
specifier|protected
name|void
name|checkOnlineClassification
parameter_list|(
name|Classifier
argument_list|<
name|T
argument_list|>
name|classifier
parameter_list|,
name|String
name|inputDoc
parameter_list|,
name|T
name|expectedResult
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|textFieldName
parameter_list|,
name|String
name|classFieldName
parameter_list|)
throws|throws
name|Exception
block|{
name|checkOnlineClassification
argument_list|(
name|classifier
argument_list|,
name|inputDoc
argument_list|,
name|expectedResult
argument_list|,
name|analyzer
argument_list|,
name|textFieldName
argument_list|,
name|classFieldName
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|checkOnlineClassification
specifier|protected
name|void
name|checkOnlineClassification
parameter_list|(
name|Classifier
argument_list|<
name|T
argument_list|>
name|classifier
parameter_list|,
name|String
name|inputDoc
parameter_list|,
name|T
name|expectedResult
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|textFieldName
parameter_list|,
name|String
name|classFieldName
parameter_list|,
name|Query
name|query
parameter_list|)
throws|throws
name|Exception
block|{
name|getSampleIndex
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|ClassificationResult
argument_list|<
name|T
argument_list|>
name|classificationResult
init|=
name|classifier
operator|.
name|assignClass
argument_list|(
name|inputDoc
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|classificationResult
operator|.
name|getAssignedClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"got an assigned class of "
operator|+
name|classificationResult
operator|.
name|getAssignedClass
argument_list|()
argument_list|,
name|expectedResult
argument_list|,
name|classificationResult
operator|.
name|getAssignedClass
argument_list|()
argument_list|)
expr_stmt|;
name|double
name|score
init|=
name|classificationResult
operator|.
name|getScore
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"score should be between 0 and 1, got: "
operator|+
name|score
argument_list|,
name|score
operator|<=
literal|1
operator|&&
name|score
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|updateSampleIndex
argument_list|()
expr_stmt|;
name|ClassificationResult
argument_list|<
name|T
argument_list|>
name|secondClassificationResult
init|=
name|classifier
operator|.
name|assignClass
argument_list|(
name|inputDoc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|classificationResult
operator|.
name|getAssignedClass
argument_list|()
argument_list|,
name|secondClassificationResult
operator|.
name|getAssignedClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|valueOf
argument_list|(
name|score
argument_list|)
argument_list|,
name|Double
operator|.
name|valueOf
argument_list|(
name|secondClassificationResult
operator|.
name|getScore
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getSampleIndex
specifier|protected
name|LeafReader
name|getSampleIndex
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexWriter
operator|=
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
name|analyzer
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|String
name|text
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|text
operator|=
literal|"The traveling press secretary for Mitt Romney lost his cool and cursed at reporters "
operator|+
literal|"who attempted to ask questions of the Republican presidential candidate in a public plaza near the Tomb of "
operator|+
literal|"the Unknown Soldier in Warsaw Tuesday."
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
name|text
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"politics"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"true"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|text
operator|=
literal|"Mitt Romney seeks to assure Israel and Iran, as well as Jewish voters in the United"
operator|+
literal|" States, that he will be tougher against Iran's nuclear ambitions than President Barack Obama."
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
name|text
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"politics"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"true"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|text
operator|=
literal|"And there's a threshold question that he has to answer for the American people and "
operator|+
literal|"that's whether he is prepared to be commander-in-chief,\" she continued. \"As we look to the past events, we "
operator|+
literal|"know that this raises some questions about his preparedness and we'll see how the rest of his trip goes.\""
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
name|text
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"politics"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"true"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|text
operator|=
literal|"Still, when it comes to gun policy, many congressional Democrats have \"decided to "
operator|+
literal|"keep quiet and not go there,\" said Alan Lizotte, dean and professor at the State University of New York at "
operator|+
literal|"Albany's School of Criminal Justice."
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
name|text
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"politics"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"true"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|text
operator|=
literal|"Standing amongst the thousands of people at the state Capitol, Jorstad, director of "
operator|+
literal|"technology at the University of Wisconsin-La Crosse, documented the historic moment and shared it with the "
operator|+
literal|"world through the Internet."
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
name|text
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"technology"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"false"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|text
operator|=
literal|"So, about all those experts and analysts who've spent the past year or so saying "
operator|+
literal|"Facebook was going to make a phone. A new expert has stepped forward to say it's not going to happen."
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
name|text
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"technology"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"false"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|text
operator|=
literal|"More than 400 million people trust Google with their e-mail, and 50 million store files"
operator|+
literal|" in the cloud using the Dropbox service. People manage their bank accounts, pay bills, trade stocks and "
operator|+
literal|"generally transfer or store huge volumes of personal data online."
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
name|text
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"technology"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"false"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|text
operator|=
literal|"unlabeled doc"
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
name|text
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|indexWriter
operator|.
name|getReader
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getRandomIndex
specifier|protected
name|LeafReader
name|getRandomIndex
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexWriter
operator|=
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
name|analyzer
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
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
name|ft
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Random
name|random
init|=
name|random
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|b
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
name|createRandomString
argument_list|(
name|random
argument_list|)
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
argument_list|)
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|b
argument_list|)
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|indexWriter
operator|.
name|getReader
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createRandomString
specifier|private
name|String
name|createRandomString
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|updateSampleIndex
specifier|private
name|void
name|updateSampleIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|text
operator|=
literal|"Warren Bennis says John F. Kennedy grasped a key lesson about the presidency that few have followed."
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
name|text
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"politics"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"true"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|text
operator|=
literal|"Julian Zelizer says Bill Clinton is still trying to shape his party, years after the White House, while George W. Bush opts for a much more passive role."
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
name|text
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"politics"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"true"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|text
operator|=
literal|"Crossfire: Sen. Tim Scott passes on Sen. Lindsey Graham endorsement"
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
name|text
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"politics"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"true"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|text
operator|=
literal|"Illinois becomes 16th state to allow same-sex marriage."
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
name|text
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"politics"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"true"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|text
operator|=
literal|"Apple is developing iPhones with curved-glass screens and enhanced sensors that detect different levels of pressure, according to a new report."
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
name|text
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"technology"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"false"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|text
operator|=
literal|"The Xbox One is Microsoft's first new gaming console in eight years. It's a quality piece of hardware but it's also noteworthy because Microsoft is using it to make a statement."
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
name|text
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"technology"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"false"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|text
operator|=
literal|"Google says it will replace a Google Maps image after a California father complained it shows the body of his teen-age son, who was shot to death in 2009."
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
name|text
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"technology"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"false"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|text
operator|=
literal|"second unlabeled doc"
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
name|text
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
