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
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|ValuesField
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
operator|.
name|Index
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
operator|.
name|Store
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
operator|.
name|TermVector
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
name|values
operator|.
name|Values
import|;
end_import
begin_comment
comment|/**  * Creates {@link Document} objects. Uses a {@link ContentSource} to generate  * {@link DocData} objects. Supports the following parameters:  *<ul>  *<li><b>content.source</b> - specifies the {@link ContentSource} class to use  * (default<b>SingleDocSource</b>).  *<li><b>doc.stored</b> - specifies whether fields should be stored (default  *<b>false</b>).  *<li><b>doc.body.stored</b> - specifies whether the body field should be stored (default  * =<b>doc.stored</b>).  *<li><b>doc.tokenized</b> - specifies whether fields should be tokenized  * (default<b>true</b>).  *<li><b>doc.body.tokenized</b> - specifies whether the  * body field should be tokenized (default =<b>doc.tokenized</b>).  *<li><b>doc.tokenized.norms</b> - specifies whether norms should be stored in  * the index or not. (default<b>false</b>).  *<li><b>doc.body.tokenized.norms</b> - specifies whether norms should be  * stored in the index for the body field. This can be set to true, while  *<code>doc.tokenized.norms</code> is set to false, to allow norms storing just  * for the body field. (default<b>true</b>).  *<li><b>doc.term.vector</b> - specifies whether term vectors should be stored  * for fields (default<b>false</b>).  *<li><b>doc.term.vector.positions</b> - specifies whether term vectors should  * be stored with positions (default<b>false</b>).  *<li><b>doc.term.vector.offsets</b> - specifies whether term vectors should be  * stored with offsets (default<b>false</b>).  *<li><b>doc.store.body.bytes</b> - specifies whether to store the raw bytes of  * the document's content in the document (default<b>false</b>).  *<li><b>doc.reuse.fields</b> - specifies whether Field and Document objects  * should be reused (default<b>true</b>).  *<li><b>doc.index.props</b> - specifies whether the properties returned by  *<li><b>doc.random.id.limit</b> - if specified, docs will be assigned random  * IDs from 0 to this limit.  This is useful with UpdateDoc  * for testing performance of IndexWriter.updateDocument.  * {@link DocData#getProps()} will be indexed. (default<b>false</b>).  *</ul>  */
end_comment
begin_class
DECL|class|DocMaker
specifier|public
class|class
name|DocMaker
block|{
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
DECL|field|r
specifier|private
name|Random
name|r
decl_stmt|;
DECL|field|updateDocIDLimit
specifier|private
name|int
name|updateDocIDLimit
decl_stmt|;
DECL|class|DocState
specifier|static
class|class
name|DocState
block|{
DECL|field|fields
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Field
argument_list|>
name|fields
decl_stmt|;
DECL|field|reuseFields
specifier|private
specifier|final
name|boolean
name|reuseFields
decl_stmt|;
DECL|field|doc
specifier|final
name|Document
name|doc
decl_stmt|;
DECL|field|docData
name|DocData
name|docData
init|=
operator|new
name|DocData
argument_list|()
decl_stmt|;
DECL|method|DocState
specifier|public
name|DocState
parameter_list|(
name|boolean
name|reuseFields
parameter_list|,
name|Store
name|store
parameter_list|,
name|Store
name|bodyStore
parameter_list|,
name|Index
name|index
parameter_list|,
name|Index
name|bodyIndex
parameter_list|,
name|TermVector
name|termVector
parameter_list|)
block|{
name|this
operator|.
name|reuseFields
operator|=
name|reuseFields
expr_stmt|;
if|if
condition|(
name|reuseFields
condition|)
block|{
name|fields
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Field
argument_list|>
argument_list|()
expr_stmt|;
comment|// Initialize the map with the default fields.
name|fields
operator|.
name|put
argument_list|(
name|BODY_FIELD
argument_list|,
operator|new
name|Field
argument_list|(
name|BODY_FIELD
argument_list|,
literal|""
argument_list|,
name|bodyStore
argument_list|,
name|bodyIndex
argument_list|,
name|termVector
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|TITLE_FIELD
argument_list|,
operator|new
name|Field
argument_list|(
name|TITLE_FIELD
argument_list|,
literal|""
argument_list|,
name|store
argument_list|,
name|index
argument_list|,
name|termVector
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|DATE_FIELD
argument_list|,
operator|new
name|Field
argument_list|(
name|DATE_FIELD
argument_list|,
literal|""
argument_list|,
name|store
argument_list|,
name|index
argument_list|,
name|termVector
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|ID_FIELD
argument_list|,
operator|new
name|Field
argument_list|(
name|ID_FIELD
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED_NO_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|NAME_FIELD
argument_list|,
operator|new
name|Field
argument_list|(
name|NAME_FIELD
argument_list|,
literal|""
argument_list|,
name|store
argument_list|,
name|index
argument_list|,
name|termVector
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|fields
operator|=
literal|null
expr_stmt|;
name|doc
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**      * Returns a field corresponding to the field name. If      *<code>reuseFields</code> was set to true, then it attempts to reuse a      * Field instance. If such a field does not exist, it creates a new one.      */
DECL|method|getField
name|Field
name|getField
parameter_list|(
name|String
name|name
parameter_list|,
name|Store
name|store
parameter_list|,
name|Index
name|index
parameter_list|,
name|TermVector
name|termVector
parameter_list|)
block|{
if|if
condition|(
operator|!
name|reuseFields
condition|)
block|{
return|return
operator|new
name|Field
argument_list|(
name|name
argument_list|,
literal|""
argument_list|,
name|store
argument_list|,
name|index
argument_list|,
name|termVector
argument_list|)
return|;
block|}
name|Field
name|f
init|=
name|fields
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
block|{
name|f
operator|=
operator|new
name|Field
argument_list|(
name|name
argument_list|,
literal|""
argument_list|,
name|store
argument_list|,
name|index
argument_list|,
name|termVector
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
return|return
name|f
return|;
block|}
block|}
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
comment|// leftovers are thread local, because it is unsafe to share residues between threads
DECL|field|leftovr
specifier|private
name|ThreadLocal
argument_list|<
name|LeftOver
argument_list|>
name|leftovr
init|=
operator|new
name|ThreadLocal
argument_list|<
name|LeftOver
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|docState
specifier|private
name|ThreadLocal
argument_list|<
name|DocState
argument_list|>
name|docState
init|=
operator|new
name|ThreadLocal
argument_list|<
name|DocState
argument_list|>
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
DECL|field|config
specifier|protected
name|Config
name|config
decl_stmt|;
DECL|field|storeVal
specifier|protected
name|Store
name|storeVal
init|=
name|Store
operator|.
name|NO
decl_stmt|;
DECL|field|bodyStoreVal
specifier|protected
name|Store
name|bodyStoreVal
init|=
name|Store
operator|.
name|NO
decl_stmt|;
DECL|field|indexVal
specifier|protected
name|Index
name|indexVal
init|=
name|Index
operator|.
name|ANALYZED_NO_NORMS
decl_stmt|;
DECL|field|bodyIndexVal
specifier|protected
name|Index
name|bodyIndexVal
init|=
name|Index
operator|.
name|ANALYZED
decl_stmt|;
DECL|field|termVecVal
specifier|protected
name|TermVector
name|termVecVal
init|=
name|TermVector
operator|.
name|NO
decl_stmt|;
DECL|field|source
specifier|protected
name|ContentSource
name|source
decl_stmt|;
DECL|field|reuseFields
specifier|protected
name|boolean
name|reuseFields
decl_stmt|;
DECL|field|indexProperties
specifier|protected
name|boolean
name|indexProperties
decl_stmt|;
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
DECL|field|fieldVauleMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Values
argument_list|>
name|fieldVauleMap
decl_stmt|;
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
name|Values
name|valueType
decl_stmt|;
specifier|final
name|DocState
name|ds
init|=
name|getDocState
argument_list|()
decl_stmt|;
specifier|final
name|Document
name|doc
init|=
name|reuseFields
condition|?
name|ds
operator|.
name|doc
else|:
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Set ID_FIELD
name|Field
name|idField
init|=
name|ds
operator|.
name|getField
argument_list|(
name|ID_FIELD
argument_list|,
name|storeVal
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED_NO_NORMS
argument_list|,
name|termVecVal
argument_list|)
decl_stmt|;
name|idField
operator|.
name|setValue
argument_list|(
literal|"doc"
operator|+
operator|(
name|r
operator|!=
literal|null
condition|?
name|r
operator|.
name|nextInt
argument_list|(
name|updateDocIDLimit
argument_list|)
else|:
name|incrNumDocsCreated
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
name|trySetIndexValues
argument_list|(
name|idField
argument_list|)
expr_stmt|;
comment|// Set NAME_FIELD
name|String
name|name
init|=
name|docData
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
name|name
operator|=
literal|""
expr_stmt|;
name|name
operator|=
name|cnt
operator|<
literal|0
condition|?
name|name
else|:
name|name
operator|+
literal|"_"
operator|+
name|cnt
expr_stmt|;
name|Field
name|nameField
init|=
name|ds
operator|.
name|getField
argument_list|(
name|NAME_FIELD
argument_list|,
name|storeVal
argument_list|,
name|indexVal
argument_list|,
name|termVecVal
argument_list|)
decl_stmt|;
name|nameField
operator|.
name|setValue
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|trySetIndexValues
argument_list|(
name|nameField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|nameField
argument_list|)
expr_stmt|;
comment|// Set DATE_FIELD
name|String
name|date
init|=
name|docData
operator|.
name|getDate
argument_list|()
decl_stmt|;
if|if
condition|(
name|date
operator|==
literal|null
condition|)
block|{
name|date
operator|=
literal|""
expr_stmt|;
block|}
name|Field
name|dateField
init|=
name|ds
operator|.
name|getField
argument_list|(
name|DATE_FIELD
argument_list|,
name|storeVal
argument_list|,
name|indexVal
argument_list|,
name|termVecVal
argument_list|)
decl_stmt|;
name|dateField
operator|.
name|setValue
argument_list|(
name|date
argument_list|)
expr_stmt|;
name|trySetIndexValues
argument_list|(
name|dateField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|dateField
argument_list|)
expr_stmt|;
comment|// Set TITLE_FIELD
name|String
name|title
init|=
name|docData
operator|.
name|getTitle
argument_list|()
decl_stmt|;
name|Field
name|titleField
init|=
name|ds
operator|.
name|getField
argument_list|(
name|TITLE_FIELD
argument_list|,
name|storeVal
argument_list|,
name|indexVal
argument_list|,
name|termVecVal
argument_list|)
decl_stmt|;
name|titleField
operator|.
name|setValue
argument_list|(
name|title
operator|==
literal|null
condition|?
literal|""
else|:
name|title
argument_list|)
expr_stmt|;
name|trySetIndexValues
argument_list|(
name|titleField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|titleField
argument_list|)
expr_stmt|;
name|String
name|body
init|=
name|docData
operator|.
name|getBody
argument_list|()
decl_stmt|;
if|if
condition|(
name|body
operator|!=
literal|null
operator|&&
name|body
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
name|body
operator|.
name|length
argument_list|()
condition|)
block|{
name|bdy
operator|=
name|body
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
name|body
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
name|body
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
name|body
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
name|body
operator|.
name|substring
argument_list|(
name|size
argument_list|)
argument_list|)
expr_stmt|;
comment|// some left
block|}
name|Field
name|bodyField
init|=
name|ds
operator|.
name|getField
argument_list|(
name|BODY_FIELD
argument_list|,
name|bodyStoreVal
argument_list|,
name|bodyIndexVal
argument_list|,
name|termVecVal
argument_list|)
decl_stmt|;
name|bodyField
operator|.
name|setValue
argument_list|(
name|bdy
argument_list|)
expr_stmt|;
name|trySetIndexValues
argument_list|(
name|bodyField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|bodyField
argument_list|)
expr_stmt|;
if|if
condition|(
name|storeBytes
condition|)
block|{
name|Field
name|bytesField
init|=
name|ds
operator|.
name|getField
argument_list|(
name|BYTES_FIELD
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED_NO_NORMS
argument_list|,
name|TermVector
operator|.
name|NO
argument_list|)
decl_stmt|;
name|bytesField
operator|.
name|setValue
argument_list|(
name|bdy
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|trySetIndexValues
argument_list|(
name|bytesField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|bytesField
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|indexProperties
condition|)
block|{
name|Properties
name|props
init|=
name|docData
operator|.
name|getProps
argument_list|()
decl_stmt|;
if|if
condition|(
name|props
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|props
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Field
name|f
init|=
name|ds
operator|.
name|getField
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|storeVal
argument_list|,
name|indexVal
argument_list|,
name|termVecVal
argument_list|)
decl_stmt|;
name|f
operator|.
name|setValue
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|trySetIndexValues
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
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
block|}
comment|//System.out.println("============== Created doc "+numDocsCreated+" :\n"+doc+"\n==========");
return|return
name|doc
return|;
block|}
DECL|method|trySetIndexValues
specifier|private
name|void
name|trySetIndexValues
parameter_list|(
name|Field
name|field
parameter_list|)
block|{
specifier|final
name|Values
name|valueType
decl_stmt|;
if|if
condition|(
operator|(
name|valueType
operator|=
name|fieldVauleMap
operator|.
name|get
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
operator|)
operator|!=
literal|null
condition|)
name|ValuesField
operator|.
name|set
argument_list|(
name|field
argument_list|,
name|valueType
argument_list|)
expr_stmt|;
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
DECL|method|getDocState
specifier|protected
name|DocState
name|getDocState
parameter_list|()
block|{
name|DocState
name|ds
init|=
name|docState
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|ds
operator|==
literal|null
condition|)
block|{
name|ds
operator|=
operator|new
name|DocState
argument_list|(
name|reuseFields
argument_list|,
name|storeVal
argument_list|,
name|bodyStoreVal
argument_list|,
name|indexVal
argument_list|,
name|bodyIndexVal
argument_list|,
name|termVecVal
argument_list|)
expr_stmt|;
name|docState
operator|.
name|set
argument_list|(
name|ds
argument_list|)
expr_stmt|;
block|}
return|return
name|ds
return|;
block|}
DECL|method|incrNumDocsCreated
specifier|protected
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
comment|/**    * Closes the {@link DocMaker}. The base implementation closes the    * {@link ContentSource}, and it can be overridden to do more work (but make    * sure to call super.close()).    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|source
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns the number of bytes generated by the content source since last    * reset.    */
DECL|method|getBytesCount
specifier|public
specifier|synchronized
name|long
name|getBytesCount
parameter_list|()
block|{
return|return
name|source
operator|.
name|getBytesCount
argument_list|()
return|;
block|}
comment|/**    * Returns the total number of bytes that were generated by the content source    * defined to that doc maker.    */
DECL|method|getTotalBytesCount
specifier|public
name|long
name|getTotalBytesCount
parameter_list|()
block|{
return|return
name|source
operator|.
name|getTotalBytesCount
argument_list|()
return|;
block|}
comment|/**    * Creates a {@link Document} object ready for indexing. This method uses the    * {@link ContentSource} to get the next document from the source, and creates    * a {@link Document} object from the returned fields. If    *<code>reuseFields</code> was set to true, it will reuse {@link Document}    * and {@link Field} instances.    */
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
name|source
operator|.
name|getNextDocData
argument_list|(
name|getDocState
argument_list|()
operator|.
name|docData
argument_list|)
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
comment|/**    * Same as {@link #makeDocument()}, only this method creates a document of the    * given size input by<code>size</code>.    */
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
name|docData
init|=
name|getDocState
argument_list|()
operator|.
name|docData
decl_stmt|;
name|DocData
name|dd
init|=
operator|(
name|lvr
operator|==
literal|null
condition|?
name|source
operator|.
name|getNextDocData
argument_list|(
name|docData
argument_list|)
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
name|source
operator|.
name|getNextDocData
argument_list|(
operator|new
name|DocData
argument_list|()
argument_list|)
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
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
name|source
operator|.
name|getTotalDocsCount
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
name|getTotalBytesCount
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
name|source
operator|.
name|getDocsCount
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
name|source
operator|.
name|getDocsCount
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
name|getBytesCount
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
comment|/** Reset inputs so that the test run would behave, input wise, as if it just started. */
DECL|method|resetInputs
specifier|public
specifier|synchronized
name|void
name|resetInputs
parameter_list|()
throws|throws
name|IOException
block|{
name|printDocStatistics
argument_list|()
expr_stmt|;
comment|// re-initiate since properties by round may have changed.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|source
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
name|numDocsCreated
operator|=
literal|0
expr_stmt|;
name|resetLeftovers
argument_list|()
expr_stmt|;
block|}
DECL|method|parseValueFields
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Values
argument_list|>
name|parseValueFields
parameter_list|(
name|String
name|fields
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
name|String
index|[]
name|split
init|=
name|fields
operator|.
name|split
argument_list|(
literal|";"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Values
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Values
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|tuple
range|:
name|split
control|)
block|{
specifier|final
name|String
index|[]
name|nameValue
init|=
name|tuple
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
if|if
condition|(
name|nameValue
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"illegal doc.stored.values format: "
operator|+
name|fields
operator|+
literal|" expected fieldname=ValuesType;...;...;"
argument_list|)
throw|;
block|}
name|result
operator|.
name|put
argument_list|(
name|nameValue
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
argument_list|,
name|Values
operator|.
name|valueOf
argument_list|(
name|nameValue
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/** Set the configuration parameters of this doc maker. */
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
try|try
block|{
name|String
name|sourceClass
init|=
name|config
operator|.
name|get
argument_list|(
literal|"content.source"
argument_list|,
literal|"org.apache.lucene.benchmark.byTask.feeds.SingleDocSource"
argument_list|)
decl_stmt|;
name|source
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|sourceClass
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|ContentSource
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|source
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Should not get here. Throw runtime exception.
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
name|bodyStored
init|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.body.stored"
argument_list|,
name|stored
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
name|bodyTokenized
init|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.body.tokenized"
argument_list|,
name|tokenized
argument_list|)
decl_stmt|;
name|boolean
name|norms
init|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.tokenized.norms"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|bodyNorms
init|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.body.tokenized.norms"
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
name|fieldVauleMap
operator|=
name|parseValueFields
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"doc.stored.values"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
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
name|bodyStoreVal
operator|=
operator|(
name|bodyStored
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
if|if
condition|(
name|tokenized
condition|)
block|{
name|indexVal
operator|=
name|norms
condition|?
name|Index
operator|.
name|ANALYZED
else|:
name|Index
operator|.
name|ANALYZED_NO_NORMS
expr_stmt|;
block|}
else|else
block|{
name|indexVal
operator|=
name|norms
condition|?
name|Index
operator|.
name|NOT_ANALYZED
else|:
name|Index
operator|.
name|NOT_ANALYZED_NO_NORMS
expr_stmt|;
block|}
if|if
condition|(
name|bodyTokenized
condition|)
block|{
name|bodyIndexVal
operator|=
name|bodyNorms
condition|?
name|Index
operator|.
name|ANALYZED
else|:
name|Index
operator|.
name|ANALYZED_NO_NORMS
expr_stmt|;
block|}
else|else
block|{
name|bodyIndexVal
operator|=
name|bodyNorms
condition|?
name|Index
operator|.
name|NOT_ANALYZED
else|:
name|Index
operator|.
name|NOT_ANALYZED_NO_NORMS
expr_stmt|;
block|}
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
block|{
name|termVecVal
operator|=
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|termVecPositions
condition|)
block|{
name|termVecVal
operator|=
name|TermVector
operator|.
name|WITH_POSITIONS
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|termVecOffsets
condition|)
block|{
name|termVecVal
operator|=
name|TermVector
operator|.
name|WITH_OFFSETS
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|termVec
condition|)
block|{
name|termVecVal
operator|=
name|TermVector
operator|.
name|YES
expr_stmt|;
block|}
else|else
block|{
name|termVecVal
operator|=
name|TermVector
operator|.
name|NO
expr_stmt|;
block|}
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
name|reuseFields
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.reuse.fields"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// In a multi-rounds run, it is important to reset DocState since settings
comment|// of fields may change between rounds, and this is the only way to reset
comment|// the cache of all threads.
name|docState
operator|=
operator|new
name|ThreadLocal
argument_list|<
name|DocState
argument_list|>
argument_list|()
expr_stmt|;
name|indexProperties
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.index.props"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|updateDocIDLimit
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"doc.random.id.limit"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|updateDocIDLimit
operator|!=
operator|-
literal|1
condition|)
block|{
name|r
operator|=
operator|new
name|Random
argument_list|(
literal|179
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
