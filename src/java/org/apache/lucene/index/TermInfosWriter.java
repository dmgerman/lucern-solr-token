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
name|store
operator|.
name|IndexOutput
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
name|StringHelper
import|;
end_import
begin_comment
comment|/** This stores a monotonically increasing set of<Term, TermInfo> pairs in a   Directory.  A TermInfos can be written once, in order.  */
end_comment
begin_class
DECL|class|TermInfosWriter
specifier|final
class|class
name|TermInfosWriter
block|{
comment|/** The file format version, a negative number. */
DECL|field|FORMAT
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT
init|=
operator|-
literal|2
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|output
specifier|private
name|IndexOutput
name|output
decl_stmt|;
DECL|field|lastTerm
specifier|private
name|Term
name|lastTerm
init|=
operator|new
name|Term
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
DECL|field|lastTi
specifier|private
name|TermInfo
name|lastTi
init|=
operator|new
name|TermInfo
argument_list|()
decl_stmt|;
DECL|field|size
specifier|private
name|long
name|size
init|=
literal|0
decl_stmt|;
comment|// TODO: the default values for these two parameters should be settable from
comment|// IndexWriter.  However, once that's done, folks will start setting them to
comment|// ridiculous values and complaining that things don't work well, as with
comment|// mergeFactor.  So, let's wait until a number of folks find that alternate
comment|// values work better.  Note that both of these values are stored in the
comment|// segment, so that it's safe to change these w/o rebuilding all indexes.
comment|/** Expert: The fraction of terms in the "dictionary" which should be stored    * in RAM.  Smaller values use more memory, but make searching slightly    * faster, while larger values use less memory and make searching slightly    * slower.  Searching is typically not dominated by dictionary lookup, so    * tweaking this is rarely useful.*/
DECL|field|indexInterval
name|int
name|indexInterval
init|=
literal|128
decl_stmt|;
comment|/** Expert: The fraction of {@link TermDocs} entries stored in skip tables,    * used to accellerate {@link TermDocs#skipTo(int)}.  Larger values result in    * smaller indexes, greater acceleration, but fewer accelerable cases, while    * smaller values result in bigger indexes, less acceleration and more    * accelerable cases. More detailed experiments would be useful here. */
DECL|field|skipInterval
name|int
name|skipInterval
init|=
literal|16
decl_stmt|;
DECL|field|lastIndexPointer
specifier|private
name|long
name|lastIndexPointer
init|=
literal|0
decl_stmt|;
DECL|field|isIndex
specifier|private
name|boolean
name|isIndex
init|=
literal|false
decl_stmt|;
DECL|field|other
specifier|private
name|TermInfosWriter
name|other
init|=
literal|null
decl_stmt|;
DECL|method|TermInfosWriter
name|TermInfosWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fis
parameter_list|,
name|int
name|interval
parameter_list|)
throws|throws
name|IOException
block|{
name|initialize
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|fis
argument_list|,
name|interval
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|other
operator|=
operator|new
name|TermInfosWriter
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|fis
argument_list|,
name|interval
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|other
operator|.
name|other
operator|=
name|this
expr_stmt|;
block|}
DECL|method|TermInfosWriter
specifier|private
name|TermInfosWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fis
parameter_list|,
name|int
name|interval
parameter_list|,
name|boolean
name|isIndex
parameter_list|)
throws|throws
name|IOException
block|{
name|initialize
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|fis
argument_list|,
name|interval
argument_list|,
name|isIndex
argument_list|)
expr_stmt|;
block|}
DECL|method|initialize
specifier|private
name|void
name|initialize
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fis
parameter_list|,
name|int
name|interval
parameter_list|,
name|boolean
name|isi
parameter_list|)
throws|throws
name|IOException
block|{
name|indexInterval
operator|=
name|interval
expr_stmt|;
name|fieldInfos
operator|=
name|fis
expr_stmt|;
name|isIndex
operator|=
name|isi
expr_stmt|;
name|output
operator|=
name|directory
operator|.
name|createOutput
argument_list|(
name|segment
operator|+
operator|(
name|isIndex
condition|?
literal|".tii"
else|:
literal|".tis"
operator|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|FORMAT
argument_list|)
expr_stmt|;
comment|// write format
name|output
operator|.
name|writeLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// leave space for size
name|output
operator|.
name|writeInt
argument_list|(
name|indexInterval
argument_list|)
expr_stmt|;
comment|// write indexInterval
name|output
operator|.
name|writeInt
argument_list|(
name|skipInterval
argument_list|)
expr_stmt|;
comment|// write skipInterval
block|}
comment|/** Adds a new<Term, TermInfo> pair to the set.     Term must be lexicographically greater than all previous Terms added.     TermInfo pointers must be positive and greater than all previous.*/
DECL|method|add
specifier|final
name|void
name|add
parameter_list|(
name|Term
name|term
parameter_list|,
name|TermInfo
name|ti
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isIndex
operator|&&
name|term
operator|.
name|compareTo
argument_list|(
name|lastTerm
argument_list|)
operator|<=
literal|0
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"term out of order"
argument_list|)
throw|;
if|if
condition|(
name|ti
operator|.
name|freqPointer
operator|<
name|lastTi
operator|.
name|freqPointer
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"freqPointer out of order"
argument_list|)
throw|;
if|if
condition|(
name|ti
operator|.
name|proxPointer
operator|<
name|lastTi
operator|.
name|proxPointer
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"proxPointer out of order"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|isIndex
operator|&&
name|size
operator|%
name|indexInterval
operator|==
literal|0
condition|)
name|other
operator|.
name|add
argument_list|(
name|lastTerm
argument_list|,
name|lastTi
argument_list|)
expr_stmt|;
comment|// add an index term
name|writeTerm
argument_list|(
name|term
argument_list|)
expr_stmt|;
comment|// write term
name|output
operator|.
name|writeVInt
argument_list|(
name|ti
operator|.
name|docFreq
argument_list|)
expr_stmt|;
comment|// write doc freq
name|output
operator|.
name|writeVLong
argument_list|(
name|ti
operator|.
name|freqPointer
operator|-
name|lastTi
operator|.
name|freqPointer
argument_list|)
expr_stmt|;
comment|// write pointers
name|output
operator|.
name|writeVLong
argument_list|(
name|ti
operator|.
name|proxPointer
operator|-
name|lastTi
operator|.
name|proxPointer
argument_list|)
expr_stmt|;
if|if
condition|(
name|ti
operator|.
name|docFreq
operator|>=
name|skipInterval
condition|)
block|{
name|output
operator|.
name|writeVInt
argument_list|(
name|ti
operator|.
name|skipOffset
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isIndex
condition|)
block|{
name|output
operator|.
name|writeVLong
argument_list|(
name|other
operator|.
name|output
operator|.
name|getFilePointer
argument_list|()
operator|-
name|lastIndexPointer
argument_list|)
expr_stmt|;
name|lastIndexPointer
operator|=
name|other
operator|.
name|output
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
comment|// write pointer
block|}
name|lastTi
operator|.
name|set
argument_list|(
name|ti
argument_list|)
expr_stmt|;
name|size
operator|++
expr_stmt|;
block|}
DECL|method|writeTerm
specifier|private
specifier|final
name|void
name|writeTerm
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|start
init|=
name|StringHelper
operator|.
name|stringDifference
argument_list|(
name|lastTerm
operator|.
name|text
argument_list|,
name|term
operator|.
name|text
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|term
operator|.
name|text
operator|.
name|length
argument_list|()
operator|-
name|start
decl_stmt|;
name|output
operator|.
name|writeVInt
argument_list|(
name|start
argument_list|)
expr_stmt|;
comment|// write shared prefix length
name|output
operator|.
name|writeVInt
argument_list|(
name|length
argument_list|)
expr_stmt|;
comment|// write delta length
name|output
operator|.
name|writeChars
argument_list|(
name|term
operator|.
name|text
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
comment|// write delta chars
name|output
operator|.
name|writeVInt
argument_list|(
name|fieldInfos
operator|.
name|fieldNumber
argument_list|(
name|term
operator|.
name|field
argument_list|)
argument_list|)
expr_stmt|;
comment|// write field num
name|lastTerm
operator|=
name|term
expr_stmt|;
block|}
comment|/** Called to complete TermInfos creation. */
DECL|method|close
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|output
operator|.
name|seek
argument_list|(
literal|4
argument_list|)
expr_stmt|;
comment|// write size after format
name|output
operator|.
name|writeLong
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isIndex
condition|)
name|other
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
