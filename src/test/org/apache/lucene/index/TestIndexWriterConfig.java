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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
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
name|Set
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
name|SimpleAnalyzer
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
name|WhitespaceAnalyzer
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
name|DocumentsWriter
operator|.
name|IndexingChain
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
operator|.
name|OpenMode
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
name|DefaultSimilarity
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
name|Similarity
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
name|RAMDirectory
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
name|LuceneTestCaseJ4
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
DECL|class|TestIndexWriterConfig
specifier|public
class|class
name|TestIndexWriterConfig
extends|extends
name|LuceneTestCaseJ4
block|{
DECL|class|MySimilarity
specifier|private
specifier|static
specifier|final
class|class
name|MySimilarity
extends|extends
name|DefaultSimilarity
block|{
comment|// Does not implement anything - used only for type checking on IndexWriterConfig.
block|}
DECL|class|MyIndexingChain
specifier|private
specifier|static
specifier|final
class|class
name|MyIndexingChain
extends|extends
name|IndexingChain
block|{
comment|// Does not implement anything - used only for type checking on IndexWriterConfig.
annotation|@
name|Override
DECL|method|getChain
name|DocConsumer
name|getChain
parameter_list|(
name|DocumentsWriter
name|documentsWriter
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexWriterConfig
name|conf
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|WhitespaceAnalyzer
operator|.
name|class
argument_list|,
name|conf
operator|.
name|getAnalyzer
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|conf
operator|.
name|getIndexCommit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|KeepOnlyLastCommitDeletionPolicy
operator|.
name|class
argument_list|,
name|conf
operator|.
name|getIndexDeletionPolicy
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexWriterConfig
operator|.
name|UNLIMITED_FIELD_LENGTH
argument_list|,
name|conf
operator|.
name|getMaxFieldLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ConcurrentMergeScheduler
operator|.
name|class
argument_list|,
name|conf
operator|.
name|getMergeScheduler
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OpenMode
operator|.
name|CREATE_OR_APPEND
argument_list|,
name|conf
operator|.
name|getOpenMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Similarity
operator|.
name|getDefault
argument_list|()
operator|==
name|conf
operator|.
name|getSimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexWriterConfig
operator|.
name|DEFAULT_TERM_INDEX_INTERVAL
argument_list|,
name|conf
operator|.
name|getTermIndexInterval
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexWriterConfig
operator|.
name|getDefaultWriteLockTimeout
argument_list|()
argument_list|,
name|conf
operator|.
name|getWriteLockTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexWriterConfig
operator|.
name|WRITE_LOCK_TIMEOUT
argument_list|,
name|IndexWriterConfig
operator|.
name|getDefaultWriteLockTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexWriterConfig
operator|.
name|DEFAULT_MAX_BUFFERED_DELETE_TERMS
argument_list|,
name|conf
operator|.
name|getMaxBufferedDeleteTerms
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexWriterConfig
operator|.
name|DEFAULT_RAM_BUFFER_SIZE_MB
argument_list|,
name|conf
operator|.
name|getRAMBufferSizeMB
argument_list|()
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexWriterConfig
operator|.
name|DEFAULT_MAX_BUFFERED_DOCS
argument_list|,
name|conf
operator|.
name|getMaxBufferedDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DocumentsWriter
operator|.
name|defaultIndexingChain
operator|==
name|conf
operator|.
name|getIndexingChain
argument_list|()
argument_list|)
expr_stmt|;
comment|// Sanity check - validate that all getters are covered.
name|Set
argument_list|<
name|String
argument_list|>
name|getters
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|getters
operator|.
name|add
argument_list|(
literal|"getAnalyzer"
argument_list|)
expr_stmt|;
name|getters
operator|.
name|add
argument_list|(
literal|"getIndexCommit"
argument_list|)
expr_stmt|;
name|getters
operator|.
name|add
argument_list|(
literal|"getIndexDeletionPolicy"
argument_list|)
expr_stmt|;
name|getters
operator|.
name|add
argument_list|(
literal|"getMaxFieldLength"
argument_list|)
expr_stmt|;
name|getters
operator|.
name|add
argument_list|(
literal|"getMergeScheduler"
argument_list|)
expr_stmt|;
name|getters
operator|.
name|add
argument_list|(
literal|"getOpenMode"
argument_list|)
expr_stmt|;
name|getters
operator|.
name|add
argument_list|(
literal|"getSimilarity"
argument_list|)
expr_stmt|;
name|getters
operator|.
name|add
argument_list|(
literal|"getTermIndexInterval"
argument_list|)
expr_stmt|;
name|getters
operator|.
name|add
argument_list|(
literal|"getWriteLockTimeout"
argument_list|)
expr_stmt|;
name|getters
operator|.
name|add
argument_list|(
literal|"getDefaultWriteLockTimeout"
argument_list|)
expr_stmt|;
name|getters
operator|.
name|add
argument_list|(
literal|"getMaxBufferedDeleteTerms"
argument_list|)
expr_stmt|;
name|getters
operator|.
name|add
argument_list|(
literal|"getRAMBufferSizeMB"
argument_list|)
expr_stmt|;
name|getters
operator|.
name|add
argument_list|(
literal|"getMaxBufferedDocs"
argument_list|)
expr_stmt|;
name|getters
operator|.
name|add
argument_list|(
literal|"getIndexingChain"
argument_list|)
expr_stmt|;
for|for
control|(
name|Method
name|m
range|:
name|IndexWriterConfig
operator|.
name|class
operator|.
name|getDeclaredMethods
argument_list|()
control|)
block|{
if|if
condition|(
name|m
operator|.
name|getDeclaringClass
argument_list|()
operator|==
name|IndexWriterConfig
operator|.
name|class
operator|&&
name|m
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"get"
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
literal|"method "
operator|+
name|m
operator|.
name|getName
argument_list|()
operator|+
literal|" is not tested for defaults"
argument_list|,
name|getters
operator|.
name|contains
argument_list|(
name|m
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testSettersChaining
specifier|public
name|void
name|testSettersChaining
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Ensures that every setter returns IndexWriterConfig to enable easy
comment|// chaining.
for|for
control|(
name|Method
name|m
range|:
name|IndexWriterConfig
operator|.
name|class
operator|.
name|getDeclaredMethods
argument_list|()
control|)
block|{
if|if
condition|(
name|m
operator|.
name|getDeclaringClass
argument_list|()
operator|==
name|IndexWriterConfig
operator|.
name|class
operator|&&
name|m
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"set"
argument_list|)
operator|&&
operator|!
name|Modifier
operator|.
name|isStatic
argument_list|(
name|m
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|"method "
operator|+
name|m
operator|.
name|getName
argument_list|()
operator|+
literal|" does not return IndexWriterConfig"
argument_list|,
name|IndexWriterConfig
operator|.
name|class
argument_list|,
name|m
operator|.
name|getReturnType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testConstants
specifier|public
name|void
name|testConstants
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Tests that the values of the constants does not change
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|IndexWriterConfig
operator|.
name|WRITE_LOCK_TIMEOUT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|128
argument_list|,
name|IndexWriterConfig
operator|.
name|DEFAULT_TERM_INDEX_INTERVAL
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|IndexWriterConfig
operator|.
name|UNLIMITED_FIELD_LENGTH
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|,
name|IndexWriterConfig
operator|.
name|DEFAULT_MAX_BUFFERED_DELETE_TERMS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|,
name|IndexWriterConfig
operator|.
name|DEFAULT_MAX_BUFFERED_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16.0
argument_list|,
name|IndexWriterConfig
operator|.
name|DEFAULT_RAM_BUFFER_SIZE_MB
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|str
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
for|for
control|(
name|Field
name|f
range|:
name|IndexWriterConfig
operator|.
name|class
operator|.
name|getDeclaredFields
argument_list|()
control|)
block|{
name|int
name|modifiers
init|=
name|f
operator|.
name|getModifiers
argument_list|()
decl_stmt|;
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|modifiers
argument_list|)
operator|&&
name|Modifier
operator|.
name|isFinal
argument_list|(
name|modifiers
argument_list|)
condition|)
block|{
comment|// Skip static final fields, they are only constants
continue|continue;
block|}
elseif|else
if|if
condition|(
literal|"indexingChain"
operator|.
name|equals
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|// indexingChain is a package-private setting and thus is not output by
comment|// toString.
continue|continue;
block|}
name|assertTrue
argument_list|(
name|f
operator|.
name|getName
argument_list|()
operator|+
literal|" not found in toString"
argument_list|,
name|str
operator|.
name|indexOf
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testClone
specifier|public
name|void
name|testClone
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexWriterConfig
name|conf
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|clone
init|=
operator|(
name|IndexWriterConfig
operator|)
name|conf
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|// Clone is shallow since not all parameters are cloneable.
name|assertTrue
argument_list|(
name|conf
operator|.
name|getIndexDeletionPolicy
argument_list|()
operator|==
name|clone
operator|.
name|getIndexDeletionPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ConcurrentMergeScheduler
operator|.
name|class
argument_list|,
name|clone
operator|.
name|getMergeScheduler
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidValues
specifier|public
name|void
name|testInvalidValues
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexWriterConfig
name|conf
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
comment|// Test Analyzer
name|assertEquals
argument_list|(
name|WhitespaceAnalyzer
operator|.
name|class
argument_list|,
name|conf
operator|.
name|getAnalyzer
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setAnalyzer
argument_list|(
operator|new
name|SimpleAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SimpleAnalyzer
operator|.
name|class
argument_list|,
name|conf
operator|.
name|getAnalyzer
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setAnalyzer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|WhitespaceAnalyzer
operator|.
name|class
argument_list|,
name|conf
operator|.
name|getAnalyzer
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test IndexDeletionPolicy
name|assertEquals
argument_list|(
name|KeepOnlyLastCommitDeletionPolicy
operator|.
name|class
argument_list|,
name|conf
operator|.
name|getIndexDeletionPolicy
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setIndexDeletionPolicy
argument_list|(
operator|new
name|SnapshotDeletionPolicy
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SnapshotDeletionPolicy
operator|.
name|class
argument_list|,
name|conf
operator|.
name|getIndexDeletionPolicy
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setIndexDeletionPolicy
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|KeepOnlyLastCommitDeletionPolicy
operator|.
name|class
argument_list|,
name|conf
operator|.
name|getIndexDeletionPolicy
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test MergeScheduler
name|assertEquals
argument_list|(
name|ConcurrentMergeScheduler
operator|.
name|class
argument_list|,
name|conf
operator|.
name|getMergeScheduler
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SerialMergeScheduler
operator|.
name|class
argument_list|,
name|conf
operator|.
name|getMergeScheduler
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMergeScheduler
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ConcurrentMergeScheduler
operator|.
name|class
argument_list|,
name|conf
operator|.
name|getMergeScheduler
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test Similarity
name|assertTrue
argument_list|(
name|Similarity
operator|.
name|getDefault
argument_list|()
operator|==
name|conf
operator|.
name|getSimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setSimilarity
argument_list|(
operator|new
name|MySimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MySimilarity
operator|.
name|class
argument_list|,
name|conf
operator|.
name|getSimilarity
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setSimilarity
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Similarity
operator|.
name|getDefault
argument_list|()
operator|==
name|conf
operator|.
name|getSimilarity
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test IndexingChain
name|assertTrue
argument_list|(
name|DocumentsWriter
operator|.
name|defaultIndexingChain
operator|==
name|conf
operator|.
name|getIndexingChain
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setIndexingChain
argument_list|(
operator|new
name|MyIndexingChain
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MyIndexingChain
operator|.
name|class
argument_list|,
name|conf
operator|.
name|getIndexingChain
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setIndexingChain
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DocumentsWriter
operator|.
name|defaultIndexingChain
operator|==
name|conf
operator|.
name|getIndexingChain
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|conf
operator|.
name|setMaxBufferedDeleteTerms
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should not have succeeded to set maxBufferedDeleteTerms to 0"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// this is expected
block|}
try|try
block|{
name|conf
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should not have succeeded to set maxBufferedDocs to 1"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// this is expected
block|}
try|try
block|{
comment|// Disable both MAX_BUF_DOCS and RAM_SIZE_MB
name|conf
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaxBufferedDocs
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should not have succeeded to disable maxBufferedDocs when ramBufferSizeMB is disabled as well"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// this is expected
block|}
name|conf
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|IndexWriterConfig
operator|.
name|DEFAULT_RAM_BUFFER_SIZE_MB
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaxBufferedDocs
argument_list|(
name|IndexWriterConfig
operator|.
name|DEFAULT_MAX_BUFFERED_DOCS
argument_list|)
expr_stmt|;
try|try
block|{
name|conf
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should not have succeeded to disable ramBufferSizeMB when maxBufferedDocs is disabled as well"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// this is expected
block|}
block|}
comment|/**    * @deprecated should be removed once all the deprecated setters are removed    *             from IndexWriter.    */
annotation|@
name|Test
DECL|method|testIndexWriterSetters
specifier|public
name|void
name|testIndexWriterSetters
parameter_list|()
throws|throws
name|Exception
block|{
comment|// This test intentionally tests deprecated methods. The purpose is to pass
comment|// whatever the user set on IW to IWC, so that if the user calls
comment|// iw.getConfig().getXYZ(), he'll get the same value he passed to
comment|// iw.setXYZ().
name|IndexWriterConfig
name|conf
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setSimilarity
argument_list|(
operator|new
name|MySimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MySimilarity
operator|.
name|class
argument_list|,
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getSimilarity
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMaxBufferedDeleteTerms
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMaxBufferedDeleteTerms
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMaxBufferedDocs
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMaxFieldLength
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMaxFieldLength
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SerialMergeScheduler
operator|.
name|class
argument_list|,
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergeScheduler
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|1.5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.5
argument_list|,
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getRAMBufferSizeMB
argument_list|()
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setTermIndexInterval
argument_list|(
literal|40
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|40
argument_list|,
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getTermIndexInterval
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setWriteLockTimeout
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getWriteLockTimeout
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
