begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Format
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
name|DateTools
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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_comment
comment|/**  * Create documents for the test.  * Maintains counters of chars etc. so that sub-classes just need to   * provide textual content, and the create-by-size is handled here.  *  *<p/>  * Config Params (default is in caps):  * doc.stored=true|FALSE<br/>  * doc.tokenized=TRUE|false<br/>  * doc.term.vector=true|FALSE<br/>  * doc.term.vector.positions=true|FALSE<br/>  * doc.term.vector.offsets=true|FALSE<br/>  * doc.store.body.bytes=true|FALSE //Store the body contents raw UTF-8 bytes as a field<br/>  */
end_comment
begin_class
DECL|class|BasicDocMaker
specifier|public
specifier|abstract
class|class
name|BasicDocMaker
implements|implements
name|DocMaker
block|{
DECL|field|numDocsCreated
specifier|private
name|int
name|numDocsCreated
init|=
literal|0
decl_stmt|;
DECL|field|storeBytes
specifier|private
name|boolean
name|storeBytes
init|=
literal|false
decl_stmt|;
DECL|field|forever
specifier|protected
name|boolean
name|forever
decl_stmt|;
DECL|class|LeftOver
specifier|private
specifier|static
class|class
name|LeftOver
block|{
DECL|field|docdata
specifier|private
name|DocData
name|docdata
decl_stmt|;
DECL|field|cnt
specifier|private
name|int
name|cnt
decl_stmt|;
block|}
comment|// leftovers are thread local, because it is unsafe to share residues between threads
DECL|field|leftovr
specifier|private
name|ThreadLocal
name|leftovr
init|=
operator|new
name|ThreadLocal
argument_list|()
decl_stmt|;
DECL|field|BODY_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|BODY_FIELD
init|=
literal|"body"
decl_stmt|;
DECL|field|TITLE_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|TITLE_FIELD
init|=
literal|"doctitle"
decl_stmt|;
DECL|field|DATE_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|DATE_FIELD
init|=
literal|"docdate"
decl_stmt|;
DECL|field|ID_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|ID_FIELD
init|=
literal|"docid"
decl_stmt|;
DECL|field|BYTES_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|BYTES_FIELD
init|=
literal|"bytes"
decl_stmt|;
DECL|field|NAME_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|NAME_FIELD
init|=
literal|"docname"
decl_stmt|;
DECL|field|numBytes
specifier|private
name|long
name|numBytes
init|=
literal|0
decl_stmt|;
DECL|field|numUniqueBytes
specifier|private
name|long
name|numUniqueBytes
init|=
literal|0
decl_stmt|;
DECL|field|config
specifier|protected
name|Config
name|config
decl_stmt|;
DECL|field|storeVal
specifier|protected
name|Field
operator|.
name|Store
name|storeVal
init|=
name|Field
operator|.
name|Store
operator|.
name|NO
decl_stmt|;
DECL|field|indexVal
specifier|protected
name|Field
operator|.
name|Index
name|indexVal
init|=
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
decl_stmt|;
DECL|field|termVecVal
specifier|protected
name|Field
operator|.
name|TermVector
name|termVecVal
init|=
name|Field
operator|.
name|TermVector
operator|.
name|NO
decl_stmt|;
DECL|method|incrNumDocsCreated
specifier|private
specifier|synchronized
name|int
name|incrNumDocsCreated
parameter_list|()
block|{
return|return
name|numDocsCreated
operator|++
return|;
block|}
comment|/**    * Return the data of the next document.    * All current implementations can create docs forever.     * When the input data is exhausted, input files are iterated.    * This re-iteration can be avoided by setting doc.maker.forever to false (default is true).    * @return data of the next document.    * @exception if cannot create the next doc data    * @exception NoMoreDataException if data is exhausted (and 'forever' set to false).    */
DECL|method|getNextDocData
specifier|protected
specifier|abstract
name|DocData
name|getNextDocData
parameter_list|()
throws|throws
name|NoMoreDataException
throws|,
name|Exception
function_decl|;
comment|/*    *  (non-Javadoc)    * @see org.apache.lucene.benchmark.byTask.feeds.DocMaker#makeDocument()    */
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|resetLeftovers
argument_list|()
expr_stmt|;
name|DocData
name|docData
init|=
name|getNextDocData
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|createDocument
argument_list|(
name|docData
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
return|return
name|doc
return|;
block|}
comment|// create a doc
comment|// use only part of the body, modify it to keep the rest (or use all if size==0).
comment|// reset the docdata properties so they are not added more than once.
DECL|method|createDocument
specifier|private
name|Document
name|createDocument
parameter_list|(
name|DocData
name|docData
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|cnt
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
name|int
name|docid
init|=
name|incrNumDocsCreated
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
name|ID_FIELD
argument_list|,
literal|"doc"
operator|+
name|docid
argument_list|,
name|storeVal
argument_list|,
name|indexVal
argument_list|,
name|termVecVal
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|docData
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|name
init|=
operator|(
name|cnt
operator|<
literal|0
condition|?
name|docData
operator|.
name|getName
argument_list|()
else|:
name|docData
operator|.
name|getName
argument_list|()
operator|+
literal|"_"
operator|+
name|cnt
operator|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|NAME_FIELD
argument_list|,
name|name
argument_list|,
name|storeVal
argument_list|,
name|indexVal
argument_list|,
name|termVecVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|docData
operator|.
name|getDate
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|dateStr
init|=
name|DateTools
operator|.
name|dateToString
argument_list|(
name|docData
operator|.
name|getDate
argument_list|()
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|SECOND
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|DATE_FIELD
argument_list|,
name|dateStr
argument_list|,
name|storeVal
argument_list|,
name|indexVal
argument_list|,
name|termVecVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|docData
operator|.
name|getTitle
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|TITLE_FIELD
argument_list|,
name|docData
operator|.
name|getTitle
argument_list|()
argument_list|,
name|storeVal
argument_list|,
name|indexVal
argument_list|,
name|termVecVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|docData
operator|.
name|getBody
argument_list|()
operator|!=
literal|null
operator|&&
name|docData
operator|.
name|getBody
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|bdy
decl_stmt|;
if|if
condition|(
name|size
operator|<=
literal|0
operator|||
name|size
operator|>=
name|docData
operator|.
name|getBody
argument_list|()
operator|.
name|length
argument_list|()
condition|)
block|{
name|bdy
operator|=
name|docData
operator|.
name|getBody
argument_list|()
expr_stmt|;
comment|// use all
name|docData
operator|.
name|setBody
argument_list|(
literal|""
argument_list|)
expr_stmt|;
comment|// nothing left
block|}
else|else
block|{
comment|// attempt not to break words - if whitespace found within next 20 chars...
for|for
control|(
name|int
name|n
init|=
name|size
operator|-
literal|1
init|;
name|n
operator|<
name|size
operator|+
literal|20
operator|&&
name|n
operator|<
name|docData
operator|.
name|getBody
argument_list|()
operator|.
name|length
argument_list|()
condition|;
name|n
operator|++
control|)
block|{
if|if
condition|(
name|Character
operator|.
name|isWhitespace
argument_list|(
name|docData
operator|.
name|getBody
argument_list|()
operator|.
name|charAt
argument_list|(
name|n
argument_list|)
argument_list|)
condition|)
block|{
name|size
operator|=
name|n
expr_stmt|;
break|break;
block|}
block|}
name|bdy
operator|=
name|docData
operator|.
name|getBody
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
comment|// use part
name|docData
operator|.
name|setBody
argument_list|(
name|docData
operator|.
name|getBody
argument_list|()
operator|.
name|substring
argument_list|(
name|size
argument_list|)
argument_list|)
expr_stmt|;
comment|// some left
block|}
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|BODY_FIELD
argument_list|,
name|bdy
argument_list|,
name|storeVal
argument_list|,
name|indexVal
argument_list|,
name|termVecVal
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|storeBytes
operator|==
literal|true
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|BYTES_FIELD
argument_list|,
name|bdy
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
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
block|}
if|if
condition|(
name|docData
operator|.
name|getProps
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
name|it
init|=
name|docData
operator|.
name|getProps
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|val
init|=
operator|(
name|String
operator|)
name|docData
operator|.
name|getProps
argument_list|()
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|key
argument_list|,
name|val
argument_list|,
name|storeVal
argument_list|,
name|indexVal
argument_list|,
name|termVecVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|docData
operator|.
name|setProps
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("============== Created doc "+numDocsCreated+" :\n"+doc+"\n==========");
return|return
name|doc
return|;
block|}
comment|/*    *  (non-Javadoc)    * @see org.apache.lucene.benchmark.byTask.feeds.DocMaker#makeDocument(int)    */
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|Exception
block|{
name|LeftOver
name|lvr
init|=
operator|(
name|LeftOver
operator|)
name|leftovr
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|lvr
operator|==
literal|null
operator|||
name|lvr
operator|.
name|docdata
operator|==
literal|null
operator|||
name|lvr
operator|.
name|docdata
operator|.
name|getBody
argument_list|()
operator|==
literal|null
operator|||
name|lvr
operator|.
name|docdata
operator|.
name|getBody
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|resetLeftovers
argument_list|()
expr_stmt|;
block|}
name|DocData
name|dd
init|=
operator|(
name|lvr
operator|==
literal|null
condition|?
name|getNextDocData
argument_list|()
else|:
name|lvr
operator|.
name|docdata
operator|)
decl_stmt|;
name|int
name|cnt
init|=
operator|(
name|lvr
operator|==
literal|null
condition|?
literal|0
else|:
name|lvr
operator|.
name|cnt
operator|)
decl_stmt|;
while|while
condition|(
name|dd
operator|.
name|getBody
argument_list|()
operator|==
literal|null
operator|||
name|dd
operator|.
name|getBody
argument_list|()
operator|.
name|length
argument_list|()
operator|<
name|size
condition|)
block|{
name|DocData
name|dd2
init|=
name|dd
decl_stmt|;
name|dd
operator|=
name|getNextDocData
argument_list|()
expr_stmt|;
name|cnt
operator|=
literal|0
expr_stmt|;
name|dd
operator|.
name|setBody
argument_list|(
name|dd2
operator|.
name|getBody
argument_list|()
operator|+
name|dd
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Document
name|doc
init|=
name|createDocument
argument_list|(
name|dd
argument_list|,
name|size
argument_list|,
name|cnt
argument_list|)
decl_stmt|;
if|if
condition|(
name|dd
operator|.
name|getBody
argument_list|()
operator|==
literal|null
operator|||
name|dd
operator|.
name|getBody
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|resetLeftovers
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|lvr
operator|==
literal|null
condition|)
block|{
name|lvr
operator|=
operator|new
name|LeftOver
argument_list|()
expr_stmt|;
name|leftovr
operator|.
name|set
argument_list|(
name|lvr
argument_list|)
expr_stmt|;
block|}
name|lvr
operator|.
name|docdata
operator|=
name|dd
expr_stmt|;
name|lvr
operator|.
name|cnt
operator|=
operator|++
name|cnt
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
DECL|method|resetLeftovers
specifier|private
name|void
name|resetLeftovers
parameter_list|()
block|{
name|leftovr
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see DocMaker#setConfig(java.util.Properties)    */
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|boolean
name|stored
init|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.stored"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|tokenized
init|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.tokenized"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|boolean
name|termVec
init|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.term.vector"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|storeVal
operator|=
operator|(
name|stored
condition|?
name|Field
operator|.
name|Store
operator|.
name|YES
else|:
name|Field
operator|.
name|Store
operator|.
name|NO
operator|)
expr_stmt|;
name|indexVal
operator|=
operator|(
name|tokenized
condition|?
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
else|:
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
operator|)
expr_stmt|;
name|boolean
name|termVecPositions
init|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.term.vector.positions"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|termVecOffsets
init|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.term.vector.offsets"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|termVecPositions
operator|&&
name|termVecOffsets
condition|)
name|termVecVal
operator|=
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
expr_stmt|;
elseif|else
if|if
condition|(
name|termVecPositions
condition|)
name|termVecVal
operator|=
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS
expr_stmt|;
elseif|else
if|if
condition|(
name|termVecOffsets
condition|)
name|termVecVal
operator|=
name|Field
operator|.
name|TermVector
operator|.
name|WITH_OFFSETS
expr_stmt|;
elseif|else
if|if
condition|(
name|termVec
condition|)
name|termVecVal
operator|=
name|Field
operator|.
name|TermVector
operator|.
name|YES
expr_stmt|;
else|else
name|termVecVal
operator|=
name|Field
operator|.
name|TermVector
operator|.
name|NO
expr_stmt|;
name|storeBytes
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.store.body.bytes"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|forever
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.maker.forever"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/*    *  (non-Javadoc)    * @see DocMaker#resetIinputs()    */
DECL|method|resetInputs
specifier|public
specifier|synchronized
name|void
name|resetInputs
parameter_list|()
block|{
name|printDocStatistics
argument_list|()
expr_stmt|;
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|//re-initiate since properties by round may have changed.
name|numBytes
operator|=
literal|0
expr_stmt|;
name|numDocsCreated
operator|=
literal|0
expr_stmt|;
name|resetLeftovers
argument_list|()
expr_stmt|;
block|}
comment|/*    *  (non-Javadoc)    * @see org.apache.lucene.benchmark.byTask.feeds.DocMaker#numUniqueBytes()    */
DECL|method|numUniqueBytes
specifier|public
name|long
name|numUniqueBytes
parameter_list|()
block|{
return|return
name|numUniqueBytes
return|;
block|}
comment|/*    *  (non-Javadoc)    * @see DocMaker#getCount()    */
DECL|method|getCount
specifier|public
specifier|synchronized
name|int
name|getCount
parameter_list|()
block|{
return|return
name|numDocsCreated
return|;
block|}
comment|/*    *  (non-Javadoc)    * @see DocMaker#getByteCount()    */
DECL|method|getByteCount
specifier|public
specifier|synchronized
name|long
name|getByteCount
parameter_list|()
block|{
return|return
name|numBytes
return|;
block|}
DECL|method|addUniqueBytes
specifier|protected
name|void
name|addUniqueBytes
parameter_list|(
name|long
name|n
parameter_list|)
block|{
name|numUniqueBytes
operator|+=
name|n
expr_stmt|;
block|}
DECL|method|resetUniqueBytes
specifier|protected
name|void
name|resetUniqueBytes
parameter_list|()
block|{
name|numUniqueBytes
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|addBytes
specifier|protected
specifier|synchronized
name|void
name|addBytes
parameter_list|(
name|long
name|n
parameter_list|)
block|{
name|numBytes
operator|+=
name|n
expr_stmt|;
block|}
comment|/*    *  (non-Javadoc)    * @see org.apache.lucene.benchmark.byTask.feeds.DocMaker#printDocStatistics()    */
DECL|field|lastPrintedNumUniqueTexts
specifier|private
name|int
name|lastPrintedNumUniqueTexts
init|=
literal|0
decl_stmt|;
DECL|field|lastPrintedNumUniqueBytes
specifier|private
name|long
name|lastPrintedNumUniqueBytes
init|=
literal|0
decl_stmt|;
DECL|field|printNum
specifier|private
name|int
name|printNum
init|=
literal|0
decl_stmt|;
DECL|field|htmlParser
specifier|private
name|HTMLParser
name|htmlParser
decl_stmt|;
DECL|method|printDocStatistics
specifier|public
name|void
name|printDocStatistics
parameter_list|()
block|{
name|boolean
name|print
init|=
literal|false
decl_stmt|;
name|String
name|col
init|=
literal|"                  "
decl_stmt|;
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|String
name|newline
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"------------> "
argument_list|)
operator|.
name|append
argument_list|(
name|Format
operator|.
name|simpleName
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|" statistics ("
argument_list|)
operator|.
name|append
argument_list|(
name|printNum
argument_list|)
operator|.
name|append
argument_list|(
literal|"): "
argument_list|)
operator|.
name|append
argument_list|(
name|newline
argument_list|)
expr_stmt|;
name|int
name|nut
init|=
name|numUniqueTexts
argument_list|()
decl_stmt|;
if|if
condition|(
name|nut
operator|>
name|lastPrintedNumUniqueTexts
condition|)
block|{
name|print
operator|=
literal|true
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"total count of unique texts: "
argument_list|)
operator|.
name|append
argument_list|(
name|Format
operator|.
name|format
argument_list|(
literal|0
argument_list|,
name|nut
argument_list|,
name|col
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|newline
argument_list|)
expr_stmt|;
name|lastPrintedNumUniqueTexts
operator|=
name|nut
expr_stmt|;
block|}
name|long
name|nub
init|=
name|numUniqueBytes
argument_list|()
decl_stmt|;
if|if
condition|(
name|nub
operator|>
name|lastPrintedNumUniqueBytes
condition|)
block|{
name|print
operator|=
literal|true
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"total bytes of unique texts: "
argument_list|)
operator|.
name|append
argument_list|(
name|Format
operator|.
name|format
argument_list|(
literal|0
argument_list|,
name|nub
argument_list|,
name|col
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|newline
argument_list|)
expr_stmt|;
name|lastPrintedNumUniqueBytes
operator|=
name|nub
expr_stmt|;
block|}
if|if
condition|(
name|getCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|print
operator|=
literal|true
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"num docs added since last inputs reset:   "
argument_list|)
operator|.
name|append
argument_list|(
name|Format
operator|.
name|format
argument_list|(
literal|0
argument_list|,
name|getCount
argument_list|()
argument_list|,
name|col
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|newline
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"total bytes added since last inputs reset: "
argument_list|)
operator|.
name|append
argument_list|(
name|Format
operator|.
name|format
argument_list|(
literal|0
argument_list|,
name|getByteCount
argument_list|()
argument_list|,
name|col
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|newline
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|print
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|sb
operator|.
name|append
argument_list|(
name|newline
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|printNum
operator|++
expr_stmt|;
block|}
block|}
DECL|method|collectFiles
specifier|protected
name|void
name|collectFiles
parameter_list|(
name|File
name|f
parameter_list|,
name|ArrayList
name|inputFiles
parameter_list|)
block|{
comment|//System.out.println("Collect: "+f.getAbsolutePath());
if|if
condition|(
operator|!
name|f
operator|.
name|canRead
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|String
name|files
index|[]
init|=
name|f
operator|.
name|list
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|files
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|collectFiles
argument_list|(
operator|new
name|File
argument_list|(
name|f
argument_list|,
name|files
index|[
name|i
index|]
argument_list|)
argument_list|,
name|inputFiles
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|inputFiles
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|addUniqueBytes
argument_list|(
name|f
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.benchmark.byTask.feeds.DocMaker#setHTMLParser(org.apache.lucene.benchmark.byTask.feeds.HTMLParser)    */
DECL|method|setHTMLParser
specifier|public
name|void
name|setHTMLParser
parameter_list|(
name|HTMLParser
name|htmlParser
parameter_list|)
block|{
name|this
operator|.
name|htmlParser
operator|=
name|htmlParser
expr_stmt|;
block|}
comment|/*    *  (non-Javadoc)    * @see org.apache.lucene.benchmark.byTask.feeds.DocMaker#getHtmlParser()    */
DECL|method|getHtmlParser
specifier|public
name|HTMLParser
name|getHtmlParser
parameter_list|()
block|{
return|return
name|htmlParser
return|;
block|}
block|}
end_class
end_unit
