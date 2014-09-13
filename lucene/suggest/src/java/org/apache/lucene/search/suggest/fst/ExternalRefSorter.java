begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest.fst
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
operator|.
name|fst
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
name|Closeable
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
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|BytesRefIterator
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
name|OfflineSorter
import|;
end_import
begin_comment
comment|/**  * Builds and iterates over sequences stored on disk.  * @lucene.experimental  * @lucene.internal  */
end_comment
begin_class
DECL|class|ExternalRefSorter
specifier|public
class|class
name|ExternalRefSorter
implements|implements
name|BytesRefSorter
implements|,
name|Closeable
block|{
DECL|field|sort
specifier|private
specifier|final
name|OfflineSorter
name|sort
decl_stmt|;
DECL|field|writer
specifier|private
name|OfflineSorter
operator|.
name|ByteSequencesWriter
name|writer
decl_stmt|;
DECL|field|input
specifier|private
name|Path
name|input
decl_stmt|;
DECL|field|sorted
specifier|private
name|Path
name|sorted
decl_stmt|;
comment|/**    * Will buffer all sequences to a temporary file and then sort (all on-disk).    */
DECL|method|ExternalRefSorter
specifier|public
name|ExternalRefSorter
parameter_list|(
name|OfflineSorter
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
name|this
operator|.
name|input
operator|=
name|Files
operator|.
name|createTempFile
argument_list|(
name|OfflineSorter
operator|.
name|defaultTempDir
argument_list|()
argument_list|,
literal|"RefSorter-"
argument_list|,
literal|".raw"
argument_list|)
expr_stmt|;
name|this
operator|.
name|writer
operator|=
operator|new
name|OfflineSorter
operator|.
name|ByteSequencesWriter
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|BytesRef
name|utf8
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
name|writer
operator|.
name|write
argument_list|(
name|utf8
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|BytesRefIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|sorted
operator|==
literal|null
condition|)
block|{
name|closeWriter
argument_list|()
expr_stmt|;
name|sorted
operator|=
name|Files
operator|.
name|createTempFile
argument_list|(
name|OfflineSorter
operator|.
name|defaultTempDir
argument_list|()
argument_list|,
literal|"RefSorter-"
argument_list|,
literal|".sorted"
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|sort
operator|.
name|sort
argument_list|(
name|input
argument_list|,
name|sorted
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|Files
operator|.
name|delete
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
name|input
operator|=
literal|null
expr_stmt|;
block|}
return|return
operator|new
name|ByteSequenceIterator
argument_list|(
operator|new
name|OfflineSorter
operator|.
name|ByteSequencesReader
argument_list|(
name|sorted
argument_list|)
argument_list|)
return|;
block|}
DECL|method|closeWriter
specifier|private
name|void
name|closeWriter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Removes any written temporary files.    */
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
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|closeWriter
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|deleteFilesIfExist
argument_list|(
name|input
argument_list|,
name|sorted
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|input
argument_list|,
name|sorted
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Iterate over byte refs in a file.    */
DECL|class|ByteSequenceIterator
class|class
name|ByteSequenceIterator
implements|implements
name|BytesRefIterator
block|{
DECL|field|reader
specifier|private
specifier|final
name|OfflineSorter
operator|.
name|ByteSequencesReader
name|reader
decl_stmt|;
DECL|field|scratch
specifier|private
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|method|ByteSequenceIterator
specifier|public
name|ByteSequenceIterator
parameter_list|(
name|OfflineSorter
operator|.
name|ByteSequencesReader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|scratch
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|next
init|=
name|reader
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|scratch
operator|.
name|bytes
operator|=
name|next
expr_stmt|;
name|scratch
operator|.
name|length
operator|=
name|next
operator|.
name|length
expr_stmt|;
name|scratch
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|scratch
operator|=
literal|null
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|scratch
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|sort
operator|.
name|getComparator
argument_list|()
return|;
block|}
block|}
end_class
end_unit
