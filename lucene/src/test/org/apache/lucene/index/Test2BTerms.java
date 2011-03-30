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
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|*
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
name|*
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
name|*
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
name|tokenattributes
operator|.
name|*
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
name|*
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
name|codecs
operator|.
name|CodecProvider
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
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import
begin_comment
comment|// NOTE: this test will fail w/ PreFlexRW codec!  (Because
end_comment
begin_comment
comment|// this test uses full binary term space, but PreFlex cannot
end_comment
begin_comment
comment|// handle this since it requires the terms are UTF8 bytes).
end_comment
begin_comment
comment|//
end_comment
begin_comment
comment|// Also, SimpleText codec will consume very large amounts of
end_comment
begin_comment
comment|// disk (but, should run successfully).  Best to run w/
end_comment
begin_comment
comment|// -Dtests.codec=Standard, and w/ plenty of RAM, eg:
end_comment
begin_comment
comment|//
end_comment
begin_comment
comment|//   ant compile-test
end_comment
begin_comment
comment|//
end_comment
begin_comment
comment|//   java -server -Xmx2g -Xms2g -d64 -cp .:lib/junit-4.7.jar:./build/classes/test:./build/classes/java -Dlucene.version=4.0-dev -Dtests.directory=SimpleFSDirectory -Dtests.codec=Standard -DtempDir=build -ea org.junit.runner.JUnitCore org.apache.lucene.index.Test2BTerms
end_comment
begin_comment
comment|//
end_comment
begin_class
DECL|class|Test2BTerms
specifier|public
class|class
name|Test2BTerms
extends|extends
name|LuceneTestCase
block|{
DECL|field|TOKEN_LEN
specifier|private
specifier|final
specifier|static
name|int
name|TOKEN_LEN
init|=
literal|10
decl_stmt|;
DECL|field|bytes
specifier|private
specifier|final
specifier|static
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
name|TOKEN_LEN
argument_list|)
decl_stmt|;
DECL|class|MyTokenStream
specifier|private
specifier|static
specifier|final
class|class
name|MyTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|tokensPerDoc
specifier|private
specifier|final
name|int
name|tokensPerDoc
decl_stmt|;
DECL|field|tokenCount
specifier|private
name|int
name|tokenCount
decl_stmt|;
DECL|field|byteUpto
specifier|private
name|int
name|byteUpto
decl_stmt|;
DECL|method|MyTokenStream
specifier|public
name|MyTokenStream
parameter_list|(
name|int
name|tokensPerDoc
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|MyAttributeFactory
argument_list|(
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|tokensPerDoc
operator|=
name|tokensPerDoc
expr_stmt|;
name|addAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|length
operator|=
name|TOKEN_LEN
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|tokenCount
operator|>=
name|tokensPerDoc
condition|)
block|{
return|return
literal|false
return|;
block|}
name|random
operator|.
name|nextBytes
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|)
expr_stmt|;
name|tokenCount
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|tokenCount
operator|=
literal|0
expr_stmt|;
block|}
DECL|class|MyTermAttributeImpl
specifier|private
specifier|final
specifier|static
class|class
name|MyTermAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|TermToBytesRefAttribute
block|{
DECL|method|fillBytesRef
specifier|public
name|int
name|fillBytesRef
parameter_list|()
block|{
return|return
name|bytes
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|getBytesRef
specifier|public
name|BytesRef
name|getBytesRef
parameter_list|()
block|{
return|return
name|bytes
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{       }
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|other
operator|==
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{       }
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
DECL|class|MyAttributeFactory
specifier|private
specifier|static
specifier|final
class|class
name|MyAttributeFactory
extends|extends
name|AttributeFactory
block|{
DECL|field|delegate
specifier|private
specifier|final
name|AttributeFactory
name|delegate
decl_stmt|;
DECL|method|MyAttributeFactory
specifier|public
name|MyAttributeFactory
parameter_list|(
name|AttributeFactory
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createAttributeInstance
specifier|public
name|AttributeImpl
name|createAttributeInstance
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attClass
parameter_list|)
block|{
if|if
condition|(
name|attClass
operator|==
name|TermToBytesRefAttribute
operator|.
name|class
condition|)
return|return
operator|new
name|MyTermAttributeImpl
argument_list|()
return|;
if|if
condition|(
name|CharTermAttribute
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|attClass
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no"
argument_list|)
throw|;
return|return
name|delegate
operator|.
name|createAttributeInstance
argument_list|(
name|attClass
argument_list|)
return|;
block|}
block|}
block|}
annotation|@
name|Ignore
argument_list|(
literal|"Takes ~4 hours to run on a fast machine!!  And requires that you don't use PreFlex codec."
argument_list|)
DECL|method|test2BTerms
specifier|public
name|void
name|test2BTerms
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
literal|"PreFlex"
operator|.
name|equals
argument_list|(
name|CodecProvider
operator|.
name|getDefault
argument_list|()
operator|.
name|getDefaultFieldCodec
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"thist test cannot run with PreFlex codec"
argument_list|)
throw|;
block|}
name|long
name|TERM_COUNT
init|=
operator|(
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
operator|)
operator|+
literal|100000000
decl_stmt|;
name|int
name|TERMS_PER_DOC
init|=
literal|1000000
decl_stmt|;
name|Directory
name|dir
init|=
name|newFSDirectory
argument_list|(
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"2BTerms"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|256.0
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|false
argument_list|,
literal|10
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|MergePolicy
name|mp
init|=
name|w
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
if|if
condition|(
name|mp
operator|instanceof
name|LogByteSizeMergePolicy
condition|)
block|{
comment|// 1 petabyte:
operator|(
operator|(
name|LogByteSizeMergePolicy
operator|)
name|mp
operator|)
operator|.
name|setMaxMergeMB
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
operator|new
name|MyTokenStream
argument_list|(
name|TERMS_PER_DOC
argument_list|)
argument_list|)
decl_stmt|;
name|field
operator|.
name|setOmitTermFreqAndPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|field
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
comment|//w.setInfoStream(System.out);
specifier|final
name|int
name|numDocs
init|=
call|(
name|int
call|)
argument_list|(
name|TERM_COUNT
operator|/
name|TERMS_PER_DOC
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
specifier|final
name|long
name|t0
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|" of "
operator|+
name|numDocs
operator|+
literal|" "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t0
operator|)
operator|+
literal|" msec"
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"now optimize..."
argument_list|)
expr_stmt|;
name|w
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"now CheckIndex..."
argument_list|)
expr_stmt|;
name|CheckIndex
operator|.
name|Status
name|status
init|=
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|long
name|tc
init|=
name|status
operator|.
name|segmentInfos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|termIndexStatus
operator|.
name|termCount
decl_stmt|;
name|assertTrue
argument_list|(
literal|"count "
operator|+
name|tc
operator|+
literal|" is not> "
operator|+
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|tc
operator|>
name|Integer
operator|.
name|MAX_VALUE
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
