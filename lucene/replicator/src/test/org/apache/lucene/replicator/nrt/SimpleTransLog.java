begin_unit
begin_package
DECL|package|org.apache.lucene.replicator.nrt
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
operator|.
name|nrt
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
name|EOFException
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
name|ByteBuffer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
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
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
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
name|CorruptIndexException
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
name|ByteArrayDataInput
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
name|DataInput
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
name|RAMOutputStream
import|;
end_import
begin_comment
comment|/** This is a stupid yet functional transaction log: it never fsync's, never prunes, it's over-synchronized, it hard-wires id field name to "docid", can  *  only handle specific docs/fields used by this test, etc.  It's just barely enough to show how a translog could work on top of NRT  *  replication to guarantee no data loss when nodes crash */
end_comment
begin_class
DECL|class|SimpleTransLog
class|class
name|SimpleTransLog
implements|implements
name|Closeable
block|{
DECL|field|channel
specifier|final
name|FileChannel
name|channel
decl_stmt|;
DECL|field|buffer
specifier|final
name|RAMOutputStream
name|buffer
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
DECL|field|intBuffer
specifier|final
name|byte
index|[]
name|intBuffer
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
DECL|field|intByteBuffer
specifier|final
name|ByteBuffer
name|intByteBuffer
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|intBuffer
argument_list|)
decl_stmt|;
DECL|field|OP_ADD_DOCUMENT
specifier|private
specifier|final
specifier|static
name|byte
name|OP_ADD_DOCUMENT
init|=
operator|(
name|byte
operator|)
literal|0
decl_stmt|;
DECL|field|OP_UPDATE_DOCUMENT
specifier|private
specifier|final
specifier|static
name|byte
name|OP_UPDATE_DOCUMENT
init|=
operator|(
name|byte
operator|)
literal|1
decl_stmt|;
DECL|field|OP_DELETE_DOCUMENTS
specifier|private
specifier|final
specifier|static
name|byte
name|OP_DELETE_DOCUMENTS
init|=
operator|(
name|byte
operator|)
literal|2
decl_stmt|;
DECL|method|SimpleTransLog
specifier|public
name|SimpleTransLog
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|channel
operator|=
name|FileChannel
operator|.
name|open
argument_list|(
name|path
argument_list|,
name|StandardOpenOption
operator|.
name|READ
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|,
name|StandardOpenOption
operator|.
name|CREATE_NEW
argument_list|)
expr_stmt|;
block|}
DECL|method|getNextLocation
specifier|public
specifier|synchronized
name|long
name|getNextLocation
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|channel
operator|.
name|position
argument_list|()
return|;
block|}
comment|/** Appends an addDocument op */
DECL|method|addDocument
specifier|public
specifier|synchronized
name|long
name|addDocument
parameter_list|(
name|String
name|id
parameter_list|,
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|buffer
operator|.
name|getFilePointer
argument_list|()
operator|==
literal|0
assert|;
name|buffer
operator|.
name|writeByte
argument_list|(
name|OP_ADD_DOCUMENT
argument_list|)
expr_stmt|;
name|encode
argument_list|(
name|id
argument_list|,
name|doc
argument_list|)
expr_stmt|;
return|return
name|flushBuffer
argument_list|()
return|;
block|}
comment|/** Appends an updateDocument op */
DECL|method|updateDocument
specifier|public
specifier|synchronized
name|long
name|updateDocument
parameter_list|(
name|String
name|id
parameter_list|,
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|buffer
operator|.
name|getFilePointer
argument_list|()
operator|==
literal|0
assert|;
name|buffer
operator|.
name|writeByte
argument_list|(
name|OP_UPDATE_DOCUMENT
argument_list|)
expr_stmt|;
name|encode
argument_list|(
name|id
argument_list|,
name|doc
argument_list|)
expr_stmt|;
return|return
name|flushBuffer
argument_list|()
return|;
block|}
comment|/** Appends a deleteDocuments op */
DECL|method|deleteDocuments
specifier|public
specifier|synchronized
name|long
name|deleteDocuments
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|buffer
operator|.
name|getFilePointer
argument_list|()
operator|==
literal|0
assert|;
name|buffer
operator|.
name|writeByte
argument_list|(
name|OP_DELETE_DOCUMENTS
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeString
argument_list|(
name|id
argument_list|)
expr_stmt|;
return|return
name|flushBuffer
argument_list|()
return|;
block|}
comment|/** Writes buffer to the file and returns the start position. */
DECL|method|flushBuffer
specifier|private
specifier|synchronized
name|long
name|flushBuffer
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|pos
init|=
name|channel
operator|.
name|position
argument_list|()
decl_stmt|;
name|int
name|len
init|=
operator|(
name|int
operator|)
name|buffer
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|buffer
operator|.
name|writeTo
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|intBuffer
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|len
operator|>>
literal|24
argument_list|)
expr_stmt|;
name|intBuffer
index|[
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|len
operator|>>
literal|16
argument_list|)
expr_stmt|;
name|intBuffer
index|[
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|len
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|intBuffer
index|[
literal|3
index|]
operator|=
operator|(
name|byte
operator|)
name|len
expr_stmt|;
name|intByteBuffer
operator|.
name|limit
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|intByteBuffer
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|writeBytesToChannel
argument_list|(
name|intByteBuffer
argument_list|)
expr_stmt|;
name|writeBytesToChannel
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|pos
return|;
block|}
DECL|method|writeBytesToChannel
specifier|private
name|void
name|writeBytesToChannel
parameter_list|(
name|ByteBuffer
name|src
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|left
init|=
name|src
operator|.
name|limit
argument_list|()
decl_stmt|;
while|while
condition|(
name|left
operator|!=
literal|0
condition|)
block|{
name|left
operator|-=
name|channel
operator|.
name|write
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readBytesFromChannel
specifier|private
name|void
name|readBytesFromChannel
parameter_list|(
name|long
name|pos
parameter_list|,
name|ByteBuffer
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|left
init|=
name|dest
operator|.
name|limit
argument_list|()
operator|-
name|dest
operator|.
name|position
argument_list|()
decl_stmt|;
name|long
name|end
init|=
name|pos
operator|+
name|left
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|end
condition|)
block|{
name|int
name|inc
init|=
name|channel
operator|.
name|read
argument_list|(
name|dest
argument_list|,
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|inc
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
name|pos
operator|+=
name|inc
expr_stmt|;
block|}
block|}
comment|/** Replays ops between start and end location against the provided writer.  Can run concurrently with ongoing operations. */
DECL|method|replay
specifier|public
name|void
name|replay
parameter_list|(
name|NodeProcess
name|primary
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|Connection
name|c
init|=
operator|new
name|Connection
argument_list|(
name|primary
operator|.
name|tcpPort
argument_list|)
init|)
block|{
name|c
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_INDEXING
argument_list|)
expr_stmt|;
name|byte
index|[]
name|intBuffer
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
name|ByteBuffer
name|intByteBuffer
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|intBuffer
argument_list|)
decl_stmt|;
name|ByteArrayDataInput
name|in
init|=
operator|new
name|ByteArrayDataInput
argument_list|()
decl_stmt|;
name|long
name|pos
init|=
name|start
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|end
condition|)
block|{
name|intByteBuffer
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|intByteBuffer
operator|.
name|limit
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|readBytesFromChannel
argument_list|(
name|pos
argument_list|,
name|intByteBuffer
argument_list|)
expr_stmt|;
name|pos
operator|+=
literal|4
expr_stmt|;
name|int
name|len
init|=
operator|(
operator|(
name|intBuffer
index|[
literal|0
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
name|intBuffer
index|[
literal|1
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator||
operator|(
name|intBuffer
index|[
literal|2
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator||
operator|(
name|intBuffer
index|[
literal|3
index|]
operator|&
literal|0xff
operator|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|readBytesFromChannel
argument_list|(
name|pos
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|len
expr_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|byte
name|op
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
comment|//System.out.println("xlog: replay op=" + op);
switch|switch
condition|(
name|op
condition|)
block|{
case|case
literal|0
case|:
comment|// We replay add as update:
name|replayAddDocument
argument_list|(
name|c
argument_list|,
name|primary
argument_list|,
name|in
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
comment|// We replay add as update:
name|replayAddDocument
argument_list|(
name|c
argument_list|,
name|primary
argument_list|,
name|in
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|replayDeleteDocuments
argument_list|(
name|c
argument_list|,
name|primary
argument_list|,
name|in
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid operation "
operator|+
name|op
argument_list|,
name|in
argument_list|)
throw|;
block|}
block|}
assert|assert
name|pos
operator|==
name|end
assert|;
comment|//System.out.println("xlog: done replay");
name|c
operator|.
name|out
operator|.
name|writeByte
argument_list|(
name|SimplePrimaryNode
operator|.
name|CMD_INDEXING_DONE
argument_list|)
expr_stmt|;
name|c
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|//System.out.println("xlog: done flush");
name|c
operator|.
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
comment|//System.out.println("xlog: done readByte");
block|}
block|}
DECL|method|replayAddDocument
specifier|private
name|void
name|replayAddDocument
parameter_list|(
name|Connection
name|c
parameter_list|,
name|NodeProcess
name|primary
parameter_list|,
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|id
init|=
name|in
operator|.
name|readString
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
name|StringField
argument_list|(
literal|"docid"
argument_list|,
name|id
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|title
init|=
name|readNullableString
argument_list|(
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
name|title
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"title"
argument_list|,
name|title
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"titleTokenized"
argument_list|,
name|title
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|body
init|=
name|readNullableString
argument_list|(
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
name|body
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"body"
argument_list|,
name|body
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|marker
init|=
name|readNullableString
argument_list|(
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
name|marker
operator|!=
literal|null
condition|)
block|{
comment|//System.out.println("xlog: replay marker=" + id);
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"marker"
argument_list|,
name|marker
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// For both add and update originally, we use updateDocument to replay,
comment|// because the doc could in fact already be in the index:
comment|// nocomit what if this fails?
name|primary
operator|.
name|addOrUpdateDocument
argument_list|(
name|c
argument_list|,
name|doc
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|replayDeleteDocuments
specifier|private
name|void
name|replayDeleteDocuments
parameter_list|(
name|Connection
name|c
parameter_list|,
name|NodeProcess
name|primary
parameter_list|,
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|id
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
comment|// nocomit what if this fails?
name|primary
operator|.
name|deleteDocument
argument_list|(
name|c
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
comment|/** Encodes doc into buffer.  NOTE: this is NOT general purpose!  It only handles the fields used in this test! */
DECL|method|encode
specifier|private
specifier|synchronized
name|void
name|encode
parameter_list|(
name|String
name|id
parameter_list|,
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|id
operator|.
name|equals
argument_list|(
name|doc
operator|.
name|get
argument_list|(
literal|"docid"
argument_list|)
argument_list|)
operator|:
literal|"id="
operator|+
name|id
operator|+
literal|" vs docid="
operator|+
name|doc
operator|.
name|get
argument_list|(
literal|"docid"
argument_list|)
assert|;
name|buffer
operator|.
name|writeString
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|writeNullableString
argument_list|(
name|doc
operator|.
name|get
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
name|writeNullableString
argument_list|(
name|doc
operator|.
name|get
argument_list|(
literal|"body"
argument_list|)
argument_list|)
expr_stmt|;
name|writeNullableString
argument_list|(
name|doc
operator|.
name|get
argument_list|(
literal|"marker"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writeNullableString
specifier|private
specifier|synchronized
name|void
name|writeNullableString
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
name|buffer
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeString
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readNullableString
specifier|private
name|String
name|readNullableString
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|b
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|b
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|1
condition|)
block|{
return|return
name|in
operator|.
name|readString
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid string lead byte "
operator|+
name|b
argument_list|,
name|in
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
