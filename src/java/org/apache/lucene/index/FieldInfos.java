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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|OutputStream
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
name|IndexInput
import|;
end_import
begin_comment
comment|/** Access to the Field Info file that describes document fields and whether or  *  not they are indexed. Each segment has a separate Field Info file. Objects  *  of this class are thread-safe for multiple readers, but only one thread can  *  be adding documents at a time, with no other reader or writer threads  *  accessing this object.  */
end_comment
begin_class
DECL|class|FieldInfos
specifier|final
class|class
name|FieldInfos
block|{
DECL|field|byNumber
specifier|private
name|ArrayList
name|byNumber
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|byName
specifier|private
name|HashMap
name|byName
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|method|FieldInfos
name|FieldInfos
parameter_list|()
block|{
name|add
argument_list|(
literal|""
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a FieldInfos object using the directory and the name of the file    * IndexInput    * @param d The directory to open the IndexInput from    * @param name The name of the file to open the IndexInput from in the Directory    * @throws IOException    */
DECL|method|FieldInfos
name|FieldInfos
parameter_list|(
name|Directory
name|d
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|input
init|=
name|d
operator|.
name|openInput
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|read
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Adds field info for a Document. */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|Enumeration
name|fields
init|=
name|doc
operator|.
name|fields
argument_list|()
decl_stmt|;
while|while
condition|(
name|fields
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Field
name|field
init|=
operator|(
name|Field
operator|)
name|fields
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|add
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|field
operator|.
name|isIndexed
argument_list|()
argument_list|,
name|field
operator|.
name|isTermVectorStored
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @param names The names of the fields    * @param storeTermVectors Whether the fields store term vectors or not    */
DECL|method|addIndexed
specifier|public
name|void
name|addIndexed
parameter_list|(
name|Collection
name|names
parameter_list|,
name|boolean
name|storeTermVectors
parameter_list|)
block|{
name|Iterator
name|i
init|=
name|names
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|add
argument_list|(
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
argument_list|,
literal|true
argument_list|,
name|storeTermVectors
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Assumes the field is not storing term vectors     * @param names The names of the fields    * @param isIndexed Whether the fields are indexed or not    *     * @see #add(String, boolean)    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Collection
name|names
parameter_list|,
name|boolean
name|isIndexed
parameter_list|)
block|{
name|Iterator
name|i
init|=
name|names
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|add
argument_list|(
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
argument_list|,
name|isIndexed
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Calls three parameter add with false for the storeTermVector parameter     * @param name The name of the Field    * @param isIndexed true if the field is indexed    * @see #add(String, boolean, boolean)    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isIndexed
parameter_list|)
block|{
name|add
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** If the field is not yet known, adds it. If it is known, checks to make    *  sure that the isIndexed flag is the same as was given previously for this    *  field. If not - marks it as being indexed.  Same goes for storeTermVector    *     * @param name The name of the field    * @param isIndexed true if the field is indexed    * @param storeTermVector true if the term vector should be stored    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isIndexed
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfo
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
condition|)
block|{
name|addInternal
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|storeTermVector
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|fi
operator|.
name|isIndexed
operator|!=
name|isIndexed
condition|)
block|{
name|fi
operator|.
name|isIndexed
operator|=
literal|true
expr_stmt|;
comment|// once indexed, always index
block|}
if|if
condition|(
name|fi
operator|.
name|storeTermVector
operator|!=
name|storeTermVector
condition|)
block|{
name|fi
operator|.
name|storeTermVector
operator|=
literal|true
expr_stmt|;
comment|// once vector, always vector
block|}
block|}
block|}
DECL|method|addInternal
specifier|private
name|void
name|addInternal
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isIndexed
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|)
block|{
name|FieldInfo
name|fi
init|=
operator|new
name|FieldInfo
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|byNumber
operator|.
name|size
argument_list|()
argument_list|,
name|storeTermVector
argument_list|)
decl_stmt|;
name|byNumber
operator|.
name|add
argument_list|(
name|fi
argument_list|)
expr_stmt|;
name|byName
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|fi
argument_list|)
expr_stmt|;
block|}
DECL|method|fieldNumber
specifier|public
name|int
name|fieldNumber
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfo
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|!=
literal|null
condition|)
return|return
name|fi
operator|.
name|number
return|;
else|else
return|return
operator|-
literal|1
return|;
block|}
DECL|method|fieldInfo
specifier|public
name|FieldInfo
name|fieldInfo
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|(
name|FieldInfo
operator|)
name|byName
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
DECL|method|fieldName
specifier|public
name|String
name|fieldName
parameter_list|(
name|int
name|fieldNumber
parameter_list|)
block|{
return|return
name|fieldInfo
argument_list|(
name|fieldNumber
argument_list|)
operator|.
name|name
return|;
block|}
DECL|method|fieldInfo
specifier|public
name|FieldInfo
name|fieldInfo
parameter_list|(
name|int
name|fieldNumber
parameter_list|)
block|{
return|return
operator|(
name|FieldInfo
operator|)
name|byNumber
operator|.
name|get
argument_list|(
name|fieldNumber
argument_list|)
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|byNumber
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|hasVectors
specifier|public
name|boolean
name|hasVectors
parameter_list|()
block|{
name|boolean
name|hasVectors
init|=
literal|false
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
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|fieldInfo
argument_list|(
name|i
argument_list|)
operator|.
name|storeTermVector
condition|)
block|{
name|hasVectors
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
return|return
name|hasVectors
return|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Directory
name|d
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStream
name|output
init|=
name|d
operator|.
name|createFile
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|write
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|OutputStream
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|output
operator|.
name|writeVInt
argument_list|(
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfo
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|byte
name|bits
init|=
literal|0x0
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|isIndexed
condition|)
name|bits
operator||=
literal|0x1
expr_stmt|;
if|if
condition|(
name|fi
operator|.
name|storeTermVector
condition|)
name|bits
operator||=
literal|0x2
expr_stmt|;
name|output
operator|.
name|writeString
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
comment|//Was REMOVE
comment|//output.writeByte((byte)(fi.isIndexed ? 1 : 0));
name|output
operator|.
name|writeByte
argument_list|(
name|bits
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|read
specifier|private
name|void
name|read
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|size
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|//read in the size
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
name|String
name|name
init|=
name|input
operator|.
name|readString
argument_list|()
operator|.
name|intern
argument_list|()
decl_stmt|;
name|byte
name|bits
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|boolean
name|isIndexed
init|=
operator|(
name|bits
operator|&
literal|0x1
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|storeTermVector
init|=
operator|(
name|bits
operator|&
literal|0x2
operator|)
operator|!=
literal|0
decl_stmt|;
name|addInternal
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|storeTermVector
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
